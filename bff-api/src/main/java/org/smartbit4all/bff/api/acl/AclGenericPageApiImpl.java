package org.smartbit4all.bff.api.acl;

import static java.util.stream.Collectors.toList;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.grid;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.label;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACL;
import org.smartbit4all.api.org.bean.ACLEntry;
import org.smartbit4all.api.org.bean.ACLEntry.EntryKindEnum;
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
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewType;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.config.PlatformViewNames;
import org.smartbit4all.bff.api.subjectselector.bean.AclGridConfig;
import org.smartbit4all.bff.api.subjectselector.bean.AclGridItem;
import org.smartbit4all.bff.api.subjectselector.bean.AclPageConfig;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class AclGenericPageApiImpl extends PageApiImpl<Object> implements AclGenericPageApi {

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
    View view;
    AclPageConfig config;
    ObjectNode aclObjectNode;

    protected PageContext load(View view) {
      Objects.requireNonNull(view.getObjectUri(), "ACL object must be specified");
      this.aclObjectNode = objectApi.loadLatest(
          view.getObjectUri(), view.getBranchUri());
      Objects.requireNonNull(aclObjectNode, "ACL object not found");
      this.view = view;
      this.config = parameters(view).get(PARAM_ACL_PAGE_CONFIG, AclPageConfig.class);
      return this;
    }

    protected AclGridConfig findGridConfig(String name) {
      Objects.requireNonNull(name, "GridConfig name cannot be null");
      return config.getGridConfigs().stream()
          .filter(c -> name.equals(c.getAclName()))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Invalid GridConfigName"));
    }

    protected String getSelectorViewName() {
      return config.getSelectorViewName() != null ? config.getSelectorViewName()
          : PlatformViewNames.SUBJECT_SELECTOR_PAGE;
    }

    protected InvocationRequest getSelectionCallback(String gridId) {
      return config.getSelectionCallback() != null ? config.getSelectionCallback()
          : invocationApi.builder(AclGenericPageApi.class)
              .build(api -> api.handleSubjectSelected(
                  view.getUuid(),
                  Invocations.listOf(Collections.emptyList(), URI.class),
                  gridId));
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
    SmartComponentLayoutDefinition layout = createLayout();
    view.putComponentLayoutsItem("default", layout);
    addGridsToLayout(view, layout);

    return new HashMap<String, Object>();
  }

  protected SmartComponentLayoutDefinition createLayout() {
    return container(LayoutDirection.VERTICAL);
  }

  protected void addGridsToLayout(View view, SmartComponentLayoutDefinition layout) {
    PageContext ctx = context(view);
    ACLObject aclObject = ctx.aclObjectNode.getObject(ACLObject.class);

    UUID viewUuid = view.getUuid();
    for (AclGridConfig config : ctx.config.getGridConfigs()) {
      String name = config.getAclName();
      ACL acl = getAclFromObject(aclObject, name);
      String gridId = config.getAclName();
      initGridInView(view, viewUuid, config);
      refreshGrid(viewUuid, acl, config);
      layout.addComponentsItem(createGridLayout(gridId));
    }
  }

  protected ACL getAclFromObject(ACLObject aclObject, String name) {
    return aclObject.getMap().computeIfAbsent(
        name,
        (s) -> new ACL().rootEntry(new ACLEntry().entryKind(EntryKindEnum.SET)));
  }

  protected void initGridInView(View view, UUID viewUuid, AclGridConfig config) {
    String gridId = config.getAclName();
    GridModel gridModel = viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    if (gridModel == null) {
      createGridModel(viewUuid, gridId, config.getSearchPageConfig());
      view.addActionsItem(new UiAction()
          .code(ADD_SUBJECT)
          .toolbar(gridId + UiActions.TOOLBAR_SUFFIX)
          .identifier(gridId)
          .descriptor(new UiActionDescriptor()
              .icon("Plus")
              .title(" ")
              .type(UiActionButtonType.ICON)
              .color(UiActions.Color.ACCENT)));
    }
  }

  protected GridModel createGridModel(UUID viewUuid, String gridId,
      SearchPageConfig searchPageConfig) {

    List<String> columns;
    GridModel gridModel;
    if (searchPageConfig != null) {
      columns = new ArrayList<>(
          searchPageConfig.getGridViewOptions().get(0).getOrderedColumnNames());
      EntityDefinition entityDefinition = collectionApi
          .searchIndex(
              searchPageConfig.getSearchIndexSchema(),
              searchPageConfig.getSearchIndexName())
          .getDefinition().getDefinition();
      gridModel = gridModelApi
          .createGridModel(entityDefinition, columns, User.class.getSimpleName());
    } else {
      columns = Arrays.asList(AclGridItem.NAME, AclGridItem.COMMENT, AclGridItem.SUBJECT);
      gridModel = gridModelApi.createGridModel(
          AclGridItem.class,
          columns);
    }
    if (!columns.contains(AclGridItem.SUBJECT)) {
      columns.add(AclGridItem.SUBJECT);
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

  protected void refreshGrid(UUID viewUuid, ACL acl, AclGridConfig config) {
    String gridId = config.getAclName();
    List<ACLSubject> subjects = getSubjects(acl, config);
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

  protected List<ACLSubject> getSubjects(ACL acl, AclGridConfig config) {
    return accessControlInternalApi.getSubjects(acl, config.getOperation());
  }

  protected SmartComponentLayoutDefinition createGridLayout(String gridId) {
    return container(LayoutDirection.VERTICAL)
        .addComponentsItem(form(LayoutDirection.VERTICAL,
            label(null, localeSettingApi.get(PREFIX, gridId))))
        .addComponentsItem(grid(gridId));
  }

  @Override
  public GridPage addGridActions(GridPage page, UUID viewUuid, String gridId) {
    page.getRows().forEach(row -> {
      row.addActionsItem(new UiAction()
          .code(DELETE_SUBJECT)
          .descriptor(new UiActionDescriptor()
              .title(localeSettingApi.get(PREFIX, DELETE_SUBJECT))));
    });
    return page;
  }

  @Override
  public void performAddSubject(UUID viewUuid, UiActionRequest request) {
    PageContext ctx = context(viewUuid);
    String gridId = request.getIdentifier();
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);

    viewApi.showView(new View()
        .viewName(ctx.getSelectorViewName())
        .putParametersItem(SubjectSelectorPageApi.PARAM_SUBJECT_MODEL_NAME,
            gridConfig.getAclModel())
        .putParametersItem(SubjectSelectorPageApi.PARAM_SUBJECT_TYPES,
            gridConfig.getSubjectTypes())
        .putParametersItem(SubjectSelectorPageApi.PARAM_SELECTION_MODE,
            gridConfig.getSelectionMode())
        .putParametersItem(SubjectSelectorPageApi.PARAM_SELECTION_CALLBACK,
            ctx.getSelectionCallback(gridId))
        .type(ViewType.DIALOG));


  }

  @Override
  public void performDeleteSubject(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, viewUuid, gridId);
    Optional<GridRow> gridRow = GridModels.findGridRowById(gridModel, rowId);
    if (gridRow.isPresent()) {
      Subject subject = objectApi.asType(Subject.class,
          GridModels.getValueFromGridRow(gridRow.get(), AclGridItem.SUBJECT));
      if (subject != null && subject.getRef() != null) {
        ctx.aclObjectNode.modify(ACLObject.class, aclObject -> {
          ACL acl = getAclFromObject(aclObject, gridConfig.getAclName());
          String operation = gridConfig.getOperation();
          List<ACLSubject> subjects = accessControlInternalApi.getSubjects(acl, operation);
          boolean anyChange = subjects.removeIf(
              sub -> objectApi.equalsIgnoreVersion(sub.getSubject().getRef(), subject.getRef()));
          if (anyChange) {
            accessControlInternalApi.applySubjects(acl, subjects, gridConfig.getOperation());
          }
          refreshGrid(viewUuid, acl, gridConfig);
          return aclObject;
        });
        objectApi.save(ctx.aclObjectNode);
      }
    }
  }

  @Override
  public void handleSubjectSelected(UUID viewUuid, List<URI> subjectUriList, String gridId) {
    PageContext ctx = context(viewUuid);
    AclGridConfig gridConfig = ctx.findGridConfig(gridId);

    ctx.aclObjectNode.modify(ACLObject.class, aclObject -> {
      ACL acl = getAclFromObject(aclObject, gridConfig.getAclName());
      String operation = gridConfig.getOperation();
      List<ACLSubject> subjects = accessControlInternalApi.getSubjects(acl, operation);

      for (URI uri : subjectUriList) {
        if (checkSubjectIsAlreadyInAcl(acl, uri)) {
          throw new RuntimeException(
              String.format("Subject reference by %s is already in ACL", uri));
        }
        subjects.add(
            new ACLSubject()
                .operation(new ACLOperation().name(operation))
                .subject(new Subject().ref(uri)));
      }

      accessControlInternalApi.applySubjects(acl, subjects, gridConfig.getOperation());

      refreshGrid(viewUuid, acl, gridConfig);
      return aclObject;
    });

    objectApi.save(ctx.aclObjectNode);
  }

  private boolean checkSubjectIsAlreadyInAcl(ACL acl, URI subjectUri) {
    if (acl == null || acl.getRootEntry() == null || acl.getRootEntry().getEntries() == null) {
      return false;
    }
    return acl.getRootEntry().getEntries().stream().map(entry -> entry.getSubject().getRef())
        .collect(toList())
        .contains(subjectUri);
  }


}
