package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * The create is a service responsible for creating new instances with the given values.
 * 
 * @author Peter Boros
 */
public interface Create<E extends EntityDefinition>
    extends SB4Function<CreateInput<E>, CreateOutput<E>> {

  // TODO into should be TableData result. Maybe result? like insertWithRefresh

  /**
   * Set the values based on a {@link TableData}.
   * 
   * @param tableData The table data with the values for the create (insert) statement.
   * @return Fluid API
   * 
   */
  Create<E> values(TableData<E> data);

  // * Set the values based on a collection of beans. The beans will be converted to {@link
  // TableData}
  // * in the background.
  // *
  // * @param <B>
  // * @param beans The collection of beans.
  // * @return Fluid API
  // */
  // <B> Create values(Collection<B> beans);

}
