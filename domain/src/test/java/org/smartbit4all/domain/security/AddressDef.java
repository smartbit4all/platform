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

  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  PropertyOwned<Long> id();

  @OwnProperty(name = ZIPCODE, columnName = ZIPCODE_COL)
  Property<String> zipcode();

  @OwnProperty(name = CITY, columnName = CITY_COL)
  Property<String> city();

  @OwnProperty(name = ADDRESS, columnName = ADDRESS_COL)
  Property<String> address();

}
