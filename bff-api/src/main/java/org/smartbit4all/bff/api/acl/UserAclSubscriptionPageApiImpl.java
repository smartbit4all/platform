package org.smartbit4all.bff.api.acl;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.IconPosition;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.bean.UiActionDescriptor;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.springframework.beans.factory.annotation.Autowired;

public class UserAclSubscriptionPageApiImpl extends PageApiImpl<Object>
    implements UserAclSubscriptionPageApi {

  @Autowired
  private ViewApi viewApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Autowired
  private OrgApi orgApi;

  @Autowired
  private AccessControlInternalApi accessControlInternalApi;

  protected class PageContext {

    public PageContext(UUID viewUUID) {
      this(viewApi.getView(viewUUID));
    }

    public PageContext(View view) {
      this.viewUUID = view.getUuid();
      this.view = view;
      ObjectMapHelper params = parameters(view);
      user = orgApi.getUserByUsername(params.get(PARAM_USER, String.class));
      subjectModels = params.getAsList(PARAM_SUBJECT_MODELS, String.class);
    }

    UUID viewUUID;

    View view;

    User user;

    List<String> subjectModels;

  }

  public UserAclSubscriptionPageApiImpl() {
    super(Object.class);
  }

  @Override
  public Object initModel(View view) {
    return null;
  }

  @Override
  public GridPage addGridActions(GridPage page, UUID viewUuid, String gridId) {
    // PageContext ctx = new PageContext(viewUuid);
    page.getRows().forEach(row -> {
      row.addActionsItem(new UiAction()
          .code(DELETE_SUBSCRIPTION)
          .descriptor(new UiActionDescriptor()
              .title(
                  localeSettingApi.get(ACL_PREFIX, DELETE_SUBSCRIPTION))
              .iconPosition(IconPosition.PRE).icon("delete")));
    });

    // List<ACLSubjectOperations> userAllOperations =
    // accessControlInternalApi.getUserAllOperations(ctx.user.getUri(), ctx.subjectModels);
    // List<ACLOperationReference> rowsOfTheGrid = userAllOperations.stream().flatMap(so ->
    // so.getOperations().stream()).collect(toList());
    // All the rows must be extended with the properties of the subjcet and the formatted summary of
    // the referenced context.
    return page;

  }

  // protected void refreshGrid() {
  // TableData<?> gridContent = null;
  // FilterExpressionList filters = filterExpressionBuilderApi
  // .getFilterExpressionList(ctx.viewUUID, FILTER_BUILDER_WIDGET_ID);
  //
  // Stream<ObjectNode> nodesToQuery = getNodesToQuery();
  // if (nodesToQuery != null) {
  // // We have an injected node stream to use.
  // gridContent =
  // ctx.searchIndex.executeSearchOnNodes(nodesToQuery, filters, getOrderByList(ctx));
  // } else if (ctx.uris != null) {
  // // We have an explicit uri list. We use it directly.
  // gridContent =
  // ctx.searchIndex.executeSearchOn(ctx.uris.stream(), filters, getOrderByList(ctx));
  // } else if (ctx.list != null) {
  // // We have a stored list the query is working on.
  // gridContent =
  // ctx.searchIndex.executeSearchOnNodes(ctx.list.nodesFromCache(), filters,
  // getOrderByList(ctx));
  // } else if (model.getHistoryRange() != null) {
  // // We have an object history the query is working on.
  // URI objectUri = model.getHistoryRange().getObjectUri();
  // // Update the lowerBound if empty
  // ObjectHistoryIterator historyIterator = objectApi.objectHistory(objectUri)
  // .firstIndex(model.getHistoryRange().getLowerBound().getVersionNr())
  // .lastVersion(model.getHistoryRange().getUpperBound().getVersionNr()).reverse(true)
  // .useCache(true);
  // gridContent =
  // ctx.searchIndex.executeSearchOnNodes(Streams.stream(historyIterator), filters,
  // getOrderByList(ctx));
  // } else {
  // // We try the database or read all.
  // gridContent = ctx.searchIndex.executeSearch(filters, getOrderByList(ctx));
  // }
  // setDataToGrid(ctx.view.getUuid(), ctx.searchIndex, gridContent, filters);
  // if (gridContent.size() == 0) {
  // model.noResultText(ctx.pageConfig.getNoResultText());
  // } else {
  // model.noResultText(null);
  // }
  // }
  //
  // protected void setDataToGrid(UUID uuid, SearchIndex<?> searchIndex, TableData<?> gridContent,
  // FilterExpressionList filters) {
  // if (gridContent != null) {
  // gridModelApi.setData(uuid, WIDGET_RESULT_GRID, gridContent);
  // }
  // }
  //

  @Override
  public void closeView(UUID viewUuid, UiActionRequest request) {
    viewApi.closeView(viewUuid);
  }

  @Override
  public void performDeleteSubject(UUID viewUuid, String gridId, String rowId,
      UiActionRequest request) {
    // TODO Auto-generated method stub
  }

}
