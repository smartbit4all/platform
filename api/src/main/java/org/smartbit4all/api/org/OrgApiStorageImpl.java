package org.smartbit4all.api.org;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
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
import org.smartbit4all.api.session.UserSessionApi;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class OrgApiStorageImpl implements OrgApi {

  private static final Logger log = LoggerFactory.getLogger(LocaleSettingApi.class);

  public static final String ORG_SCHEME = "org";

  private final String USER_OBJECTMAP_REFERENCE = "userList";
  private final String INACTIVE_USER_OBJECTMAP_REFERENCE = "inactiveUserList";
  private final String GROUP_OBJECTMAP_REFERENCE = "groupList";

  private final String USERS_OF_GROUP_LIST_REFERENCE = "usersOfGroupList";
  private final String GROUPS_OF_USER_LIST_REFERENCE = "groupsOfUserList";

  @Autowired(required = false)
  private List<SecurityOption> securityOptions;

  @Autowired
  private StorageApi storageApi;

  private Supplier<Storage> storage = new Supplier<Storage>() {

    private Storage storageInstance;

    @Override
    public Storage get() {
      if (storageInstance == null) {
        storageInstance = storageApi.get(ORG_SCHEME);
      }
      return storageInstance;
    }
  };

  @Autowired
  private OrgApi self;

  @Autowired
  private UserSessionApi userSessionApi;

  public OrgApiStorageImpl() {}

  public OrgApiStorageImpl(StorageApi storageApi, List<SecurityOption> securityOptions)
      throws Exception {
    this.storageApi = storageApi;
    this.securityOptions = securityOptions;
    initSecurityOptions();
  }

  /**
   * The cache for the groups of user.
   */
  private Cache<URI, List<Group>> groupsOfUserCache =
      CacheBuilder.newBuilder().concurrencyLevel(10).build();

  /**
   * The cache for the groups of user.
   */
  private Cache<URI, List<User>> usersOfGroupCache =
      CacheBuilder.newBuilder().concurrencyLevel(10).build();

  /**
   * The cache for the groups of user.
   */
  private Cache<String, Group> groupByNameCache =
      CacheBuilder.newBuilder().concurrencyLevel(10).build();

  public static final Group GROUP_NOT_FOUND = new Group().name("GROUP_NOT_FOUND");

  /**
   * Invalidate the cache after modification.
   */
  private final void invalidateCache() {
    groupsOfUserCache.invalidateAll();
    usersOfGroupCache.invalidateAll();
    groupByNameCache.invalidateAll();
  }

  /**
   * This function analyze the given class to discover the {@link LocaleString} fields. We add this
   * API for them to enable locale specific behavior for them.
   * 
   * @param option
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
            securityGroup.setSecurityPredicate(
                (sg, uri) -> OrgUtils.securityPredicate(self, userSessionApi, sg, uri));
            // securityGroup.setOrgApi(this);
            // securityGroup.setUserSessionApi(userSessionApi);
            String key = ReflectionUtility.getQualifiedName(field);
            securityGroup.setName(key);
            String name = securityGroup.getTitle();
            if (name == null) {
              securityGroup.setTitle(field.getName());
            }
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
          new Group()
              .name(securityGroup.getName())
              .title(securityGroup.getTitle())
              .description(securityGroup.getDescription())
              .builtIn(securityGroup.isbuiltIn());
      saveGroup(group);
      return group;
    }
    return null;
  }

  @EventListener(ApplicationStartedEvent.class)
  public void initSecurityOptions() throws Exception {
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


  private ObjectMap loadObjectMap(String mapName) {
    return storage.get().getAttachedMap(storage.get().settings().getUri(), mapName);
  }

  private <T> StorageObject<T> loadSettingsReference(String referenceName,
      Class<T> clazz) {

    URI uri = getOrCreateObjectReferenceURI(referenceName, clazz);
    return storage.get().load(uri, clazz);
  }

  private <T> T readSettingsReference(String referenceName, Class<T> clazz) {

    URI uri = getOrCreateObjectReferenceURI(referenceName, clazz);
    return storage.get().read(uri, clazz);
  }

  private <T> URI getOrCreateObjectReferenceURI(String referenceName, Class<T> clazz) {
    StorageObject<StorageSettings> settings = storage.get().settings();
    StorageObjectReferenceEntry reference = settings.getReference(referenceName);

    if (reference == null) {
      StorageObject<T> referenceObject = storage.get().instanceOf(clazz);
      try {
        referenceObject.setObject(clazz.newInstance());
      } catch (InstantiationException | IllegalAccessException e) {
        throw new IllegalArgumentException("Failed to instantiate class: " + clazz.getName());
      }
      URI referenceObjectUri = storage.get().save(referenceObject);
      settings.setReference(referenceName, new ObjectReference().uri(referenceObjectUri));
      storage.get().save(settings);
      return referenceObjectUri;
    }
    return reference.getReferenceData().getUri();
  }

  @Override
  public List<Group> getAllGroups() {
    List<Group> groups = new ArrayList<>();

    ObjectMap groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE);

    Collection<URI> values = groupObjectMap.getUris().values();
    groups = storage.get().read(new ArrayList<>(values), Group.class);

    return groups;
  }

  @Override
  public List<User> getActiveUsers() {
    List<User> users = new ArrayList<>();

    ObjectMap activeUserObjectMap = loadObjectMap(USER_OBJECTMAP_REFERENCE);
    Collection<URI> activeUserUris = activeUserObjectMap.getUris().values();

    ArrayList<URI> userUris = new ArrayList<>(activeUserUris);

    users = storage.get().read(userUris, User.class);

    return users;
  }

  @Override
  public List<User> getInactiveUsers() {
    List<User> users = new ArrayList<>();

    ObjectMap userObjectMap = loadObjectMap(INACTIVE_USER_OBJECTMAP_REFERENCE);

    Collection<URI> values = userObjectMap.getUris().values();
    users = storage.get().read(new ArrayList<>(values), User.class);

    return users;
  }

  @Override
  public List<User> getAllUsers() {
    List<User> activeUsers = getActiveUsers();
    List<User> inactiveUsers = getInactiveUsers();

    activeUsers.addAll(inactiveUsers);
    return activeUsers;
  }

  @Override
  public List<User> getUsersOfGroup(URI groupUri) {
    try {
      return usersOfGroupCache.get(groupUri, new Callable<List<User>>() {

        @Override
        public List<User> call() throws Exception {
          Set<User> users = new HashSet<>();
          List<URI> allSubgroups = getAllSubgroups(groupUri);
          allSubgroups.add(groupUri);

          UsersOfGroupCollection collection =
              readSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
          List<UsersOfGroup> usersOfGroupCollection = collection.getUsersOfGroupCollection();

          for (UsersOfGroup usersOfGroup : usersOfGroupCollection) {

            if (usersOfGroup.getGroupUri().equals(groupUri)) {
              users.addAll(storage.get().read(usersOfGroup.getUsers(), User.class));
            }
          }
          return new ArrayList<>(users);
        }

      });
    } catch (ExecutionException e) {
      log.error("Unable to retrieve the users of group.", e);
      return Collections.emptyList();
    }
  }

  @Override
  public List<Group> getGroupsOfUser(URI userUri) {
    try {
      return groupsOfUserCache.get(userUri, new Callable<List<Group>>() {

        @Override
        public List<Group> call() throws Exception {
          Set<Group> groups = new HashSet<>();
          GroupsOfUserCollection collection =
              readSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
          List<GroupsOfUser> groupsOfUserCollection = collection.getGroupsOfUserCollection();

          for (GroupsOfUser groupsOfUser : groupsOfUserCollection) {

            if (groupsOfUser.getUserUri().equals(userUri)) {
              List<Group> directGroups = storage.get().read(groupsOfUser.getGroups(), Group.class);
              for (Group group : directGroups) {
                groups.add(group);
                groups.addAll(storage.get().read(getAllSubgroups(group.getUri()), Group.class));
              }
            }

          }
          return new ArrayList<>(groups);
        }
      });
    } catch (ExecutionException e) {
      log.error("Unable to retrieve the groups of user.", e);
      return Collections.emptyList();
    }
  }

  @Override
  public User getUser(URI userUri) {
    return storage.get().read(userUri, User.class);
  }

  @Override
  public Group getGroup(URI groupUri) {
    return storage.get().read(groupUri, Group.class);
  }

  @Override
  public List<Group> getSubGroups(URI groupUri) {
    return storage.get().read(getAllSubgroups(groupUri), Group.class);
  }

  @Override
  public List<Group> getConnectingSubGroups(URI groupUri) {
    Group group = storage.get().read(groupUri, Group.class);
    List<URI> children = group.getChildren();
    return storage.get().read(children, Group.class);
  }

  /**
   * Összegyűjti egy csoport összes alcsoportját.
   * 
   * @param groupUri
   * @return
   */
  private List<URI> getAllSubgroups(URI groupUri) {
    Set<URI> subgroups = new HashSet<>();
    Group group = storage.get().read(groupUri, Group.class);
    List<URI> children = group.getChildren();
    subgroups.addAll(children);
    for (URI uri : children) {
      subgroups.addAll(getAllSubgroups(uri));
    }
    return new ArrayList<>(subgroups);
  }


  @Override
  public URI saveGroup(Group group) {
    Group groupByName = getGroupByName(group.getName());
    if (groupByName != null) {
      throw new IllegalStateException("Group with name " + group.getName() + " already exists!");
    }

    StorageObject<Group> groupStorageObj = storage.get().instanceOf(Group.class);
    groupStorageObj.setObject(group);
    URI uri = storage.get().save(groupStorageObj);

    addToObjectMap(GROUP_OBJECTMAP_REFERENCE, group.getName(), uri);

    invalidateCache();

    return uri;
  }

  @Override
  public void addUserToGroup(URI userUri, URI groupUri) {

    boolean anyChange1 = addToUsersOfGroupCollection(userUri, groupUri);
    boolean anyChange2 = addToGroupsOfUserCollection(userUri, groupUri);
    if (anyChange1 || anyChange2) {
      invalidateCache();
    }
  }

  /**
   * Creates or update a UsersOfGroup entry in the globally save UsersOfGroupCollection object,
   * indicating that user is part of the group.
   * 
   * @param userUri
   * @param groupUri
   * @return any change happened
   */
  private boolean addToUsersOfGroupCollection(URI userUri, URI groupUri) {
    StorageObject<UsersOfGroupCollection> usersOfGRoupCollectionSO =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection usersOfGroupCollection = usersOfGRoupCollectionSO.getObject();

    List<UsersOfGroup> usersOfGroupList = usersOfGroupCollection.getUsersOfGroupCollection();
    UsersOfGroup usersOfGroup = usersOfGroupList.stream()
        .filter(u -> u.getGroupUri().equals(groupUri))
        .findFirst()
        .orElse(null);

    boolean anyChange = false;
    if (usersOfGroup == null) {
      // group wasn't in collection, create and save new entry
      usersOfGroup = new UsersOfGroup()
          .uri(generateUri())
          .groupUri(groupUri)
          .addUsersItem(userUri);
      usersOfGroupCollection.addUsersOfGroupCollectionItem(usersOfGroup);
      anyChange = true;
    } else if (!usersOfGroup.getUsers().contains(userUri)) {
      // user wasn't in group's list, update and save entry
      usersOfGroup.addUsersItem(userUri);
      anyChange = true;
    }
    if (anyChange) {
      storage.get().save(usersOfGRoupCollectionSO);
    }
    return anyChange;
  }

  /**
   * Creates or update a GroupsOfUser entry in the globally save GroupsOfUserCollection object,
   * indicating that user is part of the group.
   * 
   * @param userUri
   * @param groupUri
   * @return any change happened
   */
  private boolean addToGroupsOfUserCollection(URI userUri, URI groupUri) {
    StorageObject<GroupsOfUserCollection> groupsOfUserCollectionSO =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    GroupsOfUserCollection groupsOfUserCollection = groupsOfUserCollectionSO.getObject();
    List<GroupsOfUser> groupsOfUserList = groupsOfUserCollection.getGroupsOfUserCollection();
    GroupsOfUser groupsOfUser = groupsOfUserList.stream()
        .filter(u -> u.getUserUri().equals(userUri))
        .findFirst()
        .orElse(null);

    boolean anyChange = false;
    if (groupsOfUser == null) {
      // user wasn't in collection, create and save new entry
      groupsOfUser = new GroupsOfUser()
          .uri(generateUri())
          .userUri(userUri)
          .addGroupsItem(groupUri);
      groupsOfUserCollection.addGroupsOfUserCollectionItem(groupsOfUser);
      anyChange = true;
    } else if (!groupsOfUser.getGroups().contains(groupUri)) {
      // group wasn't in user's list, update and save entry
      groupsOfUser.addGroupsItem(groupUri);
      anyChange = true;
    }
    if (anyChange) {
      storage.get().save(groupsOfUserCollectionSO);
    }
    return anyChange;
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
    // set to inactive
    setUserToInactive(userUri);

    // remove from setting USER_OBJECTMAP_REFERENCE
    removeItemFromObjectMapByValue(USER_OBJECTMAP_REFERENCE, userUri);

    // add to INVALID_USER_OBJECTMAP-REFERENCE
    User user = getUser(userUri);
    addToObjectMap(INACTIVE_USER_OBJECTMAP_REFERENCE, user.getUsername(), userUri);

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
    storage.get().save(usersOfGroupCollectionReference);

    // Remove GroupsOfUser object
    StorageObject<GroupsOfUserCollection> storageObject =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    GroupsOfUserCollection collection = storageObject.getObject();

    List<GroupsOfUser> groupsOfUserCollection = collection.getGroupsOfUserCollection();
    groupsOfUserCollection.removeIf(o -> o.getUserUri().equals(userUri));
    storage.get().save(storageObject);

    invalidateCache();
  }

  private void setUserToInactive(URI userUri) {
    StorageObject<User> userSO = storage.get().load(userUri, User.class);
    userSO.getObject().setInactive(true);
    storage.get().save(userSO);

    invalidateCache();
  }

  /**
   * Loads the StorgaeObject with the given uri and class, and sets it to deleted.
   * 
   * @param <T>
   * @param uri
   * @param clazz
   */
  private <T> void setObjectToDeleted(URI uri, Class<T> clazz) {
    if (storage.get().exists(uri)) {
      StorageObject<T> storageObject = storage.get().load(uri, clazz);
      storageObject.setDeleted();
      storage.get().save(storageObject);
    }
  }

  /**
   * Removes the object with the given uri from the ObjectMap
   * 
   * @param mapName Identifier of ObjectMap to remove from.
   * @param value Value to remove from ObjectMap.
   */
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
   * @param groupUri URI of the group to remove.
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
    storage.get().save(groupsOfUserCollectionReference);

    // Remove UsersOfGroup object
    StorageObject<UsersOfGroupCollection> storageObject =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection collection = storageObject.getObject();

    List<UsersOfGroup> usersOfGroupCollection = collection.getUsersOfGroupCollection();
    usersOfGroupCollection.removeIf(o -> o.getGroupUri().equals(groupUri));
    storage.get().save(storageObject);

    invalidateCache();
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
    storage.get().save(groupsOfUserReference);

    StorageObject<UsersOfGroupCollection> usersOfGroupReference =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection usersOfGroupObject = usersOfGroupReference.getObject();

    List<UsersOfGroup> usersOfGroupCollection = usersOfGroupObject.getUsersOfGroupCollection();
    for (UsersOfGroup usersOfGroup : usersOfGroupCollection) {
      if (usersOfGroup.getGroupUri().equals(groupUri)) {
        usersOfGroup.getUsers().remove(userUri);
      }
    }
    storage.get().save(usersOfGroupReference);

    invalidateCache();

  }

  protected URI generateUri() {
    return SecurityGateways.generateURI(UUID.randomUUID().toString());
  }

  @Override
  public void addChildGroup(Group parentGroup, Group childGroup) {
    if (parentGroup.getChildren().contains(childGroup.getUri())) {
      return;
    }

    parentGroup.getChildren().add(childGroup.getUri());

    if (getGroupByName(parentGroup.getName()) == null) {
      saveGroup(parentGroup);
    } else {
      updateGroup(parentGroup);
    }
  }

  @Override
  public User getUserByUsername(String username) {
    ObjectMap activeObjectMap = loadObjectMap(USER_OBJECTMAP_REFERENCE);

    Map<String, URI> uris = activeObjectMap.getUris();
    URI userUri = uris.get(username);
    if (userUri == null) {
      return null;
    }
    return storage.get().exists(userUri) ? storage.get().read(userUri, User.class) : null;
  }

  @Override
  public Group getGroupByName(String name) {
    try {
      Group group = groupByNameCache.get(name, new Callable<Group>() {

        @Override
        public Group call() throws Exception {
          ObjectMap groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE);
          URI groupUri = groupObjectMap.getUris().get(name);
          if (groupUri == null) {
            return GROUP_NOT_FOUND;
          }
          return storage.get().exists(groupUri) ? storage.get().read(groupUri, Group.class)
              : GROUP_NOT_FOUND;
        }

      });
      return group == GROUP_NOT_FOUND ? null : group;
    } catch (ExecutionException e) {
      log.error("Unable to retrieve the groups by name.", e);
      return null;
    }

  }

  @Override
  public URI updateUser(User user) {
    String username = user.getUsername();
    if (username == null) {
      throw new IllegalArgumentException("User: " + user.toString() + " has no username!");
    }
    User userByUsername = getUserByUsername(username);
    if (userByUsername != null) {
      if (storage.get().exists(userByUsername.getUri())) {
        StorageObject<User> oldUser = storage.get().load(userByUsername.getUri(), User.class);
        oldUser.setObject(user);
        URI uri = storage.get().save(oldUser);
        invalidateCache();
        return uri;
      }
      throw new IllegalArgumentException("Failed to update user: " + user.toString());
    }
    throw new IllegalArgumentException("Failed to update user: " + user.toString());
  }


  @Override
  public URI saveUser(User user) {
    if (user.getUsername() == null) {
      throw new IllegalArgumentException("Username is required! User: " + user);
    }

    boolean anyMatch =
        getAllUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()));
    if (anyMatch) {
      throw new IllegalStateException(
          "User with username [" + user.getUsername() + "] already exists!");
    }

    StorageObject<User> userStorageObj = storage.get().instanceOf(User.class);
    userStorageObj.setObject(user);
    URI uri = storage.get().save(userStorageObj);

    addToObjectMap(USER_OBJECTMAP_REFERENCE, user.getUsername(), uri);

    invalidateCache();

    return uri;
  }

  @Override
  public URI updateGroup(Group group) {
    String name = group.getName();
    if (name == null) {
      throw new IllegalArgumentException("Group: " + group.toString() + "has no name!");
    }
    Group groupByName = getGroupByName(name);
    if (storage.get().exists(groupByName.getUri())) {
      StorageObject<Group> oldGroup = storage.get().load(groupByName.getUri(), Group.class);
      oldGroup.setObject(group);
      URI uri = storage.get().save(oldGroup);
      invalidateCache();
      return uri;
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
    storage.get().updateAttachedMap(
        storage.get().settings().getUri(),
        new ObjectMapRequest().mapName(mapName).putUrisToAddItem(key, value));
  }

  private void removeFromObjectMap(String mapName, String key, URI value) {
    storage.get().updateAttachedMap(
        storage.get().settings().getUri(),
        new ObjectMapRequest().mapName(mapName).putUrisToRemoveItem(key, value));
  }

  @Override
  public void restoreDeletedUser(URI userUri) {

    // remove from setting INVALID_USER_OBJECTMAP_REFERENCE
    removeItemFromObjectMapByValue(INACTIVE_USER_OBJECTMAP_REFERENCE, userUri);

    // add to USER_OBJECTMAP_REFERENCE
    User user = getUser(userUri);
    addToObjectMap(USER_OBJECTMAP_REFERENCE, user.getUsername(), userUri);

    setUserToActive(userUri);
  }

  private void setUserToActive(URI userUri) {
    StorageObject<User> userSO = storage.get().load(userUri, User.class);
    userSO.getObject().setInactive(false);
    storage.get().save(userSO);
    invalidateCache();
  }

}
