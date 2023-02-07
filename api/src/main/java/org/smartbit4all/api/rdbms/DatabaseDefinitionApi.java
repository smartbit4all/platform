package org.smartbit4all.api.rdbms;

import java.util.Collection;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition;
import org.smartbit4all.api.databasedefinition.bean.DatabaseDefinition;
import org.smartbit4all.api.databasedefinition.bean.DatabaseKind;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * The structure of the relational databases are important for the applications for searching and
 * data transfers. To control their structure this api and the {@link DatabaseDefinition} object
 * gives us a full control. The whole structure can be described with this object and we can save,
 * load, render and compare these objects.
 * 
 * @author Peter Boros
 */
public interface DatabaseDefinitionApi {

  /**
   * Constructs the render by the database definition.
   * 
   * @param dbDefinition The database definition object.
   * @return
   */
  DatabaseRendition render(DatabaseDefinition dbDefinition);

  /**
   * Render the database type as string.
   * 
   * @param typeDef The type definition.
   * @param dbKind The db kind as target rendering platform.
   * @return
   */
  String render(ColumnTypeDefinition typeDef, DatabaseKind dbKind);

  /**
   * Constructs the database definition model based on the {@link EntityDefinition} in the list
   * 
   * @param definitions
   * @return
   */
  DatabaseDefinition definitionOf(Collection<EntityDefinition> definitions);

}
