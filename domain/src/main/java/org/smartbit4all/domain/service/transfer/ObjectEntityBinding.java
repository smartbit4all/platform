package org.smartbit4all.domain.service.transfer;

import org.smartbit4all.core.object.ObjectChange;
import org.smartbit4all.domain.meta.EntityDefinition;

/**
 * This interface can transfer the {@link ObjectChange} to a set of CRUD operation against
 * {@link EntityDefinition}s. By executing the operations we can update our RDBMS database with the
 * state changes of the object hierarchy. These bindings are composed from the Spring configuration
 * by the {@link TransferService}
 * 
 * @author Peter Boros
 */
public interface ObjectEntityBinding {

}
