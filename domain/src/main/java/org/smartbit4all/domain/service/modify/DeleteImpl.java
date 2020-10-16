package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public abstract class DeleteImpl<E extends EntityDefinition>
    extends SB4FunctionImpl<DeleteInput<E>, DeleteOutput<E>> implements Delete<E> {

  /**
   * The {@link EntityDefinition} that we running on.
   */
  protected E entityDef;

  @Override
  public Delete<E> by(TableData<E> data) {
    this.input = new DeleteInput<>(data);
    return this;
  }

}
