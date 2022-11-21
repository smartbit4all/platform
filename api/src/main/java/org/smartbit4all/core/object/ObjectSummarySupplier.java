package org.smartbit4all.core.object;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The domain objects are subject of listing and displaying. In this situation it's important to
 * have a common logic to produce the readable summary of the object. The implementations are
 * collected by the {@link ObjectApi} and we can get the summary supplier through the
 * {@link ObjectDefinition}.
 * 
 * @author Peter Boros
 */
public abstract class ObjectSummarySupplier<T> implements Function<T, String> {

  private static final Logger log = LoggerFactory.getLogger(ObjectSummarySupplier.class);

  /**
   * The default name of the summary supplier. If we add more supplier to the object then use a
   * unique name for the class.
   */
  public static final String DEFAULT = "default";

  /**
   * The default name of the history entry summary supplier.
   */
  public static final String HISTORY = "history";

  /**
   * The class of the bean that is managed by the supplier.
   */
  private Class<T> clazz;

  /**
   * Optionally we can assign a name to the given supplier if we have more than one. The name must
   * be unique for a class. If it's not unique then we get warning while system startup.
   */
  private String name = DEFAULT;

  /**
   * If we create the supplier for a class with a name.
   * 
   * @param clazz
   * @param name
   */
  public ObjectSummarySupplier(Class<T> clazz, String name) {
    super();
    this.clazz = clazz;
    this.name = name;
  }

  public ObjectSummarySupplier(Class<T> clazz) {
    super();
    this.clazz = clazz;
  }

  public final Class<T> getClazz() {
    return clazz;
  }

  public final void setClazz(Class<T> clazz) {
    this.clazz = clazz;
  }

  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  @SuppressWarnings("unchecked")
  public String getSummary(Object o) {
    return apply((T) o);
  }

  public static final String getDefaultSummary(Object o) {
    if (o == null) {
      return StringConstant.MINUS_SIGN;
    }
    BeanMeta meta = null;
    try {
      meta = BeanMetaUtil.meta(o.getClass());
    } catch (Exception e) {
      log.warn("Unable to get meta for " + o.getClass(), e);
    }
    StringBuilder sb = new StringBuilder();
    if (meta != null) {
      for (PropertyMeta propertyMeta : meta.getProperties().values()) {
        if (String.class.equals(propertyMeta.getType())) {
          if (sb.length() > 0) {
            sb.append(StringConstant.COMMA_SPACE);
          }
          sb.append(propertyMeta.getValue(o));
        }
      }

      PropertyMeta uriProperty = meta.getProperties().get("URI");
      if (uriProperty != null) {
        boolean enclose = false;
        if (sb.length() > 0) {
          sb.append(StringConstant.SPACE);
          sb.append(StringConstant.LEFT_PARENTHESIS);
          enclose = true;
        }
        Object value = uriProperty.getValue(o);
        sb.append(value == null ? StringConstant.MINUS_SIGN : value.toString());
        if (enclose) {
          sb.append(StringConstant.RIGHT_PARENTHESIS);
        }
      }
    }
    return sb.length() == 0 ? StringConstant.MINUS_SIGN : sb.toString();
  }

}
