package org.smartbit4all.api.org;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.gateway.SecurityGateways;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.GroupsOfUser;
import org.smartbit4all.api.org.bean.GroupsOfUserCollection;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.org.bean.UsersOfGroup;
import org.smartbit4all.api.org.bean.UsersOfGroupCollection;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.setting.LocaleString;
import org.smartbit4all.api.storage.bean.ObjectMap;
import org.smartbit4all.api.storage.bean.ObjectMapRequest;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectReferenceEntry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class OrgApiStorageImpl implements OrgApi, InitializingBean {

  private static final Logger log = LoggerFactory.getLogger(LocaleSettingApi.class);

  public static final String ORG_SCHEME = "org";

  private final String USER_OBJECTMAP_REFERENCE = "userList";
  private final String GROUP_OBJECTMAP_REFERENCE = "groupList";

  private final String USERS_OF_GROUP_LIST_REFERENCE = "usersOfGroupList";
  private final String GROUPS_OF_USER_LIST_REFERENCE = "groupsOfUserList";

  @Autowired(required = false)
  private List<SecurityOption> securityOptions;

  @Autowired
  private StorageApi storageApi;

  private Storage orgStorage;

  /**
   * This function analyze the given class to discover the {@link LocaleString} fields. We add this
   * API for them to enable locale specific behavior for them.
   * 
   * @param clazz
   */
  private final Map<SecurityGroup, Group> analyzeSecurityOptions(SecurityOption option) {
    // Let's check the static LocaleString
    Field[] fields = option.getClass().getFields();
    Map<SecurityGroup, Group> result = new HashMap<>();
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      if (field.getType().isAssignableFrom(SecurityGroup.class)) {
        try {
          SecurityGroup securityGroup = (SecurityGroup) field.get(option);
          if (securityGroup != null) {
            securityGroup.setOrgApi(this);
            String key = ReflectionUtility.getQualifiedName(field);
            securityGroup.setName(key);

            Group newGroup = checkGroupExist(securityGroup);
            if (newGroup != null) {
              result.put(securityGroup, newGroup);
            }

          }
        } catch (IllegalArgumentException | IllegalAccessException e) {
          log.debug("Unable to access the value of the " + field, e);
        }
      }
    }
    return result;
  }

  /**
   * If there is no Group for the SecurtyGroup, then create one.
   * 
   */
  private Group checkGroupExist(SecurityGroup securityGroup) {
    Group groupByName = getGroupByName(securityGroup.getName());
    if (groupByName == null) {
      Group group =
          new Group().name(securityGroup.getName()).description(securityGroup.getDescription());
      saveGroup(group);
      return group;
    }
    return null;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (securityOptions != null) {
      Map<SecurityGroup, Group> allNewGroups = new HashMap<>();
      for (SecurityOption securityOption : securityOptions) {
        allNewGroups.putAll(analyzeSecurityOptions(securityOption));
      }
      for (Entry<SecurityGroup, Group> entry : allNewGroups.entrySet()) {
        for (SecurityGroup subGroup : entry.getKey().getSubGroups()) {
          Group subGroupByName = getGroupByName(subGroup.getName());
          if (subGroupByName != null) {
            addChildGroup(entry.getValue(), subGroupByName);
          }
        }
      }
    }
  }

  public Storage getStorage() {
    if (orgStorage == null) {
      orgStorage = storageApi.get(ORG_SCHEME);
    }
    return orgStorage;
  }


  private ObjectMap loadObjectMap(String mapName) {
    return getStorage().getAttachedMap(getStorage().settings().getUri(), mapName);
  }

  private <T> StorageObject<T> loadSettingsReference(String referenceName,
      Class<T> clazz) {

    URI uri = getOrCreateObjectReferenceURI(referenceName, clazz);
    return getStorage().load(uri, clazz);
  }

  private <T> T readSettingsReference(String referenceName, Class<T> clazz) {

    URI uri = getOrCreateObjectReferenceURI(referenceName, clazz);
    return getStorage().read(uri, clazz);
  }

  private <T> URI getOrCreateObjectReferenceURI(String referenceName, Class<T> clazz) {
    StorageObject<StorageSettings> settings = getStorage().settings();
    StorageObjectReferenceEntry reference = settings.getReference(referenceName);

    if (reference == null) {
      StorageObject<T> referenceObject = getStorage().instanceOf(clazz);
      try {
        referenceObject.setObject(clazz.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        throw new IllegalArgumentException("Failed to instantiate class: " + clazz.getName());
      }
      URI referenceObjectUri = getStorage().save(referenceObject);
      settings.setReference(referenceName, new ObjectReference().uri(referenceObjectUri));
      getStorage().save(settings);
      return referenceObjectUri;
    }
    return reference.getReferenceData().getUri();
  }

  @Override
  public List<Group> getAllGroups() {
    List<Group> groups = new ArrayList<>();

    ObjectMap groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE);

    Collection<URI> values = groupObjectMap.getUris().values();
    groups = getStorage().read(new ArrayList<>(values), Group.class);

    return groups;
  }

  @Override
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();

    ObjectMap userObjectMap = loadObjectMap(USER_OBJECTMAP_REFERENCE);

    Collection<URI> values = userObjectMap.getUris().values();
    users = getStorage().read(new ArrayList<>(values), User.class);

    return users;
  }

  @Override
  public List<User> getUsersOfGroup(URI groupUri) {
    Set<User> users = new HashSet<>();
    List<URI> allSubgroups = getAllSubgroups(groupUri);
    allSubgroups.add(groupUri);

    for (URI uri : allSubgroups) {

      UsersOfGroupCollection collection =
          readSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
      List<UsersOfGroup> usersOfGroupCollection = collection.getUsersOfGroupCollection();

      for (UsersOfGroup usersOfGroup : usersOfGroupCollection) {

        if (usersOfGroup.getGroupUri().equals(uri)) {
          users.addAll(getStorage().read(usersOfGroup.getUsers(), User.class));
        }
      }
    }
    return new ArrayList<>(users);
  }

  @Override
  public List<Group> getGroupsOfUser(URI userUri) {
    Set<Group> groups = new HashSet<>();
    GroupsOfUserCollection collection =
        readSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    List<GroupsOfUser> groupsOfUserCollection = collection.getGroupsOfUserCollection();

    for (GroupsOfUser groupsOfUser : groupsOfUserCollection) {

      if (groupsOfUser.getUserUri().equals(userUri)) {
        List<Group> directGroups = getStorage().read(groupsOfUser.getGroups(), Group.class);
        for (Group group : directGroups) {
          groups.add(group);
          groups.addAll(getStorage().read(getAllSubgroups(group.getUri()), Group.class));
        }
      }

    }
    return new ArrayList<>(groups);
  }

  @Override
  public User getUser(URI userUri) {
    return getStorage().read(userUri, User.class);
  }

  @Override
  public Group getGroup(URI groupUri) {
    return getStorage().read(groupUri, Group.class);
  }

  @Override
  public List<Group> getSubGroups(URI groupUri) {
    return getStorage().read(getAllSubgroups(groupUri), Group.class);
  }

  /**
   * Összegyűjti egy csoport összes alcsoportját.
   * 
   * @param groupUri
   * @return
   */
  private List<URI> getAllSubgroups(URI groupUri) {
    List<URI> subgroups = new ArrayList<>();
    Group group = getStorage().read(groupUri, Group.class);
    List<URI> children = group.getChildren();
    subgroups.addAll(children);
    for (URI uri : children) {
      subgroups.addAll(getAllSubgroups(uri));
    }
    return subgroups;
  }


  @Override
  public URI saveGroup(Group group) {
    Group groupByName = getGroupByName(group.getName());
    if (groupByName != null) {
      throw new IllegalStateException("Group with name " + group.getName() + " already exists!");
    }

    StorageObject<Group> groupStorageObj = getStorage().instanceOf(Group.class);
    groupStorageObj.setObject(group);
    URI uri = getStorage().save(groupStorageObj);

    addToObjectMap(GROUP_OBJECTMAP_REFERENCE, group.getName(), uri);

    return uri;
  }

  @Override
  public void addUserToGroup(URI userUri, URI groupUri) {

    UsersOfGroup usersOfGroup = getUsersOfGroupObject(groupUri);
    usersOfGroup.setUri(generateUri());
    usersOfGroup.setGroupUri(groupUri);

    List<URI> users = usersOfGroup.getUsers();
    if (!users.contains(userUri)) {
      users.add(userUri);
    }

    StorageObject<UsersOfGroupCollection> usersOfGroupCollectionStorage =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection usersOfGroupCollection = usersOfGroupCollectionStorage.getObject();

    usersOfGroupCollection.getUsersOfGroupCollection().add(usersOfGroup);
    getStorage().save(usersOfGroupCollectionStorage);

    GroupsOfUser groupsOfUser = getGroupsOfUserObject(userUri);
    groupsOfUser.setUri(generateUri());
    groupsOfUser.setUserUri(userUri);

    List<URI> groups = groupsOfUser.getGroups();
    if (!groups.contains(groupUri)) {
      groups.add(groupUri);
    }

    StorageObject<GroupsOfUserCollection> groupsOfUserCollectionStorage =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    GroupsOfUserCollection groupsOfUserCollection = groupsOfUserCollectionStorage.getObject();
    groupsOfUserCollection.getGroupsOfUserCollection().add(groupsOfUser);
    getStorage().save(groupsOfUserCollectionStorage);
  }

  private GroupsOfUser getGroupsOfUserObject(URI userUri) {
    StorageObject<GroupsOfUserCollection> groupsOfUserCollectionSO =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    GroupsOfUserCollection groupsOfUserCollection = groupsOfUserCollectionSO.getObject();

    List<GroupsOfUser> groupsOfUserList = groupsOfUserCollection.getGroupsOfUserCollection();
    List<GroupsOfUser> filteredList =
        groupsOfUserList.stream().filter(u -> u.getUserUri().equals(userUri))
            .collect(Collectors.toList());
    if (filteredList.size() > 0) {
      return filteredList.get(0);
    }
    return new GroupsOfUser();
  }

  private UsersOfGroup getUsersOfGroupObject(URI groupUri) {
    StorageObject<UsersOfGroupCollection> usersOfGRoupCollectionSO =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection usersOfGroupCollection = usersOfGRoupCollectionSO.getObject();

    List<UsersOfGroup> usersOfGroupList = usersOfGroupCollection.getUsersOfGroupCollection();
    List<UsersOfGroup> filteredList = usersOfGroupList.stream()
        .filter(u -> u.getGroupUri().equals(groupUri)).collect(Collectors.toList());
    if (filteredList.size() > 0) {
      return filteredList.get(0);
    }

    return new UsersOfGroup();
  }

  @Override
  public void removeUser(URI userUri) {
    // remove from storage
    setObjectToDeleted(userUri, User.class);

    // remove from setting USER_LIST_REFERENCE
    removeUserFromUserListReference(userUri);

    // Collect Group containing user
    GroupsOfUser groupsOfUser = getGroupsOfUserObject(userUri);
    List<URI> groups = groupsOfUser.getGroups();

    // settings -> UsersOfGroupCollection -> UsersOfGroup -> Remove user from groups
    StorageObject<UsersOfGroupCollection> usersOfGroupCollectionReference =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    List<UsersOfGroup> usersOfGroupCollection =
        usersOfGroupCollectionReference.getObject().getUsersOfGroupCollection();
    for (URI groupUri : groups) {
      for (UsersOfGroup usersOfGroup : usersOfGroupCollection) {
        if (groupUri.equals(usersOfGroup.getGroupUri())) {
          usersOfGroup.getUsers().remove(userUri);
        }
      }
    }
    // ... save
    getStorage().save(usersOfGroupCollectionReference);

    // Remove GroupsOfUser object
    StorageObject<GroupsOfUserCollection> storageObject =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    GroupsOfUserCollection collection = storageObject.getObject();

    List<GroupsOfUser> groupsOfUserCollection = collection.getGroupsOfUserCollection();
    groupsOfUserCollection.removeIf(o -> o.getUserUri().equals(userUri));
    getStorage().save(storageObject);
  }

  /**
   * Loads the StorgaeObject with the given uri and class, and sets it to deleted.
   * 
   * @param <T>
   * @param uri
   * @param clazz
   */
  private <T> void setObjectToDeleted(URI uri, Class<T> clazz) {
    if (getStorage().exists(uri)) {
      StorageObject<T> storageObject = getStorage().load(uri, clazz);
      storageObject.setDeleted();
      getStorage().save(storageObject);
    }
  }

  /**
   * Removes the user with the given uri from the USER_LIST_REFERNCE ObjectMap, which contains the
   * username - userUri mapping of all existing users.
   * 
   * @param userUri uri of the user to remove
   */
  private void removeUserFromUserListReference(URI userUri) {
    removeItemFromObjectMapByValue(USER_OBJECTMAP_REFERENCE, userUri);
  }

  private void removeItemFromObjectMapByValue(String mapName, URI value) {
    ObjectMap objectMap = loadObjectMap(mapName);

    String keyByValue = getKeyByValue(value, objectMap);
    removeFromObjectMap(mapName, keyByValue, value);
  }

  private String getKeyByValue(URI value, ObjectMap objectMap) {
    for (Entry<String, URI> entry : objectMap.getUris().entrySet()) {
      if (entry.getValue().equals(value)) {
        return entry.getKey();
      }
    }
    return null;
  }

  /**
   * Removes the group with the given uri from the GROUP_LIST_REFERNCE ObjectMap, which contains the
   * name - groupUri mapping of all existing groups.
   * 
   * @param userUri uri of the user to remove
   */
  private void removeGroupFromGroupListReference(URI groupUri) {
    removeItemFromObjectMapByValue(GROUP_OBJECTMAP_REFERENCE, groupUri);
  }


  @Override
  public void removeGroup(URI groupUri) {
    // remove from storage
    setObjectToDeleted(groupUri, Group.class);

    // remove from setting USER_LIST_REFERENCE
    removeGroupFromGroupListReference(groupUri);

    // Collect Group containing user
    UsersOfGroup usersOfGroup = getUsersOfGroupObject(groupUri);
    List<URI> users = usersOfGroup.getUsers();

    // settings -> GroupsOfUserCollection -> GRoupsOfUSer-> Remove group from users
    StorageObject<GroupsOfUserCollection> groupsOfUserCollectionReference =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    List<GroupsOfUser> groupsOfUserCollection =
        groupsOfUserCollectionReference.getObject().getGroupsOfUserCollection();
    for (URI userUri : users) {
      for (GroupsOfUser groupsOfUser : groupsOfUserCollection) {
        if (userUri.equals(groupsOfUser.getUserUri())) {
          usersOfGroup.getUsers().remove(groupUri);
        }
      }
    }
    // save
    getStorage().save(groupsOfUserCollectionReference);

    // Remove UsersOfGroup object
    StorageObject<UsersOfGroupCollection> storageObject =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection collection = storageObject.getObject();

    List<UsersOfGroup> usersOfGroupCollection = collection.getUsersOfGroupCollection();
    usersOfGroupCollection.removeIf(o -> o.getGroupUri().equals(groupUri));
    getStorage().save(storageObject);
  }

  @Override
  public void removeUserFromGroup(URI userUri, URI groupUri) {
    StorageObject<GroupsOfUserCollection> groupsOfUserReference =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    GroupsOfUserCollection object = groupsOfUserReference.getObject();
    List<GroupsOfUser> groupsOfUserCollection = object.getGroupsOfUserCollection();
    for (GroupsOfUser groupsOfUser : groupsOfUserCollection) {
      if (groupsOfUser.getUserUri().equals(userUri)) {
        groupsOfUser.getGroups().remove(groupUri);
      }
    }
    getStorage().save(groupsOfUserReference);

    StorageObject<UsersOfGroupCollection> usersOfGroupReference =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection usersOfGroupObject = usersOfGroupReference.getObject();

    List<UsersOfGroup> usersOfGroupCollection = usersOfGroupObject.getUsersOfGroupCollection();
    for (UsersOfGroup usersOfGroup : usersOfGroupCollection) {
      if (usersOfGroup.getGroupUri().equals(groupUri)) {
        usersOfGroup.getUsers().remove(userUri);
      }
    }
    getStorage().save(usersOfGroupReference);

  }

  protected URI generateUri() {
    return SecurityGateways.generateURI(UUID.randomUUID().toString());
  }

  @Override
  public void addChildGroup(Group parentGroup, Group childGroup) {
    childGroup.setParent(parentGroup.getUri());
    parentGroup.getChildren().add(childGroup.getUri());
    updateGroup(childGroup);
    updateGroup(parentGroup);
  }

  @Override
  public User getUserByUsername(String username) {
    ObjectMap userObjectMap = loadObjectMap(USER_OBJECTMAP_REFERENCE);
    URI userUri = userObjectMap.getUris().get(username);
    if (userUri == null) {
      return null;
    }
    return getStorage().exists(userUri) ? getStorage().read(userUri, User.class) : null;
  }

  @Override
  public Group getGroupByName(String name) {
    ObjectMap groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE);
    URI groupUri = groupObjectMap.getUris().get(name);
    if (groupUri == null) {
      return null;
    }
    return getStorage().exists(groupUri) ? getStorage().read(groupUri, Group.class)
        : null;
  }

  @Override
  public URI updateUser(User user) {
    String username = user.getUsername();
    if (username == null) {
      throw new IllegalArgumentException("User: " + user.toString() + " has no username!");
    }
    User userByUsername = getUserByUsername(username);
    if (userByUsername != null) {
      if (getStorage().exists(userByUsername.getUri())) {
        StorageObject<User> oldUser = getStorage().load(userByUsername.getUri(), User.class);
        oldUser.setObject(user);
        return getStorage().save(oldUser);
      } else {
        throw new IllegalArgumentException("Failed to update user: " + user.toString());
      }
    } else {
      throw new IllegalArgumentException("Failed to update user: " + user.toString());
    }
  }


  @Override
  public URI saveUser(User user) {
    if (user.getUsername() == null) {
      throw new IllegalArgumentException("Username is required!");
    }
    User userByUsername = getUserByUsername(user.getUsername());
    if (userByUsername != null) {
      throw new IllegalStateException(
          "User with username " + user.getUsername() + "already exists!");
    }

    StorageObject<User> userStorageObj = getStorage().instanceOf(User.class);
    userStorageObj.setObject(user);
    URI uri = getStorage().save(userStorageObj);

    addToObjectMap(USER_OBJECTMAP_REFERENCE, user.getUsername(), uri);
    return uri;
  }

  @Override
  public URI updateGroup(Group group) {
    String name = group.getName();
    if (name == null) {
      throw new IllegalArgumentException("Group: " + group.toString() + "has no name!");
    }
    Group groupByName = getGroupByName(name);
    if (getStorage().exists(groupByName.getUri())) {
      StorageObject<Group> oldGroup = getStorage().load(groupByName.getUri(), Group.class);
      oldGroup.setObject(group);
      return getStorage().save(oldGroup);
    }
    throw new IllegalArgumentException("Failed to update user: " + group.toString());
  }

  @Override
  public void removeSubGroup(URI parentGroupUri, URI childGroupUri) {
    Group parentGroup = getGroup(parentGroupUri);

    boolean isDirectSubGroup = parentGroup.getChildren().contains(childGroupUri);
    if (isDirectSubGroup) {
      removeDirectSubGroup(parentGroupUri, childGroupUri);
    } else {
      for (URI uri : parentGroup.getChildren()) {
        removeSubGroup(uri, childGroupUri);
      }
    }
  }

  private void removeDirectSubGroup(URI parentGroupUri, URI childGroupUri) {
    Group parentGroup = getGroup(parentGroupUri);
    parentGroup.getChildren().removeIf(c -> c.equals(childGroupUri));
    updateGroup(parentGroup);
  }

  @Override
  public List<Group> getRootGroups() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getUserImage(URI userUri) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public InputStream getGroupImage(URI groupUri) {
    // TODO Auto-generated method stub
    return null;
  }

  private void addToObjectMap(String mapName, String key, URI value) {
    getStorage().updateAttachedMap(
        getStorage().settings().getUri(),
        new ObjectMapRequest().mapName(mapName).putUrisToAddItem(key, value));
  }

  private void removeFromObjectMap(String mapName, String key, URI value) {
    getStorage().updateAttachedMap(
        getStorage().settings().getUri(),
        new ObjectMapRequest().mapName(mapName).putUrisToRemoveItem(key, value));
  }

}
