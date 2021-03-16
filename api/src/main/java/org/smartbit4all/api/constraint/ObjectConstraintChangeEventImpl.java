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
package org.smartbit4all.api.constraint;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.object.ApiObjectRef;
import org.smartbit4all.domain.meta.EventDefinitionBase;
import org.smartbit4all.domain.meta.EventListener;
import org.smartbit4all.domain.meta.EventSubscription;

/**
 * The implementation of the constraint change events.
 * 
 * @author Peter Boros
 *
 * @param <C>
 */
public class ObjectConstraintChangeEventImpl<C> extends EventDefinitionBase<C>
    implements ObjectConstraintChangeEvent<C> {

  /**
   * The helper for the constraints.
   */
  private HierarchicalConstraintHelper<C> helper;

  /**
   * All the subscriptions for the event.
   */
  private List<EventSubscription<C>> subscriptions = new ArrayList<>();

  /**
   * The listeners map by the path as key. If we add / remove subscription the this map is updated.
   */
  private Map<String, List<EventListener<C>>> listeners = new HashMap<>();

  public ObjectConstraintChangeEventImpl(URI uri, String name, C defaultValue) {
    super(URI.create("event:/objectediting/constraint"));
    this.helper = new HierarchicalConstraintHelper<>(defaultValue);
  }

  @Override
  public ObjectConstraintChangeSubscription<C> subscribe() {
    ObjectConstraintChangeSubscription<C> subscription =
        new ObjectConstraintChangeSubscription<>(this);
    subscriptions.add(subscription);
    return subscription;
  }

  /**
   * Function for the {@link ObjectConstraintChangeSubscription}s to remove themselves from the
   * registry.
   * 
   * @param subsription
   */
  void unsubscribe(ObjectConstraintChangeSubscription<C> subsription) {
    subscriptions.remove(subsription);
    List<EventListener<C>> pathListeners = listeners.get(subsription.getPath());
    pathListeners.removeAll(subsription.getListeners());
  }

  /**
   * Add a listener to the registry.
   * 
   * @param path
   * @param listener
   */
  void addListener(String path, EventListener<C> listener) {
    List<EventListener<C>> list = listeners.computeIfAbsent(path, k -> new ArrayList<>());
    list.add(listener);
  }

  /**
   * Render the actual changes and clear the change status. Notify the listeners.
   * 
   * @param objectRef
   */
  void notifyListeners(ApiObjectRef objectRef) {
    List<ConstraintChange<C>> changes = helper.renderAndCleanChanges(objectRef);
    for (ConstraintChange<C> change : changes) {
      List<EventListener<C>> listenerList = listeners.get(change.getPath());
      for (EventListener<C> eventListener : listenerList) {
        eventListener.accept(change.getNewValue());
      }
    }
  }

}
