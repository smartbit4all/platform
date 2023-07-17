package org.smartbit4all.api.view;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.utility.StringConstant;
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

  /**
   * Retrieve an instance of the view parameter helper that encapsulate the
   * {@link View#getParameters()} as value. So if we get values the parameter map will be updated
   * with the typed object to enhance the subsequent retrieves.
   * 
   * @param viewUuid The uuid of the view.
   * @return
   */
  protected ObjectMapHelper parameters(UUID viewUuid) {
    return parameters(viewApi.getView(viewUuid));
  }

  /**
   * Retrieve an instance of the view parameter helper that encapsulate the
   * {@link View#getParameters()} as value. So if we get values the parameter map will be updated
   * with the typed object to enhance the subsequent retrieves.
   * 
   * @param view The view itself.
   * @return
   */
  protected ObjectMapHelper parameters(View view) {
    return new ObjectMapHelper(view.getParameters(), objectApi, view.getViewName()
        + StringConstant.SPACE_HYPHEN_SPACE + view.getUuid() + " view parameters");
  }

  /**
   * Retrieve an instance of the view variable helper that encapsulate the
   * {@link View#getVariables()} as value. So if we get values the parameter map will be updated
   * with the typed object to enhance the subsequent retrieves.
   * 
   * @param viewUuid The uuid of the view.
   * @return
   */
  protected ObjectMapHelper variables(UUID viewUuid) {
    return variables(viewApi.getView(viewUuid));
  }

  /**
   * Retrieve an instance of the view variables helper that encapsulate the
   * {@link View#getVariables()} as value. So if we get values the parameter map will be updated
   * with the typed object to enhance the subsequent retrieves.
   * 
   * @param view The view itself.
   * @return
   */
  protected ObjectMapHelper variables(View view) {
    return new ObjectMapHelper(view.getVariables(), objectApi, view.getViewName()
        + StringConstant.SPACE_HYPHEN_SPACE + view.getUuid() + " view variables");
  }

  protected ObjectMapHelper actionRequestHelper(UiActionRequest request) {
    return new ObjectMapHelper(request.getParams(), objectApi, request.getCode()
        + StringConstant.SPACE_HYPHEN_SPACE + request.getIdentifier()
        + StringConstant.SPACE_HYPHEN_SPACE + request.getPath() + " action parameters");
  }

  protected M extractClientModel(UiActionRequest request) {
    return extractParam(getClazz(), UiActions.MODEL, request.getParams());
  }


  /**
   * @param <T>
   * @param clazz
   * @param paramName
   * @param parameters
   * @return
   * @deprecated Use the parameter() ObjectMapHelper rather!
   */
  @Deprecated
  protected <T> T extractParam(Class<T> clazz, String paramName, Map<String, Object> parameters) {
    if (!parameters.containsKey(paramName)) {
      throw new IllegalArgumentException(paramName + " parameter not found in UI request");
    }
    return extractParamUnChecked(clazz, paramName, parameters);
  }

  /**
   * @param <T>
   * @param clazz
   * @param paramName
   * @param parameters
   * @return
   * @deprecated Use the parameter() ObjectMapHelper rather!
   */
  @Deprecated
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

  /**
   * @param <T>
   * @param clazz
   * @param paramName
   * @param parameters
   * @return
   * @deprecated Use the parameter() ObjectMapHelper rather!
   */
  @Deprecated
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
