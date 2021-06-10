/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.core.object;

import org.smartbit4all.core.event.EventPublisher;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * This event definition is published for accessing the modification events of an object hierarchy.
 * The event are managed in a hierarchical way. If we have a master object the we will have the
 * modification of it's property. For the objects we can have new, deleted and modified events. For
 * a given object instance can be notified about the modification of its properties.
 * 
 * We can subscribe with the following configurations:
 * 
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
 * The main idea behind that the events are created prior to the evaluation. The evaluation and the
 * notification of the listeners is an ordered process. Therefore if we catch an event about a newly
 * created object instance in a collection then we can create the listeners for the inner structure,
 * we can subscribe them and they automatically receive the ongoing event in the same round. So on
 * the UI we can create UI structure for an element of the collection. We can add event listeners
 * and we will have all the property change events.
 * 
 * @author Peter Boros
 */
public interface ObservableObject extends EventPublisher {

  /**
   * Publisher of {@link PropertyChange} events.
   * 
   * @return
   */
  Observable<PropertyChange> properties();

  /**
   * Publisher of {@link ReferenceChange} events.
   * 
   * @return
   */
  Observable<ReferenceChange> references();

  /**
   * Publisher of {@link ReferenceChange} events.
   * 
   * @return
   */
  Observable<ReferencedObjectChange> referencedObjects();

  /**
   * Publisher of {@link CollectionChange} events.
   * 
   * @return
   */
  Observable<CollectionChange> collections();

  /**
   * Publisher of {@link CollectionObjectChange} events.
   * 
   * @return
   */
  Observable<CollectionObjectChange> collectionObjects();

  @NotifyListeners
  void setValue(String propertyPath, Object value);

  @NotifyListeners
  void addValue(String collectionPath, Object value);

  @NotifyListeners
  void removeValue(String collectionElementPath);

  Disposable onPropertyChange(String path, String property,
      @NonNull Consumer<? super PropertyChange> onPropertyChange);

  Disposable onReferenceChange(String path, String reference,
      @NonNull Consumer<? super ReferenceChange> onReferenceChange);

  Disposable onReferencedObjectChange(String path, String reference,
      @NonNull Consumer<? super ReferencedObjectChange> onReferencedObjectChange);

  Disposable onCollectionChange(String path, String collection,
      @NonNull Consumer<? super CollectionChange> onCollectionChange);

  Disposable onCollectionObjectChange(String path, String collection,
      @NonNull Consumer<? super CollectionObjectChange> onCollectionObjectChange);

  /**
   * Sets a consumer that can do more before and after the event listeners are called (e.g. It can open a UI thread if it is not on one)
   * 
   * @param publisherWrapper
   */
  void setPublisherWrapper(Consumer<Runnable> publisherWrapper);

}
