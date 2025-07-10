package org.smartbit4all.api.invocation;

import java.util.List;
import java.util.function.Consumer;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.invocation.bean.InvocationRunItem;
import org.smartbit4all.api.object.bean.ContextMappingDefinition;
import org.smartbit4all.api.object.bean.ContextMappingItem;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;
import org.smartbit4all.core.object.ObjectMappingDefinitionBuilder;

/**
 * Builder class for constructing {@link InvocationRunItem} instances.
 * <p>
 * An {@code InvocationRunItem} encapsulates a single execution unit with its request, parameter
 * resolvers, and optional result mappings.
 * <p>
 * This builder allows flexible creation of run items by specifying the input request, defining
 * resolvers (parameter mappers), and result context transformations.
 */
public class InvocationRunItemBuilder {
  private final InvocationRunItem item;
  private final InvocationRequestDefinition requestDefinition;

  /**
   * Private constructor to enforce static factory usage.
   */
  private InvocationRunItemBuilder() {
    item = new InvocationRunItem();
    requestDefinition = new InvocationRequestDefinition();
  }

  /**
   * Creates a new builder instance for {@link InvocationRunItem}.
   *
   * @return a new {@code InvocationRunItemBuilder} instance
   */
  public static InvocationRunItemBuilder builder() {
    return new InvocationRunItemBuilder();
  }

  /**
   * Creates a new builder with a predefined {@link InvocationRequest}.
   *
   * @param request the request object to set
   * @return the builder instance
   */
  public static InvocationRunItemBuilder withRequest(InvocationRequest request) {
    InvocationRunItemBuilder result = new InvocationRunItemBuilder();
    result.requestDefinition.setRequest(request);
    return result;
  }

  /**
   * Adds a resolver to the run item using a predefined {@link ObjectMappingDefinition}.
   * <p>
   * This resolver maps context input data to the request parameter structure.
   *
   * @param mappingDef the object mapping definition to use as a resolver
   * @return the builder instance
   */
  public InvocationRunItemBuilder addResolver(ObjectMappingDefinition mappingDef) {
    InvocationParameterResolver resolver = new InvocationParameterResolver().definition(mappingDef);
    this.requestDefinition.addResolversItem(resolver);
    return this;
  }

  /**
   * Adds a resolver using a {@link ObjectMappingDefinitionBuilder} to define the mapping inline.
   *
   * @param build a consumer that configures the object mapping definition builder
   * @return the builder instance
   */
  public InvocationRunItemBuilder addResolver(Consumer<ObjectMappingDefinitionBuilder> build) {
    ObjectMappingDefinitionBuilder builder = ObjectMappingDefinitionBuilder.create();
    build.accept(builder);
    InvocationParameterResolver resolver =
        new InvocationParameterResolver().definition(builder.build());
    this.requestDefinition.addResolversItem(resolver);
    return this;
  }

  /**
   * Adds a simple resolver that maps values from a list of input path items.
   * <p>
   * This is a shortcut for mapping input context fields directly.
   *
   * @param fromPathItems the input path segments to map
   * @return the builder instance
   */
  public InvocationRunItemBuilder addResolverFromPath(String... fromPathItems) {
    ObjectMappingDefinition mapping = new ObjectMappingDefinition();
    ObjectPropertyMapping propertyMapping = new ObjectPropertyMapping();
    for (String path : fromPathItems) {
      propertyMapping.addFromPathItem(path);
    }
    mapping.addMappingsItem(propertyMapping);
    return addResolver(mapping);
  }

  /**
   * Applies the result of the invocation to a given output path in the context, using a mapping
   * from the specified input path.
   *
   * @param outputPath the output path to apply the result to
   * @param fromPath the input path from which to retrieve the value
   * @return the builder instance
   */
  public InvocationRunItemBuilder applyResult(List<String> outputPath, List<String> fromPath) {
    if (fromPath == null || outputPath == null) {
      return this;
    }
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

  /**
   * Applies a custom {@link ContextMappingDefinition} to transform and store the invocation result
   * in the context.
   *
   * @param ctxMap the context mapping definition for the result
   * @return the builder instance
   */
  public InvocationRunItemBuilder applyResult(ContextMappingDefinition ctxMap) {
    requestDefinition.setApplyResult(ctxMap);
    return this;
  }

  /**
   * Configures the run item to throw an exception if the invocation fails.
   *
   * @param value {@code true} to throw an exception, {@code false} to suppress it
   * @return the builder instance
   */
  public InvocationRunItemBuilder throwException(boolean value) {
    requestDefinition.setThrowException(value);
    return this;
  }

  /**
   * Finalizes and builds the {@link InvocationRunItem} instance.
   *
   * @return the constructed run item
   */
  public InvocationRunItem build() {
    item.setRequestDefinition(requestDefinition);
    return item;
  }
}
