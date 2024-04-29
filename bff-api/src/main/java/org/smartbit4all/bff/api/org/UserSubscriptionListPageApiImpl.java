package org.smartbit4all.bff.api.org;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOrderBy.OrderEnum;
import org.smartbit4all.api.filterexpression.bean.SearchPageConfig;
import org.smartbit4all.api.grid.bean.GridColumnMeta;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.grid.bean.GridView;
import org.smartbit4all.api.grid.bean.GridViewDescriptor;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.object.SubscriptionConfigApi;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.search.SearchPageApiImpl;
import org.smartbit4all.bff.api.searchpage.bean.SearchPageModel;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class UserSubscriptionListPageApiImpl extends SearchPageApiImpl
    implements UserSubscriptionListPageApi {

  public static String PREFIX = "UserSubscriptionListPageApi";

  @Autowired
  protected LocaleSettingApi localeSettingApi;

  @Autowired
  protected SessionApi sessionApi;

  @Autowired
  protected AccessControlInternalApi aclInternalApi;

  protected List<String> subjectModels = new ArrayList<>();

  @Override
  public SearchPageModel initModel(View view) {
    subjectModels.add(PlatformApiConfig.SUBJECT_ACL);
    List<String> orderedColumns = List.of(
        SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_NAME,
        SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_TYPE_NAME,
        SubscriptionConfigApi.SUBSCRIPTION_OPERATION_ENTITYSUMMARY,
        SubscriptionConfigApi.SUBSCRIPTION_OPERATION_NAME);

    List<GridColumnMeta> columns = orderedColumns.stream()
        .map(col -> new GridColumnMeta().propertyName(col)
            .typeClass(null)
            .label(localeSettingApi.get(PREFIX, col)))
        .collect(toList());

    SearchPageConfig searchPageConfig = new SearchPageConfig()
        .searchIndexSchema(OrgApiStorageImpl.ORG_SCHEME)
        .searchIndexName(SubscriptionConfigApi.SEARCH_USER_SUBSCRIPTION)
        .filterModel(null)
        .gridViewOptions(List.of(new GridView()
            .orderedColumnNames(orderedColumns)
            .addOrderByListItem(new FilterExpressionOrderBy()
                .propertyName(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_TYPE)
                .order(OrderEnum.ASC))
            .descriptor(new GridViewDescriptor()
                .columns(columns))));
    ObjectMapHelper params = parameters(view);
    params.put(PARAM_SEARCHPAGECONFIG, searchPageConfig);
    params.put(PARAM_GRID_PAGE_RENDER_CALLBACK,
        invocationApi.builder(UserSubscriptionListPageApi.class)
            .build(api -> api.onPageRender(null)));
    SearchPageModel model = super.initModel(view);
    model.setPageTitle(null);
    GridModel gridModel =
        viewApi.getWidgetModelFromView(GridModel.class, view.getUuid(), WIDGET_RESULT_GRID);
    gridModel.setPaginator(true);

    return model;
  }

  @Override
  protected Stream<ObjectNode> getNodesToQuery(PageContext ctx) {
    return aclInternalApi.getUserAllSubscriptions(sessionApi.getUser().getUri(), subjectModels)
        .stream().map(s -> objectApi.create(null, s));
  }

  @Override
  public GridPage onPageRender(GridPage page) {
    // TODO Auto-generated method stub
    return null;
  }

}
