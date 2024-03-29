package org.smartbit4all.bff.api.mdm;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textbox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionDataType;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperation;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.mdm.MDMApprovalApi;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationNote;
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
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionInputType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.mdm.bean.MDMEntryChangesPageModel;
import org.smartbit4all.bff.api.mdm.utility.MDMActions;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMEntryChangesPageApiImpl extends PageApiImpl<MDMEntryChangesPageModel>
    implements MDMEntryChangesPageApi {

  protected static final String LATEST_MODIFICATION_NOTE_KEY =
      MDMEntryChangesPageModel.LATEST_MODIFICATION_NOTE + StringConstant.DOT
          + MDMModificationNote.NOTE;

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

  /**
   * The page context is a useful object to encapsulate all the parameters necessary to execute the
   * actions of the page.
   */
  protected class PageContext {

    View view;
    MDMDefinition definition;
    List<MDMEntryApi> entryApisWithChanges;
    Map<String, SearchIndex<BranchedObjectEntry>> searchIndexAdminsByDescriptorName;
    List<MDMModificationNote> modificationNotes;
    MDMModificationNote latestModificationNote;

    public PageContext loadByView() {
      ObjectMapHelper parameters = parameters(view);
      setDefition(parameters);
      entryApisWithChanges = getDefinition().getDescriptors().keySet().stream()
          .map(descriptorName -> masterDataManagementApi.getApi(getDefinition().getName(),
              descriptorName))
          .filter(entryApi -> entryApi.getBranchingList().stream()
              .anyMatch(e -> e.getBranchingState() != BranchingStateEnum.NOP))
          .collect(toList());
      searchIndexAdminsByDescriptorName = getEntryApisWithChanges().stream()
          .collect(toMap(MDMEntryApi::getName,
              e -> collectionApi.searchIndex(getDefinition().getName(),
                  e.getDescriptor().getSearchIndexForEntries(),
                  BranchedObjectEntry.class)));
      setModificationNotes(getDefinition());

      return this;
    }


    public PageContext loadOnlyDefinitionByView() {
      setDefition(parameters(view));
      setModificationNotes(getDefinition());
      return this;
    }

    private final void setDefition(ObjectMapHelper parameters) {
      definition = masterDataManagementApi
          .getDefinition(parameters.require(PARAM_MDM_DEFINITION, String.class));
    }

    private void setModificationNotes(MDMDefinition definition) {
      ObjectNode state = objectApi.loadLatest(definition.getState());
      modificationNotes = state.getValueAsList(MDMModificationNote.class,
          MDMDefinitionState.GLOBAL_MODIFICATION, MDMModification.NOTES);
      latestModificationNote = !modificationNotes.isEmpty()
          ? modificationNotes.get(modificationNotes.size() - 1)
          : null;
    }

    public boolean checkAdmin() {
      return OrgUtils.securityPredicate(sessionApi, getDefinition().getAdminGroupName());
    }

    public boolean getBranchActive() {
      ObjectNode definitionNode = objectApi.loadLatest(getDefinition().getUri());
      ObjectNode defitionState = definitionNode.ref(MDMDefinition.STATE).get();
      MDMDefinitionState mdmDefinitionState = defitionState.getObject(MDMDefinitionState.class);
      return mdmDefinitionState.getGlobalModification() != null;
    }

    public URI getApprover() {
      ObjectNode definitionNode = objectApi.loadLatest(getDefinition().getUri());
      ObjectNode defitionState = definitionNode.ref(MDMDefinition.STATE).get();
      MDMDefinitionState mdmDefinitionState = defitionState.getObject(MDMDefinitionState.class);
      if (mdmDefinitionState.getGlobalModification() != null) {
        return mdmDefinitionState.getGlobalModification().getApprover();
      } else {
        return null;
      }

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
    URI approver = pageContext.getApprover();
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
            .visible(pageContext.getApprover() != null)));
  }

  protected void refreshActions(PageContext ctx) {
    if (ctx.getDefinition().getBranchingStrategy() == MDMBranchingStrategy.GLOBAL) {
      boolean isAdmin = ctx.checkAdmin();
      boolean branchActive = ctx.getBranchActive();
      // if API present, approving enabled
      boolean approvingEnabled = mdmApprovalApi != null;
      UiActionBuilder uiActions = UiActions.builder();
      if (approvingEnabled) {
        URI approver = ctx.getApprover();
        boolean underApproval = approver != null;
        boolean isApprover = approver != null && approver.equals(sessionApi.getUserUri());
        boolean canEdit = canEdit(isAdmin, underApproval, isApprover);

        uiActions
            .addIf(new UiAction().code(MDMActions.ACTION_START_EDITING).confirm(true),
                isAdmin, !branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_CANCEL_CHANGES).confirm(true),
                canEdit, !underApproval, branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_SEND_FOR_APPROVAL).confirm(true),
                isAdmin, !underApproval, branchActive)
            .addIf(new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_OK).confirm(true),
                isApprover, underApproval, branchActive)
            .addIf(
                new UiAction().code(MDMActions.ACTION_ADMIN_APPROVE_NOT_OK).confirm(true)
                    .input2Type(UiActionInputType.TEXTAREA),
                isApprover, underApproval, branchActive);
      } else {
        uiActions
            .addIf(MDMActions.ACTION_START_EDITING, isAdmin, !branchActive)
            .addIf(MDMActions.ACTION_FINALIZE_CHANGES, isAdmin, branchActive)
            .addIf(MDMActions.ACTION_CANCEL_CHANGES, isAdmin, branchActive);
      }

      uiActions.add(MDMActions.REFRESH);
      ctx.view.actions(uiActions.build());
    }
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
        GridModel entryGridModel =
            viewApi.getWidgetModelFromView(GridModel.class, ctx.view.getUuid(),
                entryApi.getName());
        if (entryGridModel == null) {
          createGridModelByEntryApi(ctx, entryApi);
        }

        List<BranchedObjectEntry> list = entryApi.getBranchingList();
        gridModelApi.setData(ctx.view.getUuid(), entryApi.getName(),
            createTableDataForGrid(ctx, entryApi, list));

        changesLayout
            .addComponentsItem(
                form(LayoutDirection.VERTICAL, label(null, entryApi.getDisplayNameList())))
            .addComponentsItem(grid(entryApi.getName()));
      });
    }

    ctx.view.putComponentLayoutsItem("changesLayout", changesLayout);
  }

  protected GridModel createGridModelByEntryApi(PageContext ctx, MDMEntryApi entryApi) {
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
            columns, ctx.getDefinition().getName(), entryApi.getName());
    if (columns.contains(BranchedObjectEntry.BRANCHING_STATE)) {
      List<String> newCols = new ArrayList<>();
      newCols.add(BranchedObjectEntry.BRANCHING_STATE);
      newCols.addAll(
          columns.stream()
              .filter(col -> !BranchedObjectEntry.BRANCHING_STATE.equals(col))
              .collect(toList()));
      entryGridModel.getView().setOrderedColumnNames(newCols);
    }
    GridModels.hideColumns(entryGridModel, BranchedObjectEntry.ORIGINAL_URI,
        BranchedObjectEntry.BRANCH_URI);
    if (columns.contains(MDMConstants.PROPERTY_URI)) {
      GridModels.hideColumns(entryGridModel, MDMConstants.PROPERTY_URI);
    }

    final List<GridView> gridViewOptions = entryApi.getDescriptor().getListPageGridViews();
    if (gridViewOptions != null && !gridViewOptions.isEmpty()) {
      entryGridModel.setView(gridViewOptions.get(0));
      entryGridModel.setAvailableViews(new ArrayList<>(gridViewOptions));
    }

    gridModelApi.initGridInView(ctx.view.getUuid(), entryApi.getName(), entryGridModel);
    gridModelApi.addGridPageCallback(ctx.view.getUuid(), entryApi.getName(), invocationApi
        .builder(MDMEntryChangesPageApi.class)
        .build(api -> api.addWidgetEntryGridActions(null, ctx.view.getUuid())));

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
          return objectApi.create(ctx.getDefinition().getName(), objectDefinition,
              objectDefinition.toMap(i));
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
    refreshActions(context);
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
    PageContext context = getContextByViewUUID(viewUuid, true);
    masterDataManagementApi.dropGlobal(context.getDefinition().getName());
    context.loadByView();
    refreshViewProperties(context);
  }

  protected void refreshViewProperties(PageContext context) {
    refreshActions(context);
    createLayout(context);
    context.view.constraint(createViewConstraint(context));
    setModel(context.view.getUuid(), createModel(context));
  }

  @Override
  public void finalizeChanges(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid, true);
    masterDataManagementApi.mergeGlobal(context.getDefinition().getName());
    context.loadByView();
    refreshViewProperties(context);
  }

  @Override
  public void sendForApproval(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid, false);
    if (mdmApprovalApi == null) {
      throw new IllegalStateException("Az admin jóváhagyó nem elérhető!");
    }
    String definition = context.getDefinition().getName();
    List<URI> approvers = mdmApprovalApi.getApprovers(definition);
    if (approvers == null || approvers.size() != 1) {
      throw new IllegalStateException("Az admin jóváhagyó nincs beállítva!");
    }
    masterDataManagementApi.sendForApprovalGlobal(
        context.getDefinition().getName(),
        approvers.get(0));
    context.loadByView();
    refreshViewProperties(context);
  }

  @Override
  public void adminApproveOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid, true);
    masterDataManagementApi.approvalAcceptedGlobal(context.getDefinition().getName());
    context.loadByView();
    refreshViewProperties(context);
  }

  @Override
  public void adminApproveNotOk(UUID viewUuid, UiActionRequest request) {
    PageContext context = getContextByViewUUID(viewUuid);
    String reason = actionRequestHelper(request).get(UiActions.INPUT2, String.class);
    masterDataManagementApi.approvalRejectedGlobal(context.getDefinition().getName(), reason);
    context.loadOnlyDefinitionByView();
    refreshViewProperties(context);
  }

  @Override
  public GridPage addWidgetEntryGridActions(GridPage page, UUID viewUuid) {
    page.getRows().forEach(row -> {
      String icon;
      Map<String, Object> map = (Map<String, Object>) row.getData();
      BranchingStateEnum state = (BranchingStateEnum) map.get(BranchedObjectEntry.BRANCHING_STATE);
      switch (state) {
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
      if (icon != null) {
        row.putIconsItem(BranchedObjectEntry.BRANCHING_STATE,
            Arrays.asList(new ImageResource()
                .source("smart-icon")
                .identifier(icon)));
      }
    });
    return page;
  }
}
