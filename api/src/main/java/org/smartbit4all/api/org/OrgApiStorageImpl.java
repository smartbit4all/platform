package org.smartbit4all.api.org;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.gateway.SecurityGateways;
import org.smartbit4all.api.org.bean.BulkUpdateOperation;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.GroupOfGroupUpdate;
import org.smartbit4all.api.org.bean.GroupUpdate;
import org.smartbit4all.api.org.bean.GroupsOfUser;
import org.smartbit4all.api.org.bean.GroupsOfUserCollection;
import org.smartbit4all.api.org.bean.OrgBulkUpdate;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.org.bean.UserOfGroupUpdate;
import org.smartbit4all.api.org.bean.UserUpdate;
import org.smartbit4all.api.org.bean.UsersOfGroup;
import org.smartbit4all.api.org.bean.UsersOfGroupCollection;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.UserSessionApi;
import org.smartbit4all.api.setting.LocaleString;
import org.smartbit4all.api.storage.bean.ObjectMap;
import org.smartbit4all.api.storage.bean.ObjectMapRequest;
import org.smartbit4all.api.storage.bean.ObjectReference;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.smartbit4all.core.utility.ReflectionUtility;
import org.smartbit4all.domain.data.storage.ObjectStorageImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.domain.data.storage.StorageObjectReferenceEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

public class OrgApiStorageImpl implements OrgApi {

  private static final Logger log = LoggerFactory.getLogger(OrgApiStorageImpl.class);

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

  @Autowired(required = false)
  private UserSessionApi userSessionApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

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
   * The cache for users of the groups and subgroups.
   */
  private Cache<URI, List<User>> usersOfGroupCache =
      CacheBuilder.newBuilder().concurrencyLevel(10).build();

  /**
   * The cache for users of the groups and parent groups.
   */
  private Cache<URI, List<User>> usersOfGroupAndParentGroupsCache =
      CacheBuilder.newBuilder().concurrencyLevel(10).build();

  /**
   * The cache for the groups of user.
   */
  private Cache<String, Group> groupByNameCache =
      CacheBuilder.newBuilder().concurrencyLevel(10).build();

  public static final Group GROUP_NOT_FOUND = new Group().name("GROUP_NOT_FOUND");

  private List<Group> allGroups;

  /**
   * Invalidate the cache after modification.
   */
  private final synchronized void invalidateCache() {
    groupsOfUserCache.invalidateAll();
    usersOfGroupCache.invalidateAll();
    usersOfGroupAndParentGroupsCache.invalidateAll();
    groupByNameCache.invalidateAll();
    allGroups = null;
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
                (sg, uri) -> OrgUtils.securityPredicate(
                    self,
                    getCurrentUserProvider(),
                    sessionApi,
                    sg, uri));
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

  private Supplier<User> getCurrentUserProvider() {
    if (userSessionApi != null) {
      return () -> userSessionApi.currentUser();
    }
    if (sessionApi != null) {
      return () -> sessionApi.getUser();
    }
    throw new IllegalStateException(
        "There is no UserSessionApi nor SessionApi registered in the Spring context!");
  }

  /**
   * If there is no Group for the SecurtyGroup, then create one.
   */
  private Group checkGroupExist(SecurityGroup securityGroup) {
    Group group = getGroupByName(securityGroup.getName());
    if (group == null) {
      Group newGroup =
          new Group()
              .name(securityGroup.getName())
              .title(securityGroup.getTitle())
              .description(securityGroup.getDescription())
              .builtIn(securityGroup.isbuiltIn());
      saveGroup(newGroup);
      return newGroup;
    } else if (!(Objects.equal(group.getName(), securityGroup.getName())
        && Objects.equal(group.getTitle(), securityGroup.getTitle())
        && Objects.equal(group.getDescription(), securityGroup.getDescription())
        && Objects.equal(group.getBuiltIn(), securityGroup.isbuiltIn()))) {
      group
          .name(securityGroup.getName())
          .title(securityGroup.getTitle())
          .description(securityGroup.getDescription())
          .builtIn(securityGroup.isbuiltIn());
      updateGroup(group);
    }
    return null;
  }

