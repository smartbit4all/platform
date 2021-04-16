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
package org.smartbit4all.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionBoolean;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.ExpressionClause;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionInDataSet;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import org.smartbit4all.domain.meta.ExpressionVisitor;
import org.smartbit4all.domain.meta.JDBCDataConverter;

/**
 * The where node is responsible for an expression that forms the where section of a select, insert
 * or update.
 * 
 * @author Peter Boros
 */
public class SQLWhere implements SQLStatementNode {

  /**
   * The condition expression for the where.
   */
  private Expression condition;

  /**
   * The traversal algorithm ensure the same path so we can use a simple list to access the bind
   * position for the literals. This list contains all the bind values.
   */
  private List<SQLBindValueLiteral> binds = new ArrayList<>();

  /**
   * Constructs a SQL where node with the given expression.
   * 
   * @param condition The expression.
   */
  public SQLWhere(Expression condition) {
    super();
    this.condition = condition;
  }

  @Override
  public void render(SQLStatementBuilderIF b) {

    ExpressionVisitor visitor = new ExpressionVisitor() {
      @Override
      public void visitClause(ExpressionClause expression) {
        b.append(expression.getOperator());
      }

      @Override
      public void visitBracketPre(ExpressionBracket expression) {
        b.appendOpenParenthesis(expression);
      }

      @Override
      public void visitBracketPost(ExpressionBracket expression) {
        b.append(StringConstant.RIGHT_PARENTHESIS);
      }


      @Override
      public <T> void visit2Operand(Expression2Operand<T> expression) {
        binds.addAll(b.append(expression));
      }

      @Override
      public void visitBetween(ExpressionBetween<?> expression) {
        binds.addAll(b.append(expression));
      }

      @Override
      public <T> void visitIn(ExpressionIn<T> expression) {
        binds.addAll(b.append(expression));
      }

      @Override
      public void visitIsNull(ExpressionIsNull expression) {
        binds.addAll(b.append(expression));
      }

      @Override
      public void visitBoolean(ExpressionBoolean expression) {
        b.append(expression);
      }

      @Override
      public void visitInDataSet(ExpressionInDataSet expression) {
        binds.addAll(b.append(expression));
      }

    };
    condition.accept(visitor);

  }

  @Override
  public void bind(SQLStatementBuilderIF b, PreparedStatement stmt) throws SQLException {
    for (SQLBindValueLiteral valueLiteral : binds) {
      JDBCDataConverter<?, ?> typeHandler = valueLiteral.literal.typeHandler();
      bind(b, stmt, valueLiteral, typeHandler.appType());
    }
  }

  // We cast by the typeHandler's appType.
  @SuppressWarnings("unchecked")
  private <T> void bind(SQLStatementBuilderIF b, PreparedStatement stmt,
      SQLBindValueLiteral valueLiteral, Class<T> sourceClazz) throws SQLException {
    b.bind(stmt, valueLiteral.position,
        (JDBCDataConverter<T, ?>) valueLiteral.literal.typeHandler(),
        (T) valueLiteral.literal.value());
  }

  /**
   * The bind values after the render.
   * 
   * @return
   */
  public List<SQLBindValueLiteral> binds() {
    return binds;
  }

}
