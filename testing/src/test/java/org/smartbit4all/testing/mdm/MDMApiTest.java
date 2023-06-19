package org.smartbit4all.testing.mdm;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.mdm.MDMEntry;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.view.layout.SmartLayoutApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.sec.localauth.LocalAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@SpringBootTest(classes = {MDMApiTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class MDMApiTest {

  private static final String SCHEMA = "test";

  private static final String CATEGORY = "category";

  private static final String PROPERTY_LONG = "propertyLong";

  private static final String PROPERTY_STRING = "propertyString";

  private static final String ORG_SMARTBIT4ALL_MYDOMAIN_APPLE = "org.smartbit4all.mydomain.Apple";

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  OrgApi orgApi;

  @Autowired
  CollectionApi collectionApi;

  @Autowired
  ValueSetApi valueSetApi;

  @Autowired
  private LocalAuthenticationService authService;

  @Autowired
  private SmartLayoutApi smartLayoutApi;

  private URI adminUri;

  private static final String PASSWD =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";
  private static final String admin = "user_admin";

  @BeforeAll
  void setUpBeforeClass() throws Exception {
    sessionManagementApi.startSession();

    adminUri = createUser(admin, "Adminisztrátor Aladár");

    authService.login(admin, "asd");

  }

  @Test
  @Order(1)
  void testPublishingAndEditingAsDraft() throws IOException {

    MDMEntryApi typeApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        SampleCategoryType.class.getSimpleName());

    typeApi.saveAsNewPublished(new SampleCategoryType().code("TYPE1").name("Type one")
        .description("This is the first category type."));
    SampleCategoryType second = new SampleCategoryType().code("TYPE2").name("Type two")
        .description("This is the second category type.");
    URI publishedSecond = typeApi.saveAsNewPublished(second);
    typeApi.saveAsNewPublished(new SampleCategoryType().code("TYPE3").name("Type three")
        .description("This is the third category type."));

    Map<String, SampleCategoryType> publishedObjects =
        getPublishedObjects(typeApi, SampleCategoryType.class);
    Assertions.assertThat(publishedObjects.values().stream().map(sct -> sct.getName()))
        .containsExactlyInAnyOrder("Type one", "Type two", "Type three");

    URI draft = typeApi.saveAsDraft(second.name("Type two v1"));

    URI draftNew = typeApi.saveAsDraft(new SampleCategoryType().code("TYPE4").name("Type four")
        .description("This is the fourth category type."));

    ObjectNode objectNode = objectApi.load(draft).setValue("This is the second category type v2.",
        SampleCategoryType.DESCRIPTION);

    objectApi.save(objectNode);

    List<MDMEntry> publishedAndDraftObjects =
        typeApi.getAllEntries();

    Assertions
        .assertThat(publishedAndDraftObjects.stream()
            .map(oe -> {
              SampleCategoryType p = getPublishedObject(oe, SampleCategoryType.class);
              SampleCategoryType d = getDraftObject(oe, SampleCategoryType.class);
              return p == null ? StringConstant.ARROW + d.getName()
                  : (d != null
                      ? p.getName() + StringConstant.ARROW + d.getName()
                      : p.getName());
            }))
        .containsExactlyInAnyOrder("Type one", "Type two->Type two v1", "Type three",
            "->Type four");

    typeApi.publishCurrentModifications();

    List<String> listOfDescription = collectionApi.list(MDMApiTestConfig.TEST,
        SampleCategoryType.class.getSimpleName() + "List").uris().stream()
        .map(u -> objectApi.read(u, SampleCategoryType.class).getDescription()).collect(toList());
    Assertions.assertThat(listOfDescription)
        .containsExactlyInAnyOrder("This is the first category type.",
            "This is the second category type v2.", "This is the third category type.",
            "This is the fourth category type.");

    ValueSetDefinitionData definitionData = valueSetApi.getDefinitionData(MDMApiTestConfig.TEST,
        SampleCategoryType.class.getSimpleName());
    ValueSetData valueSetData = valueSetApi.valuesOf(definitionData);

    List<String> listOfDescriptionFromValueSet =
        valueSetData.getValues().stream().map(o -> objectApi.asType(SampleCategoryType.class, o))
            .map(ct -> ct.getDescription()).collect(toList());
    Assertions.assertThat(listOfDescriptionFromValueSet)
        .containsExactlyInAnyOrder("This is the first category type.",
            "This is the second category type v2.", "This is the third category type.",
            "This is the fourth category type.");

    publishedObjects = getPublishedObjects(typeApi, SampleCategoryType.class);
    Assertions.assertThat(publishedObjects.values().stream().map(sct -> sct.getDescription()))
        .containsExactlyInAnyOrder("This is the first category type.",
            "This is the second category type v2.", "This is the third category type.",
            "This is the fourth category type.");

  }

  public <O> O getPublishedObject(MDMEntry entry, Class<O> clazz) {
    return entry == null || entry.getPublished() == null ? null
        : objectApi.read(entry.getPublished(), clazz);
  }

  public <O> O getDraftObject(MDMEntry entry, Class<O> clazz) {
    return entry == null || entry.getDraft() == null ? null
        : objectApi.read(entry.getDraft(), clazz);
  }


  private <O> Map<String, O> getPublishedObjects(MDMEntryApi typeApi, Class<O> clazz) {
    Map<String, O> publishedObjects =
        typeApi.getPublishedObjects().entrySet().stream().collect(
            toMap(Entry::getKey,
                e -> objectApi.read(e.getValue().getObjectUri(), clazz)));
    return publishedObjects;
  }

  @Test
  @Order(2)
  void testObjectPropertyDefinition() {
    MDMEntryApi objectDefinitionMDMApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        ObjectDefinitionData.class.getSimpleName());
    MDMEntryApi propertyDefinitionMDMApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        PropertyDefinitionData.class.getSimpleName());

    // Save some property definition drafts
    URI draftString = propertyDefinitionMDMApi.saveAsDraft(
        new PropertyDefinitionData().name(PROPERTY_STRING).typeClass(String.class.getName())
            .widget(new SmartWidgetDefinition().type(SmartFormWidgetType.TEXT_FIELD)));
    URI draftLong = propertyDefinitionMDMApi.saveAsDraft(
        new PropertyDefinitionData().name(PROPERTY_LONG).typeClass(Long.class.getName())
            .widget(new SmartWidgetDefinition().type(SmartFormWidgetType.TEXT_FIELD)));
    URI draftCategoryUri = propertyDefinitionMDMApi.saveAsDraft(
        new PropertyDefinitionData().name(CATEGORY).typeClass(URI.class.getName())
            .referredType(SampleCategoryType.class.getName())
            .referredPropertyName(SampleCategoryType.URI)
            .widget(new SmartWidgetDefinition().type(SmartFormWidgetType.TEXT_FIELD)));

    propertyDefinitionMDMApi.publishCurrentModifications();

    Map<String, PropertyDefinitionData> publishedProperties =
        getPublishedObjects(propertyDefinitionMDMApi, PropertyDefinitionData.class);
    URI appleDefUri = objectDefinitionMDMApi.saveAsNewPublished(new ObjectDefinitionData()
        .qualifiedName(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE).addPropertiesItem(
            publishedProperties
                .get(objectApi.getLatestUri(draftString).toString()))
        .addPropertiesItem(
            publishedProperties
                .get(objectApi.getLatestUri(draftLong).toString()))
        .addPropertiesItem(
            publishedProperties
                .get(objectApi.getLatestUri(draftCategoryUri).toString())));

    ObjectDefinitionData definitionData = objectApi
        .loadLatest(objectDefinitionMDMApi.getPublishedMap().get(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE))
        .getObject(ObjectDefinitionData.class);

    ObjectDefinition<?> defApple = objectApi.definition(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE);

    defApple.reloadDefinitionData();

    Assertions.assertThat(objectDefinitionMDMApi.getPublishedMap())
        .containsKeys(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE);

    Assertions
        .assertThat(objectDefinitionMDMApi.getPublishedList().uris().stream()
            .map(u -> objectApi.read(u, ObjectDefinitionData.class))
            .collect(toMap(ObjectDefinitionData::getQualifiedName, odd -> odd)))
        .containsKeys(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE);

    Assertions.assertThat(definitionData.getQualifiedName())
        .isEqualTo(defApple.getQualifiedName());

    Map<String, PropertyDefinitionData> propertiesByName = defApple.getPropertiesByName();

    Assertions.assertThat(propertiesByName).containsKeys("propertyString");

    SmartLayoutDefinition layout = smartLayoutApi.createLayout(defApple.getDefinitionData(),
        Arrays.asList(PROPERTY_STRING, PROPERTY_LONG));

    Assertions.assertThat(layout.getWidgets().stream().map(sw -> sw.getKey()))
        .containsExactly(PROPERTY_STRING, PROPERTY_LONG);

  }

  @Test
  @Order(3)
  void testBranchCache() {

    ObjectCacheEntry<BranchEntry> branchCacheEntry =
        objectApi.getCacheEntry(BranchEntry.class);

    URI branch1 = objectApi.saveAsNew("branch", new BranchEntry().caption("Branch"));

    for (int i = 0; i < 100; i++) {
      String caption = "Branch " + i;
      objectApi.save(
          objectApi.loadLatest(branch1).modify(BranchEntry.class, be -> be.caption(caption)));

      BranchEntry branchEntry = branchCacheEntry.get(branch1);

      Assertions.assertThat(branchEntry).satisfies(new Condition<>(
          entry -> caption.equals(branchEntry.getCaption()), "The caption is different"));

      BranchEntry branchEntry2 = branchCacheEntry.get(branch1);

      assertEquals(branchEntry, branchEntry2);

    }

  }

  @Test
  @Order(4)
  void testBranchingOperations() {

    ObjectCacheEntry<BranchEntry> branchCacheEntry =
        objectApi.getCacheEntry(BranchEntry.class);

    String caption = "First branch";
    URI branch1 = objectApi.saveAsNew("branch", new BranchEntry().caption(caption));

    // Construct the baseline for a hierarchical object structure in one object node.
    String rootCategoryCaption = "root category";
    ObjectNode rootNode = objectApi.create(SCHEMA,
        new SampleCategory().name(rootCategoryCaption).cost(Long.valueOf(1500))
            .createdAt(OffsetDateTime.now()));

    String firstSubCategoryCaption = "first sub category";
    String secondSubCategoryCaption = "second sub category";
    {
      ObjectNodeList subCategories = rootNode.list(SampleCategory.SUB_CATEGORIES);
      {
        ObjectNode subCategoryNode = subCategories
            .addNewObject(
                new SampleCategory().name(firstSubCategoryCaption).cost(Long.valueOf(2500))
                    .createdAt(OffsetDateTime.now()));
      }
      {
        ObjectNode subCategoryNode = subCategories
            .addNewObject(
                new SampleCategory().name(secondSubCategoryCaption).cost(Long.valueOf(2500))
                    .createdAt(OffsetDateTime.now()));
      }
    }

    URI rootUri = objectApi.save(rootNode);

    rootNode = objectApi.loadLatest(rootUri);

    rootNode.list(SampleCategory.SUB_CATEGORIES).nodeStream().forEach(node -> node
        .modify(SampleCategory.class, c -> c.name(c.getName() + StringConstant.HYPHEN + caption)));

    objectApi.save(rootNode, branch1);

    // Load on the "main" branch
    {
      ObjectNode objectNode = objectApi.loadLatest(rootUri);
      Assertions.assertThat(objectNode).extracting(o -> o.getValueAsString(SampleCategory.NAME),
          o -> o.getValueAsString(SampleCategory.SUB_CATEGORIES, "0", SampleCategory.NAME),
          o -> o.getValueAsString(SampleCategory.SUB_CATEGORIES, "1", SampleCategory.NAME))
          .containsExactly(rootCategoryCaption, firstSubCategoryCaption, secondSubCategoryCaption);
    }

    {
      ObjectNode objectNode = objectApi.loadLatest(rootUri, branch1);
      Assertions.assertThat(objectNode).extracting(o -> o.getValueAsString(SampleCategory.NAME),
          o -> o.getValueAsString(SampleCategory.SUB_CATEGORIES, "0", SampleCategory.NAME),
          o -> o.getValueAsString(SampleCategory.SUB_CATEGORIES, "1", SampleCategory.NAME))
          .containsExactly(rootCategoryCaption,
              firstSubCategoryCaption + StringConstant.HYPHEN + caption,
              secondSubCategoryCaption + StringConstant.HYPHEN + caption);
    }

  }

  private URI createUser(String username, String fullname, SecurityGroup... group) {
    URI uri = orgApi.saveUser(new User().username(username).password(PASSWD).name(fullname));
    Arrays.asList(group).stream()
        .forEach(g -> orgApi.addUserToGroup(uri, orgApi.getGroupByName(g.getName()).getUri()));

    return uri;
  }

}