  @EventListener(ApplicationStartedEvent.class)
  public void initSecurityOptions() throws Exception {
    if (securityOptions != null) {
      groupByNameCache.invalidateAll();
      usersOfGroupCache.invalidateAll();
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
  public synchronized List<Group> getAllGroups() {
    if (allGroups == null) {
      ObjectMap groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE);
      Collection<URI> values = groupObjectMap.getUris().values();
      allGroups = storage.get().read(new ArrayList<>(values), Group.class);
    }
    return allGroups;
  }

  @Override
  public List<User> getActiveUsers() {
    return getUsersFromObjectMap(USER_OBJECTMAP_REFERENCE);
  }

  @Override
  public List<User> getInactiveUsers() {
    return getUsersFromObjectMap(INACTIVE_USER_OBJECTMAP_REFERENCE);
  }

  private List<User> getUsersFromObjectMap(String objectMapName) {
    ObjectMap userObjectMap = loadObjectMap(objectMapName);
    Collection<URI> values = userObjectMap.getUris().values();
    return storage.get().read(new ArrayList<>(values), User.class);
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
      return usersOfGroupCache.get(
          groupUri,
          () -> getUsersOfGroups(Arrays.asList(groupUri)));
    } catch (ExecutionException e) {
      log.error("Unable to retrieve the users of group.", e);
      return Collections.emptyList();
    }
  }

  @Override
  public List<User> getUsersOfGroupAndParentGroups(URI groupUri) {
    try {
      return usersOfGroupAndParentGroupsCache.get(
          groupUri,
          () -> {
            List<URI> groupUris = getAllParentGroups(groupUri);
            groupUris.add(groupUri);
            return getUsersOfGroups(groupUris);
          });
    } catch (ExecutionException e) {
      log.error("Unable to retrieve the users of group.", e);
      return Collections.emptyList();
    }
  }

