package org.smartbit4all.api.view;

import java.util.Arrays;
import java.util.List;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.view.UiActions.UiActionBuilder;
import org.smartbit4all.api.view.bean.UiAction;

public class ActionSupplier1 extends ActionSupplierApiImpl {

  public ActionSupplier1() {
    super(ActionSupplier1.class.getName(), Arrays.asList(SampleCategory.class.getName()),
        Arrays.asList(ViewApiTestConfig.MENU1));
  }

  @Override
  public List<UiAction> buildActions(Object item, List<String> menus,
      List<UiAction> currentActions) {
    SampleCategory cat = (SampleCategory) item;
    UiActionBuilder builder = UiActions.builder(currentActions);
    builder.add(cat.getColor().name());
    return builder.build();
  }

}
