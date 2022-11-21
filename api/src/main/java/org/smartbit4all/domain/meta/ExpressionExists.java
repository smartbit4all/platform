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

import java.util.Iterator;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.Reference.Join;

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
   * This is the expression for the entity filtered by the exists.
   */
  private Expression expression;

  /**
   * The context of the {@link #expression} even if it's referred or a detail one.
   */
  private EntityDefinition contextEntity;

  /**
   * The master entity that is the root of the {@link #referencePath}.
   */
  private EntityDefinition rootEntity;

  /**
   * The join path to access the context entity of the exists. If it's a referred entity (a simple
   * join) then it's the context of the expression. Else we have another join path to access the
   * same entity from the direction of a detail entity. If it's empty then the master entity owns
   * the properties directly.
   */
  private JoinPath referencePath;

  /**
   * If the exists related with a detail entity then this join path is the access to the master
   * entity as a join. If it's null then the {@link #contextEntity} is referred directly by the
   * {@link #referencePath} from the {@link #rootEntity}. If it has value then the reference path
   * contains the join to the master entity that owns the properties referred by the
   * {@link #masterReferencePath}.
   */
  private JoinPath masterReferencePath;

  /**
   * This constructor constructs an exists expression with a directly referred entity.
   * 
   * @param rootEntity The root entity.
   * @param contextEntity The context entity the expression is related to.
   * @param expression The expression.
   * @param referencePath The reference path to access the context entity from the root.
   */
  public ExpressionExists(EntityDefinition rootEntity, EntityDefinition contextEntity,
      Expression expression, JoinPath referencePath) {
    super();
    this.rootEntity = rootEntity;
    this.contextEntity = contextEntity;
    this.expression = expression;
    this.referencePath = referencePath;
  }

  /**
   * This constructor constructs an exists expression with a detail context entity.
   * 
   * @param rootEntity The root entity.
   * @param contextEntity The context entity the expression is related to.
   * @param expression The expression.
   * @param referencePath The reference path to access the context entity from the root.
   */
  public ExpressionExists(EntityDefinition rootEntity, EntityDefinition contextEntity,
      Expression expression, JoinPath referencePath, JoinPath masterReferencePath) {
    this(rootEntity, contextEntity, expression, referencePath);
    this.masterReferencePath = masterReferencePath;
  }

  @Override
  public Expression NOT() {
    setNegate(!isNegate());
    return this;
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
    return new ExpressionExists(rootEntity, contextEntity, expression, referencePath,
        masterReferencePath);
  }

  public final Expression getExpression() {
    return expression;
  }

  public final EntityDefinition getContextEntity() {
    return contextEntity;
  }

  public final EntityDefinition getRootEntity() {
    return rootEntity;
  }

  public final JoinPath getReferencePath() {
    return referencePath;
  }

  final void setReferencePath(JoinPath joinPath) {
    this.referencePath = joinPath;
    if (!joinPath.references.isEmpty()) {
      this.rootEntity = joinPath.firstEntity();
    }
  }

  public final JoinPath getMasterReferencePath() {
    return masterReferencePath;
  }

  public final Expression getTranslatedReferredExpression() {
    if (getMasterReferencePath() == null) {
      // It's an exists related to a simple referred entity. We can translate the
      // expression and attach it to the original where instead of the exists itself.
      if (getReferencePath() != null)
        return expression.translate(rootEntity, referencePath.references);
    }
    return null;
  }

  public final Property<?> getContextProperty() {
    Iterator<Join<?>> lastJoinIter = masterReferencePath.last().joins().iterator();
    if (lastJoinIter.hasNext()) {
      Join<?> join = lastJoinIter.next();
      Property<?> targetProperty = join.getTargetProperty();
      if (referencePath.references.isEmpty()) {
        // We refer to the property of the root. There is no need to translate.
        return targetProperty;
      }
      // The target property must be translated to the root entity to add the query. All the queried
      // properties must come from the root.
      return rootEntity.findOrCreateReferredProperty(referencePath.references, targetProperty);
    }
    return null;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("~~>");
    builder.append(isNegate() ? "NOT" : "");
    builder.append(StringConstant.SPACE);
    builder.append("EXIST");
    builder.append(StringConstant.LEFT_CURLY);
    builder.append(contextEntity.entityDefName());
    builder.append("->");
    builder.append(StringConstant.SPACE);
    builder.append(ExpressionToString.toString(expression));
    builder.append(StringConstant.RIGHT_CURLY);
    return builder.toString();
  }

}