  private List<User> getUsersOfGroups(List<URI> groupUris) {
    Set<URI> users = new HashSet<>();

    UsersOfGroupCollection collection =
        readSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    List<UsersOfGroup> usersOfGroupCollection = collection.getUsersOfGroupCollection();
    for (UsersOfGroup usersOfGroup : usersOfGroupCollection) {

      if (groupUris.contains(usersOfGroup.getGroupUri())) {
        users.addAll(usersOfGroup.getUsers());
      }
    }
    return storage.get().read(new ArrayList<>(users), User.class);
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
  public List<User> getUsers(List<URI> userUris) {
    return storage.get().read(new ArrayList<>(userUris), User.class);
  }

  public List<Group> getGroups(List<URI> groupUris) {
    return storage.get().read(new ArrayList<>(groupUris), Group.class);
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

  private List<URI> getAllParentGroups(URI groupUri) {
    return getParentUris(getAllGroups(), groupUri).collect(toList());
  }

  private Stream<URI> getParentUris(List<Group> groups, URI groupUri) {
    return groups.stream()
        .filter(group -> group.getChildren().contains(groupUri))
        .map(Group::getUri)
        .flatMap(uri -> Stream.concat(Stream.of(uri), getParentUris(groups, uri)));
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
    final URI valueWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(value);
    return objectMap.getUris().entrySet().stream()
        .filter(e -> {
          final URI uriWithoutVersion = ObjectStorageImpl.getUriWithoutVersion(e.getValue());
          return uriWithoutVersion.equals(valueWithoutVersion);
        })
        .findFirst()
        .map(Map.Entry::getKey)
        .orElse(null);
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
  public User getUserByUsernameIgnoreCase(String username) {
    ObjectMap activeObjectMap = loadObjectMap(USER_OBJECTMAP_REFERENCE);

    try {
      Map<String, URI> uris = activeObjectMap.getUris().entrySet().stream()
          .collect(toMap(e -> e.getKey().toLowerCase(), Entry::getValue));
      URI userUri = uris.get(username.toLowerCase());
      if (userUri == null) {
        return null;
      }
      return storage.get().exists(userUri) ? storage.get().read(userUri, User.class) : null;
    } catch (Exception e) {
      log.warn("The same username present with different cases.", e);
      return getUserByUsername(username);
    }
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
        URI uri = storage.get().update(userByUsername.getUri(), User.class, u -> user);
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
        getActiveUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()));
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
      URI uri = storage.get().update(groupByName.getUri(), Group.class, g -> group);
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
    final String username = getUser(userUri).getUsername();
    if (username == null) {
      throw new IllegalArgumentException(
          "User " + userUri + " cannot be restored, because they lack a username!");
    }
    final boolean usernameIsAlreadyTaken = getActiveUsers().stream()
        .map(User::getUsername)
        .anyMatch(username::equals);
    if (usernameIsAlreadyTaken) {
      throw new IllegalStateException("Deleted user cannot be restored, because their username ["
          + username
          + "] is already taken!");
    }

    // remove from setting INVALID_USER_OBJECTMAP_REFERENCE
    removeItemFromObjectMapByValue(INACTIVE_USER_OBJECTMAP_REFERENCE, userUri);

    // add to USER_OBJECTMAP_REFERENCE
    User user = getUser(userUri);
    addToObjectMap(USER_OBJECTMAP_REFERENCE, user.getUsername(), userUri);

    setUserToActive(userUri);
  }

  private void setUserToInactive(URI userUri) {
    storage.get().update(userUri, User.class, u -> u.inactive(true));
    invalidateCache();
  }

  private void setUserToActive(URI userUri) {
    storage.get().update(userUri, User.class, u -> u.inactive(false));
    invalidateCache();
  }

  @Override
  public void updateUsername(User user, String username) {
    removeFromObjectMap(USER_OBJECTMAP_REFERENCE, user.getUsername(), null);
    storage.get().update(user.getUri(), User.class, u -> user.username(username));
    addToObjectMap(USER_OBJECTMAP_REFERENCE, username, user.getUri());

    invalidateCache();

  }

  @Override
  public void bulkUpdate(OrgBulkUpdate update) {
    bulkUpdateGroups(update);
    bulkUpdateUsers(update);
    bulkUpdateGroupsOfGroups(update);
    bulkUpdateUsersOfGroups(update);

    invalidateCache();

  }

  private void bulkUpdateUsersOfGroups(OrgBulkUpdate update) {
    // load users of group
    StorageObject<UsersOfGroupCollection> usersOfGRoupCollectionSO =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection usersOfGroupCollection = usersOfGRoupCollectionSO.getObject();
    List<UsersOfGroup> usersOfGroupList = usersOfGroupCollection.getUsersOfGroupCollection();
    Map<URI, UsersOfGroup> usersOfGroupMap =
        usersOfGroupList.stream().collect(Collectors.toMap(a -> a.getGroupUri(), a -> a));

    // load Groups of user
    StorageObject<GroupsOfUserCollection> groupsOfUserCollectionSO =
        loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
    GroupsOfUserCollection groupsOfUserCollection = groupsOfUserCollectionSO.getObject();
    List<GroupsOfUser> groupsOfUserList = groupsOfUserCollection.getGroupsOfUserCollection();
    Map<Object, GroupsOfUser> groupsOfUserMap =
        groupsOfUserList.stream().collect(Collectors.toMap(a -> a.getUserUri(), a -> a));

    // Load all users
    Map<String, URI> userObjectMap = loadObjectMap(USER_OBJECTMAP_REFERENCE).getUris();
    // Load all groups
    Map<String, URI> groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE).getUris();

    for (UserOfGroupUpdate usersOfGroupUpdate : update.getUsersOfGroup()) {

      URI groupUri = groupObjectMap.get(usersOfGroupUpdate.getGroup().getName());
      URI userUri = userObjectMap.get(usersOfGroupUpdate.getUser().getUsername());
      if (usersOfGroupUpdate.getOperation() == BulkUpdateOperation.INSERT) {
        // add to UsersOfGroup
        UsersOfGroup usersOfGroup = usersOfGroupMap.get(groupUri);
        if (usersOfGroup == null) {
          usersOfGroup = new UsersOfGroup().groupUri(groupUri);
          usersOfGroupList.add(usersOfGroup);
          usersOfGroupMap.put(groupUri, usersOfGroup);
        }
        usersOfGroup.addUsersItem(userUri);

        // add to GroupsOfUser
        GroupsOfUser groupsOfUser = groupsOfUserMap.get(userUri);
        if (groupsOfUser == null) {
          groupsOfUser = new GroupsOfUser().userUri(userUri);
          groupsOfUserList.add(groupsOfUser);
          groupsOfUserMap.put(userUri, groupsOfUser);
        }
        groupsOfUser.addGroupsItem(groupUri);
      } else if (usersOfGroupUpdate.getOperation() == BulkUpdateOperation.DELETE) {
        // remove from UsersOfGroup
        UsersOfGroup usersOfGroup = usersOfGroupMap.get(groupUri);
        if (usersOfGroup != null) {
          usersOfGroup.getUsers().remove(userUri);
        }
        // remove from GroupsOfUser
        GroupsOfUser groupsOfUser = groupsOfUserMap.get(userUri);
        if (groupsOfUser != null) {
          groupsOfUser.getGroups().remove(groupUri);
        }
      }
    }

    // update collections
    if (!update.getUsersOfGroup().isEmpty()) {
      storage.get().save(usersOfGRoupCollectionSO);
      storage.get().save(groupsOfUserCollectionSO);
    }
  }

