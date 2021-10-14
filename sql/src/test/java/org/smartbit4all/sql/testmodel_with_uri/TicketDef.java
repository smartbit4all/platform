package org.smartbit4all.sql.testmodel_with_uri;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(TicketDef.ENTITY_NAME)
@Table(TicketDef.TABLE_NAME)
public interface TicketDef extends EntityDefinition {

  public static final String ENTITY_NAME = "ticketDef";
  public static final String TABLE_NAME = "TICKET";

  public static final String ID = "id";
  public static final String ID_COL = "ID";

  public static final String URI = "uri";
  public static final String URI_COL = "URI";

  public static final String TITLE = "title";
  public static final String TITLE_COL = "TITLE";

  public static final String PRIMARYPERSONID = "primaryPersonId";
  public static final String PRIMARYPERSONID_COL = "PRIMARY_PERSON_ID";

  public static final String SECONDARYPERSONID = "secondaryPersonId";
  public static final String SECONDARYPERSONID_COL = "SECONDARY_PERSON_ID";

  public static final String PARENTID = "parentUid";
  public static final String PARENTID_COL = "PARENT_ID";

  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<String> id();

  @OwnProperty(name = URI, columnName = URI_COL)
  Property<String> uri();

  @OwnProperty(name = TITLE, columnName = TITLE_COL)
  Property<String> title();

  @OwnProperty(name = PRIMARYPERSONID, columnName = PRIMARYPERSONID_COL)
  Property<String> primaryPersonId();

  @OwnProperty(name = SECONDARYPERSONID, columnName = SECONDARYPERSONID_COL)
  Property<String> secondaryPersonId();

  @OwnProperty(name = PARENTID, columnName = PARENTID_COL)
  Property<String> parentUid();

  @ReferenceEntity
  @Join(source = PRIMARYPERSONID, target = PersonDef.ID)
  PersonDef primaryPerson();

  @ReferenceEntity
  @Join(source = SECONDARYPERSONID, target = PersonDef.ID)
  PersonDef secondaryPerson();

  @ReferenceEntity
  @Join(source = PARENTID, target = TicketDef.ID)
  TicketDef parent();

}
