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
package org.smartbit4all.core.constraint;

import java.lang.ref.WeakReference;
import org.smartbit4all.core.event.ListenerAware;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.utility.StringConstant;

/**
 * The base implementation of the {@link ObjectConstraintPublisher}.
 * 
 * @author Peter Boros
 */
public final class ObjectConstraintPublisherImpl
    implements ObjectConstraintPublisher, ListenerAware {

  private WeakReference<ApiObjectRef> apiObjectRef;

  private final ObjectConstraintChangeEventImpl<Boolean> mandatories =
      new ObjectConstraintChangeEventImpl<>(null, "mandatory", false);

  private final ObjectConstraintChangeEventImpl<Boolean> editables =
      new ObjectConstraintChangeEventImpl<>(null, "editables", false);

  private final ObjectConstraintChangeEventImpl<String> formats =
      new ObjectConstraintChangeEventImpl<>(null, "formats", StringConstant.EMPTY);

  @Override
  public ObjectConstraintChangeEvent<Boolean> mandatory() {
    return mandatories;
  }

  @Override
  public ObjectConstraintChangeEvent<Boolean> editable() {
    return editables;
  }

  @Override
  public ObjectConstraintChangeEvent<String> format() {
    return formats;
  }

  @Override
  public void notifyListeners() {
    ApiObjectRef objectRef = apiObjectRef.get();
    if (objectRef != null) {
      mandatories.notifyListeners(objectRef);
      editables.notifyListeners(objectRef);
      formats.notifyListeners(objectRef);
    }
  }

  public final ObjectConstraintChangeEvent<Boolean> getMandatories() {
    return mandatories;
  }

  public final ObjectConstraintChangeEvent<Boolean> getEditables() {
    return editables;
  }

  public final ObjectConstraintChangeEvent<String> getFormats() {
    return formats;
  }

  public final ApiObjectRef getApiObjectRef() {
    return apiObjectRef.get();
  }

  public final void setApiObjectRef(ApiObjectRef apiObjectRef) {
    this.apiObjectRef = new WeakReference<>(apiObjectRef);
  }

}
