package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.ObjectListMapping;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * The object property mapper is the central logic that can help to map values between objects.
 * 
 * @author Peter Boros
 */
public final class ObjectMapping {

  private static final String NODE_POSTFIX = "Node";

  private static final String OBJ_NODE = "objNode";

  private static final String OBJ = "obj";

  private static final String RESULT_OBJECT = "resultObject";

  private static final Logger log = LoggerFactory.getLogger(ObjectMapping.class);

  private WeakReference<ObjectApi> objectApiRef;

  private ObjectMappingDefinition definition;

  private final Map<ObjectPropertyMapping, Expression> cachedExpressions = new HashMap<>();

  private static final SpelExpressionParser parser = new SpelExpressionParser();

  private static final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

  private final Map<String, ContextObjectItem> contextObjects = new HashMap<>();

  private ContextObjectItem singleContextItem;

  /**
   * The result object that is an empty map by definition. It can be set to an existing object if it
   * is a merge rather then a create.
   */
  Map<String, Object> resultObject = new HashMap<>();

  private static final String SINGLE_CONTEXT_ITEM = "singleContextItem";

  ObjectMapping(ObjectApi objectApi) {
    super();
    this.objectApiRef = new WeakReference<>(objectApi);
  }

  public ObjectMapping mapping(ObjectMappingDefinition definition) {
    this.definition = definition;
    return this;
  }

  private ObjectApi objectApi() {
    return objectApiRef.get();
  }

