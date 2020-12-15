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
package org.smartbit4all.domain.data.retrieve;

import org.smartbit4all.domain.data.DataReference;
import org.smartbit4all.domain.service.retrieve.RetrieveExecutionEdge;

/**
 * This edge connects two node of the retrieval execution. It points to a node that is triggered and
 * contains the {@link DataReference} and the direction. If we trigger by the source or by the
 * target.
 * 
 * @author Peter Boros
 *
 */
public class RetrieveExecutionEdgeTableData extends RetrieveExecutionEdge {

  /**
   * The data reference for accessing and mutating the instances of the references.
   */
  DataReference<?, ?> dataReference;

  RetrieveExecutionEdgeTableData(RetrieveExecutionNodeTableData node,
      DataReference<?, ?> dataReference, boolean forward) {
    super(node, dataReference.getReferenceDef(), forward);
    this.dataReference = dataReference;
  }

}
