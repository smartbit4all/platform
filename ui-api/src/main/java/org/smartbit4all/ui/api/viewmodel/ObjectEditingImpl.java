package org.smartbit4all.ui.api.viewmodel;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.constraint.ObjectConstraintPublisher;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;

public class ObjectEditingImpl implements ObjectEditing {

  private static final Logger log = LoggerFactory.getLogger(ObjectEditingImpl.class);

  protected ApiObjectRef ref;

  protected ObjectConstraintPublisher constraints;

  protected NavigationTarget navigationTarget;

  protected UUID navigationTargetUUID;

  public ObjectEditingImpl() {
    navigationTarget = currentConstructionUUID.get();
    if (navigationTarget != null) {
      navigationTargetUUID = navigationTarget.getUuid();
    }
  }

  @Override
  public ObjectConstraintPublisher constraints() {
    return constraints;
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    log.warn("Unhandled command: {}/{}", commandPath, command);
  }

  protected void checkParamNumber(String command, int number, Object... params) {
    if (params == null || params.length != number) {
      StringBuilder sb = new StringBuilder();
      sb.append("Missing or too many parameters (expected ");
      sb.append(number);
      sb.append(", received");
      sb.append(params == null ? "null" : params.length);
      sb.append(")! Command: ");
      sb.append(command);
      throw new IllegalArgumentException(sb.toString());
    }
  }

  /**
   * Returns the parameter with the given index from the params array. Checks the param's type with
   * the given one and casts it.
   */
  protected <T> T getParam(Object[] params, int idx, Class<T> type) {
    if (params == null || params.length - 1 < idx) {
      String msg = String
          .format("The given index [%d] is out of the param range [%d]", idx, params.length);
      throw new IndexOutOfBoundsException(msg);
    }
    Object param = params[idx];
    if (param == null) {
      // FIXME assert if type is Void.class and throw exception if not?
      return null;
    }
    if (type.isAssignableFrom(param.getClass())) {
      return type.cast(param);
    } else {
      String msg = String.format("The given type [%s] can not be assigned from the param type [%s]",
          type.getName(), param.getClass().getName());
      throw new IllegalArgumentException(msg);
    }
  }

}
