/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.navigation.entity;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.smartbit4all.api.navigation.Navigation;
import org.smartbit4all.api.navigation.NavigationCallBackApi;
import org.smartbit4all.api.navigation.NavigationImpl;
import org.smartbit4all.api.navigation.bean.NavigationAssociationMeta;
import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationEntryMeta;
import org.smartbit4all.api.navigation.bean.NavigationReferenceEntry;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.Reference.Join;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.smartbit4all.domain.utility.crud.Crud;
import org.springframework.stereotype.Service;

@Service
public class EntityNavigation extends NavigationImpl {

  public static final String ENTITYNAV_NAME = EntityUris.SCHEME_ENTITY;

  private EntityManager entityManager;
  private EntityNavigationEntryProvider navigationEntryProvider;

  private Map<URI, NavigationEntryMeta> entryMetasByEntityDefUri = new HashMap<>();
  private Map<URI, Boolean> isAssocsSetForEntryMetaByUris = new HashMap<>();
  private Map<URI, NavigationAssociationMeta> refAssocMetasByEntityReferenceUri = new HashMap<>();
  private Map<URI, NavigationAssociationMeta> detAssocMetasByEntityReferenceUri = new HashMap<>();

  private Map<URI, List<URI>> propUrisToQueryByEntityUri = new HashMap<>();

  private boolean isInitialized = false;

  public EntityNavigation(EntityManager entityManager,
      EntityNavigationEntryProvider navigationEntryProvider) {
    super(ENTITYNAV_NAME);
    this.entityManager = entityManager;
    this.navigationEntryProvider = navigationEntryProvider;

    // can't init here because the entitydef reflections are not ready yet...
    // init(entityManager.allDefinitions());
  }

  private void init(List<EntityDefinition> entityDefs) {
    for (EntityDefinition entityDef : entityDefs) {
      URI entityDefUri = entityDef.getUri();
      NavigationEntryMeta navigationEntryMeta = entryMetasByEntityDefUri.get(entityDefUri);
      if (!isEntryMetaAssocsSetUp(entityDefUri)) {

        if (navigationEntryMeta == null) {
          navigationEntryMeta = createEntryMeta(entityDef);
        }
        createAssocMetas(navigationEntryMeta, entityDef);
        isAssocsSetForEntryMetaByUris.put(entityDefUri, Boolean.TRUE);
      }
    }
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris) {
    Map<URI, List<NavigationReferenceEntry>> naviResult = new HashMap<>();
    String objectEntityName = EntityUris.getEntityName(objectUri);
    EntityDefinition entityDef = entityManager.definition(objectUri);
    DataRow objectData = queryObjectData(objectUri, entityDef);

    if (objectData == null) { // TODO remove when non id query works
      return naviResult;
    }
    for (URI assocMetaUri : associationMetaUris) {
      List<NavigationReferenceEntry> resultNaviReferenceEntries = new ArrayList<>();
      String refSourceEntityName = EntityUris.getEntityName(assocMetaUri);

      if (objectEntityName.equals(refSourceEntityName)) {
        // it's a ref
        NavigationAssociationMeta assocMeta = refAssocMetasByEntityReferenceUri.get(assocMetaUri);
        Reference<?, ?> reference = entityManager.reference(assocMetaUri);
        Property<?> fkProperty = reference.joins().get(0).getSourceProperty(); // TODO multiple
                                                                               // joins
        String id = objectData.get(fkProperty).toString();
        URI entryObjectUri = EntityUris.createEntityObjectUri(reference.getTarget().getUri(), id);
        NavigationReferenceEntry referenceEntry =
            createReferenceEntry(objectUri, assocMeta, entryObjectUri);
        resultNaviReferenceEntries.add(referenceEntry);

      } else {
        // it's a detail
        NavigationAssociationMeta assocMeta = detAssocMetasByEntityReferenceUri.get(assocMetaUri);
        Reference<?, ?> reference = entityManager.reference(assocMetaUri);
        Join<?> join = reference.joins().get(0); // TODO multiple joins
        Property<Object> fkProperty = (Property<Object>) join.getSourceProperty();
        Property<?> targetProperty = join.getTargetProperty();
        EntityDefinition detEntityDef = reference.getSource();
        Object fkValue = objectData.get(targetProperty);
        Map<String, Property<?>> detPkPropsByName =
            getPropertiesByName(detEntityDef.PRIMARYKEYDEF());


        List<DataRow> detailRows = queryDetailRows(detEntityDef, fkProperty, fkValue);
        for (DataRow row : detailRows) {
          if (detPkPropsByName.size() == 1) {
            Property<?> pkProp = detPkPropsByName.values().stream().findFirst().get();
            Object pkValue = row.get(pkProp);

            URI entryObjectUri =
                EntityUris.createEntityObjectUri(detEntityDef.getUri(), pkValue.toString());
            NavigationReferenceEntry referenceEntry =
                createReferenceEntry(objectUri, assocMeta, entryObjectUri);
            resultNaviReferenceEntries.add(referenceEntry);
          } else {
            // handle multiple keys
          }

        }
      }
      naviResult.put(assocMetaUri, resultNaviReferenceEntries);

    }
    return naviResult;
  }

