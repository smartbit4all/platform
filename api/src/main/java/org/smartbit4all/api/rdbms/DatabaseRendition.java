package org.smartbit4all.api.rdbms;

import static java.util.stream.Collectors.joining;
import java.util.Stack;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.databasedefinition.bean.AlterOperation;
import org.smartbit4all.api.databasedefinition.bean.ColumnDefinition;
import org.smartbit4all.api.databasedefinition.bean.DDLStatementData;
import org.smartbit4all.api.databasedefinition.bean.DDLStatementKind;
import org.smartbit4all.api.databasedefinition.bean.DatabaseDefinition;
import org.smartbit4all.api.databasedefinition.bean.TableDefinition;
import org.smartbit4all.core.utility.StringConstant;
import com.google.common.base.Strings;

/**
 * The rendition is a database specific DDL script objcet that contains all the necessary scripts to
 * create or modify the database structure. All the statements are managed by this rendition to be
 * able to inject the database specific rendition logic easily.
 * 
 * @author Peter Boros
 */
public class DatabaseRendition implements Consumer<DatabaseDefinition> {


  private static final Logger log = LoggerFactory.getLogger(DatabaseRendition.class);

  public static final String SCHEMA_TOKEN = "{$schema}";

  public static final String NOT_NULL = "NOT NULL";

  public static final String NULL = "NULL";

  public static final String STMT_INDENT = "  ";

  /**
   * The alter script statement as a root object for the whole statement hierarchy.
   */
  private final DDLStatementData rootStatement =
      new DDLStatementData().kind(DDLStatementKind.ALTERSCRIPT);

  /**
   * The stack of the actual statements.
   */
  private final Stack<DDLStatementData> statementStack = new Stack<>();

  /**
   * If the schema is not set for a given object then it can give a default value. Not necessary to
   * set if empty then we will have a script without schema.
   */
  private String defaultSchema;

  private DatabaseDefinitionApi definitionApi;

  private DatabaseDefinition databaseDefinition;

  DatabaseRendition(String defaultSchema, DatabaseDefinitionApi definitionApi) {
    super();
    this.defaultSchema = defaultSchema;
    // set the root statement as actual.
    statementStack.push(rootStatement);
    this.definitionApi = definitionApi;
  }

  /**
   * The schema qualified name of the given database object.
   * 
   * @param schema The schema that can be null or empty to ignore.
   * @param objectName The name of the object that must be not empty.
   */
  final String objectName(String schema, String objectName) {
    return constructSchemaPrefix(schema) + objectName;
  }

  /**
   * If the schema is not null or the {@link #defaultSchema} is defined then it will produce the
   * schema. text literal else empty string returned.
   * 
   * @param schema
   * @return
   */
  final String constructSchemaPrefix(String schema) {
    return (Strings.isNullOrEmpty(schema))
        ? (Strings.isNullOrEmpty(defaultSchema) ? StringConstant.EMPTY
            : defaultSchema + StringConstant.DOT)
        : schema + StringConstant.DOT;
  }

  @Override
  public void accept(DatabaseDefinition dbDef) {
    // Render all the table statements that are created.
    this.databaseDefinition = dbDef;
    dbDef.getTables().stream().forEach(t -> renderTable(t));
  }

  final void renderTable(TableDefinition table) {
    if (table.getOperation() == AlterOperation.CREATE) {
      startCreateTable(table);
      for (ColumnDefinition column : table.getColumns()) {
        defineColumn(table, column);
      }
      finishCreateTable(table);
      // Generate primary key
      alterTableAddPrimaryKey(table);
    }
  }


  void startCreateTable(TableDefinition table) {
    DDLStatementData createTable =
        new DDLStatementData().kind(DDLStatementKind.CREATETABLE).prefix("CREATE TABLE "
            + objectName(table.getSchema(), table.getName()) + StringConstant.LEFT_PARENTHESIS);
    rootStatement.addInnerStatementsItem(createTable);
    statementStack.clear();
    statementStack.push(rootStatement);
    statementStack.push(createTable);
  }

  public void finishCreateTable(TableDefinition table) {
    statementStack.pop().suffix(StringConstant.RIGHT_PARENTHESIS);
  }

  public void defineColumn(TableDefinition table, ColumnDefinition column) {
    statementStack.peek()
        .addInnerStatementsItem(new DDLStatementData().kind(DDLStatementKind.DEFINECOLUMN)
            .prefix(column.getName() + " "
                + definitionApi.render(column.getTypeDefinition(),
                    databaseDefinition.getDatabaseKind())
                + defaultClause(column, true)
                + nullClause(column)));
  }

  void alterTableAddPrimaryKey(TableDefinition table) {

  }

  String nullClause(ColumnDefinition column) {
    return StringConstant.SPACE + (Boolean.TRUE.equals(column.getNullable()) ? NULL : NOT_NULL);
  }

  /**
   * 
   * @param column The column
   * @param forCreate If we need the clause for create table.
   * @return The default clause proper for the database kind.
   */
  public String defaultClause(ColumnDefinition column, boolean forCreate) {
    return column.getDefaultValue() == null ? StringConstant.EMPTY
        : (StringConstant.SPACE + column.getDefaultValue());
  }

  public final DDLStatementData getRootStatement() {
    return rootStatement;
  }

  public String getScript() {
    return rootStatement.getInnerStatements().stream()
        .map(stmt -> generateScript(StringConstant.EMPTY, stmt))
        .collect(
            joining(StringConstant.NEW_LINE, StringConstant.NEW_LINE, StringConstant.SEMICOLON));
  }

  private String generateScript(String indent, DDLStatementData stmt) {
    String result = indent + stmt.getPrefix();
    if (!stmt.getInnerStatements().isEmpty()) {
      result += StringConstant.NEW_LINE +
          stmt.getInnerStatements().stream().map(s -> generateScript(indent + STMT_INDENT, s))
              .collect(
                  joining(StringConstant.COMMA + StringConstant.NEW_LINE));
      if (!Strings.isNullOrEmpty(stmt.getSuffix())) {
        result += StringConstant.NEW_LINE + indent + stmt.getSuffix();
      }
    }
    return result;
  }

}
