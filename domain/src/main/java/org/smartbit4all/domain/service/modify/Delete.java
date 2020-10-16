package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * The create is a service responsible for creating new instances with the given values.
 * 
 * @author Peter Boros
 * @param <E>
 */
public interface Delete<E extends EntityDefinition>
    extends SB4Function<DeleteInput<E>, DeleteOutput<E>> {

  /**
   * The modification source that defines the identified records to delete.
   * 
   * @param source
   * @return
   */
  Delete<E> by(TableData<E> data);

  // <B> Delete by(Collection<B> beans);

}
