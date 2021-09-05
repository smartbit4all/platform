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
package org.smartbit4all.domain.security;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyOwned;

@Entity(AddressDef.ENTITY_NAME)
@Table(AddressDef.TABLE_NAME)
public interface AddressDef extends EntityDefinition {

  public static final String ENTITY_NAME = "addressDef";
  public static final String TABLE_NAME = "ADDRESS";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String ZIPCODE = "zipcode";
  public static final String ZIPCODE_COL = "ZIPCODE";
  public static final String CITY = "city";
  public static final String CITY_COL = "CITY";
  public static final String ADDRESS = "address";
  public static final String ADDRESS_COL = "ADDRESS";
  public static final String DONT_REF_THIS_BY_URI = "dontRefThisByUri";
  public static final String DONT_REF_THIS_BY_URI_COL = "DONT_REF_THIS_BY_URI";
  public static final String DONT_REF_THIS_BY_REF = "dontRefThisByRef";
  public static final String DONT_REF_THIS_BY_REF_COL = "DONT_REF_THIS_BY_REF";

  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  PropertyOwned<Long> id();

  @OwnProperty(name = ZIPCODE, columnName = ZIPCODE_COL)
  Property<String> zipcode();

  @OwnProperty(name = CITY, columnName = CITY_COL)
  Property<String> city();

  @OwnProperty(name = ADDRESS, columnName = ADDRESS_COL)
  Property<String> address();
  
  @OwnProperty(name = DONT_REF_THIS_BY_URI, columnName = DONT_REF_THIS_BY_URI_COL)
  Property<String> dontRefThisByUri();
  
  @OwnProperty(name = DONT_REF_THIS_BY_REF, columnName = DONT_REF_THIS_BY_REF_COL)
  Property<String> dontRefThisByRef();

}
