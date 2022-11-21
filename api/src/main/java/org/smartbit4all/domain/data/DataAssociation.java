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
package org.smartbit4all.domain.data;

/**
 * This association is contained by an entity data table and belongs to a row. It refers to another
 * {@link TableData} and contains the references for every row of the source entity data
 * table. All the associations are maintained from both direction. So if have a master - detail
 * relation then we will have an assoc direct from the detail to the master and an assoc detail back
 * from the master
 * 
 * This association has the following types:
 * <ul>
 * <li>Direct reference: The source entity refer one row from the target one.</li>
 * <li>Back reference: The source entity is referred by one or more row from the target one.</li>
 * <li>Multiple reference: There is an association entity between the source and the target so there
 * can be zero or more reference between the records of the entities. An association record belong
 * to every association instance</li>
 * </ul>
 * These types are the subclasses of this association.
 * 
 * @author Peter Boros
 *
 */
public class DataAssociation {

  /**
   * A source entity of the association that contains it.
   */
  protected TableData source;

  /**
   * The target entity data table.
   */
  protected TableData target;
  
}
