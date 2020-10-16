package org.smartbit4all.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation references a property of an entity definition by name. The String name is a must
 * because we cann't refer to Method or other definition level item. This name will be used to
 * identify the {@link Property} of the {@link EntityDefinition#getProperty(String)} runtime using
 * the appropriate method.
 * 
 * @author Peter Boros
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyWired {

  /**
   * This is the name of the property in the given entity.
   * 
   * @return
   */
  public String value();

}
