/*******************************************************************************
 * Copyright (C) 2020 - 2021 it4all Hungary Kft.
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

/**
 * This expression can connect filtering on more {@link EntityDefinition}. The checking of the
 * existence could be different depending on the direction of the {@link Reference}. And we also can
 * add instructions for the evaluation.
 * 
 * <p>
 * 
 * <b>Referred entity existence</b>
 * <p>
 * In this case we would like to check the existence of records referred by the root
 * {@link EntityDefinition}. It means that we have the reference path to the referred entity. When a
 * customer entity refers its postal address the we can add an existence expression for the address
 * to the expression of the customer. In this case we need to add the reference something like this:
 * customerDef.postalAddress().exists(ADDRESS expression) The parameters of the evaluation:
 * <ul>
 * <li><b>Join the expression into the root query or not?</b> - By default the expression will be
 * joined into the original expression and there will be only one select against the database
 * including this expression also. We can change the default to run a separated select and the
 * result will be used as temporary value set. In this case we will have two selects.</li>
 * </ul>
 * </p>
 * 
 * <b>Entity existence that refers the root entity - backward reference</b>
 * <p>
 * In this case we would like to check the existence of records that refers the root
 * {@link EntityDefinition}. It means that we get a reference path from the entity of the expression
 * to the root entity. To continue the previous example if we have more addresses for the customer
 * then we can add existence expression to the customer query. In this case we need to define the
 * back reference something like this: addressDef.customer().exists. For the evaluation of the
 * detail will use the maximal conjunctive part of the master expression. For example if we search
 * for the customer by it's first name and we add exists for the addresses then the query for the
 * address will include the filter for the first name also. In this way we can avoid querying a
 * large value set from the address and later on filtering it with the query of the customer. In
 * this way we can have only the related addresses that could result much less records in this way.
 * Of course if we have a disjunction (an or) in the query for the master then the detail can use
 * only the related part of the master query. If we query the customer with a condition where we are
 * looking for the customer that (first name like ... OR (last name like ... AND EXISTS (ADDRESS
 * expression))). Then the address query will include only the last name filter.
 *
 * <pre>
 * expression(caseDef) 
 *      AND/OR EXISTS(caseDef.primaryPartner(), addressDef.customer(), expression(AddressDef)) 
 *      AND/OR EXISTS(caseDef.secondaryPartner(), addressDef.customer(), expression(AddressDef))
 *      AND/OR EXISTS(caseDef.parent(), expression(CaseDef))
 * </pre>
 * 
 * </p>
 * 
 * </p>
 * 
 * @author Peter Boros
 */
public final class ExpressionExists extends Expression {

  /**
   * This is the expression for other entity.
   */
  private Expression expression;

  public ExpressionExists(Expression expression) {
    super();
    this.expression = expression;
  }

  @Override
  public Expression NOT() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void accept(ExpressionVisitor visitor) {
    visitor.visitExists(this);
  }

  @Override
  public boolean evaluate() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Expression copy() {
    return new ExpressionExists(expression != null ? expression.copy() : null);
  }

  public final Expression getExpression() {
    return expression;
  }

}
