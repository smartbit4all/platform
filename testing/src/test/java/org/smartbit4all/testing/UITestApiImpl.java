package org.smartbit4all.testing;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.bean.View;
import org.springframework.beans.factory.annotation.Autowired;

public class UITestApiImpl implements UITestApi {

  @Autowired
  private ViewApi viewApi;

  @Autowired
  private ViewContextService viewContextService;

  @Override
  public View getView(String viewName) {
    List<View> views = viewApi.getViews(viewName);
    if (views.isEmpty()) {
      return null;
    }
    return views.get(0);
  }

  @Override
  public void runInViewContext(UUID viewContextUUID, Runnable process) throws Exception {
    viewContextService.execute(
        viewContextUUID,
        () -> process.run());
  }

}
