package org.smartbit4all.domain.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.smartbit4all.core.utility.ListBasedMap;
import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterHelper;
import org.smartbit4all.domain.meta.logic.Avg;
import org.smartbit4all.domain.meta.logic.Count;
import org.smartbit4all.domain.meta.logic.Max;
import org.smartbit4all.domain.meta.logic.Min;
import org.smartbit4all.domain.meta.logic.Sum;

/**
 * This property is computed by an algorithm represented by a {@link EventHandler}. For the
 * computation there are other necessary properties to include.
 * 
 * @author Peter Boros
 *
 * @param <T>
 */
public class PropertyComputed<T> extends Property<T> {

  /**
   * At setup time we identify if the given computation is a basic one handled at platform level.
   * These one are more or less the SQL level supported ones.
   * 
   * @author Peter Boros
   */
  public static enum BasicComputation {

    COUNT, MIN, MAX, AVG, SUM, NONE

  }

  private static final Map<Class<? extends EventHandler>, BasicComputation> basicComputationMap =
      new ListBasedMap<>();

  static {
    basicComputationMap.put(Count.class, BasicComputation.COUNT);
    basicComputationMap.put(Min.class, BasicComputation.MIN);
    basicComputationMap.put(Max.class, BasicComputation.MAX);
    basicComputationMap.put(Avg.class, BasicComputation.AVG);
    basicComputationMap.put(Sum.class, BasicComputation.SUM);
  }

  /**
   * The computation logic definition responsible for computing the value of this property. If this
   * property is added to a data representation then the logic will be added automatically. The
   * logic itself is always instantiated at the given place. So the same computation logic can have
   * more then one implementation depending on the context that we currently have.
   */
  private Class<? extends EventHandler> logic;

  /**
   * At setup time we identify if the given computation is a basic one handled at platform level.
   * These one are more or less the SQL level supported ones.
   */
  private BasicComputation basicType = BasicComputation.NONE;

  /**
   * In some cases the required properties are filled by the dependencies of the {@link #logic} set
   * for the given property. But at runtime when we use this cache to get the required properties
   * faster.
   */
  private List<Property<?>> requiredProperties = new ArrayList<>(3);

  /**
   * Constructs a computed property.
   * 
   * @param name
   * @param type
   * @param logic
   */
  public PropertyComputed(String name, Class<T> type, Class<? extends EventHandler> logic) {
    this(name, type, null, logic);
  }

  public PropertyComputed(String name, Class<T> type, JDBCDataConverter<T, ?> jdbcConverter,
      Class<? extends EventHandler> logic) {
    super(name, type, jdbcConverter);
    this.logic = logic;
    BasicComputation basicComputation = basicComputationMap.get(logic);
    if (basicComputation != null) {
      basicType = basicComputation;
    }
  }

  /**
   * The interface class of the logic that calculates the given property.
   * 
   * @return
   */
  public Class<? extends EventHandler> getLogic() {
    return logic;
  }

  /**
   * In some cases the required properties are filled by the dependencies of the {@link #logic} set
   * for the given property. But at runtime when we use this cache to get the required properties
   * 
   * @return
   */
  public List<Property<?>> getRequiredProperties() {
    return requiredProperties;
  }

  /**
   * Add a required property manually. It can be useful to add the evident dependencies in case of a
   * dynamic computation. We heavily use it to add the required properties in case of by definition
   * logics like {@link Sum}, {@link Avg} and so on.
   */
  public void addRequired(Property<?> required) {
    if (requiredProperties.contains(required)) {
      return;
    }
    requiredProperties.add(required);
  }

  /**
   * At setup time we identify if the given computation is a basic one handled at platform level.
   * These one are more or less the SQL level supported ones.
   * 
   * @return If {@link BasicComputation#NONE} then we have specific computation.
   */
  public BasicComputation getBasicType() {
    return basicType;
  }

  public static <T> PropertyComputed<T> create(String name, Class<T> type,
      JDBCDataConverterHelper jdbcDataConverterHelper, Class<? extends EventHandler> logic) {
    return new PropertyComputed<T>(name, type, jdbcDataConverterHelper.from(type), logic);
  }
}
