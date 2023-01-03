package org.smartbit4all.api.view.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.smartbit4all.api.view.bean.View;

/**
 * This method will be called when a view's model is needed but not present yet. It will receive a
 * {@link View} containing all information available to initialize model, and must return the model
 * Object.
 *
 * @author matea
 *
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface InitModel {
}
