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
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Comparator;
import org.smartbit4all.domain.meta.Expression2Operand.Operator;
import org.smartbit4all.domain.service.entity.EntityUris;

/**
 * It describes the property of the entities. The subclasses are responsible for describing the data
 * structure. The known subclasses are:
 * 
 * <ul>
 * <li>{@link PropertyOwned} - This property is persisted directly into the data table belongs to
 * the given entity. It defines the database column name also. All the properties can be dependent
 * of computations and also the result. If a property has computation references then they will
 * influence the structure of the data table.</li>
 * <li>{@link PropertyRef} - This property is a reference to a property of a related entity. It
 * contains a list of {@link Reference} and the final property at the end. The list of references
 * means a list of joins till we reach the entity.</li>
 * </ul>
 * 
 * @author Peter Boros
 *
 * @param <T> The type parameters must be the acceptable types at the JDBC level. The
 *        {@link PreparedStatement#setLong(int, long)} etc. functions are used to store the value to
 *        the database.
 */
public abstract class Property<T> {

  private String name;

  /**
   * Defines if the database column is mandatory or not.
   */
  private boolean mandatory = false;

  /**
   * If the given property is read only or not
   */
  private boolean readOnly = false;

  /**
   * The {@link JDBCDataConverter} is a converter that manages the JDBC level data type of the
   * property. It defines the database column type and convert to and from between the JDBC and the
   * application when binding and fetching the data.
   */
  private JDBCDataConverter<T, ?> jdbcConverter;

  /**
   * The class of the type parameter.
   */
  private Class<T> type;

  /**
   * The entity definition which the property was created for.
   */
  private EntityDefinition entityDef;

  /**
   * The comparator that can be used to compare the values of the property. Can be set explicitly,
   * but if the type implements Comparable then the compareTo method is used implicitly.
   */
  private Comparator<? super T> comparator;

  /**
   * The property function that extends the property's behavior
   */
  private PropertyFunction propertyFunction;

  public Property(String name, Class<T> type, JDBCDataConverter<T, ?> jdbcConverter) {
    this.name = name;
    this.type = type;
    this.jdbcConverter = jdbcConverter;
    if (this.jdbcConverter == null && this instanceof PropertyOwned) {
      throw new NullPointerException(
          "The JDBCDataConverter must be specified! (" + name + ": " + type.getName() + ")");
    }
  }

  public String getName() {
    return name;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }

  public boolean isReadOnly() {
    return readOnly;
  }

  public void setReadOnly(boolean readOnly) {
    this.readOnly = readOnly;
  }

  /**
   * TODO: Set this in the constructor.
   * 
   * @param entityDef
   */
  public void setEntityDef(EntityDefinition entityDef) {
    this.entityDef = entityDef;
  }

  public EntityDefinition getEntityDef() {
    return entityDef;
  }

  public PropertyFunction getPropertyFunction() {
    return propertyFunction;
  }

  public void setPropertyFunction(PropertyFunction propertyFunction) {
    this.propertyFunction = propertyFunction;
  }

  /**
   * @return An is null expression for the given property.
   */
  public final ExpressionIsNull isNull() {
    return new ExpressionIsNull(new OperandProperty<>(this));
  }

  /**
   * @return An is not null expression for the given property.
   */
  public final ExpressionIsNull isNotNull() {
    return (new ExpressionIsNull(new OperandProperty<>(this))).NOT();
  }

  /**
   * @param value
   * @return Returns an equals expression with the given value as literal.
   */
  public final Expression2Operand<T> eq(T value) {
    return new Expression2Operand<>(new OperandProperty<>(this), Operator.EQ,
        new OperandLiteral<>(value, jdbcConverter()));
  }

  /**
   * @param value
   * @return Returns a not equals expression with the given value as literal.
   */
  public final Expression2Operand<T> noteq(T value) {
    return (new Expression2Operand<>(new OperandProperty<>(this), Operator.EQ,
        new OperandLiteral<>(value, jdbcConverter()))).NOT();
  }

  /**
   * @param value
   * @return Returns a greater or equal expression with the given value as literal.
   */
  public final Expression2Operand<T> ge(T value) {
    return new Expression2Operand<>(new OperandProperty<>(this), Operator.GE,
        new OperandLiteral<>(value, jdbcConverter()));
  }

