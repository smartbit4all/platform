package org.smartbit4all.testing.mdm;

import static org.awaitility.Awaitility.with;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
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
import org.smartbit4all.api.collection.StoredList;
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
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.invocation.config.InvocationApiMdmConfig;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MDMModificationApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModificationRequest;
import org.smartbit4all.api.mdm.bean.MDMModificationRequestData;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.bean.BranchEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.SecurityGroup;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleGenericContainer;
import org.smartbit4all.api.sample.bean.SampleInlineObject;
import org.smartbit4all.api.sample.bean.SampleTimeBasedData;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.session.bean.AccountInfo;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.GenericValue;
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
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectCacheEntry;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeList;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.sec.localauth.LocalAuthenticationService;
import org.smartbit4all.testing.UITestApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@SpringBootTest(classes = {MDMApiTestConfig.class}, properties = {
    "invocationregistry.refresh.fixeddelay=2000",
    "applicationruntime.maintain.fixeddelay=2000",
    "applicationsetup.schedule.initdelay=1000",
    "applicationsetup.schedule.fixeddelay=200"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
class MDMApiTest {

  private static final String ORG_SMARTBIT4ALL_API_MY_CUSTOM_API =
      "org.smartbit4all.api.MyCustomApi";

  private static final String MY_CUSTOM_API =
      "MyCustomApi";

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
  StorageApi storageApi;

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

  @Autowired
  private InvocationApi invocationApi;

  private URI adminUri;

  private URI normalUri;

  private UUID viewContextUUID;

  private static final String PASSWD =
      "$2a$10$2LXntgURMBoixkUhddcnVuBPCfcPyB/ely5HkPXc45LmDpdR3nFcS";
  private static final String admin = "user_admin";

  private static final String admin2 = "user_admin2";

  private static final String normal_user = "user_normal";

  @BeforeAll
  void setUpBeforeClass() throws Exception {
    sessionManagementApi.startSession();

    adminUri = createUser(admin, "Adminisztrátor Aladár", MDMSecurityOptions.admin);

    adminUri = createUser(admin2, "Adminisztrátor Árpád", MDMSecurityOptions.admin);

    normalUri = createUser(normal_user, "Publikus József");

  }

  @Test
  @Order(1)
  void testPublishingAndEditingAsDraft() throws Exception {

    authService.login(admin, "asd");

    List<AccountInfo> authentications = sessionApi.getAuthentications();

    MDMEntryApi typeApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        SampleCategoryType.class.getSimpleName());

    MDMEntryApi containerApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        SampleGenericContainer.class.getSimpleName());

    MDMDefinition mdmDefinition = masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST);

    typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE1").name("Type one")
        .description("This is the first category type.")));
    SampleCategoryType second = new SampleCategoryType().code("TYPE2").name("Type two")
        .description("This is the second category type.");
    URI publishedSecond = typeApi.save(objectApi.create(SCHEMA, second)).get(0);
    typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE3").name("Type three")
        .description("This is the third category type.")));

    ObjectNode sameNameCategoryNode =
        objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE3").name("Type four")
            .description("This is the four category type with the same code with type 3."));
    assertThrows(IllegalArgumentException.class, () -> typeApi.save(sameNameCategoryNode),
        "MDMEntryApi don't check unique properties properly");


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

    try {
      typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE4").name("Type four")
          .description("This is the fourth category type.")));
    } catch (IllegalArgumentException e) {
      fail("The unique property map doesn't remove the used unique values on remove entry.", e);
    }

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

    // Test constraint check on cancel and restore.
    // Initiate a branch for the given entry.
    masterDataManagementApi.initiateGlobalBranch(MDMApiTestConfig.TEST, "Editing session 1");
    List<BranchedObjectEntry> list = typeApi.getBranchingList();
    BranchedObjectEntry firstType = list.get(0);
    String firstTypeName = objectApi.loadLatest(firstType.getOriginalUri())
        .getValueAsString(SampleCategoryType.CODE);
    typeApi.remove(firstType.getOriginalUri());

    // Test constraint check on restore.
    BranchedObjectEntry secondType = list.get(1);
    ObjectNode secondTypeNode = objectApi.loadLatest(secondType.getOriginalUri());
    String secondTypeCode = secondTypeNode.getValueAsString(SampleCategoryType.CODE);
    secondTypeNode.setValue(firstTypeName, SampleCategoryType.CODE);
    URI secondTypeBranchUri = typeApi.save(secondTypeNode).get(0);
    assertThrows(IllegalArgumentException.class, () -> typeApi.restore(firstType.getOriginalUri()),
        "On restore the constraint check doesn't work properly.");

    // Test constraint check on cancel.
    BranchedObjectEntry thirdType = list.get(2);
    ObjectNode thridTypeNode = objectApi.loadLatest(thirdType.getOriginalUri());
    thridTypeNode.setValue(secondTypeCode, SampleCategoryType.CODE);
    typeApi.save(thridTypeNode);
    assertThrows(IllegalArgumentException.class, () -> typeApi.cancel(secondTypeBranchUri),
        "On cancel the constraint check doesn't work properly.");

    // Drop the changes we made because constraint check.
    masterDataManagementApi.dropGlobal(MDMApiTestConfig.TEST);


    masterDataManagementApi.initiateGlobalBranch(MDMApiTestConfig.TEST, "Editing session 1");
    List<BranchedObjectEntry> typeList = typeApi.getBranchingList();

    BranchedObjectEntry firstTypeItem = typeList.get(0);

    URI firstTypeBranchUri =
        typeApi.save(objectApi.load(firstTypeItem.getOriginalUri()))
            .get(0);

    ObjectNode containerNode =
        objectApi.create(SCHEMA, new SampleGenericContainer());
    containerNode.ref(SampleGenericContainer.CONTENT).set(firstTypeBranchUri);
    containerApi.save(containerNode);
    masterDataManagementApi.mergeGlobal(MDMApiTestConfig.TEST);

    List<BranchedObjectEntry> updatedTypeList = typeApi.getBranchingList();
    BranchedObjectEntry updatedTypeItem = updatedTypeList.get(0);

    List<BranchedObjectEntry> containerList = containerApi.getBranchingList();
    BranchedObjectEntry firstContainerItem = containerList.get(0);

    ObjectNode firstContainerItemNode =
        objectApi.loadLatest(firstContainerItem.getOriginalUri());

    assertTrue(objectApi.equalsIgnoreVersion(updatedTypeItem.getOriginalUri(),
        firstContainerItemNode.ref(SampleGenericContainer.CONTENT).getObjectUri()));
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

      masterDataManagementApi.mergeGlobal(MDMApiTestConfig.TEST);

      MDMDefinition definition = masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST);

      View querySetView = new View().viewName(MDMApiTestConfig.MDM_LIST_PAGE)
          .putParametersItem(MDMEntryListPageApi.PARAM_MDM_DEFINITION, definition)
          .putParametersItem(MDMEntryListPageApi.PARAM_ENTRY_DESCRIPTOR, masterDataManagementApi
              .getEntryDescriptor(definition, SampleCategoryType.class.getSimpleName()));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      // Call the finalize action.
      // viewContextService.performAction(uuid,
      // new UiActionRequest().code(MDMActions.ACTION_FINALIZE_CHANGES));


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

  private final void update(URI uri, SampleCategory cat) {
    ObjectNode objNode = objectApi.loadLatest(uri);
    objNode.modify(SampleCategory.class, c -> cat);
    objectApi.save(objNode);
  }

  @Test
  @Order(4)
  void testSearchIndexResultPageApiHistory() throws Exception {
    authService.login(normal_user, "asd");

    URI uri = objectApi.saveAsNew(MDMApiTestConfig.TEST,
        new SampleCategory().name("Category 1").color(ColorEnum.RED));
    update(uri, new SampleCategory().name("Category 2").color(ColorEnum.BLACK));
    update(uri, new SampleCategory().name("Category 3").color(ColorEnum.GREEN));
    update(uri, new SampleCategory().name("Category 4").color(ColorEnum.WHITE));

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      SearchPageConfig config =
          new SearchPageConfig().searchIndexSchema(MDMApiTestConfig.TEST)
              .searchIndexName(MDMApiTestConfig.SI_SAMPLECATEGORY)
              .historyPageSize(10)
              .historyObjectUri(uri)
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

      List<String> requiredNames =
          Stream.of("Category 4", "Category 3", "Category 2", "Category 1").collect(toList());

      checkTypeNames(uuid, requiredNames);

    });

    authService.logout();
  }

  @Test
  @Order(5)
  void testSearchIndexResultPageApiHistoryHighVolume() throws Exception {
    authService.login(normal_user, "asd");

    URI uri = null;

    for (int i = 0; i < 250; i++) {
      if (uri == null) {
        uri = objectApi.saveAsNew(MDMApiTestConfig.TEST,
            new SampleCategory().name("Category " + i).color(ColorEnum.RED));
      } else {
        update(uri, new SampleCategory().name("Category " + i).color(ColorEnum.BLACK));
      }
    }

    final URI historyUri = uri;

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      SearchPageConfig config =
          new SearchPageConfig().searchIndexSchema(MDMApiTestConfig.TEST)
              .searchIndexName(MDMApiTestConfig.SI_SAMPLECATEGORY)
              .historyPageSize(10)
              .historyObjectUri(historyUri)
              .addGridViewOptionsItem(new GridView()
                  .orderedColumnNames(
                      Arrays.asList(SampleCategory.NAME, SampleCategory.COLOR,
                          SampleCategory.URI)));

      View querySetView = new View().viewName(MDMApiTestConfig.SEARCHINDEX_LIST_PAGE)
          .objectUri(objectApi.getLatestUri(objectApi.saveAsNew(MDMApiTestConfig.TEST, config)));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      View view = viewApi.getView(uuid);

      // The changed list is visible for the normal user also.
      {
        List<String> requiredNames = new ArrayList<>();
        for (int i = 249; i >= 240; i--) {
          requiredNames.add("Category " + i);
        }
        checkTypeNames(uuid, requiredNames);
      }

      {
        SearchPageModel model = (SearchPageModel) view.getModel();
        viewContextService.performAction(querySetView.getUuid(),
            new UiActionRequest().code(SearchPageApi.ACTION_HISTORY_PREV).putParamsItem("model",
                model));
      }
      {
        List<String> requiredNames = new ArrayList<>();
        for (int i = 239; i >= 230; i--) {
          requiredNames.add("Category " + i);
        }
        checkTypeNames(uuid, requiredNames);
      }
      {
        SearchPageModel model = (SearchPageModel) view.getModel();
        model.setHistoryPageSize(25);
        viewContextService.performAction(querySetView.getUuid(),
            new UiActionRequest().code(SearchPageApi.ACTION_HISTORY_NEXT).putParamsItem("model",
                model));
      }
      {
        List<String> requiredNames = new ArrayList<>();
        for (int i = 249; i >= 240; i--) {
          requiredNames.add("Category " + i);
        }
        checkTypeNames(uuid, requiredNames);
      }

    });

    uiTestApi.runInViewContext(viewContextUUID, () -> {

      SearchPageConfig config =
          new SearchPageConfig().searchIndexSchema(MDMApiTestConfig.TEST)
              .searchIndexName(MDMApiTestConfig.SI_SAMPLECATEGORY)
              .historyLoadAllLimit(1000)
              .historyObjectUri(historyUri)
              .pageSize(300)
              .addGridViewOptionsItem(new GridView()
                  .orderedColumnNames(
                      Arrays.asList(SampleCategory.NAME, SampleCategory.COLOR,
                          SampleCategory.URI)));

      View querySetView = new View().viewName(MDMApiTestConfig.SEARCHINDEX_LIST_PAGE)
          .objectUri(objectApi.getLatestUri(objectApi.saveAsNew(MDMApiTestConfig.TEST, config)));

      UUID uuid = viewApi.showView(querySetView);

      // TODO This should be called implicitly when doing test
      ComponentModel componentModel = viewContextService.getComponentModel(uuid);

      View view = viewApi.getView(uuid);

      // The full list is visible at once.
      {
        List<String> requiredNames = new ArrayList<>();
        for (int i = 249; i >= 0; i--) {
          requiredNames.add("Category " + i);
        }
        checkTypeNames(uuid, requiredNames);
      }

    });

    authService.logout();
  }

  private void checkTypeNames(UUID uuid, List<String> requiredNames) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, uuid,
        SearchPageApi.WIDGET_RESULT_GRID);
    GridPage page = gridModel.getPage();
    List<String> typeNames = page.getRows().stream()
        .map(r -> (String) ((Map<String, Object>) r.getData()).get(SampleCategoryType.NAME))
        .collect(toList());
    Assertions.assertThat(typeNames)
        .containsExactly(requiredNames.toArray(StringConstant.EMPTY_ARRAY));
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
            toMap(n -> objectApi.getLatestUri(n.getValue(URI.class, keyPropertyPath)).toString(),
                n -> n.getObject(clazz)));
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
                    .get(objectApi.getLatestUri(draftString).toString()))
            .addPropertiesItem(
                publishedProperties
                    .get(objectApi.getLatestUri(draftLong).toString()))
            .addPropertiesItem(
                publishedProperties
                    .get(objectApi.getLatestUri(draftCategoryUri).toString()))))
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

    rootNode.list(SampleCategory.SUB_CATEGORIES).nodes().forEach(node -> node
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

  @Test
  @Order(7)
  void testUpdateAndSet() {
    MDMEntryApi entryApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        SampleContainerItem.class.getSimpleName());

    Assertions.assertThat(entryApi.getList().uris()).isEmpty();

    // Update on the main skip create a branch.
    {
      List<Object> toSave = new ArrayList<>();
      toSave.add(new SampleContainerItem().name("Apple").cost(Long.valueOf(0))
          .inlineObject(new SampleInlineObject().name("Apple inline")));
      toSave.add(
          objectApi.create(SCHEMA, new SampleContainerItem().name("Peach").cost(Long.valueOf(1))
              .inlineObject(new SampleInlineObject().name("Peach inline"))));
      toSave.add(
          objectApi.create(SCHEMA, new SampleContainerItem().name("Grape").cost(Long.valueOf(2))
              .inlineObject(new SampleInlineObject().name("Grape inline"))).getObjectAsMap());
      entryApi.updateList(SCHEMA, toSave);
    }

    checkSampleContainerValues(entryApi,
        Arrays.asList("Apple, Apple inline, 0", "Peach, Peach inline, 1",
            "Grape, Grape inline, 2"));

    // Update still on the main and still skip branching.
    {
      List<Object> toSave = new ArrayList<>();
      toSave.add(
          new SampleContainerItem().name("Grape").cost(Long.valueOf(2))
              .inlineObject(new SampleInlineObject().name("Grape inline modified")));
      toSave.add(
          objectApi.create(SCHEMA, new SampleContainerItem().name("Orange").cost(Long.valueOf(3))
              .inlineObject(new SampleInlineObject().name("Orange inline"))).getObjectAsMap());
      entryApi.updateList(SCHEMA, toSave);
    }

    checkSampleContainerValues(entryApi,
        Arrays.asList("Apple, Apple inline, 0", "Peach, Peach inline, 1",
            "Grape, Grape inline modified, 2", "Orange, Orange inline, 3"));

    masterDataManagementApi.initiateGlobalBranch(MDMApiTestConfig.TEST, "Editing properties");

    // Update still on the main and still skip branching.
    SampleContainerItem orange = entryApi.lookup().findByUnique(
        new ObjectPropertyValue().addPathItem(SampleContainerItem.NAME).value("Orange"),
        SampleContainerItem.class);
    {
      List<Object> toSave = new ArrayList<>();
      toSave.add(
          new SampleContainerItem().name("Grape").cost(Long.valueOf(2))
              .inlineObject(new SampleInlineObject().name("Grape inline remodified")));
      toSave.add(
          objectApi.create(SCHEMA, new SampleContainerItem().name("Orange").cost(Long.valueOf(3))
              .inlineObject(new SampleInlineObject().name("Orange inline"))).getObjectAsMap());
      toSave.add(
          objectApi.create(SCHEMA, new SampleContainerItem().name("Lemon").cost(Long.valueOf(4))
              .inlineObject(new SampleInlineObject().name("Lemon inline"))).getObjectAsMap());
      entryApi.updateList(SCHEMA, toSave);
    }

    masterDataManagementApi.mergeGlobal(MDMApiTestConfig.TEST);

    SampleContainerItem orange2 = entryApi.lookup().findByUnique(
        new ObjectPropertyValue().addPathItem(SampleContainerItem.NAME).value("Orange"),
        SampleContainerItem.class);

    Assertions.assertThat(orange.getUri()).isEqualTo(orange2.getUri());

    checkSampleContainerValues(entryApi,
        Arrays.asList("Apple, Apple inline, 0", "Peach, Peach inline, 1",
            "Grape, Grape inline remodified, 2", "Orange, Orange inline, 3",
            "Lemon, Lemon inline, 4"));

  }

  @Test
  @Order(8)
  void testUploadGenericValuesWithExtraProperties() {
    MDMEntryApi entryApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        GenericValue.class.getSimpleName());

    Assertions.assertThat(entryApi.getList().uris()).isEmpty();

    // Update on the main skip create a branch.
    {
      List<Map<String, String>> toSave = new ArrayList<>();
      {
        Map<String, String> map = new HashMap<>();
        map.put(GenericValue.CODE, "Apple");
        map.put(SampleContainerItem.COST, "0");
        map.put(SampleContainerItem.INLINE_OBJECT + StringConstant.SLASH + SampleInlineObject.NAME,
            "Apple inline");
        toSave.add(map);
      }
      {
        Map<String, String> map = new HashMap<>();
        map.put(GenericValue.CODE, "Peach");
        map.put(SampleContainerItem.COST, "1");
        map.put(SampleContainerItem.INLINE_OBJECT + StringConstant.SLASH + SampleInlineObject.NAME,
            "Peach inline");
        toSave.add(map);
      }
      {
        Map<String, String> map = new HashMap<>();
        map.put(GenericValue.CODE, "Grape");
        map.put(SampleContainerItem.COST, "2");
        map.put(SampleContainerItem.INLINE_OBJECT + StringConstant.SLASH + SampleInlineObject.NAME,
            "Grape inline");
        toSave.add(map);
      }
      masterDataManagementApi.importData(MDMApiTestConfig.TEST,
          GenericValue.class.getSimpleName(),
          new MDMModificationRequest().data(new MDMModificationRequestData().definition(toSave)),
          null);
    }

    checkGeneric(entryApi,
        Arrays.asList("Apple, Apple inline, 0", "Peach, Peach inline, 1",
            "Grape, Grape inline, 2"));

    // Update still on the main and still skip branching.
    {
      List<Map<String, String>> toSave = new ArrayList<>();
      {
        Map<String, String> map = new HashMap<>();
        map.put(GenericValue.CODE, "Grape");
        map.put(SampleContainerItem.COST, "2");
        map.put(SampleContainerItem.INLINE_OBJECT + StringConstant.SLASH + SampleInlineObject.NAME,
            "Grape inline modified");
        toSave.add(map);
      }
      {
        Map<String, String> map = new HashMap<>();
        map.put(GenericValue.CODE, "Orange");
        map.put(SampleContainerItem.COST, "3");
        map.put(SampleContainerItem.INLINE_OBJECT + StringConstant.SLASH + SampleInlineObject.NAME,
            "Orange inline");
        toSave.add(map);
      }
      masterDataManagementApi.importData(MDMApiTestConfig.TEST,
          GenericValue.class.getSimpleName(),
          new MDMModificationRequest().data(new MDMModificationRequestData().definition(toSave)),
          null);
    }

    checkGeneric(entryApi,
        Arrays.asList("Apple, Apple inline, 0", "Peach, Peach inline, 1",
            "Grape, Grape inline modified, 2", "Orange, Orange inline, 3"));

    // masterDataManagementApi.initiateGlobalBranch(MDMApiTestConfig.TEST, "Editing properties");
    //
    // // Update still on the main and still skip branching.
    // SampleContainerItem orange = entryApi.lookup().findByUnique(
    // new ObjectPropertyValue().addPathItem(SampleContainerItem.NAME).value("Orange"),
    // SampleContainerItem.class);
    // {
    // List<Object> toSave = new ArrayList<>();
    // toSave.add(
    // new SampleContainerItem().name("Grape").cost(Long.valueOf(2))
    // .inlineObject(new SampleInlineObject().name("Grape inline remodified")));
    // toSave.add(
    // objectApi.create(SCHEMA, new SampleContainerItem().name("Orange").cost(Long.valueOf(3))
    // .inlineObject(new SampleInlineObject().name("Orange inline"))).getObjectAsMap());
    // toSave.add(
    // objectApi.create(SCHEMA, new SampleContainerItem().name("Lemon").cost(Long.valueOf(4))
    // .inlineObject(new SampleInlineObject().name("Lemon inline"))).getObjectAsMap());
    // entryApi.updateList(SCHEMA, toSave);
    // }
    //
    // masterDataManagementApi.mergeGlobal(MDMApiTestConfig.TEST);
    //
    // SampleContainerItem orange2 = entryApi.lookup().findByUnique(
    // new ObjectPropertyValue().addPathItem(SampleContainerItem.NAME).value("Orange"),
    // SampleContainerItem.class);
    //
    // Assertions.assertThat(orange.getUri()).isEqualTo(orange2.getUri());
    //
    // checkSampleContainerValues(entryApi,
    // Arrays.asList("Apple, Apple inline, 0", "Peach, Peach inline, 1",
    // "Grape, Grape inline remodified, 2", "Orange, Orange inline, 3",
    // "Lemon, Lemon inline, 4"));

  }

  /**
   * Validate if the api registry and the related service connection is working well. The newly
   * registered api can be called.
   *
   * @throws InterruptedException
   */
  @Test
  @Order(9)
  void testInvocationApiByApiRegistry() throws InterruptedException {

    MDMEntryApi apiRegisryEntry =
        masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            InvocationApiMdmConfig.MDM_ENTRY_APIREGISTRY);

    String serviceConnection = "MyApiConnection";
    // Create a new ApiData entry. Ber careful, the URI of the ApiData is prepared from the fully
    // qualified name of the api.
    apiRegisryEntry.save(objectApi.create(SCHEMA,
        new ApiData().name(MY_CUSTOM_API)
            .interfaceName(ORG_SMARTBIT4ALL_API_MY_CUSTOM_API)
            .executionApi(InvocationExecutionApiTest.class.getName())
            .serviceConnection(serviceConnection)
            .uri(Invocations.uriOf(ORG_SMARTBIT4ALL_API_MY_CUSTOM_API, MY_CUSTOM_API))));

    // Add a service connection to meet with the api service connection.
    MDMEntryApi serviceConnectionEntry =
        masterDataManagementApi.getApi(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
            InvocationApiMdmConfig.MDM_ENTRY_SERVICECONNECTION);
    serviceConnectionEntry
        .save(objectApi.create(SCHEMA, new ServiceConnection().name(serviceConnection)));

    // Check if we have the api entry
    StoredList apiList = collectionApi.list(Invocations.INVOCATION_SCHEME,
        InvocationApiMdmConfig.MDM_ENTRY_APIREGISTRY);
    assertThat(apiList.uris()).hasSize(1);

    // Wait a little bit to ensure that the InvocationRegistryApi load the give api to the registry
    Thread.sleep(6000);

    InvocationRequest invocationRequest = new InvocationRequest().name(MY_CUSTOM_API)
        .interfaceClass(ORG_SMARTBIT4ALL_API_MY_CUSTOM_API).methodName("doSomeThing");

    try {
      // Do invoke where the execution api is simple reserve the last invovation request
      invocationApi.invoke(invocationRequest);
    } catch (ApiNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Check if we have the invocation request (the invoke was successful
    assertThat(invocationRequest).isEqualTo(InvocationExecutionApiTest.lastRequest);

  }

  private void checkSampleContainerValues(MDMEntryApi entryApi, List<String> valueList) {
    List<ObjectNode> list = entryApi.getList().nodesFromCache().collect(toList());
    Assertions.assertThat(list).hasSize(valueList.size());
    Assertions
        .assertThat(list.stream()
            .map(n -> n.getValueAsString(SampleContainerItem.NAME) + StringConstant.COMMA_SPACE
                + n.getValueAsString(SampleContainerItem.INLINE_OBJECT, SampleInlineObject.NAME)
                + StringConstant.COMMA_SPACE + n.getValue(SampleContainerItem.COST).toString()))
        .containsExactlyInAnyOrder(StringConstant.toArray(valueList));
  }

  private void checkGeneric(MDMEntryApi entryApi, List<String> valueList) {
    List<ObjectNode> list = entryApi.getList().nodesFromCache().collect(toList());
    Assertions.assertThat(list).hasSize(valueList.size());
    Assertions
        .assertThat(list.stream()
            .map(n -> n.getValueAsString(GenericValue.CODE) + StringConstant.COMMA_SPACE
                + n.getValueAsString(SampleContainerItem.INLINE_OBJECT, SampleInlineObject.NAME)
                + StringConstant.COMMA_SPACE + n.getValue(SampleContainerItem.COST).toString()))
        .containsExactlyInAnyOrder(StringConstant.toArray(valueList));
  }

  private URI createUser(String username, String fullname, SecurityGroup... group) {
    URI uri = orgApi.saveUser(new User().username(username).password(PASSWD).name(fullname));
    Arrays.asList(group).stream()
        .forEach(g -> orgApi.addUserToGroup(uri, orgApi.getGroupByName(g.getName()).getUri()));

    return uri;
  }

  @Test
  @Order(10)
  void testUserActivityLogHandling() throws Exception {

    authService.login(admin, "asd");

    MDMEntryApi mdmEntryApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST,
        SampleCategoryType.class.getSimpleName());

    String testTypeCode = "testUserActivityLogHandling_TYPE1";
    ObjectNode sampleNode = objectApi.create(SCHEMA, new SampleCategoryType()
        .code(testTypeCode)
        .name("Type one")
        .description("This is the first category type."));

    List<URI> saveResult = mdmEntryApi.save(sampleNode);
    assertThat(saveResult).isNotEmpty().hasSize(1);
    URI sampleUri = saveResult.get(0);
    sampleNode = objectApi.loadLatest(sampleUri);

    checkUserActivityLogs(sampleNode, true, false, false, false, false);

    sampleNode.setValue(testTypeCode + "_MODIFIED", SampleCategoryType.CODE);

    saveResult = mdmEntryApi.save(sampleNode);
    assertThat(saveResult).isNotEmpty().hasSize(1);
    sampleUri = saveResult.get(0);
    sampleNode = objectApi.loadLatest(sampleUri);

    checkUserActivityLogs(sampleNode, true, true, false, false, false);

    boolean isRemoved = mdmEntryApi.remove(sampleUri);
    assertTrue(isRemoved);
    sampleNode = objectApi.loadLatest(sampleUri);
    sampleUri = sampleNode.getObjectUri();

    checkUserActivityLogs(sampleNode, true, true, false, false, true);
    assertThat(mdmEntryApi.getList().nodes())
        .noneMatch(node -> node.getValueAsString(SampleCategoryType.CODE).contains(testTypeCode));

    boolean isRestored = mdmEntryApi.restore(sampleUri);
    assertTrue(isRestored);
    sampleNode = objectApi.loadLatest(sampleUri);
    sampleUri = sampleNode.getObjectUri();

    checkUserActivityLogs(sampleNode, true, true, false, true, false);
    assertThat(mdmEntryApi.getList().nodes())
        .haveExactly(1, new Condition<>(
            node -> node.getValueAsString(SampleCategoryType.CODE).contains(testTypeCode),
            "only one matching can be restored"));

    // TODO test for MDMEntryApi.Props.MERGED

  }

  private void checkUserActivityLogs(ObjectNode sampleNode, boolean createdShouldExist,
      boolean updatedShouldExist, boolean mergedShouldExist, boolean restoredShouldExist,
      boolean removedShouldExist) {
    checkUserActivityLog(sampleNode, createdShouldExist, MDMEntryApi.Props.CREATED);
    checkUserActivityLog(sampleNode, updatedShouldExist, MDMEntryApi.Props.UPDATED);
    checkUserActivityLog(sampleNode, mergedShouldExist, MDMEntryApi.Props.MERGED);
    checkUserActivityLog(sampleNode, restoredShouldExist, MDMEntryApi.Props.RESTORED);
    checkUserActivityLog(sampleNode, removedShouldExist, MDMEntryApi.Props.REMOVED);
  }

  private void checkUserActivityLog(ObjectNode sampleNode, boolean shouldExist, String prop) {
    UserActivityLog userActivityLog = sampleNode.getValue(UserActivityLog.class, prop);
    if (shouldExist) {
      assertNotNull(userActivityLog);
      assertEquals(admin, userActivityLog.getUserName());
    } else {
      assertNull(userActivityLog);
    }
  }

  @Test
  @Order(100)
  void testPublishingAndEditingAsDraft_paralel() throws Exception {

    authService.login(admin, "asd");

    List<AccountInfo> authentications = sessionApi.getAuthentications();

    MDMEntryApi typeApi = masterDataManagementApi.getApi(MDMApiTestConfig.TEST_PARALEL,
        SampleCategoryType.class.getSimpleName());

    MDMDefinition mdmDefinition =
        masterDataManagementApi.getDefinition(MDMApiTestConfig.TEST_PARALEL);

    typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE1").name("Type one")
        .description("This is the first category type.")));
    SampleCategoryType second = new SampleCategoryType().code("TYPE2").name("Type two")
        .description("This is the second category type.");
    URI publishedSecond = typeApi.save(objectApi.create(SCHEMA, second)).get(0);
    typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE3").name("Type three")
        .description("This is the third category type.")));

    ObjectNode sameNameCategoryNode =
        objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE3").name("Type four")
            .description("This is the four category type with the same code with type 3."));
    assertThrows(IllegalArgumentException.class, () -> typeApi.save(sameNameCategoryNode),
        "MDMEntryApi don't check unique properties properly");


    URI publishedToDelete = typeApi
        .save(objectApi.create(SCHEMA,
            new SampleCategoryType().code("TYPE_TO_DELETE").name("Type to delete")
                .description("This is the category type to delete.")))
        .get(0);


    Assertions
        .assertThat(typeApi.getList().nodes().map(n -> n.getValueAsString(SampleCategoryType.NAME)))
        .containsExactlyInAnyOrder("Type one", "Type two", "Type three", "Type to delete");

    // Initiate a branch for the given entry.
    String modificationId1 =
        masterDataManagementApi.initiateModificationBranch(MDMApiTestConfig.TEST_PARALEL,
            "Editing session 1");
    MDMModificationApi modificationApi1 =
        masterDataManagementApi.getModificationApi(MDMApiTestConfig.TEST_PARALEL, modificationId1);
    modificationApi1.startEditing();

    URI draft =
        typeApi.save(objectApi.loadLatest(publishedSecond).modify(SampleCategoryType.class,
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

    try {
      typeApi.save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE4").name("Type four")
          .description("This is the fourth category type.")));
    } catch (IllegalArgumentException e) {
      fail("The unique property map doesn't remove the used unique values on remove entry.", e);
    }

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
    modificationApi1.merge();

    List<String> listOfDescription = collectionApi.list(MDMApiTestConfig.TEST_PARALEL,
        SampleCategoryType.class.getSimpleName() + "List").uris().stream()
        .map(u -> objectApi.read(u, SampleCategoryType.class).getDescription()).collect(toList());
    Assertions.assertThat(listOfDescription)
        .containsExactlyInAnyOrder("This is the first category type.",
            "This is the second category type v2.", "This is the third category type.",
            "This is the fourth category type.");

    ValueSetDefinitionData definitionData =
        valueSetApi.getDefinitionData(MDMApiTestConfig.TEST_PARALEL,
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
        collectionApi.searchIndex(MDMApiTestConfig.TEST_PARALEL,
            SampleCategoryType.class.getSimpleName(),
            SampleCategoryType.class);

    FilterExpressionList filters = new FilterExpressionList().addExpressionsItem(
        new FilterExpressionData().currentOperation(FilterExpressionOperation.EQUAL)
            .operand1(new FilterExpressionOperandData().isDataName(true)
                .valueAsString(SampleCategoryType.NAME))
            .operand2(new FilterExpressionOperandData().isDataName(false)
                .type(FilterExpressionDataType.STRING).valueAsString("Type two v1")));

    {
      // Test constraint check on cancel and restore.
      // Initiate a branch for the given entry.
      String modificationId2 = masterDataManagementApi
          .initiateModificationBranch(MDMApiTestConfig.TEST_PARALEL, "Editing session 2");
      MDMModificationApi modificationApi2 =
          masterDataManagementApi.getModificationApi(MDMApiTestConfig.TEST_PARALEL,
              modificationId2);
      modificationApi2.startEditing();

      List<BranchedObjectEntry> list = typeApi.getBranchingList();
      BranchedObjectEntry firstType = list.get(0);
      String firstTypeName = objectApi.loadLatest(firstType.getOriginalUri())
          .getValueAsString(SampleCategoryType.CODE);
      URI firstTypeBranchedUri = typeApi.save(objectApi.load(firstType.getOriginalUri())).get(0);
      typeApi.remove(firstTypeBranchedUri);

      // Test constraint check on restore.
      BranchedObjectEntry secondType = list.get(1);
      ObjectNode secondTypeNode = objectApi.loadLatest(secondType.getOriginalUri());
      String secondTypeCode = secondTypeNode.getValueAsString(SampleCategoryType.CODE);
      secondTypeNode.setValue(firstTypeName, SampleCategoryType.CODE);
      URI secondTypeBranchUri = typeApi.save(secondTypeNode).get(0);
      assertThrows(IllegalArgumentException.class,
          () -> typeApi.restore(firstTypeBranchedUri),
          "On restore the constraint check doesn't work properly.");

      // Test constraint check on cancel.
      BranchedObjectEntry thirdType = list.get(2);
      ObjectNode thridTypeNode = objectApi.loadLatest(thirdType.getOriginalUri());
      thridTypeNode.setValue(secondTypeCode, SampleCategoryType.CODE);
      typeApi.save(thridTypeNode);
      assertThrows(IllegalArgumentException.class, () -> typeApi.cancel(secondTypeBranchUri),
          "On cancel the constraint check doesn't work properly.");

      // Drop the changes we made because constraint check.
      modificationApi2.cancel();
    }

    {
      // Test the two paralel editing branch at the same time.
      String modificationId3 = masterDataManagementApi
          .initiateModificationBranch(MDMApiTestConfig.TEST_PARALEL, "Editing session 3");
      MDMModificationApi modificationApi3 =
          masterDataManagementApi.getModificationApi(MDMApiTestConfig.TEST_PARALEL,
              modificationId3);
      modificationApi3.startEditing();

      // Do some modification to see if see the
      Map<String, ObjectNode> byCode = typeApi.getList().nodes()
          .collect(toMap(n -> n.getValueAsString(SampleCategoryType.CODE), n -> n));
      typeApi.remove(byCode.get("TYPE4").getObjectUri());
      typeApi.save(byCode.get("TYPE3").setValue("Type three v2",
          SampleCategoryType.NAME));
      typeApi
          .save(objectApi.create(SCHEMA, new SampleCategoryType().code("TYPE6").name("Type six")
              .description("This is the sixth category type.")))
          .get(0);

      Assertions
          .assertThat(typeApi.getBranchingList().stream()
              .map(oe -> branchApi.toStringBranchedObjectEntry(oe, SampleCategoryType.NAME)))
          .containsExactlyInAnyOrder("NOP: Type one",
              "NOP: Type two v1",
              "MODIFIED: Type three -> Type three v2",
              "DELETED: Type four",
              "NEW: Type six");

      Assertions
          .assertThat(
              typeApi.getList().nodes()
                  .map(n -> n.getValueAsString(SampleCategoryType.DESCRIPTION)))
          .containsExactlyInAnyOrder("This is the first category type.",
              "This is the second category type v2.", "This is the third category type.",
              "This is the fourth category type.");


      modificationApi3.stopEditing();

      Assertions
          .assertThat(
              typeApi.getList().nodes()
                  .map(n -> n.getValueAsString(SampleCategoryType.DESCRIPTION)))
          .containsExactlyInAnyOrder("This is the first category type.",
              "This is the second category type v2.", "This is the third category type.",
              "This is the fourth category type.");

      Assertions
          .assertThat(typeApi.getBranchingList().stream()
              .map(oe -> branchApi.toStringBranchedObjectEntry(oe, SampleCategoryType.NAME)))
          .containsExactlyInAnyOrder("NOP: Type one",
              "NOP: Type two v1",
              "NOP: Type three",
              "NOP: Type four");

      authService.logout();

      authService.login(admin2, "asd");

      Assertions
          .assertThat(typeApi.getBranchingList().stream()
              .map(oe -> branchApi.toStringBranchedObjectEntry(oe, SampleCategoryType.NAME)))
          .containsExactlyInAnyOrder("NOP: Type one",
              "NOP: Type two v1",
              "NOP: Type three",
              "NOP: Type four");

      modificationApi3.startEditing();

      Assertions
          .assertThat(typeApi.getBranchingList().stream()
              .map(oe -> branchApi.toStringBranchedObjectEntry(oe, SampleCategoryType.NAME)))
          .containsExactlyInAnyOrder("NOP: Type one",
              "NOP: Type two v1",
              "MODIFIED: Type three -> Type three v2",
              "DELETED: Type four",
              "NEW: Type six");

      modificationApi3.stopEditing();

      String modificationId4 = masterDataManagementApi
          .initiateModificationBranch(MDMApiTestConfig.TEST_PARALEL, "Editing session 4");
      MDMModificationApi modificationApi4 =
          masterDataManagementApi.getModificationApi(MDMApiTestConfig.TEST_PARALEL,
              modificationId4);
      modificationApi4.startEditing();

    }

  }

  @Test
  @Order(200)
  void testApplicationSetup() throws Exception {
    with()
        .pollInterval(100L, TimeUnit.MILLISECONDS)
        .and()
        .with()
        .pollDelay(1_000L, TimeUnit.MILLISECONDS) // worst case scenario: The Gradle cache concludes
                                                  // none of the other test have to run, so this one
                                                  // is run as the first and only -> we must wait
                                                  // for _at least_ the application setup mgmt API
                                                  // to start initialising the Setup APIs...
        .await()
        .atMost(5_000L, TimeUnit.MILLISECONDS)
        .untilAsserted(() -> assertThat(MDMApiTestSetupv1.executionCounter).isEqualTo(1));
    with()
        .pollInterval(100L, TimeUnit.MILLISECONDS)
        .await()
        .atMost(5_000L, TimeUnit.MILLISECONDS)
        .untilAsserted(() -> assertThat(MDMApiTestSetupv2.executionCounter).isEqualTo(3));
  }

  @Test
  @Order(300)
  void testTimeSeries() throws Exception {
    OffsetDateTime now = OffsetDateTime.now();
    // Construct data to check.
    Random rnd = new Random();
    int minutes = 10;
    List<Double> sumOfMinutes = new ArrayList<>();
    for (int i = 1; i <= minutes; i++) {
      double sumOfMinute = 0.0;
      for (int j = 0; j < 5; j++) {
        double value = rnd.nextInt(100);
        objectApi.saveAsNew(SCHEMA,
            new SampleTimeBasedData().name("T1").value(value).timeOf(now.minusMinutes(i)));
        sumOfMinute += value;
      }
      sumOfMinutes.add(sumOfMinute);
    }
    Collections.reverse(sumOfMinutes);
    // Read all the saved objects
    Storage storage = storageApi.get(SCHEMA);
    {
      Stream<List<URI>> timeSeries =
          storage.streamOfTimeSeries(null, SampleTimeBasedData.class.getName(),
              now.minusMinutes(10).toLocalDateTime(),
              now.toLocalDateTime(), ChronoUnit.MINUTES);
      Map<String, DoubleSummaryStatistics> result = timeSeries
          .flatMap(l -> l.stream().map(u -> objectApi.loadLatest(u)))
          .collect(groupingBy(n -> n.getValueAsString(SampleTimeBasedData.NAME))).entrySet()
          .stream()
          .collect(toMap(Entry::getKey, e -> e.getValue().stream()
              .collect(
                  summarizingDouble(n -> n.getValue(Double.class, SampleTimeBasedData.VALUE)))));
      System.out.println(result);
    }
    {
      Stream<List<URI>> timeSeries =
          storage.streamOfTimeSeries(null, SampleTimeBasedData.class.getName(),
              now.minusMinutes(10).toLocalDateTime(),
              now.toLocalDateTime(), ChronoUnit.MINUTES);
      List<DoubleSummaryStatistics> statisticList = timeSeries
          .map(l -> l.stream().map(u -> objectApi.loadLatest(u))
              .collect(summarizingDouble(n -> n.getValue(Double.class, SampleTimeBasedData.VALUE))))
          .collect(toList());
      assertThat(statisticList.stream().map(s -> s.getSum()).collect(toList()))
          .containsExactlyElementsOf(sumOfMinutes);
    }
  }

}
