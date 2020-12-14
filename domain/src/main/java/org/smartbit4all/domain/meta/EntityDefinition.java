/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.meta;

import java.util.List;
import org.smartbit4all.core.SB4Service;
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
public interface EntityDefinition extends SB4Service {

  public static class TableDefinition {
    /**
     * The databases usually defines schemas. They can have different level of support but anyway
     * from the perspective of an SQL select they are namespaces. So if we know this namespace then
     * we have to use it to construct the qualified name of a table.
     */
    private final String schema;

    /**
     * The name of the table that is used in the SQL statement. Be careful with the case of this
     * String. It must match with the database table name handling. If you need case sensitive table
     * names then use {@link StringConstant#DOUBLE_QUOTE} inside the name!
     */
    private final String name;

    /**
     * Constructs a table node.
     * 
     * @param schema The database schema.
     * @param name The name of the table.
     */
    public TableDefinition(String schema, String name) {
      this.schema = schema;
      this.name = name;
    }

    public String getSchema() {
      return schema;
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

  Property<?> getReferredPropertyByPath(List<Reference<?, ?>> joinPath,
      Property<?> referredProperty);

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

  EntityService<?> services();

  PropertySet allProperties();

}
