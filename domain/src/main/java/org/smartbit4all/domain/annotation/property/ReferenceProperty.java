package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.smartbit4all.domain.meta.Property;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface ReferenceProperty {

  /**
   * Explicit name of the property. If not specified, the default is the method name of the
   * {@link Property} getter.
   * 
   */
  String name() default "";

  /**
   * Reference names which specify the join path for the referred property.
   * 
   * @return
   */
  String[] path();

  /**
   * The referred property which can be found at the end of the path.
   * 
   * @return
   */
  String property();

}
