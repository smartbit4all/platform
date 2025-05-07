package org.smartbit4all.bff.api.org;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderModel;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridRow;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.grid.bean.GridViewDescriptor;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.object.SubscriptionConfigApi;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.grid.GridModels;
import org.smartbit4all.bff.api.search.SearchPageApiImpl;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class UserSubscriptionListPageApiImpl extends SearchPageApiImpl
    implements UserSubscriptionListPageApi {

  public static String PREFIX = "UserSubscriptionListPageApi";

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  protected SessionApi sessionApi;

  @Autowired
  protected AccessControlInternalApi aclInternalApi;

  protected static final List<String> orderedColumns = new ArrayList<>();

  static {
    orderedColumns.add(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_NAME);
    orderedColumns.add(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_TYPE_NAME);
    orderedColumns.add(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_ENTITYSUMMARY);
    orderedColumns.add(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_CONTEXTCONFIG_NAME);
  }

  @Override
  public SearchPageModel initModel(View view) {
    List<GridColumnMeta> columns = orderedColumns.stream()
        .map(col -> new GridColumnMeta().propertyName(col)
            .typeClass(null)
            .label(localeSettingApi.get(PREFIX, col)))
        .collect(toList());

    SearchPageConfig searchPageConfig = new SearchPageConfig()
        .searchIndexSchema(OrgApiStorageImpl.ORG_SCHEME)
        .searchIndexName(SubscriptionConfigApi.SEARCH_USER_SUBSCRIPTION)
        .filterModel(new FilterExpressionBuilderModel())
        .gridViewOptions(Arrays.asList(new GridView()
            .orderedColumnNames(orderedColumns)
            .descriptor(new GridViewDescriptor()
                .columns(columns))));
    ObjectMapHelper params = parameters(view);
    params.put(PARAM_SEARCHPAGECONFIG, searchPageConfig);
    params.put(PARAM_GRID_PAGE_RENDER_CALLBACK,
        invocationApi.builder(UserSubscriptionListPageApi.class)
            .build(api -> api.onPageRender(null)));
    SearchPageModel model = super.initModel(view);
    view.getActions().forEach(a -> {
      UiActionDescriptor descriptor = a.getDescriptor();
      if (a.getDescriptor() == null) {
        descriptor = new UiActionDescriptor();
        a.descriptor(descriptor);
      }

      descriptor.title(localeSettingApi.get(PREFIX, a.getCode()));
    });
    model.setPageTitle(localeSettingApi.get("subject.subscription.title"));
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, view.getUuid(), WIDGET_RESULT_GRID);
    gridModel.setPaginator(true);

    return model;
  }

  @Override
  protected Stream<ObjectNode> getNodesToQuery(PageContext ctx) {
    return aclInternalApi.getUserAllSubscriptions(sessionApi.getUser().getUri(), getSubjectModels())
        .stream().map(s -> objectApi.create(null, s));
  }

  protected List<String> getSubjectModels() {
    return Arrays.asList(PlatformApiConfig.SUBJECT_ACL);
  }

  @Override
  public GridPage onPageRender(GridPage page) {
    final List<GridRow> rows = page.getRows();

    if (rows != null && !rows.isEmpty()) {
      rows.forEach(row -> {
        addDefaultRowActions(row);
      });
    }
    return page;
  }


  private void addDefaultRowActions(GridRow row) {
    Boolean revokeSupported = objectApi.asType(Boolean.class,
        GridModels.getValueFromGridRow(row,
            SubscriptionConfigApi.SUBSCRIPTION_OPERATION_REVOKE_SUPPORTED));

    Boolean modifySupported = objectApi.asType(Boolean.class,
        GridModels.getValueFromGridRow(row,
            SubscriptionConfigApi.SUBSCRIPTION_OPERATION_MODIFY_SUPPORTED));

    if (Boolean.TRUE.equals(modifySupported)) {
      row.addActionsItem(new UiAction().code(MODIFY));
    }

    if (Boolean.TRUE.equals(revokeSupported)) {
      row.addActionsItem(new UiAction().code(REVOKE));
    }

  }

  @Override
  public void revoke(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request) {
    // TODO
  }

  @Override
  public void modify(UUID viewUuid, String widgetId, String nodeId, UiActionRequest request) {
    // override
  }

}
