/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.api.query;

import org.smartbit4all.core.SB4Function;

/**
 * This is the main API object of the {@link QueryApi}. It manages the execution graph of the given
 * request. It consists of different {@link SB4Function}s that represents series of action to be
 * done. The functions can be:
 * <ul>
 * <li>Query - a query that is prepared for the execution. The result of the query will be available
 * during the whole execution. If we have a simple query then there is only one query in the plan
 * and the result is the result of the whole execution.</li>
 * <li>Load query result into named set - an action that is responsible for extracting and saving a
 * column from a query result. This action is a typical follow up for the sub queries.</li>
 * <li>Initiate named set - an action that fills a named set with a list of values already known at
 * the moment of preparation. Typically the IN value list.</li>
 * <li>Stored named set - an action that is responsible for saving its values into a temporary store
 * (like a temporary table in RDBMS). It has a list of value for this. It can be executed against a
 * given storage. So if we have a named set then we might have to activate it into different
 * storages! So don't forget to have the storage Api also not only the activation data itself.</li>
 * </ul>
 * 
 * <p>
 * If we a have the following query then the execution plan looks like this:
 * <code>CustomerDef: vip = true and firstName in ('Joe', 'John'...) AND 
 *              EXISTS(AddressDef.customer(), zipcode IN('2030', '2040', '5000'...) AND 
 *                                              type IN ('utca', sétány', 'köz' ...)) </code>
 * 
 * <pre>
 *                                                                      -- ACT('SYS001') <- LOAD('SYS001', ('2030', '2040', '5000'...))
 *                                                                     |
 *                  <- ACT <- LOAD('CUSTOMERID1') <- QRY(ADDRESS1) -- +            
 *                  |                                                  |
 * QRY(CUSTOMER1) - +                                                   -- ACT('SYS002') <- LOAD('SYS002', ('utca', sétány', 'köz' ...))
 *                  |
 *                  |
 *                  |
 *                  <- ACT( 'SYS003' ) <- LOAD('SYS003', ('Joe', 'John'...))
 * </pre>
 * </p>
 * The execution starts from the leafs. Until the junction points the paths are parallel
 * executables. In this case we can load the constant IN lists into a named set. Next activating
 * these into the database. After this we can execute the query for the address using the SYS001 and
 * the SYS002 temporary sets. (They are already included into the query during the preparation
 * phase.) The customer id result of the query is loaded
 * 
 * @author Peter Boros
 */
public class QueryExecutionPlan {

}
