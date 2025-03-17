package org.smartbit4all.bff.api.org;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.SubjectAssociationModification;
import org.smartbit4all.api.org.bean.SubjectAssociationModification.OperationEnum;
import org.smartbit4all.api.org.bean.SubjectAssociationModificationModel;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.MessageData;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.acl.SubjectSelectorPageApi;
import org.smartbit4all.bff.api.subjectselector.bean.AclGridItem;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectAssignerPageConfig;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectAssignerPageModel;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectChanges;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectGridConfig;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.google.common.base.Strings;

public class SubjectAssignerPageApiImpl extends PageApiImpl<SubjectAssignerPageModel>
    implements SubjectAssignerPageApi {

  protected static final String PREFIX = SubjectAssignerPageApi.class.getSimpleName();

  @Autowired
  protected SubjectManagementApi subjectManagementApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  protected InvocationApi invocationApi;

  @Autowired
  protected CollectionApi collectionApi;


  protected class PageContext {
    protected View view;
    private SubjectAssignerPageConfig config;
    private Map<SubjectGridConfig, List<Subject>> subjectsByGridConfig;

    protected PageContext load(View view) {
      Objects.requireNonNull(view.getObjectUri(), "Object uri must be specified");
      this.view = view;
      ObjectMapHelper params = parameters(view);
      this.config = params.get(PARAM_SUBJECT_PAGE_CONFIG, SubjectAssignerPageConfig.class);
      return this;
    }

    public Map<SubjectGridConfig, List<Subject>> getSubjectsByGridConfig(List<Subject> subjects) {
      if (subjectsByGridConfig == null) {
        subjectsByGridConfig = config.getSubjectGridConfigs().stream()
            .collect(Collectors.toMap(
                gridConfig -> gridConfig,
                gridConfig -> subjects.stream()
                    .filter(subject -> objectApi.asType(SubjectGridConfig.class, gridConfig)
                        .getSubjectType().equals(subject.getType()))
                    .collect(Collectors.toList()),
                (existing, replacement) -> existing,
                LinkedHashMap::new));
      }
      return subjectsByGridConfig;
    }


    public SubjectAssignerPageConfig getConfig() {
      return config;
    }

    public View getView() {
      return view;
    }

    public Map<SubjectGridConfig, List<Subject>> findGridConfigWithSubjects(String name,
        List<Subject> subjects) {
      Objects.requireNonNull(name, "GridConfig name cannot be null");
      return getSubjectsByGridConfig(subjects).entrySet().stream()
          .filter(e -> name.equals(getGridId(e.getKey())))
          .findFirst()
          .<Map<SubjectGridConfig, List<Subject>>>map(
              entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))
          .orElseThrow(() -> new IllegalArgumentException("Invalid GridConfigName"));
    }

    public SubjectGridConfig findGridConfig(String name) {
      Objects.requireNonNull(name, "GridConfig name cannot be null");
      return getConfig().getSubjectGridConfigs().stream()
          .filter(c -> name.equals(getGridId(c)))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Invalid GridConfigName"));
    }

    protected InvocationRequest getSubjectSelectionCallback(String gridId) {
      return invocationApi.builder(SubjectAssignerPageApi.class)
          .build(api -> api.handleSubjectSelected(
              getView().getUuid(),
              Invocations.listOf(Collections.emptyList(), Subject.class),
              gridId));
    }
  }

  protected PageContext context(View view) {
    return new PageContext().load(view);
  }

  protected PageContext context(UUID viewUuid) {
    return context(viewApi.getView(viewUuid));
  }

  public SubjectAssignerPageApiImpl() {
    super(SubjectAssignerPageModel.class);
  }

  @Override
  public SubjectAssignerPageModel initModel(View view) {
    PageContext ctx = context(view);
    UiActions.add(view, new UiAction().code(DEFAULT_CLOSE)
        .descriptor(new UiActionDescriptor().title(localeSettingApi.get("close"))));
    UiActions.add(view, new UiAction().code(SAVE)
        .descriptor(new UiActionDescriptor().title(localeSettingApi.get("save"))));
    SmartComponentLayoutDefinition layout = createLayout(ctx);
    view.putComponentLayoutsItem("default", layout);
    List<Subject> subjects = getSubjects(view, ctx);
    Map<SubjectGridConfig, List<Subject>> subjectsByGridConfig =
        ctx.getSubjectsByGridConfig(subjects);

    SubjectAssignerPageModel model = new SubjectAssignerPageModel();
    subjectsByGridConfig.entrySet().stream().forEach(subjectByConfig -> {

      SubjectAssociationModificationModel assocationModel =
          new SubjectAssociationModificationModel().userUri(view.getObjectUri())
              .subjectAssociations(new ArrayList<>());
      subjectByConfig.getValue().stream().forEach(subject -> assocationModel
          .addSubjectAssociationsItem(new SubjectAssociationModification().subject(subject)));
      model.addSubjectChangesItem(
          new SubjectChanges().subjectGridConfig(subjectByConfig.getKey())
              .subjectAssociationModel(assocationModel));
    });
    addGridsToLayout(ctx, model, layout);

    return model;
  }

  private List<Subject> getSubjects(View view, PageContext ctx) {
    List<Subject> subjects =
        subjectManagementApi.getSubjectsOfUser(ctx.config.getSubjectModel(), view.getObjectUri());
    return subjects;
  }

  protected SmartComponentLayoutDefinition createLayout(PageContext ctx) {
    return container(LayoutDirection.VERTICAL);
  }

  protected void addGridsToLayout(PageContext ctx,
      SubjectAssignerPageModel model, SmartComponentLayoutDefinition layout) {
    UUID viewUuid = ctx.getView().getUuid();
    for (SubjectChanges changes : model.getSubjectChanges()) {
      initGridInView(ctx, changes);
      refreshGrid(viewUuid, changes.getSubjectGridConfig(),
          changes.getSubjectAssociationModel().getSubjectAssociations());
      layout.addComponentsItem(
          createGridLayout(viewUuid, getGridId(changes.getSubjectGridConfig())));

    }
  }

  protected SmartComponentLayoutDefinition createGridLayout(UUID viewUuid, String gridId) {
    return container(LayoutDirection.VERTICAL)
        .addComponentsItem(form(LayoutDirection.VERTICAL,
            label(null, localeSettingApi.get(PREFIX, gridId))))
        .addComponentsItem(grid(gridId));
  }

  protected void refreshGrid(UUID viewUuid, SubjectGridConfig config,
      List<SubjectAssociationModification> associations) {
    Objects.requireNonNull(config, "config cannot be null!");

    String gridId = getGridId(config);
    SearchPageConfig searchPageConfig = config.getSearchPageConfig();
    if (searchPageConfig != null) {
      List<ObjectNode> objects = new ArrayList<>();
      if (!ObjectUtils.isEmpty(associations)) {
        objects = associations.stream()
            .map(subject -> objectApi.create(null, subject)).collect(Collectors.toList());
      }
      TableData<?> result = collectionApi
          .searchIndex(
              PlatformApiConfig.DEFAULT_SCHEME,
              SubjectAssociationModification.class.getSimpleName())
          .executeSearchOnNodes(objects.stream(), null);
      gridModelApi.setData(viewUuid, gridId, result);
    }
  }

  protected void initGridInView(PageContext ctx, SubjectChanges changes) {
    View view = ctx.getView();
    String gridId = getGridId(changes.getSubjectGridConfig());
    UUID viewUuid = view.getUuid();
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    if (gridModel == null) {
      createGridModel(viewUuid, gridId, changes.getSubjectGridConfig().getSearchPageConfig());
    }

    UiAction addSubjectAction = new UiAction()
        .code(ADD_SUBJECT)
        .model(true)
        .toolbar(gridId + UiActions.TOOLBAR_SUFFIX)
        .identifier(gridId)
        .descriptor(new UiActionDescriptor()
            .icon("Plus")
            .title(" ")
            .type(UiActionButtonType.ICON)
            .color(UiActions.Color.ACCENT));
    if (!view.getActions().contains(addSubjectAction)) {
      view.addActionsItem(addSubjectAction);
    }
  }

  protected GridModel createGridModel(UUID viewUuid, String gridId,
      SearchPageConfig searchPageConfig) {
    Objects.requireNonNull(searchPageConfig, "searchPageConfig cannot be null!");

    List<String> columns;
    GridModel gridModel;

    GridView gridView = searchPageConfig.getGridViewOptions().get(0);

    SearchIndex<?> searchIndex = collectionApi
        .searchIndex(
            PlatformApiConfig.DEFAULT_SCHEME,
            SubjectAssociationModification.class.getSimpleName());
    EntityDefinition entityDefinition = searchIndex
        .getDefinition().getDefinition();

    columns = entityDefinition.allProperties().stream()
        .map(Property::getName)
        .collect(toList());

    gridModel = gridModelApi
        .createGridModel(entityDefinition, columns, searchIndex.logicalSchema());

    if (gridView.getDescriptor() != null) {
      gridModel.setView(gridView);
    }

    gridModel.getView().getDescriptor().showEditColumns(false);
    gridModel.paginator(true);
    GridModels.hideColumns(gridModel, SubjectAssociationModification.SUBJECT);
    gridModelApi.initGridInView(viewUuid, gridId, gridModel);
    gridModelApi.addGridPageCallback(viewUuid, gridId,
        invocationApi
            .builder(SubjectAssignerPageApi.class)
            .build(api -> api.addGridActions(null, viewUuid, gridId)));

    return gridModel;
  }

  @Override
  public GridPage addGridActions(GridPage page, UUID viewUuid, String gridId) {
    PageContext ctx = context(viewUuid);

    page.getRows().forEach(row -> {
      Map<String, Object> map = (Map<String, Object>) row.getData();

      OperationEnum operation =
          (OperationEnum) map.get(SubjectAssociationModification.OPERATION);
      if (OperationEnum.REMOVE.equals(operation)) {
        row.addActionsItem(new UiAction()
            .code(DELETE_UNDO)
            .descriptor(new UiActionDescriptor()
                .title(localeSettingApi.get(PREFIX, DELETE_UNDO))));
      } else if (OperationEnum.ADD.equals(operation)) {
        row.addActionsItem(new UiAction()
            .code(ADD_UNDO)
            .descriptor(new UiActionDescriptor()
                .title(localeSettingApi.get(PREFIX, ADD_UNDO))));
      } else {
        row.addActionsItem(new UiAction()
            .code(DELETE_SUBJECT)
            .descriptor(new UiActionDescriptor()
                .title(localeSettingApi.get(PREFIX, DELETE_SUBJECT))));
      }
      addRowAction(ctx, row, viewUuid, gridId);

      String icon;
      if (operation != null) {
        icon = setIconToSubject(operation);
        if (icon != null) {
          row.putIconsItem(SubjectAssociationModification.OPERATION,
              Arrays.asList(new ImageResource()
                  .source("smart-icon")
                  .identifier(icon)));
        }
      }
    });
    return page;
  }

  protected String setIconToSubject(OperationEnum operation) {
    String icon;
    switch (operation) {
      case ADD:
        icon = "add_circle";
        break;
      case REMOVE:
        icon = "cancel";
        break;
      default:
        icon = "radio_button_unchecked";
        break;
    }
    return icon;
  }

  protected void addRowAction(PageContext ctx, GridRow row, UUID viewUuid, String gridId) {}

  @Override
  public void performAddSubject(UUID viewUuid, UiActionRequest request) {
    setModel(viewUuid, extractClientModel(request));
    PageContext ctx = context(viewUuid);
    String gridId = request.getIdentifier();
    SubjectGridConfig gridConfig = ctx.findGridConfig(gridId);
    List<String> subjectTypes = new ArrayList<>();
    subjectTypes.add(gridConfig.getSubjectType());
    viewApi.showView(new View()
        .viewName(PlatformViewNames.SUBJECT_SELECTOR_PAGE)
        .putParametersItem(SubjectSelectorPageApi.PARAM_SUBJECT_MODEL_NAME,
            ctx.config.getSubjectModel())
        .putParametersItem(SubjectSelectorPageApi.PARAM_SUBJECT_TYPES,
            subjectTypes)
        .putParametersItem(SubjectSelectorPageApi.PARAM_SELECTION_MODE,
            gridConfig.getSelectionMode())
        .putParametersItem(SubjectSelectorPageApi.PARAM_SELECTION_CALLBACK,
            ctx.getSubjectSelectionCallback(gridId))
        .type(ViewType.DIALOG));
  }



  @Override
  public void performSave(UUID viewUuid, UiActionRequest request) {
    SubjectAssignerPageModel model = getModel(viewUuid);
    PageContext pageContext = context(viewUuid);
    List<SubjectChanges> subjectChanges = model.getSubjectChanges();
    SubjectAssociationModificationModel associationModel = new SubjectAssociationModificationModel()
        .userUri(subjectChanges.get(0).getSubjectAssociationModel().getUserUri());
    associationModel.subjectAssociations(subjectChanges.stream().flatMap(changes -> {
      if (ObjectUtils.isEmpty(changes.getSubjectAssociationModel().getSubjectAssociations())) {
        return null;
      }
      return changes.getSubjectAssociationModel().getSubjectAssociations().stream();
    }).filter(Objects::nonNull).collect(Collectors.toList()));
    subjectManagementApi.processSubjectChanges(pageContext.config.getSubjectModel(),
        associationModel);
    viewApi.closeView(viewUuid);


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
      handleSubjectDeleteChange(viewUuid, gridId, subject, OperationEnum.REMOVE);
    }
  }

  @Override
  public void performDeleteUndo(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);

    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);
    if (gridRow.isPresent()) {
      Subject subject = objectApi.asType(Subject.class,
          GridModels.getValueFromGridRow(gridRow.get(), AclGridItem.SUBJECT));
      handleSubjectDeleteChange(viewUuid, gridId, subject, null);
    }
  }


  @Override
  public void performAddUndo(UUID viewUuid, String gridId, String rowId, UiActionRequest request) {
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);

    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);
    if (gridRow.isPresent()) {
      Subject subject = objectApi.asType(Subject.class,
          GridModels.getValueFromGridRow(gridRow.get(), AclGridItem.SUBJECT));
      handleSubjectAddUndo(viewUuid, gridId, subject);
    }

  }

  private void handleSubjectAddUndo(UUID viewUuid, String gridId, Subject subject) {
    SubjectAssignerPageModel model = getModel(viewUuid);
    model.getSubjectChanges().replaceAll(changes -> {
      String id = changes.getSubjectGridConfig().getGridId() != null
          ? changes.getSubjectGridConfig().getGridId()
          : changes.getSubjectGridConfig().getSubjectType();
      if (gridId.equals(id)) {
        changes.getSubjectAssociationModel().getSubjectAssociations()
            .removeIf(association -> (objectApi
                .equalsIgnoreVersion(association.getSubject().getRef(), subject.getRef())));
        refreshGrid(viewUuid, changes.getSubjectGridConfig(),
            changes.getSubjectAssociationModel().getSubjectAssociations());
        return changes;
      }
      return changes;
    });
    setModel(viewUuid, model);
  }



  private void handleSubjectDeleteChange(UUID viewUuid, String gridId, Subject subject,
      OperationEnum operation) {
    SubjectAssignerPageModel model = getModel(viewUuid);
    model.getSubjectChanges().replaceAll(changes -> {
      String id = changes.getSubjectGridConfig().getGridId() != null
          ? changes.getSubjectGridConfig().getGridId()
          : changes.getSubjectGridConfig().getSubjectType();
      if (gridId.equals(id)) {
        changes.getSubjectAssociationModel().getSubjectAssociations().replaceAll(association -> {
          if (objectApi.equalsIgnoreVersion(association.getSubject().getRef(), subject.getRef())) {
            return association.operation(operation);
          }
          return association;
        });
        refreshGrid(viewUuid, changes.getSubjectGridConfig(),
            changes.getSubjectAssociationModel().getSubjectAssociations());
        return changes;
      }
      return changes;
    });
    setModel(viewUuid, model);
  }

  @Override
  public void handleSubjectSelected(UUID viewUuid, List<Subject> subjects, String gridId) {
    SubjectAssignerPageModel model = getModel(viewUuid);
    model.getSubjectChanges().replaceAll(changes -> {
      String id = changes.getSubjectGridConfig().getGridId() != null
          ? changes.getSubjectGridConfig().getGridId()
          : changes.getSubjectGridConfig().getSubjectType();
      if (gridId.equals(id)
          && Boolean.FALSE.equals(checkForExistingSubjects(viewUuid, changes, subjects))) {
        subjects.stream().forEach(subject -> {
          changes.getSubjectAssociationModel().addSubjectAssociationsItem(
              new SubjectAssociationModification().subject(subject).operation(OperationEnum.ADD));
        });
        refreshGrid(viewUuid, changes.getSubjectGridConfig(),
            changes.getSubjectAssociationModel().getSubjectAssociations());
        return changes;
      }
      return changes;
    });
    setModel(viewUuid, model);
  }

  protected Boolean checkForExistingSubjects(UUID viewUuid, SubjectChanges changes,
      List<Subject> subjects) {
    PageContext ctx = context(viewUuid);
    String message = localeSettingApi.get(PREFIX, "ALREADY_SELECTED");
    List<String> names = new ArrayList<>();
    for (Subject subject : subjects) {
      if (checkSubjectIsAlreadyInAcl(subject.getRef(),
          changes.getSubjectAssociationModel().getSubjectAssociations())) {
        names.add(subjectManagementApi.getDisplayValue(ctx.getConfig().getSubjectModel(),
            Arrays.asList(subject)).get(0));
      }
    }
    if (!ObjectUtils.isEmpty(names)) {
      viewApi.showMessage(new MessageData().viewUuid(viewUuid).text(MessageFormat.format(
          message,
          names.stream().collect(Collectors.joining(",")),
          localeSettingApi.get(PREFIX, getGridId(changes.getSubjectGridConfig())))));
      return true;
    }
    return false;
  }

  private boolean checkSubjectIsAlreadyInAcl(URI subjectUri,
      List<SubjectAssociationModification> modifications) {
    return modifications.stream()
        .map(modification -> modification.getSubject().getRef())
        .anyMatch(uri -> objectApi.equalsIgnoreVersion(uri, subjectUri));
  }

  protected String getGridId(SubjectGridConfig config) {
    String gridId = config.getGridId();
    if (!Strings.isNullOrEmpty(gridId)) {
      return gridId;
    }
    return config.getSubjectType();
  }



}
