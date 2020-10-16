package org.smartbit4all.domain.security;

import org.smartbit4all.domain.annotation.property.ComputedProperty;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Ref;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.ReferenceProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.Reference;

@Entity(UserAccountDef.ENTITY_NAME)
@Table(UserAccountDef.TABLE_NAME)
public interface UserAccountDef extends EntityDefinition {

  public static final String ENTITY_NAME = "userAccountDef";
  public static final String TABLE_NAME = "USER_ACCOUNT";

  public static final String ID = "id";
  public static final String ID_COL = "ID";

  public static final String FULLNAME = "fullname";
  public static final String FULLNAME_COL = "FULLNAME";

  public static final String LASTNAME = "lastname";
  public static final String LASTNAME_COL = "LASTNAME";

  public static final String FIRSTNAME = "firstname";
  public static final String FIRSTNAME_COL = "FIRSTNAME";

  public static final String NAME = "name";
  public static final String NAME_COL = "NAME";

  public static final String PRIMARYADDRESS_ID = "primaryAddressId";
  public static final String PRIMARYADDRESS_ID_COL = "PRIMARYADDRESS_ID";

  public static final String TITLE_CODE = "titleCode";
  public static final String TITLE_CODE_COL = "TITLE_CODE";

  public static final String TITLE = "title";
  public static final String TITLE_COL = "TITLE";

  public static final String PRIMARYADDRESS_REF = "primaryAddressRef";

  public static final String PRIMARYZIPCODE = "primaryZipcode";


  @Id
  @OwnProperty(columnName = ID_COL)
  PropertyOwned<Long> id();

  @OwnProperty(columnName = NAME_COL, name = NAME)
  Property<String> name();

  @OwnProperty(columnName = FIRSTNAME, name = FIRSTNAME)
  Property<String> firstname();

  @OwnProperty(columnName = LASTNAME, name = LASTNAME)
  Property<String> lastname();

  @OwnProperty(columnName = PRIMARYADDRESS_ID, name = PRIMARYADDRESS_ID)
  Property<Long> primaryAddressId();

  @OwnProperty(columnName = TITLE_CODE, name = TITLE_CODE)
  Property<String> titleCode();

  @ComputedProperty(name = TITLE, implementation = UserAccountFullName.class)
  Property<String> title();

  @ReferenceProperty(name = PRIMARYZIPCODE, path = PRIMARYADDRESS_REF,
      property = AddressDef.ZIPCODE)
  Property<String> primaryZipcode();

  /**
   * This is computed from {@link #firstname()} and {@link #lastname()}.
   * 
   * @return
   */
  @ComputedProperty(name = FULLNAME, implementation = UserTitle.class)
  Property<String> fullname();

  @ReferenceEntity(PRIMARYADDRESS_REF)
  // @ReferenceEntity(name = "primaryAddress", referenceName = PRIMARYADDRESS_REF) // TODO name vs.
  // // referenceName
  @Join(source = PRIMARYADDRESS_ID, target = AddressDef.ID)
  // @Join(source = TITLE_CODE, targetLiteral = "RED")
  // @Join(sourceLiteral = "GREEN", target = AddressDef.CITY)
  AddressDef primaryAddress();

  @Ref(PRIMARYADDRESS_REF)
  Reference<UserAccountDef, AddressDef> primaryAddressRef();

}
