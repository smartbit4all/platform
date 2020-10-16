package org.smartbit4all.domain.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation points a property of an entity definition. It doesn't set the name of the
 * property because the property related variable will be set runtime. So this is just a sign that
 * the field will contain some property at runtime.
 * 
 * @author Peter Boros
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyDynamic {

}
