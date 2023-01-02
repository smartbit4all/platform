package org.smartbit4all.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generic UI/BFF API implementation for loading and performing actions.
 *
 * @author matea
 *
 * @param <M> type of the model which this API handles
 */
public abstract class AbstractApiImpl<M> {

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected ViewApi viewApi;

  /**
   * Simply return M.class.
   *
   * @return
   */
  protected abstract Class<M> getClazz();

  protected abstract M createModel(View view);

  protected M getModel(UUID viewUuid) {
    View view = viewApi.getView(viewUuid);
    Object modelObject = view.getModel();
    if (modelObject == null) {
      M model = createModel(view);
      view.setModel(model);
      return model;
    }
    if (getClazz().isInstance(modelObject)) {
      return (M) modelObject;
    }
    M model = objectApi.asType(getClazz(), view.getModel());
    // this is to ensure View holds a typed object, not a Map representing the object
    view.setModel(model);
    return model;
  }

  protected void setModel(UUID viewUuid, M model) {
    viewApi.getView(viewUuid).setModel(model);
  }

  public M load(UUID viewUuid) {
    return getModel(viewUuid);
  }

  protected M extractClientModel(UiActionRequest request) {
    return extractParam(getClazz(), "model", request.getParams());
  }

  protected <T> T extractParam(Class<T> clazz, String paramName, Map<String, Object> parameters) {
    Object param = parameters.get(paramName);
    if (param == null) {
      throw new IllegalArgumentException(paramName + " parameter not found in UI request");
    }
    return objectApi.asType(clazz, param);
  }

  protected <T> List<T> extractListParam(Class<T> clazz, String paramName,
      Map<String, Object> parameters) {
    Object param = parameters.get(paramName);
    if (param == null) {
      throw new IllegalArgumentException(paramName + " parameter not found in UI request");
    }
    if (!(param instanceof List)) {
      throw new IllegalArgumentException(paramName + " parameter is not List<>!");
    }
    return objectApi.asList(clazz, (List<?>) param);
  }

}
