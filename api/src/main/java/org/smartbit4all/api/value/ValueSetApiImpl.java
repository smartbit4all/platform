package org.smartbit4all.api.value;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSet;
import org.smartbit4all.api.value.bean.ValueSetDefinition;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.api.value.bean.ValueSetExpression;
import org.smartbit4all.api.value.bean.ValueSetOperation;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.beans.factory.annotation.Autowired;
import static java.util.stream.Collectors.toList;

public class ValueSetApiImpl implements ValueSetApi {

  private static final Logger log = LoggerFactory.getLogger(ValueSetApiImpl.class);

  private static final String SCHEMA = "valueSet";

  private static final String GLOBAL_VALUESETS = "globalValueSets";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public ValueSet evaluate(ValueSetExpression expression) {
    return null;
  }

  @Override
  public ValueSet valuesOf(String qualifiedName) {
    // The version URI of the given ValueSetDefinition. The new version of the values set must be
    // promoted before the first usage.
    URI uri = collectionApi.map(SCHEMA, GLOBAL_VALUESETS).uris().get(qualifiedName);
    if (uri != null) {
      ValueSetDefinition definition = objectApi.read(uri, ValueSetDefinition.class);
      ValueSetDefinitionData definitionData = definition.getData();
      if (definitionData == null) {
        return new ValueSet().qualifiedName(qualifiedName);
      }
      ObjectDefinition<?> objectDefinition = getObjectDefinition(definitionData);
      if (objectDefinition != null) {
        ValueSet result = new ValueSet().qualifiedName(definitionData.getQualifiedName())
            .iconCode(definitionData.getIconCode());
        result.lazy(Boolean.FALSE).undefined(Boolean.FALSE);
        result.setProperties(objectDefinition.getProperties());
        // We can continue with the final value set definition.
        result.setValues(readValues(definitionData, objectDefinition));
      }
      return new ValueSet().qualifiedName(qualifiedName);

    } else {
      return new ValueSet().qualifiedName(qualifiedName);
    }
  }

  private List<Object> readValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition) {
    List<Object> result = Collections.emptyList();
    if (definitionData.getKind() == ValueSetDefinitionKind.ENUM) {
      if (objectDefinition.getClazz() != null
          && Enum.class.isAssignableFrom(objectDefinition.getClazz())) {
        result = readEnumClass(objectDefinition.getClazz(), definitionData.getQualifiedName());
      }
    } else if (definitionData.getKind() == ValueSetDefinitionKind.INLINE) {
      result = readInlineValues(definitionData, objectDefinition);
    } else if (definitionData.getKind() == ValueSetDefinitionKind.ALLOF) {
      result = readAllObjectValues(definitionData, objectDefinition);
    } else if (definitionData.getKind() == ValueSetDefinitionKind.LIST) {
      result = readListValues(definitionData, objectDefinition);
    } else if (definitionData.getKind() == ValueSetDefinitionKind.COMPOSITE) {
      // It's a value set composed from other sets. There must be an expression that defines the
      // way. We have to evaluate the expression to have the final definition.
      result = readCompositeValues(definitionData, objectDefinition);
    }

    return result;
  }

  private List<Object> readCompositeValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition) {
    if (definitionData.getExpression() == null) {
      log.warn("The {} composite value set doen't have any expression.",
          definitionData.getQualifiedName());
      return Collections.emptyList();
    }
    // TODO Evaluate the expression. All the set must have the same object type or else we can not
    // merge the values.
    List<List<Object>> valueLists = definitionData.getExpression().getOperands().stream()
        .map(vso -> readValues(vso.getData(), objectDefinition)).collect(toList());
    ValueSetOperation operation = definitionData.getExpression().getOperation();
    if (operation == ValueSetOperation.UNION) {
      return valueLists.stream().flatMap(List::stream).distinct().collect(toList());
    } else if (operation == ValueSetOperation.INTERSECT) {

    } else if (operation == ValueSetOperation.DIF) {

    } else if (operation == ValueSetOperation.SYMMETRICDIF) {

    }
    return Collections.emptyList();
  }

  private List<Object> readListValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition) {
    StoredList storedList =
        collectionApi.list(definitionData.getStorageSchema(), definitionData.getContainerName());
    if (storedList != null) {
      return storedList.uris().stream().map(u -> objectApi.read(u, objectDefinition.getClazz()))
          .collect(toList());
    }
    return Collections.emptyList();
  }

  private List<Object> readAllObjectValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition) {
    Storage storage = storageApi.get(definitionData.getStorageSchema());
    if (storage != null) {
      List<URI> allObjectUris = storage.readAllUris(objectDefinition.getClazz());
      // Quick win that we read all the properties.
      return allObjectUris.stream().map(u -> objectApi.read(u, objectDefinition.getClazz()))
          .collect(toList());
    }
    return Collections.emptyList();
  }

  private List<Object> readInlineValues(ValueSetDefinitionData definitionData,
      ObjectDefinition<?> objectDefinition) {
    // Try to load the definition by the URI or initiate it based on the qualified name.
    if (objectDefinition != null) {
      // Copy by reference because the inline values won't be edited in memory!
      return definitionData.getInlineValues();
    }
    return Collections.emptyList();
  }

  @Override
  public <T extends Enum> ValueSet valuesOf(Class<T> clazz) {
    return valuesOf(clazz.getName());
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

  private List<Object> readEnumClass(Class<?> enumClass, String qualifiedName) {
    List<Object> result = new ArrayList<>();
    // Fill all the values.
    for (int i = 0; i < enumClass.getEnumConstants().length; i++) {
      Object enumValue = enumClass.getEnumConstants()[i];
      Map<String, Object> valueObject = new HashMap<>();
      valueObject.put(Value.CODE, enumValue);
      valueObject.put(Value.DISPLAY_VALUE,
          localeSettingApi.get(qualifiedName, enumValue.toString()));
      result.add(valueObject);
    }
    return result;
  }

}