  private NavigationReferenceEntry createReferenceEntry(URI objectUri,
      NavigationAssociationMeta assocMeta,
      URI entryObjectUri) {
    NavigationEntry entry = navigationEntryProvider.getEntry(entryObjectUri);
    entry.setMetaUri(assocMeta.getEndEntry().getUri());
    NavigationReferenceEntry referenceEntry = Navigation.referenceEntry(objectUri, entry, null);
    return referenceEntry;
  }

  private Map<String, Property<?>> getPropertiesByName(PropertySet propSet) {
    return propSet.stream().collect(Collectors.toMap(p -> p.getName(), p -> p));
  }

  private List<DataRow> queryDetailRows(EntityDefinition detEntityDef, Property<Object> fkProperty,
      Object fkValue) {
    try {
      return Crud.read(detEntityDef).select(detEntityDef.PRIMARYKEYDEF())
          .where(fkProperty.eq(fkValue)).executeIntoTableData().rows();
    } catch (Exception e) {
      throw new RuntimeException("Unable to query detail data", e);
    }
  }

  private DataRow queryObjectData(URI objectUri, EntityDefinition entityDef) {
    List<URI> propUrisToQuery = propUrisToQueryByEntityUri.get(entityDef.getUri());
    if (propUrisToQuery == null || propUrisToQuery.isEmpty()) {
      return null;
    }
    List<Property<?>> propsToQuery = propUrisToQuery
        .stream()
        .map(entityManager::property)
        .collect(Collectors.toList());
    Property<Object> idProp =
        (Property<Object>) entityDef.PRIMARYKEYDEF().stream().findFirst().get(); // TODO props
                                                                                 // should come from
                                                                                 // uri

    String objectId = EntityUris.getEntityObjectId(objectUri);
    // TODO get the proper type of the property and convert from string objectId
    Object id = null;
    try {
      id = Long.valueOf(objectId);
    } catch (NumberFormatException e) {
      id = objectId;
    }
    DataRow objectData;
    try {
      objectData = Crud.read(entityDef)
          .select(propsToQuery)
          .where(idProp.eq(id))
          .onlyOne()
          // .orElseThrow(() -> new RuntimeException("No data for objectUri: " + objectUri));
          .orElse(null);
    } catch (Exception e) {
      throw new RuntimeException("Unable to query data for entity with objectUri: " + objectUri, e);
    }
    return objectData;
  }

  @Override
  public NavigationEntry getEntry(URI entryMetaUri, URI objectUri) {
    // not a good place to init..
    initIfNeeded();

    NavigationEntryMeta entryMeta = getEntryMetaWithCheck(entryMetaUri);
    NavigationEntry entry = navigationEntryProvider.getEntry(objectUri);
    entry.setMetaUri(entryMetaUri);
    return entry;
  }

  private void initIfNeeded() {
    if (!isInitialized) {
      init(entityManager.allDefinitions());
      isInitialized = true;
    }
  }

  private NavigationEntryMeta getEntryMetaWithCheck(URI entryMetaUri) {
    NavigationEntryMeta navigationEntryMeta = getEntryMeta(entryMetaUri);
    if (navigationEntryMeta == null) {
      throw new RuntimeException("There is no NavigatinEntryMeta for uri: " + entryMetaUri);
    }
    return navigationEntryMeta;
  }

