package org.smartbit4all.api.view;

import java.util.UUID;
import org.smartbit4all.api.view.annotation.InitModel;
import org.smartbit4all.api.view.bean.View;

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
