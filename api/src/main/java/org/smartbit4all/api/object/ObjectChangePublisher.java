package org.smartbit4all.api.object;

import org.smartbit4all.domain.meta.EventPublisher;

/**
 * This event definition is published for accessing the modification events of an object hierarchy.
 * The event are managed in a hierarchical way. If we have a master object the we will have the
 * modification of it's property. For the objects we can have new, deleted and modified events. For
 * a given object instance can be notified about the modification of its properties.
 * 
 * We can subscribe with the following configurations:
 * 
 * <ul>
 * <li><b>Object change event - {@link ObjectChange} </b> - We cann't directly subscribe to this
 * event. We can subscribe for a reference change with empty empty context that means the root
 * object instance. An event publisher can have more root objects but in this case we wont define
 * the exact instance. If we subscribe for events of inner objects then we need the context URI
 * where the given reference or collection is defined.</li>
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
 * The main idea behind that the events are created prior to the evaluation. The evaluation and the
 * notification of the listeners is an ordered process. Therefore if we catch an event about a newly
 * created object instance in a collection then we can create the listeners for the inner structure,
 * we can subscribe them and they automatically receive the ongoing event in the same round. So on
 * the UI we can create UI structure for an element of the collection. We can add event listeners
 * and we will have all the property change events.
 * 
 * @author Peter Boros
 */
public interface ObjectChangePublisher extends EventPublisher {

  /**
   * The event definition of the properties.
   * 
   * @return
   */
  PropertyChangeEvent properties();

  /**
   * The event definition of the references.
   * 
   * @return
   */
  ReferenceChangeEvent references();

  /**
   * The event definition of the collections.
   * 
   * @return
   */
  CollectionChangeEvent collections();

}
