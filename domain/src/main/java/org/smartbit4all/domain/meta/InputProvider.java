package org.smartbit4all.domain.meta;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * We can annotate the given field in a {@link EventHandler} that it's an input provider for us.
 * 
 * @author Peter Boros
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface InputProvider {

}
