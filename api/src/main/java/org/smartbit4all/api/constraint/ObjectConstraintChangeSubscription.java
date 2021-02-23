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

import java.lang.ref.WeakReference;
import org.smartbit4all.api.object.ApiObjectRef;
import org.smartbit4all.domain.meta.EventListener;
import org.smartbit4all.domain.meta.EventSubscription;

/**
 * A builder API to subscribe for the constraint changes of an {@link ApiObjectRef}. It can be
 * initiated for every constraint like mandatories or editable.
 * 
 * @author Peter Boros
 *
 * @param <C> The type of the constraint.
 */
public class ObjectConstraintChangeSubscription<C> extends EventSubscription<C> {

  /**
   * The path for the subscription.
   */
  private String path;

  /**
   * The reference of the event. it's used to refresh the registry of the event.
   */
  private final WeakReference<ObjectConstraintChangeEventImpl<C>> eventRef;

  public ObjectConstraintChangeSubscription(
      ObjectConstraintChangeEventImpl<C> eventDef) {
    super();
    this.eventRef = new WeakReference<>(eventDef);
  }

  public ObjectConstraintChangeSubscription<C> path(String path) {
    this.path = path;
    return this;
  }

  @Override
  public EventSubscription<C> add(EventListener<C> listener) {
    ObjectConstraintChangeEventImpl<C> eventImpl = eventRef.get();
    if (eventImpl != null) {
      eventImpl.addListener(path, listener);
      return super.add(listener);
    }
    return null;
  }

  @Override
  public void unsubsribe() {
    ObjectConstraintChangeEventImpl<C> eventImpl = eventRef.get();
    if (eventImpl != null) {
      eventImpl.unsubscribe(this);
    }
  }

  public final String getPath() {
    return path;
  }

}
