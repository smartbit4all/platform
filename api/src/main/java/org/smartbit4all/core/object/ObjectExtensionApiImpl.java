package org.smartbit4all.core.object;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.combobox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.container;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.multiSelectCombobox;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.selectionDefinition;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static java.util.stream.Collectors.toMap;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.ObjectDescriptor;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor.PropertyKindEnum;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class ObjectExtensionApiImpl implements ObjectExtensionApi {

  // TODO: We lose type information HERE!!!
  private static final ObjectPropertyDescriptor inlinePropertyDescriptor(
      PropertyDefinitionData propertyDefinitionData) {
    final ObjectPropertyDescriptor descriptor = new ObjectPropertyDescriptor()
        .propertyName(propertyDefinitionData.getName())
        .propertyKind(PropertyKindEnum.INLINE);

    final String typeClass = propertyDefinitionData.getTypeClass();
    if (Objects.equals(List.class.getName(), typeClass)) {
      descriptor
          .propertyStructure(ReferencePropertyKind.LIST)
          .propertyQualifiedName(Object.class.getName());
    } else if (Objects.equals(Map.class.getName(), typeClass)) {
      descriptor
          .propertyStructure(ReferencePropertyKind.MAP)
          .propertyQualifiedName(Object.class.getName());
    } else {
      descriptor
          .propertyStructure(ReferencePropertyKind.REFERENCE)
          .propertyQualifiedName(propertyDefinitionData.getTypeClass());
    }

    return descriptor;
  }

  private static final ObjectPropertyDescriptor refPropertyDescriptor(
      ReferenceDefinition referenceDefinition) {
    return new ObjectPropertyDescriptor()
        .propertyName(referenceDefinition.getSourcePropertyPath())
        .propertyKind(PropertyKindEnum.REFERENCE)
        .propertyQualifiedName(URI.class.getName()) // TODO: Can it be anything else?
        .referencedTypeQualifiedName(referenceDefinition.getTarget().getQualifiedName())
        .aggregation(referenceDefinition.getAggregation())
        .propertyStructure(referenceDefinition.getReferencePropertyKind());
  }

  @Autowired
  CollectionApi collectionApi;
  @Autowired
  ObjectApi objectApi;
  @Autowired
  ObjectDefinitionApi objectDefinitionApi;
  @Autowired
  ObjectLayoutApi objectLayoutApi;

  @Override
  public URI create(String definitionName, List<ObjectPropertyDescriptor> propertyDescriptors) {
    PropertyEvaluationResult evaluationResult = new PropertyDescriptorEvaluator(definitionName)
        .evaluate(propertyDescriptors);

    updateObjectDefinition(definitionName, evaluationResult);

    ObjectDescriptor objectDescriptor = new ObjectDescriptor()
        .definitionProperties(Collections.emptyMap())
        .extensionProperties(savePropertyDescriptors(propertyDescriptors))
        .name(definitionName)
        .layoutDescriptor(createDefaultLayout(definitionName, evaluationResult.layoutElements));

    return saveAndAddToGlobalMap(definitionName, objectDescriptor);
  }

  private void updateObjectDefinition(String name, PropertyEvaluationResult evaluationResult) {
    ObjectDefinitionBuilder builder = objectDefinitionApi
        .definition(name)
        .builder();
    builder.addAll(evaluationResult.propertyDefinitions)
        .addAllOutgoingReference(evaluationResult.referenceDefinitions)
        .commit();
    // do we need to do things akin to
    // org.smartbit4all.core.object.ObjectDefinitionApiImpl.initObjectReferences()?
  }

  private URI saveAndAddToGlobalMap(String definitionName, ObjectDescriptor objectDescriptor) {
    final URI objectDescriptorUri = objectApi.saveAsNew(SCHEMA, objectDescriptor);
    collectionApi
        .map(SCHEMA, EXTENSION_MAP)
        .put(definitionName, objectApi.getLatestUri(objectDescriptorUri));

    return objectDescriptorUri;
  }

  /**
   * Saves a list of property descriptors if necessary.
   * 
   * <p>
   * {@link ObjectPropertyDescriptor}s are saved only if their
   * {@link ObjectPropertyDescriptor#getUri()} property is null. If any two properties share the
   * same name, an unchecked exception is thrown.
   * 
   * @param propertyDescriptors a {@code List} of {@code ObjectPropertyDescriptor}s with unique
   *        names, may be null
   * @return a {@code Map} containing the {@code URI}s of the persisted property descriptors
   *         identified by their names
   */
  private Map<String, URI> savePropertyDescriptors(
      Collection<ObjectPropertyDescriptor> propertyDescriptors) {
    if (propertyDescriptors == null || propertyDescriptors.isEmpty()) {
      return Collections.emptyMap();
    }

    return propertyDescriptors.stream()
        .filter(Objects::nonNull)
        .collect(toMap(ObjectPropertyDescriptor::getPropertyName,
            this::persistPropertyDescriptor));
  }

  /**
   * Persists a property descriptor if its {@link ObjectPropertyDescriptor#getUri()} property is
   * null.
   * 
   * @param propertyDescriptor an {@link ObjectPropertyDescriptor}, not null
   * @return the {@code URI} of the persisted property descriptor
   */
  private URI persistPropertyDescriptor(ObjectPropertyDescriptor propertyDescriptor) {
    Objects.requireNonNull(propertyDescriptor, "propertyDescriptor cannot be null!");

    return (propertyDescriptor.getUri() == null)
        ? objectApi.saveAsNew(SCHEMA, propertyDescriptor)
        : propertyDescriptor.getUri();
  }

  /**
   * Creates and persists a default layout based on the supplied and/or generated layout elements.
   * 
   * <p>
   * If the layout elements of the object are solely form widgets, the returned layout describes a
   * <i>smart form</i>, with its items laid out vertically. If there are other, component level
   * layout elements present (e.g. a grid), the entire layout is wrapped in a vertical container,
   * with the form elements on the top (if any). Any subsequent components follow this optional form
   * in the container.
   * 
   * @param definitionName the qualified name of the object definition, not null
   * @param layoutElements a {@code List} of {@link SmartLayoutItem}s, not null or empty
   * @return the {@code URI} of the saved {@link ObjectLayoutDescriptor}
   */
  private URI createDefaultLayout(String definitionName, List<SmartLayoutItem> layoutElements) {
    if (layoutElements == null || layoutElements.isEmpty()) {
      throw new IllegalArgumentException("Layout elements may not be null or empty!");
    }
    if (Strings.isNullOrEmpty(definitionName)) {
      throw new IllegalArgumentException("definitionName may not be null or empty!");
    }

    if (layoutElements.stream().allMatch(SmartLayoutItem::isNone)) {
      throw new IllegalArgumentException();
    }

    ObjectLayoutBuilder layoutBuilder = objectLayoutApi.create(definitionName);
    if (layoutElements.stream().noneMatch(SmartLayoutItem::isComponent)) {
      // only form elements are present (and maybe NONEs):
      return layoutBuilder
          .layout(form(LayoutDirection.VERTICAL, layoutElements.stream()
              .filter(SmartLayoutItem::isFormElement)
              .map(SmartLayoutItem::formElement)
              .toArray(SmartWidgetDefinition[]::new)))
          .build();
    }

    final boolean hasForm = layoutElements.stream().anyMatch(SmartLayoutItem::isFormElement);
    final SmartComponentLayoutDefinition container = container(LayoutDirection.VERTICAL);
    if (hasForm) {
      container.addComponentsItem(form(
          LayoutDirection.VERTICAL,
          layoutElements.stream()
              .filter(SmartLayoutItem::isFormElement)
              .map(SmartLayoutItem::formElement)
              .toArray(SmartWidgetDefinition[]::new)));
    }

    layoutElements.stream()
        .filter(SmartLayoutItem::isComponent)
        .map(SmartLayoutItem::component)
        .forEach(container::addComponentsItem);

    return layoutBuilder
        .layout(container)
        .build();
  }

  @Override
  public URI create(String definitionName, ObjectDefinition<?> definition,
      List<ObjectPropertyDescriptor> extensionDescriptors) {
    Objects.requireNonNull(definitionName, "definitionName cannot be null!");
    Objects.requireNonNull(definition, "definition cannot be null!");
    Objects.requireNonNull(extensionDescriptors, "extensionDescriptors cannot be null!");

    if (definitionName.equals(definition.getQualifiedName())) {
      throw new IllegalArgumentException(
          "Extension's qualified name may not match the original definition's name: [ "
              + definitionName
              + " ]!");
    }

    Map<String, PropertyDefinitionData> propertiesByName = definition.getPropertiesByName();
    Map<String, ReferenceDefinition> outgoingReferences = definition.getOutgoingReferences();

    final Map<String, ObjectPropertyDescriptor> propertyDescriptors = new HashMap<>();
    propertyDescriptors.putAll(propertiesByName.entrySet().stream()
        .collect(toMap(Map.Entry::getKey, e -> inlinePropertyDescriptor(e.getValue()))));
    propertyDescriptors.putAll(outgoingReferences.entrySet().stream()
        .collect(toMap(Map.Entry::getKey, e -> refPropertyDescriptor(e.getValue()))));

    // we save a shallow copy for later:
    final Map<String, ObjectPropertyDescriptor> definitionProperties =
        new HashMap<>(propertyDescriptors);
    final Map<String, ObjectPropertyDescriptor> extensionProperties = extensionDescriptors.stream()
        .collect(toMap(
            ObjectPropertyDescriptor::getPropertyName,
            Function.identity(),
            (a, b) -> b));

    // the incoming extension descriptors override the base definition's values:
    propertyDescriptors.putAll(extensionProperties);

    PropertyEvaluationResult evaluationResult = new PropertyDescriptorEvaluator(definitionName)
        .evaluate(propertyDescriptors.values());
    updateObjectDefinition(definitionName, evaluationResult);

    ObjectDescriptor objectDescriptor = new ObjectDescriptor()
        .definitionProperties(savePropertyDescriptors(definitionProperties.values()))
        .extensionProperties(savePropertyDescriptors(extensionProperties.values()))
        .name(definitionName)
        .layoutDescriptor(createDefaultLayout(definitionName, evaluationResult.layoutElements));

    return saveAndAddToGlobalMap(definitionName, objectDescriptor);
  }

  @Override
  public URI extend(String definitionName, List<ObjectPropertyDescriptor> extensionDescriptors) {
    final boolean knownExtension = collectionApi
        .map(SCHEMA, EXTENSION_MAP)
        .uris()
        .containsKey(definitionName);
    if (!knownExtension) {
      throw new IllegalArgumentException("Cannot extend the [ " + definitionName
          + " ] object descriptor, for it does not exist! Try calling 'create()'!");
    }

    PropertyEvaluationResult evaluationResult = new PropertyDescriptorEvaluator(definitionName)
        .evaluate(extensionDescriptors);
    updateObjectDefinition(definitionName, evaluationResult);
    // let's merge changes in the descriptor:
    Map<String, ObjectPropertyDescriptor> extensionDescriptorsByName = extensionDescriptors
        .stream().collect(toMap(
            ObjectPropertyDescriptor::getPropertyName,
            Function.identity(),
            (a, b) -> a));
    final ObjectNode objectDescriptorNode = objectApi.load(collectionApi
        .map(SCHEMA, EXTENSION_MAP)
        .uris()
        .get(definitionName));
    objectDescriptorNode.modify(ObjectDescriptor.class, d -> {

      Map<String, URI> definitionProperties = d.getDefinitionProperties();
      extensionDescriptorsByName.keySet().forEach(definitionProperties::remove);

      return d;
    });
    return objectApi.save(objectDescriptorNode);
  }

  private static final class PropertyDescriptorEvaluator {

    private final String definitionName;

    private PropertyDescriptorEvaluator(String definitionName) {
      this.definitionName = definitionName;
    }

    private PropertyEvaluationResult evaluate(
        Collection<ObjectPropertyDescriptor> propertyDescriptors) {
      final List<PropertyDefinitionData> propertyDefinitions = new ArrayList<>();
      final List<ReferenceDefinition> referenceDefinitions = new ArrayList<>();
      final List<SmartLayoutItem> layoutElements = new ArrayList<>();

      for (ObjectPropertyDescriptor opd : propertyDescriptors) {
        final String propertyName = opd.getPropertyName();
        if (PropertyKindEnum.INLINE == opd.getPropertyKind()) {
          PropertyDefinitionData propDefData = new PropertyDefinitionData()
              .name(propertyName)
              .typeClass(getInlinePropertyDefinitionTypeClass(opd));
          // TODO: HANDLE LISTS AND MAPS HERE!
          propertyDefinitions.add(propDefData);

        } else if (PropertyKindEnum.REFERENCE == opd.getPropertyKind()) {
          ReferenceDefinition refDefinition = new ReferenceDefinition(new ReferenceDefinitionData()
              .sourceObjectName(definitionName)
              .propertyPath(propertyName)
              .propertyKind(opd.getPropertyStructure())
              .targetObjectName(opd.getReferencedTypeQualifiedName())
              .aggregation(opd.getAggregation()));
          referenceDefinitions.add(refDefinition);
        }
        layoutElements.add(createDefaultWidget(opd));
      }

      return new PropertyEvaluationResult(
          propertyDefinitions,
          referenceDefinitions,
          layoutElements);
    }

    private String getInlinePropertyDefinitionTypeClass(ObjectPropertyDescriptor opd) {
      switch (opd.getPropertyStructure()) {
        case REFERENCE:
          return opd.getPropertyQualifiedName();
        case LIST:
          return List.class.getName();
        case MAP:
          return Map.class.getName();
        default:
          throw new AssertionError();
      }
    }

    /**
     * 
     * @param definitionName
     * @param propertyDescriptor
     * @return
     */
    private SmartLayoutItem createDefaultWidget(ObjectPropertyDescriptor propertyDescriptor) {
      Objects.requireNonNull(propertyDescriptor, "propertyDescriptor cannot be null!");

      final SmartWidgetDefinition widget = propertyDescriptor.getWidget();
      if (widget != null) {
        return SmartLayoutItem.ofFormElement(widget);
      }

      return new SmartWidgetConstructor(definitionName, propertyDescriptor).construct();
    }

  }

  private static final class PropertyEvaluationResult {
    private final List<PropertyDefinitionData> propertyDefinitions;
    private final List<ReferenceDefinition> referenceDefinitions;
    private final List<SmartLayoutItem> layoutElements;

    private PropertyEvaluationResult(List<PropertyDefinitionData> propertyDefinitions,
        List<ReferenceDefinition> referenceDefinitions, List<SmartLayoutItem> layoutElements) {
      this.propertyDefinitions = propertyDefinitions;
      this.referenceDefinitions = referenceDefinitions;
      this.layoutElements = layoutElements;
    }
  }

  private static final class SmartWidgetConstructor {

    // input data:
    private final String idPrefix;
    private final ObjectPropertyDescriptor propertyDescriptor;

    // calculated data:
    private final boolean valueSetPresent;
    private final String key;
    private final boolean inline;

    private SmartWidgetConstructor(String idPrefix, ObjectPropertyDescriptor propertyDescriptor) {
      this.idPrefix = Objects.requireNonNull(idPrefix, "idPrefix cannot be null!");;
      this.propertyDescriptor =
          Objects.requireNonNull(propertyDescriptor, "propertyDescriptor cannot be null!");;

      this.valueSetPresent = (propertyDescriptor.getValueSet() != null)
          || (propertyDescriptor.getValueSetName() != null);
      this.key = propertyDescriptor.getPropertyName();
      this.inline = PropertyKindEnum.INLINE == propertyDescriptor.getPropertyKind();
    }

    private SmartLayoutItem construct() {

      // if type is String and no value set is present, it's a textfield!
      if (isPlainStringProperty()) {
        return SmartLayoutItem.ofFormElement(textfield(key, key));
      }

      // if type is date, it's a date picker:
      if (isPlainDateProperty()) {
        return SmartLayoutItem.ofFormElement(ObjectLayoutBuilder.datePicker(key, key));
      }

      // if type is date-time, it's a date-time picker:
      if (isPlainDateTimeProperty()) {
        return SmartLayoutItem.ofFormElement(ObjectLayoutBuilder.datetimePicker(key, key));
      }

      if (isPlainPropertyOfType(Short.class, Integer.class, Long.class, short.class, int.class,
          long.class)) {
        return SmartLayoutItem.ofFormElement(ObjectLayoutBuilder.integralNumberField(key, key));
      }

      if (isPlainPropertyOfType(Float.class, Double.class, float.class, double.class)) {
        return SmartLayoutItem
            .ofFormElement(ObjectLayoutBuilder.floatingPointNumberField(key, key));
      }

      // if type is boolean, it's a radio button group:
      if (isPlainPropertyOfType(Boolean.class)) {
        return SmartLayoutItem.ofFormElement(ObjectLayoutBuilder.radioButtonGroup(key, key, null)
            .addValuesItem(new Value()
                .code("true")
                .displayValue("true"))
            .addValuesItem(new Value()
                .code("false")
                .displayValue("false"))); // TODO: TEST!
      }

      // if a value set is present, it can be a select or a multi-select depending on the struct:
      if (valueSetPresent) {
        // TODO: Find a way to generalise this!
        final SelectionDefinition selectionDefinition = selectionDefinition(
            propertyDescriptor.getValueSetName(),
            GenericValue.NAME);
        switch (propertyDescriptor.getPropertyStructure()) {
          case REFERENCE:
            // single object:
            return SmartLayoutItem.ofFormElement(combobox(key, key, selectionDefinition));
          case LIST:
            return SmartLayoutItem
                .ofFormElement(multiSelectCombobox(key, idPrefix, selectionDefinition));
          case MAP:
            throw new UnsupportedOperationException();
          default:
            break;
        }
      }

      // if value is a ref list, this is a grid:
      if (!inline && ReferencePropertyKind.LIST == propertyDescriptor.getPropertyStructure()) {
        return SmartLayoutItem.ofComponent(ObjectLayoutBuilder
            .grid(idPrefix + StringConstant.HYPHEN + key + StringConstant.HYPHEN + "grid"));
      }

      return SmartLayoutItem.none();
    }

    private boolean isPlainStringProperty() {
      return inline
          && Objects.equals(String.class.getName(), propertyDescriptor.getPropertyQualifiedName());
    }

    private boolean isPlainDateProperty() {
      return inline
          && Objects.equals(LocalDate.class.getName(),
              propertyDescriptor.getPropertyQualifiedName());
    }

    private boolean isPlainDateTimeProperty() {
      return inline
          && Stream
              .of(LocalDateTime.class.getName(), OffsetDateTime.class.getName())
              .anyMatch(propertyDescriptor.getPropertyQualifiedName()::equals);
    }

    @SafeVarargs
    @SuppressWarnings("rawtypes")
    private final boolean isPlainPropertyOfType(Class type, Class... otherTypes) {
      return inline && ((otherTypes == null || otherTypes.length == 0)
          ? Objects.equals(type.getName(), propertyDescriptor.getPropertyQualifiedName())
          : Stream.concat(Stream.of(type), Arrays.stream(otherTypes))
              .map(Class::getName)
              .anyMatch(propertyDescriptor.getPropertyQualifiedName()::equals));
    }

  }

  private static final class SmartLayoutItem {
    private final SmartWidgetDefinition formElement;
    private final SmartComponentLayoutDefinition component;

    private static SmartLayoutItem ofFormElement(SmartWidgetDefinition formElement) {
      return new SmartLayoutItem(
          Objects.requireNonNull(formElement, "formElement cannot be null!"),
          null);
    }

    private static SmartLayoutItem ofComponent(SmartComponentLayoutDefinition component) {
      return new SmartLayoutItem(
          null,
          Objects.requireNonNull(component, "component cannot be null!"));
    }

    private static SmartLayoutItem none() {
      return new SmartLayoutItem(null, null);
    }

    private SmartLayoutItem(SmartWidgetDefinition formElement,
        SmartComponentLayoutDefinition component) {
      this.formElement = formElement;
      this.component = component;
    }

    private boolean isFormElement() {
      return formElement != null;
    }

    private boolean isComponent() {
      return component != null;
    }

    private boolean isNone() {
      return formElement == null && component == null;
    }

    private SmartWidgetDefinition formElement() {
      return this.formElement;
    }

    private SmartComponentLayoutDefinition component() {
      return this.component;
    }

  }
}
