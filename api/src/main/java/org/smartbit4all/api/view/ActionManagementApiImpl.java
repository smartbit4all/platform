package org.smartbit4all.api.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import static java.util.stream.Collectors.toList;

public class ActionManagementApiImpl extends PrimaryApiImpl<ActionSupplierApi>
    implements ActionManagementApi {

  public ActionManagementApiImpl() {
    super(ActionSupplierApi.class);
  }

  @Override
  public List<UiAction> calculateActions(Object item, String... menus) {
    List<UiAction> result = new ArrayList<>();
    List<String> menuList = menus == null ? Collections.emptyList() : Arrays.asList(menus);
    List<ActionSupplierApi> extendingApis =
        getContributionApis().values().stream().filter(a -> a.extending(item, menuList))
            .collect(toList());
    for (ActionSupplierApi api : extendingApis) {
      result = api.buildActions(item, menuList, result);
    }
    for (ActionSupplierApi api : extendingApis) {
      result = api.postProcessActions(item, menuList, result);
    }
    // TODO Now apply the ACL by knowing the ACL conditions of the UiActions.
    return result;
  }

}