  private void bulkUpdateGroupsOfGroups(OrgBulkUpdate update) {
    Map<String, URI> groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE).getUris();
    for (GroupOfGroupUpdate groupsOfGroup : update.getGroupsOfGroup()) {
      Group parentGroup = groupsOfGroup.getParentGroup();
      Group childGroup = groupsOfGroup.getChildGroup();
      URI parentGroupUri = groupObjectMap.get(parentGroup.getName());
      if (parentGroupUri == null) {
        // this group has been deleted
        continue;
      }
      URI childGroupUriFromStorage = groupObjectMap.get(childGroup.getName());
      URI childGroupUri =
          childGroupUriFromStorage != null ? childGroupUriFromStorage : childGroup.getUri();
      if (groupsOfGroup.getOperation() == BulkUpdateOperation.INSERT) {
        if (parentGroup.getChildren().contains(childGroupUri)) {
          continue;
        }
        storage.get().update(parentGroupUri, Group.class,
            g -> g.addChildrenItem(childGroupUri));
      } else if (groupsOfGroup.getOperation() == BulkUpdateOperation.DELETE) {
        if (parentGroup.getChildren().contains(childGroupUri)) {
          storage.get().update(parentGroupUri, Group.class,
              g -> {
                g.getChildren().remove(childGroupUri);
                return g;
              });
        }
      }
    }
  }

  private void bulkUpdateUsers(OrgBulkUpdate update) {
    // collect new and deleted users
    List<User> newUsers = new ArrayList<>();
    List<User> deletedUsers = new ArrayList<>();
    Map<String, URI> userObjectMap = loadObjectMap(USER_OBJECTMAP_REFERENCE).getUris();
    for (UserUpdate userUpdate : update.getUsers()) {
      User user = userUpdate.getUser();
      if (userUpdate.getOperation() == BulkUpdateOperation.INSERT) {
        URI uri = userObjectMap.get(user.getUsername());
        if (uri == null) {
          StorageObject<User> userStorageObj = storage.get().instanceOf(User.class);
          userStorageObj.setObject(user);
          storage.get().save(userStorageObj);
          newUsers.add(user);
          userObjectMap.put(user.getUsername(), user.getUri());
        } else {
          storage.get().update(user.getUri(), User.class, u -> user);
        }
      } else if (userUpdate.getOperation() == BulkUpdateOperation.UPDATE) {
        if (storage.get().exists(user.getUri())) {
          storage.get().update(user.getUri(), User.class, u -> user);
        }
      } else if (userUpdate.getOperation() == BulkUpdateOperation.DELETE) {
        storage.get().update(user.getUri(), User.class, u -> u.inactive(true));
        deletedUsers.add(user);
        userObjectMap.remove(user.getUsername());
      }
    }

    boolean userAdded = false;
    boolean userDeleted = false;
    ObjectMapRequest userMap = new ObjectMapRequest().mapName(USER_OBJECTMAP_REFERENCE);
    for (User user : newUsers) {
      userAdded = true;
      userMap.putUrisToAddItem(user.getUsername(), user.getUri());
    }
    for (User user : deletedUsers) {
      userDeleted = true;
      userMap.putUrisToRemoveItem(user.getUsername(), user.getUri());
    }
    if (userAdded || userDeleted) {
      storage.get().updateAttachedMap(
          storage.get().settings().getUri(), userMap);
    }

    if (userDeleted) {
      ObjectMapRequest inactiveUserMap =
          new ObjectMapRequest().mapName(INACTIVE_USER_OBJECTMAP_REFERENCE);

      StorageObject<GroupsOfUserCollection> groupsOfUserCollectionSO =
          loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
      GroupsOfUserCollection groupsOfUserCollection = groupsOfUserCollectionSO.getObject();
      List<GroupsOfUser> groupsOfUserList = groupsOfUserCollection.getGroupsOfUserCollection();
      Map<URI, GroupsOfUser> groupsOfUserByUserUri =
          groupsOfUserList.stream().collect(Collectors.toMap(u -> u.getUserUri(), u -> u));

      StorageObject<UsersOfGroupCollection> usersOfGroupCollectionReference =
          loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
      List<UsersOfGroup> usersOfGroupCollection =
          usersOfGroupCollectionReference.getObject().getUsersOfGroupCollection();
      Map<URI, UsersOfGroup> usersOfGroupByUri =
          usersOfGroupCollection.stream().collect(Collectors.toMap(g -> g.getGroupUri(), g -> g));

      for (User user : deletedUsers) {
        // Inactivate user
        inactiveUserMap.putUrisToAddItem(user.getUsername(), user.getUri());

        GroupsOfUser groupsOfUser = groupsOfUserByUserUri.get(user.getUri());
        if (groupsOfUser != null) {
          // remove the group of user from the group of user list
          groupsOfUserList.remove(groupsOfUser);
          // remove from all users of group
          for (URI groupUri : groupsOfUser.getGroups()) {
            UsersOfGroup usersOfGroup = usersOfGroupByUri.get(groupUri);
            if (usersOfGroup != null) {
              usersOfGroup.getUsers().remove(user.getUri());
            }
          }
        }
      }
      storage.get().updateAttachedMap(
          storage.get().settings().getUri(), inactiveUserMap);
      storage.get().save(usersOfGroupCollectionReference);
      storage.get().save(groupsOfUserCollectionSO);
    }
  }

  private void bulkUpdateGroups(OrgBulkUpdate update) {
    List<Group> newGroups = new ArrayList<>();
    List<Group> deletedGroups = new ArrayList<>();

    ObjectMap groupObjectMap = loadObjectMap(GROUP_OBJECTMAP_REFERENCE);

    for (GroupUpdate groupUpdate : update.getGroups()) {
      Group group = groupUpdate.getGroup();
      if (groupUpdate.getOperation() == BulkUpdateOperation.INSERT) {
        URI uri = groupObjectMap.getUris().get(group.getName());
        if (uri == null) {
          StorageObject<Group> groupStorageObj = storage.get().instanceOf(Group.class);
          groupStorageObj.setObject(group);
          storage.get().save(groupStorageObj);
          newGroups.add(group);
        } else {
          storage.get().update(group.getUri(), Group.class, g -> groupUpdate.getGroup());
        }
      } else if (groupUpdate.getOperation() == BulkUpdateOperation.UPDATE) {
        if (storage.get().exists(group.getUri())) {
          storage.get().update(group.getUri(), Group.class, g -> groupUpdate.getGroup());
        }
      } else if (groupUpdate.getOperation() == BulkUpdateOperation.DELETE) {
        if (storage.get().exists(group.getUri())) {
          StorageObject<Group> storageObject = storage.get().load(group.getUri(), Group.class);
          storageObject.setDeleted();
          storage.get().save(storageObject);
        }
        deletedGroups.add(group);
      }

    }

    ObjectMapRequest groupMap = new ObjectMapRequest().mapName(GROUP_OBJECTMAP_REFERENCE);
    boolean groupAdded = false;
    boolean groupDeleted = false;
    for (Group group : newGroups) {
      groupAdded = true;
      groupMap.putUrisToAddItem(group.getName(), group.getUri());
    }
    for (Group group : deletedGroups) {
      groupDeleted = true;
      groupMap.putUrisToRemoveItem(group.getName(), group.getUri());
    }
    if (groupAdded || groupDeleted) {
      storage.get().updateAttachedMap(
          storage.get().settings().getUri(), groupMap);
    }
    if (groupDeleted) {
      StorageObject<GroupsOfUserCollection> groupsOfUserCollectionSO =
          loadSettingsReference(GROUPS_OF_USER_LIST_REFERENCE, GroupsOfUserCollection.class);
      GroupsOfUserCollection groupsOfUserCollection = groupsOfUserCollectionSO.getObject();
      List<GroupsOfUser> groupsOfUserList = groupsOfUserCollection.getGroupsOfUserCollection();
      Map<Object, GroupsOfUser> groupsOfUserByUserUri =
          groupsOfUserList.stream().collect(Collectors.toMap(u -> u.getUserUri(), u -> u));

      // settings -> UsersOfGroupCollection -> UsersOfGroup -> Remove user from groups
      StorageObject<UsersOfGroupCollection> usersOfGroupCollectionReference =
          loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
      List<UsersOfGroup> usersOfGroupCollection =
          usersOfGroupCollectionReference.getObject().getUsersOfGroupCollection();
      Map<Object, UsersOfGroup> usersOfGroupByUri =
          usersOfGroupCollection.stream().collect(Collectors.toMap(g -> g.getGroupUri(), g -> g));

      for (Group group : deletedGroups) {
        UsersOfGroup usersOfGroup = usersOfGroupByUri.get(group.getUri());
        if (usersOfGroup != null) {
          usersOfGroupCollection.remove(usersOfGroup);
          for (URI userUri : usersOfGroup.getUsers()) {
            GroupsOfUser groupsOfUser = groupsOfUserByUserUri.get(userUri);
            if (groupsOfUser != null) {
              groupsOfUser.getGroups().remove(group.getUri());
            }
          }
        }
      }
      storage.get().save(usersOfGroupCollectionReference);
      storage.get().save(groupsOfUserCollectionSO);
    }
  }

  @Override
  public org.smartbit4all.api.org.bean.OrgState getOrgState() {
    List<User> users = getActiveUsers();
    List<Group> groups = getAllGroups();

    Map<String, User> usersByUserNames =
        users.stream().collect(Collectors.toMap(u -> u.getUsername(), u -> u));
    Map<URI, User> usersByUri =
        users.stream().collect(Collectors.toMap(u -> u.getUri(), u -> u));
    Map<String, Group> groupsByNames =
        groups.stream().collect(Collectors.toMap(g -> g.getName(), g -> g));
    Map<URI, Group> groupsByUri =
        groups.stream().collect(Collectors.toMap(g -> g.getUri(), g -> g));

    StorageObject<UsersOfGroupCollection> usersOfGRoupCollectionSO =
        loadSettingsReference(USERS_OF_GROUP_LIST_REFERENCE, UsersOfGroupCollection.class);
    UsersOfGroupCollection usersOfGroupCollection = usersOfGRoupCollectionSO.getObject();

    List<UsersOfGroup> usersOfGroupList = usersOfGroupCollection.getUsersOfGroupCollection();

    Map<String, List<User>> usersOfGroup = usersOfGroupList.stream()
        .collect(Collectors.toMap(
            u -> groupsByUri.get(u.getGroupUri()).getName(),
            u -> u.getUsers().stream().map(uri -> usersByUri.get(uri))
                .collect(Collectors.toList())));

    Map<String, List<Group>> groupsOfGroup = groups.stream()
        .collect(Collectors.toMap(
            g -> g.getName(),
            g -> g.getChildren().stream().map(uri -> groupsByUri.get(uri))
                .collect(Collectors.toList())));
    return new org.smartbit4all.api.org.bean.OrgState()
        .users(usersByUserNames)
        .groups(groupsByNames)
        .usersOfGroup(usersOfGroup)
        .groupsOfGroup(groupsOfGroup);
  }
}
