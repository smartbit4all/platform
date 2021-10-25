package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(CDef.ENTITY_NAME)
@Table(CDef.TABLE_NAME)
public interface CDef extends EntityDefinition {

  public static final String ENTITY_NAME = "cDef";
  public static final String TABLE_NAME = "C";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String UID = "uid";
  public static final String UID_COL = "UID";
  public static final String CF1 = "cf1";
  public static final String CF1_COL = "CF1";
  public static final String CF2 = "cf2";
  public static final String CF2_COL = "CF2";
  public static final String CF3 = "cf3";
  public static final String CF3_COL = "CF3";
  public static final String FID = "fId";
  public static final String FID_COL = "F_ID";


  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<Long> id();

  @OwnProperty(name = UID, columnName = UID_COL)
  Property<String> uid();


  @OwnProperty(name = CF1, columnName = CF1_COL)
  Property<String> cf1();

  @OwnProperty(name = CF2, columnName = CF2_COL)
  Property<String> cf2();

  @OwnProperty(name = CF3, columnName = CF3_COL)
  Property<String> cf3();

  @OwnProperty(name = FID, columnName = FID_COL)
  Property<Long> fId();

  @ReferenceEntity
  @Join(source = FID, target = FDef.ID)
  FDef fDef();

}
