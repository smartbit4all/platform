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
import java.time.OffsetDateTime;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

/**
 * This storage table contains the object versions of the application. The versioned objects
 * reserves all of their versions. So every modification produces a new version.
 */
@Entity(ObjectVersionDef.ENTITY_NAME)
@Table(ObjectVersionDef.TABLE_NAME)
public interface ObjectVersionDef extends EntityDefinition {

  String ENTITY_NAME = "ObjectVersionDef";
  String TABLE_NAME = "OBJECT_VERSION";

  String ENTRY_ID = "entryid";
  String ENTRY_ID_COL = "ENTRY_ID";

  String VERSION = "version";
  String VERSION_COL = "VERSION";

  String TRANSACTION_ID = "transactionId";
  String TRANSACTION_ID_COL = "TRANSACTION_ID";

  String CREATED_AT = "createdAt";
  String CREATED_AT_COL = "CREATED_AT";

  String CREATED_BY_URI = "createdByUri";
  String CREATED_BY_URI_COL = "CREATEDBYURI";

  String CREATED_BY = "createdBy";
  String CREATED_BY_COL = "CREATEDBY";

  String OPERATION = "operation";
  String OPERATION_COL = "OPERATION";

  String REBASED_FROM_URI = "rebasedFromUri";
  String REBASED_FROM_URI_COL = "REBASEDFROMURI";

  String COMMON_ANCESTOR_URI = "commonAncestorUri";
  String COMMON_ANCESTOR_URI_COL = "COMMONANCESTORURI";

  String MERGED_WITH_URI = "mergedWithUri";
  String MERGED_WITH_URI_COL = "MERGEDWITHURI";


  String OBJECT_CONTENT = "objectcontent";
  String OBJECT_CONTENT_COL = "OBJECT_CONTENT";

  @OwnProperty(name = ENTRY_ID, columnName = ENTRY_ID_COL, mandatory = true)
  Property<Long> entryId();

  @OwnProperty(name = VERSION, columnName = VERSION_COL, mandatory = true)
  Property<Long> version();

  @OwnProperty(name = TRANSACTION_ID, columnName = TRANSACTION_ID_COL, mandatory = false)
  Property<String> transactionId();

  @OwnProperty(name = CREATED_AT, columnName = CREATED_AT_COL, mandatory = true)
  Property<OffsetDateTime> createdAt();

  @OwnProperty(name = CREATED_BY_URI, columnName = CREATED_AT_COL, mandatory = false)
  Property<URI> createdByUri();

  @OwnProperty(name = CREATED_BY, columnName = CREATED_BY, mandatory = false)
  Property<String> createdBy();

  @OwnProperty(name = OPERATION, columnName = OPERATION_COL, mandatory = false)
  Property<String> operation();

  @OwnProperty(name = REBASED_FROM_URI, columnName = REBASED_FROM_URI_COL, mandatory = false)
  Property<URI> rebasedFromUri();

  @OwnProperty(name = COMMON_ANCESTOR_URI, columnName = COMMON_ANCESTOR_URI_COL, mandatory = false)
  Property<URI> commonAncestorUri();

  @OwnProperty(name = MERGED_WITH_URI, columnName = MERGED_WITH_URI_COL, mandatory = false)
  Property<URI> mergedWithUri();

  @OwnProperty(name = OBJECT_CONTENT, columnName = OBJECT_CONTENT_COL, mandatory = true)
  Property<BinaryData> objectContent();

  @ReferenceEntity
  @Join(source = ENTRY_ID, target = ObjectEntryDef.ID)
  ObjectEntryDef objectEntry();

}
