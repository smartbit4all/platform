package org.smartbit4all.domain.service.modify;

import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public class DeleteInput<E extends EntityDefinition> extends GenericModifyInput<E> {

  public DeleteInput(TableData<E> tableData) {
    super(tableData);
  }

}
