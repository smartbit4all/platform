package org.smartbit4all.api.invocation;

import java.util.List;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.invocation.bean.InvocationRunItem;
import org.smartbit4all.api.object.bean.ContextMappingDefinition;
import org.smartbit4all.api.object.bean.ContextMappingItem;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;

public class InvocationRunItemBuilder {
  private final InvocationRunItem item = new InvocationRunItem();
  private final InvocationRequestDefinition requestDefinition = new InvocationRequestDefinition();

  public static InvocationRunItemBuilder withRequest(InvocationRequest request) {
    InvocationRunItemBuilder result = new InvocationRunItemBuilder();
    result.requestDefinition.setRequest(request);
    return result;
  }

  public InvocationRunItemBuilder addResolver(ObjectMappingDefinition mappingDef) {
    InvocationParameterResolver resolver = new InvocationParameterResolver().definition(mappingDef);
    this.requestDefinition.addResolversItem(resolver);
    return this;
  }

  public InvocationRunItemBuilder addResolverFromPath(String... fromPathItems) {
    ObjectMappingDefinition mapping = new ObjectMappingDefinition();
    ObjectPropertyMapping propertyMapping = new ObjectPropertyMapping();
    for (String path : fromPathItems) {
      propertyMapping.addFromPathItem(path);
    }
    mapping.addMappingsItem(propertyMapping);
    return addResolver(mapping);
  }

  public InvocationRunItemBuilder applyResult(List<String> outputPath, List<String> fromPath) {
    ObjectMappingDefinition mapping = new ObjectMappingDefinition();
    ObjectPropertyMapping propertyMapping = new ObjectPropertyMapping();
    for (String path : fromPath) {
      propertyMapping.addFromPathItem(path);
    }
    mapping.addMappingsItem(propertyMapping);

    ContextMappingDefinition ctxMap = new ContextMappingDefinition();
    ctxMap
        .addItemsItem(new ContextMappingItem().valueMapping(mapping).outputPath(outputPath));
    requestDefinition.setApplyResult(ctxMap);
    return this;
  }

  public InvocationRunItemBuilder applyResult(ContextMappingDefinition ctxMap) {
    requestDefinition.setApplyResult(ctxMap);
    return this;
  }

  public InvocationRunItemBuilder throwException(boolean value) {
    requestDefinition.setThrowException(value);
    return this;
  }

  public InvocationRunItem build() {
    item.setRequestDefinition(requestDefinition);
    return item;
  }
}
