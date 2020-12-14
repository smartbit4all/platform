/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
