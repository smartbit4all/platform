package org.smartbit4all.api.value;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.value.bean.ValueSetDefinition;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.api.value.bean.ValueSetOperand;
import org.smartbit4all.api.value.bean.ValueSetOperation;
import org.smartbit4all.api.view.bean.ValueSet;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;

public class ValueSetApiImpl implements ValueSetApi {

  private static final Logger log = LoggerFactory.getLogger(ValueSetApiImpl.class);

  public static final String SCHEMA = "valueSet";

  public static final String GLOBAL_VALUESETS = "globalValueSets";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public ValueSetDefinitionData getDefinitionData(String qualifiedName) {
    return getDefinitionData(GLOBAL_VALUESETS, qualifiedName);
  }

  @Override
  public ValueSetDefinitionData getDefinitionData(String namespace, String qualifiedName) {
    URI uri = collectionApi.map(SCHEMA, namespace).uris().get(qualifiedName);
    if (uri != null) {
      ValueSetDefinition definition = objectApi.read(uri, ValueSetDefinition.class);
      return definition.getData();
    }
    return null;
  }

  @Override
  public <T extends Enum> ValueSetData valuesOf(Class<T> clazz) {
    ValueSetDefinitionData definitionData = getDefinitionData(clazz.getName());
    if (definitionData == null) {
      // Construct the definition data for the enum.
      definitionData = constructDefinitionData(clazz);
    }
    return valuesOf(definitionData);
  }

  private <T> ValueSetDefinitionData constructDefinitionData(Class<T> clazz) {
    return new ValueSetDefinitionData().kind(ValueSetDefinitionKind.ENUM)
        .typeClass(clazz.getName()).qualifiedName(clazz.getName());
  }

  @Override
  public ValueSetData valuesOf(String qualifiedName) {
    return valuesOf(GLOBAL_VALUESETS, qualifiedName);
  }

  @Override
  public ValueSetData valuesOf(String logicalSchema, String qualifiedName, URI branchUri,
      boolean useObjectNode) {
    // The version URI of the given ValueSetDefinition. The new version of the values set must be
    // promoted before the first usage.
    ValueSetDefinitionData definitionData = getDefinitionData(logicalSchema, qualifiedName);
    if (definitionData != null) {
      return valuesOf(definitionData, branchUri, useObjectNode);
    }
    return new ValueSetData().qualifiedName(qualifiedName);
  }

  @Override
  public ValueSetData valuesOf(ValueSetDefinitionData definitionData, URI branchUri,
      boolean useObjectNode) {
    if (definitionData != null) {
      ObjectDefinition<?> objectDefinition = getObjectDefinition(definitionData);
      if (objectDefinition != null) {
        ValueSetData result = new ValueSetData().qualifiedName(definitionData.getQualifiedName())
            .iconCode(definitionData.getIconCode());
        result.lazy(Boolean.FALSE).undefined(Boolean.FALSE);
        result.setProperties(objectDefinition.getProperties());
        // We can continue with the final value set definition.
        result.setValues(
            readValues(definitionData, objectDefinition, branchUri, useObjectNode).values().stream()
                .collect(toList()));
        return result;
      }
      return new ValueSetData().qualifiedName(definitionData.getQualifiedName());
    }
    return null;
  }

