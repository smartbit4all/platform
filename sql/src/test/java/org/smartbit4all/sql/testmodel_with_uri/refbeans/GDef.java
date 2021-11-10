package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(GDef.ENTITY_NAME)
@Table(GDef.TABLE_NAME)
public interface GDef extends EntityDefinition {

  public static final String ENTITY_NAME = "gDef";
  public static final String TABLE_NAME = "G";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  public static final String UID = "uid";
  public static final String UID_COL = "UID";
  public static final String AF1 = "af1";
  public static final String AF1_COL = "AF1";
  public static final String BF1 = "bf1";
  public static final String BF1_COL = "BF1";
  public static final String AID = "aId";
  public static final String AID_COL = "A_ID";
  public static final String BID = "bId";
  public static final String BID_COL = "B_ID";


  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<Long> id();

  @OwnProperty(name = UID, columnName = UID_COL)
  Property<String> uid();

  @OwnProperty(name = AF1, columnName = AF1_COL)
  Property<String> af1();

  @OwnProperty(name = BF1, columnName = BF1_COL)
  Property<String> bf1();

  @OwnProperty(name = AID, columnName = AID_COL)
  Property<Long> aId();

  @ReferenceEntity
  @Join(source = AID, target = ADef.ID)
  ADef aDef();

  @OwnProperty(name = BID, columnName = BID_COL)
  Property<Long> bId();

  @ReferenceEntity
  @Join(source = BID, target = BDef.ID)
  BDef bDef();

}
