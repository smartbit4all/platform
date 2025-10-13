package org.smartbit4all.api.object;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.ContextMappingDefinition;
import org.smartbit4all.api.object.bean.ContextMappingItem;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.ObjectListMapping;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyFormatter;
import org.smartbit4all.api.object.bean.ObjectPropertyFormatterParameter;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.ACLOperation;
import org.smartbit4all.api.org.bean.ACLSubject;
import org.smartbit4all.api.org.bean.ACLSubjectOperations;
import org.smartbit4all.api.org.bean.ACLSubjectSubscription;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleExtensibleObject;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
import org.smartbit4all.api.sample.bean.SampleProperties;
import org.smartbit4all.api.sample.bean.SamplePropertyContainer;
import org.smartbit4all.api.sample.bean.SamplePropertyContainerWithId;
import org.smartbit4all.api.sample.bean.SampleStandaloneObject;
import org.smartbit4all.core.object.ContextMapping;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectMapping;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectPropertyResolver;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.annotation.DirtiesContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class ObjectApiTestBase {

  public static final String USER_CATEGORY = "USER_CATEGORY";
  private static final String PASSWDCODE =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";
  private static final String PASSWD =
      "asd";

  private static final String ADDED_WITHTYPECLASSNAME = "ADDED_WITHTYPECLASSNAME";
  private static final String ADDED_WITHTYPECLASS = "ADDED_WITHTYPECLASS";
  public static final String SCHEMA_ASPECTS = "aspectTest";

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private BranchApi branchApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  private SubjectManagementApi subjectManagementApi;

  @Autowired
  private AccessControlInternalApi accessControlInternalApi;

  @Autowired
  private StorageApi storageApi;

  /**
   * A flag to check if every {@link ObjectNode} has different physical object id but the same
   * object has the same.
   */
  protected boolean checkPhysicalId = false;

  @Test
  void testPredefinedDefinition() throws IOException {
    ObjectDefinition<DomainObjectTestBean> definition =
        objectApi.definition(DomainObjectTestBean.class);
    assertTrue(definition.isExplicitUri());
    assertEquals(ObjectMapper.class.getName(), definition.getDefaultSerializer().getName());

    DomainObjectTestBean myBean = new DomainObjectTestBean();

    myBean.setCounter(1);
    myBean.setEnabled(false);
    // Add specific characters to check utf-8 save and load.
    myBean.setName("árvíztűrőtükörfúrógép");

    BinaryData binaryData =
        definition.getDefaultSerializer().serialize(myBean, DomainObjectTestBean.class);

    Optional<DomainObjectTestBean> deserializeResult =
        definition.getDefaultSerializer().deserialize(binaryData, DomainObjectTestBean.class);

    Assertions.assertTrue(deserializeResult.isPresent());

    DomainObjectTestBean reloadedBean = deserializeResult.get();

    assertEquals(myBean.getName(), reloadedBean.getName());

  }

  @Test
  void testUndefinedBeanDefinition() throws IOException {
    ObjectDefinition<SampleContainerItem> definition =
        objectApi.definition(SampleContainerItem.class);
    assertEquals(SampleContainerItem.class.getName().replace('.', '_'),
        definition.getAlias());
    assertEquals(ObjectMapper.class.getName(), definition.getDefaultSerializer().getName());

    SampleContainerItem myBean = new SampleContainerItem().name("árvíztűrőtükörfúrógép.txt")
        .cost(Long.valueOf(1024)).createdAt(OffsetDateTime.now());

    URI uri = URI.create("scheme:/path#fragment");
    definition.setUri(myBean, uri);

    BinaryData binaryData =
        definition.getDefaultSerializer().serialize(myBean, SampleContainerItem.class);

    SampleContainerItem reloadedBean = definition.getDefaultSerializer()
        .deserialize(binaryData, SampleContainerItem.class).get();

    assertEquals(myBean.getName(), reloadedBean.getName());
    assertEquals(myBean.getCost(), reloadedBean.getCost());
    assertEquals(myBean.getUri(), definition.getUri(reloadedBean));


  }

  @Test
  @DirtiesContext
  void extendExistingObjectDefinition() {
    ObjectDefinition<SampleCategory> definition = objectApi.definition(SampleCategory.class);
    List<String> propertyList = new ArrayList<>(Arrays.asList(SampleCategory.URI,
        SampleCategory.NAME, SampleCategory.COLOR, SampleCategory.CONTAINER_ITEMS,
        SampleCategory.COST, SampleCategory.CREATED_AT, SampleCategory.KEY_WORDS,
        SampleCategory.LINKS, SampleCategory.SUB_CATEGORIES));
    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);
    definition.builder().addProperty(ADDED_WITHTYPECLASS, String.class)
        .addProperty(ADDED_WITHTYPECLASSNAME, Long.class.getName()).commit();

    propertyList.add(ADDED_WITHTYPECLASS);
    propertyList.add(ADDED_WITHTYPECLASSNAME);

    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

    ObjectDefinitionData savedDefinition =
        objectApi.loadLatest(definition.getDefinitionData().getUri())
            .getObject(ObjectDefinitionData.class);

    org.assertj.core.api.Assertions.assertThat(savedDefinition.getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

  }

  @Test
  void createBrandNewObjectDefinition() {
    ObjectDefinition<?> definition =
        objectDefinitionApi
            .definition(
                SampleCategory.class.getPackage().getName() + StringConstant.DOT + "Extension");
    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties())
        .hasSize(1)
        .allSatisfy(
            prop -> org.assertj.core.api.Assertions.assertThat(prop.getName()).isEqualTo("uri"));
    definition.builder().addProperty(ADDED_WITHTYPECLASS, String.class)
        .addProperty(ADDED_WITHTYPECLASSNAME, Long.class.getName()).commit();

    List<String> propertyList = new ArrayList<>();
    propertyList.add(ADDED_WITHTYPECLASS);
    propertyList.add(ADDED_WITHTYPECLASSNAME);

    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

    ObjectDefinitionData savedDefinition =
        objectApi.loadLatest(definition.getDefinitionData().getUri())
            .getObject(ObjectDefinitionData.class);

    org.assertj.core.api.Assertions.assertThat(savedDefinition.getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

  }

  @Test
  void objectMapHelper() {

    Map<String, Object> baseMap = new HashMap<>();
    baseMap.put("nullvalue", null);
    baseMap.put(String.class.getSimpleName(), "My string");
    baseMap.put(SampleCategoryType.class.getSimpleName(),
        new SampleCategoryType().code("CODE 1").name("Name 1"));
    baseMap.put(Double.class.getSimpleName(), "1.0");

    {
      Map<String, Object> categoryTypeAsMap = new HashMap<>();
      categoryTypeAsMap.put(SampleCategoryType.CODE, "CODE 2");
      categoryTypeAsMap.put(SampleCategoryType.NAME, "Name 2");
      baseMap.put(Map.class.getSimpleName(),
          categoryTypeAsMap);
    }
    baseMap.put(List.class.getSimpleName(),
        Arrays.asList(new SampleCategoryType().code("LIST CODE 1").name("List Name 1"),
            new SampleCategoryType().code("LIST CODE 2").name("List Name 2")));

    {
      List<Map<String, Object>> list = new ArrayList<>();
      {
        Map<String, Object> categoryTypeAsMap = new HashMap<>();
        categoryTypeAsMap.put(SampleCategoryType.CODE, "LIST CODE 1");
        categoryTypeAsMap.put(SampleCategoryType.NAME, "List Name 1");
        list.add(categoryTypeAsMap);
      }
      {
        Map<String, Object> categoryTypeAsMap = new HashMap<>();
        categoryTypeAsMap.put(SampleCategoryType.CODE, "LIST CODE 2");
        categoryTypeAsMap.put(SampleCategoryType.NAME, "List Name 2");
        list.add(categoryTypeAsMap);
      }
      baseMap.put("listWithMap",
          list);
    }

    ObjectMapHelper mapHelper = new ObjectMapHelper(baseMap, objectApi, "test map");

    {
      String require = mapHelper.require("nullvalue", String.class);
      Assertions.assertNull(require);
    }
    {
      String require = mapHelper.requireNonNullElse("nullvalue", String.class, "apple");
      Assertions.assertEquals("apple", require);
    }

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> mapHelper.requireNonNull("nullvalue", String.class));

    {
      String require = mapHelper.require(String.class.getSimpleName(), String.class);
      Assertions.assertEquals("My string", require);
    }

    {
      SampleCategoryType require =
          mapHelper.require(SampleCategoryType.class.getSimpleName(), SampleCategoryType.class);
      Assertions.assertEquals("CODE 1", require.getCode());
      Assertions.assertEquals("Name 1", require.getName());
    }

    {
      SampleCategoryType require =
          mapHelper.require(Map.class.getSimpleName(), SampleCategoryType.class);
      Object object = mapHelper.getMap().get(Map.class.getSimpleName());

      Assertions.assertEquals(object, require);
    }

    {
      List<String> require = mapHelper.requireNonNullElseAsList("not existing key", String.class,
          Arrays.asList("s1", "s2", "s3"));
      org.assertj.core.api.Assertions.assertThat(require).containsExactly("s1", "s2", "s3");
    }

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> mapHelper.requireNonNullAsList("not existing key", String.class));

    {
      List<SampleCategoryType> require =
          mapHelper.requireNonNullAsList(List.class.getSimpleName(), SampleCategoryType.class);
      org.assertj.core.api.Assertions.assertThat(require.stream().map(ct -> ct.getCode()))
          .containsExactly("LIST CODE 1", "LIST CODE 2");
    }

    {
      List<SampleCategoryType> require =
          mapHelper.requireNonNullAsList("listWithMap", SampleCategoryType.class);
      @SuppressWarnings("unchecked")
      List<SampleCategoryType> listFromMap =
          (List<SampleCategoryType>) mapHelper.getMap().get("listWithMap");
      org.assertj.core.api.Assertions.assertThat(require).containsSequence(listFromMap);
    }

  }

  @Test
  void testAspects() {
    URI rootUri = objectApi.saveAsNew(SCHEMA_ASPECTS, new SampleCategory().name("Root"));
    URI everybodyUri = objectApi.getLatestUri(
        objectApi.saveAsNew(OrgApiStorageImpl.ORG_SCHEME, new Group().name("everybody")));
    {
      ObjectNode rootNode = objectApi.load(rootUri);
      org.assertj.core.api.Assertions.assertThat(rootNode.aspects().get()).isNullOrEmpty();
      rootNode.aspects().modify(AccessControlInternalApi.ACL_ASPECT, ACL.class,
          acl -> new ACL().rootEntry(
              new ACLEntry().addEntriesItem(new ACLEntry().subject(new Subject().ref(everybodyUri))
                  .addOperationsItem("read").addOperationsItem("write"))));
      objectApi.save(rootNode);
    }
    {
      // Now read the ACL again.
      ObjectNode rootNode = objectApi.loadLatest(rootUri);
      org.assertj.core.api.Assertions.assertThat(rootNode.aspects().get()).isNotNull();
      ACL acl = rootNode.aspects().get(AccessControlInternalApi.ACL_ASPECT, ACL.class);
      org.assertj.core.api.Assertions
          .assertThat(acl.getRootEntry().getEntries().stream().map(e -> e.getSubject().getRef()))
          .contains(everybodyUri);
    }

  }

  @Test
  protected void testBranchBasics() {
    URI objectUri = objectApi.saveAsNew(SCHEMA_ASPECTS, new SampleCategory().name("Root"));

    ObjectNode node = objectApi.load(objectUri);
    node.modify(SampleCategory.class, cat -> cat.name("Root modified on branch"));

    URI branchUri = branchApi.makeBranch("test branch").getUri();
    objectApi.save(node, branchUri);

    node = objectApi.load(objectUri);
    node.modify(SampleCategory.class, cat -> cat.name("Root modified on main"));
    objectApi.save(node, null);

    ObjectNode nodeMain = objectApi.loadLatest(objectUri);
    ObjectNode nodeBranch = objectApi.loadLatest(objectUri, branchUri);
    assertEquals("Root modified on main", nodeMain.getValueAsString(SampleCategory.NAME));
    assertEquals("Root modified on branch", nodeBranch.getValueAsString(SampleCategory.NAME));
  }

  @Test
  protected void testSnapshotBasics() {
    URI userUri = objectApi.saveAsNew(SCHEMA_ASPECTS,
        new User().name("user"));
    URI userLatestUri = objectApi.getLatestUri(userUri);
    URI objectUri = objectApi.saveAsNew(SCHEMA_ASPECTS,
        new SampleContainerItem().name("Root").userUri(userLatestUri));

    BranchEntry branch = branchApi.makeBranch("snapshot branch");
    ObjectNode contentNode = objectApi.load(
        objectApi.request(SampleContainerItem.class)
            .add(SampleContainerItem.USER_URI),
        objectUri);
    URI branchuri = branch.getUri();
    branchApi.addSnapshotBranch(branchuri, Arrays.asList(contentNode));

    ObjectNode userNode = objectApi.load(userLatestUri);
    userNode.modify(User.class, user -> user.name("user modified on main"));
    objectApi.save(userNode, null);

    ObjectNode userNodeMain = objectApi.loadLatest(userLatestUri);
    ObjectNode userNodeBranch = objectApi.loadLatest(userLatestUri, branchuri);
    assertEquals("user modified on main", userNodeMain.getValueAsString(User.NAME));
    assertEquals("user", userNodeBranch.getValueAsString(User.NAME));

    ObjectNode itemNodeOnMain = objectApi.load(objectUri);
    ObjectNode itemNodeOnBranch = objectApi.load(objectUri, branchuri);
    assertEquals("user modified on main", itemNodeOnMain.getValueAsString(
        SampleContainerItem.USER_URI, User.NAME));
    assertEquals("user", itemNodeOnBranch.getValueAsString(
        SampleContainerItem.USER_URI, User.NAME));

  }


  @Test
  protected void testSubjects() {

    // Contructs a sample category hierarchy and the users included by the category defined by the
    // sub categories and theircontainer object.
    ObjectNode rootNode = objectApi.create(SCHEMA_ASPECTS, new SampleCategory().name("Root"));

    URI admin = orgApi.saveGroup(new Group().builtIn(true).name("admin"));
    URI normal = orgApi.saveGroup(new Group().builtIn(true).name("normal"));
    URI superGroup = orgApi.saveGroup(new Group().builtIn(true).name("super"));
    orgApi.addChildGroup(orgApi.getGroup(superGroup), orgApi.getGroup(admin));
    orgApi.addChildGroup(orgApi.getGroup(superGroup), orgApi.getGroup(normal));

    URI rootAdmin = createUser("root admin", "root admin", admin);
    URI superUserUri = createUser("super user", "super user", superGroup);

    URI rootUri = objectApi.save(rootNode);

    StoredMap map = collectionApi.map(SCHEMA_ASPECTS, USER_CATEGORY);
    map.put(objectApi.getLatestUri(rootAdmin).toString(), rootUri);

    {
      List<Subject> subjectsOfUser =
          subjectManagementApi.getSubjectsOfUser(ObjectApiTestConfigBase.SAMPLE_SUBJECT_MODEL,
              rootAdmin);

      org.assertj.core.api.Assertions.assertThat(subjectsOfUser.stream().map(s -> s.getRef()))
          .containsExactlyInAnyOrder(admin, rootAdmin, rootUri);
    }

    {
      List<Subject> subjectsOfUser =
          subjectManagementApi.getAllSubjects(ObjectApiTestConfigBase.SAMPLE_SUBJECT_MODEL,
              Arrays.asList(new Subject().ref(superGroup).type(Group.class.getName())));

      org.assertj.core.api.Assertions.assertThat(subjectsOfUser.stream().map(s -> s.getRef()))
          .containsExactlyInAnyOrder(admin, normal, superGroup);
    }

    List<URI> objectsToEval = new ArrayList<>();

    List<Subject> allSubjects =
        subjectManagementApi.getAllSubjects(ObjectApiTestConfigBase.SAMPLE_SUBJECT_MODEL);

    {
      ObjectNode categoryNode =
          objectApi.create(SCHEMA_ASPECTS, new SampleCategory().name("My Category 1"));
      categoryNode.aspects().modify(
          AccessControlInternalApi.ACL_ASPECT, ACL.class,
          acl -> new ACL().rootEntry(new ACLEntry().addEntriesItem(
              new ACLEntry().subject(getSubject(allSubjects, admin)).addOperationsItem("read"))
              .addEntriesItem(new ACLEntry().subject(getSubject(allSubjects, normal))
                  .addOperationsItem("write"))));
      objectsToEval.add(objectApi.save(categoryNode));
    }
    {
      ObjectNode categoryNode =
          objectApi.create(SCHEMA_ASPECTS, new SampleCategory().name("My Category 2"));
      categoryNode.aspects().modify(
          AccessControlInternalApi.ACL_ASPECT, ACL.class,
          acl -> new ACL().rootEntry(new ACLEntry().addEntriesItem(
              new ACLEntry().subject(getSubject(allSubjects, normal)).addOperationsItem("read"))
              .addEntriesItem(new ACLEntry().subject(getSubject(allSubjects, rootAdmin))
                  .addOperationsItem("write"))));
      objectsToEval.add(objectApi.save(categoryNode));
    }
    {
      ObjectNode categoryNode =
          objectApi.create(SCHEMA_ASPECTS, new SampleCategory().name("My Category 3"));
      objectsToEval.add(objectApi.save(categoryNode));
    }
    {
      Subject superGroupSubject = getSubject(allSubjects, superGroup);
      Subject superUserSubject = getSubject(allSubjects, superUserUri);

      ObjectNode categoryNode =
          objectApi.create(SCHEMA_ASPECTS, new SampleCategory().name("My Category 4"));
      URI uri = objectApi.save(categoryNode);
      categoryNode = objectApi.loadLatest(uri);
      categoryNode.aspects().modify(
          AccessControlInternalApi.ACL_ASPECT, ACL.class,
          acl -> {
            acl = new ACL().rootEntry(new ACLEntry());
            {
              List<ACLSubject> subjects = new ArrayList<>();
              subjects.add(new ACLSubject().subject(superGroupSubject)
                  .operation(new ACLOperation().name("read")));
              acl = accessControlInternalApi.applySubjects(acl, subjects,
                  "read", objectApi.getLatestUri(uri),
                  SubscriptionConfigContributionTestApi.READ_WRITE_CATEGORY);
            }
            {
              List<ACLSubject> subjects = new ArrayList<>();
              subjects.add(new ACLSubject().subject(superGroupSubject)
                  .operation(new ACLOperation().name("write")));
              subjects.add(new ACLSubject().subject(superUserSubject)
                  .operation(new ACLOperation().name("read-write")));
              acl = accessControlInternalApi.applySubjects(acl, subjects,
                  "write", objectApi.getLatestUri(uri),
                  SubscriptionConfigContributionTestApi.READ_WRITE_CATEGORY);
            }
            return acl;
            // new ACL().rootEntry(new ACLEntry().addEntriesItem(
            // new ACLEntry().subject(getSubject(allSubjects, superGroup))
            // .addOperationsItem("read")
            // .addOperationsItem("write")));
          });
      objectsToEval.add(objectApi.save(categoryNode));

      List<String> models = new ArrayList<>();
      models.add(ObjectApiTestConfigBase.SAMPLE_SUBJECT_MODEL);
      List<ACLSubjectOperations> userAllOperations =
          accessControlInternalApi.getUserAllOperations(superUserUri, models);
      org.assertj.core.api.Assertions.assertThat(userAllOperations).hasSize(2);

      List<ACLSubjectSubscription> userAllSubscriptions =
          accessControlInternalApi.getUserAllSubscriptions(superUserUri, models);
      org.assertj.core.api.Assertions.assertThat(userAllSubscriptions).hasSize(3);

      TableData<?> tableData = collectionApi.searchIndex(OrgApiStorageImpl.ORG_SCHEME,
          SubscriptionConfigApi.SEARCH_USER_SUBSCRIPTION, ACLSubjectSubscription.class)
          .executeSearchOnNodes(
              userAllSubscriptions.stream().map(o -> objectApi.create(null, o)), null);

      System.out.println(TableDatas.toStringAdv(tableData));

    }
    List<String> operations = Arrays.asList("read", "write");
    {
      Map<String, Set<String>> operationsByCategory =
          objectsToEval.stream().map(u -> objectApi.loadLatest(u))
              .collect(toMap(n -> n.getValueAsString(SampleCategory.NAME),
                  n -> accessControlInternalApi.getAvailableOperationsOn(rootAdmin, n, operations,
                      ObjectApiTestConfigBase.SAMPLE_SUBJECT_MODEL)));
      org.assertj.core.api.Assertions.assertThat(operationsByCategory)
          .containsEntry("My Category 1", new HashSet<>(Arrays.asList("read")))
          .containsEntry("My Category 2", new HashSet<>(Arrays.asList("write")))
          .containsEntry("My Category 3", new HashSet<>(Arrays.asList("read", "write")))
          .containsEntry("My Category 4", new HashSet<>());
    }

    {

      Map<String, Set<String>> operationsByCategory =
          objectsToEval.stream().map(u -> objectApi.loadLatest(u))
              .collect(toMap(n -> n.getValueAsString(SampleCategory.NAME),
                  n -> accessControlInternalApi.getAvailableOperationsOn(superUserUri, n,
                      operations,
                      ObjectApiTestConfigBase.SAMPLE_SUBJECT_MODEL)));
      org.assertj.core.api.Assertions.assertThat(operationsByCategory)
          .containsEntry("My Category 1", new HashSet<>(Arrays.asList("read", "write")))
          .containsEntry("My Category 2", new HashSet<>(Arrays.asList("read")))
          .containsEntry("My Category 3", new HashSet<>(Arrays.asList("read", "write")))
          .containsEntry("My Category 4", new HashSet<>(Arrays.asList("read", "write")));
    }

  }

  @Test
  void testObjectPropertyFormat() {
    ObjectNode rootNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("Root").singleLink(new SampleLinkObject().linkName("link")));
    URI uri = objectApi.save(rootNode);
    ObjectNode loadedNode = objectApi.load(uri);
    assertEquals(
        "Root",
        loadedNode.getValueAsString(SampleCategory.NAME));
    assertEquals(
        "link",
        loadedNode.getValueAsString(SampleCategory.SINGLE_LINK, SampleLinkObject.LINK_NAME));
    ObjectPropertyResolver resolver = objectApi.resolver();
    resolver.addContextObject("object", uri);
    String resolve = resolver.resolve(new ObjectPropertyFormatter().formatString("{0} ({1})")
        .addParametersItem(
            new ObjectPropertyFormatterParameter().propertyUri(URI.create("object:/#name")))
        .addParametersItem(new ObjectPropertyFormatterParameter()
            .propertyUri(URI.create("object:/singleLink#linkName"))));

    Assertions.assertEquals("Root (link)", resolve);
  }

  @Test
  void testObjectPropertyMapperSameObject() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("From category").color(ColorEnum.BLACK)
            .singleLink(new SampleLinkObject().linkName("from link")));
    URI uriFrom = objectApi.save(fromNode);
    ObjectNode toNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("To category").color(ColorEnum.GREEN)
            .singleLink(new SampleLinkObject().linkName("to link")));
    URI uriTo = objectApi.save(toNode);

    fromNode = objectApi.loadLatest(uriFrom);
    toNode = objectApi.loadLatest(uriTo);

    ObjectMapping mapper = objectApi.mapper()
        .mapping(new ObjectMappingDefinition()
            .fromTypeQualifiedName(fromNode.getDefinition().getQualifiedName())
            .toTypeQualifiedName(toNode.getDefinition().getQualifiedName())
            .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.NAME)
                .addToPathItem(SampleCategory.NAME))
            .addMappingsItem(
                new ObjectPropertyMapping().addFromPathItem(SampleCategory.SINGLE_LINK)
                    .addFromPathItem(SampleLinkObject.LINK_NAME)
                    .addToPathItem(SampleCategory.SINGLE_LINK)
                    .addToPathItem(SampleLinkObject.LINK_NAME)));

    Map<String, Object> expectedResult = new HashMap<>(fromNode.getObjectAsMap());
    expectedResult.put(SampleCategory.COLOR, ColorEnum.GREEN.toString());
    expectedResult.remove(SampleCategory.URI);

    Map<String, Object> result =
        mapper.copyAllValues(fromNode.getObjectAsMap(), toNode.getObjectAsMap());

    org.assertj.core.api.Assertions.assertThat(result)
        .containsAllEntriesOf(expectedResult);

  }

  @Test
  void testObjectMapperToSingleObject() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("From category").color(ColorEnum.BLACK)
            .singleLink(new SampleLinkObject().linkName("from link")));
    URI uriFrom = objectApi.save(fromNode);
    ObjectNode toNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("To category").color(ColorEnum.GREEN)
            .singleLink(new SampleLinkObject().linkName("to link")));
    URI uriTo = objectApi.save(toNode);

    fromNode = objectApi.loadLatest(uriFrom);
    toNode = objectApi.loadLatest(uriTo);

    ObjectMapping mapper = objectApi.mapper()
        .mapping(new ObjectMappingDefinition()
            .fromTypeQualifiedName(fromNode.getDefinition().getQualifiedName())
            .toTypeQualifiedName(toNode.getDefinition().getQualifiedName())
            .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.NAME)
                .addToPathItem(SampleCategory.NAME))
            .addMappingsItem(
                new ObjectPropertyMapping().addFromPathItem(SampleCategory.SINGLE_LINK)
                    .addFromPathItem(SampleLinkObject.LINK_NAME)
                    .addToPathItem(SampleCategory.SINGLE_LINK)
                    .addToPathItem(SampleLinkObject.LINK_NAME)))
        .set(fromNode);

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put(SampleCategory.NAME, fromNode.getValue(SampleCategory.NAME));
    expectedResult.put(SampleCategory.SINGLE_LINK, Map.of(SampleLinkObject.LINK_NAME,
        fromNode.getValue(SampleCategory.SINGLE_LINK, SampleLinkObject.LINK_NAME)));

    Map<String, Object> result =
        (Map<String, Object>) mapper.execute();

    org.assertj.core.api.Assertions.assertThat(result)
        .containsAllEntriesOf(expectedResult);

  }

  @Test
  void testObjectMapperToSingleValue() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("From category").color(ColorEnum.BLACK)
            .singleLink(new SampleLinkObject().linkName("from link")).cost(12l)
            .addKeyWordsItem("keyword1").addKeyWordsItem("keyword2"));
    URI uriFrom = objectApi.save(fromNode);

    fromNode = objectApi.loadLatest(uriFrom);

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.COST)))
          .set(fromNode).execute();
      org.assertj.core.api.Assertions.assertThat(value).isInstanceOf(Integer.class)
          .isEqualTo(Integer.valueOf(12));
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.COST)
                  .typeClass(Long.class.getName())))
          .set(fromNode).execute();
      org.assertj.core.api.Assertions.assertThat(value).isInstanceOf(Long.class).isEqualTo(12l);
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(
                  new ObjectPropertyMapping().addFromPathItem(SampleCategory.KEY_WORDS)))
          .set(fromNode).execute();
      org.assertj.core.api.Assertions.assertThat((List) value).containsExactlyInAnyOrder("keyword1",
          "keyword2");
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(
                  new ObjectPropertyMapping().addFromPathItem(SampleCategory.KEY_WORDS)
                      .iterationDefinition(new ObjectMappingDefinition()
                          .addMappingsItem(new ObjectPropertyMapping().expression("'apple'")))))
          .set(fromNode).execute();
      org.assertj.core.api.Assertions.assertThat((List) value).containsExactlyInAnyOrder("apple",
          "apple");
    }

  }

  @Test
  void testObjectMapperFromMapToSingleValue() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("From category").color(ColorEnum.BLACK)
            .singleLink(new SampleLinkObject().linkName("from link")).cost(12l)
            .addKeyWordsItem("keyword1").addKeyWordsItem("keyword2"));
    URI uriFrom = objectApi.save(fromNode);
    fromNode = objectApi.loadLatest(uriFrom);

    Map<String, Object> objectAsMap = fromNode.getObjectAsMap();

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.COST)))
          .set(objectAsMap).execute();
      org.assertj.core.api.Assertions.assertThat(value).isInstanceOf(Integer.class)
          .isEqualTo(Integer.valueOf(12));
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.COST)
                  .typeClass(Long.class.getName())))
          .set(objectAsMap).execute();
      org.assertj.core.api.Assertions.assertThat(value).isInstanceOf(Long.class).isEqualTo(12l);
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(
                  new ObjectPropertyMapping().addFromPathItem(SampleCategory.KEY_WORDS)))
          .set(objectAsMap).execute();
      org.assertj.core.api.Assertions.assertThat((List) value).containsExactlyInAnyOrder("keyword1",
          "keyword2");
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(
                  new ObjectPropertyMapping().addFromPathItem(SampleCategory.KEY_WORDS)
                      .iterationDefinition(new ObjectMappingDefinition()
                          .addMappingsItem(new ObjectPropertyMapping().expression("'apple'")))))
          .set(objectAsMap).execute();
      org.assertj.core.api.Assertions.assertThat((List) value).containsExactlyInAnyOrder("apple",
          "apple");
    }

  }

  @Test
  void testObjectMapperFromContextToSingleValue() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("From category").color(ColorEnum.BLACK)
            .singleLink(new SampleLinkObject().linkName("from link")).cost(12l)
            .addKeyWordsItem("keyword1").addKeyWordsItem("keyword2"));
    URI uriFrom = objectApi.save(fromNode);
    fromNode = objectApi.loadLatest(uriFrom);

    Map<String, Object> objectAsMap = fromNode.getObjectAsMap();

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(new ObjectPropertyMapping().addFromPathItem("obj")
                  .addFromPathItem(SampleCategory.COST)))
          .set("obj", fromNode).execute();
      org.assertj.core.api.Assertions.assertThat(value).isInstanceOf(Integer.class)
          .isEqualTo(Integer.valueOf(12));
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(new ObjectPropertyMapping().addFromPathItem("obj")
                  .addFromPathItem(SampleCategory.COST)
                  .typeClass(Long.class.getName())))
          .set("obj", objectAsMap).execute();
      org.assertj.core.api.Assertions.assertThat(value).isInstanceOf(Long.class).isEqualTo(12l);
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(
                  new ObjectPropertyMapping().addFromPathItem("obj")
                      .addFromPathItem(SampleCategory.KEY_WORDS)))
          .set("obj", fromNode).execute();
      org.assertj.core.api.Assertions.assertThat((List) value).containsExactlyInAnyOrder("keyword1",
          "keyword2");
    }

    {
      Object value = objectApi.mapper()
          .mapping(new ObjectMappingDefinition()
              .addMappingsItem(
                  new ObjectPropertyMapping().addFromPathItem("obj")
                      .addFromPathItem(SampleCategory.KEY_WORDS)
                      .iterationDefinition(new ObjectMappingDefinition()
                          .addMappingsItem(new ObjectPropertyMapping()
                              .expression("#obj['name'] + ' ' + #listItem")))))
          .set("obj", objectAsMap).execute();
      org.assertj.core.api.Assertions.assertThat((List) value).containsExactlyInAnyOrder(
          "From category keyword1",
          "From category keyword2");
    }

  }

  @Test
  void testObjectPropertyMapperDifferentObject() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("From category").cost(Long.valueOf(100)).color(ColorEnum.BLACK)
            .singleLink(new SampleLinkObject().linkName("from link")));
    URI uriFrom = objectApi.save(fromNode);
    ObjectNode toNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategoryType().name("name").description("description"));
    URI uriTo = objectApi.save(toNode);

    fromNode = objectApi.loadLatest(uriFrom);
    toNode = objectApi.loadLatest(uriTo);

    ObjectMapping mapper = objectApi.mapper()
        .mapping(new ObjectMappingDefinition()
            .fromTypeQualifiedName(fromNode.getDefinition().getQualifiedName())
            .toTypeQualifiedName(toNode.getDefinition().getQualifiedName())
            .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.NAME)
                .addToPathItem(SampleCategoryType.NAME))
            .addMappingsItem(
                new ObjectPropertyMapping().addFromPathItem(SampleCategory.SINGLE_LINK)
                    .addFromPathItem(SampleLinkObject.LINK_NAME)
                    .addToPathItem(SampleCategoryType.DESCRIPTION))
            .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategory.COST)
                .addToPathItem(SampleCategoryType.CODE)));

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put(SampleCategoryType.NAME, "From category");
    expectedResult.put(SampleCategoryType.DESCRIPTION, "from link");
    expectedResult.put(SampleCategoryType.CODE, Integer.valueOf(100));

    Map<String, Object> result =
        mapper.copyAllValues(fromNode.getObjectAsMap(), toNode.getObjectAsMap());

    org.assertj.core.api.Assertions.assertThat(result)
        .containsAllEntriesOf(expectedResult);

  }

  @SuppressWarnings("unchecked")
  @Test
  void testObjectPropertyMapperWithPrimitiveList() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategoryType().name("from name").description("from description")
            .code("from code"));
    URI uriFrom = objectApi.save(fromNode);
    ObjectNode toNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("category"));
    URI uriTo = objectApi.save(toNode);

    fromNode = objectApi.loadLatest(uriFrom);
    toNode = objectApi.loadLatest(uriTo);

    ObjectMapping mapper = objectApi.mapper()
        .mapping(new ObjectMappingDefinition()
            .fromTypeQualifiedName(fromNode.getDefinition().getQualifiedName())
            .toTypeQualifiedName(toNode.getDefinition().getQualifiedName())
            .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategoryType.NAME)
                .addToPathItem(SampleCategory.NAME))
            .addListMappingsItem(new ObjectListMapping().addToListPathItem(SampleCategory.KEY_WORDS)
                .addFromPrimitivePathItem(SampleCategoryType.CODE))
            .addListMappingsItem(new ObjectListMapping().addToListPathItem(SampleCategory.KEY_WORDS)
                .addFromPrimitivePathItem(SampleCategoryType.NAME)));

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put(SampleCategoryType.NAME, "from name");

    Map<String, Object> result =
        mapper.copyAllValues(fromNode.getObjectAsMap(), toNode.getObjectAsMap());

    org.assertj.core.api.Assertions.assertThat(result)
        .containsAllEntriesOf(expectedResult);

    org.assertj.core.api.Assertions.assertThat((List<String>) result.get(SampleCategory.KEY_WORDS))
        .contains("from name", "from code");
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Test
  void testObjectPropertyMapperWithObjectList() {
    ObjectNode fromNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategoryType().name("from name").description("from description")
            .code("from code"));
    URI uriFrom = objectApi.save(fromNode);
    ObjectNode toNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleExtensibleObject().name("..."));
    URI uriTo = objectApi.save(toNode);

    fromNode = objectApi.loadLatest(uriFrom);
    toNode = objectApi.loadLatest(uriTo);

    ObjectMapping mapper = objectApi.mapper()
        .mapping(new ObjectMappingDefinition()
            .fromTypeQualifiedName(fromNode.getDefinition().getQualifiedName())
            .toTypeQualifiedName(toNode.getDefinition().getQualifiedName())
            .addMappingsItem(new ObjectPropertyMapping().addFromPathItem(SampleCategoryType.NAME)
                .addToPathItem(SampleExtensibleObject.NAME))
            .addListMappingsItem(new ObjectListMapping()
                .addToListPathItem(SampleExtensibleObject.LINKS)
                .objectMapping(new ObjectMappingDefinition().addMappingsItem(
                    new ObjectPropertyMapping().addFromPathItem(SampleCategoryType.CODE)
                        .addToPathItem(SampleLinkObject.ITEM))))
            .addListMappingsItem(new ObjectListMapping()
                .addToListPathItem(SampleExtensibleObject.LINKS)
                .objectMapping(new ObjectMappingDefinition().addMappingsItem(
                    new ObjectPropertyMapping().addFromPathItem(SampleCategoryType.NAME)
                        .addToPathItem(SampleLinkObject.ITEM)))));

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put(SampleCategoryType.NAME, "from name");

    Map<String, Object> result =
        mapper.copyAllValues(fromNode.getObjectAsMap(), toNode.getObjectAsMap());

    org.assertj.core.api.Assertions.assertThat(result)
        .containsAllEntriesOf(expectedResult);

    org.assertj.core.api.Assertions
        .assertThat(((List) result.get(SampleExtensibleObject.LINKS)).stream()
            .map(o -> ((Map) o).get(SampleLinkObject.ITEM)))
        .contains("from name", "from code");
  }

  @Test
  void testObjectContextMapping() {
    ObjectNode targetNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("from name"));
    URI uriTarget = objectApi.save(targetNode);
    ObjectNode typeNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategoryType().name("type name"));

    targetNode = objectApi.loadLatest(uriTarget);

    ContextMappingDefinition contextMappingDefinition =
        new ContextMappingDefinition().addItemsItem(new ContextMappingItem()
            .addOutputPathItem("target").addOutputPathItem(SampleCategory.NAME)
            .valueMapping(new ObjectMappingDefinition()
                .addMappingsItem(new ObjectPropertyMapping()
                    .expression("#map1['prop1'] + ' - ' + #categoryType['name']"))));
    try {
      System.out
          .println(objectApi.getDefaultSerializer().writeValueAsString(contextMappingDefinition));
    } catch (JsonProcessingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    ContextMapping contextMapping = objectApi.contextMapper()
        .mapping(contextMappingDefinition);

    contextMapping.from().set("map1", Map.of("prop1", "map1.prop1")).set("categoryType", typeNode);
    contextMapping.to().set("target", targetNode);

    contextMapping.execute();

    org.assertj.core.api.Assertions.assertThat(targetNode.getValueAsString(SampleCategory.NAME))
        .isEqualTo("map1.prop1 - type name");

  }

  @Test
  void testObjectNodeListSort() {
    ObjectNode rootNode = objectApi.create(SCHEMA_ASPECTS,
        new SampleCategory().name("Root to sort"));
    ObjectNodeList list = rootNode.list(SampleCategory.SUB_CATEGORIES);
    list.addNewObject(new SampleCategory().name("Peach").cost(Long.valueOf(6)));
    list.addNewObject(new SampleCategory().name("Apple").cost(Long.valueOf(7)));
    list.addNewObject(new SampleCategory().name("Wallnut").cost(Long.valueOf(9)));
    list.addNewObject(new SampleCategory().name("Pear").cost(Long.valueOf(1)));
    list.addNewObject(new SampleCategory().name("Peach").cost(Long.valueOf(0)));

    list.sort((o1, o2) -> {
      Long v1 = o1.getValue(Long.class, SampleCategory.COST);
      Long v2 = o2.getValue(Long.class, SampleCategory.COST);
      return Long.compare(v1, v2);
    });

    URI uri = objectApi.save(rootNode);

    ObjectNode rootLoaded = objectApi.load(uri);

    ObjectNodeList subCategoryList = rootLoaded.list(SampleCategory.SUB_CATEGORIES);
    org.assertj.core.api.Assertions.assertThat(subCategoryList
        .nodes().stream().map(n -> n.getValueAsString(SampleCategory.NAME)))
        .containsExactly("Peach", "Pear", "Peach", "Apple", "Wallnut");

    subCategoryList.sort((o1, o2) -> {
      String v1 = o1.getValueAsString(SampleCategory.NAME);
      String v2 = o2.getValueAsString(SampleCategory.NAME);
      return v1.compareTo(v2);
    });

    org.assertj.core.api.Assertions.assertThat(subCategoryList
        .nodeStream().map(n -> n.getValueAsString(SampleCategory.NAME)))
        .containsExactly("Apple", "Peach", "Peach", "Pear", "Wallnut");

  }

  @Test
  void testSaveWithId() throws IOException {

    List<Tuple> ids = new ArrayList<>();
    int size = 10;
    String[] idStrings = new String[size];
    for (int i = 0; i < size; i++) {
      String id = UUID.randomUUID().toString().replace(StringConstant.HYPHEN, StringConstant.EMPTY);

      idStrings[i] = Integer.toString(i);
      ObjectNode node = objectApi.create(SCHEMA_ASPECTS,
          new SamplePropertyContainerWithId().id(id)
              .props(new SampleProperties().primary(idStrings[i])));
      URI uri = objectApi.save(node);
      ids.add(Tuple.tuple(id, uri));
    }

    Storage storage = storageApi.get(SCHEMA_ASPECTS);

    ObjectDefinition<SamplePropertyContainerWithId> definition =
        objectDefinitionApi.definition(SamplePropertyContainerWithId.class);

    org.assertj.core.api.Assertions.assertThat(ids)
        .allMatch(t -> (storage
            .constructUriForId(definition,
                (String) t.toArray()[0])
            + ".v0")
                .equals(t.toArray()[1].toString()));

    List<ObjectNode> results = ids.stream()
        .map(t -> objectApi.loadLatest(SCHEMA_ASPECTS, definition, t.toArray()[0].toString()))
        .collect(toList());

    org.assertj.core.api.Assertions
        .assertThat(results.stream()
            .map(node -> node.getValueAsString(SamplePropertyContainerWithId.PROPS,
                SampleProperties.PRIMARY)))
        .containsExactly(idStrings);

  }

  @Test
  @DisplayName("Updating nested value on a path already carrying a typesafe modification succeeds.")
  void updatingNodeOnPathWithTypeSafeObject_thenSettingValueOnFurtherNestedPath_succeeds() {
    final ObjectNode node = objectApi.create("foo", new SampleStandaloneObject());
    node.setValue(new SamplePropertyContainer().name("Incorrect Name"),
        SampleStandaloneObject.PROPERTY_CONTAINER);
    org.junit.jupiter.api.Assertions.assertDoesNotThrow(
        () -> node.setValue("My Name",
            SampleStandaloneObject.PROPERTY_CONTAINER,
            SamplePropertyContainer.NAME),
        "Failed to set 'My Name' to node.propertyContainer.name!");

    assertThat(node.getValueAsString(
        SampleStandaloneObject.PROPERTY_CONTAINER,
        SamplePropertyContainer.NAME))
            .isEqualTo("My Name");
  }

  private final Subject getSubject(List<Subject> subjects, URI uri) {
    return subjects.stream().filter(s -> objectApi.getLatestUri(uri).equals(s.getRef())).findFirst()
        .get();
  }

  private URI createUser(String username, String fullname, URI... group) {
    URI uri = orgApi.saveUser(new User().username(username)
        .password(PASSWDCODE)
        .name(fullname));
    Arrays.asList(group).stream()
        .forEach(g -> orgApi.addUserToGroup(
            uri,
            g));
    return uri;
  }

  @Test
  void loadBatchVersionedUris() {
    URI uri0 = objectApi.saveAsNew(SCHEMA_ASPECTS, new SampleCategory().name("Root0"));
    ObjectNode node = objectApi.load(uri0);
    node.setValue("Root1", SampleCategory.NAME);
    URI uri1 = objectApi.save(node);
    node = objectApi.load(uri1);
    node.setValue("Root2", SampleCategory.NAME);
    URI uri2 = objectApi.save(node);
    URI latestUri = objectApi.getLatestUri(uri0);

    objectApi.enableReadCache();
    ObjectNode node0 = objectApi.load(uri0);
    ObjectNode nodeLatest = objectApi.loadLatest(uri0);
    assertEquals(uri0, node0.getObjectUri());
    assertEquals(uri2, nodeLatest.getObjectUri());
    node0 = objectApi.load(uri0);
    nodeLatest = objectApi.loadLatest(uri0);
    assertEquals(uri0, node0.getObjectUri());
    assertEquals(uri2, nodeLatest.getObjectUri());
    nodeLatest = objectApi.loadLatest(uri1);
    ObjectNode node1 = objectApi.load(uri1);
    assertEquals(uri1, node1.getObjectUri());
    assertEquals(uri2, nodeLatest.getObjectUri());
    nodeLatest = objectApi.load(latestUri);
    assertEquals(uri2, nodeLatest.getObjectUri());
    objectApi.disableReadCache();

    objectApi.enableReadCache();
    List<ObjectNode> node0List = objectApi.loadBatch(List.of(uri0));
    assertNotNull(node0List);
    assertEquals(1, node0List.size());
    node0 = node0List.get(0);
    List<ObjectNode> nodeLatestList = objectApi.loadLatestBatch(List.of(uri0));
    assertNotNull(nodeLatestList);
    assertEquals(1, nodeLatestList.size());
    nodeLatest = nodeLatestList.get(0);
    assertEquals(uri0, node0.getObjectUri());
    assertEquals(uri2, nodeLatest.getObjectUri());
    objectApi.disableReadCache();

    objectApi.enableReadCache();
    assertTrue(objectApi.isReadCacheEnabled());

    // read several times to ensure cache is working
    List<ObjectNode> nodes;
    for (int i = 0; i < 3; i++) {
      nodes = objectApi.loadBatch(Arrays.asList(uri0, uri1, uri2));
      assertTrue(objectApi.isReadCacheEnabled());
      assertNotNull(nodes);
      assertEquals(3, nodes.size());
      assertEquals("Root0", nodes.get(0).getValueAsString(SampleCategory.NAME));
      assertEquals("Root1", nodes.get(1).getValueAsString(SampleCategory.NAME));
      assertEquals("Root2", nodes.get(2).getValueAsString(SampleCategory.NAME));
    }

    URI uriLatest = objectApi.getLatestUri(uri2);

    URI uriOther = objectApi.saveAsNew(SCHEMA_ASPECTS, new SampleCategory().name("RootOther"));
    assertFalse(objectApi.isReadCacheEnabled());
    objectApi.enableReadCache();
    objectApi.enableReadCache();
    objectApi.enableReadCache();
    assertTrue(objectApi.isReadCacheEnabled());
    nodes = objectApi
        .loadBatch(Arrays.asList(uri0, uri1, uri2, uriLatest, uri1, uri0, uriLatest, uriOther));

    assertTrue(objectApi.isReadCacheEnabled());
    objectApi.disableReadCache();

    assertNotNull(nodes);
    assertEquals(8, nodes.size());
    assertEquals("Root0", nodes.get(0).getValueAsString(SampleCategory.NAME));
    assertEquals("Root1", nodes.get(1).getValueAsString(SampleCategory.NAME));
    assertEquals("Root2", nodes.get(2).getValueAsString(SampleCategory.NAME));

    nodes = objectApi
        .loadBatch(Arrays.asList(uri0, uri1, uri2, uriLatest, uri1, uri0, uriLatest, uriOther));

    assertTrue(objectApi.isReadCacheEnabled());
    objectApi.disableReadCache();
    assertTrue(objectApi.isReadCacheEnabled());
    objectApi.disableReadCache();
    assertFalse(objectApi.isReadCacheEnabled());

    assertNotNull(nodes);
    assertEquals(8, nodes.size());
    assertEquals("Root0", nodes.get(0).getValueAsString(SampleCategory.NAME));
    assertEquals("Root1", nodes.get(1).getValueAsString(SampleCategory.NAME));
    assertEquals("Root2", nodes.get(2).getValueAsString(SampleCategory.NAME));
    assertEquals("Root2", nodes.get(3).getValueAsString(SampleCategory.NAME));
    assertEquals("Root1", nodes.get(4).getValueAsString(SampleCategory.NAME));
    assertEquals("Root0", nodes.get(5).getValueAsString(SampleCategory.NAME));
    assertEquals("Root2", nodes.get(6).getValueAsString(SampleCategory.NAME));
    assertEquals("RootOther", nodes.get(7).getValueAsString(SampleCategory.NAME));


    if (checkPhysicalId) {
      // Assert that all the different nodes has different physical object id. The 8 loaded version
      // blongs to 2 different object. So we must have two physical object id.
      assertThat(nodes.stream().map(n -> n.getPhysicalObjectId()).collect(toSet()))
          .hasSize(2);
    }
  }

  @Test
  @ExtendWith(OutputCaptureExtension.class)
  void testConsecutiveReadsOfSameObjectWhileReadCacheIsEnabled_yieldsConsistentResultsEvenIfNodeIsModifiedInMemory(
      final CapturedOutput output) {
    final URI uri = objectApi.saveAsNew("test", new SampleCategory().name("foo"));
    final URI latestUri = objectApi.getLatestUri(uri);

    objectApi.enableReadCache();
    {
      final ObjectNode node = objectApi.loadLatest(latestUri);
      assertThat(node).returns("foo", it -> it.getValueAsString(SampleCategory.NAME));

      // FIXME: Ideally this would incur a forced shutdown of the readCache:
      node.setValue("bar", SampleCategory.NAME);
      assertThat(node).returns("bar", it -> it.getValueAsString(SampleCategory.NAME));
    }
    {
      // FIXME: If ReadCache treated this as unacceptable: this would be the latest point where an
      // exception could be thrown.
      final ObjectNode node = objectApi.loadLatest(latestUri);
      assertThat(node).returns("foo", it -> it.getValueAsString(SampleCategory.NAME));
    }

    assertThat(objectApi.isReadCacheEnabled()).isTrue();
    objectApi.disableReadCache();
    assertThat(output).contains(
        "Poisoned cache entry: CacheKey[uri=test:/org_smartbit4all_api_sample_bean_SampleCategory");
  }

  @Test
  void testManyVersionsFromAnObject() {
    URI objectUri = objectApi.saveAsNew("test",
        new SampleCategory().name("Root").cost(Long.valueOf(0)));

    for (int i = 0; i < 511; i++) {
      ObjectNode node = objectApi.loadLatest(objectUri);
      Long cost = node.getValue(Long.class, SampleCategory.COST);
      node.setValue(cost++, SampleCategory.COST);
      objectApi.save(node);
    }
  }


}
