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
package org.smartbit4all.ui.common.filter;

import java.util.UUID;
import org.smartbit4all.domain.meta.EventAware;
import org.smartbit4all.domain.meta.EventPublisherHelper;

public abstract class AbstractUIState implements EventAware {

  protected String id;

  protected EventPublisherHelper<UIStateEvent> helper;

  protected AbstractUIState() {
    this.id = UUID.randomUUID().toString();
    helper =
        new EventPublisherHelper<>(UIStateEvent.class, "api:/AbstractUIStateEventApi");
  }

  public String getId() {
    return id;
  }

  @Override
  public UIStateEvent events() {
    return helper.publisher();
  }

}
