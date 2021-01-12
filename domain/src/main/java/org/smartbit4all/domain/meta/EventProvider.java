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
package org.smartbit4all.domain.meta;

/**
 * This extension for the APIs is used to publish {@link EntityDefinition}s and subscription API for
 * the consumers. The consumers register {@link EventListener}s to receive the relevant events. An
 * API is event driven it offers this interface as an operation. This interface can be implemented
 * 
 * @author Peter Boros
 *
 */
public interface EventProvider {

  /**
   * If an API implements this interface then the caller can access the published event definitions
   * via this operation. It returns a registry where we can get every event definition. Using this
   * event definition we can construct a subscription.
   * 
   * @return
   */
  EventProviderRegistry events();

}
