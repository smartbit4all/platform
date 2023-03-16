package org.smartbit4all.api.value;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSet;
import org.smartbit4all.api.value.bean.ValueSetDefinition;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.api.value.bean.ValueSetExpression;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.beans.factory.annotation.Autowired;

public class ValueSetApiImpl implements ValueSetApi {

  private static final Logger log = LoggerFactory.getLogger(ValueSetApiImpl.class);

  private static final String SCHEMA = "valueSet";

  private static final String GLOBAL_VALUESETS = "globalValueSets";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public ValueSetDefinitionData evaluate(ValueSetExpression expression) {
    // TODO Auto-generated method stub
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
      // It's a value set composed from other sets. There must be an expression that defines the
      // way. We have to evaluate the expression to have the final definition.
      if (definitionData.getKind() == ValueSetDefinitionKind.COMPOSITE) {
        if (definitionData.getExpression() == null) {
          log.warn("The {} composite value set doen't have any expression.", qualifiedName);
          return new ValueSet().qualifiedName(qualifiedName);
        }
        definitionData = evaluate(definitionData.getExpression());
      }

      // We can continue with the final value set definition.
      return readValues(definitionData);

    } else {
      return new ValueSet().qualifiedName(qualifiedName);
    }
  }

  private ValueSet readValues(ValueSetDefinitionData definitionData) {
    ValueSet result = new ValueSet().qualifiedName(definitionData.getQualifiedName())
        .iconCode(definitionData.getIconCode());
    if (definitionData.getKind() == ValueSetDefinitionKind.ENUM) {
      if (definitionData.getQualifiedName() != null) {
        Class<?> enumClass;
        try {
          enumClass = Class.forName(definitionData.getQualifiedName());
        } catch (ClassNotFoundException e) {
          log.warn("The {} enum class was not found in the current runtime.",
              result.getQualifiedName());
          enumClass = null;
        }
        if (enumClass != null && Enum.class.isAssignableFrom(enumClass)) {
          readEnumClass(result, enumClass);
        }
      }
    } else if (definitionData.getKind() == ValueSetDefinitionKind.INLINE) {
      readInlineValues(definitionData, result);
    } else if (definitionData.getKind() == ValueSetDefinitionKind.ALLOF) {
      // Try to load the definition by the URI or initiate it based on the qualified name.
      readInlineValues(definitionData, result);
    }
    return result;
  }

  private void readInlineValues(ValueSetDefinitionData definitionData, ValueSet result) {
    // Try to load the definition by the URI or initiate it based on the qualified name.
    ObjectDefinition<?> objectDefinition = getObjectDefinition(definitionData);
    if (objectDefinition != null) {
      result.lazy(Boolean.FALSE).undefined(Boolean.FALSE);
      result.setProperties(objectDefinition.getProperties());
      // Copy by reference because the inline values won't be edited in memory!
      result.setValues(definitionData.getInlineValues());
    }
  }

  private ObjectDefinition<?> getObjectDefinition(ValueSetDefinitionData definitionData) {
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

  private void readEnumClass(ValueSet result, Class<?> enumClass) {
    ObjectDefinition<Value> definition = objectApi.definition(Value.class);
    result.setProperties(definition.getProperties());
    result.lazy(Boolean.FALSE).undefined(Boolean.FALSE);
    // Fill all the values.
    for (int i = 0; i < enumClass.getEnumConstants().length; i++) {
      Object enumValue = enumClass.getEnumConstants()[i];
      Map<String, Object> valueObject = new HashMap<>();
      valueObject.put(Value.CODE, enumValue);
      valueObject.put(Value.DISPLAY_VALUE,
          localeSettingApi.get(result.getQualifiedName(), enumValue.toString()));
      result.addValuesItem(valueObject);
    }
  }

  @Override
  public <T extends Enum> ValueSet valuesOf(Class<T> clazz) {
    return valuesOf(clazz.getName());
  }
}
