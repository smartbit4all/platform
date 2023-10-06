package org.smartbit4all.testing.mdm;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy.OrderEnum;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.ComponentModel;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.layout.SmartLayoutApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;
import org.smartbit4all.bff.api.mdm.MDMEntryListPageApi;
import org.smartbit4all.bff.api.search.SearchPageApi;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.sec.localauth.LocalAuthenticationService;
import org.smartbit4all.testing.UITestApi;
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
  private FilterExpressionApi filterExpressionApi;

  @Autowired
  private SessionManagementApi sessionManagementApi;

  @Autowired
  OrgApi orgApi;

  @Autowired
  CollectionApi collectionApi;

  @Autowired
  BranchApi branchApi;

  @Autowired
  SessionApi sessionApi;

  @Autowired
  ValueSetApi valueSetApi;

  @Autowired
  private LocalAuthenticationService authService;

  @Autowired
  private SmartLayoutApi smartLayoutApi;

  @Autowired
  private ViewContextService viewContextService;

  @Autowired
  private ViewApi viewApi;

  @Autowired
  private UITestApi uiTestApi;

  @Autowired
  private MDMEntryListPageApi listPageApi;

  @Autowired
  private MDMEntryEditPageApi editorPageApi;

  private URI adminUri;

  private URI normalUri;

  private UUID viewContextUUID;

  private static final String PASSWD =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";
  private static final String admin = "user_admin";

  private static final String normal_user = "user_normal";

  @BeforeAll
  void setUpBeforeClass() throws Exception {
    sessionManagementApi.startSession();

    adminUri = createUser(admin, "Adminisztrátor Aladár", MDMSecurityOptions.admin);

    normalUri = createUser(normal_user, "Publikus József");

  }

  @Test
  @Order(1)
  void testPublishingAndEditingAsDraft() throws Exception {

    authService.login(admin, "asd");

    List<AccountInfo> authentications = sessionApi.getAuthentications();

    MDMEntryApi typeApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        SampleCategoryType.class.getSimpleName());

    MDMDefinition mdmDefinition = masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST);

    typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE1").name("Type one")
        .description("This is the first category type.")));
    SampleCategoryType second = new SampleCategoryType().code("TYPE2").name("Type two")
        .description("This is the second category type.");
    URI publishedSecond = typeApi.save(objectApi.create(SCHEMA, second)).get(0);
    typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE3").name("Type three")
        .description("This is the third category type.")));
    URI publishedToDelete = typeApi
        .save(objectApi.create(SCHEMA,
            new SampleCategoryType().code("TYPE_TO_DELETE").name("Type to delete")
                .description("This is the category type to delete.")))
        .get(0);


    Assertions
        .assertThat(typeApi.getList().nodes().map(n -> n.getValueAsString(SampleCategoryType.NAME)))
        .containsExactlyInAnyOrder("Type one", "Type two", "Type three", "Type to delete");

    // Initiate a branch for the given entry.
    masterDataManagementApi.initiateGlobalBranch(MDMApiTestConfig.TEST, "Editing session 1");

    URI draft = typeApi.save(objectApi.loadLatest(publishedSecond).modify(SampleCategoryType.class,
        t -> t.name("Type two v1"))).get(0);

    URI draftNew = typeApi
        .save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE4").name("Type four 4")
            .description("This is the fourth category type.")))
        .get(0);

    URI draftNewToDelete =
        typeApi
            .save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE5").name("Type five")
                .description("This is the fifth category type.")))
            .get(0);

    ObjectNode objectNode = objectApi.load(draft).setValue("This is the second category type v2.",
        SampleCategoryType.DESCRIPTION);

    typeApi.remove(draftNewToDelete);
    typeApi.remove(publishedToDelete);

    typeApi.restore(draftNewToDelete);
    typeApi.remove(draftNewToDelete);

    objectApi.save(objectNode);

    typeApi.remove(draftNew);

    Assertions
        .assertThat(
            typeApi.getList().nodes().filter(n -> draftNew.equals(n.getObjectUri())).findFirst())
        .isNotPresent();

    typeApi.restore(draftNew);

    ObjectNode objectFour =
        objectApi.load(draftNew).setValue("Type four", SampleCategoryType.NAME);
    objectApi.save(objectFour);

    List<BranchedObjectEntry> publishedAndDraftObjects =
        typeApi.getBranchingList();

    Assertions
        .assertThat(publishedAndDraftObjects.stream()
            .map(oe -> branchApi.toStringBranchedObjectEntry(oe, SampleCategoryType.NAME)))
        .containsExactlyInAnyOrder("NOP: Type one",
            "MODIFIED: Type two -> Type two v1",
            "NOP: Type three",
            "NEW: Type four",
            "DELETED: Type to delete");

    MDMEntryDescriptor descriptor = typeApi.getDescriptor();

    SearchIndex<SampleCategoryType> searchIndexEntries =
        collectionApi.searchIndex(MDMApiTestConfig.TEST,
            typeApi.getDescriptor().getSearchIndexForEntries(),
            SampleCategoryType.class);

    TableData<?> tdAllEntries =
        searchIndexEntries.executeSearchOnNodes(typeApi.getBranchingList().stream()
            .map(i -> {
              ObjectDefinition<?> definition =
                  objectApi.definition(
                      masterDataManagementApi.constructObjectDefinitionName(mdmDefinition,
                          descriptor));
              return objectApi.create(SCHEMA, definition, definition.toMap(i));
            }), null);

    List<Property<?>> properties = tdAllEntries.properties();

    Property<String> propertyName = (Property<String>) properties.stream()
        .filter(p -> SampleCategoryType.NAME.equals(p.getName())).findFirst().get();
    Property<BranchingStateEnum> propertyState = (Property<BranchingStateEnum>) properties.stream()
        .filter(p -> BranchedObjectEntry.BRANCHING_STATE.equals(p.getName())).findFirst().get();

    Assertions.assertThat(tdAllEntries.values(propertyName)).containsExactlyInAnyOrder(
        "Type one", "Type two v1", "Type three",
        "Type four", "Type to delete");
    Assertions.assertThat(tdAllEntries.values(propertyState)).containsExactlyInAnyOrder(
        BranchingStateEnum.MODIFIED, BranchingStateEnum.NEW, BranchingStateEnum.DELETED,
        BranchingStateEnum.NOP, BranchingStateEnum.NOP);

    // Now we can see the modifications as published
    masterDataManagementApi.mergeGlobal(MDMApiTestConfig.TEST);

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

    Assertions
        .assertThat(
            typeApi.getList().nodes().map(n -> n.getValueAsString(SampleCategoryType.DESCRIPTION)))
        .containsExactlyInAnyOrder("This is the first category type.",
            "This is the second category type v2.", "This is the third category type.",
            "This is the fourth category type.");

    SearchIndex<SampleCategoryType> searchIndex =
        collectionApi.searchIndex(MDMApiTestConfig.TEST, SampleCategoryType.class.getSimpleName(),
            SampleCategoryType.class);

    FilterExpressionList filters = new FilterExpressionList().addExpressionsItem(
        new FilterExpressionData().currentOperation(FilterExpressionOperation.EQUAL)
            .operand1(new FilterExpressionOperandData().isDataName(true)
                .valueAsString(SampleCategoryType.NAME))
            .operand2(new FilterExpressionOperandData().isDataName(false)
                .type(FilterExpressionDataType.STRING).valueAsString("Type two v1")));

    TableData<?> tableData =
        searchIndex.executeSearchOn(typeApi.getList().uris().stream(), filters);

    DataRow row = tableData.rows().get(0);

    List<Object> rowValues =
        tableData.columns().stream().filter(c -> SampleCategoryType.URI.equals(c.getName()))
            .map(c -> tableData.get(c, row)).collect(toList());

  }

  @Test
  @Order(2)
  void testMDMPageApisAsAdmin() throws Exception {
    viewContextUUID = viewContextService.createViewContext().getUuid();

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      List<AccountInfo> authentications = sessionApi.getAuthentications();

      MDMDefinition definition = masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST);

      View querySetView = new View().viewName(MDMApiTestConfig.MDM_LIST_PAGE)
          .putParametersItem(MDMEntryListPageApi.PARAM_MDM_DEFINITION, definition)
          .putParametersItem(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, masterDataManagementApi
              .getEntryDescriptor(definition, SampleCategoryType.class.getSimpleName()));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      View view = uiTestApi.getView(MDMApiTestConfig.MDM_LIST_PAGE);

      Assertions.assertThat(view.getUuid()).isEqualTo(uuid);


      String rowIdTypeThree;
      {
        GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, view.getUuid(),
            MDMEntryListPageApi.WIDGET_ENTRY_GRID);

        GridPage page = gridModel.getPage();

        List<String> typeNames = page.getRows().stream()
            .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
            .collect(toList());

        Assertions.assertThat(typeNames).containsExactlyInAnyOrder(
            "Type one", "Type two v1", "Type three",
            "Type four");

        rowIdTypeThree = getRowIdByPropertyValue(gridModel, SampleCategoryType.NAME, "Type three");
      }

      // Initiate the global branch
      masterDataManagementApi.initiateGlobalBranch(MDMApiTestConfig.TEST, "Admin editing session.");

      // The admin delete an entry. It will be seen as deleted in the grid
      listPageApi.performDeleteEntry(uuid, MDMEntryListPageApi.WIDGET_ENTRY_GRID, rowIdTypeThree,
          new UiActionRequest().code(MDMEntryListPageApi.ACTION_DELETE_ENTRY));

      String rowIdTypeOne;
      {
        GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, view.getUuid(),
            MDMEntryListPageApi.WIDGET_ENTRY_GRID);

        GridPage page = gridModel.getPage();

        List<String> typeNames = page.getRows().stream()
            .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
            .collect(toList());

        Assertions.assertThat(typeNames).containsExactlyInAnyOrder(
            "Type one", "Type two v1", "Type three",
            "Type four");

        BranchingStateEnum stateEnum = page.getRows().stream()
            .filter(r -> "Type three"
                .equals(((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME)))
            .map(r -> objectApi.asType(BranchingStateEnum.class,
                objectApi.getValueFromObjectMap((Map<String, Object>) r.getData(),
                    BranchedObjectEntry.BRANCHING_STATE)))
            .findFirst().get();

        Assertions.assertThat(stateEnum).isEqualTo(BranchingStateEnum.DELETED);
        rowIdTypeOne = getRowIdByPropertyValue(gridModel, SampleCategoryType.NAME, "Type one");

      }

      listPageApi.performEditEntry(uuid, MDMEntryListPageApi.WIDGET_ENTRY_GRID, rowIdTypeOne,
          new UiActionRequest().code(MDMEntryListPageApi.ACTION_EDIT_ENTRY));

      View viewEditing = uiTestApi.getView(MDMApiTestConfig.MDM_EDITING_PAGE);
      Assertions.assertThat(viewEditing).isNotNull();
      // initModel must be called before accessing view.getModel()
      viewApi.getModel(viewEditing.getUuid(), null);

      // Modify the model
      SampleCategoryType editingObject =
          objectApi.asType(SampleCategoryType.class, viewEditing.getModel());
      editingObject.name("Type one v1");

      viewContextService.performAction(viewEditing.getUuid(),
          new UiActionRequest().code(MDMEntryEditPageApi.ACTION_SAVE).putParamsItem("model",
              editingObject));
      {
        GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, view.getUuid(),
            MDMEntryListPageApi.WIDGET_ENTRY_GRID);

        GridPage page = gridModel.getPage();

        List<String> typeNames = page.getRows().stream()
            .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
            .collect(toList());

        Assertions.assertThat(typeNames).containsExactlyInAnyOrder(
            "Type one v1", "Type two v1", "Type three",
            "Type four");

      }

    });

    authService.logout();

    authService.login(normal_user, "asd");

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      MDMDefinition definition = masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST);

      View querySetView = new View().viewName(MDMApiTestConfig.MDM_LIST_PAGE)
          .putParametersItem(MDMEntryListPageApi.PARAM_MDM_DEFINITION, definition)
          .putParametersItem(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, masterDataManagementApi
              .getEntryDescriptor(definition, SampleCategoryType.class.getSimpleName()));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      {
        GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, uuid,
            MDMEntryListPageApi.WIDGET_ENTRY_GRID);

        GridPage page = gridModel.getPage();

        List<String> typeNames = page.getRows().stream()
            .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
            .collect(toList());

        // Noting happened up till now for the public user.
        Assertions.assertThat(typeNames).containsExactlyInAnyOrder(
            "Type one", "Type two v1", "Type three",
            "Type four");

      }

    });

    authService.logout();

    authService.login(admin, "asd");

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      MDMDefinition definition = masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST);

      View querySetView = new View().viewName(MDMApiTestConfig.MDM_LIST_PAGE)
          .putParametersItem(MDMEntryListPageApi.PARAM_MDM_DEFINITION, definition)
          .putParametersItem(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, masterDataManagementApi
              .getEntryDescriptor(definition, SampleCategoryType.class.getSimpleName()));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      // Call the finalize action.
      viewContextService.performAction(uuid,
          new UiActionRequest().code(MDMEntryListPageApi.ACTION_FINALIZE_CHANGES));

      {
        GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, uuid,
            MDMEntryListPageApi.WIDGET_ENTRY_GRID);

        GridPage page = gridModel.getPage();

        List<String> typeNames = page.getRows().stream()
            .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
            .collect(toList());

        // The changed list is visible for the admin user also.
        Assertions.assertThat(typeNames).containsExactlyInAnyOrder(
            "Type one v1", "Type two v1", "Type four");

      }

    });

    authService.logout();

    authService.login(normal_user, "asd");

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      MDMDefinition definition = masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST);

      View querySetView = new View().viewName(MDMApiTestConfig.MDM_LIST_PAGE)
          .putParametersItem(MDMEntryListPageApi.PARAM_MDM_DEFINITION, definition)
          .putParametersItem(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, masterDataManagementApi
              .getEntryDescriptor(definition, SampleCategoryType.class.getSimpleName()));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      {
        GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, uuid,
            MDMEntryListPageApi.WIDGET_ENTRY_GRID);

        GridPage page = gridModel.getPage();

        List<String> typeNames = page.getRows().stream()
            .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
            .collect(toList());

        // The changed list is visible for the normal user also.
        Assertions.assertThat(typeNames).containsExactlyInAnyOrder(
            "Type one v1", "Type two v1", "Type four");

      }

    });

    authService.logout();

  }

  @Test
  @Order(3)
  void testSearchIndexResultPageApis() throws Exception {


    authService.login(normal_user, "asd");

    objectApi.saveAsNew(MDMApiTestConfig.TEST,
        new SampleCategory().name("Category 1").color(ColorEnum.RED));
    objectApi.saveAsNew(MDMApiTestConfig.TEST,
        new SampleCategory().name("Category 2").color(ColorEnum.BLACK));
    objectApi.saveAsNew(MDMApiTestConfig.TEST,
        new SampleCategory().name("Category 3").color(ColorEnum.GREEN));
    objectApi.saveAsNew(MDMApiTestConfig.TEST,
        new SampleCategory().name("Category 4").color(ColorEnum.WHITE));

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      SearchPageConfig config =
          new SearchPageConfig().searchIndexSchema(MDMApiTestConfig.TEST)
              .searchIndexName(MDMApiTestConfig.SI_SAMPLECATEGORY)
              .addGridViewOptionsItem(new GridView()
                  .orderedColumnNames(
                      Arrays.asList(SampleCategory.NAME, SampleCategory.COLOR, SampleCategory.URI))
                  .addOrderByListItem(new FilterExpressionOrderBy()
                      .propertyName(SampleCategory.NAME).order(OrderEnum.DESC)));

      View querySetView = new View().viewName(MDMApiTestConfig.SEARCHINDEX_LIST_PAGE)
          .objectUri(objectApi.getLatestUri(objectApi.saveAsNew(MDMApiTestConfig.TEST, config)));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      View view = viewApi.getView(uuid);

      {
        GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, uuid,
            SearchPageApi.WIDGET_RESULT_GRID);

        GridPage page = gridModel.getPage();

        List<String> typeNames = page.getRows().stream()
            .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
            .collect(toList());

        // The changed list is visible for the normal user also.
        Assertions.assertThat(typeNames).containsExactly(
            "Category 4", "Category 3", "Category 2", "Category 1");

      }

    });

    authService.logout();
  }


  private static final String getRowIdByPropertyValue(GridModel model, String propertyName,
      String propertyValue) {
    return model.getPage().getRows().stream()
        .filter(r -> propertyValue
            .equals(((Map<String, Object>) r.getData()).get(propertyName)))
        .map(r -> r.getId())
        .findFirst().get();
  }

  public <O> O getPublishedObject(BranchedObjectEntry entry, Class<O> clazz) {
    return entry == null || entry.getOriginalUri() == null ? null
        : objectApi.read(entry.getOriginalUri(), clazz);
  }

  public <O> O getDraftObject(BranchedObjectEntry entry, Class<O> clazz) {
    return entry == null || entry.getBranchUri() == null ? null
        : objectApi.read(entry.getBranchUri(), clazz);
  }


  private <O> Map<String, O> getPublishedObjects(MDMEntryApi typeApi, Class<O> clazz,
      String... keyPropertyPath) {
    return typeApi.getList().nodes()
        .collect(
            toMap(n -> n.getValue(URI.class, keyPropertyPath).toString(), n -> n.getObject(clazz)));
  }

  @Test
  @Order(4)
  void testObjectPropertyDefinition() {
    MDMEntryApi objectDefinitionMDMApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        ObjectDefinitionData.class.getSimpleName());
    MDMEntryApi propertyDefinitionMDMApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        PropertyDefinitionData.class.getSimpleName());

    masterDataManagementApi.initiateGlobalBranch(MDMApiTestConfig.TEST, "Editing properties");

    // Save some property definition drafts
    URI draftString = propertyDefinitionMDMApi.save(objectApi.create(SCHEMA,
        new PropertyDefinitionData().name(PROPERTY_STRING).typeClass(String.class.getName())
            .widget(new SmartWidgetDefinition().type(SmartFormWidgetType.TEXT_FIELD))))
        .get(0);
    URI draftLong = propertyDefinitionMDMApi.save(objectApi.create(SCHEMA,
        new PropertyDefinitionData().name(PROPERTY_LONG).typeClass(Long.class.getName())
            .widget(new SmartWidgetDefinition().type(SmartFormWidgetType.TEXT_FIELD))))
        .get(0);
    URI draftCategoryUri = propertyDefinitionMDMApi.save(objectApi.create(SCHEMA,
        new PropertyDefinitionData().name(CATEGORY).typeClass(URI.class.getName())
            .referredType(SampleCategoryType.class.getName())
            .referredPropertyName(SampleCategoryType.URI)
            .widget(new SmartWidgetDefinition().type(SmartFormWidgetType.TEXT_FIELD))))
        .get(0);

    masterDataManagementApi.mergeGlobal(MDMApiTestConfig.TEST);

    Map<String, PropertyDefinitionData> publishedProperties =
        getPublishedObjects(propertyDefinitionMDMApi, PropertyDefinitionData.class,
            PropertyDefinitionData.URI);
    URI appleDefUri = objectDefinitionMDMApi.save(objectApi.create(SCHEMA,
        new ObjectDefinitionData()
            .uri(ObjectDefinition.uriOf(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE))
            .qualifiedName(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE).addPropertiesItem(
                publishedProperties
                    .get(draftString.toString()))
            .addPropertiesItem(
                publishedProperties
                    .get(draftLong.toString()))
            .addPropertiesItem(
                publishedProperties
                    .get(draftCategoryUri.toString()))))
        .get(0);


    ObjectDefinitionData definitionData = objectApi
        .loadLatest(objectDefinitionMDMApi.getList().uris().stream()
            .filter(u -> objectApi.equalsIgnoreVersion(u, appleDefUri)).findFirst().get())
        .getObject(ObjectDefinitionData.class);

    ObjectDefinition<?> defApple = objectApi.definition(ORG_SMARTBIT4ALL_MYDOMAIN_APPLE);

    defApple.reloadDefinitionData();

    Assertions.assertThat(objectDefinitionMDMApi.getList().uris())
        .contains(appleDefUri);

    Assertions
        .assertThat(objectDefinitionMDMApi.getList().uris().stream()
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
  @Order(5)
  void testBranchCache() {

    ObjectCacheEntry<BranchEntry> branchCacheEntry =
        objectApi.getCacheEntry(BranchEntry.class);

    URI branch1 = objectApi.saveAsNew("branch", new BranchEntry().caption("Branch"));

    for (int i = 0; i < 10; i++) {
      String caption = "Branch " + i;
      objectApi.save(
          objectApi.loadLatest(branch1).modify(BranchEntry.class, be -> be.caption(caption)));

      try {
        Thread.sleep(20);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      BranchEntry branchEntry = branchCacheEntry.get(branch1);

      Assertions.assertThat(branchEntry).satisfies(new Condition<>(
          entry -> caption.equals(branchEntry.getCaption()), "The caption is different"));

      BranchEntry branchEntry2 = branchCacheEntry.get(branch1);

      assertEquals(branchEntry, branchEntry2);

    }

  }

  @Test
  @Order(6)
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
