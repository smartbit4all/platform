package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(EDef.ENTITY_NAME)
@Table(EDef.TABLE_NAME)
public interface EDef extends EntityDefinition {

  public static final String ENTITY_NAME = "eDef";
  public static final String TABLE_NAME = "E";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String UID = "uid";
  public static final String UID_COL = "UID";
  public static final String EF1 = "ef1";
  public static final String EF1_COL = "EF1";
  public static final String EF2 = "ef2";
  public static final String EF2_COL = "EF2";


  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<Long> id();

  @OwnProperty(name = UID, columnName = UID_COL)
  Property<String> uid();


  @OwnProperty(name = EF1, columnName = EF1_COL)
  Property<String> ef1();

  @OwnProperty(name = EF2, columnName = EF2_COL)
  Property<String> ef2();


}
