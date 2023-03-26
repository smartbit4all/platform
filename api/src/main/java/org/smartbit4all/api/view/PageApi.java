package org.smartbit4all.api.view;

import java.util.UUID;
import org.smartbit4all.api.view.bean.View;

/**
 * Generic BFF API interface.
 *
 * @author matea
 *
 * @param <M> Type of the model.
 */
public interface PageApi<M> {

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

}
