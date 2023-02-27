package org.smartbit4all.api.collection;

/**
 * Represents a contextual extension performed on a {@link SearchIndexMappingObject}.
 * 
 * <p>
 * When defining a {@link SearchIndexImpl}, its
 * {@link SearchIndexImpl#extendMapping(SearchIndexMappingExtensionStrategy)} accepts an instance of
 * this type, preferable as a lambda expression. The implemented
 * {@link #extend(SearchIndexMappingObject)} does not spur the SearchIndex mapping object to reload
 * its definition, unless the method returns {@code true}.
 * 
 * @author Szabolcs Bazil Papp
 *
 */
@FunctionalInterface
public interface SearchIndexMappingExtensionStrategy {

  boolean extend(SearchIndexMappingObject mappingObject);

}
