package org.smartbit4all.api.navigation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.api.navigation.bean.NavigationView;
import org.smartbit4all.core.object.BeanMeta;
import org.smartbit4all.core.object.BeanMetaUtil;
import org.smartbit4all.core.object.DomainObjectRef;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectChange;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.PropertyMeta;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This object navigation is based on the {@link ObjectDefinition} and the {@link ObjectApi} meta
 * data.
 * 
 * TODO We must figure out how to extend the parameterization.
 * 
 * @author Peter Boros
 *
 */
public class ObjectNavigation extends NavigationImpl {

  @Autowired
  ObjectApi objectApi;

  @Autowired
  StorageApi storageApi;

  public static final String OBJECT_NAVIGATION = "object";

  public ObjectNavigation() {
    super(OBJECT_NAVIGATION);
  }

  /**
   * The association meta mapped by the uri of the meta. Constructed on demand.
   */
  private final Map<URI, ObjectNavigationReference> associationMetas = new ConcurrentHashMap<>();

  /**
   * The entry meta mapped by the uri of the meta.
   */
  private final Map<String, NavigationEntryMeta> entryMetas = new ConcurrentHashMap<>();

  /**
   * The {@link BeanMeta} object cached for the classes.
   */
  private final Map<Class<?>, BeanMeta> classMetas = new ConcurrentHashMap<>();

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris) {
    if (associationMetaUris == null || associationMetaUris.isEmpty()) {
      return Collections.emptyMap();
    }
    ObjectDefinition<?> objectDefinition = objectApi.definition(objectUri);
    if (objectDefinition == null) {
      return Collections.emptyMap();
    }
    NavigationEntryMeta sourceMeta =
        entryMetas.computeIfAbsent(objectDefinition.getQualifiedName(), name -> {
          return createNavigationEntryMeta(name);
        });
    return associationMetaUris.parallelStream().map(uri -> {
      ObjectNavigationReference objectNavigationReference =
          associationMetas.computeIfAbsent(uri, u -> {
            ReferenceDefinition referenceDefinition =
                objectDefinition.getOutgoingReferences().get(uri.getPath());
            if (referenceDefinition == null) {
              return null;
            }
            return new ObjectNavigationReference(
                new NavigationAssociationMeta().startEntry(sourceMeta),
                referenceDefinition);
          });
      List<NavigationReferenceEntry> result =
          navigateWithStorageApi(objectUri, objectNavigationReference);
      return new NavigationAssocResult(uri, result);
    }).collect(Collectors.toMap(r -> r.uri, r -> r.list));
  }

  /**
   * Creates a navigation entry meta based on the definition of the object denoted by the specified
   * URI.
   * 
   * @param objectUri an {@code URI}
   * @return a {@code NavigationEntryMeta} with its URI based on the provided object URI.
   */
  public NavigationEntryMeta createNavigationEntryMeta(URI objectUri) {
    ObjectDefinition<?> definition = objectApi.definition(objectUri);
    return createNavigationEntryMeta(definition.getQualifiedName());
  }

  private NavigationEntryMeta createNavigationEntryMeta(String name) {
    return new NavigationEntryMeta().uri(URI.create(OBJECT_NAVIGATION + StringConstant.COLON
        + StringConstant.SLASH + name.replace(StringConstant.DOT, StringConstant.SLASH)))
        .name(name);
  }

  /**
   * Later on replace with API from the parameterization. Use the StorageApi directly to load the
   * Data.
   * 
   * @param objectUri
   * @param objectNavigationReference
   * @return
   * @deprecated Replace with the proper api call.
   */
  @Deprecated
  private List<NavigationReferenceEntry> navigateWithStorageApi(URI objectUri,
      ObjectNavigationReference objectNavigationReference) {
    List<NavigationReferenceEntry> result = new ArrayList<>();
    {
      StorageObject<?> object = storageApi.load(objectUri);
      BeanMeta beanMeta = classMetas.computeIfAbsent(object.getObject().getClass(),
          c -> BeanMetaUtil.meta(object.getObject().getClass()));
      PropertyMeta sourcePropertyMeta = beanMeta.getProperties()
          .get(objectNavigationReference.referenceDefinition.getSourcePropertyPath());
      if (sourcePropertyMeta != null) {
        Object sourceValue = sourcePropertyMeta.getValue(object.getObject());
        if (sourceValue instanceof URI) {
          // Direct reference to another Object.
          NavigationReferenceEntry referenceEntry = loadEntry(objectUri, sourceValue);
          result.add(referenceEntry);
        } else if (sourceValue instanceof List) {
          // List of references.
          for (URI sourceUri : (List<URI>) sourceValue) {
            NavigationReferenceEntry referenceEntry = loadEntry(objectUri, sourceUri);
            result.add(referenceEntry);
          }
        }
      }
    }
    return result;
  }

  private NavigationReferenceEntry loadEntry(URI objectUri, Object sourceValue) {
    StorageObject<?> targetObject = storageApi.load((URI) sourceValue);
    return Navigation.referenceEntry(
        objectUri,
        objectNavEntry(targetObject, false),
        null);
  }

  private NavigationEntry objectNavEntry(StorageObject<?> object, boolean editing) {
    NavigationEntryMeta meta = new NavigationEntryMeta();
    String viewName = "fake";
    return Navigation.entry(
        meta,
        object.getUri(),
        "title",
        "icon",
        new NavigationView()
            .name(viewName)
            .putParametersItem("EDITING", editing));
  }

  private class NavigationAssocResult {

    URI uri;

    List<NavigationReferenceEntry> list;

    public NavigationAssocResult(URI uri, List<NavigationReferenceEntry> list) {
      super();
      this.uri = uri;
      this.list = list;
    }

  }

  @Override
  public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) {
    NavigationEntryMeta meta = createNavigationEntryMeta(objectUri);
    return Navigation.entry(
        meta,
        objectUri,
        meta.getName(),
        null);
  }

  @Override
  public Optional<DomainObjectRef> loadObject(URI entryMetaUri, URI objectUri) {
    StorageObject<?> targetObject = storageApi.load(objectUri);
    Map<String, Object> map = convertMapToTreeMap(targetObject.getObjectAsMap());
    return Optional.of(new DomainObjectRef() {

      @Override
      public void setValueByPath(String path, Object value) {
        // TODO Auto-generated method stub
      }

      @Override
      public void setObject(Object loadedObject) {
        // TODO Auto-generated method stub
      }

      @Override
      public Optional<ObjectChange> renderAndCleanChanges() {
        // TODO Auto-generated method stub
        return Optional.empty();
      }

      @Override
      public void removeValueByPath(String path) {
        // TODO Auto-generated method stub
      }

      @Override
      public void reevaluateChanges() {
        // TODO Auto-generated method stub
      }

      @Override
      public <T> T getWrapper(Class<T> beanClass) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public DomainObjectRef getValueRefByPath(String path) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Object getValueByPath(String path) {
        if (path != null) {
          String[] pathElements = path.split(StringConstant.SLASH);
          Map<String, Object> currentObjectMap = map;
          for (int i = 0; i < pathElements.length; i++) {
            String element = pathElements[i];
            if (i != pathElements.length - 1) {
              Object object = map.get(element);
              if (object instanceof Map) {
                currentObjectMap = convertMapToTreeMap((Map<String, Object>) object);
              } else {
                return null;
              }
            } else {
              return currentObjectMap.get(element);
            }
          }
        }

        return null;
      }

      @Override
      public Object getValue(String propertyName) {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getPath() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Object getObject() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public DomainObjectRef addValueByPath(String path, Object value) {
        // TODO Auto-generated method stub
        return null;
      }
    });
  }

  private Map<String, Object> convertMapToTreeMap(Map<String, Object> source) {
    Map<String, Object> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    map.putAll(source);
    return map;
  }
}
