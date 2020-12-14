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

import org.smartbit4all.core.SB4Service;

/**
 * The data conversion is a super interface for the data conversion services of the application.
 * These conversions can be used at any level as they are globally registered. But on the other hand
 * they are heavily used by the {@link JDBCDataConverter} infrastructure also. The given type will
 * offer some conversion methods that can transform the data to and from.
 * 
 * The functions are named by the following logic. We convert from the source to the target. So when
 * we have the source value then we {@link #app2ext(Object)}. If we have the target and would like to
 * have the source then we {@link #ext2app(Object)}.
 * 
 * 
 * @author Peter Boros
 * @param <APPDOMAINTYPE> The type of source value.
 * @param <EXTERNALTYPE> The type of the target value.
 *
 */
public interface DataConverter<APPDOMAINTYPE, EXTERNALTYPE> extends SB4Service {

  /**
   * Reads the source value and produces the target value by the rules of the conversation.
   * 
   * @param appValue The source value.
   * @return The target value.
   */
  EXTERNALTYPE app2ext(APPDOMAINTYPE appValue);

  /**
   * The inversion of the {@link #app2ext(Object)}. Receives the target value and produces the source.
   * 
   * @param extValue
   * @return
   */
  APPDOMAINTYPE ext2app(EXTERNALTYPE extValue);

  /**
   * The class of the source.
   * 
   * @return
   */
  Class<APPDOMAINTYPE> appType();

  /**
   * The class of the target type.
   * 
   * @return
   */
  Class<EXTERNALTYPE> extType();

}
