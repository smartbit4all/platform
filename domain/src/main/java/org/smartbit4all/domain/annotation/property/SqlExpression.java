package org.smartbit4all.domain.annotation.property;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.smartbit4all.domain.utility.SupportedDatabase;

@Retention(RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(SqlExpressions.class)
public @interface SqlExpression {

  SupportedDatabase dialect();

  String expression();
}
