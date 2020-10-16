package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
public @interface ValueComparator {

  Class<? extends Comparator<?>> comparator();
  
}
