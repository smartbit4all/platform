package org.smartbit4all.core.object;

import static java.util.stream.Collectors.toMap;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import org.smartbit4all.api.object.bean.ContextObjectData;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

public class ContextObject {

  private final Map<String, ContextObjectItem> items = new HashMap<>();

  private ContextObjectItem singleContextItem;

  private static final String SINGLE_CONTEXT_ITEM = "singleContextItem";

  public static final String NODE_POSTFIX = "Node";

  public static final String OBJ_NODE = "objNode";

  public static final String OBJ = "obj";

  public static final String INVOCATION_RESULT = "invocationResult";

  private WeakReference<ObjectApi> objectApiRef;

  private ObjectApi objectApi() {
    return objectApiRef.get();
  }

  ContextObject(ObjectApi objectApi) {
    super();
    this.objectApiRef = new WeakReference<>(objectApi);
  }

  public ContextObject init(ContextObject from) {
    singleContextItem = from.singleContextItem;
    items.putAll(from.items);
    return this;
  }

  public ContextObject init(ContextObjectData from) {
    if (from.getSingleItem() != null) {
      singleContextItem = new ContextObjectItem(objectApi(), from.getSingleItem());
    }
    items.putAll(from.getItems().stream().map(di -> new ContextObjectItem(objectApi(), di))
        .collect(toMap(i -> i.getName(), i -> i)));
    return this;
  }

  /**
   * Converts this instance from a single context object item to a multi one.
   * 
   * @param name the {@link String} name to set for the context object item held exclusively in this
   *        instance for the new one
   * @return a new {@link ContextObject} holding the previously held context item with the provided
   *         name
   */
  public ContextObject toNamed(final String name) {
    if (!isSingleItemContext()) {
      throw new IllegalStateException("This instance holds multiple items already!");
    }

    final var ctx = new ContextObject(objectApi());
    ctx.items.put(name, singleContextItem);
    return ctx;
  }

  /**
   * Set the context as an URI to load when the mapping tries to access the values of this object.
   * 
   * @param name The name of the context object.
   * @param uri The uri of the object.
   * @return
   */
  public ContextObject set(String name, URI uri) {
    items.put(name, new ContextObjectItem(objectApi(), name, uri));
    return this;
  }

  /**
   * Set the context as an object that can be used when the mapping tries to access the values of
   * this object.
   * 
   * @param name The name of the context object.
   * @param object The object itself that can be a single value or a Map also.
   * @return
   */
  public ContextObject set(String name, Object object) {
    items.put(name, new ContextObjectItem(objectApi(), name, object));
    return this;
  }

  /**
   * Set the context as an {@link ObjectNode} that can be used when the mapping tries to access the
   * values of this context object.
   * 
   * @param name The name of the context object.
   * @param node The object node.
   * @return
   */
  public ContextObject set(String name, ObjectNode node) {
    items.put(name, new ContextObjectItem(objectApi(), name, node));
    return this;
  }

  /**
   * Set the context as an URI to load when the mapping tries to access the values of this object.
   * 
   * @param uri The uri of the object.
   * @return
   */
  public ContextObject set(URI uri) {
    singleContextItem = new ContextObjectItem(objectApi(), SINGLE_CONTEXT_ITEM, uri);
    return this;
  }

  /**
   * Set the context as an object that can be used when the mapping tries to access the values of
   * this object.
   * 
   * @param object The object itself that can be a single value or a Map also.
   * @return
   */
  public ContextObject set(Object object) {
    singleContextItem = new ContextObjectItem(objectApi(), SINGLE_CONTEXT_ITEM, object);
    return this;
  }

  /**
   * Set the context as an {@link ObjectNode} that can be used when the mapping tries to access the
   * values of this context object.
   * 
   * @param node The object node.
   * @return
   */
  public ContextObject set(ObjectNode node) {
    singleContextItem = new ContextObjectItem(objectApi(), SINGLE_CONTEXT_ITEM, node);
    return this;
  }

  /**
   * Sets multiple values in this context by their name.
   * 
   * @param values a {@link Map} of ordered pairs of (k, v), where k is the {@String} unique name of
   *        the item in this context, and v is either an {@link ObjectNode}, an {@link URI} or a
   *        plain Java object; null values are skipped; nullable
   * @return this instance
   */
  public ContextObject setAll(Map<String, ? extends Object> values) {
    if (values == null || values.isEmpty()) {
      return this;
    }


    for (final var e : values.entrySet()) {
      final String key = e.getKey();
      switch (e.getValue()) {
        case null -> {
        }
        case URI uri -> set(key, uri);
        case ObjectNode node -> set(key, node);
        default -> set(key, e.getValue());
      }
    }
    return this;
  }