  /**
   * Set the context as an URI to load when the mapping tries to access the values of this object.
   * 
   * @param name The name of the context object.
   * @param uri The uri of the object.
   * @return
   */
  public ObjectMapping set(String name, URI uri) {
    contextObjects.put(name, new ContextObjectItem(objectApi(), name, uri));
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
  public ObjectMapping set(String name, Object object) {
    contextObjects.put(name, new ContextObjectItem(objectApi(), name, object));
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
  public ObjectMapping set(String name, ObjectNode node) {
    contextObjects.put(name, new ContextObjectItem(objectApi(), name, node));
    return this;
  }

  /**
   * Set the context as an URI to load when the mapping tries to access the values of this object.
   * 
   * @param uri The uri of the object.
   * @return
   */
  public ObjectMapping set(URI uri) {
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
  public ObjectMapping set(Object object) {
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
  public ObjectMapping set(ObjectNode node) {
    singleContextItem = new ContextObjectItem(objectApi(), SINGLE_CONTEXT_ITEM, node);
    return this;
  }

  public ObjectMapping setResultObject(Map<String, Object> resultObject) {
    this.resultObject = resultObject;
    return this;
  }

  public ObjectMapping initFrom(ObjectMapping mapping) {
    singleContextItem = mapping.singleContextItem;
    contextObjects.putAll(mapping.contextObjects);
    return this;
  }

  /**
   * Returned the cached expression to avoid parsing again.
   * 
   * @return
   */
  private final Expression getExpression(ObjectPropertyMapping propertyMapping) {
    return cachedExpressions.computeIfAbsent(propertyMapping,
        om -> parser.parseExpression(om.getExpression()));
  }

  /**
   * Iterates the parameter list and constructs a result list based on the source list element. The
   * actual list item is always available as listItem in the context.
   * 
   * @param list The list to iterate on.
   * @return The result list with the mapped values from the parameter list.
   */
  public List<Object> iterate(List<Object> list) {
    if (list == null || list.isEmpty()) {
      return new ArrayList<>();
    }
    return list.stream().map(o -> {
      set("listItem", o);
      return execute();
    }).toList();
  }

  /**
   * Executes the mappings and constructs the result object. It can be a single value but it can be
   * a map also.
   * 
   * @return
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public Object execute() {
    ObjectApi objectApi = objectApi();
    for (ObjectPropertyMapping propertyMapping : definition.getMappings()) {
      List<String> toPath = propertyMapping.getToPath();
      // The highest precedence is the list iteration, the script, then the expresion and the simple
      // mapping at last.
      Object value;
      if (propertyMapping.getIterationDefinition() != null) {
        value = objectApi.mapper().mapping(propertyMapping.getIterationDefinition()).initFrom(this)
            .iterate((List) getValueFromContext(propertyMapping.getFromPath()));
      } else if (!StringConstant.isNullOrBlank(propertyMapping.getScriptBody())) {
        value = evaluateScript(propertyMapping);
      } else if (!StringConstant.isNullOrBlank(propertyMapping.getExpression())) {
        Expression expression = getExpression(propertyMapping);
        value = expression.getValue(getEvaluationContext());
      } else if (propertyMapping.getFromPath() != null
          && !propertyMapping.getFromPath().isEmpty()) {
        value = getValueFromContext(propertyMapping.getFromPath());
      } else {
        // Error the given mapping is skipped.
        throw new IllegalStateException("The " + propertyMapping + " mapping is not correct.");
      }

      // Force the required type conversion if any.
      if (propertyMapping.getTypeClass() != null) {
        Class<?> clazz = null;
        try {
          clazz = Class.forName(propertyMapping.getTypeClass());
          value = objectApi().asType(clazz, value);
        } catch (ClassNotFoundException e) {
          log.error("Unable to cast to required type", e);
        }
      }

      if (toPath == null || toPath.isEmpty()) {
        // This property mapper is set the result value as is. So there is no need to do any further
        // mapping.
        return value;
      }
      objectApi.setValueIntoObjectMap(resultObject, value,
          StringConstant.toArray(propertyMapping.getToPath()));
    }
    return resultObject;
  }

  private final Object evaluateScript(ObjectPropertyMapping mapping) {
    String scriptKind = mapping.getScriptKind() == null ? "Groovy" : mapping.getScriptKind();
    ScriptEngine engine = scriptEngineManager.getEngineByName(scriptKind);
    if (engine == null) {
      throw new IllegalArgumentException("Unable to load the " + scriptKind + " script engine.");
    }
    try {
      return engine.eval(mapping.getScriptBody(), getScriptBindings(engine));
    } catch (ScriptException e) {
      log.error("Failed to execute the {} script with the {} engine.", mapping.getScriptBody(),
          mapping.getScriptKind(), e);
      throw new UnsupportedOperationException(
          "Failed to execute the script with the " + mapping.getScriptKind() + " engine.", e);
    }

  }

  /**
   * Constructs an evaluation context for the {@link Expression} of the SpEL.
   * 
   * @return
   */
  private final EvaluationContext getEvaluationContext() {
    StandardEvaluationContext result = new StandardEvaluationContext();
    if (singleContextItem != null) {
      // We should set the root object and also the variable.
      result.setRootObject(singleContextItem.getValue());
    }
    for (ContextObjectItem contextObject : contextObjects.values()) {
      result.setVariable(contextObject.getName(), contextObject.getValue());
    }
    result.setVariable(RESULT_OBJECT, resultObject);
    return result;
  }

  /**
   * Constructs an evaluation context for the {@link Expression} of the SpEL.
   * 
   * @return
   */
  private final Bindings getScriptBindings(ScriptEngine engine) {
    Bindings result = engine.createBindings();
    if (singleContextItem != null) {
      // We should set the root object and also the variable.
      result.put(OBJ, singleContextItem.getValue());
      result.put(OBJ_NODE, singleContextItem.objectNode());
    }
    for (ContextObjectItem contextObject : contextObjects.values()) {
      result.put(contextObject.getName(), contextObject.getValue());
      result.put(contextObject.getName() + NODE_POSTFIX, contextObject.objectNode());
    }
    result.put(RESULT_OBJECT, resultObject);
    return result;
  }

  /**
   * Get the value identified by the path from the context set to the mapping.
   * 
   * @param path The path of the property to get.
   * @return The value denoted by the path.
   */
  private final Object getValueFromContext(List<String> path) {
    Objects.requireNonNull(path);
    ContextObjectItem contextObject;
    List<String> finalPath;
    if (singleContextItem != null) {
      // The whole path is evaluated inside the single context object.
      contextObject = singleContextItem;
      finalPath = path;
    } else {
      // The first segment of the path identifies the context object and the rest is the path
      // inside.
      if (path.size() < 1) {
        throw new IllegalArgumentException(
            "Unable to get value from context, at least the context object must be denoted.");
      }
      String ctxName = path.get(0);
      contextObject = contextObjects.get(ctxName);
      if (contextObject == null) {
        throw new IllegalArgumentException(
            ctxName + " context object is not found.");
      }
      finalPath = path.subList(1, path.size());
    }
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

  public Map<String, Object> copyAllValues(Map<String, Object> from, Map<String, Object> to) {
    return copyAllValues(definition, from, to);
    // Objects.requireNonNull(from);
    // Objects.requireNonNull(to);
    // setResultObject(to);
    // Object result = execute();
    // if (result instanceof Map) {
    // to.putAll((Map<String, Object>) result);
    // }
    // return to;
  }

  private final Map<String, Object> copyAllValues(ObjectMappingDefinition mappingDef,
      Map<String, Object> from, Map<String, Object> to) {
    ObjectApi objectApi = objectApi();
    for (ObjectPropertyMapping propertyMapping : mappingDef.getMappings()) {
      Object value = objectApi.getValueFromObjectMap(from,
          StringConstant.toArray(propertyMapping.getFromPath()));
      if (value != null) {
        objectApi.setValueIntoObjectMap(to, value,
            StringConstant.toArray(propertyMapping.getToPath()));
      }
    }
    for (ObjectListMapping listMapping : mappingDef.getListMappings()) {
      String[] toListPath = StringConstant.toArray(listMapping.getToListPath());
      Object toValueList = objectApi.getValueFromObjectMap(to,
          toListPath);
      List<Object> toList;
      if (!(toValueList instanceof List)) {
        toList = new ArrayList<>();
      } else {
        toList = (List<Object>) toValueList;
      }
      if (listMapping.getFromPrimitivePath() != null
          && !listMapping.getFromPrimitivePath().isEmpty()) {
        Object value = objectApi.getValueFromObjectMap(from,
            StringConstant.toArray(listMapping.getFromPrimitivePath()));
        if (value != null) {
          toList.add(value);
        }
      } else if (listMapping.getObjectMapping() != null) {
        Map<String, Object> toListItemMap = new HashMap<>();
        toListItemMap = copyAllValues(listMapping.getObjectMapping(), from, toListItemMap);
        if (!toListItemMap.isEmpty()) {
          toList.add(toListItemMap);
        }
      }
      objectApi.setValueIntoObjectMap(to, toList,
          toListPath);
    }
    return to;
  }

}
