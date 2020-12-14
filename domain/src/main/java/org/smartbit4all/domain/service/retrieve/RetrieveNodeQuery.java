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
package org.smartbit4all.domain.service.retrieve;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.utility.CompositeValue;

/**
 * The request for a retrieve node that contains the list of keys to use during the query. The next
 * round will produce the results and at the end of the round we set the references to complete the
 * operation by adding the new associations. In the mean time we collect the
 * {@link RetrieveExecutionNode#newRecordsToProcess} for the next round.
 * 
 * @author Peter Boros
 */
public abstract class RetrieveNodeQuery implements SB4Function {

  /**
   * The list of keys we have to use to query the next nodes for the node. In case of composite key
   * we must use the {@link CompositeValue} object with the values.
   */
  Set<Object> keyValues = new HashSet<>();

  /**
   * The result records after the query next round. It can be used to merge the result to the source
   * and instantiate all the references they need. The key of the map is the same as in the
   * {@link #keyValues} set. In case of composite key we must use the {@link CompositeValue} object
   * with the values.
   */
  Map<Object, DataRow> results = null;

}
