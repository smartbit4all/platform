package org.smartbit4all.core.object;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.object.bean.ObjectPropertyFormatter;
import org.smartbit4all.api.object.bean.ObjectPropertyResolverContext;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.lang.NonNull;
import com.google.common.base.Strings;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * The object property resolver is the central logic that can help to access the values in an
 * application logic. To perform resolution we have to define the context that is a bunch of named
 * object uri that can be referred during the resolution. The great advantage of this approach that
 * we can use a standard URI format to point to every property. This object is like a session is a
 * stateful object that remembers the already loaded part of the object graph to avoid repetitive
 * reload of the objects for every resolve call. But be careful it cannot be refreshed so if we make
 * changes on the original object then this will resolve the previous values until we create a new
 * resolver via the {@link ObjectApi#resolver()}.
 * 
 * @author Peter Boros
 */
public final class ObjectPropertyResolver {

  private static final Logger log = LoggerFactory.getLogger(ObjectPropertyResolver.class);

  private WeakReference<ObjectApi> objectApiRef;

  ObjectPropertyResolver(ObjectApi objectApi) {
    super();
    this.objectApiRef = new WeakReference<>(objectApi);
  }

  private ObjectApi objectApi() {
    return objectApiRef.get();
  }

  public static final class PropertyPath {

    private static final String PATH_REGEX = "^(?:([^#?]+))?(?:\\#([^#?]*))?(?:\\?([^?]*))?$";
    private static final Pattern PATH_PTRN = Pattern.compile(PATH_REGEX);

    public final String original;
    /** The leading part of the property path, representing a chain of <b>ref</b>s. */
    public final String route;
    /** The second part of the property path, representing a chain of inline fields. */
    public final String field;
    /**
     * The final section.
     *
     * <p>
     * Denotes the conversion algorithm to be used to transform the object found at the property
     * path into the desired format.
     */
    public final String strat;

    private PropertyPath(String original, String route, String field, String strat) {
      this.original = original;
      this.route = route;
      this.field = field;
      this.strat = strat;
    }

    public static PropertyPath parse(String s) {
      Matcher m = PATH_PTRN.matcher(s);
      if (m.matches()) {
        return new PropertyPath(s, m.group(1), m.group(2), m.group(3));
      }
      throw new IllegalArgumentException("Cannot parse as PropertyPath: " + s);
    }

  }

  public static final String REDIRECT_REGEX =
      "^redirect\\{if=\"([^\"]*)\",\\s*then=\"([^\"]*)\"}$";
  public static final Pattern REDIRECT_PTRN = Pattern.compile(REDIRECT_REGEX);

  /**
   * The context object inner structure that contains the name, uri and the loaded object node if it
   * was already referred.
   * 
   * @author Peter Boros
   */
  private class ContextObject {

    String name;

    URI uri;

    ObjectNode loadedObjectNode;

    ContextObject(String name, URI uri) {
      super();
      this.name = name;
      this.uri = uri;
    }

    ObjectNode objectNode() {
      if (loadedObjectNode == null) {
        loadedObjectNode = objectApi().load(uri);
      }
      return loadedObjectNode;
    }

  }

  private final Map<String, ContextObject> contextObjects = new HashMap<>();

  /**
   * We can add context objects to the given resolver. The name uris points to the root objects of
   * the resolution. Can be referred by the scheme of the property uris. like in the following uri
   * <b>SomeObject</b>:/ref1/ref2#property1
   * 
   * @param context
   * @return
   */
  public ObjectPropertyResolver addContextObjects(Map<String, URI> context) {
    if (context != null) {
      context.entrySet().stream().map(e -> new ContextObject(e.getKey(), e.getValue()))
          .forEach(co -> contextObjects.putIfAbsent(co.name, co));
    }
    return this;
  }

  /**
   * We can add context objects to the given resolver. The name uris points to the root objects of
   * the resolution. Can be referred by the scheme of the property uris. like in the following uri
   * <b>SomeObject</b>:/ref1/ref2#property1
   * 
   * @param context
   * @return
   */
  public ObjectPropertyResolver addContextObjects(ObjectPropertyResolverContext context) {
    if (context != null) {
      context.getObjects().stream().map(o -> new ContextObject(o.getName(), o.getUri()))
          .forEach(co -> contextObjects.putIfAbsent(co.name, co));
    }
    return this;
  }

  /**
   * We can add context objects to the given resolver. The name uris points to the root objects of
   * the resolution. Can be referred by the scheme of the property uris. like in the following uri
   * <b>SomeObject</b>:/ref1/ref2#property1
   * 
   * @param name The name of the context object.
   * @param objectUri The uri of the context object.
   * @return
   */
  public ObjectPropertyResolver addContextObject(String name, URI objectUri) {
    if (name != null && objectUri != null) {
      contextObjects.putIfAbsent(name, new ContextObject(name, objectUri));
    }
    return this;
  }

  /**
   * The core functionality of the resolver. We can give a list of URI that points to propreties
   * accessible from the context objects.
   * 
   * @param properties
   * @param language The language code that can be used to replace in the language related part of
   *        the uri.
   * @return The resolved values mapped by the URI of the property.
   */
  public Map<URI, Object> resolve(List<URI> properties, String language) {
    if (properties == null || properties.isEmpty()) {
      return Collections.emptyMap();
    }
    return properties.stream().collect(toMap(u -> u, u -> performResolution(u, language)));
  }

  /**
   * The core functionality of the resolver. We can give a list of URI that points to propreties
   * accessible from the context objects.
   * 
   * @param properties
   * @return The resolved values mapped by the URI of the property.
   */
  public Map<URI, Object> resolve(List<URI> properties) {
    return resolve(properties, null);
  }

  /**
   * The core functionality of the resolver. We can give a list of URI that points to propreties
   * accessible from the context objects.
   * 
   * @param property
   * @param language The language code that can be used to replace in the language related part of
   *        the uri.
   * @return The resolved values mapped by the URI of the property.
   */
  public Object resolve(URI property, String language) {
    if (property == null) {
      return null;
    }
    return performResolution(property, language);
  }

  /**
   * The core functionality of the resolver. We can give a list of URI that points to propreties
   * accessible from the context objects.
   * 
   * @param property
   * @return The resolved values mapped by the URI of the property.
   */
  public Object resolve(URI property) {
    return resolve(property, null);
  }

  private Object performResolution(URI propertyUri, String language) {
    if (propertyUri == null) {
      return null;
    }
    ContextObject contextObject = contextObjects.get(propertyUri.getScheme());
    if (contextObject == null) {
      throw new IllegalArgumentException(
          "Unable to resolve the " + propertyUri + " property because the "
              + propertyUri.getScheme() + " object is not defined in the context.");
    }
    String propertyUriString = propertyUri.toString();
    return performResolution(contextObject.objectNode(),
        PropertyPath.parse(propertyUriString.substring(propertyUriString.indexOf('/'))),
        language);
  }

  private Object performResolution(@NonNull final ObjectNode objectNode,
      final PropertyPath propertyPath, final String language) {
    final ObjectNode targetNode;
    if (propertyPath.route.equals(StringConstant.SLASH)) {
      // we are on the target node.
      targetNode = objectNode;
    } else {
      // we navigate through the references until we hit the target node
      String[] routeParts = propertyPath.route.split(StringConstant.SLASH);
      // omit the empty first element referring to the root node itself:
      routeParts = Arrays.copyOfRange(routeParts, 1, routeParts.length);
      fillLanguagePlaceholder(routeParts, language);
      targetNode = objectNode.ref(routeParts).get();
    }

    if (targetNode == null) {
      return null;
    }

    // read the acquired node's property:
    final Object foundValue;
    if (Strings.isNullOrEmpty(propertyPath.field)) {
      // the thing we are looking for is the target node:
      foundValue = targetNode.getObject();
    } else {
      // the thing we are looking for is an inline sub-element of the target node:
      final String[] fieldParts = propertyPath.field.split(StringConstant.SLASH);
      fillLanguagePlaceholder(fieldParts, language);
      foundValue = targetNode.getValue(fieldParts);
    }

    if (propertyPath.strat == null || !propertyPath.strat.startsWith("redirect")) {
      return foundValue;
    }
    // Redirection is a special resolution strategy: if the resolved value matches the contents of
    // the "if" clause, then we resolve the value found in the "then" path:
    final Matcher redirectMatcher =
        ObjectPropertyResolver.REDIRECT_PTRN.matcher(propertyPath.strat);
    if (!redirectMatcher.matches()) {
      log.warn("[ {} ] does not match redirect regex, could not redirect!", propertyPath.strat);
      return foundValue;
    }
    final String forbiddenValue = redirectMatcher.group(1);
    if (!forbiddenValue.equals(foundValue.toString())) {
      return foundValue;
    }
    PropertyPath alternativePath = PropertyPath.parse(redirectMatcher.group(2));
    return performResolution(objectNode, alternativePath, language);
  }

  private static void fillLanguagePlaceholder(String[] parts, String language) {
    if (language != null) {
      for (int i = 0; i < parts.length; i++) {
        if (parts[i].equals("[lang]")) {
          parts[i] = language;
        }
      }
    }
  }

  public ObjectNode getContextObjectNode(String objectName) {
    ContextObject contextObject = contextObjects.get(objectName);
    return contextObject != null ? contextObject.objectNode() : null;
  }

  /**
   * This special resolver accept a formatter that contains the message format string (currently in
   * the format of the Java {@link MessageFormat}). It also contains the parameter definition also.
   * 
   * @param formatter The formatter.
   * @return The result of the format with all the parameters resolved by the context.
   */
  public String resolve(ObjectPropertyFormatter formatter) {
    if (formatter == null) {
      return StringConstant.EMPTY;
    }
    List<Object> properties =
        formatter.getParameters().stream().map(p -> resolve(p.getPropertyUri())).collect(toList());
    return MessageFormat.format(formatter.getFormatString(), properties.toArray());
  }

}
