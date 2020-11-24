package org.smartbit4all.ui.vaadin.view;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.smartbit4all.ui.common.controller.UIController;
import org.smartbit4all.ui.common.view.UIView;

/**
 * The generic parameter bean for {@link UIView} implementations. The {@link UIController} can
 * prepare the parameters of a currently opening or already opened view. The view can access the
 * parameters by calling the static get function. The get will always clear the parameter to avoid
 * unnecessary memory consumption. The parameter transition depends on the UI framework.
 * 
 * TODO We need some garbage collection to avoid memory problems.
 * 
 * @author Peter Boros
 */
public class UIViewParameter {

  /**
   * The parameter bean identified by some unique string like UUID or random number.
   */
  private final Map<String, Object> parameterMap = new HashMap<>();

  /**
   * Set the given parameter.
   * 
   * @param parameter
   * @return
   */
  public String push(String name, Object parameter) {
    String key = UUID.randomUUID().toString();
    parameterMap.put(key, parameter);
    return key;
  }

  /**
   * Retrieves the parameter and remove it from the store.
   * 
   * @param <T>
   * @param key The key of the parameter.
   * @param type The type of the parameter.
   * @return
   */
  public <T> T pop(String key, Class<T> type) {
    @SuppressWarnings("unchecked")
    T result = (T) parameterMap.get(key);
    return result;
  };

}