  /**
   * @param value
   * @return Returns a not greater or equal (less...) expression with the given value as literal.
   */
  public final Expression2Operand<T> notge(T value) {
    return (new Expression2Operand<>(new OperandProperty<>(this), Operator.GE,
        new OperandLiteral<>(value, jdbcConverter()))).NOT();
  }

  /**
   * @param value
   * @return Returns a greater than expression with the given value as literal.
   */
  public final Expression2Operand<T> gt(T value) {
    return new Expression2Operand<>(new OperandProperty<>(this), Operator.GT,
        new OperandLiteral<>(value, jdbcConverter()));
  }

  /**
   * @param value
   * @return Returns a not greater than (less or equal) expression with the given value as literal.
   */
  public final Expression2Operand<T> notgt(T value) {
    return (new Expression2Operand<>(new OperandProperty<>(this), Operator.GT,
        new OperandLiteral<>(value, jdbcConverter()))).NOT();
  }

  /**
   * @param value
   * @return Returns a less or equal expression with the given value as literal.
   */
  public final Expression2Operand<T> le(T value) {
    return new Expression2Operand<>(new OperandProperty<>(this), Operator.LE,
        new OperandLiteral<>(value, jdbcConverter()));
  }

  /**
   * @param value
   * @return Returns a not less or equal (greater than) expression with the given value as literal.
   */
  public final Expression2Operand<T> notle(T value) {
    return (new Expression2Operand<>(new OperandProperty<>(this), Operator.LE,
        new OperandLiteral<>(value, jdbcConverter()))).NOT();
  }

  /**
   * @param value
   * @return Returns a less than expression with the given value as literal.
   */
  public final Expression2Operand<T> lt(T value) {
    return new Expression2Operand<>(new OperandProperty<>(this), Operator.LE,
        new OperandLiteral<>(value, jdbcConverter()));
  }

  /**
   * @param value
   * @return Returns a not less than (greater or equal) expression with the given value as literal.
   */
  public final Expression2Operand<T> notlt(T value) {
    return (new Expression2Operand<>(new OperandProperty<>(this), Operator.LE,
        new OperandLiteral<>(value, jdbcConverter()))).NOT();
  }

  /**
   * @param value
   * @return Returns a like expression with the given value as literal. It's based on the SQL level
   *         like syntax.
   */
  public final Expression2Operand<T> like(T value) {
    return new Expression2Operand<>(new OperandProperty<>(this), Operator.LIKE,
        new OperandLiteral<>(value, jdbcConverter()));
  }

  /**
   * @param value
   * @return Returns a not like expression with the given value as literal. It's based on the SQL
   *         level like syntax.
   */
  public final Expression2Operand<T> notlike(T value) {
    return (new Expression2Operand<>(new OperandProperty<>(this), Operator.LIKE,
        new OperandLiteral<>(value, jdbcConverter()))).NOT();
  }

  /**
   * 
   * @param values
   * @return Returns an in expression with the given values as values.
   * 
   */
  public final ExpressionIn<T> in(Collection<T> values) {
    return new ExpressionIn<>(new OperandProperty<>(this), values);
  }

  /**
   * 
   * @param values
   * @return Returns a not in expression with the given values as values.
   * 
   */
  public final ExpressionIn<T> notin(Collection<T> values) {
    return new ExpressionIn<>(new OperandProperty<>(this), values).NOT();
  }

  /**
   * 
   * @param values
   * @return Returns a between expression with the given operand values as lowerbound and
   *         upperbound.
   * 
   */
  public final ExpressionBetween<T> between(Operand<T> lowerBound, Operand<T> upperBound) {
    return new ExpressionBetween<>(new OperandProperty<>(this), lowerBound, upperBound);
  }

  /**
   * 
   * @param values
   * @return Returns a between expression with the given values as lowerbound and upperbound.
   * 
   */
  public final ExpressionBetween<T> between(T lowerBound, T upperBound) {
    return new ExpressionBetween<>(new OperandProperty<>(this),
        new OperandLiteral<>(lowerBound, jdbcConverter()),
        new OperandLiteral<>(upperBound, jdbcConverter()));
  }

