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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import org.smartbit4all.api.object.bean.ContextObjectData;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

/**
 * Represents a container for contextual objects used during evaluation, scripting, or data mapping
 * operations.
 * <p>
 * A {@code ContextObject} can operate in two modes:
 * <ul>
 * <li><b>Single item context</b> - wrapping a single unnamed {@link ContextObjectItem}.</li>
 * <li><b>Multi-item context</b> - wrapping a map of named {@link ContextObjectItem}s.</li>
 * </ul>
 * It provides methods to set, retrieve, and update context items which can be plain Java objects,
 * {@link ObjectNode}s, or {@link URI}-based references. Context objects are commonly used in
 * expression evaluation (SpEL), scripting environments, and for mapping values across different
 * execution contexts.
 *
 * <p>
 * This class is thread-safe due to the use of a {@link ReadWriteLock} when mutating internal state.
 * </p>
 */
public class ContextObject {

  private final Map<String, ContextObjectItem> items = new HashMap<>();

  private ContextObjectItem singleContextItem;

  private static final String SINGLE_CONTEXT_ITEM = "singleContextItem";

  public static final String NODE_POSTFIX = "Node";

  public static final String OBJ_NODE = "objNode";

  public static final String OBJ = "obj";

  public static final String INVOCATION_RESULT = "invocationResult";

  private WeakReference<ObjectApi> objectApiRef;

  /**
   * The read-write lock for the context object.
   */
  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

  private ObjectApi objectApi() {
    ObjectApi result = objectApiRef == null ? null : objectApiRef.get();
    if (result == null) {
      throw new IllegalStateException("The object api is not set.");
    }
    return result;
  }

  /**
   * Creates a new {@code ContextObject} bound to the given {@link ObjectApi}.
   *
   * @param objectApi the object API reference used to resolve object nodes
   */
  ContextObject(ObjectApi objectApi) {
    super();
    this.objectApiRef = new WeakReference<>(objectApi);
  }

  public ContextObject getSubContext() {
    rwLock.readLock().lock();
    try {
      ContextObject result = new ContextObject(objectApi());
      return result.init(this);
    } finally {
      rwLock.readLock().unlock();
    }
  }

  /**
   * Initializes this context object with the contents of another {@link ContextObject}. This
   * performs a shallow copy of its items and single item reference.
   *
   * @param from the source context to copy from
   * @return this instance for method chaining
   */
  public ContextObject init(ContextObject from) {
    // Acquire source read lock first, then this write lock to copy a stable snapshot.
    from.rwLock.readLock().lock();
    try {
      rwLock.writeLock().lock();
      try {
        singleContextItem = from.singleContextItem;
        items.clear();
        items.putAll(from.items);
        return this;
      } finally {
        rwLock.writeLock().unlock();
      }
    } finally {
      from.rwLock.readLock().unlock();
    }
  }

  /**
   * Initializes this context object with the contents of a {@link ContextObjectData}. Creates new
   * {@link ContextObjectItem}s for all provided data items.
   *
   * @param from the source context data to copy from
   * @return this instance for method chaining
   */
  public ContextObject init(ContextObjectData from) {
    rwLock.writeLock().lock();
    try {
      if (from.getSingleItem() != null) {
        singleContextItem = new ContextObjectItem(objectApi(), from.getSingleItem());
      } else {
        singleContextItem = null;
      }
      items.clear();
      items.putAll(from.getItems().stream().map(di -> new ContextObjectItem(objectApi(), di))
          .collect(toMap(i -> i.getName(), i -> i)));
      return this;
    } finally {
      rwLock.writeLock().unlock();
    }
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
    checkName(name);
    rwLock.readLock().lock();
    try {
      if (singleContextItem == null) {
        throw new IllegalStateException("This instance holds multiple items already!");
      }
      final var ctx = new ContextObject(objectApi());
      ctx.rwLock.writeLock().lock();
      try {
        ctx.items.put(name, singleContextItem);
      } finally {
        ctx.rwLock.writeLock().unlock();
      }
      return ctx;
    } finally {
      rwLock.readLock().unlock();
    }
  }

