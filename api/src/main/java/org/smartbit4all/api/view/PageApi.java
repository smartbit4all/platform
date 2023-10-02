package org.smartbit4all.api.view;

import java.util.UUID;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;

/**
 * Generic BFF API interface.
 *
 * @author matea
 *
 * @param <M> Type of the model.
 */
public interface PageApi<M> {

  public static final String DEFAULT_CLOSE = "DEFAULT_CLOSE";

  M load(UUID viewUuid);

  /**
   * This method will be called when a view's model is needed but not present yet. It receives a
   * {@link View} containing all information available to initialize model, and must return the
   * model Object.
   *
   * @param view
   * @return
   */
  M initModel(View view);

  /**
   * Returns the page's model's class.
   *
   * @return
   */
  Class<M> getClazz();

  /**
   * Default view close uiAction handler.<br/>
   * Used ui action code: {@link #DEFAULT_CLOSE}
   * 
   * @param viewUuid
   * @param request
   */
  @ActionHandler(DEFAULT_CLOSE)
  void defaultClose(UUID viewUuid, UiActionRequest request);

}
