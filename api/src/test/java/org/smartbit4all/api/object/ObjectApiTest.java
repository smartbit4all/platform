package org.smartbit4all.api.object;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
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
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.object.ObjectPropertyMapper;
import org.smartbit4all.core.object.ObjectPropertyResolver;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.util.stream.Collectors.toMap;

@SpringBootTest(classes = {ObjectApiTestConfig.class})
class ObjectApiTest {

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
  private CollectionApi collectionApi;

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  private SubjectManagementApi subjectManagementApi;

  @Autowired
  private AccessControlInternalApi accessControlInternalApi;

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
    ObjectDefinition<BinaryContent> definition = objectApi.definition(BinaryContent.class);
    assertEquals(BinaryContent.class.getName().replace('.', '_'),
        definition.getAlias());
    assertEquals(ObjectMapper.class.getName(), definition.getDefaultSerializer().getName());

    BinaryContent myBean = new BinaryContent();

    myBean.setExtension("txt");
    myBean.setFileName("árvíztűrőtükörfúrógép.txt");
    myBean.setMimeType("application/text");
    myBean.setSize(Long.valueOf(1024));

    URI uri = URI.create("scheme:/path#fragment");
    definition.setUri(myBean, uri);

    BinaryData binaryData =
        definition.getDefaultSerializer().serialize(myBean, BinaryContent.class);

    Optional<BinaryContent> deserializeResult =
        definition.getDefaultSerializer().deserialize(binaryData, BinaryContent.class);

    Assertions.assertTrue(deserializeResult.isPresent());

    BinaryContent reloadedBean = deserializeResult.get();

    assertEquals(myBean.getFileName(), reloadedBean.getFileName());
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
      org.assertj.core.api.Assertions.assertThat(rootNode.aspects().get()).isNull();
      rootNode.aspects().modify("ACL", ACL.class,
          acl -> new ACL().rootEntry(
              new ACLEntry().addEntriesItem(new ACLEntry().subject(new Subject().ref(everybodyUri))
                  .addOperationsItem("read").addOperationsItem("write"))));
      objectApi.save(rootNode);
    }
    {
      // Now read the ACL again.
      ObjectNode rootNode = objectApi.loadLatest(rootUri);
      org.assertj.core.api.Assertions.assertThat(rootNode.aspects().get()).isNotNull();
      ACL acl = rootNode.aspects().get("ACL", ACL.class);
      org.assertj.core.api.Assertions
          .assertThat(acl.getRootEntry().getEntries().stream().map(e -> e.getSubject().getRef()))
          .contains(everybodyUri);
    }

  }

  @Test
  void testSubjects() {

    // Contructs a sample category hierarchy and the users included by the category defined by the
    // sub categories and theircontainer object.
    ObjectNode rootNode = objectApi.create(SCHEMA_ASPECTS, new SampleCategory().name("Root"));

    List<URI> admins = new ArrayList<>();
    List<URI> normals = new ArrayList<>();

    URI admin = orgApi.saveGroup(new Group().builtIn(true).name("admin"));
    URI normal = orgApi.saveGroup(new Group().builtIn(true).name("normal"));
    URI superGroup = orgApi.saveGroup(new Group().builtIn(true).name("super"));
    orgApi.addChildGroup(orgApi.getGroup(superGroup), orgApi.getGroup(admin));
    orgApi.addChildGroup(orgApi.getGroup(superGroup), orgApi.getGroup(normal));

    URI normalUserUri = createUser("normal user", "Normal Norman", normal);
    URI rootAdmin = createUser("root admin", "root admin", admin);
    URI superUserUri = createUser("super user", "super user", superGroup);

    URI rootUri = objectApi.save(rootNode);

    StoredMap map = collectionApi.map(SCHEMA_ASPECTS, USER_CATEGORY);
    map.put(objectApi.getLatestUri(rootAdmin).toString(), rootUri);

    {
      List<Subject> subjectsOfUser =
          subjectManagementApi.getSubjectsOfUser(ObjectApiTestConfig.SAMPLE_SUBJECT_MODEL,
              rootAdmin);

      org.assertj.core.api.Assertions.assertThat(subjectsOfUser.stream().map(s -> s.getRef()))
          .containsExactlyInAnyOrder(admin, rootAdmin, rootUri);
    }

    {
      List<Subject> subjectsOfUser =
          subjectManagementApi.getAllSubjects(ObjectApiTestConfig.SAMPLE_SUBJECT_MODEL,
              Arrays.asList(new Subject().ref(superGroup).type(Group.class.getName())));

      org.assertj.core.api.Assertions.assertThat(subjectsOfUser.stream().map(s -> s.getRef()))
          .containsExactlyInAnyOrder(admin, normal, superGroup);
    }

    List<URI> objectsToEval = new ArrayList<>();

    List<Subject> allSubjects =
        subjectManagementApi.getAllSubjects(ObjectApiTestConfig.SAMPLE_SUBJECT_MODEL);

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
      ObjectNode categoryNode =
          objectApi.create(SCHEMA_ASPECTS, new SampleCategory().name("My Category 4"));
      categoryNode.aspects().modify(
          AccessControlInternalApi.ACL_ASPECT, ACL.class,
          acl -> new ACL().rootEntry(new ACLEntry().addEntriesItem(
              new ACLEntry().subject(getSubject(allSubjects, superGroup)).addOperationsItem("read")
                  .addOperationsItem("write"))));
      objectsToEval.add(objectApi.save(categoryNode));
    }
    List<String> operations = Arrays.asList("read", "write");
    {
      Map<String, Set<String>> operationsByCategory =
          objectsToEval.stream().map(u -> objectApi.loadLatest(u))
              .collect(toMap(n -> n.getValueAsString(SampleCategory.NAME),
                  n -> accessControlInternalApi.getAvailableOperationsOn(rootAdmin, n, operations,
                      ObjectApiTestConfig.SAMPLE_SUBJECT_MODEL)));
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
                      ObjectApiTestConfig.SAMPLE_SUBJECT_MODEL)));
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

    ObjectPropertyMapper mapper = objectApi.mapper()
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

    ObjectPropertyMapper mapper = objectApi.mapper()
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
        .nodeStream().map(n -> n.getValueAsString(SampleCategory.NAME)))
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

}
