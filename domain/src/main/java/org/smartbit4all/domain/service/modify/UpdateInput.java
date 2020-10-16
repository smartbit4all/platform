package org.smartbit4all.domain.service.modify;

import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public class UpdateInput<E extends EntityDefinition> extends GenericModifyInput<E> {

  public UpdateInput(TableData<E> tableData) {
    super(tableData);
  }

}
