package org.smartbit4all.core.object;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PersistableObject;
import org.smartbit4all.api.object.bean.ReferenceDefinitionData;

/**
 * Collects and handles object definitions for API objects.
 * 
 * @author matea
 *
 */
public interface ObjectDefinitionApi {

  /**
   * The set contains the types (classes) of the properties that must be assumed as value and not as
   * embedded object. This is the default set that is used if it is not set manually.
   */
  Set<Class<?>> defaultAsValueClasses = Set.of(BigDecimal.class, Boolean.class, Date.class,
      java.sql.Date.class, Double.class, Integer.class, LocalDate.class, LocalDateTime.class,
      LocalTime.class, OffsetDateTime.class, Long.class, String.class, URI.class, UUID.class);

  /**
   * Check if the given object is a value object that shouldn't be converted to a map or used as
   * Bean.
   * 
   * @param o
   * @return
   */
  boolean isValue(Object o);

  /**
   * Get the definition for the given Class.
   * 
   * @param <T> The type of the class
   * @param clazz The class of the domain object (Java bean)
   * @return The definition of the given class.
   */
  <T> ObjectDefinition<T> definition(Class<T> clazz);

  /**
   * The object api tries to identify the {@link ObjectDefinition} based on the standard format of
   * the URI. The first part of the URI contains the alias of the {@link ObjectDefinition}.
   * 
   * @param objectUri The object URI that must match the standard.
   * @return The {@link ObjectDefinition} if it was identified or null if it was not found.
   */
  ObjectDefinition<?> definition(URI objectUri);

  /**
   * The fully qualified name of the object class tries to identify the {@link ObjectDefinition}.
   * 
   * @param className The object class name.
   * @return The {@link ObjectDefinition} if it was identified by the existing source. If it is not
   *         found then we try to read the {@link ObjectDefinitionData} from the storage or creates
   *         a new definition it is not found. The newly created definition can be extended by the
   *         ObjectDefinitionBuilder that will save the definition into the storage.
   */
  ObjectDefinition<?> definition(String className);


  ObjectDefinition<PersistableObject> baseDefinition(String className);

  /**
   * @return The default serializer for the objects.
   */
  ObjectSerializer getDefaultSerializer();

  void reloadDefinitionData(ObjectDefinition<?> definition);

  void saveDefinitionData(ObjectDefinition<?> definition);

  /**
   * Add a new reference to the object definitions. It can be used when the application is already
   * started, do not use till the end of the initialization.
   * 
   * @param referenceDefinitionData
   */
  void addReference(ReferenceDefinitionData referenceDefinitionData);

  /**
   * Walks until the end of the path end returns the type class of the property found there.
   * 
   * @param definition
   * @param defaultType
   * @param path
   * @return If not found then the String.class is returned.
   */
  Class<?> getTypeOfProperty(ObjectDefinition<?> definition, Class<?> defaultType, String... path);

  /**
   * Can be used to convert any Java object to a Map<String, Object>.
   *
   * @param o The object to convert
   * @return The result Map. Typical JSON like mapping. Returns null if the object is null. If the
   *         object is null, then an empty map will be returned.
   */
  Map<String, Object> toMapObject(Object o);

}
