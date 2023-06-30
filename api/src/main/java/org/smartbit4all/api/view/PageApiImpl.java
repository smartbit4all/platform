package org.smartbit4all.api.view;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generic UI/BFF API implementation for loading and performing actions.
 *
 * @author matea
 *
 * @param <M> type of the model which this API handles
 */
public abstract class PageApiImpl<M> implements PageApi<M> {

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected ViewApi viewApi;

  private Class<M> clazz;

  public PageApiImpl(Class<M> clazz) {
    this.clazz = clazz;
  }

  @Override
  public Class<M> getClazz() {
    return clazz;
  }

  protected M getModel(UUID viewUuid) {
    return viewApi.getModel(viewUuid, getClazz());
  }

  protected void setModel(UUID viewUuid, M model) {
    viewApi.getView(viewUuid).setModel(model);
  }

  @Override
  public M load(UUID viewUuid) {
    return getModel(viewUuid);
  }

  protected M extractClientModel(UiActionRequest request) {
    return extractParam(getClazz(), UiActions.MODEL, request.getParams());
  }

  protected <T> T extractParam(Class<T> clazz, String paramName, Map<String, Object> parameters) {
    T result = extractParamUnChecked(clazz, paramName, parameters);
    if (result == null) {
      throw new IllegalArgumentException(paramName + " parameter not found in UI request");
    }
    return result;
  }

  protected <T> T extractParamUnChecked(Class<T> clazz, String paramName,
      Map<String, Object> parameters) {
    Object param = parameters.get(paramName);
    if (param == null) {
      return null;
    }
    T typedParam = objectApi.asType(clazz, param);
    parameters.put(paramName, typedParam);
    return typedParam;
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
    List<T> typedParam = objectApi.asList(clazz, (List<?>) param);
    parameters.put(paramName, typedParam);
    return typedParam;
  }

}