  private final void checkName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException(
          "The name of the context item can not be null, empty or blank.");
    }
  }

  /**
   * Set the context as an URI to load when the mapping tries to access the values of this object.
   * 
   * @param name The name of the context object.
   * @param uri The uri of the object.
   * @return
   */
  public ContextObject set(String name, URI uri) {
    checkName(name);
    rwLock.writeLock().lock();
    try {
      items.put(name, new ContextObjectItem(objectApi(), name, uri));
      return this;
    } finally {
      rwLock.writeLock().unlock();
    }
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
    checkName(name);
    rwLock.writeLock().lock();
    try {
      items.put(name, new ContextObjectItem(objectApi(), name, object));
      return this;
    } finally {
      rwLock.writeLock().unlock();
    }
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
    checkName(name);
    rwLock.writeLock().lock();
    try {
      items.put(name, new ContextObjectItem(objectApi(), name, node));
      return this;
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  /**
   * Set the context as an URI to load when the mapping tries to access the values of this object.
   * 
   * @param uri The uri of the object.
   * @return
   */
  public ContextObject set(URI uri) {
    rwLock.writeLock().lock();
    try {
      singleContextItem = new ContextObjectItem(objectApi(), SINGLE_CONTEXT_ITEM, uri);
      return this;
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  /**
   * Set the context as an object that can be used when the mapping tries to access the values of
   * this object.
   * 
   * @param object The object itself that can be a single value or a Map also.
   * @return
   */
  public ContextObject set(Object object) {
    rwLock.writeLock().lock();
    try {
      singleContextItem = new ContextObjectItem(objectApi(), SINGLE_CONTEXT_ITEM, object);
      return this;
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  /**
   * Set the context as an {@link ObjectNode} that can be used when the mapping tries to access the
   * values of this context object.
   * 
   * @param node The object node.
   * @return
   */
  public ContextObject set(ObjectNode node) {
    rwLock.writeLock().lock();
    try {
      singleContextItem = new ContextObjectItem(objectApi(), SINGLE_CONTEXT_ITEM, node);
      return this;
    } finally {
      rwLock.writeLock().unlock();
    }
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


    rwLock.writeLock().lock();
    try {
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
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  /**
   * Constructs an evaluation context for the {@link Expression} of the SpEL.
   * 
   * @return
   */
  public final EvaluationContext getEvaluationContext() {
    rwLock.readLock().lock();
    try {
      StandardEvaluationContext result = new StandardEvaluationContext();
      if (singleContextItem != null) {
        // We should set the root object and also the variable.
        result.setRootObject(singleContextItem.getValue());
      }
      for (ContextObjectItem contextObject : items.values()) {
        result.setVariable(contextObject.getName(), contextObject.getValue());
      }
      return result;
    } finally {
      rwLock.readLock().unlock();
    }
  }

  /**
   * Constructs an evaluation context for the {@link Expression} of the SpEL.
   * 
   * @return
   */
  public final Bindings getScriptBindings(ScriptEngine engine) {
    rwLock.readLock().lock();
    try {
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
    } finally {
      rwLock.readLock().unlock();
    }
  }

  /**
   * Get the value identified by the path from the context set to the mapping.
   * 
   * @param path The path of the property to get.
   * @return The value denoted by the path.
   */
  public final Object getValueFromContext(List<String> path) {
    Objects.requireNonNull(path);
    rwLock.readLock().lock();
    try {
      ContextObjectItem contextObject;
      List<String> finalPath = new ArrayList<>();
      contextObject = findItem(path, finalPath);
      contextObject.getRwLock().readLock().lock();
      try {
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
      } finally {
        contextObject.getRwLock().readLock().unlock();
      }
    } finally {
      rwLock.readLock().unlock();
    }
  }

  public final <T> T getItemAsObject(String itemName, Class<T> clazz) {
    rwLock.readLock().lock();
    try {
      ContextObjectItem contextObjectItem = items.get(itemName);
      if (contextObjectItem == null) {
        return null;
      }
      contextObjectItem.getRwLock().readLock().lock();
      try {
        return contextObjectItem.objectNode().getObject(clazz);
      } finally {
        contextObjectItem.getRwLock().readLock().unlock();
      }
    } finally {
      rwLock.readLock().unlock();
    }
  }

  private final ContextObjectItem findItem(List<String> path, List<String> finalPath) {
    // Caller should hold at least the read lock; acquire read lock defensively.
    ContextObjectItem contextObject;
    if (singleContextItem != null) {
      // The whole path is evaluated inside the single context object.
      contextObject = singleContextItem;
      finalPath.addAll(path);
    } else {
      // The first segment of the path identifies the context object and the rest is the path
      // inside.
      if (path.isEmpty()) {
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

  public void setValue(List<String> path, Object value, boolean merge) {
    Objects.requireNonNull(path);
    if (path.size() == 1) {
      // Set a context object itself
      String itemName = path.get(0);
      boolean setValue = false;
      rwLock.readLock().lock();
      try {
        setValue = setValueImpl(itemName, value, merge, false);
      } finally {
        rwLock.readLock().unlock();
      }
      if (setValue) {
        rwLock.writeLock().lock();
        try {
          setValueImpl(itemName, value, merge, true);
        } finally {
          rwLock.writeLock().unlock();
        }
      }
      return;
    }
    List<String> finalPath = new ArrayList<>();

    ContextObjectItem contextObjectItem;
    ObjectNode objectNode = null;
    rwLock.readLock().lock();
    try {
      contextObjectItem = findItem(path, finalPath);
      objectNode = contextObjectItem.objectNode();
      if (contextObjectItem != null && objectNode != null) {
        contextObjectItem.getRwLock().writeLock().lock();
        try {
          if (merge && !objectApi().isValue(value)) {
            objectNode.mergeValues(getMergeMap(finalPath, objectApi().toMapObject(value)));
          } else {
            if (ObjectUtils.isEmpty(finalPath)) {
              objectNode.setObject(value);
            } else {
              objectNode.setValue(value, StringConstant.toArray(finalPath));
            }
          }
        } finally {
          contextObjectItem.getRwLock().writeLock().unlock();
        }
      }
    } finally {
      rwLock.readLock().unlock();
    }

  }

  /**
   * @param itemName
   * @param value
   * @param merge
   * @param executeSet This indicator is true if this function can add new item to the context - so
   *        we have write lock for the whole context object. If it is false then the existing item
   *        can be modified or set only because we have a readLock for the context object.
   * @return
   */
  private final boolean setValueImpl(String itemName, Object value, boolean merge,
      boolean executeSet) {
    ContextObjectItem item;
    ObjectNode objectNode;
    item = items.get(itemName);

    if (item != null) {
      item.getRwLock().writeLock().lock();
      try {
        objectNode = item.objectNode();
        if (objectNode != null) {
          if (merge) {
            objectNode.setValues(objectApi().toMapObject(value));
          } else {
            objectNode.setObject(value);
          }
        } else {
          item.data._object(value);
        }
      } finally {
        item.getRwLock().writeLock().unlock();
      }
      // Managed to set or merge the value so there is no need to set in a writeLock block.
      return false;
    } else {
      if (executeSet) {
        items.put(itemName, new ContextObjectItem(objectApi(), itemName, value));
        return false;
      }
      // In this case we would add a new item to the context object but we have only a readLock on
      // instead of writeLock. So it is not safe to add any item to the items map of the context.
      return true;
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

  ContextObjectItem getItem(String name) {
    rwLock.readLock().lock();
    try {
      return items.get(name);
    } finally {
      rwLock.readLock().unlock();
    }
  }

  public ObjectNode getObjectNode(String name) {
    rwLock.readLock().lock();
    try {
      ContextObjectItem item = items.get(name);
      if (item == null) {
        return null;
      }
      return item.objectNode();
    } finally {
      rwLock.readLock().unlock();
    }
  }

  public boolean exists(String name) {
    rwLock.readLock().lock();
    try {
      return items.containsKey(name);
    } finally {
      rwLock.readLock().unlock();
    }
  }

  /**
   * Checks whether this context object houses multiple or a singular item.
   * 
   * @return true, if there is an unnamed singular item being wrapped, false otherwise
   */
  public boolean isSingleItemContext() {
    rwLock.readLock().lock();
    try {
      return singleContextItem != null;
    } finally {
      rwLock.readLock().unlock();
    }
  }

  Map<String, ContextObjectItem> getItems() {
    rwLock.readLock().lock();
    try {
      if (singleContextItem != null) {
        return Collections.singletonMap(StringConstant.EMPTY, singleContextItem);
      }
      return Collections.unmodifiableMap(new HashMap<>(items));
    } finally {
      rwLock.readLock().unlock();
    }
  }

  public Map<String, Object> getItemValues() {
    rwLock.readLock().lock();
    try {
      if (singleContextItem != null) {
        return Collections.singletonMap(StringConstant.EMPTY, singleContextItem.getValue());
      }
      return items.entrySet().stream()
          .filter(
              e -> e.getKey() != null && e.getValue() != null && e.getValue().getValue() != null)
          .collect(toMap(e -> e.getKey(), e -> e.getValue().getValue()));
    } finally {
      rwLock.readLock().unlock();
    }
  }

  public List<String> getItemNames() {
    rwLock.readLock().lock();
    try {
      if (singleContextItem != null) {
        return List.of(StringConstant.EMPTY);
      }
      return new ArrayList<>(items.keySet());
    } finally {
      rwLock.readLock().unlock();
    }
  }

  @Override
  public String toString() {
    return getItemValues().toString();
  }

}
