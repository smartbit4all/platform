package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.smartbit4all.domain.meta.Property;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface OwnProperty {

  /**
   * Explicit name of the property. If not specified, the default is the method name of the
   * {@link Property} getter.
   */
  String name() default "";

  /**
   * The name as the property is stored in the database
   */
  String columnName();
  
}
