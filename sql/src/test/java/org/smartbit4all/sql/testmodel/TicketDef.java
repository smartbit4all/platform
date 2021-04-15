package org.smartbit4all.sql.testmodel;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.Join;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.ReferenceEntity;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.PropertyOwned;

@Entity(TicketDef.ENTITY_NAME)
@Table(TicketDef.TABLE_NAME)
public interface TicketDef extends EntityDefinition {
  
  public static final String ENTITY_NAME = "ticketDef";
  public static final String TABLE_NAME = "TICKET";

  public static final String ID = "id";
  public static final String ID_COL = "ID";
  
  public static final String TITLE = "title";
  public static final String TITLE_COL = "TITLE";
  
  public static final String PRIMARYPERSONID = "primaryPersonId";
  public static final String PRIMARYPERSONID_COL = "PRIMARY_PERSON_ID";
  
  public static final String SECONDARYPERSONID = "secondaryPersonId";
  public static final String SECONDARYPERSONID_COL = "SECONDARY_PERSON_ID";
  
  public static final String PARENTID = "parentId";
  public static final String PARENTID_COL = "PARENT_ID";
  
  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  PropertyOwned<Long> id();
  
  @OwnProperty(name = TITLE, columnName = TITLE_COL)
  PropertyOwned<String> title();
  
  @OwnProperty(name = PRIMARYPERSONID, columnName = PRIMARYPERSONID_COL)
  PropertyOwned<Long> primaryPersonId();
  
  @OwnProperty(name = SECONDARYPERSONID, columnName = SECONDARYPERSONID_COL)
  PropertyOwned<Long> secondaryPersonId();
  
  @OwnProperty(name = PARENTID, columnName = PARENTID_COL)
  PropertyOwned<Long> parentId();
  
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
