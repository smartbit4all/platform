package org.smartbit4all.api.view;

import java.util.UUID;
import org.smartbit4all.api.view.annotation.InitModel;
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

  @InitModel
  M initModel(View view);

  /**
   * Returns the page's model's class.
   *
   * @return
   */
  Class<M> getClazz();

}
