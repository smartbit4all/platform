package org.smartbit4all.api.invocation;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.collection.StoredReference;
import org.smartbit4all.api.invocation.bean.InvocationStackItem;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.api.object.bean.ObjectPropertyValueSet;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This object manages the stack of an invocation.
 */
public final class InvocationStack {

  /**
   * The URI of the stack.
   */
  private URI uri;

  StoredReference<InvocationStackItem> stackRef;

  private ObjectApi objectApi;

  private InvocationStackItem rootItem;

  /**
   * Construct a stack based on a {@link StoredReference}.
   * 
   * @param objectApi
   * @param stackRef
   * @param rootItem
   */
  public InvocationStack(ObjectApi objectApi, StoredReference<InvocationStackItem> stackRef,
      InvocationStackItem rootItem) {
    super();
    Objects.requireNonNull(stackRef);
    this.objectApi = objectApi;
    this.rootItem = rootItem;
    this.stackRef = stackRef;
    this.uri = stackRef.getUri();
  }

  public InvocationStack(ObjectApi objectApi, URI stackUri,
      InvocationStackItem rootItem) {
    super();
    Objects.requireNonNull(stackUri);
    this.objectApi = objectApi;
    this.rootItem = rootItem;
    this.uri = stackUri;
  }

  public final URI getUri() {
    return uri;
  }

  final InvocationStackItem getRootItem() {
    return rootItem;
  }

  public Object getValue(String variable) {
    Object object = rootItem.getVariables().get(variable);
    if (object == null) {
      // Check this input variable.
      object = rootItem.getInputParameters().get(variable);
    }
    return object;
  }

  public <T> T getValueAs(String variable, Class<T> clazz) {
    return objectApi.asType(clazz, getValue(variable));
  }

  public String getValueAsString(String variable) {
    return getValueAs(variable, String.class);
  }

  public InvocationStack set(String variable, Object val) {
    rootItem.putVariablesItem(variable, val);
    return this;
  }

  /**
   * We have a list of property values to set in the context objects.
   * 
   * @param values The values sets are named context objects and their properties to set.
   * @return
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  public InvocationStack apply(ObjectPropertyValueSet values) {
    if (values == null) {
      return this;
    }
    for (ObjectPropertyValue value : values.getValues()) {
      if (value.getContextObject() != null) {
        if (value.getPath().isEmpty()) {
          // We set the context variable itself.
          set(value.getContextObject(), value.getValue());
        } else {
          Object contextObject = getValue(value.getContextObject());
          if (contextObject instanceof Map) {
            objectApi.setValueIntoObjectMap((Map) contextObject, value.getValue(),
                StringConstant.toArray(value.getPath()));
          } else {
            ObjectNode node = objectApi.create(null, contextObject);
            node.setValue(value.getValue(), StringConstant.toArray(value.getPath()));
            set(value.getContextObject(), node.getObject());
          }
        }
      } else {
        // Throw an exception, unable to create untyped context object.
        throw new IllegalArgumentException(
            "Unable to construct the context object to set its variable (" + value + ")");
      }
    }
    return this;
  }

  StoredReference<InvocationStackItem> getStackRef() {
    return stackRef;
  }

}
