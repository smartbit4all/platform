package org.smartbit4all.bff.api.acl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridSelectionMode;
import org.smartbit4all.api.grid.bean.GridSelectionType;
import org.smartbit4all.api.grid.bean.GridViewDescriptor.KindEnum;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.SubjectModel;
import org.smartbit4all.api.org.bean.SubjectTypeDescriptor;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.config.PlatformBffApiConfig;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectSelectorPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.domain.data.TableData;
import org.springframework.beans.factory.annotation.Autowired;

public class SubjectSelectorPageApiImpl extends PageApiImpl<SubjectSelectorPageModel>
    implements SubjectSelectorPageApi {

  @Autowired
  private SubjectManagementApi subjectManagementApi;

  @Autowired
  protected CollectionApi collectionApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  protected FilterExpressionApi filterExpressionApi;

  @Autowired
  private InvocationApi invocationApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  public SubjectSelectorPageApiImpl() {
    super(SubjectSelectorPageModel.class);
  }

  @Override
  public SubjectSelectorPageModel initModel(View view) {

    SubjectModel model = getSubjectModel(view);

    initGrid(model.getDescriptors().get(0), view.getUuid());

    view.addActionsItem(new UiAction().code(CANCEL));
    view.addActionsItem(new UiAction().code(SUBMIT_SELECTION)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(SUBMIT_SELECTION))
            .color(UiActions.Color.PRIMARY)
            .type(UiActionButtonType.RAISED)));

    return new SubjectSelectorPageModel()
        .descriptors(
            model.getDescriptors())
        .selectedDescriptor(model.getDescriptors().get(0))
        .selectedSubjectName(model.getDescriptors().get(0).getName())
        .sujectModelName(
            model.getTitle() != null ? localeSettingApi.get(model.getTitle()) : model.getName());
  }

  @Override
  public void peformSelectSubject(UUID viewUuid, UiActionRequest request) {
    ObjectMapHelper params = actionRequestHelper(request);
    String subjectTypeDescriptorName =
        params.get(UiActions.MODEL, String.class);
    SubjectSelectorPageModel pageModel = this.getModel(viewUuid);

    Optional<SubjectTypeDescriptor> subjectTypeDescriptor = pageModel.getDescriptors().stream()
        .filter(d -> d.getName().equals(subjectTypeDescriptorName)).findFirst();

    if (subjectTypeDescriptor.isPresent()) {
      pageModel.setSelectedDescriptor(subjectTypeDescriptor.get());
      pageModel.setSelectedSubjectName(subjectTypeDescriptorName);
      setModel(viewUuid, pageModel);

      initGrid(subjectTypeDescriptor.get(), viewUuid);
    }
  }

  @Override
  public void performCancel(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  @Override
  public void performSubmitSelection(UUID viewUuid, UiActionRequest request) {
    List<GridRow> selectedRows = gridModelApi.getSelectedRows(viewUuid, SUBJECT_GRID_ID);
    View view = viewApi.getView(viewUuid);
    ObjectMapHelper params = parameters(view);
    InvocationRequest invocationRequest = params.get(INVOCATION, InvocationRequest.class);

    List<URI> subjectUriList = selectedRows.stream()
        .map(row -> extractUriFromGridRow(row)).collect(Collectors.toList());

    if (Objects.nonNull(invocationRequest)) {
      try {
        invocationRequest.getParameters().get(1)
            .setValue(Invocations.listOf(subjectUriList, URI.class));
        invocationApi.invoke(invocationRequest);
        viewApi.closeView(viewUuid);
      } catch (ApiNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private URI extractUriFromGridRow(GridRow row) {

    return objectApi.asType(URI.class, getUriObject(row));

  }

  protected Object getUriObject(GridRow row) {
    Object uri = ((Map<?, ?>) row.getData()).get("uri");
    if (uri == null) {
      uri = ((Map<?, ?>) row.getData()).get("originalUri");
    }
    return uri;
  }

  protected void initGrid(SubjectTypeDescriptor subjectTypeDescriptor, UUID viewUuid) {
    SearchPageConfig selectionConfig = subjectTypeDescriptor.getSelectionConfig();

    SearchIndex<?> searchIndex =
        collectionApi.searchIndex(selectionConfig.getSearchIndexSchema(),
            selectionConfig.getSearchIndexName());

    List<String> columns =
        new ArrayList<>(selectionConfig.getGridViewOptions().get(0).getOrderedColumnNames());
    boolean isTree = subjectTypeDescriptor.getParentPropertyName() != null
        && subjectTypeDescriptor.getParentIdentifierPropertyName() != null;
    if (isTree) {
      columns.add(subjectTypeDescriptor.getParentPropertyName());
      columns.add(subjectTypeDescriptor.getParentIdentifierPropertyName());
    }
    GridModel gridModel =
        gridModelApi.createGridModel(searchIndex.getDefinition().getDefinition(),
            columns, "");
    GridSelectionMode selectionMode =
        Optional.ofNullable(parameters(viewUuid).get(SELECTION_MODE, GridSelectionMode.class))
            .orElse(GridSelectionMode.MULTIPLE);
    gridModel.getView().getDescriptor()
        .selectionMode(selectionMode)
        .selectionType(GridSelectionType.CHECKBOX)
        .kind(isTree ? KindEnum.TREE : KindEnum.TABLE);
    if (isTree) {
      GridModels.hideColumns(gridModel, subjectTypeDescriptor.getParentPropertyName());
      GridModels.hideColumns(gridModel, subjectTypeDescriptor.getParentIdentifierPropertyName());
      gridModel.getView().getDescriptor().setShowEditColumns(false);
    }
    gridModelApi.initGridInView(viewUuid, SUBJECT_GRID_ID, gridModel);
    if (isTree) {
      gridModelApi.setTreePropertyNames(viewUuid, SUBJECT_GRID_ID,
          subjectTypeDescriptor.getParentIdentifierPropertyName(),
          subjectTypeDescriptor.getParentPropertyName());
    }

    TableData<?> tableData =
        getTableData(selectionConfig, filterExpressionApi.of(searchIndex.allFilterFields()));
    gridModelApi.setData(viewUuid, SUBJECT_GRID_ID, tableData);
  }

  protected TableData<?> getTableData(SearchPageConfig selectionConfig,
      FilterExpressionList expressionList) {
    SearchIndex<?> searchIndex =
        collectionApi.searchIndex(selectionConfig.getSearchIndexSchema(),
            selectionConfig.getSearchIndexName());
    TableData<?> tableData = null;
    if (selectionConfig.getContainer() != null) {
      StoredList list = collectionApi.list(selectionConfig.getContainer());
      if (list != null) {
        tableData = searchIndex.executeSearchOnNodes(list.nodesFromCache(),
            expressionList,
            selectionConfig.getGridViewOptions().get(0).getOrderByList());
      }
    }
    if (tableData == null) {
      tableData = searchIndex.executeSearch(expressionList,
          selectionConfig.getGridViewOptions().get(0).getOrderByList());
    }
    return tableData;
  }

  private SubjectModel getSubjectModel(View view) {
    Object modelName = view.getParameters().get(PlatformBffApiConfig.SUBJECT_MODEL_NAME);
    String subjectModelName;
    if (modelName == null) {
      // By default we use the ACL subject model.
      subjectModelName = PlatformApiConfig.SUBJECT_ACL;
    } else {
      subjectModelName = modelName.toString();
    }
    return subjectManagementApi.getModel(subjectModelName);

  }
}
