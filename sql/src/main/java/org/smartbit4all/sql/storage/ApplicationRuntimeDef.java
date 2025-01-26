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
package org.smartbit4all.sql.storage;

import java.time.OffsetDateTime;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(ApplicationRuntimeDef.ENTITY_NAME)
@Table(ApplicationRuntimeDef.TABLE_NAME)
public interface ApplicationRuntimeDef extends EntityDefinition {

  final String ENTITY_NAME = "ApplicationRuntimeDef";
  final String TABLE_NAME = "ApplicationRuntime";

  String URI = "URI";
  String URI_COL = "URI";

  String UUID = "UUID";
  String UUID_COL = "UUID";

  String BASEURL = "BASEURL";
  String BASEURL_COL = "BASEURL";

  String STARTUPTIME = "STARTUPTIME";
  String STARTUPTIME_COL = "STARTUPTIME";

  String STOPTIME = "STOPTIME";
  String STOPTIME_COL = "STOPTIME";

  String TIMEOFFSET = "TIMEOFFSET";
  String TIMEOFFSET_COL = "TIMEOFFSET";

  String LASTTOUCHTIME = "LASTTOUCHTIME";
  String LASTTOUCHTIME_COL = "LASTTOUCHTIME";

  @OwnProperty(name = URI, columnName = URI_COL)
  @Id
  Property<String> uri();

  @OwnProperty(name = UUID, columnName = UUID_COL)
  Property<String> uuid();

  @OwnProperty(name = BASEURL, columnName = BASEURL_COL)
  Property<String> baseUrl();

  @OwnProperty(name = STARTUPTIME, columnName = STARTUPTIME_COL)
  Property<OffsetDateTime> startupTime();

  @OwnProperty(name = STOPTIME, columnName = STOPTIME_COL)
  Property<OffsetDateTime> stopTime();

  @OwnProperty(name = TIMEOFFSET, columnName = TIMEOFFSET_COL)
  Property<Long> timeOffset();

  @OwnProperty(name = LASTTOUCHTIME, columnName = LASTTOUCHTIME_COL)
  Property<OffsetDateTime> lastTouchTime();

}
