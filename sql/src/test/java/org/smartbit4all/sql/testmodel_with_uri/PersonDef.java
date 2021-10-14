package org.smartbit4all.sql.testmodel_with_uri;

import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;

@Entity(PersonDef.ENTITY_NAME)
@Table(PersonDef.TABLE_NAME)
public interface PersonDef extends EntityDefinition {

  public static final String ENTITY_NAME = "personDef";
  public static final String TABLE_NAME = "PERSON";

  public static final String ID = "id";
  public static final String ID_COL = "ID";

  public static final String NAME = "name";
  public static final String NAME_COL = "NAME";


  @Id
  @OwnProperty(name = ID, columnName = ID_COL)
  Property<String> id();

  @OwnProperty(name = NAME, columnName = NAME_COL)
  Property<String> name();

}
