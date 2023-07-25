package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
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
   * 
   * @param name
   * @return
   */
  public ObjectExtensionBuilder name(String name) {
    if (Objects.equals(name, descriptor.getName())) {
      return this;
    }

    newDefinition = true;
    descriptor.name(name);
    return this;
  }

  public ObjectPropertyBuilder property(String propertyName) {
    return new ObjectPropertyBuilder(propertyName);
  }

  public ObjectExtensionBuilder layout(String name, SmartLayoutDefinition layoutDefinition) {
    descriptor.putLayoutsItem(name, layoutDefinition);
    return this;
  }

  public ObjectConstraintBuilder constraint() {
    return new ObjectConstraintBuilder();
  }

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

    private final ObjectPropertyDescriptor propertyDescriptor = new ObjectPropertyDescriptor();
    private final String name;

    private ObjectPropertyBuilder(String name) {
      this.name = name;
    }

    public ObjectPropertyBuilder builtIn() {
      propertyDescriptor.builtIn(true);
      return this;
    }

    public ObjectPropertyBuilder typeClass(Class<?> clazz) {
      return typeClass(clazz.getName());
    }

    public ObjectPropertyBuilder typeClass(String className) {
      propertyDescriptor.propertyDefinition(new PropertyDefinitionData()
          .name(name)
          .typeClass(className));
      return this;
    }


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

    public ObjectConstraintBuilder context(ObjectPropertyResolverContext context) {
      constraintDescriptor.contexts(context);
      return this;
    }

    public ObjectConstraintBuilder contextObject(String name, URI contextObjectUri) {
      constraintDescriptor.getContexts().addObjectsItem(new ObjectPropertyResolverContextObject()
          .name(name)
          .uri(contextObjectUri));
      return this;
    }

    public ObjectConstraintBuilder componentConstraints(List<ComponentConstraint> constraints) {
      constraintDescriptor.componentConstraints(constraints);
      return this;
    }

    public ObjectConstraintBuilder actionConstraints(List<UiActionConstraint> actionConstraints) {
      constraintDescriptor.actionConstraints(actionConstraints);
      return this;
    }

    public ObjectConstraintBuilder predicates(List<InvocationRequestDefinition> predicates) {
      constraintDescriptor.predicates(predicates);
      return this;
    }

    public ObjectExtensionBuilder and() {
      ObjectExtensionBuilder.this.descriptor.addConstraintsItem(constraintDescriptor);
      return ObjectExtensionBuilder.this;
    }
  }
}
