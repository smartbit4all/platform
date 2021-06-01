package org.smartbit4all.sql.storage;

import java.net.URI;
import org.smartbit4all.domain.annotation.property.Entity;
import org.smartbit4all.domain.annotation.property.Id;
import org.smartbit4all.domain.annotation.property.OwnProperty;
import org.smartbit4all.domain.annotation.property.Table;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.types.binarydata.BinaryData;

@Entity("test")
@Table("test")
public interface TestSearchDef extends EntityDefinition {

  @Id
  @OwnProperty(name = "KEY", columnName = "KEY")
  Property<URI> key();

  @OwnProperty(name = "STATE", columnName = "STATE")
  Property<String> state();

  @OwnProperty(name = "EMPTYSTATE", columnName = "EMPTYSTATE")
  Property<String> emptyState();

  @OwnProperty(name = "ISACTIVE", columnName = "ISACTIVE")
  Property<Boolean> isActive();

  @OwnProperty(name = "CONTENT", columnName = "CONTENT")
  Property<BinaryData> content();
  
}
