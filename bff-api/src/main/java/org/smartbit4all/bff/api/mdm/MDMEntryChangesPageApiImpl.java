package org.smartbit4all.bff.api.mdm;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textbox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.widgetKey;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.mdm.MDMActions;
import org.smartbit4all.api.mdm.MDMApprovalApi;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MDMModificationApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationItem;
import org.smartbit4all.api.mdm.bean.MDMModificationItem.StateEnum;
import org.smartbit4all.api.mdm.bean.MDMModificationNote;
import org.smartbit4all.api.mdm.bean.MDMModificationState;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.org.OrgUtils;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.UiActions.UiActionBuilder;
import org.smartbit4all.api.view.ViewPublisherApi;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.Style;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonDescriptor;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionDialogDescriptor;
import org.smartbit4all.api.view.bean.UiActionFeedbackType;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.generic.GenericPageApi;
import org.smartbit4all.bff.api.mdm.bean.MDMEntryChangesPageModel;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class MDMEntryChangesPageApiImpl extends PageApiImpl<MDMEntryChangesPageModel>
    implements MDMEntryChangesPageApi {

  protected static final String LATEST_MODIFICATION_NOTE_KEY =
      MDMEntryChangesPageModel.LATEST_MODIFICATION_NOTE + StringConstant.DOT
          + MDMModificationNote.NOTE;

  private static DateTimeFormatter dateTimeformatter =
      DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Autowired
  private SessionApi sessionApi;

  @Autowired(required = false)
  private MDMApprovalApi mdmApprovalApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  protected InvocationApi invocationApi;

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  private ViewPublisherApi viewPublisherApi;

  @Autowired
  private MDMAdminPageApi mdmAdminPageApi;

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class PageContext {

    View view;
    MDMDefinition definition;
    // URI mdmBranch;
    List<MDMEntryApi> entryApisWithChanges;
    Map<String, SearchIndex<BranchedObjectEntry>> searchIndexAdminsByDescriptorName;
    List<MDMModificationNote> modificationNotes;
    MDMModificationNote latestModificationNote;
    MDMModificationApi modificationApi;

    public PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      setDefinition(parameters);
      URI mdmBranch = getMdmBranch();
      entryApisWithChanges =
          masterDataManagementApi.getEntryDescriptors(definition, mdmBranch)
              .keySet().stream()
              .map(descriptorName -> masterDataManagementApi.getApi(getDefinition().getName(),
                  descriptorName, mdmBranch))
              .filter(entryApi -> entryApi.getBranchingList().stream()
                  .anyMatch(e -> e.getBranchingState() != BranchingStateEnum.NOP))
              .collect(toList());
      searchIndexAdminsByDescriptorName = getEntryApisWithChanges().stream()
          .collect(toMap(MDMEntryApi::getName,
              e -> collectionApi.searchIndex(getDefinition().getName(),
                  e.getDescriptor().getSearchIndexForEntries(),
                  BranchedObjectEntry.class)));

      return this;
    }


    public PageContext loadOnlyDefinitionByView() {
      setDefinition(parameters(view));
      return this;
    }

    private final void setDefinition(ObjectMapHelper parameters) {
      definition = masterDataManagementApi
          .getDefinition(parameters.require(PARAM_MDM_DEFINITION, String.class));
      modificationApi = masterDataManagementApi
          .getModificationApiForUser(definition.getName(), sessionApi.getUserUri());
      modificationNotes = modificationApi == null ? null
          : modificationApi.getModification().getNotes();
      if (modificationNotes == null) {
        modificationNotes = new ArrayList<>();
      }
      latestModificationNote = !modificationNotes.isEmpty()
          ? modificationNotes.get(modificationNotes.size() - 1)
          : null;
    }

    public boolean isAdmin() {
      return OrgUtils.securityPredicate(sessionApi, getDefinition().getAdminGroupName());
    }

    public boolean isCurrentApprover() {
      URI approver = getCurrentApprover();
      return approver != null && approver.equals(sessionApi.getUserUri());
    }

    public boolean getBranchActive() {
      return getMdmBranch() != null;
    }

    public URI getCurrentApprover() {
      if (modificationApi == null) {
        return null;
      }
      return modificationApi.getModification().getApprover();
    }

    public View getView() {
      return view;
    }


    public List<MDMEntryApi> getEntryApisWithChanges() {
      return entryApisWithChanges;
    }


    public MDMDefinition getDefinition() {
      return definition;
    }

    public MDMModificationApi getModificationApi() {
      return modificationApi;
    }

    public URI getMdmBranch() {
      return modificationApi == null ? null : modificationApi.getModification().getBranchUri();
    }

  }

  public MDMEntryChangesPageApiImpl() {
    super(MDMEntryChangesPageModel.class);
  }

  @Override
  public MDMEntryChangesPageModel initModel(View view) {
    PageContext pageContext = new PageContext();
    pageContext.view = view;
    pageContext.loadByView();
    refreshActions(pageContext);
    createLayout(pageContext);
    view.constraint(createViewConstraint(pageContext));

    return createModel(pageContext);
  }

  protected MDMEntryChangesPageModel createModel(PageContext pageContext) {
    MDMEntryChangesPageModel model = new MDMEntryChangesPageModel();
    model.setLatestModificationNote(pageContext.latestModificationNote);
    URI approver = pageContext.getCurrentApprover();
    if (approver != null) {
      model.approverName(objectApi.loadLatest(approver).getValueAsString(User.NAME));
    }
    return model;
  }

  protected ViewConstraint createViewConstraint(PageContext pageContext) {
    return new ViewConstraint().componentConstraints(Arrays.asList(
        new ComponentConstraint()
            .dataName(LATEST_MODIFICATION_NOTE_KEY)
            .enabled(false)
            .visible(pageContext.latestModificationNote != null),
        new ComponentConstraint()
            .dataName(MDMEntryChangesPageModel.APPROVER_NAME)
            .enabled(false)
            .visible(pageContext.getCurrentApprover() != null)));
  }

  protected void refreshActions(PageContext ctx) {
    if (ctx.getDefinition().getBranchingStrategy() == MDMBranchingStrategy.GLOBAL
        || ctx.getDefinition().getBranchingStrategy() == MDMBranchingStrategy.STRICT_PARALLEL) {
      boolean isAdmin = ctx.isAdmin();
      boolean branchActive = ctx.getBranchActive();
      // if API present, approving enabled
      boolean approvingEnabled = mdmApprovalApi != null;
      boolean globalBranching =
          ctx.getDefinition().getBranchingStrategy() == MDMBranchingStrategy.GLOBAL;
      UiActionBuilder uiActions = UiActions.builder();
      if (approvingEnabled) {
        boolean underApproval = ctx.getCurrentApprover() != null;
        boolean isApprover = ctx.isCurrentApprover();
        boolean canEdit = canEdit(isAdmin, underApproval, isApprover);

        uiActions
            // branchingStrategy = STRICT_PARALLEL
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_CLOSE_EDITING),
                !globalBranching, branchActive)
            // branchingStrategy = GLOBAL
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_START_EDITING).confirm(true),
                isAdmin, globalBranching, !branchActive)
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_CANCEL_CHANGES).confirm(true),
                canEdit, !underApproval, branchActive)
            // approval handling
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_SEND_FOR_APPROVAL).confirm(true),
                isAdmin, !underApproval, branchActive)
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_ADMIN_APPROVE_OK).confirm(true),
                isApprover, underApproval, branchActive)
            .addIf(
                createUiActionWithDescriptor(MDMActions.ACTION_ADMIN_APPROVE_NOT_OK).confirm(true)
                    .input2Type(UiActionInputType.TEXTAREA),
                isApprover, underApproval, branchActive);
      } else {
        uiActions
            // branchingStrategy = STRICT_PARALLEL
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_CLOSE_EDITING),
                isAdmin, !globalBranching, branchActive)
            // branchingStrategy = GLOBAL
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_START_EDITING),
                isAdmin, globalBranching, !branchActive)
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_FINALIZE_CHANGES),
                isAdmin, globalBranching, branchActive)
            .addIf(createUiActionWithDescriptor(MDMActions.ACTION_CANCEL_CHANGES),
                isAdmin, globalBranching, branchActive);
      }

      uiActions.add(MDMActions.REFRESH);
      ctx.view.actions(uiActions.build());

      // Refresh the actions on the admin page

    }
  }

  private UiAction createUiActionWithDescriptor(String actionCode) {
    return new UiAction().code(actionCode).descriptor(
        new UiActionDescriptor()
            .title(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(), actionCode)));
  }

  protected void createLayout(PageContext ctx) {
    SmartComponentLayoutDefinition changesLayout =
        container(LayoutDirection.VERTICAL);

    SmartWidgetDefinition modificationNoteTextField = textbox(LATEST_MODIFICATION_NOTE_KEY,
        localeSettingApi.get("mdm.changes.latestModification"));
    SmartWidgetDefinition selectedApproverLabel = textfield(MDMEntryChangesPageModel.APPROVER_NAME,
        localeSettingApi.get("mdm.changes.approver"));

    if (ctx.getEntryApisWithChanges().isEmpty()) {
      SmartComponentLayoutDefinition emptyForm =
          form(LayoutDirection.VERTICAL,
              selectedApproverLabel,
              modificationNoteTextField,
              label(null, localeSettingApi.get("mdm.changes.nop")));
      changesLayout.addComponentsItem(emptyForm);
    } else {
      SmartComponentLayoutDefinition modificationForm =
          form(LayoutDirection.VERTICAL,
              selectedApproverLabel,
              modificationNoteTextField);
      changesLayout.addComponentsItem(modificationForm);
      // modificationForm.addFormItem(selectedApproverLabel);
      // modificationForm.addFormItem(modificationNoteTextField);
      // if (modificationForm.getForm() != null && !modificationForm.getForm().isEmpty()) {
      // }
      ctx.getEntryApisWithChanges().forEach(entryApi -> {
        String gridId = entryApi.getName().replace(StringConstant.DOT, StringConstant.UNDERLINE);
        GridModel entryGridModel =
            viewApi.getWidgetModelFromView(GridModel.class, ctx.view.getUuid(),
                gridId);
        if (entryGridModel == null) {
          createGridModelByEntryApi(ctx, entryApi, gridId);
        }

        List<BranchedObjectEntry> list = entryApi.getBranchingList();
        gridModelApi.setData(ctx.view.getUuid(), gridId,
            createTableDataForGrid(ctx, entryApi, list));

        changesLayout
            .addComponentsItem(
                form(LayoutDirection.VERTICAL, label(null, entryApi.getDisplayNameList())))
            .addComponentsItem(grid(gridId));
      });
    }

    ctx.view.putComponentLayoutsItem("changesLayout", changesLayout);
  }

  protected GridModel createGridModelByEntryApi(PageContext ctx, MDMEntryApi entryApi,
      String gridId) {
    SearchIndex<BranchedObjectEntry> searchIndexAdmin =
        ctx.searchIndexAdminsByDescriptorName.get(entryApi.getName());
    MDMEntryDescriptor descriptor = entryApi.getDescriptor();
    List<String> columns;
    List<String> searchIndexColumns =
        searchIndexAdmin.getDefinition().getDefinition().allProperties().stream()
            .map(Property::getName).collect(toList());

    if (descriptor.getTableColumns() != null) {
      List<String> tableColumns = descriptor.getTableColumns().stream()
          .map(MDMTableColumnDescriptor::getName)
          .filter(col -> searchIndexColumns.contains(col)) // valid columnNames only!
          .collect(toList());

      List<String> extraColumns = searchIndexColumns.stream()
          .filter(col -> !tableColumns.contains(col))
          .collect(toList());
      columns = new ArrayList<>();
      columns.addAll(tableColumns);
      columns.addAll(extraColumns);
    } else {
      columns = searchIndexColumns;
    }

    GridModel entryGridModel =
        gridModelApi.createGridModel(searchIndexAdmin.getDefinition().getDefinition(),
            columns, ctx.getDefinition().getName(), gridId);
    if (columns.contains(BranchedObjectEntry.BRANCHING_STATE)) {
      List<String> newCols = new ArrayList<>();
      newCols.add(BranchedObjectEntry.BRANCHING_STATE);
      newCols.addAll(
          columns.stream()
              .filter(col -> !BranchedObjectEntry.BRANCHING_STATE.equals(col))
              .collect(toList()));
      entryGridModel.getView().setOrderedColumnNames(newCols);
      entryGridModel.getView().getDescriptor().label(entryApi.getDisplayNameList());
    }
    GridModels.hideColumns(entryGridModel, BranchedObjectEntry.ORIGINAL_URI,
        BranchedObjectEntry.BRANCH_URI);
    if (columns.contains(MDMConstants.PROPERTY_URI)) {
      GridModels.hideColumns(entryGridModel, MDMConstants.PROPERTY_URI);
    }
    boolean hasStateColumn = columns.contains(MDMModificationItem.STATE);
    if (hasStateColumn) {
      GridModels.hideColumns(entryGridModel, MDMModificationItem.STATE);
    }

    final List<GridView> gridViewOptions = entryApi.getDescriptor().getListPageGridViews();
    if (gridViewOptions != null && !gridViewOptions.isEmpty()) {
      entryGridModel.setView(gridViewOptions.get(0));
      entryGridModel.setAvailableViews(new ArrayList<>(gridViewOptions));
    }

    gridModelApi.initGridInView(ctx.view.getUuid(), gridId, entryGridModel);
    gridModelApi.addGridPageCallback(ctx.view.getUuid(), gridId, invocationApi
        .builder(MDMEntryChangesPageApi.class)
        .build(api -> api.addWidgetEntryGridActions(null, ctx.view.getUuid(),
            Boolean.valueOf(hasStateColumn))));

    return entryGridModel;
  }

  protected TableData<?> createTableDataForGrid(PageContext ctx, MDMEntryApi entryApi,
      List<BranchedObjectEntry> list) {
    String constructObjectDefinitionName =
        masterDataManagementApi.constructObjectDefinitionName(ctx.getDefinition(),
            entryApi.getDescriptor());
    ObjectDefinition<?> branchedObjectDefinition =
        objectApi.definition(constructObjectDefinitionName);

    return ctx.searchIndexAdminsByDescriptorName.get(entryApi.getName())
        .executeSearchOnNodes(list.stream().map(i -> {
          ObjectDefinition<?> objectDefinition = branchedObjectDefinition;
          Map<String, Object> objectMap = objectDefinition.toMap(i);
          return objectApi.create(ctx.getDefinition().getName(), objectDefinition,
              objectMap);
        }), new FilterExpressionList().addExpressionsItem(
            new FilterExpressionData().currentOperation(FilterExpressionOperation.NOT_EQUAL)
                .operand1(new FilterExpressionOperandData()
                    .isDataName(true)
                    .valueAsString(BranchedObjectEntry.BRANCHING_STATE)
                    .type(FilterExpressionDataType.STRING))
                .operand2(new FilterExpressionOperandData()
                    .valueAsString(BranchingStateEnum.NOP.toString())
                    .isDataName(false)
                    .type(FilterExpressionDataType.STRING))));
  }

  protected boolean canEdit(boolean isAdmin, boolean underApproval, boolean isApprover) {
    return (isAdmin && !underApproval) || (isApprover && underApproval);
  }

  @Override
  public void refresh(UUID viewUuid, UiActionRequest request) {
    setModel(viewUuid, initModel(viewApi.getView(viewUuid)));
  }

  @Override
  public void startEditing(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    masterDataManagementApi.initiateGlobalBranch(context.getDefinition().getName(),
        String.valueOf(System.currentTimeMillis()));
    context.loadByView();
    refreshActions(context);
    fireActionPerformed(request, context);
  }

  protected PageContext getContextByViewUUID(UUID viewUuid) {
    return getContextByViewUUID(viewUuid, false);
  }

  protected PageContext getContextByViewUUID(UUID viewUuid, boolean loadOnlyDefition) {
    PageContext result = new PageContext();
    result.view = viewApi.getView(viewUuid);
    return loadOnlyDefition ? result.loadOnlyDefinitionByView()
        : result.loadByView();
  }

  @Override
  public void cancelChanges(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid, true);
    if (ctx.modificationApi == null) {
      throw new BusinessLogicException("Trying to cancel changes without active modification!");
    }
    ctx.modificationApi.cancel();

    fireActionPerformed(request, ctx);
    closeOrRefreshPage(ctx);
  }

  private void fireActionPerformed(UiActionRequest request, PageContext ctx) {
    viewPublisherApi.fireActionPerformed(ctx.view, request,
        ctx.modificationApi.getModification().getId(),
        ctx.modificationApi.getModification().getName());
  }

  private void fireActionPerformed(UiActionRequest request, PageContext ctx, Object prevModel,
      Object nextModel) {
    viewPublisherApi.fireActionPerformed(ctx.view, request,
        ctx.modificationApi.getModification().getId(),
        ctx.modificationApi.getModification().getName(),
        prevModel,
        nextModel);
  }

  @Override
  public void stopEditing(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid, true);
    if (ctx.definition.getBranchingStrategy() == MDMBranchingStrategy.STRICT_PARALLEL) {
      // stopEditing is enabled in this strategy
      ctx.modificationApi.stopEditing();
      fireActionPerformed(request, ctx);
      closeOrRefreshPage(ctx);
    }
  }

  protected void closeOrRefreshPage(PageContext ctx) {
    if (ctx.definition.getBranchingStrategy() == MDMBranchingStrategy.STRICT_PARALLEL) {
      showAdminPage(ctx);
    } else {
      ctx.loadByView();
      refreshViewProperties(ctx);
    }
  }

  protected void showAdminPage(PageContext ctx) {
    viewApi.showView(new View()
        .viewName("Admin")
        .putParametersItem(MDMAdminPageApi.PARAM_MDM_DEFINITION,
            ctx.definition.getName()));
  }

  protected void refreshViewProperties(PageContext context) {
    refreshActions(context);
    createLayout(context);
    context.view.constraint(createViewConstraint(context));
    setModel(context.view.getUuid(), createModel(context));
  }

  @Override
  public void finalizeChanges(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid, true);
    if (ctx.getModificationApi() == null) {
      throw new BusinessLogicException("Trying to finalize changes without active modification!");
    }
    ctx.modificationApi.merge();
    fireActionPerformed(request, ctx);
    closeOrRefreshPage(ctx);
  }

  @Override
  public void sendForApproval(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid, false);
    if (mdmApprovalApi == null) {
      throw new BusinessLogicException("Az admin jóváhagyó nem elérhető!");
    }
    String definition = ctx.getDefinition().getName();
    List<URI> approvers = mdmApprovalApi.getApprovers(definition);
    if (approvers == null || approvers.size() != 1) {
      throw new BusinessLogicException("Az admin jóváhagyó nincs beállítva!");
    }
    sendForApproval(viewUuid, approvers.get(0), request);
  }

  protected void sendForApproval(UUID viewUuid, URI approverUri, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid, true);
    if (ctx.getModificationApi() == null) {
      throw new BusinessLogicException("Trying to cancel changes without active modification!");
    }
    ctx.getModificationApi().sendForApproval(approverUri);
    ctx.getModificationApi().stopEditing();

    String approverName = objectApi.loadLatest(approverUri).getValueAsString(User.NAME);
    fireActionPerformed(request, ctx, Collections.emptyMap(),
        Collections.singletonMap(MDMConstants.MDM_ADMIN_APPROVER, approverName));
    closeOrRefreshPage(ctx);
  }

  @Override
  public void adminApproveOk(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid, true);
    if (ctx.getModificationApi() == null) {
      throw new BusinessLogicException("Trying to approve changes without active modification!");
    }
    ctx.getModificationApi().approvalAccepted();

    fireActionPerformed(request, ctx);
    closeOrRefreshPage(ctx);
  }

  @Override
  public void adminApproveNotOk(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = getContextByViewUUID(viewUuid, true);
    if (ctx.getModificationApi() == null) {
      throw new BusinessLogicException("Trying to reject changes without active modification!");
    }
    String reason = actionRequestHelper(request).get(UiActions.INPUT2, String.class);
    ctx.getModificationApi().approvalRejected(reason);

    fireActionPerformed(request, ctx);
    closeOrRefreshPage(ctx);
  }

  @Override
  public GridPage addWidgetEntryGridActions(GridPage page, UUID viewUuid, Boolean hasStateColumn) {
    PageContext ctx = new PageContext();
    ctx.view = viewApi.getView(viewUuid);
    ctx.loadOnlyDefinitionByView();
    Map<String, MDMModificationItem> modificationItems =
        ctx.getModificationApi().getModification().getModificationItems();


    MDMModificationState modificationState = ctx.getModificationApi().getModification().getState();
    boolean isApprover = ctx.isCurrentApprover();
    boolean isAdmin = ctx.isAdmin();
    page.getRows().forEach(row -> {
      updateGridRow(hasStateColumn, modificationItems, modificationState, row, isApprover, isAdmin);
    });
    return page;
  }

  protected void updateGridRow(Boolean hasStateColumn,
      Map<String, MDMModificationItem> modificationItems,
      MDMModificationState modificationState, GridRow row,
      boolean isApprover, boolean isAdmin) {
    String icon;
    Map<String, Object> map = (Map<String, Object>) row.getData();
    BranchingStateEnum brancingState =
        BranchingStateEnum.valueOf(map.get(BranchedObjectEntry.BRANCHING_STATE).toString());
    icon = setIconToEntry(brancingState);
    if (icon != null) {
      row.putIconsItem(BranchedObjectEntry.BRANCHING_STATE,
          Arrays.asList(new ImageResource()
              .source("smart-icon")
              .identifier(icon)));
    }

    UiActionBuilder builder = UiActions.builder();

    URI objectUri = getObjectUri(row);
    if (Boolean.TRUE.equals(hasStateColumn)) {
      MDMModificationItem mdmModificationItem = modificationItems != null
          ? modificationItems.get(objectUri.toString())
          : null;
      StateEnum itemState = mdmModificationItem != null
          ? mdmModificationItem.getState()
          : null;
      map.put(MDMModification.STATE, itemState);
      map.put(MDMDefinitionOption.STATE_NAME, localeSettingApi.get(itemState));

      builder
          .addIf(createUiActionWithDescriptor(MDMActions.ACTION_APPROVE_ENTRY), isApprover,
              modificationState == MDMModificationState.APPROVING
                  && (itemState == null || itemState == StateEnum.FIXED
                      || itemState == StateEnum.REJECTED))
          .addIf(createRejectToEntryAction(), isApprover,
              modificationState == MDMModificationState.APPROVING
                  && (itemState == null || itemState == StateEnum.FIXED
                      || itemState == StateEnum.APPROVED))
          .addIf(createUiActionWithDescriptor(MDMActions.ACTION_FIX_ENTRY), isAdmin,
              (modificationState == MDMModificationState.ACTIVE
                  || modificationState == MDMModificationState.REJECTED)
                  && (itemState == StateEnum.REJECTED));
    }



    builder.add(createAddCommentToEntryAction());
    if (modificationItems != null && modificationItems.containsKey(objectUri.toString())) {
      builder.add(createOpenCommentsToEntryAction());
    }


    row.getActions().clear();
    UiActions.add(row.getActions(), builder.build());
  }

  protected String setIconToEntry(BranchingStateEnum brancingState) {
    String icon;
    switch (brancingState) {
      case NEW:
        icon = "add_circle";
        break;
      case MODIFIED:
        icon = "tag";
        break;
      case DELETED:
        icon = "cancel";
        break;

      default:
        icon = "radio_button_unchecked";
        break;
    }
    return icon;
  }

  private URI getObjectUri(GridRow row) {
    Object rawOiriginalUri = GridModels.getValueFromGridRow(row, BranchedObjectEntry.ORIGINAL_URI);
    Object rawBranchUri = GridModels.getValueFromGridRow(row, BranchedObjectEntry.BRANCH_URI);
    return rawOiriginalUri != null
        ? URI.create(rawOiriginalUri.toString())
        : URI.create(rawBranchUri.toString());
  }

  private UiAction createRejectToEntryAction() {
    return new UiAction().code(MDMActions.ACTION_REJECT_ENTRY)
        .inputType(UiActionInputType.TEXTAREA)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                MDMActions.ACTION_REJECT_ENTRY))
            .inputDialog(new UiActionDialogDescriptor()
                .cancelButton(new UiActionButtonDescriptor()
                    .caption(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                        MDMActions.ACTION_REJECT_ENTRY, "input", "cancel"))
                    .color("gray"))
                .actionButton(new UiActionButtonDescriptor()
                    .caption(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                        MDMActions.ACTION_REJECT_ENTRY, "input", "action"))
                    .color("primary"))
                .title(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                    MDMActions.ACTION_REJECT_ENTRY, "input", "title"))));
  }

  private UiAction createAddCommentToEntryAction() {
    return new UiAction()
        .code(MDMActions.ACTION_ADD_COMMENT_TO_ENTRY)
        .inputType(UiActionInputType.TEXTAREA)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                MDMActions.ACTION_ADD_COMMENT_TO_ENTRY))
            .inputDialog(new UiActionDialogDescriptor()
                .cancelButton(new UiActionButtonDescriptor()
                    .caption(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                        MDMActions.ACTION_ADD_COMMENT_TO_ENTRY, "input", "cancel"))
                    .color("gray"))
                .actionButton(new UiActionButtonDescriptor()
                    .caption(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                        MDMActions.ACTION_ADD_COMMENT_TO_ENTRY, "input", "action"))
                    .color("primary"))
                .title(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                    MDMActions.ACTION_ADD_COMMENT_TO_ENTRY, "input", "title"))));
  }

  private UiAction createOpenCommentsToEntryAction() {
    return new UiAction().code(MDMActions.ACTION_OPEN_COMMENTS_TO_ENTRY)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(),
                MDMActions.ACTION_OPEN_COMMENTS_TO_ENTRY)));
  }

  @Override
  public void addCommentToEntry(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    String comment =
        actionRequestHelper(request).get(UiActions.INPUT, String.class);
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    GridModels.findGridRowById(gridModel, nodeId).ifPresent(row -> {
      URI objectUri = getObjectUri(row);
      PageContext ctx = new PageContext();
      ctx.view = viewApi.getView(viewUuid);
      ctx.loadOnlyDefinitionByView();
      ctx.modificationApi.addComment(objectUri, comment);

      // publish comment added
      Map<String, Object> objectAsMap =
          objectApi.create(objectUri.getScheme(), row.getData()).getObjectAsMap();
      objectAsMap.put("mdmComment", comment);
      objectAsMap.put("mdmEntryType", gridModel.getView().getDescriptor().getLabel());
      UiActions.add(row.getActions(), createOpenCommentsToEntryAction());
      fireActionPerformed(request, ctx, Collections.emptyMap(),
          objectAsMap);
    });
  }

  @Override
  public void openCommentsToEntry(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    GridModels.findGridRowById(gridModel, nodeId).ifPresent(row -> {
      URI objectUri = getObjectUri(row);
      PageContext result = new PageContext();
      result.view = viewApi.getView(viewUuid);
      result.loadOnlyDefinitionByView();
      MDMModificationItem modificationItem =
          result.modificationApi.getModification().getModificationItems().get(objectUri.toString());

      int idx = 0;
      Map<Integer, MDMModificationNote> notesModel = new HashMap<>();
      for (MDMModificationNote note : modificationItem.getNotes()) {
        notesModel.put(idx++, note);
      }

      SmartWidgetDefinition[] widgets = notesModel.entrySet().stream().map(
          e -> textbox(widgetKey(e.getKey().toString(), MDMModificationNote.NOTE),
              e.getValue().getCreated().getName()
                  + StringConstant.COMMA_SPACE
                  + dateTimeformatter.format(e.getValue().getCreated().getTimestamp())))
          .toArray(SmartWidgetDefinition[]::new);

      ViewConstraint viewConstraint = new ViewConstraint().addComponentConstraintsItem(
          new ComponentConstraint()
              .dataName("**")
              .enabled(false));

      viewApi.showView(new View()
          .viewName(PlatformViewNames.GENERIC_PAGE)
          .style(new Style().addClassesToAddItem("mdm-entry-modification-comment-dialog"))
          .constraint(viewConstraint)
          .type(ViewType.DIALOG)
          .putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT,
              form(LayoutDirection.VERTICAL, widgets)
                  .style(new Style().addClassesToAddItem("mdm-entry-modification-comment-layout")))
          .putParametersItem(GenericPageApi.PARAM_MODEL, notesModel)
          .actions(UiActions.builder()
              .add(new UiAction()
                  .code(GenericPageApi.ACTION_CLOSE_VIEW)
                  .descriptor(new UiActionDescriptor()
                      .title(localeSettingApi.get("closeview"))
                      .type(UiActionButtonType.FLAT)
                      .feedbackType(UiActionFeedbackType.NONE)
                      .color("gray")))
              .build()));

      fireActionPerformed(request, result);
    });
  }

  @Override
  public void changeStateToEntry(UUID viewUuid, String widgetId, String nodeId,
      UiActionRequest request) {
    MDMModificationItem.StateEnum state = null;
    String comment = null;
    if (MDMActions.ACTION_APPROVE_ENTRY.equals(request.getCode())) {
      state = StateEnum.APPROVED;
    } else if (MDMActions.ACTION_REJECT_ENTRY.equals(request.getCode())) {
      state = StateEnum.REJECTED;
      comment = actionRequestHelper(request).require(UiActions.INPUT, String.class);
      if (StringUtils.isBlank(comment)) {
        throw new BusinessLogicException(
            localeSettingApi.get(MDMEntryChangesPageApi.class.getSimpleName(), "emptyreason"));
      }
    } else if (MDMActions.ACTION_FIX_ENTRY.equals(request.getCode())) {
      state = StateEnum.FIXED;
    }

    final MDMModificationItem.StateEnum stateConst = state;
    final String commentConst = comment;
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, widgetId);
    GridModels.findGridRowById(gridModel, nodeId).ifPresent(row -> {
      URI objectUri = getObjectUri(row);
      PageContext ctx = new PageContext();
      ctx.view = viewApi.getView(viewUuid);
      ctx.loadOnlyDefinitionByView();
      Map<String, MDMModificationItem> modificationItems =
          ctx.getModificationApi().getModification().getModificationItems();
      StateEnum prevState =
          modificationItems == null ? null : modificationItems.get(objectUri.toString()).getState();
      ctx.modificationApi.updateItemState(objectUri, stateConst);
      if (commentConst != null) {
        ctx.modificationApi.addComment(objectUri, commentConst);
      }
      ctx.loadOnlyDefinitionByView();
      updateGridRow(true, ctx.getModificationApi().getModification().getModificationItems(),
          ctx.getModificationApi().getModification().getState(), row,
          ctx.isCurrentApprover(), ctx.isAdmin());

      // publish state change
      ObjectNode objectNodeToPublish = objectApi.create(objectUri.getScheme(), row.getData());
      Map<String, Object> objectAsMap =
          objectNodeToPublish.getObjectAsMap();
      objectAsMap.put("mdmEntryType", gridModel.getView().getDescriptor().getLabel());
      objectAsMap.put("mdmModificationItemState", stateConst);
      UiActions.add(row.getActions(), createOpenCommentsToEntryAction());
      fireActionPerformed(request, ctx,
          Collections.singletonMap("mdmModificationItemState", prevState),
          objectAsMap);
    });
  }
}
