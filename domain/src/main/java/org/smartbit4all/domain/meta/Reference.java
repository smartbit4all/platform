package org.smartbit4all.domain.meta;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.domain.data.DataRow;

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

    public ExpressionIn<V> in(Collection<? extends DataRow> rows) {
      Set<V> values = rows.stream().map(row -> row.get(targetProperty)).collect(Collectors.toSet());
      return sourceProperty.in(values);
    }

    public ExpressionIn<V> in(List<Reference<?, ?>> joinPath, Collection<? extends DataRow> rows) {
      Set<V> values = rows.stream().map(row -> row.get(targetProperty)).collect(Collectors.toSet());
      return propRef(joinPath).in(values);
    }

    @SuppressWarnings("unchecked")
    public PropertyRef<V> propRef(List<Reference<?, ?>> joinPath) {
      EntityDefinition sourceEntity = joinPath.get(0).getSource();
      Property<?> existingProperty =
          sourceEntity.findOrCreateReferredProperty(joinPath, sourceProperty);
      return (PropertyRef<V>) existingProperty;
    }

  }

  /**
   * The join between the properties of the source and the target. The key is the property of the
   * source and the value is from the target. This is typically one to one mapping so we use this
   * special map to avoid unnecessary map administration.
   */
  ListBasedMap<Property<?>, Property<?>> joins_old = new ListBasedMap<>();

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
   * @param <V>
   * 
   * @return
   */
  public <V extends Comparable<V>> List<Join<?>> joins() {
    return joins;
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

}
