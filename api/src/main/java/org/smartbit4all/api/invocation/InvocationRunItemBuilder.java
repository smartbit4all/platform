package org.smartbit4all.api.invocation;

import java.util.List;
import java.util.function.Consumer;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationPredicate;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.invocation.bean.InvocationRun;
import org.smartbit4all.api.invocation.bean.InvocationRunConditional;
import org.smartbit4all.api.invocation.bean.InvocationRunItem;
import org.smartbit4all.api.object.bean.ContextMappingDefinition;
import org.smartbit4all.api.object.bean.ContextMappingItem;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;
import org.smartbit4all.core.object.ObjectMappingDefinitionBuilder;
import org.smartbit4all.core.object.ObjectPropertyMappingBuilder;

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

  private final InvocationRequestDefinition getDefinition() {
    if (item.getRequestDefinition() == null) {
      item.setRequestDefinition(new InvocationRequestDefinition());
    }
    return item.getRequestDefinition();
  }

  /**
   * Private constructor to enforce static factory usage.
   */
  private InvocationRunItemBuilder() {
    item = new InvocationRunItem();
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
    result.getDefinition().setRequest(request);
    return result;
  }

  public InvocationRunItemBuilder request(InvocationRequest request) {
    getDefinition().setRequest(request);
    return this;
  }

  public InvocationRunItemBuilder setElse(InvocationRun elseRun) {
    item._else(elseRun);
    return this;
  }

  public InvocationRunItemBuilder setElse(Consumer<InvocationRunBuilder> rb) {
    InvocationRunBuilder runBuilder = new InvocationRunBuilder();
    rb.accept(runBuilder);
    item._else(runBuilder.build());
    return this;
  }

  public InvocationRunItemBuilder addParallel(InvocationRun run) {
    item.addParallelsItem(run);
    return this;
  }

  public InvocationRunItemBuilder addParallel(Consumer<InvocationRunBuilder> rb) {
    InvocationRunBuilder runBuilder = new InvocationRunBuilder();
    rb.accept(runBuilder);
    item.addParallelsItem(runBuilder.build());
    return this;
  }

  public InvocationRunItemBuilder conditional(Consumer<ObjectPropertyMappingBuilder> pb,
      Consumer<InvocationRunBuilder> rb) {
    ObjectMappingDefinitionBuilder mappingBuilder = ObjectMappingDefinitionBuilder.create();
    InvocationRunBuilder runBuilder = new InvocationRunBuilder();
    mappingBuilder.addMapping(pb);
    rb.accept(runBuilder);
    InvocationRunConditional conditional = new InvocationRunConditional()
        .predicate(new InvocationPredicate().definition(mappingBuilder.build()))
        .run(runBuilder.build());
    item.addConditionalsItem(conditional);
    return this;
  }

  public InvocationRunItemBuilder setWhile(Consumer<ObjectPropertyMappingBuilder> pb,
      Consumer<InvocationRunBuilder> rb) {
    ObjectMappingDefinitionBuilder mappingBuilder = ObjectMappingDefinitionBuilder.create();
    InvocationRunBuilder runBuilder = new InvocationRunBuilder();
    mappingBuilder.addMapping(pb);
    rb.accept(runBuilder);
    InvocationRunConditional conditional = new InvocationRunConditional()
        .predicate(new InvocationPredicate().definition(mappingBuilder.build()))
        .run(runBuilder.build());
    item.setWhileLoop(conditional);
    return this;
  }

  public InvocationRunItemBuilder setDoWhile(Consumer<ObjectPropertyMappingBuilder> pb,
      Consumer<InvocationRunBuilder> rb) {
    ObjectMappingDefinitionBuilder mappingBuilder = ObjectMappingDefinitionBuilder.create();
    InvocationRunBuilder runBuilder = new InvocationRunBuilder();
    mappingBuilder.addMapping(pb);
    rb.accept(runBuilder);
    InvocationRunConditional conditional = new InvocationRunConditional()
        .predicate(new InvocationPredicate().definition(mappingBuilder.build()))
        .run(runBuilder.build());
    item.setDoWhileLoop(conditional);
    return this;
  }

  /**
   * Adds a resolver to the run item using a predefined {@link ObjectMappingDefinition}.
   * <p>
   * This resolver maps context input data to the request parameter structure.
   *
   * @param mappingDef the object mapping definition to use as a resolver
   * @return the builder instance
   */
  private final InvocationRunItemBuilder addResolver(ObjectMappingDefinition mappingDef) {
    InvocationParameterResolver resolver = new InvocationParameterResolver()
        .definition(mappingDef);
    getDefinition().addResolversItem(resolver);
    return this;
  }


  private final InvocationRunItemBuilder addResolver(Integer position,
      ObjectMappingDefinition mappingDef) {
    InvocationParameterResolver resolver = new InvocationParameterResolver()
        .definition(mappingDef)
        .position(position);
    getDefinition().addResolversItem(resolver);
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
    getDefinition().addResolversItem(resolver);
    return this;
  }

  public InvocationRunItemBuilder addResolver(Integer position,
      Consumer<ObjectMappingDefinitionBuilder> build) {
    ObjectMappingDefinitionBuilder builder = ObjectMappingDefinitionBuilder.create();
    build.accept(builder);
    InvocationParameterResolver resolver =
        new InvocationParameterResolver().definition(builder.build())
            .position(position);
    getDefinition().addResolversItem(resolver);
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

  public InvocationRunItemBuilder addResolverFromPath(Integer position, String... fromPathItems) {
    ObjectMappingDefinition mapping = new ObjectMappingDefinition();
    ObjectPropertyMapping propertyMapping = new ObjectPropertyMapping();
    for (String path : fromPathItems) {
      propertyMapping.addFromPathItem(path);
    }
    mapping.addMappingsItem(propertyMapping);
    return addResolver(position, mapping);
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
    getDefinition().setApplyResult(ctxMap);
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
    getDefinition().setApplyResult(ctxMap);
    return this;
  }

  /**
   * Configures the run item to throw an exception if the invocation fails.
   *
   * @param value {@code true} to throw an exception, {@code false} to suppress it
   * @return the builder instance
   */
  public InvocationRunItemBuilder throwException(boolean value) {
    getDefinition().setThrowException(value);
    return this;
  }

  /**
   * Finalizes and builds the {@link InvocationRunItem} instance.
   *
   * @return the constructed run item
   */
  public InvocationRunItem build() {
    return item;
  }
}
