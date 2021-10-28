package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(BDef.ENTITY_NAME)
@Table(BDef.TABLE_NAME)
public interface BDef extends EntityDefinition {

  public static final String ENTITY_NAME = "bDef";
  public static final String TABLE_NAME = "B";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String UID = "uid";
  public static final String UID_COL = "UID";
  public static final String BF1 = "bf1";
  public static final String BF1_COL = "BF1";
  public static final String BF2 = "bf2";
  public static final String BF2_COL = "BF2";
  public static final String CID = "cId";
  public static final String CID_COL = "C_ID";


  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<Long> id();

  @OwnProperty(name = UID, columnName = UID_COL)
  Property<String> uid();


  @OwnProperty(name = BF1, columnName = BF1_COL)
  Property<String> bf1();

  @OwnProperty(name = BF2, columnName = BF2_COL)
  Property<String> bf2();

  @OwnProperty(name = CID, columnName = CID_COL)
  Property<Long> cId();

  @ReferenceEntity
  @Join(source = CID, target = DDef.ID)
  CDef cDef();

}