  /**
   * Constructs an evaluation context for the {@link Expression} of the SpEL.
   * 
   * @return
   */
  public final EvaluationContext getEvaluationContext() {
    StandardEvaluationContext result = new StandardEvaluationContext();
    if (singleContextItem != null) {
      // We should set the root object and also the variable.
      result.setRootObject(singleContextItem.getValue());
    }
    for (ContextObjectItem contextObject : items.values()) {
      result.setVariable(contextObject.getName(), contextObject.getValue());
    }
    return result;
  }

  /**
   * Constructs an evaluation context for the {@link Expression} of the SpEL.
   * 
   * @return
   */
  public final Bindings getScriptBindings(ScriptEngine engine) {
    Bindings result = engine.createBindings();
    if (singleContextItem != null) {
      // We should set the root object and also the variable.
      result.put(OBJ, singleContextItem.getValue());
      result.put(OBJ_NODE, singleContextItem.objectNode());
    }
    for (ContextObjectItem contextObject : items.values()) {
      result.put(contextObject.getName(), contextObject.getValue());
      result.put(contextObject.getName() + NODE_POSTFIX, contextObject.objectNode());
    }
    return result;
  }

  /**
   * Get the value identified by the path from the context set to the mapping.
   * 
   * @param path The path of the property to get.
   * @return The value denoted by the path.
   */
  public final Object getValueFromContext(List<String> path) {
    Objects.requireNonNull(path);
    ContextObjectItem contextObject;
    List<String> finalPath = new ArrayList<>();
    contextObject = findItem(path, finalPath);
    if (finalPath.isEmpty()) {
      // We arrived we need the context value as is.
      return contextObject.getValue();
    }
    ObjectNode objectNode = contextObject.objectNode();
    if (objectNode == null) {
      throw new IllegalArgumentException(
          "Unable to load the context object " + contextObject.getName()
              + " it is not set correctly.");
    }
    return objectNode.getValue(StringConstant.toArray(finalPath));
  }

  private ContextObjectItem findItem(String itemName) {
    return items.get(itemName);
  }

  private ContextObjectItem findItem(List<String> path, List<String> finalPath) {
    ContextObjectItem contextObject;
    if (singleContextItem != null) {
      // The whole path is evaluated inside the single context object.
      contextObject = singleContextItem;
      finalPath.addAll(path);
    } else {
      // The first segment of the path identifies the context object and the rest is the path
      // inside.
      if (path.size() < 1) {
        throw new IllegalArgumentException(
            "Unable to get value from context, at least the context object must be denoted.");
      }
      String ctxName = path.get(0);
      contextObject = items.get(ctxName);
      if (contextObject == null) {
        throw new IllegalArgumentException(
            ctxName + " context object is not found.");
      }
      finalPath.addAll(path.subList(1, path.size()));
    }
    return contextObject;
  }

  @SuppressWarnings("unchecked")
  public void setValue(List<String> path, Object value, boolean merge) {
    Objects.requireNonNull(path);
    if (path.size() == 1) {
      // Set a context object itself
      String itemName = path.get(0);
      ContextObjectItem item = findItem(itemName);
      if (item != null) {
        ObjectNode objectNode = item.objectNode();
        if (objectNode != null) {
          if (merge) {
            objectNode.setValues(objectApi().toMapObject(value));
          } else {
            objectNode.setObject(value);
          }
        } else {
          set(itemName, value);
        }
      } else {
        set(itemName, value);
      }
      return;
    }
    ContextObjectItem contextObjectItem;
    List<String> finalPath = new ArrayList<>();
    contextObjectItem = findItem(path, finalPath);
    ObjectNode objectNode = contextObjectItem.objectNode();
    if (objectNode != null) {
      if (merge && !objectApi().isValue(value)) {
        objectNode.mergeValues(getMergeMap(finalPath, objectApi().toMapObject(value)));
      } else {
        if (ObjectUtils.isEmpty(finalPath)) {
          objectNode.setObject(value);
        } else {
          objectNode.setValue(value, StringConstant.toArray(finalPath));
        }
      }
    }
  }

  private final Map<String, Object> getMergeMap(List<String> finalPath,
      Map<String, Object> values) {
    if (finalPath == null) {
      return values;
    }
    Map<String, Object> result = new HashMap<>();
    Map<String, Object> currMap = result;
    for (int i = 0; i < finalPath.size(); i++) {
      String path = finalPath.get(i);
      if (i == (finalPath.size() - 1)) {
        currMap.put(path, values);
      } else {
        Map<String, Object> myMap = new HashMap<>();
        currMap.put(path, myMap);
        currMap = myMap;
      }
    }
    return result;
  }

  public ContextObjectItem getItem(String name) {
    return items.get(name);
  }

  /**
   * Checks whether this context object houses multiple or a singular item.
   * 
   * @return true, if there is an unnamed singular item being wrapped, false otherwise
   */
  public boolean isSingleItemContext() {
    return singleContextItem != null;
  }

  public Map<String, ContextObjectItem> getItems() {
    if (isSingleItemContext()) {
      return Collections.singletonMap(StringConstant.EMPTY, singleContextItem);
    }

    return Collections.unmodifiableMap(items);
  }

}
