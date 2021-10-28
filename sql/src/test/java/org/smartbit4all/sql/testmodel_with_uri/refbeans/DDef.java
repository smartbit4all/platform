package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(DDef.ENTITY_NAME)
@Table(DDef.TABLE_NAME)
public interface DDef extends EntityDefinition {

  public static final String ENTITY_NAME = "Def";
  public static final String TABLE_NAME = "D";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String UID = "uid";
  public static final String UID_COL = "UID";
  public static final String DF1 = "df1";
  public static final String DF1_COL = "DF1";
  public static final String DF2 = "df2";
  public static final String DF2_COL = "DF2";
  public static final String CID = "cId";
  public static final String CID_COL = "C_ID";
  public static final String EID = "eId";
  public static final String EID_COL = "E_ID";


  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<Long> id();

  @OwnProperty(name = UID, columnName = UID_COL)
  Property<String> uid();


  @OwnProperty(name = DF1, columnName = DF1_COL)
  Property<String> df1();

  @OwnProperty(name = DF2, columnName = DF2_COL)
  Property<String> df2();

  @OwnProperty(name = CID, columnName = CID_COL)
  Property<Long> cId();

  @ReferenceEntity
  @Join(source = CID, target = DDef.ID)
  DDef cDef();

  @OwnProperty(name = EID, columnName = EID_COL)
  Property<Long> eId();

  @ReferenceEntity
  @Join(source = EID, target = EDef.ID)
  EDef eDef();

}
