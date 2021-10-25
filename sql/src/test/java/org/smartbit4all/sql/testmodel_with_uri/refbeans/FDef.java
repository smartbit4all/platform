package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(FDef.ENTITY_NAME)
@Table(FDef.TABLE_NAME)
public interface FDef extends EntityDefinition {

  public static final String ENTITY_NAME = "fDef";
  public static final String TABLE_NAME = "F";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String UID = "uid";
  public static final String UID_COL = "UID";
  public static final String FF1 = "ff1";
  public static final String FF1_COL = "FF1";
  public static final String FF2 = "ff2";
  public static final String FF2_COL = "FF2";


  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<Long> id();

  @OwnProperty(name = UID, columnName = UID_COL)
  Property<String> uid();

  @OwnProperty(name = FF1, columnName = FF1_COL)
  Property<String> ff1();

  @OwnProperty(name = FF2, columnName = FF2_COL)
  Property<String> ff2();


}
