package org.smartbit4all.bff.api.acl;

import static java.util.stream.Collectors.toList;
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
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
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
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.SubjectModel;
import org.smartbit4all.api.org.bean.SubjectTypeDescriptor;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApi;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectSelectorPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.domain.data.TableData;
import org.springframework.beans.factory.annotation.Autowired;

public class SubjectSelectorPageApiImpl extends PageApiImpl<SubjectSelectorPageModel>
    implements SubjectSelectorPageApi {

  private static final String SUBJECT_GRID_ID = "SUBJECT_GRID";
  private static final String SUBJECT_FILTER_ID = "SUBJECT_FILTER";

  @Autowired
  private SubjectManagementApi subjectManagementApi;

  @Autowired
  protected CollectionApi collectionApi;

  @Autowired
  protected GridModelApi gridModelApi;

  @Autowired
  protected FilterExpressionApi filterExpressionApi;

  @Autowired
  private FilterExpressionBuilderApi filterExpressionBuilderApi;

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

    if (model.getDescriptors() == null || model.getDescriptors().isEmpty()) {
      viewApi.closeView(view.getUuid());
      throw new IllegalStateException("subjectSelector.missingDescriptors");
    }
    if (model.getDescriptors().size() == 1) {
      view.getConstraint().addComponentConstraintsItem(new ComponentConstraint()
          .dataName(SubjectSelectorPageModel.SELECTION)
          .visible(false));
    }
    initGridAndFilter(view.getUuid(), model.getDescriptors().get(0));

    view.addActionsItem(new UiAction().code(CANCEL));
    view.addActionsItem(new UiAction().code(SUBMIT_SELECTION)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(SUBMIT_SELECTION))
            .color(UiActions.Color.PRIMARY)
            .type(UiActionButtonType.RAISED)));

    List<Object> descriptors = model.getDescriptors().stream()
        .map(desc -> new GenericValue()
            .code(desc.getName())
            .name(localeSettingApi.get(desc.getTitle())))
        .collect(toList());
    view.putValueSetsItem(SubjectSelectorPageModel.SELECTION,
        new ValueSet().valueSetData(new ValueSetData()
            .values(descriptors)
            .keyProperty(GenericValue.CODE)));

    return new SubjectSelectorPageModel()
        .selection(model.getDescriptors().get(0).getName());
  }

  @Override
  public void performChangeSelection(UUID viewUuid, UiActionRequest request) {
    SubjectSelectorPageModel clientModel = extractClientModel(request);

    Optional<SubjectTypeDescriptor> subjectTypeDescriptor =
        getSelectedSubjectType(viewUuid, clientModel.getSelection());

    if (subjectTypeDescriptor.isPresent()) {
      setModel(viewUuid, clientModel);
      initGridAndFilter(viewUuid, subjectTypeDescriptor.get());
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
    InvocationRequest invocationRequest =
        params.get(PARAM_SELECTION_CALLBACK, InvocationRequest.class);

    String model = getSubjectModel(view).getName();
    String selection = getModel(viewUuid).getSelection();
    Optional<SubjectTypeDescriptor> subjectTypeDescriptor =
        getSelectedSubjectType(viewUuid, selection);

    if (!subjectTypeDescriptor.isPresent()) {
      throw new IllegalStateException("Illegal selection! " + selection);
    }
    String type = subjectTypeDescriptor.get().getName();
    List<Subject> subjects = selectedRows.stream()
        .map(this::extractUriFromGridRow)
        .map(uri -> new Subject()
            .model(model)
            .type(type)
            .ref(uri))
        .collect(Collectors.toList());

    if (Objects.nonNull(invocationRequest)) {
      try {
        invocationRequest.getParameters().get(1)
            .setValue(Invocations.listOf(subjects, Subject.class));
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

  protected void initGridAndFilter(UUID viewUuid, SubjectTypeDescriptor subjectTypeDescriptor) {
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
            columns);
    GridSelectionMode selectionMode =
        Optional.ofNullable(parameters(viewUuid).get(PARAM_SELECTION_MODE, GridSelectionMode.class))
            .orElse(GridSelectionMode.MULTIPLE);
    gridModel.getView().getDescriptor()
        .selectionMode(selectionMode)
        .selectionType(GridSelectionType.CHECKBOX)
        .kind(isTree ? KindEnum.TREE : KindEnum.TABLE);
    gridModel.paginator(!isTree);
    if (isTree) {
      GridModels.hideColumns(gridModel, subjectTypeDescriptor.getParentPropertyName());
      GridModels.hideColumns(gridModel, subjectTypeDescriptor.getParentIdentifierPropertyName());
      gridModel.getView().getDescriptor().setShowEditColumns(false);
    }
    if (selectionConfig.getPageSize() != null) {
      gridModel.pageSize(selectionConfig.getPageSize());
    }
    gridModelApi.initGridInView(viewUuid, SUBJECT_GRID_ID, gridModel);
    if (isTree) {
      gridModelApi.setTreePropertyNames(viewUuid, SUBJECT_GRID_ID,
          subjectTypeDescriptor.getParentIdentifierPropertyName(),
          subjectTypeDescriptor.getParentPropertyName());
    }
    FilterExpressionBuilderModel filterModel = selectionConfig.getFilterModel();
    View view = viewApi.getView(viewUuid);
    if (filterModel != null) {
      FilterExpressionBuilderUiModel filterExpressionBuilderUiModel =
          filterExpressionBuilderApi.createFilterBuilder(filterModel, null);
      filterExpressionBuilderApi.initFilterBuilderInView(viewUuid, SUBJECT_FILTER_ID,
          filterExpressionBuilderUiModel);
      UiActions.add(view,
          new UiAction()
              .code(SEARCH)
              .toolbar("search")
              .descriptor(new UiActionDescriptor()
                  .title(localeSettingApi.get(SEARCH))
                  .color(UiActions.Color.PRIMARY)
                  .type(UiActionButtonType.RAISED)
                  .icon("search")));
    } else {
      viewApi.setWidgetModelInView(FilterExpressionBuilderUiModel.class, viewUuid,
          SUBJECT_FILTER_ID, null);
      UiActions.remove(view, SEARCH);
    }

    refreshGrid(viewUuid, selectionConfig);
  }

  protected void refreshGrid(UUID viewUuid) {
    String selection = getModel(viewUuid).getSelection();

    Optional<SubjectTypeDescriptor> subjectTypeDescriptor =
        getSelectedSubjectType(viewUuid, selection);

    if (subjectTypeDescriptor.isPresent()) {
      refreshGrid(viewUuid, subjectTypeDescriptor.get().getSelectionConfig());
    }

  }

  private Optional<SubjectTypeDescriptor> getSelectedSubjectType(UUID viewUuid,
      String selection) {
    return getSubjectModel(viewApi.getView(viewUuid))
        .getDescriptors().stream()
        .filter(d -> d.getName().equals(selection)).findFirst();
  }

  private void refreshGrid(UUID viewUuid, SearchPageConfig searchPageConfig) {
    TableData<?> tableData = getTableData(
        searchPageConfig,
        filterExpressionBuilderApi.getFilterExpressionList(viewUuid, SUBJECT_FILTER_ID));
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
    ObjectMapHelper params = parameters(view);
    String subjectModelName = params.get(PARAM_SUBJECT_MODEL_NAME, String.class);
    if (subjectModelName == null) {
      // By default we use the ACL subject model.
      subjectModelName = PlatformApiConfig.SUBJECT_ACL;
    }
    SubjectModel model = subjectManagementApi.getModel(subjectModelName);
    List<String> types = params.getAsList(PARAM_SUBJECT_TYPES, String.class);
    if (types != null && !types.isEmpty()) {
      List<SubjectTypeDescriptor> filteredTypes = model.getDescriptors().stream()
          .filter(type -> types.contains(type.getName()))
          .collect(toList());
      model.descriptors(filteredTypes);
    }
    return model;
  }

  @Override
  public void search(UUID viewUuid, UiActionRequest request) {
    refreshGrid(viewUuid);
  }

}
