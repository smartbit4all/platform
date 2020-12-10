package org.smartbit4all.domain.service.query;

import org.smartbit4all.core.SB4CompositeFunction;

/**
 * This composite function is a graph of individual queries to be executed as one. We can add
 * queries and they will be merged into the execution graph by their dependencies. The queries are
 * named and therefore we can access the result of a query as a set. The next query can use this in
 * an expression.
 * 
 * In this case the expression is evaluated in two steps. The provider query is executed and the
 * result is saved into the "NamedSetApi"
 * 
 * @author Peter Boros
 */
public interface CompositeQuery extends SB4CompositeFunction<Void, Void> {

}
