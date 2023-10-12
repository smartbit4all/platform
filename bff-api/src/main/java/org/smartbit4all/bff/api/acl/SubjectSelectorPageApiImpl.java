package org.smartbit4all.bff.api.acl;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridSelectionMode;
import org.smartbit4all.api.grid.bean.GridSelectionType;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.SubjectModel;
import org.smartbit4all.api.org.bean.SubjectTypeDescriptor;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionButtonType;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.bff.api.config.PlatformBffApiConfig;
import org.smartbit4all.bff.api.groupselector.bean.SubjectSelectorPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.domain.data.TableData;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toMap;

public class SubjectSelectorPageApiImpl extends PageApiImpl<SubjectSelectorPageModel>
    implements SubjectSelectorPageApi {

  @Autowired
  private SubjectManagementApi subjectManagementApi;

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private GridModelApi gridModelApi;

  @Autowired
  private FilterExpressionApi filterExpressionApi;

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
    String firstDescriptorName = model.getDescriptors().get(0).getName();

    Map<String, SubjectTypeDescriptor> descriptorMap =
        model.getDescriptors().stream().collect(toMap(SubjectTypeDescriptor::getName, d -> d));

    initGrid(firstDescriptorName, view.getUuid(), descriptorMap);

    view.addActionsItem(new UiAction().code(CANCEL));
    view.addActionsItem(new UiAction().code(SUBMIT_SELECTION)
        .descriptor(new UiActionDescriptor().title(localeSettingApi.get(SUBMIT_SELECTION))
            .color("primary").type(UiActionButtonType.RAISED)));

    return new SubjectSelectorPageModel()
        .descriptors(
            descriptorMap)
        .selectedDescriptor(model.getDescriptors().get(0))
        .selectedSubjectName(firstDescriptorName)
        .sujectModelName(
            model.getTitle() != null ? localeSettingApi.get(model.getTitle()) : model.getName());
  }

  @Override
  public void peformSelectSubject(UUID viewUuid, UiActionRequest request) {
    ObjectMapHelper params = actionRequestHelper(request);
    String subjectTypeName = params.get(SubjectTypeDescriptor.NAME, String.class);
    SubjectSelectorPageModel pageModel = this.getModel(viewUuid);

    pageModel.setSelectedDescriptor(pageModel.getDescriptors().get(subjectTypeName));
    pageModel.setSelectedSubjectName(subjectTypeName);
    setModel(viewUuid, pageModel);

    initGrid(subjectTypeName, viewUuid, pageModel.getDescriptors());
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

    URI subjectUri = extractUriFromGridRow(selectedRows.get(0));

    if (Objects.nonNull(invocationRequest)) {
      try {
        invocationRequest.getParameters().get(1).setValue(subjectUri);
        invocationApi.invoke(invocationRequest);
        viewApi.closeView(viewUuid);
      } catch (ApiNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private URI extractUriFromGridRow(GridRow row) {

    return objectApi.asType(URI.class, ((Map<?, ?>) row.getData()).get("uri"));

  }

  private void initGrid(String subjectName, UUID viewUuid,
      Map<String, SubjectTypeDescriptor> descriptors) {
    SubjectTypeDescriptor subjectTypeDescriptor = descriptors.get(subjectName);
    SearchPageConfig selectionConfig = subjectTypeDescriptor.getSelectionConfig();

    SearchIndex<?> searchIndex =
        collectionApi.searchIndex(selectionConfig.getSearchIndexSchema(),
            selectionConfig.getSearchIndexName());

    GridModel gridModel =
        gridModelApi.createGridModel(searchIndex.getDefinition().getDefinition(),
            selectionConfig.getGridViewOptions().get(0).getOrderedColumnNames(), "");
    gridModel.getView().getDescriptor().setSelectionMode(GridSelectionMode.SINGLE);
    gridModel.getView().getDescriptor().setSelectionType(GridSelectionType.CHECKBOX);
    gridModelApi.initGridInView(viewUuid, SUBJECT_GRID_ID, gridModel);

    TableData<?> tableData =
        searchIndex.executeSearch(filterExpressionApi.of(searchIndex.allFilterFields()));
    gridModelApi.setData(viewUuid, SUBJECT_GRID_ID, tableData);

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
