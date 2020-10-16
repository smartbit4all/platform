package org.smartbit4all.domain.service.modify;

import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public class CreateInput<E extends EntityDefinition> extends GenericModifyInput<E> {

  public CreateInput(TableData<E> tableData) {
    super(tableData);
  }

}
