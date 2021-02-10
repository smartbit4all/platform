package org.smartbit4all.api.constraint;

import org.smartbit4all.api.object.CollectionChange;
import org.smartbit4all.api.object.ObjectChange;
import org.smartbit4all.api.object.PropertyChange;
import org.smartbit4all.api.object.ReferenceChange;
import org.smartbit4all.domain.meta.EventPublisher;

/**
 * This interface is the inner API interface for the stateful apis providing constraints. The
 * constraints are meta data related to the api object hierarchy. The constraints can be the
 * following: Enabled/disable Mandatory/optional Format Focus Selection - collection
 * 
 * The given constraints have different characteristic. All of them are managed in a hierarchical
 * way. The path is defined by the Api object hierarchy.
 * <ul>
 * <li><b>Property change event - {@link PropertyChange}</b> - We must add a context to define the
 * instance that owns the property. Or we can say that we would like to be notified about the
 * modification of the given property it doesn't matter in which context.</li>
 * <li><b>Collection change event - {@link CollectionChange}</b> - We would like to be notified
 * about the changes of a collection. It's similar to the properties but the content of the
 * notification is a list about the changes in the in the collection. We can have only the new /
 * deleted events or we can subscribe for the modification also. The new / delete is pre order - we
 * get the notification earlier then the embedded. The modification is post order so we get the
 * modification after all the embedded subscriptions have been notified.</li>
 * <li><b>Reference change event - {@link ReferenceChange}</b> - We must add a context to define the
 * instance that owns the property. In this case the reference is another object so we will find the
 * {@link ObjectChange} event inside the notification. If we have a newly referred object then we
 * receive a NEW</li>
 * </ul>
 * 
 * @author Peter Boros
 */
public interface ConstraintPublisher extends EventPublisher {

  /**
   * The mandatory event definition.
   * 
   * @return
   */
  public MandatoryChangeEvent mandatories();

}
