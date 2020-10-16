package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public abstract class UpdateImpl<E extends EntityDefinition>
    extends SB4FunctionImpl<UpdateInput<E>, UpdateOutput<E>> implements Update<E> {

  /**
   * The {@link EntityDefinition} that we running on.
   */
  protected E entityDef;

  @Override
  public Update<E> values(TableData<E> data) {
    this.input = new UpdateInput<>(data);
    return this;
  }

}
