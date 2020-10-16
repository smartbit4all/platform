package org.smartbit4all.domain.service.modify;

import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * The update operation on an entity. It always have a list of records with primary keys to identify
 * them in the data source (like RDBMS). It updates the already existing records.
 * 
 * @author Peter Boros
 */
public interface Update<E extends EntityDefinition>
    extends SB4Function<UpdateInput<E>, UpdateOutput<E>> {

  // TODO into should be TableData result. Maybe result? like updateWithRefresh

  /**
   * Set the source of the creation. It provides the records for the creation.
   * 
   * @param source
   * @return
   */
  Update<E> values(TableData<E> data);

}
