package org.smartbit4all.core.object;

import static org.smartbit4all.core.object.ObjectLayoutBuilder.datePicker;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.datetimePicker;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.floatingPointNumberField;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.form;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.integralNumberField;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.textfield;
import static org.smartbit4all.core.object.ObjectLayoutBuilder.toggle;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ContextObjectData;
import org.smartbit4all.api.object.bean.ContextObjectDataItem;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectConstraintDescriptor;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDefinitionDescriptor;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.LayoutDirection;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiActionConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class ObjectLayoutApiImpl implements ObjectLayoutApi {

  private static final String MAP = "layout-descriptors";
  private static final Logger log = LoggerFactory.getLogger(ObjectLayoutApiImpl.class);

  private final ObjectMapper objectMapper = ObjectSerializerByObjectMapper.getObjectMapper();

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired(required = false)
  private SessionApi sessionApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Override
  public Stream<ObjectNode> findAllObjectLayoutDescriptors() {
    return collectionApi.map(SCHEMA, MAP).uris().values().stream().map(objectApi::loadLatest);
  }

  @Override
  public Optional<ObjectNode> findObjectLayoutDescriptorByName(String name) {
    return Optional
        .ofNullable(collectionApi
            .map(SCHEMA, MAP)
            .uris()
            .get(name))
        .map(objectApi::loadLatest);
  }

  @Override
  public ObjectLayoutBuilder create(String name) {
    return new ObjectLayoutBuilder(objectApi, this, name);
  }

  @Override
  public ObjectLayoutBuilder update(String name) {
    return findObjectLayoutDescriptorByName(name)
        .map(n -> n.getObject(ObjectLayoutDescriptor.class))
        .map(descriptor -> new ObjectLayoutBuilder(objectApi, this, descriptor))
        .orElseGet(() -> create(name));
  }

  @Override
  public URI saveNewLayoutDescriptor(ObjectLayoutDescriptor descriptor) {
    URI uri = objectApi.saveAsNew(SCHEMA, descriptor);
    collectionApi.map(SCHEMA, MAP).put(descriptor.getName(), objectApi.getLatestUri(uri));
    return uri;
  }

  @Override
  public ObjectDisplay getObjectDisplay(URI objectUri, String name) {
    final ObjectLayoutDescriptor layoutDescriptor = findObjectLayoutDescriptorByName(name)
        .map(n -> n.getObject(ObjectLayoutDescriptor.class))
        .orElseThrow(() -> new IllegalArgumentException(
            "Unknown object layout descriptor: [ " + name + " ]!"));
    return getObjectDisplayInternal(objectUri, layoutDescriptor);
  }

  @Override
  public ObjectDisplay getObjectDisplay(URI objectUri, URI descriptorUri) {
    final ObjectLayoutDescriptor layoutDescriptor = objectApi
        .loadLatest(descriptorUri)
        .getObject(ObjectLayoutDescriptor.class);
    return getObjectDisplayInternal(objectUri, layoutDescriptor);
  }

  @Override
  public ObjectDisplay getObjectDisplay(URI objectUri, ObjectLayoutDescriptor descriptor) {
    return getObjectDisplayInternal(objectUri, descriptor);
  }

  private ObjectDisplay getObjectDisplayInternal(URI objectUri,
      ObjectLayoutDescriptor layoutDescriptor) {
    List<ComponentConstraint> componentConstraints = new ArrayList<>();
    List<UiActionConstraint> uiActionConstraints = new ArrayList<>();
    if (layoutDescriptor.getConstraints() != null) {

      for (ObjectConstraintDescriptor constraintDescriptor : layoutDescriptor.getConstraints()) {
        ContextObjectData context = constraintDescriptor.getContexts();
        if (context == null) {
          context = new ContextObjectData();
          constraintDescriptor.setContexts(context);
        }
        if (sessionIsPresent()) {
          context.addItemsItem(sessionContext());
        }
        if (userIsPresent()) {
          context.addItemsItem(userContext());
        }
        context.addItemsItem(selfContext(objectUri));

        LangString displayName = null;
        if (test(constraintDescriptor.getPredicates(), context)) {
          componentConstraints.addAll(constraintDescriptor.getComponentConstraints());
          uiActionConstraints.addAll(constraintDescriptor.getActionConstraints() == null
              ? Collections.emptyList()
              : constraintDescriptor.getActionConstraints());
          if (constraintDescriptor.getDisplayName() != null) {
            displayName = constraintDescriptor.getDisplayName();
          } ;
        }
      }

    }
    Map<String, SmartComponentLayoutDefinition> layoutsByName = layoutDescriptor.getLayouts();
    // TODO override labels by langStrings
    return new ObjectDisplay(layoutsByName, componentConstraints, uiActionConstraints);
  }

  // if at least 1 predicate applies (by returning true), then the test is successful:
  private boolean test(List<InvocationRequestDefinition> predicateDefinitions,
      ContextObjectData context) {
    if (predicateDefinitions == null || predicateDefinitions.isEmpty()) {
      return true;
    }

    for (InvocationRequestDefinition predicateDef : predicateDefinitions) {
      InvocationRequest predicate = invocationApi.resolve(predicateDef, context);
      try {
        InvocationParameter result = invocationApi.invoke(predicate);
        if (Boolean.TRUE.equals(result.getValue())) {
          return true;
        }
      } catch (ApiNotFoundException e) {
        log.error(e.getMessage(), e);
        // no other operation: if we could not invoke the request, then its result is implicitly
        // false.
      }
    }
    return false;
  }

  private boolean sessionIsPresent() {
    return sessionApi != null && sessionApi.getSessionUri() != null;
  }

  private ContextObjectDataItem sessionContext() {
    return new ContextObjectDataItem()
        .name(SESSION_CONTEXT)
        .uri(sessionApi.getSessionUri());
  }

  private boolean userIsPresent() {
    return sessionApi != null && sessionApi.getUserUri() != null;
  }

  private ContextObjectDataItem userContext() {
    return new ContextObjectDataItem()
        .name(USER_CONTEXT)
        .uri(sessionApi.getUserUri());
  }

  private ContextObjectDataItem selfContext(URI objectUri) {
    return new ContextObjectDataItem()
        .name(THIS_CONTEXT)
        .uri(objectUri);
  }

  @Override
  public LayoutDefinitionDescriptor getLayoutDefinitionDescriptor(String jsonString) {
    if (ObjectUtils.isEmpty(jsonString)) {
      return null;
    }
    ObjectReader reader = objectMapper.readerFor(LayoutDefinitionDescriptor.class);
    try {
      return reader.readValue(jsonString);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(localeSettingApi.get("exception.objectMapper.reader"));
    }
  }

  @Override
  public ObjectDisplay getSketchDisplay(ObjectNode objectNode, ObjectLayoutDescriptor descriptor) {
    return new ObjectDisplay(descriptor.getLayouts(), new ArrayList<>(), new ArrayList<>());
  }

  @Override
  public SmartComponentLayoutDefinition constructSimpleLayout(Map<String, Object> o) {
    return constructSimpleLayout(o, LayoutDirection.HORIZONTAL);
  }

  @Override
  public SmartComponentLayoutDefinition constructSimpleLayout(Map<String, Object> o,
      LayoutDirection orientation) {
    final var primitives = flatten(o).toList();
    final var formWidgets = primitives.stream()
        .map(it -> suggestWidget(String.join(".", it.keySegments), it.value))
        .flatMap(Optional::stream)
        .toArray(SmartWidgetDefinition[]::new);
    return form(orientation, formWidgets);
  }

  private record ObjectMapValue(String[] keySegments, Object value) {

    String[] append(String segment) {
      String[] res = new String[keySegments.length + 1];
      System.arraycopy(keySegments, 0, res, 0, keySegments.length);
      res[res.length - 1] = segment;
      return res;
    }

  }

  private Stream<ObjectMapValue> flatten(Map<String, Object> o) {
    return o.entrySet().stream()
        .map(e -> new ObjectMapValue(new String[] {e.getKey()}, e.getValue()))
        .flatMap(this::flatten);
  }

  private Stream<ObjectMapValue> flatten(ObjectMapValue v) {
    final var o = v.value;
    if (o instanceof Map<?, ?>) {
      @SuppressWarnings({"unchecked"})
      final Map<String, Object> m = (Map<String, Object>) o;
      return m.entrySet().stream()
          .map(e -> new ObjectMapValue(v.append(e.getKey()), e.getValue()))
          .flatMap(this::flatten);
    } else if (o == null || o instanceof Collection<?>) {
      return Stream.empty();
    }

    return Stream.of(v);
  }

  private Optional<SmartWidgetDefinition> suggestWidget(String key, Object o) {
    if (o instanceof String) {
      return Optional.of(textfield(key, localeSettingApi.get(key)));
    }

    if (o instanceof Boolean) {
      return Optional.of(toggle(key, localeSettingApi.get(key)));
    }

    if (o instanceof LocalDate) {
      return Optional.of(datePicker(key, localeSettingApi.get(key)));
    }

    if (o instanceof LocalDateTime || o instanceof OffsetDateTime) {
      return Optional.of(datetimePicker(key, localeSettingApi.get(key)));
    }

    if (Set.of(Short.class, Integer.class, Long.class, short.class, int.class,
        long.class).stream().anyMatch(it -> it.isInstance(o))) {
      return Optional.of(integralNumberField(key, localeSettingApi.get(key)));
    }

    if (Set.of(Float.class, Double.class, float.class, double.class).stream()
        .anyMatch(it -> it.isInstance(o))) {
      return Optional.of(floatingPointNumberField(key, localeSettingApi.get(key)));
    }

    return Optional.empty();
  }

}
