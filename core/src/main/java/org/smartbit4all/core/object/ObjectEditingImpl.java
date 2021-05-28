package org.smartbit4all.core.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.constraint.ObjectConstraintPublisher;

public class ObjectEditingImpl implements ObjectEditing {

  private static final Logger log = LoggerFactory.getLogger(ObjectEditingImpl.class);

  // TODO delete, move to implementations
  protected ApiObjectRef ref;

  protected ObjectConstraintPublisher constraints;

  @Override
  public ObjectConstraintPublisher constraints() {
    return constraints;
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    log.warn("Unhandled command: {}/{}", commandPath, command);
  }

  protected void checkParamNumber(String command, int number, Object... params) {
    if (params == null || params.length != 1) {
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

}
