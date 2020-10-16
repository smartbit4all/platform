package org.smartbit4all.domain.meta;

import java.lang.reflect.Method;

/**
 * The {@link EntityDefinitionInvocationHandler} use this class structure to cache the executions
 * for the methods it's have. The subclasses are responsible for the different kind of methods.
 * 
 * @author Peter Boros
 */
abstract class EntityDefinitionMethod {

  abstract Object invoke(Object proxy, Method method, Object[] args) throws Throwable;

}
