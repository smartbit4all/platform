package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMRelationDefinition;
import org.smartbit4all.core.object.ObjectNode;

/**
 * Enables performing low level operations on unidirectional domain object relations described by
 * {@link MDMRelationDefinition}s.
 * 
 * <p>
 * Application code should not use the facilities of this interface directly.
 */
public interface MDMRelationApi {

  /** The name of the MDM Entry Descriptor containing the {@link MDMRelationDefinition}s. */
  String MDM_RELATIONS = "Relations";

  /** The name of the object aspect where related objects are stored. */
  String ASPECT_NAME = "relations";

  /**
   * Fetches all managed relation definitions originating from the specified MDM Entry.
   * 
   * @param definition the {@code String} name of the MDM definition, like
   *        {@link MasterDataManagementApi#MDM_DEFINITION_GLOBAL}, not null
   * @param entryName the {@code String} name of the {@link MDMEntryDescriptor}, not null
   * @return a {@code List} of {@link MDMRelationDefinition}s originating from the denoted entry,
   *         not null
   */
  List<MDMRelationDefinition> getManagedRelations(final String definition, final String entryName);

  /**
   * Ascribes the specified objects as related to the host under the provided relation.
   * 
   * @param host the {@link ObjectNode} representation of an MDM managed domain object
   * @param relation the {@code String} unique code of the relation originating from the host's MDM
   *        Entry; not null, the relation must exist
   * @param relatedObjects a {@code Collection} of object {@link URI}s to be persisted as related,
   *        nullable
   * @see MDMRelationDefinition#getCode()
   * @see MDMRelationDefinition#getFromDefinition()
   * @see MDMRelationDefinition#getFromEntryName()
   */
  void setRelations(
      final ObjectNode host,
      final String relation,
      final Collection<? extends URI> relatedObjects);

  /**
   * Ascribes the specified objects as related to the host under the provided relation.
   * 
   * @param host the {@link ObjectNode} representation of an MDM managed domain object
   * @param relationDefinition the {@link MDMRelationDefinition} originating from the host's MDM
   *        Entry; not null, the relation must exist
   * @param relatedObjects a {@code Collection} of object {@link URI}s to be persisted as related,
   *        not null
   */
  void setRelations(
      final ObjectNode host,
      final MDMRelationDefinition relationDefinition,
      final Collection<? extends URI> relatedObjects);

  sealed interface RelatedObjectHolder {

    static RelatedObjectHolder of(MDMRelationDefinition relationDefinition,
        List<URI> relatedObjects) {
      return switch (relationDefinition.getPropertyKind()) {
        case REFERENCE -> {
          if (relatedObjects == null || relatedObjects.isEmpty()) {
            yield new Singular(null);
          } else {
            yield new Singular(relatedObjects.getFirst());
          }
        }
        case LIST -> new Multiple(relatedObjects == null
            ? Collections.emptyList()
            : relatedObjects);
        case null, default -> throw new IllegalArgumentException(
            "Not supported related object enumeration " + relationDefinition);
      };
    }

    record Singular(URI relatedObject) implements RelatedObjectHolder {
    }

    record Multiple(List<URI> relatedObjects) implements RelatedObjectHolder {
    }

    /**
     * @return the {@link URI}s of the related object(s) wrapped in a {@link List}
     */
    default List<URI> asList() {
      return switch (this) {
        case Singular s -> s.relatedObject() == null
            ? Collections.emptyList()
            : Collections.singletonList(s.relatedObject());
        case Multiple m -> m.relatedObjects();
      };
    }

  }

  /**
   * Fetches the object(s) related to the host under the specified relation definition.
   * 
   * @param host the {@link ObjectNode} representation of an MDM managed domain object, not null
   * @param relation the {@code String} unique code of {@link MDMRelationDefinition} which describes
   *        where the related objects are located in relation to the host, not null
   * @return a {@code RelatedObjectHolder} which either wraps a {@code Singular} related object, or
   *         a {@code Multiple} of them
   */
  RelatedObjectHolder getRelations(final ObjectNode host, final String relation);

  /**
   * Fetches the object(s) related to the host under the specified relation definition.
   * 
   * @param host the {@link ObjectNode} representation of an MDM managed domain object, not null
   * @param relationDefinition the {@link MDMRelationDefinition} which describes where the related
   *        objects are located in relation to the host, not null
   * @return a {@code RelatedObjectHolder} which either wraps a {@code Singular} related object, or
   *         a {@code Multiple} of them
   */
  RelatedObjectHolder getRelations(
      final ObjectNode host,
      final MDMRelationDefinition relationDefinition);

}
