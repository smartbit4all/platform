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
package org.smartbit4all.domain.meta;

import org.springframework.context.ApplicationContextAware;

/**
 * This interface is used to setup all entities. After creating all entities, the setup be like
 * this:
 * <ul>
 * <li>{@link #initContext()} for all entities
 * <li>{@link #setupProperties()} for all entities
 * <li>{@link #setupReferences()} for all entities
 * <li>{@link #setupReferredProperties()} for all entities
 * </ul>
 * 
 * It means that in entity constructor no property is set
 * 
 * @author Attila Mate
 *
 */
public interface EntitySetup extends ApplicationContextAware {

  /**
   * Initialize context with appropriate entity customizations.
   */
  void initContext();

  /**
   * Setup all owned properties.
   */
  void setupProperties();

  /**
   * Setup all references. When called by the framework, all entities are registered with all their
   * owned properties.
   */
  void setupReferences();

  /**
   * Setup all referred properties. When called by the framework, all entities are registered with
   * all their owned properties and references.
   * 
   */
  void setupReferredProperties();

  /**
   * Finish setup. All properties propertySet will be initialized here.
   */
  void finishSetup();
}
