/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.common.filter;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.domain.meta.EventAware;
import org.smartbit4all.domain.meta.EventPublisherHelper;

public abstract class AbstractUIState implements EventAware {

  private static final Logger log = LoggerFactory.getLogger(AbstractUIState.class);
  
  protected String id;
  
  protected EventPublisherHelper<UIStateEvent> helper;

  public AbstractUIState() {
    this.id = UUID.randomUUID().toString();
    try {
      helper = new EventPublisherHelper<UIStateEvent>(UIStateEvent.class, "api:/AbstractUIStateEventApi");
    } catch (ExecutionException e) {
      log.error("Error setting up EventPublisher for " + this.getClass().getSimpleName() + "!");
    }
  }

  public String getId() {
    return id;
  }
  
  @Override
  public UIStateEvent events() {
    return helper.publisher();
  }
  
}
