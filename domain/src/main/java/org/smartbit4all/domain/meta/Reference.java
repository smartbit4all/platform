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

import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.service.entity.EntityUris;
import org.springframework.util.Assert;

/**
 * The instances of this class defines the reference meta data between two Entity. At meta level the
 * entities are referred by their interface classes. So in runtime they can be accessed by these
 * interface names. The entity descriptor interfaces are specific service singleton.
 * 
 * If we have a relation between Person and Organization then we have the following reference:
 * 
 * <ul>
 * <li>source = PersonDef</li>
 * <li>name = employer</li>
 * <li>target = OrganizationDef</li>
 * </ul>
 * 
 * It means that if we look at person from the organization through this reference then we can see
 * employee(s!). Be careful with the naming because the plural form serves the better understanding
 * of the relation. If we look at the organization from the person using this reference then we can
 * a see an employer.
 * 
 * @author Peter Boros
 *
 */
public class Reference<S extends EntityDefinition, T extends EntityDefinition> {

  /**
   * The entity that refer to the target with one or more attributes. It has the foreign keys.
   */
  private final S source;

  /**
   * The entity that is referred by the source entity. Typically its primary key is referred. TODO
   * Think it over if it's necessary to refer other unique attribute set.
   */
  private final T target;

  /**
   * The name of the reference that is unique in the {@link #source}.
   */
  private final String name;

  /**
   * If the reference is mandatory then it must have value in the source field and must refer to an
   * existing record in the target entity. By default the mandatory is null and derived from the
   * mandatory of the referrer properties. If any of them is mandatory then the reference is
   * mandatory and cann't be overridden. If none of them is mandatory then this variable will be
   * false but at setup time we can make it mandatory.
   */
  private Boolean mandatory;

  private URI uri;

  /**
   * A Join represents a {@link Property} pair, where {@link #sourceProperty} is in
   * {@link Reference#source} entity, and {@link #targetProperty} is in {@link Reference#target}
   * entity. Their value type (V) should be identical.
   * 
   * @author Attila Mate
   *
   * @param <V>
   */
  public static class Join<V extends Comparable<V>> {

    private Property<V> sourceProperty;
    private Property<V> targetProperty;

    public Join(Property<V> sourceProperty, Property<V> targetProperty) {
      if (!(sourceProperty instanceof PropertyOwned)) {
        throw new RuntimeException("Reference.sourceProperty should be PropertyOwned");
      }
      if (!(targetProperty instanceof PropertyOwned)) {
        throw new RuntimeException("Reference.targetProperty should be PropertyOwned");
      }
      this.sourceProperty = sourceProperty;
      this.targetProperty = targetProperty;
    }

    public Property<V> getSourceProperty() {
      return sourceProperty;
    }

    public Property<V> getTargetProperty() {
      return targetProperty;
    }

    public Expression2Operand<V> eq(DataRow row) {
      return sourceProperty.eq(row.get(targetProperty));
    }

    public Expression2Operand<V> eq(List<Reference<?, ?>> joinPath, DataRow row) {
      return propRef(joinPath).eq(row.get(targetProperty));
    }

    public ExpressionIn<V> inDetail(Collection<? extends DataRow> rows) {
      Set<V> values = rows.stream().map(row -> row.get(targetProperty)).collect(Collectors.toSet());
      return sourceProperty.in(values);
    }

    public ExpressionIn<V> inMaster(Collection<? extends DataRow> rows) {
      Set<V> values = rows.stream().map(row -> row.get(sourceProperty)).collect(Collectors.toSet());
      return targetProperty.in(values);
    }

    public ExpressionIn<V> in(List<Reference<?, ?>> joinPath, Collection<? extends DataRow> rows) {
      Set<V> values = rows.stream().map(row -> row.get(targetProperty)).collect(Collectors.toSet());
      return propRef(joinPath).in(values);
    }

