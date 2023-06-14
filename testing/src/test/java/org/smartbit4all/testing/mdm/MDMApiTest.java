package org.smartbit4all.testing.mdm;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.view.layout.SmartLayoutApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.sec.localauth.LocalAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.util.stream.Collectors.toMap;

@SpringBootTest(classes = {MDMApiTestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class MDMApiTest {

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
  private LocalAuthenticationService authService;

  @Autowired
  private SmartLayoutApi smartLayoutApi;

  @Autowired
  private ObjectCacheEntry<BranchEntry> branchCacheEntry;

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

    MDMEntryApi<SampleCategoryType> typeApi =
        masterDataManagementApi.getApi(SampleCategoryType.class);

    typeApi.saveAsNewPublished(new SampleCategoryType().code("TYPE1").name("Type one")
        .description("This is the first category type."));
    SampleCategoryType second = new SampleCategoryType().code("TYPE2").name("Type two")
        .description("This is the second category type.");
    URI publishedSecond = typeApi.saveAsNewPublished(second);
    typeApi.saveAsNewPublished(new SampleCategoryType().code("TYPE3").name("Type three")
        .description("This is the third category type."));

    Map<String, SampleCategoryType> publishedObjects = typeApi.getPublishedObjects();
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
              SampleCategoryType p = typeApi.getPublishedObject(oe);
              SampleCategoryType d = typeApi.getDraftObject(oe);
              return p == null ? StringConstant.ARROW + d.getName()
                  : (d != null
                      ? p.getName() + StringConstant.ARROW + d.getName()
                      : p.getName());
            }))
        .containsExactlyInAnyOrder("Type one", "Type two->Type two v1", "Type three",
            "->Type four");

    typeApi.publishCurrentModifications();

    publishedObjects = typeApi.getPublishedObjects();
    Assertions.assertThat(publishedObjects.values().stream().map(sct -> sct.getDescription()))
        .containsExactlyInAnyOrder("This is the first category type.",
            "This is the second category type v2.", "This is the third category type.",
            "This is the fourth category type.");

  }

  @Test
  @Order(2)
  void testObjectPropertyDefinition() {
    MDMEntryApi<ObjectDefinitionData> objectDefinitionMDMApi =
        masterDataManagementApi.getApi(ObjectDefinitionData.class);
    MDMEntryApi<PropertyDefinitionData> propertyDefinitionMDMApi =
        masterDataManagementApi.getApi(PropertyDefinitionData.class);

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
        propertyDefinitionMDMApi.getPublishedObjects();
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

  private URI createUser(String username, String fullname, SecurityGroup... group) {
    URI uri = orgApi.saveUser(new User().username(username).password(PASSWD).name(fullname));
    Arrays.asList(group).stream()
        .forEach(g -> orgApi.addUserToGroup(uri, orgApi.getGroupByName(g.getName()).getUri()));

    return uri;
  }

}
