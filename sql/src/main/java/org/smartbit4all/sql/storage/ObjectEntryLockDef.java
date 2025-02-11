/*******************************************************************************
 * Copyright (C) 2020 - 2022 it4all Hungary Kft.
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
package org.smartbit4all.sql.storage;

import java.time.OffsetDateTime;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

/**
 * This storage table contains the object locks of the application. Every object is identified by
 * its URI that is computed as a hierachical unique identifier in any softver componenets. On the
 * other hand the table also has a primary key that is a numeric value coming from a database
 * sequence. The URI decomposed into the columns of the table:
 * [scheme]:/[className]/[YYYY/MM/DD/HH/MI]/[uuid]
 */
@Entity(ObjectEntryLockDef.ENTITY_NAME)
@Table(ObjectEntryLockDef.TABLE_NAME)
public interface ObjectEntryLockDef extends EntityDefinition {

  String ENTITY_NAME = "ObjectEntryLockDef";
  String TABLE_NAME = "OBJECT_ENTRY_LOCK";

  String OBJECT_URI = "objectUri";
  String OBJECT_URI_COL = "OBJECT_URI";

  String APPLICATIONRUTIME = "applicationRuntime";
  String APPLICATIONRUTIME_COL = "APPLICATIONRUTIME";

  String CREATED_AT = "createdAt";
  String CREATED_AT_COL = "CREATED_AT";


  @OwnProperty(name = OBJECT_URI, columnName = OBJECT_URI_COL, mandatory = true)
  @Id
  Property<String> objectUri();

  /**
   * @return The UUID of the runtime
   */
  @OwnProperty(name = APPLICATIONRUTIME, columnName = APPLICATIONRUTIME_COL,
      mandatory = true)
  Property<String> applicationRuntime();

  @OwnProperty(name = CREATED_AT, columnName = CREATED_AT_COL, mandatory = true)
  Property<OffsetDateTime> createdAt();

}
