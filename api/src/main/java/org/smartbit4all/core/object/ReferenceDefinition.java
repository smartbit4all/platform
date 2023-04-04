package org.smartbit4all.core.object;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.core.object.PropertyMeta.PropertyKind;

/**
 * The {@link ObjectApi} construct this meta object for every {@link ReferenceDefinitionData}. This
 * is used by the algorithms directly. This descriptor can be accessed from the objects as outgoing
 * and incoming references.
 * 
 * @author Peter Boros
 */
public class ReferenceDefinition {

  private static final Logger log = LoggerFactory.getLogger(ReferenceDefinition.class);

  /**
   * The stored data of the reference
   */
  private final ReferenceDefinitionData data;

  ReferenceDefinition(ReferenceDefinitionData data) {
    super();
    this.data = data;
  }

  /**
   * The source object definition.
   */
  private ObjectDefinition<?> source;

  /**
   * The source object property that contains the reference to the target.
   */
  private PropertyMeta sourcePropertyMeta;

  private ObjectDefinition<?> target;

  public final ObjectDefinition<?> getSource() {
    return source;
  }

  final void setSource(ObjectDefinition<?> source) {
    this.source = source;
    // TODO manage the path that is not just a simple property!
    sourcePropertyMeta = source.meta().getProperties().get(getSourcePropertyPath().toUpperCase());
  }

  public Object getSourceValue(Object object) {
    if (object == null) {
      return null;
    }
    if (sourcePropertyMeta == null) {
      log.warn("Unable to access the {} property in the {} object.",
          getSourcePropertyPath(),
          source.getQualifiedName());
      return null;
    }
    try {
      return sourcePropertyMeta.getGetter().invoke(object);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      log.error("Unable to access the {} property in the {} object. ({})",
          getSourcePropertyPath(),
          source.getQualifiedName(), e);
      return null;
    }
  }

  public Object getSourceValue(Map<String, Object> object) {
    if (object == null) {
      return null;
    }
    return object.get(getSourcePropertyPath());
  }

  public void setSourceValue(Object object, Object value) {
    if (object == null) {
      return;
    }
    if (sourcePropertyMeta == null) {
      log.warn("Unable to set the {} property in the {} object.",
          getSourcePropertyPath(),
          source.getQualifiedName());
      return;
    }
    try {
      sourcePropertyMeta.getSetter().invoke(object, value);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      log.error("Unable to set the {} property in the {} object. ({})",
          getSourcePropertyPath(),
          source.getQualifiedName(), e);
    }
  }

  public void setSourceValue(Map<String, Object> object, Object value) {
    if (object == null) {
      return;
    }
    object.put(getSourcePropertyPath(), value);
  }

  public PropertyKind getSourceKind() {
    if (sourcePropertyMeta == null) {
      return PropertyKind.VALUE;
    }
    if (sourcePropertyMeta.getType().isAssignableFrom(List.class)) {
      return PropertyKind.COLLECTION;
    }
    if (sourcePropertyMeta.getType().isAssignableFrom(Map.class)) {
      return PropertyKind.COLLECTION;
    }
    return PropertyKind.VALUE;
  }

  public final ReferencePropertyKind getReferencePropertyKind() {
    return data.getPropertyKind();
  }

  public final String getSourcePropertyPath() {
    return data.getPropertyPath();
  }

  public final ObjectDefinition<?> getTarget() {
    return target;
  }

  final void setTarget(ObjectDefinition<?> target) {
    this.target = target;
  }

  public AggregationKind getAggregation() {
    return data.getAggregation();
  }

  public URI getTargetValueSet() {
    return data.getTargetValueSet();
  }

}