  /**
   * @return The type handler of the given property. This is parameterized by the type of JDBC level
   *         basic types.
   */
  public final JDBCDataConverter<T, ?> jdbcConverter() {
    return jdbcConverter;
  }

  /**
   * Return an input value for the given property.
   * 
   * @return
   */
  public final InputValue<T> input() {
    return new PropertyValue<>(this);
  }

  /**
   * Return an output value for the given property.
   * 
   * @return
   */
  public final OutputValue<T> output() {
    return new PropertyValue<>(this);
  }

  /**
   * Return an input/output value for the given property.
   * 
   * @return
   */
  public final OutputValue<T> inOut() {
    return new PropertyValue<>(this);
  }

  public Class<T> type() {
    return type;
  }

  /**
   * The derived property for the aggregate value. Take this to have the given value but be aware of
   * the group by!
   * 
   * @return The property is created on demand and will be singleton for every property.
   */
  public Property<T> min() {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }

  /**
   * The derived property for the aggregate value. Take this to have the given value but be aware of
   * the group by!
   * 
   * @return The property is created on demand and will be singleton for every property.
   */
  public Property<T> max() {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }

  /**
   * The derived property for the aggregate value. Take this to have the given value but be aware of
   * the group by!
   * 
   * @return The property is created on demand and will be singleton for every property.
   */
  public Property<T> avg() {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }

  /**
   * The derived property for the aggregate value. Take this to have the given value but be aware of
   * the group by!
   * 
   * @return The property is created on demand and will be singleton for every property.
   */
  public Property<T> sum() {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }

  /**
   * Applies 'upper' function on property
   */
  public Property<T> upper() {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }

  /**
   * Applies 'lower' function on property
   */
  public Property<T> lower() {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }

  /**
   * Applies the given function on property
   */
  public Property<T> function(String functionName) {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }

  /**
   * Applies the given function on property, with the given parameters. </br>
   * The parameters should be passed as a string where the properties can be used in '{idx}' form.
   * The {0} is the property the function was called on.</br>
   * </br>
   * For example:</br>
   * <code>
   * myEntity.myProperty.function("concatenateValues", "{0}, {1}, {2}", otherProperty1, 
   * otherProperty2)
   * </code> </br>
   */
  public Property<T> function(String functionName, String params, Property<?>... properties) {
    // handled in PropertyInvocationHandler
    throw new RuntimeException("This method should be intercepted by a proxy!");
  }


  /**
   * Constructs a sort order based on the property.
   * 
   * @return Fluid API item.
   */
  public final SortOrderProperty asc() {
    return new SortOrderProperty(this, true);
  }

  /**
   * Constructs a sort order based on the property.
   * 
   * @return Fluid API item.
   */
  public final SortOrderProperty desc() {
    return new SortOrderProperty(this, false);
  }

  public Comparator<? super T> getComparator() {
    if (comparator == null) {
      if (Comparable.class.isAssignableFrom(type)) {
        Class<Comparable<? super T>> clazz = (Class<Comparable<? super T>>) type;
        comparator = (local, other) -> {
          if (local == null && other == null) {
            return 0;
          } else if (local != null && other == null) {
            return -1;
          } else if (local == null && other != null) {
            return 1;
          } else {
            return clazz.cast(local).compareTo(other);
          }
        };
      }
    }
    return comparator;
  }

  /**
   * TODO the explicit comparator setting should be done with a yet to be implemented TypeDef
   * configuration. It may be done in the constructor or by this setter. For now the visibility is
   * left on package-private to avoid usage from the api in the meantime of planning and
   * implementing, but keeping the place here.
   */
  void setComparator(Comparator<? super T> comparator) {
    this.comparator = comparator;
  }

  /**
   * @return The URI that refers to this property
   */
  public final URI getUri() {
    if (getEntityDef() == null) {
      throw new IllegalStateException(
          "There is no EntityDefinition set to this property! "
              + "This way the URI can not be constructed for this property!");
    }
    String domain = getEntityDef().getDomain();
    String entityName = getEntityDef().entityDefName();
    URI propertyUri = EntityUris.createPropertyUri(domain, entityName, name);
    if (propertyFunction != null) {
      String functionName = propertyFunction.getName();
      return EntityUris.createFunctionPropertyUri(propertyUri, functionName);
    }
    return propertyUri;
  }

}
