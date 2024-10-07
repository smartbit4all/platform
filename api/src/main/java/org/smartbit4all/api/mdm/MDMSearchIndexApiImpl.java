package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.BranchedObjectEntry.BranchingStateEnum;
import org.smartbit4all.api.storage.bean.ObjectAspect;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ReferenceDefinition;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class MDMSearchIndexApiImpl implements MDMSearchIndexApi {

  private static final Logger log = LoggerFactory.getLogger(MDMSearchIndexApiImpl.class);

  @Autowired
  ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  ObjectApi objectApi;

  @Override
  public final SearchIndexImpl<BranchedObjectEntry> createSearchIndexForEntryInstance(
      MDMEntryDescriptor entryDescriptor,
      String mdmDefName) {
    SearchIndexImpl<BranchedObjectEntry> result =
        new SearchIndexImpl<>(mdmDefName,
            entryDescriptor.getSearchIndexForEntries(),
            entryDescriptor.getSchema(),
            BranchedObjectEntry.class);
    result.map(BranchedObjectEntry.BRANCHING_STATE, BranchingStateEnum.class,
        BranchedObjectEntry.BRANCHING_STATE);
    result.map(BranchedObjectEntry.ORIGINAL_URI, URI.class, BranchedObjectEntry.ORIGINAL_URI);
    result.map(BranchedObjectEntry.BRANCH_URI, URI.class, BranchedObjectEntry.BRANCH_URI);

    ObjectDefinition<?> objectDefinition =
        objectApi.definition(entryDescriptor.getTypeQualifiedName());
    if (!entryDescriptor.getTableColumns().isEmpty()) {
      entryDescriptor.getTableColumns().stream().forEach(
          tcd -> {
            checkTableColumnDescriptor(tcd);
            String[] path = tcd.getPath().toArray(StringConstant.EMPTY_ARRAY);
            addEntryPropertyToSearchIndex(result, tcd.getName(),
                tcd.getAspectName(), getTypeOfColumn(objectDefinition, tcd), -1,
                path);
          });
    } else {
      // Navigate to the nearest referred object.
      Map<String, ReferenceDefinition> outgoingReferences =
          objectDefinition.getOutgoingReferences();
      objectDefinition.getPropertiesByName().entrySet()
          .forEach(e -> {
            if (!outgoingReferences.containsKey(e.getKey())) {
              Class<?> typeClass = getClazz(e.getValue().getTypeClass(), String.class);
              addEntryPropertyToSearchIndex(result, e.getValue().getName(), null, typeClass, -1,
                  e.getValue().getName());
            }
          });
    }
    return result;
  }

  /**
   * Add a property to the search index with complex processing mechanism. Depending on the state of
   * the given object it will show the original object or the branched one. So we will have a
   * heterogeneous list of objects in the table data.
   *
   * @param searchIndex
   * @param propertyName
   * @param aspect The aspect of the shadow object that contains the last known integration version
   *        of the given object.
   * @param typeClass
   * @param length
   * @param path
   */
  private final void addEntryPropertyToSearchIndex(SearchIndexImpl<?> searchIndex,
      String propertyName, String aspect, Class<?> typeClass, int length, String... path) {
    Objects.requireNonNull(propertyName, "propertyName can not be null!");
    Objects.requireNonNull(path, "path can not be null!");

    // The object node is an BranchedObjectEntry.definition.entry node and can be used by the
    // ObjectApi
    // to navigate to every property let it be original or branched.
    searchIndex.mapContext(propertyName, typeClass, length,
        context -> {
          ObjectNode node = context.getRowNode();
          String key = String.valueOf(node.getObjectAsMap());
          ObjectNode actual = (ObjectNode) context.getRowVariables().get(key);
          if (actual == null) {
            actual = getActualObjectNodeOfBranchedNode(node, aspect);
            context.putRowVariablesItem(key, actual);
          }
          return actual.getValue(path);
        });
  }

  @Override
  public final ObjectNode getActualObjectNodeOfBranchedNode(ObjectNode branchedObjectEntryNode,
      String aspect) {
    Objects.requireNonNull(branchedObjectEntryNode, "branchedObjectEntryNode can not be null!");

    BranchingStateEnum stateEnum =
        branchedObjectEntryNode.getValue(BranchingStateEnum.class,
            BranchedObjectEntry.BRANCHING_STATE);

    ObjectNode objectNode;

    if (stateEnum == BranchingStateEnum.NOP || stateEnum == BranchingStateEnum.DELETED) {
      objectNode =
          getNodeOrElseAspect(aspect,
              branchedObjectEntryNode.ref(BranchedObjectEntry.ORIGINAL_URI).getObjectUri());
    } else {
      objectNode =
          getNodeOrElseAspect(aspect,
              branchedObjectEntryNode.ref(BranchedObjectEntry.BRANCH_URI).getObjectUri());
    }
    return objectNode;
  }

  private final ObjectNode getNodeOrElseAspect(String aspect, URI branchedUri) {
    ObjectNode objectNode;
    objectNode = objectApi.load(branchedUri);
    ObjectAspect objectAspect;
    // Get an ObjectNode to resolve
    if (objectNode.aspects().get() != null
        && (objectAspect = objectNode.aspects().get().get(aspect)) != null) {
      objectNode =
          objectApi.create(StringConstant.EMPTY, objectAspect.getObjectAsMap());
    }
    return objectNode;
  }

  private void checkTableColumnDescriptor(MDMTableColumnDescriptor tcd) {
    Objects.requireNonNull(tcd, "tcd can not be null!");
    Objects.requireNonNull(tcd.getName(), "tcd.getName() can not be null!");
    Objects.requireNonNull(tcd.getPath(), "tcd.getPath() can not be null!");
  }


  private final Class<?> getClazz(String className, Class<?> defaultClass) {
    Class<?> typeClass;
    try {
      typeClass = Class.forName(className);
    } catch (ClassNotFoundException | NullPointerException e1) {
      typeClass = defaultClass;
    }
    return typeClass;
  }

  @Override
  public final SearchIndexImpl<?> createSearchIndexForEntry(MDMEntryDescriptor entryDescriptor,
      String mdmDefName) {
    SearchIndexImpl<?> result =
        new SearchIndexImpl<>(mdmDefName, entryDescriptor.getName(), entryDescriptor.getSchema(),
            getClazz(entryDescriptor.getTypeQualifiedName(), Object.class));
    // The normal index for the published objects has the same column structure. But in this case
    // the mapping is simple because we get directly get the list of objects not BranchedObjectEntry
    // list.
    result.mapComplex(BranchedObjectEntry.BRANCHING_STATE, BranchingStateEnum.class, 50,
        o -> BranchingStateEnum.NOP);
    result.mapComplex(BranchedObjectEntry.ORIGINAL_URI, URI.class, 500,
        o -> o.getObjectUri());
    result.mapComplex(BranchedObjectEntry.BRANCH_URI, URI.class, 500,
        o -> null);

    ObjectDefinition<?> objectDefinition =
        objectApi.definition(entryDescriptor.getTypeQualifiedName());

    if (!entryDescriptor.getTableColumns().isEmpty()) {
      entryDescriptor.getTableColumns().stream().forEach(
          tcd -> {
            checkTableColumnDescriptor(tcd);
            String[] path = tcd.getPath().toArray(StringConstant.EMPTY_ARRAY);
            if (tcd.getAspectName() == null) {
              result.map(tcd.getName(),
                  getTypeOfColumn(objectDefinition, tcd),
                  path);
            } else {
              result.mapComplex(tcd.getName(),
                  node -> {
                    final Map<String, ObjectAspect> map = node.aspects().get();
                    if (map == null || map.isEmpty()) {
                      return null;
                    }

                    ObjectAspect objectAspect = map.get(tcd.getAspectName());
                    // Get an ObjectNode to resolve
                    if (objectAspect == null) {
                      return null;
                    }

                    ObjectNode objectNode = objectApi.create(StringConstant.EMPTY,
                        objectDefinitionApi.definition(objectAspect.getTypeQualifiedName()),
                        objectAspect.getObjectAsMap());
                    return objectNode.getValue(getTypeOfColumn(objectDefinition, tcd), path);
                  });
            }
          });
    } else {
      // Navigate to the nearest referred object.
      Map<String, ReferenceDefinition> outgoingReferences =
          objectDefinition.getOutgoingReferences();
      objectDefinition.getPropertiesByName().entrySet()
          .forEach(e -> {
            if (!outgoingReferences.containsKey(e.getKey())) {
              Class<?> typeClass;
              try {
                typeClass = Class.forName(e.getValue().getTypeClass());
              } catch (ClassNotFoundException e1) {
                typeClass = String.class;
              }
              result.map(e.getKey(), typeClass,
                  e.getValue().getName());
            }
          });
    }
    return result;
  }

  private Class<?> getTypeOfColumn(ObjectDefinition<?> objectDefinition,
      MDMTableColumnDescriptor tcd) {
    Class<?> type = null;
    if (!Strings.isNullOrEmpty(tcd.getTypeClass())) {
      try {
        type = Class.forName(tcd.getTypeClass());
      } catch (ClassNotFoundException e) {
        log.warn("ClassNotFound by table columns typeClass: {}, {}",
            tcd.getName(), tcd.getTypeClass());
      }
    }
    if (type == null) {
      type = objectDefinitionApi.getTypeOfProperty(objectDefinition, String.class,
          tcd.getPath().toArray(StringConstant.EMPTY_ARRAY));
    }
    return type;
  }

}
