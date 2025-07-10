package org.smartbit4all.core.object;

import java.util.Objects;
import java.util.function.Consumer;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;

/**
 * Builder class for constructing {@link ObjectMappingDefinition} instances.
 * <p>
 * An {@code ObjectMappingDefinition} describes the mapping between two domain objects. It can
 * include constant values, property-to-property mappings, and more complex logic like expressions
 * or script-based transformations.
 * <p>
 * This builder simplifies the fluent creation of mapping definitions used in transformation
 * processes within invocation or orchestration logic.
 */
public class ObjectMappingDefinitionBuilder {

  private final ObjectMappingDefinition definition;

  /**
   * Private constructor to enforce the use of {@link #create()}.
   */
  private ObjectMappingDefinitionBuilder() {
    this.definition = new ObjectMappingDefinition();
  }

  /**
   * Creates a new builder instance for {@link ObjectMappingDefinition}.
   *
   * @return a new instance of {@code ObjectMappingDefinitionBuilder}
   */
  public static ObjectMappingDefinitionBuilder create() {
    return new ObjectMappingDefinitionBuilder();
  }

  /**
   * Sets the fully qualified type name of the source (input) object.
   *
   * @param fromTypeQualifiedName the fully qualified name of the source type
   * @return the builder instance
   */
  public ObjectMappingDefinitionBuilder fromType(String fromTypeQualifiedName) {
    definition.setFromTypeQualifiedName(fromTypeQualifiedName);
    return this;
  }

  /**
   * Sets the fully qualified type name of the target (output) object.
   *
   * @param toTypeQualifiedName the fully qualified name of the target type
   * @return the builder instance
   */
  public ObjectMappingDefinitionBuilder toType(String toTypeQualifiedName) {
    definition.setToTypeQualifiedName(toTypeQualifiedName);
    return this;
  }

  /**
   * Sets a constant value as the result of this mapping.
   * <p>
   * This is useful when the mapping result is a fixed value and does not depend on input
   * properties.
   *
   * @param value the constant value to use in the mapping
   * @return the builder instance
   */
  public ObjectMappingDefinitionBuilder constant(Object value) {
    definition.setConstant(value);
    return this;
  }

  /**
   * Adds an {@link ObjectPropertyMapping} to this mapping definition.
   * <p>
   * Property mappings define how individual fields are transformed between objects.
   *
   * @param mapping the property mapping to add
   * @return the builder instance
   * @throws NullPointerException if the mapping is {@code null}
   */
  public ObjectMappingDefinitionBuilder addMapping(ObjectPropertyMapping mapping) {
    Objects.requireNonNull(mapping, "Mapping cannot be null");
    definition.getMappings().add(mapping);
    return this;
  }

  /**
   * Adds an {@link ObjectPropertyMapping} built via a consumer builder.
   * <p>
   * This allows in-line construction of complex property mappings using a lambda expression.
   *
   * @param build a consumer that builds a {@link ObjectPropertyMapping} via its builder
   * @return the builder instance
   */
  public ObjectMappingDefinitionBuilder addMapping(Consumer<ObjectPropertyMappingBuilder> build) {
    ObjectPropertyMappingBuilder builder = ObjectPropertyMappingBuilder.create();
    build.accept(builder);
    return addMapping(builder.build());
  }

  /**
   * Builds and returns the {@link ObjectMappingDefinition} instance.
   *
   * @return the constructed {@code ObjectMappingDefinition}
   */
  public ObjectMappingDefinition build() {
    return definition;
  }
}
