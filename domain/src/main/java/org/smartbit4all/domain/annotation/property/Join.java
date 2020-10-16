package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Joins.class)
public @interface Join {

  /**
   * Name of the source property.
   * 
   * @return
   */
  String source();

  /**
   * Name of the target property. If not specified, defaults to target entity's primary key.
   * 
   * @return
   */
  String target() default "";

  /**
   * TODO do we need this? To create join with a constant / dbExpression?
   * 
   * @return
   */
  String targetLiteral() default "";
}