  private Map<Object, Object> readValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition, URI branchUri, boolean useObjectNode) {
    Map<Object, Object> result = Collections.emptyMap();
    if (definitionData == null) {
      return result;
    }
    if (definitionData.getKind() == ValueSetDefinitionKind.ENUM) {
      if (objectDefinition.getClazz() != null
          && Enum.class.isAssignableFrom(objectDefinition.getClazz())) {
        result = readEnumClass(objectDefinition.getClazz(), definitionData.getQualifiedName());
      }
    } else if (definitionData.getKind() == ValueSetDefinitionKind.INLINE) {
      if (useObjectNode) {
        throw new IllegalArgumentException("useObjectNode not supported for inline valueSet");
      }
      // TODO use branchUri ??
      result = readInlineValues(definitionData, objectDefinition);
    } else if (definitionData.getKind() == ValueSetDefinitionKind.ALLOF) {
      // TODO use branchUri ??
      result = readAllObjectValues(definitionData, objectDefinition, useObjectNode);
    } else if (definitionData.getKind() == ValueSetDefinitionKind.LIST) {
      result = readListValues(definitionData, objectDefinition, branchUri, useObjectNode);
    } else if (definitionData.getKind() == ValueSetDefinitionKind.COMPOSITE) {
      // It's a value set composed from other sets. There must be an expression that defines the
      // way. We have to evaluate the expression to have the final definition.
      result = readCompositeValues(definitionData, objectDefinition, branchUri, useObjectNode);
    }
    return result;
  }

  private Map<Object, Object> readCompositeValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition, URI branchUri, boolean useObjectNode) {
    if (definitionData.getExpression() == null) {
      log.warn("The {} composite value set doen't have any expression.",
          definitionData.getQualifiedName());
      return Collections.emptyMap();
    }
    // TODO Evaluate the expression. All the set must have the same object type or else we can not
    // merge the values.
    List<Map<Object, Object>> valueLists = definitionData.getExpression().getOperands().stream()
        .map(vso -> readValues(getDefinitionDataFromOperand(vso), objectDefinition, branchUri,
            useObjectNode))
        .collect(toList());
    ValueSetOperation operation = definitionData.getExpression().getOperation();
    Map<Object, Object> result = null;
    if (operation == ValueSetOperation.UNION) {
      for (Map<Object, Object> map : valueLists) {
        if (result == null) {
          result = map;
        } else {
          result.putAll(map);
        }
      }
      return result;
    } else if (operation == ValueSetOperation.INTERSECT) {
      for (Map<Object, Object> map : valueLists) {
        if (result == null) {
          result = map;
        } else {
          result.entrySet().removeIf(o -> !map.containsKey(o));
        }
      }
      return result;
    } else if (operation == ValueSetOperation.DIF || operation == ValueSetOperation.SYMMETRICDIF) {
      for (Map<Object, Object> map : valueLists) {
        if (result == null) {
          result = map;
        } else {
          result.entrySet().removeIf(e -> map.containsKey(e.getKey()));
          if (operation == ValueSetOperation.SYMMETRICDIF) {
            Map<Object, Object> currentResult = result;
            map.entrySet().removeIf(e -> currentResult.containsKey(e.getKey()));
            result.putAll(map);
          }
        }
      }
      return result;
    }
    return Collections.emptyMap();
  }

  private final ValueSetDefinitionData getDefinitionDataFromOperand(ValueSetOperand operand) {
    if (operand.getData() != null) {
      return operand.getData();
    }
    return getDefinitionData(
        operand.getNamespace() == null ? GLOBAL_VALUESETS : operand.getNamespace(),
        operand.getName());
  }

  private Map<Object, Object> readListValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition, URI branchUri, boolean useObjectNode) {
    StoredList storedList =
        collectionApi.list(definitionData.getStorageSchema(), definitionData.getContainerName());
    if (storedList != null) {
      storedList.branch(branchUri);
      return storedList.uris().stream()
          .collect(toMap(u -> objectApi.getLatestUri(u),
              u -> useObjectNode ? objectApi.load(u)
                  : objectApi.read(u, objectDefinition.getClazz()),
              (v1, v2) -> v1));
    }
    return Collections.emptyMap();
  }

  private Map<Object, Object> readAllObjectValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition, boolean useObjectNode) {
    Storage storage = storageApi.get(definitionData.getStorageSchema());
    if (storage != null) {
      List<URI> allObjectUris = storage.readAllUris(objectDefinition.getClazz());
      // Quick win that we read all the properties.
      return allObjectUris.stream()
          .collect(toMap(u -> objectApi.getLatestUri(u),
              u -> useObjectNode ? objectApi.load(u)
                  : objectApi.read(u, objectDefinition.getClazz())));
    }
    return Collections.emptyMap();
  }

  private Map<Object, Object> readInlineValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition) {
    // Try to load the definition by the URI or initiate it based on the qualified name.
    if (objectDefinition != null) {
      // Copy by reference because the inline values won't be edited in memory!
      return definitionData.getInlineValues().stream()
          .map(o -> (o instanceof Map)
              ? objectApi.create(SCHEMA, o).getObject(objectDefinition.getClazz())
              : o)
          .collect(toMap(o -> {
            ObjectNode objectNode = objectApi.create(null, o);
            URI uri = objectNode.getObjectUri();
            return uri == null ? (definitionData.getKeyProperty() != null
                ? objectNode.getValueAsString(definitionData.getKeyProperty())
                : objectNode.getValueAsString(Value.CODE)) : uri;
          }, o -> o));
    }
    return Collections.emptyMap();
  }

  private final ObjectDefinition<?> getObjectDefinition(ValueSetDefinitionData definitionData) {
    if (definitionData == null) {
      return null;
    }
    ObjectDefinition<?> result = null;
    // Check if we have saved ObjectDefinition or not.
    ObjectDefinitionData objectDefinitionData = null;
    if (definitionData.getObjectDefinition() != null) {
      // TODO Make it more simple!!! Get by the URI itself from the ObjectManagementApi
      objectDefinitionData =
          objectApi.read(definitionData.getObjectDefinition(), ObjectDefinitionData.class);
      result = objectApi.definition(objectDefinitionData.getQualifiedName());
    }
    if (result == null && definitionData.getTypeClass() != null) {
      // Try to construct by the name of the typeClass
      result = objectApi.definition(definitionData.getTypeClass());
    }
    return result;
  }

  private Map<Object, Object> readEnumClass(Class<?> enumClass, String qualifiedName) {
    Map<Object, Object> result = new HashMap<>();
    // Fill all the values.
    for (int i = 0; i < enumClass.getEnumConstants().length; i++) {
      Object enumValue = enumClass.getEnumConstants()[i];
      Map<String, Object> valueObject = new HashMap<>();
      valueObject.put(Value.CODE, enumValue);
      valueObject.put(Value.DISPLAY_VALUE,
          localeSettingApi.get(qualifiedName, enumValue.toString()));
      result.put(enumValue, valueObject);
    }
    return result;
  }

  @Override
  public void save(String namespace, ValueSetDefinitionData valueSetDef) {
    collectionApi.map(SCHEMA, namespace).update(m -> {
      URI uri = m.get(valueSetDef.getQualifiedName());
      if (uri == null) {
        uri = objectApi.saveAsNew(SCHEMA, new ValueSetDefinition().data(valueSetDef));
        m.put(valueSetDef.getQualifiedName(), objectApi.getLatestUri(uri));
      } else {
        ObjectNode objectNode = objectApi.loadLatest(uri);
        ValueSetDefinitionData value =
            objectNode.getValue(ValueSetDefinitionData.class, ValueSetDefinition.DATA);
        if (!value.equals(valueSetDef)) {
          objectNode.setValue(valueSetDef, ValueSetDefinition.DATA);
          objectApi.save(objectNode);
        }
      }
      return m;
    });
  }

  @Override
  public void save(ValueSetDefinitionData valueSetDef) {
    save(GLOBAL_VALUESETS, valueSetDef);
  }

  @Override
  public <T> List<T> getValues(Class<T> typeClass, ValueSetData valueSet, String... path) {
    // We won't save this so the schema is not necessary.
    return valueSet.getValues().stream().map(o -> objectApi.create(SCHEMA, o))
        .map(on -> on.getValue(typeClass, path)).collect(toList());
  }

  @Override
  public ValueSet getValueSetWithValues(String namespace, String name, URI branchUri,
      String... path) {

    ValueSetData valueSetData = valuesOf(namespace, name, branchUri, true);
    Stream<ObjectNode> values = valueSetData.getValues().stream()
        .map(o -> ((ObjectNode) o));
    valueSetData.values(Values.valuesStream(values, path)
        // .map(v -> v.objectUri(objectApi.getLatestUri(v.getObjectUri())))
        .sorted(Values.CASE_INSENSITIVE_ORDER)
        .map(o -> ((Object) o))
        .collect(toList()));
    return new ValueSet()
        .valueSetName(name)
        .valueSetData(valueSetData.keyProperty(Value.OBJECT_URI));
  }
}
