package org.smartbit4all.core.object;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.smartbit4all.api.object.bean.ObjectPropertyMapping;

/**
 * Builder for constructing {@link ObjectPropertyMapping} instances.
 * <p>
 * An {@code ObjectPropertyMapping} defines the mapping between two properties in a domain object
 * transformation. It supports simple path-based mappings, expression-based mappings, script
 * execution, and iteration over lists using nested mappings.
 */
public class ObjectPropertyMappingBuilder {

  private final ObjectPropertyMapping mapping = new ObjectPropertyMapping();

  /**
   * Creates a new {@code ObjectPropertyMappingBuilder} instance.
   *
   * @return the builder instance
   */
  public static ObjectPropertyMappingBuilder create() {
    return new ObjectPropertyMappingBuilder();
  }

  /**
   * Sets the source path of the property from which the value is mapped.
   * <p>
   * The path segments identify the source object and property in the context. This is ignored if an
   * expression is set.
   *
   * @param fromPath list of path segments to the source property
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder fromPath(List<String> fromPath) {
    mapping.setFromPath(fromPath);
    return this;
  }

  /**
   * Sets the source path of the property from which the value is mapped.
   * <p>
   * The path segments identify the source object and property in the context. This is ignored if an
   * expression is set.
   *
   * @param fromPath list of path segments to the source property
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder from(String... fromPath) {
    if (fromPath == null) {
      return this;
    }
    fromPath(Arrays.asList(fromPath));
    return this;
  }

  /**
   * Sets the target path of the property to which the value should be mapped.
   * <p>
   * If the path is empty, the mapping applies to the whole object.
   *
   * @param toPath list of path segments to the target property
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder toPath(List<String> toPath) {
    mapping.setToPath(toPath);
    return this;
  }

  /**
   * Sets the target path of the property to which the value should be mapped.
   * <p>
   * If the path is empty, the mapping applies to the whole object.
   *
   * @param toPath list of path segments to the target property
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder to(String... toPath) {
    if (toPath == null) {
      return this;
    }
    mapping.setToPath(Arrays.asList(toPath));
    return this;
  }

  /**
   * Sets a Spring Expression Language (SpEL) expression for the mapping.
   * <p>
   * When an expression is used, the {@code fromPath} is ignored.
   *
   * @param expression the SpEL expression to evaluate
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder expression(String expression) {
    mapping.setExpression(expression);
    return this;
  }

  /**
   * Sets the script body and kind for evaluating the mapped value.
   * <p>
   * Typically used for more complex transformations using Groovy or other scripting languages.
   *
   * @param body the script source code
   * @param kind the script language (e.g., "groovy")
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder script(String body, String kind) {
    mapping.setScriptBody(body);
    mapping.setScriptKind(kind);
    return this;
  }

  /**
   * Sets the forced type class for the mapping result.
   * <p>
   * Useful when the transformation result should be explicitly cast using ObjectApi.asType.
   *
   * @param typeClass fully qualified class name of the result type
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder typeClass(String typeClass) {
    mapping.setTypeClass(typeClass);
    return this;
  }

  public ObjectPropertyMappingBuilder constant() {
    return this;
  }

  /**
   * Defines an iteration-based mapping for handling list properties.
   * <p>
   * This mapping is evaluated for each element in the source list, and the result is a list of
   * transformed items.
   *
   * @param build a consumer that builds the nested
   *        {@link org.smartbit4all.api.object.bean.ObjectMappingDefinition}
   * @return the builder instance
   */
  public ObjectPropertyMappingBuilder iteration(Consumer<ObjectMappingDefinitionBuilder> build) {
    ObjectMappingDefinitionBuilder builder = ObjectMappingDefinitionBuilder.create();
    build.accept(builder);
    mapping.setIterationDefinition(builder.build());
    return this;
  }

  /**
   * Builds the configured {@link ObjectPropertyMapping} instance.
   *
   * @return the constructed mapping object
   */
  public ObjectPropertyMapping build() {
    return mapping;
  }
}
