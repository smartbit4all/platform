package org.smartbit4all.bff.api.acl;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
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
import org.smartbit4all.api.org.bean.User;
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
import org.smartbit4all.bff.api.subjectselector.bean.UserSelectorPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class UserSelectorPageApiImpl extends PageApiImpl<UserSelectorPageModel>
    implements UserSelectorPageApi {

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

  @Autowired
  protected SearchIndex<User> userSearch;

  @Autowired(required = false)
  protected SearchPageConfig userSelectorSearchPageConfig;

  public UserSelectorPageApiImpl() {
    super(UserSelectorPageModel.class);
  }

  @Override
  public UserSelectorPageModel initModel(View view) {

    List<Subject> subjects = getSubjects(view);
    if (ObjectUtils.isEmpty(subjects)) {
      viewApi.closeView(view.getUuid());
      throw new IllegalStateException("subjectSelector.missingDescriptors");
    }
    List<Object> descriptors = subjects.stream()
        .map(subject -> new GenericValue()
            .uri(subject.getRef())
            .name(subjectManagementApi.getDisplayValue(PlatformApiConfig.SUBJECT_ACL,
                Arrays.asList(subject)).get(0)))
        .collect(toList());
    view.putValueSetsItem(UserSelectorPageModel.SELECTION,
        new ValueSet().valueSetData(new ValueSetData()
            .values(descriptors)
            .keyProperty(GenericValue.URI)));

    if (subjects.size() == 1) {
      view.getConstraint().addComponentConstraintsItem(new ComponentConstraint()
          .dataName(UserSelectorPageModel.SELECTION)
          .visible(false));
    }
    initFilter(view);
    initGrid(view.getUuid(), subjects.get(0));

    view.addActionsItem(new UiAction().code(CANCEL));
    view.addActionsItem(new UiAction().code(SUBMIT_SELECTION)
        .descriptor(new UiActionDescriptor()
            .title(localeSettingApi.get(SUBMIT_SELECTION))
            .color(UiActions.Color.PRIMARY)
            .type(UiActionButtonType.RAISED)));

    return new UserSelectorPageModel()
        .selection(subjects.get(0).getRef().toString());
  }

  private List<Subject> getSubjects(View view) {
    ObjectMapHelper params = parameters(view);
    List<Subject> subjects = params.getAsList(SUBJECT_VALUES, Subject.class);
    return subjects;
  }

  @Override
  public void performChangeSelection(UUID viewUuid, UiActionRequest request) {
    UserSelectorPageModel clientModel = extractClientModel(request);

    Optional<Subject> subject = getSubjects(viewApi.getView(viewUuid))
        .stream()
        .filter(s -> s.getRef().toString().equals(clientModel.getSelection())).findFirst();

    if (subject.isPresent()) {
      setModel(viewUuid, clientModel);
      refreshGrid(viewUuid, subject.get());
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
    InvocationRequest invocationRequest = params.get(SELECTION_CALLBACK, InvocationRequest.class);

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

  protected void initFilter(View view) {
    FilterExpressionBuilderModel filterModel = null;
    if (userSelectorSearchPageConfig != null) {
      filterModel = userSelectorSearchPageConfig.getFilterModel();
    } else {
      FilterExpressionFieldList filterFields = userSearch.allFilterFields();
      if (!ObjectUtils.isEmpty(filterFields)) {
        // init filter from searchIndex
        filterModel = new FilterExpressionBuilderModel()
            .workplaceList(filterFields);
      }
    }
    if (filterModel != null) {
      FilterExpressionBuilderUiModel filterExpressionBuilderUiModel =
          filterExpressionBuilderApi.createFilterBuilder(filterModel, null);
      filterExpressionBuilderApi.initFilterBuilderInView(view.getUuid(), SUBJECT_FILTER_ID,
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
      viewApi.setWidgetModelInView(FilterExpressionBuilderUiModel.class, view.getUuid(),
          SUBJECT_FILTER_ID, null);
      UiActions.remove(view, SEARCH);
    }
  }

  protected void initGrid(UUID viewUuid, Subject subject) {

    List<String> columns;
    EntityDefinition entityDefinition;
    if (userSelectorSearchPageConfig != null) {
      columns = new ArrayList<>(
          userSelectorSearchPageConfig.getGridViewOptions().get(0).getOrderedColumnNames());
      entityDefinition = collectionApi
          .searchIndex(
              userSelectorSearchPageConfig.getSearchIndexSchema(),
              userSelectorSearchPageConfig.getSearchIndexName())
          .getDefinition().getDefinition();
    } else {
      columns = userSearch.getDefinition().getDefinition().allProperties().stream()
          .map(prop -> prop.getName())
          .collect(toList());
      entityDefinition = userSearch.getDefinition().getDefinition();
    }
    if (!columns.contains(User.URI)) {
      columns.add(User.URI);
    }

    GridModel gridModel = gridModelApi
        .createGridModel(entityDefinition, columns, User.class.getSimpleName());
    GridModels.hideColumns(gridModel, User.URI);

    GridSelectionMode selectionMode =
        Optional.ofNullable(parameters(viewUuid).get(SELECTION_MODE, GridSelectionMode.class))
            .orElse(GridSelectionMode.MULTIPLE);
    gridModel.getView().getDescriptor()
        .selectionMode(selectionMode)
        .selectionType(GridSelectionType.CHECKBOX)
        .kind(KindEnum.TABLE);
    gridModel.paginator(true);
    gridModelApi.initGridInView(viewUuid, SUBJECT_GRID_ID, gridModel);

    refreshGrid(viewUuid, subject);
  }

  protected void refreshGrid(UUID viewUuid) {
    String selection = getModel(viewUuid).getSelection();

    Optional<Subject> subject = getSubjects(viewApi.getView(viewUuid))
        .stream()
        .filter(s -> s.getRef().toString().equals(selection)).findFirst();

    if (subject.isPresent()) {
      refreshGrid(viewUuid, subject.get());
    }

  }

  private void refreshGrid(UUID viewUuid, Subject subject) {
    TableData<?> tableData = getTableData(
        subject,
        filterExpressionBuilderApi.getFilterExpressionList(viewUuid, SUBJECT_FILTER_ID));
    gridModelApi.setData(viewUuid, SUBJECT_GRID_ID, tableData);
  }

  protected TableData<?> getTableData(Subject subject, FilterExpressionList expressionList) {
    List<URI> users = subjectManagementApi.getUsersOf(
        PlatformApiConfig.SUBJECT_ACL,
        Arrays.asList(subject));
    SearchIndex<?> searchIndex;
    if (userSelectorSearchPageConfig != null) {
      searchIndex = collectionApi.searchIndex(userSelectorSearchPageConfig.getSearchIndexSchema(),
          userSelectorSearchPageConfig.getSearchIndexName());
    } else {
      searchIndex = userSearch;
    }

    return searchIndex.executeSearchOn(users.stream(), expressionList);
  }

  @Override
  public void search(UUID viewUuid, UiActionRequest request) {
    refreshGrid(viewUuid);
  }

}
