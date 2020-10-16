package org.smartbit4all.domain.data;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import org.smartbit4all.core.SB4ContextScope;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.Reference;

/**
 * The {@link EntityCollaboration} consists of a lot of {@link TableData}s. They are indexed by the
 * {@link EntityDefinition} they belongs to. It acts like an in memory database table structure. All
 * the references are maintained in memory. You can add only valid relations. We can save the whole
 * model with an entity data gateway operation. The apply changes will evaluate the data graph and
 * save only the relevant section. It optimizes the save efforts to accomplished by the application.
 * This model can also be retrieved with the entity data gateway. We need to add at least one some
 * starting row into the root entity. The retrieve algorithm is a recursive querying by the directed
 * graph defined by the data tables and the references.
 * 
 * A model represents a complex entity data structure that can be handled as a unit of work. All the
 * actions (retrieve, modification etc.) is working on this structure. Every data table in the model
 * belong to entity definitions and stored in a simple list of column with values matrix.
 * 
 * The collaboration itself is a {@link SB4ContextScope} because it provides services initiated for
 * this collaboration.
 * 
 * The {@link #addColumn(Property)} will implicitly create ManyToOne navigation associations between
 * the data tables. For the OneToMany or the ManyToMany we can add associations manually.
 * 
 * @author Peter Boros
 *
 */
public class EntityCollaboration {

  /**
   * The model that maps data table for the entity definitions.
   */
  private final Map<EntityDefinition, TableData> model = new HashMap<>();

  private TableData rootTable;

  /**
   * Creates a new data model based on the entity definition as root.
   * 
   * @param rootEntityDef The root entity definition.
   */
  public EntityCollaboration(EntityDefinition rootEntityDef) {
    super();
    rootTable = new TableData(rootEntityDef);
    model.put(rootEntityDef, rootTable);
  }

  /**
   * This add a new property to the data model. The namespace is starting from the
   * {@link #rootTable} and all the necessary data table that are missing will be added along this
   * operation.
   * 
   * @param <T> The data type of the given property. The column will store the values from this data
   *        type.
   * @param property Property of the {@link #entityDef}.
   * @return Return the data column defined in the root entity to be able to access the data value.
   */
  public <T> DataColumn<T> addColumn(Property<T> property) {
    DataColumn<T> result;
    if (property instanceof PropertyOwned<?>) {
      /*
       * This is a property of the rootEntity data table. So we can add easily.
       */
      result = rootTable.addColumn(property);
    } else {
      /*
       * Otherwise it's a reference property. So we need to initiate a recursive algorithm to ensure
       * the whole path to the references property.
       */
      PropertyRef<T> propertyRef = (PropertyRef<T>) property;
      result = addColumn(propertyRef, propertyRef.getJoinReferences().listIterator(), rootTable);
    }
    return result;
  }

  /**
   * @param <T>
   * @param property The reference propery with a join path to access the final property at the end.
   * @param joinIndex The index of the join that we have to process. We can start with 0 to process
   *        the first join and we can increase the value by the next recursion level.
   * @param tableData A currently existing data table in the model. This source of the currently
   *        processed join.
   * @return Every table will have it's own column and this return value is the newly created or
   *         already existing column in the tableData.
   */
  private final <T> DataColumn<T> addColumn(PropertyRef<T> property,
      ListIterator<Reference<?, ?>> joinIter, TableData<?> tableData) {
    Reference<?, ?> reference = joinIter.next();
    /*
     * The source of the reference must be the entity of the tableData.
     */
    if (!tableData.entity().equals(reference.getSource())) {
      new IllegalArgumentException(
          "The source of the reference must be the entity of the current data table. "
              + reference.toString());
    }
    // TODO Find the SB4DataReference and use if exists or create a new one if we miss it.

    // Get the data table for the referred entity.
    TableData<?> referredTable = model.get(reference.getTarget());
    if (referredTable == null) {
      // This is a new data table for the given entity.
      referredTable = new TableData<>(reference.getTarget());
    }
    // If we reach the end of the join path then we can add the property to the referredTable.
    if (!joinIter.hasNext()) {
      return referredTable.addColumn(property.getReferredProperty());
    }
    // Else we need to walk ahead to discover the next reference.
    return addColumn(property, joinIter, referredTable);
  }

  private void applyChanges() {
    // TODO Auto-generated method stub

  }

}
