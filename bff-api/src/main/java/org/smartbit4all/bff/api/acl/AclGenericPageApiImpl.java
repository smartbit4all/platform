package org.smartbit4all.bff.api.acl;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textbox;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLObject;
import org.smartbit4all.api.org.bean.ACLOperation;
import org.smartbit4all.api.org.bean.ACLSubject;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.ViewEventApi;
import org.smartbit4all.api.view.bean.Style;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionFeedbackType;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewEventHandler;
import org.smartbit4all.api.view.bean.ViewEventHandler.ViewEventTypeEnum;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.generic.GenericPageApi;
import org.smartbit4all.bff.api.subjectselector.bean.AclGridConfig;
import org.smartbit4all.bff.api.subjectselector.bean.AclGridConfig.SelectionTypeEnum;
import org.smartbit4all.bff.api.subjectselector.bean.AclGridItem;
import org.smartbit4all.bff.api.subjectselector.bean.AclPageConfig;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class AclGenericPageApiImpl extends PageApiImpl<Object> implements AclGenericPageApi {

  private static final String COMMENT_FIELD = "comment";

  protected static final String PREFIX = AclGenericPageApi.class.getSimpleName();

  @Autowired
  protected AccessControlInternalApi accessControlInternalApi;

  @Autowired
  protected SubjectManagementApi subjectManagementApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  protected CollectionApi collectionApi;

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  protected InvocationApi invocationApi;

  public AclGenericPageApiImpl() {
    super(Object.class);
  }

  protected class PageContext {
    protected View view;
    private AclPageConfig config;
    private ObjectNode aclObjectNode;
    private ObjectNode originalAclObjectNode;

    // uri of modifiable ACLObjectNode
    public static final String VAR_ACLOBJECTNODE_URI = "VAR_ACLOBJECTNODE_URI";

    protected PageContext load(View view) {
      Objects.requireNonNull(view.getObjectUri(), "ACL object must be specified");
      this.view = view;
      ObjectMapHelper params = parameters(view);
      this.config = params.get(PARAM_ACL_PAGE_CONFIG, AclPageConfig.class);
      this.originalAclObjectNode = loadOriginalAclObjectNode(params,
          Boolean.TRUE.equals(config.getLoadExactVersion()));
      if (Boolean.TRUE.equals(config.getSaveDirectly())) {
        // both nodes are the same
        this.aclObjectNode = this.originalAclObjectNode;
      } else {
        ObjectMapHelper vars = variables(view);
        URI aclObjectUri = vars.get(VAR_ACLOBJECTNODE_URI, URI.class);
        if (aclObjectUri == null) {
          // first load, should save original as new
          aclObjectUri = objectApi.saveAsNew(
              "tmp",
              originalAclObjectNode.getObject(ACLObject.class));
          vars.put(VAR_ACLOBJECTNODE_URI, aclObjectUri);
        }
        this.aclObjectNode = objectApi.loadLatest(aclObjectUri);
      }
      return this;
    }

    private ObjectNode loadOriginalAclObjectNode(ObjectMapHelper params, boolean loadExactVersion) {
      URI aclObjectUri = null;
      if (!Strings.isNullOrEmpty(this.config.getAclObjectUriParam())) {
        aclObjectUri = params.get(this.config.getAclObjectUriParam(), URI.class);
        Objects.requireNonNull(aclObjectUri, "aclObjectUri parameter specified but not present");
      } else {
        aclObjectUri = getView().getObjectUri();
      }
      ObjectNode result;
      if (loadExactVersion) {
        result = objectApi.load(aclObjectUri, getView().getBranchUri());
      } else {
        result = objectApi.loadLatest(aclObjectUri, getView().getBranchUri());
      }
      Objects.requireNonNull(result, "ACL object not found");
      return result;
    }

    public AclGridConfig findGridConfig(String name) {
      Objects.requireNonNull(name, "GridConfig name cannot be null");
      return getConfig().getGridConfigs().stream()
          .filter(c -> name.equals(getGridId(c)))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Invalid GridConfigName"));
    }

    protected String getSubjectSelectorViewName() {
      return getConfig().getSubjectSelectorViewName() != null
          ? getConfig().getSubjectSelectorViewName()
          : PlatformViewNames.SUBJECT_SELECTOR_PAGE;
    }

    protected String getUserSelectorViewName() {
      return getConfig().getUserSelectorViewName() != null ? getConfig().getUserSelectorViewName()
          : PlatformViewNames.USER_SELECTOR_PAGE;
    }

    protected InvocationRequest getSubjectSelectionCallback(String gridId) {
      return getConfig().getSelectionCallback() != null ? getConfig().getSelectionCallback()
          : invocationApi.builder(AclGenericPageApi.class)
              .build(api -> api.handleSubjectSelected(
                  getView().getUuid(),
                  Invocations.listOf(Collections.emptyList(), Subject.class),
                  gridId));
    }

    protected InvocationRequest getUserSelectionCallback(String gridId) {
      return getConfig().getSelectionCallback() != null ? getConfig().getSelectionCallback()
          : invocationApi.builder(AclGenericPageApi.class)
              .build(api -> api.handleUserSelected(
                  getView().getUuid(),
                  Invocations.listOf(Collections.emptyList(), URI.class),
                  gridId));
    }

    public AclPageConfig getConfig() {
      return config;
    }

    public ObjectNode getAclObjectNode() {
      return aclObjectNode;
    }

    public ObjectNode getOriginalAclObjectNode() {
      return originalAclObjectNode;
    }

    public View getView() {
      return view;
    }

  }

  protected PageContext context(View view) {
    return new PageContext().load(view);
  }

  protected PageContext context(UUID viewUuid) {
    return context(viewApi.getView(viewUuid));
  }

  @Override
  public Object initModel(View view) {
    Map<String, Object> model = createModel(view);
    PageContext ctx = context(view);
    SmartComponentLayoutDefinition layout = createLayout(ctx, model);
    view.putComponentLayoutsItem("default", layout);
    addGridsToLayout(ctx, model, layout);

    return model;
  }

  /**
   * Create the view's initial model.
   *
   * @param view View to be opened.
   * @return
   */
  protected Map<String, Object> createModel(View view) {
    return new HashMap<>();
  }

  /**
   * Create initial layout based on the view and model.
   *
   * @param ctx PageContext of view to be opened.
   * @param model View's already created model.
   * @return
   */
  protected SmartComponentLayoutDefinition createLayout(PageContext ctx,
      Map<String, Object> model) {
    return container(LayoutDirection.VERTICAL);
  }

  /**
   * Add grids to already created layout based on view and model.
   *
   * @param ctx PageContext of view to be or already opened.
   * @param model View's already created model.
   * @param layout View's already created layout.
   */
  protected void addGridsToLayout(PageContext ctx, Map<String, Object> model,
      SmartComponentLayoutDefinition layout) {
    View view = ctx.getView();
    ACLObject aclObject = ctx.getAclObjectNode().getObject(ACLObject.class);

    UUID viewUuid = view.getUuid();
    for (AclGridConfig config : ctx.getConfig().getGridConfigs()) {
      ACL acl = accessControlInternalApi.getAclFromObject(aclObject, config.getAclName());
      initGridInView(ctx, config);
      refreshGrid(viewUuid, acl, config);
      layout.addComponentsItem(createGridLayout(view.getUuid(), getGridId(config)));
    }
  }

  protected void initGridInView(PageContext ctx, AclGridConfig config) {
    View view = ctx.getView();
    String gridId = getGridId(config);
    UUID viewUuid = view.getUuid();
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    if (gridModel == null) {
      createGridModel(viewUuid, gridId, config.getSearchPageConfig());
    }
    if (isEnableModify(ctx, gridId) && isEnableAdd(ctx, gridId)) {
      UiAction addSubjectAction = new UiAction()
          .code(ADD_SUBJECT)
          .model(true)
          .toolbar(gridId + UiActions.TOOLBAR_SUFFIX)
          .identifier(gridId)
          .descriptor(createAddActionDescriptor());
      if (!view.getActions().contains(addSubjectAction)) {
        view.addActionsItem(addSubjectAction);
      }
    }
  }

  protected UiActionDescriptor createAddActionDescriptor() {
    return new UiActionDescriptor()
        .icon("Plus")
        .title(" ")
        .type(UiActionButtonType.ICON)
        .color(UiActions.Color.ACCENT);
  }

  protected GridModel createGridModel(UUID viewUuid, String gridId,
      SearchPageConfig searchPageConfig) {

    List<String> columns;
    GridModel gridModel;
    if (searchPageConfig != null) {
      GridView gridView = searchPageConfig.getGridViewOptions().get(0);
      columns = new ArrayList<>(
          gridView.getOrderedColumnNames());
      if (!columns.contains(AclGridItem.SUBJECT)) {
        columns.add(AclGridItem.SUBJECT);
      }
      EntityDefinition entityDefinition = collectionApi
          .searchIndex(
              searchPageConfig.getSearchIndexSchema(),
              searchPageConfig.getSearchIndexName())
          .getDefinition().getDefinition();
      gridModel = gridModelApi
          .createGridModel(entityDefinition, columns, User.class.getSimpleName());
      if (gridView.getDescriptor() != null) {
        gridModel.setView(gridView);
      }
    } else {
      columns = Arrays.asList(AclGridItem.NAME, AclGridItem.COMMENT, AclGridItem.SUBJECT);
      if (!columns.contains(AclGridItem.SUBJECT)) {
        columns.add(AclGridItem.SUBJECT);
      }
      gridModel = gridModelApi.createGridModel(
          AclGridItem.class,
          columns);
    }
    GridModels.hideColumns(gridModel, AclGridItem.SUBJECT);

    gridModel.getView().getDescriptor().showEditColumns(false);
    gridModel.paginator(true);
    gridModelApi.initGridInView(viewUuid, gridId, gridModel);

    gridModelApi.addGridPageCallback(viewUuid, gridId,
        invocationApi
            .builder(AclGenericPageApi.class)
            .build(api -> api.addGridActions(null, viewUuid, gridId)));

    return gridModel;
  }

  protected void refreshGrid(UUID viewUuid, String gridId) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    ACLObject aclObject = ctx.getAclObjectNode().getObject(ACLObject.class);
    ACL acl = accessControlInternalApi.getAclFromObject(aclObject, gridConfig.getAclName());

    refreshGrid(viewUuid, acl, gridConfig);
  }

  protected void refreshGrid(UUID viewUuid, ACL acl, AclGridConfig config) {
    String gridId = getGridId(config);
    List<ACLSubject> subjects = getSubjects(viewUuid, acl, config);
    SearchPageConfig searchPageConfig = config.getSearchPageConfig();
    if (searchPageConfig != null) {
      Stream<ObjectNode> objects = subjects.stream()
          .map(subject -> objectApi.create(null, subject));
      TableData<?> result = collectionApi
          .searchIndex(
              searchPageConfig.getSearchIndexSchema(),
              searchPageConfig.getSearchIndexName())
          .executeSearchOnNodes(objects, null);
      gridModelApi.setData(viewUuid, gridId, result);
    } else {
      List<AclGridItem> items = subjects.stream()
          .map(s -> {
            Subject subject = s.getSubject();
            List<String> names = subjectManagementApi.getDisplayValue(config.getAclModel(),
                Arrays.asList(subject));
            String name = names.size() == 1 ? names.get(0) : "N/A";
            return new AclGridItem()
                .name(name)
                .subject(subject)
                .comment(s.getOperation() == null ? "" : s.getOperation().getComment());
          })
          .collect(toList());
      gridModelApi.setData(viewUuid, gridId, AclGridItem.class, items);
    }
  }

  protected List<ACLSubject> getSubjects(UUID viewUuid, ACL acl, AclGridConfig config) {
    return accessControlInternalApi.getSubjects(acl, config.getOperation());
  }

  protected SmartComponentLayoutDefinition createGridLayout(UUID viewUuid, String gridId) {
    return container(LayoutDirection.VERTICAL)
        .addComponentsItem(form(LayoutDirection.VERTICAL,
            label(null, localeSettingApi.get(PREFIX, gridId))))
        .addComponentsItem(grid(gridId));
  }

  @Override
  public GridPage addGridActions(GridPage page, UUID viewUuid, String gridId) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);

    boolean enableModify = isEnableModify(ctx, gridId);
    page.getRows().forEach(row -> {
      if (enableModify) {
        if (Boolean.TRUE.equals(gridConfig.getHasComment())) {
          row.addActionsItem(new UiAction()
              .code(EDIT_COMMENT)
              .descriptor(new UiActionDescriptor()
                  .title(localeSettingApi.get(PREFIX, EDIT_COMMENT))));
        }
        if (isEnableDelete(ctx, gridId)) {
          row.addActionsItem(new UiAction()
              .code(DELETE_SUBJECT)
              .descriptor(new UiActionDescriptor()
                  .title(localeSettingApi.get(PREFIX, DELETE_SUBJECT))));
        }

      }
      addRowAction(ctx, row, viewUuid, gridId, enableModify);
    });
    return page;
  }

  protected void addRowAction(PageContext ctx, GridRow row, UUID viewUuid, String gridId,
      boolean enableModify) {}

  protected boolean isEnableAdd(PageContext ctx, String gridId) {
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    // default enabled, need explicit false to disable ADD operation
    return !Boolean.FALSE.equals(gridConfig.getAddEnabled());
  }

  protected boolean isEnableDelete(PageContext ctx, String gridId) {
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    // default enabled, need explicit false to disable DELETE operation
    return !Boolean.FALSE.equals(gridConfig.getDeleteEnabled());
  }

  protected boolean isEnableModify(PageContext ctx, String gridId) {
    return true;
  }

  @Override
  public void performAddSubject(UUID viewUuid, UiActionRequest request) {
    setModel(viewUuid, extractClientModel(request));
    PageContext ctx = context(viewUuid);
    String gridId = request.getIdentifier();
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    Long maxNoOfRows = gridConfig.getMaxNoOfRows();
    if (maxNoOfRows != null) {
      GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
      int size = gridModel.getPage().getRows().size();
      if (size >= maxNoOfRows) {
        String message = localeSettingApi.get(PREFIX, "TOO_MANY_ROWS");
        message = MessageFormat.format(
            message,
            localeSettingApi.get(PREFIX, gridId), maxNoOfRows);
        throw new BusinessLogicException(message);
      }
    }
    SelectionTypeEnum selectionType = gridConfig.getSelectionType();
    if (selectionType == null || selectionType == SelectionTypeEnum.SUBJECT) {
      viewApi.showView(new View()
          .viewName(ctx.getSubjectSelectorViewName())
          .putParametersItem(SubjectSelectorPageApi.PARAM_SUBJECT_MODEL_NAME,
              gridConfig.getAclModel())
          .putParametersItem(SubjectSelectorPageApi.PARAM_SUBJECT_TYPES,
              gridConfig.getSubjectTypes())
          .putParametersItem(SubjectSelectorPageApi.PARAM_SELECTION_MODE,
              gridConfig.getSelectionMode())
          .putParametersItem(SubjectSelectorPageApi.PARAM_SELECTION_CALLBACK,
              ctx.getSubjectSelectionCallback(gridId))
          .type(ViewType.DIALOG));
    } else if (selectionType == SelectionTypeEnum.USER) {
      viewApi.showView(new View()
          .viewName(ctx.getUserSelectorViewName())
          // .putParametersItem(UserSelectorPageApi.PARAM_SUBJECT_MODEL_NAME,
          // gridConfig.getAclModel())
          .putParametersItem(UserSelectorPageApi.PARAM_SUBJECT_VALUES,
              gridConfig.getSubjectValues())
          .putParametersItem(UserSelectorPageApi.PARAM_SELECTION_MODE,
              gridConfig.getSelectionMode())
          .putParametersItem(UserSelectorPageApi.PARAM_SELECTION_CALLBACK,
              ctx.getUserSelectionCallback(gridId))
          .type(ViewType.DIALOG));
    }
  }

  @Override
  public void performDeleteSubject(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);
    if (gridRow.isPresent()) {
      Subject subject = objectApi.asType(Subject.class,
          GridModels.getValueFromGridRow(gridRow.get(), AclGridItem.SUBJECT));
      handleSubjectDeleted(viewUuid, gridId, subject);
    }
  }

  protected void handleSubjectDeleted(UUID viewUuid, String gridId, Subject subject) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    if (subject != null && subject.getRef() != null) {
      ctx.getAclObjectNode().modify(ACLObject.class, aclObject -> {
        ACL acl = accessControlInternalApi.getAclFromObject(aclObject, gridConfig.getAclName());
        String operation = gridConfig.getOperation();
        List<ACLSubject> subjects = accessControlInternalApi.getSubjects(acl, operation);
        boolean anyChange = subjects.removeIf(
            sub -> objectApi.equalsIgnoreVersion(sub.getSubject().getRef(), subject.getRef()));
        if (anyChange) {
          accessControlInternalApi.applySubjects(acl, subjects, operation);
        }
        refreshGrid(viewUuid, acl, gridConfig);
        return aclObject;
      });
      objectApi.save(ctx.getAclObjectNode());
    }
  }

  @Override
  public void performEditComment(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);

    String currentComment = StringConstant.EMPTY;
    if (gridRow.isPresent()) {
      Subject subject = objectApi.asType(Subject.class,
          GridModels.getValueFromGridRow(gridRow.get(), AclGridItem.SUBJECT));
      if (subject != null && subject.getRef() != null) {
        ACLObject aclObject = ctx.getAclObjectNode().getObject(ACLObject.class);
        ACL acl = accessControlInternalApi.getAclFromObject(aclObject, gridConfig.getAclName());
        String operation = gridConfig.getOperation();
        List<ACLSubject> subjects = accessControlInternalApi.getSubjects(acl, operation);
        Optional<ACLSubject> toUpdate = subjects.stream()
            .filter(
                sub -> objectApi.equalsIgnoreVersion(sub.getSubject().getRef(), subject.getRef()))
            .findFirst();
        if (toUpdate.isPresent()) {
          currentComment = toUpdate.get().getOperation().getComment();
        }
      }
    }
    showEditComment(currentComment, invocationApi.builder(AclGenericPageApi.class)
        .build(api -> api.saveComment(
            null,
            null,
            viewUuid,
            gridId,
            rowId)));
  }

  @Override
  public void saveComment(UUID dialogUuid, UiActionRequest request, UUID viewUuid, String gridId,
      String rowId) {

    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);
    if (gridRow.isPresent()) {
      String comment = getCommentField(request);
      Subject subject = objectApi.asType(Subject.class,
          GridModels.getValueFromGridRow(gridRow.get(), AclGridItem.SUBJECT));
      if (subject != null && subject.getRef() != null) {
        ctx.getAclObjectNode().modify(ACLObject.class, aclObject -> {
          ACL acl = accessControlInternalApi.getAclFromObject(aclObject, gridConfig.getAclName());
          String operation = gridConfig.getOperation();
          List<ACLSubject> subjects = accessControlInternalApi.getSubjects(acl, operation);
          Optional<ACLSubject> toUpdate = subjects.stream()
              .filter(
                  sub -> objectApi.equalsIgnoreVersion(sub.getSubject().getRef(), subject.getRef()))
              .findFirst();
          if (toUpdate.isPresent()) {
            toUpdate.get().getOperation().setComment(comment);
            accessControlInternalApi.applySubjects(acl, subjects, gridConfig.getOperation());
          }
          refreshGrid(viewUuid, acl, gridConfig);
          return aclObject;
        });
        objectApi.save(ctx.getAclObjectNode());
      }


    }

    viewApi.closeView(dialogUuid);
  }

  private String getCommentField(UiActionRequest request) {
    return ((Map<String, String>) request.getParams().get(UiActions.MODEL)).get(COMMENT_FIELD);
  }

  protected void showEditComment(String currentComment, InvocationRequest callback) {

    SmartComponentLayoutDefinition layout =
        form(LayoutDirection.HORIZONTAL,
            textbox(COMMENT_FIELD,
                localeSettingApi.get(PREFIX, COMMENT_FIELD)));
    Map<String, String> model = new HashMap<>();
    model.put(COMMENT_FIELD, currentComment != null ? currentComment : StringConstant.EMPTY);
    List<ViewEventHandler> eventHandlers = new ArrayList<>();
    eventHandlers.add(
        new ViewEventHandler()
            .viewEventType(ViewEventTypeEnum.INSTEAD)
            .addPathItem(ViewEventApi.ACTION)
            .addPathItem(SAVE_COMMENT)
            .invocationRequest(callback));
    viewApi.showView(new View()
        .viewName(PlatformViewNames.GENERIC_PAGE)
        .type(ViewType.DIALOG)
        .parentStyle(new Style()
            .putStyleItem("min-width", "40vw"))
        .putComponentLayoutsItem(ObjectLayoutApi.DEFAULT_LAYOUT, layout)
        .putParametersItem(GenericPageApi.PARAM_MODEL, model)
        .actions(UiActions.builder()
            .add(new UiAction()
                .code(GenericPageApi.ACTION_CLOSE_VIEW)
                .submit(false)
                .descriptor(new UiActionDescriptor()
                    .title(localeSettingApi.get(PREFIX, GenericPageApi.ACTION_CLOSE_VIEW))
                    .color(UiActions.Color.ACCENT)
                    .type(UiActionButtonType.NORMAL)
                    .feedbackType(UiActionFeedbackType.NONE)))
            .add(new UiAction()
                .code(SAVE_COMMENT)
                .submit(true)
                .descriptor(new UiActionDescriptor()
                    .title(localeSettingApi.get(PREFIX, SAVE_COMMENT))
                    .color(UiActions.Color.PRIMARY)
                    .type(UiActionButtonType.RAISED)
                    .feedbackType(UiActionFeedbackType.NONE)))
            .build())
        .eventHandlers(eventHandlers));
  }

  @Override
  public void handleSubjectSelected(UUID viewUuid, List<Subject> subjects, String gridId) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);

    ACL acl = accessControlInternalApi.getAclFromObject(
        ctx.getAclObjectNode().getObject(ACLObject.class),
        gridConfig.getAclName());
    checkForExistingSubjects(viewUuid, acl, subjects, gridConfig);
    if (Boolean.TRUE.equals(gridConfig.getHasComment())) {
      showEditComment("", invocationApi.builder(AclGenericPageApi.class)
          .build(api -> api.saveSubjectSelectedWithComment(
              null,
              null,
              viewUuid,
              Invocations.listOf(subjects, Subject.class),
              gridId)));
    } else {
      saveSubjectSelectedInternal(viewUuid, subjects, gridId, null);
    }
  }

  protected void checkForExistingSubjects(UUID viewUuid, ACL acl, List<Subject> subjects,
      AclGridConfig gridConfig) {
    for (Subject subject : subjects) {
      if (checkSubjectIsAlreadyInAcl(acl, subject.getRef(), gridConfig.getOperation())) {
        String message = localeSettingApi.get(PREFIX, "ALREADY_SELECTED");
        List<String> names = subjectManagementApi.getDisplayValue(gridConfig.getAclModel(),
            Arrays.asList(subject));
        String name = names.size() == 1 ? names.get(0) : "N/A";
        message = MessageFormat.format(
            message,
            name, localeSettingApi.get(PREFIX, getGridId(gridConfig)));
        throw new BusinessLogicException(message);
      }
    }
  }

  @Override
  public void saveSubjectSelectedWithComment(UUID dialogUuid, UiActionRequest request,
      UUID viewUuid, List<Subject> subjects, String gridId) {
    String comment = getCommentField(request);
    saveSubjectSelectedInternal(viewUuid, subjects, gridId, comment);
    viewApi.closeView(dialogUuid);
  }

  private void saveSubjectSelectedInternal(UUID viewUuid, List<Subject> subjects, String gridId,
      String comment) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);

    ctx.getAclObjectNode().modify(ACLObject.class, aclObject -> {
      ACL acl = accessControlInternalApi.getAclFromObject(aclObject, gridConfig.getAclName());
      String operation = gridConfig.getOperation();
      List<ACLSubject> currentSubjects = accessControlInternalApi.getSubjects(acl, operation);
      checkForExistingSubjects(viewUuid, acl, subjects, gridConfig);
      for (Subject subject : subjects) {
        currentSubjects.add(
            new ACLSubject()
                .operation(
                    new ACLOperation()
                        .name(operation)
                        .comment(comment))
                .subject(subject));
      }

      accessControlInternalApi.applySubjects(acl, currentSubjects, gridConfig.getOperation());

      refreshGrid(viewUuid, acl, gridConfig);
      return aclObject;
    });

    objectApi.save(ctx.getAclObjectNode());
  }

  private boolean checkSubjectIsAlreadyInAcl(ACL acl, URI subjectUri, String operation) {
    return accessControlInternalApi.getSubjects(acl, operation).stream()
        .map(aclSubject -> aclSubject.getSubject().getRef())
        .anyMatch(uri -> objectApi.equalsIgnoreVersion(uri, subjectUri));
  }

  @Override
  public void handleUserSelected(UUID viewUuid, List<URI> userUriList, String gridId) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);

    // TODO maybe this should be done via SubjectManagementApi?
    List<Subject> subjects = userUriList.stream()
        .map(uri -> new Subject()
            .model(gridConfig.getAclModel())
            .type(User.class.getName())
            .ref(uri))
        .collect(toList());
    handleSubjectSelected(viewUuid, subjects, gridId);
  }

  protected String getGridId(AclGridConfig config) {
    String gridId = config.getGridId();
    if (!Strings.isNullOrEmpty(gridId)) {
      return gridId;
    }
    return config.getAclName();
  }
}
