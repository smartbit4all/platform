package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition;
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition.TypeEnum;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.bean.InvocationParameterResolver;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ObjectConstraintDescriptor;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContextObject;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.ComponentType;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.ComponentWidgetType;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentWidgetDefinition;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiActionConstraint;
import org.smartbit4all.core.utility.StringConstant;

public final class ObjectLayoutBuilder {

  static final String DEFAULT_LAYOUT = "default";

  /**
   * Constructs a grid layout definition with the provided grid identifier.
   *
   * @param gridIdentifier the {@code String} grid identifier for the grid to use
   *
   * @return a {@link SmartComponentLayoutDefinition} representing a grid
   */
  public static SmartComponentLayoutDefinition grid(String gridIdentifier) {
    return new SmartComponentLayoutDefinition()
        .type(ComponentType.WIDGET)
        .widget(new SmartComponentWidgetDefinition()
            .type(ComponentWidgetType.GRID)
            .gridIdentifier(gridIdentifier));
  }

  /**
   * Constructs a form layout definition with the provided widget definitions.
   *
   * @param layoutDirection the cardinal direction in which form elements are placed after each
   *        other
   * @param formWidgetDefs one or more {@link SmartWidgetDefinition} describing a form widget
   *
   * @return a {@link SmartComponentLayoutDefinition} representing a form
   *
   * @see #textfield(String, String)
   * @see #combobox(String, String, SelectionDefinition)
   */
  public static SmartComponentLayoutDefinition form(LayoutDirection layoutDirection,
      SmartWidgetDefinition... formWidgetDefs) {
    return new SmartComponentLayoutDefinition()
        .type(ComponentType.FORM)
        .direction(layoutDirection)
        .form(Arrays.asList(formWidgetDefs));
  }

  /**
   * Constructs a grid layout definition with the provided tree identifier.
   *
   * @param treeIdentifier the {@code String} tree identifier for the tree's service to use
   *
   * @return a {@link SmartComponentLayoutDefinition} representing a tree
   */
  public static SmartComponentLayoutDefinition tree(String treeIdentifier) {
    return new SmartComponentLayoutDefinition()
        .type(ComponentType.WIDGET)
        .widget(new SmartComponentWidgetDefinition()
            .type(ComponentWidgetType.GRID)
            .treeIdentifier(treeIdentifier));
  }

  /**
   * Constructs a container for wrapping a group of arbitrary component layouts.
   *
   * <p>
   * The {@link SmartComponentLayoutDefinition#addComponentsItem(SmartComponentLayoutDefinition)}
   * method can be invoked on the layout definition returned by this static factory method to add
   * inner layout definitions.
   *
   * @param layoutDirection the cardinal direction in which inner layouts are placed after each
   *        other in this container
   *
   * @return a {@link SmartComponentLayoutDefinition} representing a container
   */
  public static SmartComponentLayoutDefinition container(LayoutDirection layoutDirection) {
    return new SmartComponentLayoutDefinition()
        .type(ComponentType.CONTAINER)
        .direction(layoutDirection);
  }

  public static SmartWidgetDefinition textfield(String key, String label) {
    return new SmartWidgetDefinition()
        .type(SmartFormWidgetType.TEXT_FIELD)
        .key(key)
        .label(label);
  }

  public static SmartWidgetDefinition combobox(String key, String label,
      SelectionDefinition selectionDefinition) {
    return new SmartWidgetDefinition()
        .type(SmartFormWidgetType.SELECT)
        .key(key)
        .label(label)
        .selection(selectionDefinition);
  }

  public static SmartWidgetDefinition datePicker(String key, String label) {
    return new SmartWidgetDefinition()
        .type(SmartFormWidgetType.DATE_PICKER)
        .key(key)
        .label(label);
  }

  public static SmartWidgetDefinition datetimePicker(String key, String label) {
    return new SmartWidgetDefinition()
        .type(SmartFormWidgetType.DATE_TIME_PICKER)
        .key(key)
        .label(label);
  }

  public static SmartWidgetDefinition radioButtonGroup(String key, String label,
      SelectionDefinition selectionDefinition) {
    return new SmartWidgetDefinition()
        .type(SmartFormWidgetType.ITEM_GROUP)
        .key(key)
        .label(label)
        .selection(selectionDefinition);
  }

  public static String widgetKey(String... elements) {
    return Arrays.stream(elements).collect(Collectors.joining(StringConstant.DOT));
  }

  public static SelectionDefinition selectionDefinition(String valueSetName,
      String displayProperty) {
    return new SelectionDefinition()
        .type(TypeEnum.PROPERTY)
        .valueSetName(valueSetName)
        .displayProperty(displayProperty);
  }

  private final ObjectApi objectApi;
  private final ObjectLayoutApi objectLayoutApi;
  private final ObjectLayoutDescriptor layoutDescriptor;

  ObjectLayoutBuilder(ObjectApi objectApi, ObjectLayoutApi objectLayoutApi, String name) {
    this(objectApi, objectLayoutApi, new ObjectLayoutDescriptor().name(name));
  }

  ObjectLayoutBuilder(ObjectApi objectApi, ObjectLayoutApi objectLayoutApi,
      ObjectLayoutDescriptor layoutDescriptor) {
    this.objectApi = Objects.requireNonNull(objectApi, "objectApi cannot be null!");
    this.objectLayoutApi =
        Objects.requireNonNull(objectLayoutApi, "objectLayoutApi cannot be null!");
    this.layoutDescriptor =
        Objects.requireNonNull(layoutDescriptor, "layoutDescriptor cannot be null!");;
  }

  public ObjectLayoutBuilder layout(SmartComponentLayoutDefinition layoutDefinition) {
    this.layoutDescriptor.putLayoutsItem(DEFAULT_LAYOUT, layoutDefinition);
    return this;
  }

  public ObjectLayoutBuilder layout(String layoutName,
      SmartComponentLayoutDefinition layoutDefinition) {
    this.layoutDescriptor.putLayoutsItem(layoutName, layoutDefinition);
    return this;
  }

  public ObjectConstraintBuilder constraint() {
    return new ObjectConstraintBuilder();
  }

  public URI build() {
    return (layoutDescriptor.getUri() != null)
        ? updateDescriptor(layoutDescriptor.getUri())
        : objectLayoutApi.saveNewLayoutDescriptor(layoutDescriptor);
  }

  private URI updateDescriptor(URI descriptorUri) {
    final ObjectNode descriptorNode = this.objectApi.load(descriptorUri);
    descriptorNode.modify(ObjectLayoutDescriptor.class, old -> layoutDescriptor);
    return objectApi.save(descriptorNode);
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
     * <li>The current session under {@link ObjectLayoutApi#SESSION_CONTEXT}
     * <li>The current user under {@link ObjectLayoutApi#USER_CONTEXT}
     * <li>The investigated object under {@link ObjectLayoutApi#THIS_CONTEXT}
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
     * underlying {@link ObjectLayoutDescriptor}.
     *
     * @return the original {@code ObjectLayoutBuilder} with the constraint changes committed
     */
    public ObjectLayoutBuilder and() {
      ObjectLayoutBuilder.this.layoutDescriptor.addConstraintsItem(constraintDescriptor);
      return ObjectLayoutBuilder.this;
    }
  }

}
