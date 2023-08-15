package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ObjectConstraintDescriptor;
import org.smartbit4all.api.object.bean.ObjectExtensionDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContextObject;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiActionConstraint;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectExtensionApiImpl implements ObjectExtensionApi {

  private static final String MAP = "extension-descriptors";
  private static final Logger log = LoggerFactory.getLogger(ObjectExtensionApiImpl.class);

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private CollectionApi collectionApi;
  @Autowired
  private InvocationApi invocationApi;
  @Autowired(required = false)
  private SessionApi sessionApi;

  @Override
  public Stream<ObjectNode> findAllObjectExtensionDescriptors() {
    return collectionApi.map(SCHEMA, MAP).uris().values().stream().map(objectApi::loadLatest);
  }

  @Override
  public Optional<ObjectNode> findObjectExtensionDescriptorByName(String name) {
    return Optional
        .ofNullable(collectionApi
            .map(SCHEMA, MAP)
            .uris()
            .get(name))
        .map(objectApi::loadLatest);
  }

  @Override
  public ObjectExtensionBuilder create() {
    return new ObjectExtensionBuilder(this, objectApi);
  }

  @Override
  public ObjectExtensionBuilder update(String extensionName) {
    return findObjectExtensionDescriptorByName(extensionName)
        .map(n -> n.getObject(ObjectExtensionDescriptor.class))
        .map(descriptor -> new ObjectExtensionBuilder(this, objectApi, descriptor))
        .orElseGet(() -> create().name(extensionName));
  }

  @Override
  public Map<String, List<ObjectPropertyDescriptor>> getPropertiesByOwnerTypes(
      String extensionName) {
    return findObjectExtensionDescriptorByName(extensionName)
        .map(n -> n.list(ObjectExtensionDescriptor.PROPERTIES).nodeStream())
        .orElse(Stream.empty())
        .map(p -> p.getObject(ObjectPropertyDescriptor.class))
        .collect(Collectors.groupingBy(desc -> desc.getObjectQualifiedName()));
  }

  @Override
  public URI saveNewExtensionDescriptor(ObjectExtensionDescriptor descriptor) {
    URI uri = objectApi.saveAsNew(SCHEMA, descriptor);
    collectionApi.map(SCHEMA, MAP).put(descriptor.getName(), objectApi.getLatestUri(uri));
    return uri;
  }

  @Override
  public ObjectDisplay getObjectDisplay(URI objectUri, String extensionName) {
    final ObjectExtensionDescriptor extensionDescriptor =
        findObjectExtensionDescriptorByName(extensionName)
            .map(n -> n.getObject(ObjectExtensionDescriptor.class))
            .orElseThrow(() -> new IllegalArgumentException("asd"));

    List<ComponentConstraint> componentConstraints = new ArrayList<>();
    List<UiActionConstraint> uiActionConstraints = new ArrayList<>();
    for (ObjectConstraintDescriptor constraintDescriptor : extensionDescriptor.getConstraints()) {
      ObjectPropertyResolverContext context = constraintDescriptor.getContexts();
      if (sessionIsPresent()) {
        context.addObjectsItem(sessionContext());
      }
      if (userIsPresent()) {
        context.addObjectsItem(userContext());
      }
      context.addObjectsItem(selfContext(objectUri));

      if (test(constraintDescriptor.getPredicates(), context)) {
        componentConstraints.addAll(constraintDescriptor.getComponentConstraints());
        uiActionConstraints.addAll(constraintDescriptor.getActionConstraints() == null
            ? Collections.emptyList()
            : constraintDescriptor.getActionConstraints());
      }
    }
    Map<String, SmartComponentLayoutDefinition> layoutsByName = extensionDescriptor.getLayouts();
    return new ObjectDisplay(layoutsByName, componentConstraints, uiActionConstraints);
  }

  // if at least 1 predicate applies (by returning true), then the test is successful:
  private boolean test(List<InvocationRequestDefinition> predicateDefinitions,
      ObjectPropertyResolverContext context) {
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

  private ObjectPropertyResolverContextObject sessionContext() {
    return new ObjectPropertyResolverContextObject()
        .name(SESSION_CONTEXT)
        .uri(sessionApi.getSessionUri());
  }

  private boolean userIsPresent() {
    return sessionApi != null && sessionApi.getUserUri() != null;
  }

  private ObjectPropertyResolverContextObject userContext() {
    return new ObjectPropertyResolverContextObject()
        .name(USER_CONTEXT)
        .uri(sessionApi.getUserUri());
  }

  private ObjectPropertyResolverContextObject selfContext(URI objectUri) {
    return new ObjectPropertyResolverContextObject()
        .name(THIS_CONTEXT)
        .uri(objectUri);
  }

}
