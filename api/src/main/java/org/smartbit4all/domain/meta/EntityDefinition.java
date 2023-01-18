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
package org.smartbit4all.domain.meta;

import java.net.URI;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This is the super interface of the entity definition singletons responsible for the runtime
 * collaboration between the entities. The meta data is available via the operations of the entity
 * definitions implementing this interface. The meta data can be used to create TableData structures
 * manually but at the same time the interface of the given entity can define different views. A
 * view is a subset of the available attributes. This is an extended version of meta data mapping
 * published by Martin Fowler in 2002.
 * 
 * 
 * @see <a href="https://www.martinfowler.com/eaaCatalog/metadataMapping.html">Meta data mapping</a>
 * 
 * 
 * @author Peter Boros
 *
 */
public interface EntityDefinition {

  public static final String PROPERTY_COUNT_NAME = "countRows";

  public static class TableDefinition {

    /**
     * The name of the table that is used in the SQL statement. Be careful with the case of this
     * String. It must match with the database table name handling. If you need case sensitive table
     * names then use {@link StringConstant#DOUBLE_QUOTE} inside the name!
     */
    private final String name;

    /**
     * Constructs a table node.
     * 
     * @param name The name of the table.
     */
    public TableDefinition(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

  }

  /**
   * This function return the property meta by name.
   * 
   * @param propertyName Property name
   * @return The meta object.
   */
  Property<?> getProperty(String propertyName);

  /**
   * This function return the property meta by name.
   * 
   * @param propertyName Property name
   * @return The meta object.
   */
  PropertyObject getPropertyObject(String propertyName);

  /**
   * Returns the referenced entity definition by reference's name.
   * 
   * TODO do we need this?
   * 
   * @param referenceName
   * @return
   */
  default EntityDefinition getReferencedEntity(String referenceName) {
    return getReference(referenceName).getTarget();
  };

  /**
   * Returns the reference by name.
   * 
   * @param referenceName
   * @return
   */
  Reference<?, ?> getReference(String referenceName);

  Property<?> findOrCreateReferredProperty(List<Reference<?, ?>> joinPath,
      Property<?> referredProperty);

  Property<?> findOrCreateReferredProperty(String[] joinPath, String referredPropertyName);

  /**
   * It will return the properties of the primary key.
   * 
   * @return
   */
  PropertySet PRIMARYKEYDEF();

  /**
   * The JDBC parameterization of the entity.
   * 
   * @return
   */
  TableDefinition tableDefinition();

  /**
   * The count is a privileged computed property. If we add this then it will contain the number of
   * records in the set. It's group by aware so in case of group by it will be calculated for the
   * similar values of the group properties.
   * 
   * @return
   */
  Property<Long> count();

  String entityDefName();

  PropertySet allProperties();

  List<Reference<?, ?>> allReferences();

  /**
   * 
   * This constructs an expression that is related to a detail entity. The master entity expression
   * must be separated to be able to include it into the detail as additional expression.
   * 
   * ExpressionExists ex = personReg.crossingReg().exists(Exp);
   * 
   * vehicleDef.crossingDef().exists(ex);
   * 
   * ExpressionExists ex = personReg.crossingReg().exists(vehicleDef.crossingDef().join(), Exp);
   * 
   * exists(JoinPath<E> joinPath, Exp)
   * 
   * vehicleDef.crossingDef()
   * 
   * @param masterJoin The {@link JoinPath} from the detail entity to the master entity. The detail
   *        entity is the first one in the join the master entity is the last one.
   * @param expression The expression must be related to the detail entity as context.
   * @return Returns an exists expression for the given expression.
   */
  public ExpressionExists exists(JoinPath masterJoin, Expression expression);

  /**
   * If we would like to add an expression for a referred entity. Like we can add
   * personDef.address().exists(addressDef.zipcode().eq("2030")) that will be equivalent with
   * personDef.address().zipcode().eq("2030") or even with personDef.zipcode().eq("2030") if we have
   * ref properties.
   * 
   * @param expressionOfTarget
   * @return
   */
  public Expression exists(Expression expressionOfTarget);

  /**
   * @return The domain of the entity definition.
   */
  String getDomain();

  /**
   * @return The URI that refers to this entity definition.
   */
  URI getUri();

  /**
   * This is the join (the list of {@link Reference}) to access the current entity. If we ask it
   * from an entity definition directly then it will be empty. Like personDef.join(). But if we use
   * the dynamic references an entity have then we will have the join path with the list of
   * references to access the final entity. Like personDef.address().settlement().join() returns the
   * two reference in proper order.
   * 
   * @return
   */
  JoinPath join();

}
