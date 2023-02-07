package org.smartbit4all.api.rdbms;

import java.util.Collection;
import java.util.EnumMap;
import java.util.function.BiFunction;
import org.smartbit4all.api.databasedefinition.bean.AlterOperation;
import org.smartbit4all.api.databasedefinition.bean.ColumnDefinition;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition;
import org.smartbit4all.api.databasedefinition.bean.DatabaseDefinition;
import org.smartbit4all.api.databasedefinition.bean.DatabaseKind;
import org.smartbit4all.api.databasedefinition.bean.TableDefinition;
import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition.BaseTypeEnum;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Peter Boros
 */
public class DatabaseDefinitionApiImpl implements DatabaseDefinitionApi {

  @Autowired
  private DatabaseDefinitionApi databaseDefinitionApi;

  @Override
  public DatabaseRendition render(DatabaseDefinition dbDefinition) {
    DatabaseRendition databaseRendition = new DatabaseRendition(null, databaseDefinitionApi);
    databaseRendition.accept(dbDefinition);
    return databaseRendition;
  }

  @Override
  public DatabaseDefinition definitionOf(Collection<EntityDefinition> definitions) {
    DatabaseDefinition result = new DatabaseDefinition();
    for (EntityDefinition entityDefinition : definitions) {
      TableDefinition tableDefinition =
          new TableDefinition().name(entityDefinition.tableDefinition().getName())
              .operation(AlterOperation.CREATE);
      for (Property<?> property : entityDefinition.allProperties()) {
        if (property instanceof PropertyOwned) {
          PropertyOwned<?> owned = (PropertyOwned) property;
          tableDefinition
              .addColumnsItem(new ColumnDefinition().name(owned.getName()).typeDefinition(
                  new ColumnTypeDefinition().baseType(BaseTypeEnum.VARCHAR).length(100)));
        }
      }
      result.addTablesItem(tableDefinition);
    }
    return result;
  }

  private static final EnumMap<BaseTypeEnum, EnumMap<DatabaseKind, BiFunction<ColumnTypeDefinition, DatabaseKind, String>>> typeMap =
      new EnumMap<>(BaseTypeEnum.class);

  static {
    {
      EnumMap<DatabaseKind, BiFunction<ColumnTypeDefinition, DatabaseKind, String>> type =
          typeMap.computeIfAbsent(BaseTypeEnum.VARCHAR,
              dt -> new EnumMap<>(DatabaseKind.class));
      type.put(DatabaseKind.ORACLE, TypeDefinitions::varcharOra);
      type.put(DatabaseKind.POSTGRESQL, TypeDefinitions::varcharOra);
      type.put(DatabaseKind.H2, TypeDefinitions::varcharOra);
    }
    {
      EnumMap<DatabaseKind, BiFunction<ColumnTypeDefinition, DatabaseKind, String>> type =
          typeMap.computeIfAbsent(BaseTypeEnum.NUMBER,
              dt -> new EnumMap<>(DatabaseKind.class));
      type.put(DatabaseKind.ORACLE, TypeDefinitions::varcharOra);
      type.put(DatabaseKind.POSTGRESQL, TypeDefinitions::varcharOra);
      type.put(DatabaseKind.H2, TypeDefinitions::varcharOra);
    }
  }

  @Override
  public String render(ColumnTypeDefinition typeDef, DatabaseKind dbKind) {
    BiFunction<ColumnTypeDefinition, DatabaseKind, String> function =
        typeMap.get(typeDef.getBaseType()).get(dbKind);
    if (function == null) {
      function = (type, db) -> {
        return "MISSINGTYPEDEF";
      };
    }
    return function.apply(typeDef, dbKind);
  }

}
