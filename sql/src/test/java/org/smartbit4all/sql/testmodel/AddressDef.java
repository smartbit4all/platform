package org.smartbit4all.sql.testmodel;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(AddressDef.ENTITY_NAME)
@Table(AddressDef.TABLE_NAME)
public interface AddressDef extends EntityDefinition {

  public static final String ENTITY_NAME = "addressDef";
  public static final String TABLE_NAME = "ADDRESS";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String ZIP = "zip";
  public static final String ZIP_COL = "ZIP";
  public static final String CITY = "city";
  public static final String CITY_COL = "CITY";
  public static final String PERSON_ID = "personId";
  public static final String PERSON_COL = "PERSON_ID";

  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<Long> id();

  @OwnProperty(name = ZIP, columnName = ZIP_COL)
  Property<String> zip();

  @OwnProperty(name = CITY, columnName = CITY_COL)
  Property<String> city();
  
  @OwnProperty(name = PERSON_ID, columnName = PERSON_COL)
  Property<Long> personId();
  
  @ReferenceEntity
  @Join(source = PERSON_ID, target = PersonDef.ID)
  PersonDef person();

}