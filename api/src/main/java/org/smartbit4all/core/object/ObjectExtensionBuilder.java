package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ObjectConstraintDescriptor;
import org.smartbit4all.api.object.bean.ObjectExtensionDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContextObject;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiActionConstraint;

public final class ObjectExtensionBuilder {

  private final ObjectApi objectApi;
  private final ObjectExtensionApi objectExtensionApi;
  private final ObjectExtensionDescriptor descriptor;

  private boolean newDefinition = false;

  ObjectExtensionBuilder(ObjectExtensionApi objectExtensionApi, ObjectApi objectApi) {
    this(objectExtensionApi, objectApi, new ObjectExtensionDescriptor());
  }

  ObjectExtensionBuilder(ObjectExtensionApi objectExtensionApi, ObjectApi objectApi,
      ObjectExtensionDescriptor objectExtensionDescriptor) {
    this.objectExtensionApi = objectExtensionApi;
    this.objectApi = objectApi;
    this.descriptor = objectExtensionDescriptor;
  }

  /**
   * Sets the name for the {@link ObjectExtensionDescriptor} under construction.
   * 
   * <p>
   * If an existing descriptor is being updated and a new name is supplied, the constructed
   * descriptor will be created based on the original supplied descriptor. When constructing a new
   * descriptor, providing a non-empty name is mandatory.
   * 
   * @param name the unique {@code String} name for this extension descriptor
   * @return this instance
   */
  public ObjectExtensionBuilder name(String name) {
    if (Objects.equals(name, descriptor.getName())) {
      return this;
    }

    newDefinition = true;
    descriptor.name(name);
    return this;
  }

  /**
   * Commences the construction of a new property definition in this extension descriptor.
   * 
   * @param propertyName the desired {@code String} name of the property
   * @param ownerClass the {@code Class} to extend with the property
   * @return a new {@code ObjectPropertyBuilder} instance
   */
  public ObjectPropertyBuilder property(String propertyName, Class<?> ownerClass) {
    return new ObjectPropertyBuilder(propertyName, ownerClass);
  }

  /**
   * Registers the specified layout with the given name in the extension descriptor
   * 
   * @param name the desired {@code String} name of the layout definition, not null
   * @param layoutDefinition the {@link SmartLayoutDefinition} describing the layout
   * @return this instance
   */
  public ObjectExtensionBuilder layout(String name, SmartLayoutDefinition layoutDefinition) {
    descriptor.putLayoutsItem(name, layoutDefinition);
    return this;
  }

  /**
   * Commences the construction of a new constraint descriptor.
   * 
   * <p>
   * The constraint descriptor may contain {@link ComponentConstraint}s and
   * {@link UiActionConstraint}s. They will be enforced if the object in question and its context
   * objects meet a certain criteria.
   * 
   * @return a new {@code ObjectConstraintBuilder} instance
   */
  public ObjectConstraintBuilder constraint() {
    return new ObjectConstraintBuilder();
  }

  /**
   * Assembles the defined extension descriptor.
   * 
   * @return the {@code URI} of the saved {@link ObjectExtensionDescriptor}
   */
  public URI build() {
    if (newDefinition) {
      return objectExtensionApi.saveNewExtensionDescriptor(descriptor);
    } else {
      ObjectNode descriptorNode = objectApi.loadLatest(descriptor.getUri());
      descriptorNode.setObject(descriptor);
      return objectApi.save(descriptorNode);
    }
  }

  public final class ObjectPropertyBuilder {

    private final ObjectPropertyDescriptor propertyDescriptor;
    private final String name;

    private ObjectPropertyBuilder(String name, Class<?> ownerClass) {
      this.name = name;
      propertyDescriptor = new ObjectPropertyDescriptor();
      propertyDescriptor.objectQualifiedName(ownerClass.getCanonicalName());
    }

    /**
     * Marks the property as <i>"built-in"</i>.
     * 
     * <p>
     * Built-in properties are non-removable, and clients should honour this obligation.
     * 
     * @return this instance
     */
    public ObjectPropertyBuilder builtIn() {
      propertyDescriptor.builtIn(true);
      return this;
    }

    /**
     * Sets the type for the property.
     * 
     * @param clazz the {@link Class} of the property
     * @return this instance
     */
    public ObjectPropertyBuilder typeClass(Class<?> clazz) {
      return typeClass(clazz.getName());
    }

    /**
     * Sets the type of the property, using its class name.
     * 
     * @param className the {@code String} name of the property's class
     * @return this instance
     */
    public ObjectPropertyBuilder typeClass(String className) {
      propertyDescriptor.propertyDefinition(new PropertyDefinitionData()
          .name(name)
          .typeClass(className));
      return this;
    }

