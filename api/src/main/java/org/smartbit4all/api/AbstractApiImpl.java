package org.smartbit4all.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generic UI/BFF API implementation for loading and performing actions.
 * 
 * @author matea
 *
 * @param <M>
 */
public abstract class AbstractApiImpl<M> {

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected ViewApi viewApi;

  protected abstract Class<M> getClazz();

  protected ObjectNode loadModel(UUID viewUuid) {
    URI modelUri = viewApi.getView(viewUuid).getObjectUri();
    return objectApi.load(modelUri);
  }

  public M load(UUID viewUuid) {
    return loadModel(viewUuid).getObject(getClazz());
  }

  protected M extractClientModel(UiActionRequest request) {
    return extractParam(getClazz(), "model", request);
  }

  protected <T> T extractParam(Class<T> clazz, String paramName, UiActionRequest request) {
    Object param = request.getParams().get(paramName);
    if (param == null) {
      throw new IllegalArgumentException(paramName + " parameter not found in UI request");
    }
    return objectApi.asType(clazz, param);
  }

  protected <T> List<T> extractListParam(Class<T> clazz, String paramName,
      UiActionRequest request) {
    Object param = request.getParams().get(paramName);
    if (param == null) {
      throw new IllegalArgumentException(paramName + " parameter not found in UI request");
    }
    if (!(param instanceof List)) {
      throw new IllegalArgumentException(paramName + " parameter is not List<>!");
    }
    return objectApi.asList(clazz, (List<?>) param);
  }

  protected void updateModel(UUID viewUuid, UnaryOperator<M> update) {
    ObjectNode modelNode = loadModel(viewUuid);
    modelNode.modify(getClazz(), update);
    objectApi.save(modelNode);
  }

}
