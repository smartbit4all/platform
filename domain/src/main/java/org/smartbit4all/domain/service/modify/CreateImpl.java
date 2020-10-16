package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4FunctionImpl;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

public abstract class CreateImpl<E extends EntityDefinition>
    extends SB4FunctionImpl<CreateInput<E>, CreateOutput<E>> implements Create<E> {

  /**
   * The {@link EntityDefinition} that we running on.
   */
  protected E entityDef;

  @Override
  public Create<E> values(TableData<E> data) {
    this.input = new CreateInput<>(data);
    return this;
  }

}
