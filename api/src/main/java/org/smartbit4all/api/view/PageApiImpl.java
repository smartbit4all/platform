package org.smartbit4all.api.view;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewState;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.utility.ObjectStreamUtils;
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

  private static final List<ViewState> statesToDefaultCloseAt =
      Arrays.asList(ViewState.OPENED, ViewState.TO_OPEN, ViewState.OPEN_PENDING);

  @Autowired
  protected ObjectApi objectApi;

  @Autowired
  protected ViewApi viewApi;

  @Autowired
  private ViewContextService viewContextService;

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

  /**
   * Executes a modification on the model.
   *
   * @param view The view.
   * @param modelModification The modification lambda. The model can be modified by reference (no
   *        replace)
   */
  protected final void execute(View view, Consumer<M> modelModification) {
    execute(view.getUuid(), modelModification);
  }

  /**
   * Executes a modification on the model.
   *
   * @param uuid The uuid of the view.
   * @param modelModification The modification lambda. The model can be modified by reference (no
   *        replace)
   */
  protected final void execute(UUID uuid, Consumer<M> modelModification) {
    M model = getModel(uuid);
    modelModification.accept(model);
    setModel(uuid, model);
  }

  @Override
  public M load(UUID viewUuid) {
    return getModel(viewUuid);
  }

  @Override
  public void defaultClose(UUID viewUuid, UiActionRequest request) {
    View view = viewApi.getView(viewUuid);
    if (statesToDefaultCloseAt.contains(view.getState())) {
      viewApi.closeView(viewUuid);
    }
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
    return parameters(viewUuid, false);
  }

  /**
   * Retrieve an instance of the view parameter helper that encapsulate the
   * {@link View#getParameters()} as value. So if we get values the parameter map will be updated
   * with the typed object to enhance the subsequent retrieves.
   *
   * @param viewUuid The uuid of the view.
   * @param all Recursively return all the parameters of the parent hierarchy.
   * @return
   */
  protected ObjectMapHelper parameters(UUID viewUuid, boolean all) {
    return parameters(viewApi.getView(viewUuid), all);
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
    return parameters(view, false);
  }

  /**
   * Retrieve an instance of the view parameter helper that encapsulate the
   * {@link View#getParameters()} as value. So if we get values the parameter map will be updated
   * with the typed object to enhance the subsequent retrieves.
   *
   * @param view The view itself.
   * @param all Recursively return all the parameters of the parent hierarchy.
   * @return
   */
  protected ObjectMapHelper parameters(View view, boolean all) {
    return new ObjectMapHelper(
        all ? viewApi.getAllParameters(view.getUuid()) : view.getParameters(), objectApi,
        view.getViewName()
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

  protected void resetInitialModel(UUID viewUuid) {
    View view = viewApi.getView(viewUuid);
    view.getParameters().put(ViewContexts.INITIAL_MODEL, getModel(viewUuid));
  }

  @Override
  public boolean hasModelChanged(UUID viewUuid, boolean includeChildComponents) {
    M currentModel = getModel(viewUuid);
    M persistedModel =
        parameters(viewUuid).get(ViewContexts.INITIAL_MODEL, getClazz());

    boolean anyChange = !modelEquals(currentModel, persistedModel);
    if (anyChange || !includeChildComponents) {
      // if this view is changed, or we don't need to check children, we have the result
      return anyChange;
    }
    return viewApi.getChildrenOfView(viewUuid).stream()
        .map(uuid -> viewApi.getView(uuid))
        .anyMatch(view -> {
          Object api = viewContextService.getApiByViewName(view.getViewName());
          if (api instanceof PageApi<?>) {
            return ((PageApi<?>) api).hasModelChanged(view.getUuid(), includeChildComponents);
          }
          return false;
        });
  }

  protected boolean modelEquals(M m1, M m2) {
    Map<String, Object> map1 = convertToMap(m1);
    Map<String, Object> map2 = convertToMap(m2);

    return ObjectStreamUtils.mapEq(map1, map2);
  }

  @SuppressWarnings("unchecked")
  protected Map<String, Object> convertToMap(M m1) {
    Map<String, Object> map1;
    if (m1 == null) {
      map1 = Collections.emptyMap();
    } else if (m1 instanceof Map) {
      map1 = (Map<String, Object>) m1;
    } else {
      map1 = objectApi.definition(getClazz()).toMap(m1);
    }
    return map1;
  }

}
