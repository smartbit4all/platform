package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.mdm.bean.MDMRelationDefinition;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.storage.bean.ObjectAspect;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectNodeAspects;
import org.springframework.beans.factory.annotation.Autowired;

public class MDMRelationApiImpl implements MDMRelationApi {

  private static final Logger log = LoggerFactory.getLogger(MDMRelationApiImpl.class);

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MasterDataManagementApi masterDataManagementApi;

  @Override
  public List<MDMRelationDefinition> getManagedRelations(
      final String definition,
      final String entryName) {
    Objects.requireNonNull(definition, "MDM Definition name cannot be null!");
    Objects.requireNonNull(entryName, "MDM Entry name cannot be null!");
    final MDMEntryApi relations = masterDataManagementApi.getApi(
        MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        MDM_RELATIONS);
    return relations.getList().nodes()
        .map(it -> it.getObject(MDMRelationDefinition.class))
        .filter(it -> Objects.equals(definition, it.getFromDefinition())
            && Objects.equals(entryName, it.getFromEntryName()))
        .sorted(Comparator.comparing(MDMRelationDefinition::getCode))
        .toList();
  }

  @Override
  public void setRelations(
      final ObjectNode host,
      final String relation,
      final Collection<? extends URI> relatedObjects) {
    Objects.requireNonNull(relation, "Relation code cannot be null!");
    setRelations(
        host,
        fetchRelationDefinition(relation),
        relatedObjects == null
            ? new ArrayList<>()
            : relatedObjects);
  }

  @Override
  public void setRelations(
      final ObjectNode host,
      final MDMRelationDefinition relationDefinition,
      final Collection<? extends URI> relatedObjects) {
    Objects.requireNonNull(host, "Host objectnode cannot be null!");
    Objects.requireNonNull(relationDefinition, "Relation Definition cannot be null!");
    Objects.requireNonNull(relatedObjects, "Related Object collection cannot be null!");

    if (ReferencePropertyKind.REFERENCE == relationDefinition.getPropertyKind()
        && relatedObjects.size() > 1) {
      throw new IllegalArgumentException("Relation " + relationDefinition
          + " supports at most 1 related object! (Provided: " + relatedObjects.size() + ")");
    }

    final Map<String, Object> relationMap = getRelationMap(host);
    relationMap.put(relationDefinition.getCode(), new ArrayList<>(relatedObjects));
  }

  private Map<String, Object> getRelationMap(final ObjectNode host) {
    final ObjectNodeAspects aspects = host.aspects();
    Map<String, ObjectAspect> aspectMap = aspects.get();
    if (aspectMap == null) {
      host.getData().setAspects(new HashMap<>());
      aspectMap = host.aspects().get();
    }

    return aspectMap
        .computeIfAbsent(
            ASPECT_NAME,
            k -> new ObjectAspect().objectAsMap(new HashMap<>()))
        .getObjectAsMap();
  }

  @Override
  public RelatedObjectHolder getRelations(final ObjectNode host, final String relation) {
    Objects.requireNonNull(host, "Host objectnode cannot be null!");

    return getRelations(host, fetchRelationDefinition(relation));
  }

  private MDMRelationDefinition fetchRelationDefinition(final String relation) {
    final MDMEntryApi relations = masterDataManagementApi.getApi(
        MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION,
        MDM_RELATIONS);
    return relations
        .getList().nodes()
        .filter(it -> Objects.equals(relation, it.getValueAsString(MDMRelationDefinition.CODE)))
        .findFirst()
        .map(it -> it.getObject(MDMRelationDefinition.class))
        .orElseThrow();
    /*
     * .lookup() .findByUnique( new ObjectPropertyValue() .value(relation)
     * .addPathItem(MDMRelationDefinition.CODE), MDMRelationDefinition.class);
     */
  }

  @Override
  public RelatedObjectHolder getRelations(
      final ObjectNode host,
      final MDMRelationDefinition relationDefinition) {
    Objects.requireNonNull(host, "Host objectnode cannot be null!");
    Objects.requireNonNull(relationDefinition, "Relation Definition cannot be null!");

    final Map<String, Object> relationMap = getRelationMap(host);
    final Object relationsObj = relationMap.get(relationDefinition.getCode());
    if (relationsObj instanceof List<?> relationObjects) {
      final List<URI> relationObjectUris = objectApi.asList(URI.class, relationObjects);
      return RelatedObjectHolder.of(relationDefinition, relationObjectUris);
    } else {
      return RelatedObjectHolder.of(relationDefinition, null);
    }
  }

}
