package org.smartbit4all.api.view;

import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.view.bean.ViewContext;

public interface ViewContextService {

  /**
   * Returns current ViewContext.
   * 
   * @return
   */
  ViewContext getCurrentViewContext();

  /**
   * Returns ViewContext by URI.
   * 
   * @return
   */
  ViewContext getViewContext(UUID uuid);

  void updateCurrentViewContext(UnaryOperator<ViewContext> update);

  void updateViewContext(UUID uuid, UnaryOperator<ViewContext> update);

}
