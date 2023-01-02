package org.smartbit4all.api.view.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This method will be called before closing the view. It should receive a viewUuid, and return a
 * boolean, indicating if the close can proceed. If returns false, view will not be closed.
 * 
 * @author matea
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface BeforeClose {
}
