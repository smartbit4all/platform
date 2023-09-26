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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.formdefinition.bean.SelectionDefinition;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.ObjectDescriptor;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.object.bean.ObjectNodeData;
import org.smartbit4all.api.object.bean.ObjectNodeState;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor.PropertyKindEnum;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.object.bean.RefObject;
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

  private static final Logger log = LoggerFactory.getLogger(ObjectExtensionApiImpl.class);

  // TODO: We lose type information HERE!!!
  private static ObjectPropertyDescriptor inlinePropertyDescriptor(
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

  private static ObjectPropertyDescriptor refPropertyDescriptor(
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
    PropertyEvaluationResult evaluationResult = new PropertyDescriptorEvaluator(
        objectDefinitionApi,
        definitionName)
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
    registerObjectDefinition(name, objectDefinitionApi.definition(name), evaluationResult);
  }

  private ObjectDefinition<?> registerObjectDefinition(String name,
      ObjectDefinition<?> definition,
      PropertyEvaluationResult evaluationResult) {
    definition.builder()
        .alias(ObjectDefinitionApiImpl.getAliasFromClassName(name))
        .addAll(evaluationResult.propertyDefinitions)
        .addAllOutgoingReference(evaluationResult.referenceDefinitions)
        .commit();
    ObjectDefinitionApiImpl.registerDefinition(objectDefinitionApi, definition);
    definition.getOutgoingReferences().values().forEach(r -> r.setSource(definition));
    // do we need to do things akin to
    // org.smartbit4all.core.object.ObjectDefinitionApiImpl.initObjectReferences()?
    return definition;
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

    final Map<String, ObjectPropertyDescriptor> propertyDescriptors =
        describeProperties(definition);

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

    PropertyEvaluationResult evaluationResult =
        new PropertyDescriptorEvaluator(objectDefinitionApi, definitionName)
            .evaluate(propertyDescriptors.values());
    updateObjectDefinition(definitionName, evaluationResult);

    ObjectDescriptor objectDescriptor = new ObjectDescriptor()
        .definitionProperties(savePropertyDescriptors(definitionProperties.values()))
        .extensionProperties(savePropertyDescriptors(extensionProperties.values()))
        .name(definitionName)
        .layoutDescriptor(createDefaultLayout(definitionName, evaluationResult.layoutElements));

    return saveAndAddToGlobalMap(definitionName, objectDescriptor);
  }

  private Map<String, ObjectPropertyDescriptor> describeProperties(ObjectDefinition<?> definition) {
    Map<String, PropertyDefinitionData> propertiesByName = definition.getPropertiesByName();
    Map<String, ReferenceDefinition> outgoingReferences = definition.getOutgoingReferences();

    final Map<String, ObjectPropertyDescriptor> propertyDescriptors = new HashMap<>();
    propertyDescriptors.putAll(propertiesByName.entrySet().stream()
        .collect(toMap(Map.Entry::getKey, e -> inlinePropertyDescriptor(e.getValue()))));
    propertyDescriptors.putAll(outgoingReferences.entrySet().stream()
        .collect(toMap(Map.Entry::getKey, e -> refPropertyDescriptor(e.getValue()))));
    return propertyDescriptors;
  }

  @Override
  public URI extend(String definitionName, List<ObjectPropertyDescriptor> extensionDescriptors) {
    if (!exists(definitionName)) {
      throw new IllegalArgumentException("Cannot extend the [ " + definitionName
          + " ] object descriptor, for it does not exist! Try calling 'create()'!");
    }

    PropertyEvaluationResult evaluationResult =
        new PropertyDescriptorEvaluator(objectDefinitionApi, definitionName)
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
      if (definitionProperties != null) {
        extensionDescriptorsByName.keySet().forEach(definitionProperties::remove);
      }

      Map<String, URI> extensionProperties = d.getExtensionProperties();
      if (extensionProperties == null) {
        extensionProperties = new HashMap<>();
      }
      extensionProperties.putAll(savePropertyDescriptors(extensionDescriptorsByName.values()));

      return d;
    });
    return objectApi.save(objectDescriptorNode);
  }

  @Override
  public boolean exists(String definitionName) {
    return collectionApi
        .map(SCHEMA, EXTENSION_MAP)
        .uris()
        .containsKey(definitionName);
  }

  @Override
  public ObjectLayoutDescriptor generateDefaultLayout(String definitionName) {
    if (!exists(definitionName)) {
      log.debug("Unknown definition [ {} ], trying to create descriptor with ObjectDefinition API",
          definitionName);
    }

    final URI descriptorUri = Optional
        .ofNullable(collectionApi
            .map(SCHEMA, EXTENSION_MAP)
            .uris()
            .get(definitionName))
        .orElseGet(() -> describe(objectDefinitionApi.definition(definitionName)));
    final ObjectDescriptor objectDescriptor = objectApi
        .load(descriptorUri)
        .getObject(ObjectDescriptor.class);

    return objectApi
        .loadLatest(objectDescriptor.getLayoutDescriptor())
        .getObject(ObjectLayoutDescriptor.class);
  }

  /**
   * Enumerates all property descriptors available in the object descriptor.
   * 
   * <p>
   * The final list is obtained by merging the contents of
   * {@link ObjectDescriptor#getDefinitionProperties()} and
   * {@link ObjectDescriptor#getExtensionProperties()}.
   * 
   * @param objectDescriptor an {@link ObjectDescriptor}
   * @return a {@code List} of all available {@link ObjectPropertyDescriptor}s in the object
   *         descriptor
   */
  private List<ObjectPropertyDescriptor> enumerateProps(final ObjectDescriptor objectDescriptor) {
    return Stream
        .concat(
            objectDescriptor.getDefinitionProperties() == null
                ? Stream.empty()
                : objectDescriptor.getDefinitionProperties().values().stream(),
            objectDescriptor.getExtensionProperties() == null
                ? Stream.empty()
                : objectDescriptor.getExtensionProperties().values().stream())
        .map(objectApi::load)
        .map(n -> n.getObject(ObjectPropertyDescriptor.class))
        .collect(Collectors.toList());
  }

  @Override
  public ObjectNode newInstance(String definitionName, String storageSchema) {
    Objects.requireNonNull(definitionName, "definitionName cannot be null!");
    Objects.requireNonNull(storageSchema, "storageSchema cannot be null!");

    final ObjectDefinition<?> definition = objectDefinitionApi.definition(definitionName);

    final ObjectNodeData data = new ObjectNodeData()
        .objectUri(null)
        .qualifiedName(definition.getQualifiedName())
        .storageSchema(storageSchema)
        .objectAsMap(new ObjectMap(definition) /* new LinkedHashMap<>() */)
        .state(ObjectNodeState.NEW)
        .versionNr(null);

    return new ObjectNode(objectApi, definition, data);
  }

  @Override
  public ObjectNode newInstance(URI objectDescriptorUri, String storageSchema) {
    Objects.requireNonNull(objectDescriptorUri, "objectDescriptorUri cannot be null!");
    Objects.requireNonNull(storageSchema, "storageSchema cannot be null!");

    return newInstance(
        objectApi.load(objectDescriptorUri).getValueAsString(ObjectDescriptor.NAME),
        storageSchema);
  }

  @Override
  public ObjectDefinition<?> assemble(String definitionName) {
    return Optional
        .ofNullable(collectionApi.map(SCHEMA, EXTENSION_MAP))
        .map(StoredMap::uris)
        .map(m -> m.get(definitionName))
        .map(objectApi::loadLatest)
        .map(n -> n.getObject(ObjectDescriptor.class))
        .map(this::enumerateProps)
        .map(props -> new PropertyDescriptorEvaluator(objectDefinitionApi, definitionName)
            .evaluateStructure(props))
        .map(res -> {
          ObjectDefinition<?> definition = objectDefinitionApi.baseDefinition(definitionName);
          return registerObjectDefinition(definitionName, definition, res);
        })
        .orElse(null);
  }

  private URI describe(ObjectDefinition<?> definition) {
    Objects.requireNonNull(definition, "definition cannot be null!");

    final String definitionName = definition.getQualifiedName();

    Map<String, ObjectPropertyDescriptor> propertiesByName = describeProperties(definition);
    PropertyEvaluationResult evaluationResult =
        new PropertyDescriptorEvaluator(objectDefinitionApi, definitionName)
            .evaluate(propertiesByName.values());

    final ObjectDescriptor objectDescriptor = new ObjectDescriptor()
        .definitionProperties(savePropertyDescriptors(propertiesByName.values()))
        .name(definitionName)
        .layoutDescriptor(createDefaultLayout(definitionName, evaluationResult.layoutElements));

    return saveAndAddToGlobalMap(definitionName, objectDescriptor);
  }

  private static final class PropertyDescriptorEvaluator {
    private final ObjectDefinitionApi objectDefinitionApi;
    private final String definitionName;

    private PropertyDescriptorEvaluator(ObjectDefinitionApi objectDefinitionApi,
        String definitionName) {
      this.objectDefinitionApi = objectDefinitionApi;
      this.definitionName = definitionName;
    }

    private PropertyEvaluationResult evaluate(
        Collection<ObjectPropertyDescriptor> propertyDescriptors, boolean layout,
        boolean structure) {
      final List<PropertyDefinitionData> propertyDefinitions = new ArrayList<>();
      final List<ReferenceDefinition> referenceDefinitions = new ArrayList<>();
      final List<SmartLayoutItem> layoutElements = new ArrayList<>();


      for (ObjectPropertyDescriptor opd : propertyDescriptors) {
        if (structure) {
          final String propertyName = opd.getPropertyName();
          if (PropertyKindEnum.INLINE == opd.getPropertyKind()) {
            PropertyDefinitionData propDefData = new PropertyDefinitionData()
                .name(propertyName)
                .typeClass(getInlinePropertyDefinitionTypeClass(opd));
            propertyDefinitions.add(propDefData);

          } else if (PropertyKindEnum.REFERENCE == opd.getPropertyKind()) {
            final String referencedTypeQualifiedName = opd.getReferencedTypeQualifiedName();
            ReferenceDefinition refDefinition =
                new ReferenceDefinition(new ReferenceDefinitionData()
                    .sourceObjectName(definitionName)
                    .propertyPath(propertyName)
                    .propertyKind(opd.getPropertyStructure())
                    .targetObjectName(referencedTypeQualifiedName)
                    .aggregation(opd.getAggregation()));
            refDefinition.setTarget(objectDefinitionApi.definition(referencedTypeQualifiedName));
            referenceDefinitions.add(refDefinition);
          }
        }

        if (layout) {
          final SmartLayoutItem widget = createDefaultWidget(opd);
          layoutElements.add(widget);
          if (widget.isFormElement() && opd.getWidget() == null) {
            opd.setWidget(widget.formElement());
          }
        }
      }

      return new PropertyEvaluationResult(
          propertyDefinitions,
          referenceDefinitions,
          layoutElements);
    }

    private PropertyEvaluationResult evaluate(
        Collection<ObjectPropertyDescriptor> propertyDescriptors) {
      return evaluate(propertyDescriptors, true, true);
    }

    private PropertyEvaluationResult evaluateStructure(
        Collection<ObjectPropertyDescriptor> propertyDescriptors) {
      return evaluate(propertyDescriptors, false, true);
    }

    private PropertyEvaluationResult evaluateLayout(
        Collection<ObjectPropertyDescriptor> propertyDescriptors) {
      return evaluate(propertyDescriptors, true, false);
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
     * Generates a default widget based on a property descriptor.
     *
     * <p>
     * If the property descriptor is equipped with a widget definition already, it is returned. If
     * there was no widget present, the most appropriate layout element is generated based on the
     * type information available in the property descriptor
     *
     * @param propertyDescriptor the {@link ObjectPropertyDescriptor} to be examined, not null
     * @return a {@link SmartLayoutItem} wrapping either a {@link SmartWidgetDefinition} for form
     *         elements, a {@link SmartComponentLayoutDefinition} for more complex, component type
     *         properties, or nothing if the described property cannot be interpreted as a widget.
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
      Objects.requireNonNull(idPrefix, "idPrefix cannot be null!");
      if (idPrefix.contains(StringConstant.DOT)) {
        this.idPrefix = idPrefix.substring(idPrefix.lastIndexOf('.') + 1);
      } else {
        this.idPrefix = idPrefix;
      }

      this.propertyDescriptor = Objects.requireNonNull(
          propertyDescriptor,
          "propertyDescriptor cannot be null!");

      this.valueSetPresent = (propertyDescriptor.getValueSet() != null)
          && (propertyDescriptor.getValueSet().getQualifiedName() != null);
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
            propertyDescriptor.getValueSet().getQualifiedName(),
            GenericValue.NAME);
        switch (propertyDescriptor.getPropertyStructure()) {
          case REFERENCE:
            // single object:
            return SmartLayoutItem.ofFormElement(combobox(key, key, selectionDefinition));
          case LIST:
            if (propertyDescriptor.getPropertyQualifiedName().equals(RefObject.class.getName())) {
              return SmartLayoutItem.ofComponent(ObjectLayoutBuilder
                  .grid(idPrefix + StringConstant.HYPHEN + key + StringConstant.HYPHEN + "grid"));
            }
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
          && Arrays.asList(LocalDateTime.class.getName(), OffsetDateTime.class.getName())
              .contains(propertyDescriptor.getPropertyQualifiedName());
    }

    @SuppressWarnings("rawtypes")
    private boolean isPlainPropertyOfType(Class type, Class... otherTypes) {
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
