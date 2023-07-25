package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.formdefinition.bean.SmartLayoutDefinition;
import org.smartbit4all.api.invocation.ApiNotFoundException;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.api.invocation.bean.InvocationRequestDefinition;
import org.smartbit4all.api.object.bean.ObjectConstraintDescriptor;
import org.smartbit4all.api.object.bean.ObjectExtensionDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContextObject;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.bean.ComponentConstraint;
import org.smartbit4all.api.view.bean.UiActionConstraint;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Szabolcs Bazil Papp
 *
 */
public class ObjectRepresentationApiImpl implements ObjectRepresentationApi {

  private static final Logger log = LoggerFactory.getLogger(ObjectRepresentationApiImpl.class);

  @Autowired
  private ObjectExtensionApi objectExtensionApi;
  @Autowired
  private InvocationApi invocationApi;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Override
  public ObjectDisplay getObjectDisplay(URI objectUri, String extensionName) {
    final ObjectExtensionDescriptor extensionDescriptor = objectExtensionApi
        .findObjectExtensionDescriptorByName(extensionName)
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
    Map<String, SmartLayoutDefinition> layoutsByName = extensionDescriptor.getLayouts();
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
        .name("session")
        .uri(sessionApi.getSessionUri());
  }

  private boolean userIsPresent() {
    return sessionApi != null && sessionApi.getUserUri() != null;
  }

  private ObjectPropertyResolverContextObject userContext() {
    return new ObjectPropertyResolverContextObject()
        .name("user")
        .uri(sessionApi.getUserUri());
  }

  private ObjectPropertyResolverContextObject selfContext(URI objectUri) {
    return new ObjectPropertyResolverContextObject()
        .name("this")
        .uri(objectUri);
  }

}