    @SuppressWarnings("unchecked")
    public PropertyRef<V> propRef(List<Reference<?, ?>> joinPath) {
      EntityDefinition sourceEntity = joinPath.get(0).getSource();
      Property<?> existingProperty =
          sourceEntity.findOrCreateReferredProperty(joinPath, targetProperty);
      return (PropertyRef<V>) existingProperty;
    }

  }

  /**
   * The join between the properties of the source and the target.
   */
  List<Join<?>> joins = new ArrayList<>();

  /**
   * Constructing a new reference between two entity.
   * 
   * @param source The source entity definition.
   * @param sourceRole The name of the source.
   * @param name The name of the reference.
   * @param target The target (referred) entity definition
   * @param targetRole The name of the target.
   */
  public Reference(S source, T target, String name) {
    super();
    this.source = source;
    this.target = target;
    this.name = name;
    this.uri = createUri();
  }

  private URI createUri() {
    return EntityUris.createReferenceUri(source.getDomain(), source.entityDefName(), name);
  }

  /**
   * Add a new join to the given reference. The join is a mapping between a source and target
   * property.
   * 
   * @param <V>
   * @param sourceProperty
   * @param targetProperty
   */
  public <V extends Comparable<V>> void addJoin(Property<V> sourceProperty,
      Property<V> targetProperty) {
    Join<V> join = new Join<>(sourceProperty, targetProperty);
    joins.add(join);
  }

  /**
   * The referrer that is the source node in the graph defined by these edges.
   * 
   * @return
   */
  public S getSource() {
    return source;
  }

  /**
   * The referred {@link EntityDefinition} that is the target of the directed edge defined by this
   * reference.
   * 
   * @return
   */
  public T getTarget() {
    return target;
  }

  /**
   * The join conditions that are the pairs.
   * 
   * @return
   */
  public List<Join<?>> joins() {
    return joins;
  }

  /**
   * Constructs the condition for querying the referred rows by the source rows.
   * 
   * @param detailRows The source rows.
   * @return The Expression that is typically an IN with the values from the source properties.
   */
  public Expression joinMaster(Collection<? extends DataRow> detailRows) {
    Assert.isTrue(joins().size() == 1,
        "The " + toString() + " reference must have only one join condition");
    Join<?> join = joins().get(0);
    return join.inMaster(detailRows);
  }

  /**
   * Constructs the condition for querying the source rows by the target rows.
   * 
   * @param targetRows The target rows.
   * @return The Expression that is typically an IN with the values from the target properties.
   */
  public Expression joinDetail(Collection<? extends DataRow> targetRows) {
    Assert.isTrue(joins().size() == 1,
        "The " + toString() + " reference must have only one join condition");
    Join<?> join = joins().get(0);
    return join.inDetail(targetRows);
  }

  /**
   * The name of the reference that must be unique inside the source entity.
   * 
   * @return
   */
  public final String getName() {
    return name;
  }

  @SuppressWarnings("unchecked")
  public <E extends T> E createProxy(Class<E> clazz) {
    return (E) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {clazz},
        new ReferenceInvocationHandler(this));
  }

  /**
   * If the {@link #mandatory} field is empty then calculates it's value from the source properties
   * of the joins.
   */
  private final void computeMandatory() {
    if (mandatory == null) {
      for (Join<? extends Comparable<?>> join : joins) {
        if (join.getSourceProperty().isMandatory()) {
          mandatory = Boolean.TRUE;
          return;
        }
      }
      mandatory = Boolean.FALSE;
    }
  }

  /**
   * @return If the reference is mandatory then there must be a referred record in the target entity
   *         for every record in the source.
   */
  public Boolean isMandatory() {
    computeMandatory();
    return mandatory;
  }

  /**
   * Setup time we can modify the mandatory property of the reference if and only if we set it true!
   * We cann't weaken the original value.
   * 
   * @param mandatory If we try to set false but it's originally true then this function doesn't
   *        change anything.
   */
  public void setMandatory(boolean mandatory) {
    computeMandatory();
    if (!this.mandatory && mandatory) {
      // We can modify the reference to be stricter than the original.
      this.mandatory = Boolean.TRUE;
    }
  }

  public URI getUri() {
    return uri;
  }

}