  private NavigationEntryMeta createEntryMeta(EntityDefinition entityDef) {
    URI entryMetaUri = entityDef.getUri();
    String entityName = entityDef.entityDefName();
    NavigationEntryMeta navigationEntryMeta = Navigation.entryMeta(entryMetaUri, entityName);
    entryMetasByEntityDefUri.put(entryMetaUri, navigationEntryMeta);
    return navigationEntryMeta;
  }

  private boolean isEntryMetaAssocsSetUp(URI entryMetaUri) {
    Boolean isSetUp = isAssocsSetForEntryMetaByUris.get(entryMetaUri);
    return Boolean.TRUE.equals(isSetUp);
  }

  private void createAssocMetas(NavigationEntryMeta entryMeta, EntityDefinition entityDef) {
    List<Reference<?, ?>> references = entityDef.allReferences();
    for (Reference<?, ?> reference : references) {
      URI referenceUri = reference.getUri();
      String referenceName = reference.getName();
      EntityDefinition endEntity = reference.getTarget();

      NavigationEntryMeta endEntryMeta = entryMetasByEntityDefUri.get(endEntity.getUri());
      if (endEntryMeta == null) {
        endEntryMeta = createEntryMeta(endEntity);
      }

      // the main difference for the detAssoc is that the assocMeta's start entity is different from
      // the reference uri's start
      createRefAssociation(entryMeta, referenceUri, referenceName, endEntryMeta);
      createDetAssociation(entryMeta, referenceUri, referenceName, endEntryMeta);

      storeQueryProperties(entryMeta, endEntryMeta, reference);

    }
  }

  private void storeQueryProperties(NavigationEntryMeta sourceEntryMeta,
      NavigationEntryMeta endEntryMeta, Reference<?, ?> reference) {
    // TODO currently we generate with only one joins which is the id. With non generated references
    // it
    // may cause bugs...
    Join<?> join = reference.joins().get(0);
    URI sourcePropUri = join.getSourceProperty().getUri();
    URI targetPropUri = join.getTargetProperty().getUri();
    storePropUriToQuery(sourceEntryMeta, sourcePropUri);
    storePropUriToQuery(endEntryMeta, targetPropUri);
  }

  private void storePropUriToQuery(NavigationEntryMeta entryMeta, URI propUri) {
    List<URI> propUrisToQuery = propUrisToQueryByEntityUri.get(entryMeta.getUri());
    if (propUrisToQuery == null) {
      propUrisToQuery = new ArrayList<>();
      propUrisToQueryByEntityUri.put(entryMeta.getUri(), propUrisToQuery);
    }
    propUrisToQuery.add(propUri);
  }

  private NavigationEntryMeta createRefAssociation(NavigationEntryMeta entryMeta, URI referenceUri,
      String referenceName, NavigationEntryMeta endEntryMeta) {
    NavigationAssociationMeta assocMeta =
        Navigation.assocMeta(referenceUri, referenceName, entryMeta,
            endEntryMeta, null);
    entryMeta.addAssociationsItem(assocMeta);
    refAssocMetasByEntityReferenceUri.put(referenceUri, assocMeta);
    return endEntryMeta;
  }

  private void createDetAssociation(NavigationEntryMeta sourceEntryMeta, URI referenceUri,
      String referenceName,
      NavigationEntryMeta endEntryMeta) {
    NavigationAssociationMeta backwardAssocMeta =
        Navigation.assocMeta(referenceUri, referenceName, endEntryMeta,
            sourceEntryMeta, null);
    endEntryMeta.addAssociationsItem(backwardAssocMeta);
    detAssocMetasByEntityReferenceUri.put(referenceUri, backwardAssocMeta);
  }

  public NavigationEntryMeta getEntryMeta(URI entityUri) {
    initIfNeeded();
    entityUri = EntityUris.getEntityUriWithoutQuery(entityUri);
    return entryMetasByEntityDefUri.get(entityUri);
  }

  public NavigationAssociationMeta getRefAssocMeta(URI referenceUri) {
    initIfNeeded();
    return refAssocMetasByEntityReferenceUri.get(referenceUri);
  }

  public NavigationAssociationMeta getDetAssocMeta(URI referenceUri) {
    initIfNeeded();
    return detAssocMetasByEntityReferenceUri.get(referenceUri);
  }

  @Override
  public Map<URI, List<NavigationReferenceEntry>> navigate(URI objectUri,
      List<URI> associationMetaUris, NavigationCallBackApi callBack) {
    return navigate(objectUri, associationMetaUris);
  }
}