    /**
     * Concludes the assembly of the property descriptor and allows further customising the
     * underlying {@link ObjectExtensionDescriptor}.
     * 
     * @return the original {@code ObjectExtensionBuilder} with the property change commited to it
     */
    public ObjectExtensionBuilder and() {
      URI propertyDescriptorUri = ObjectExtensionBuilder.this.objectApi
          .saveAsNew(ObjectExtensionApi.SCHEMA, propertyDescriptor);
      ObjectExtensionBuilder.this.descriptor.putPropertiesItem(name, propertyDescriptorUri);
      return ObjectExtensionBuilder.this;
    }
  }

  public final class ObjectConstraintBuilder {
    private final ObjectConstraintDescriptor constraintDescriptor = new ObjectConstraintDescriptor()
        .contexts(new ObjectPropertyResolverContext()
            .objects(new ArrayList<>()));

    private ObjectConstraintBuilder() {}

    /**
     * Sets the context objects for constraint evaluation.
     * 
     * @param context the {@link ObjectPropertyResolverContext} containing all context objects by
     *        alias
     * @return this instance
     */
    public ObjectConstraintBuilder context(ObjectPropertyResolverContext context) {
      constraintDescriptor.contexts(context);
      return this;
    }

    /**
     * Registers a given object as context object under the specified alias.
     * 
     * @param name the {@code String} alias one may refer to the context object in evaluation
     *        scripts and {@code InvocationRequest}s
     * @param contextObjectUri the {@code URI} of the context object identifying it in persistent
     *        storage
     * @return this instance
     */
    public ObjectConstraintBuilder contextObject(String name, URI contextObjectUri) {
      constraintDescriptor.getContexts().addObjectsItem(new ObjectPropertyResolverContextObject()
          .name(name)
          .uri(contextObjectUri));
      return this;
    }

    /**
     * Registers the component constraints to retrieve if this constraint descriptor applies to a
     * given object.
     * 
     * @param constraints a {@code List} of {@link ComponentConstraint}s which are applicable when
     *        an object meets a related predicate
     * @return this instance
     */
    public ObjectConstraintBuilder componentConstraints(List<ComponentConstraint> constraints) {
      constraintDescriptor.componentConstraints(constraints);
      return this;
    }

    /**
     * Registers the action constraints to retrieve if this constraint descriptor applies to a given
     * object.
     * 
     * @param actionConstraints a {@code List} of {@link UiActionConstraint}s which are applicable
     *        when an object meets a related predicate
     * @return this instance
     */
    public ObjectConstraintBuilder actionConstraints(List<UiActionConstraint> actionConstraints) {
      constraintDescriptor.actionConstraints(actionConstraints);
      return this;
    }

    /**
     * Registers the predicates to use to determine if this constraint descriptor currently applies
     * to an object or not.
     * 
     * <p>
     * The signature of the {@link InvocationRequest}s should return {@link Boolean} (the boxed
     * version). For every parameter the {@code InvocationRequest}s use an
     * {@link InvocationParameterResolver} must be provided. The resolvers'
     * {@link InvocationParameterResolver#PROPERTY_URI} can be constructed using one of the aliases
     * provided in {@link ObjectConstraintBuilder#contextObject(String, URI)} as schema, followed by
     * an path pointing to the required property.
     * 
     * <p>
     * Three context aliases are built in, and need not be provided:
     * 
     * <ol>
     * <li>The current session under {@link ObjectExtensionApi#SESSION_CONTEXT}
     * <li>The current user under {@link ObjectExtensionApi#USER_CONTEXT}
     * <li>The investigated object under {@link ObjectExtensionApi#THIS_CONTEXT}
     * </ol>
     * 
     * <p>
     * The currently constructed constraint descriptor's constraints shall be enforced if at least
     * one of the supplied predicates herein return with {@link Boolean#TRUE}.
     * 
     * @param predicates a {@link List} of {@link InvocationRequestDefinition}s to be used as
     *        predicates to decide whether apply the descriptor's constraints or not
     * @return this instance
     */
    public ObjectConstraintBuilder predicates(List<InvocationRequestDefinition> predicates) {
      constraintDescriptor.predicates(predicates);
      return this;
    }

    /**
     * Concludes the assembly of the constraint descriptor and allows further customising the
     * underlying {@link ObjectExtensionDescriptor}.
     * 
     * @return the original {@code ObjectExtensionBuilder} with the constraint changes committed
     */
    public ObjectExtensionBuilder and() {
      ObjectExtensionBuilder.this.descriptor.addConstraintsItem(constraintDescriptor);
      return ObjectExtensionBuilder.this;
    }
  }
}
