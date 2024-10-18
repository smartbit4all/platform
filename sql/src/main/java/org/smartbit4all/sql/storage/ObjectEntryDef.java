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

import java.net.URI;
import java.time.LocalDateTime;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

/**
 * This storage table contains the object instances of the application. Every object is identified
 * by its URI that is computed as a hierachical unique identifier in any softver componenets. On the
 * other hand the table also has a primary key that is a numeric value coming from a database
 * sequence. The URI decomposed into the columns of the table:
 * [scheme]:/[className]/[YYYY/MM/DD/HH/MI]/[uuid]
 */
@Entity(ObjectEntryDef.ENTITY_NAME)
@Table(ObjectEntryDef.TABLE_NAME)
public interface ObjectEntryDef extends EntityDefinition {

  String ENTITY_NAME = "ObjectEntryDef";
  String TABLE_NAME = "OBJECT_ENTRY";

  String URI = "uri";
  String URI_COL = "URI";

  String ID = "id";
  String ID_COL = "ID";

  String SCHEME = "scheme";
  String SCHEME_COL = "SCHEME";

  String CLASSNAME = "classname";
  String CLASSNAME_COL = "CLASSNAME";

  String CREATED_AT = "createdAt";
  String CREATED_AT_COL = "CREATED_AT";

  String MODIFIED_AT = "modifiedAt";
  String MODIFIED_AT_COL = "MODIFIED_AT";

  String UUID = "uuid";
  String UUID_COL = "UUID";

  String VERSION = "version";
  String VERSION_COL = "VERSION";

  String SINGLEVERSION = "singleVersion";
  String SINGLEVERSION_COL = "SINGLEVERSION";

  @OwnProperty(name = URI, columnName = URI_COL, mandatory = true)
  Property<URI> uri();

  @OwnProperty(name = ID, columnName = ID_COL, mandatory = true)
  @Id
  Property<Long> id();

  @OwnProperty(name = SCHEME, columnName = SCHEME_COL, mandatory = true)
  Property<String> scheme();

  @OwnProperty(name = CLASSNAME, columnName = CLASSNAME_COL, mandatory = true)
  Property<String> className();

  @OwnProperty(name = CREATED_AT, columnName = CREATED_AT_COL, mandatory = true)
  Property<LocalDateTime> createdAt();

  @OwnProperty(name = MODIFIED_AT, columnName = MODIFIED_AT_COL, mandatory = true)
  Property<LocalDateTime> modifiedAt();

  @OwnProperty(name = UUID, columnName = UUID_COL, mandatory = true)
  Property<String> uuid();

  @OwnProperty(name = VERSION, columnName = VERSION_COL, mandatory = true)
  Property<String> version();

  @OwnProperty(name = SINGLEVERSION, columnName = SINGLEVERSION_COL, mandatory = true)
  Property<Boolean> singleVersion();

}
