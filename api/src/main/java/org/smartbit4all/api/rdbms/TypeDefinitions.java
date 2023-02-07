package org.smartbit4all.api.rdbms;

import org.smartbit4all.api.databasedefinition.bean.ColumnTypeDefinition;
import org.smartbit4all.api.databasedefinition.bean.DatabaseKind;
import org.smartbit4all.core.utility.StringConstant;

final class TypeDefinitions {

  static final String varcharOra(ColumnTypeDefinition typeDef, DatabaseKind dbKind) {
    return "VARCHAR2" + StringConstant.LEFT_PARENTHESIS + typeDef.getLength()
        + StringConstant.RIGHT_PARENTHESIS;
  }

  static final String numberOra(ColumnTypeDefinition typeDef, DatabaseKind dbKind) {
    return "NUMBER" + StringConstant.LEFT_PARENTHESIS + typeDef.getPrecision()
        + (typeDef.getScale() != null ? StringConstant.COLON_SPACE + typeDef.getScale()
            : StringConstant.EMPTY)
        + StringConstant.RIGHT_PARENTHESIS;
  }

}
