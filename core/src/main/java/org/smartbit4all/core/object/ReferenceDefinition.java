package org.smartbit4all.core.object;

import org.smartbit4all.api.object.bean.PropertyKindEnum;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;

/**
 * The {@link ObjectApi} construct this meta object for every {@link ReferenceDefinitionData}. This
 * is used by the algorithms directly. This descriptor can be accessed from the objects as outgoing
 * and incoming references.
 * 
 * @author Peter Boros
 */
public class ReferenceDefinition {

  /**
   * The stored data of the reference
   */
  private final ReferenceDefinitionData data;

  ReferenceDefinition(ReferenceDefinitionData data) {
    super();
    this.data = data;
  }

  private ObjectDefinition<?> source;

  private ObjectDefinition<?> target;

  public final ObjectDefinition<?> getSource() {
    return source;
  }

  final void setSource(ObjectDefinition<?> source) {
    this.source = source;
  }

  public final String getSourcePropertyPath() {
    return data.getSource().getPropertyPath();
  }

  public final PropertyKindEnum getSourcePropertyKind() {
    return data.getSource().getPropertyKind();
  }

  public final ObjectDefinition<?> getTarget() {
    return target;
  }

  final void setTarget(ObjectDefinition<?> target) {
    this.target = target;
  }

  public final String getTargetPropertyPath() {
    return data.getTarget().getPropertyPath();
  }

  public final PropertyKindEnum getTargetPropertyKind() {
    return data.getTarget().getPropertyKind();
  }

}
