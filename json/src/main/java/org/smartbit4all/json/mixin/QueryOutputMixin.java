package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class QueryOutputMixin<E extends EntityDefinition> {

  @JsonProperty
  TableData<E> tableData;

  @JsonProperty
  EntityDefinition entityDef;

  @JsonCreator
  public QueryOutputMixin(@JsonProperty("tableData") TableData<E> tableData) {}
}
