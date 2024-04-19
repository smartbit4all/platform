package org.smartbit4all.api.view;

import static java.util.stream.Collectors.toList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.view.bean.UiAction;

public class ActionManagementApiImpl extends PrimaryApiImpl<ActionSupplierApi>
    implements ActionManagementApi {

  public ActionManagementApiImpl() {
    super(ActionSupplierApi.class);
  }

  @Override
  public List<UiAction> calculateActions(Object item, String... menus) {
    List<String> menuList = menus == null ? Collections.emptyList() : Arrays.asList(menus);
    return calculateActions(item, menuList);
  }

  @Override
  public List<UiAction> calculateActions(Object item, List<String> menus) {
    Objects.nonNull(menus);
    List<UiAction> result = new ArrayList<>();
    List<ActionSupplierApi> extendingApis =
        getContributionApis().values().stream().filter(a -> a.extending(item, menus))
            .collect(toList());
    for (ActionSupplierApi api : extendingApis) {
      result = api.buildActions(item, menus, result);
    }
    for (ActionSupplierApi api : extendingApis) {
      result = api.postProcessActions(item, menus, result);
    }
    // TODO Now apply the ACL by knowing the ACL conditions of the UiActions.
    return result;
  }

}
