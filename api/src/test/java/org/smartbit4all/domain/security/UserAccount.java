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
package org.smartbit4all.domain.security;

import org.smartbit4all.domain.annotation.databean.PropertyAccessor;

/**
 * The Bean interface for the {@link UserAccountDef}.
 * 
 * @author Peter Boros
 */
public interface UserAccount {

  @PropertyAccessor(UserAccountDef.ID)
  Long id();

  @PropertyAccessor(UserAccountDef.ID)
  UserAccount id(Long id);

  @PropertyAccessor(UserAccountDef.NAME)
  String getName();

  @PropertyAccessor(UserAccountDef.NAME)
  UserAccount name(String name);

  @PropertyAccessor(UserAccountDef.FIRSTNAME)
  String firstname();

  @PropertyAccessor(UserAccountDef.FIRSTNAME)
  UserAccount firstname(String firstname);

  @PropertyAccessor(UserAccountDef.LASTNAME)
  String lastname();

  @PropertyAccessor(UserAccountDef.LASTNAME)
  UserAccount lastname(String lastname);

  @PropertyAccessor(UserAccountDef.PRIMARYADDRESS_ID)
  Long primaryAddressId();

  @PropertyAccessor(UserAccountDef.PRIMARYADDRESS_ID)
  UserAccount primaryAddressId(Long primaryAddressId);

  @PropertyAccessor(UserAccountDef.TITLE_CODE)
  String titleCode();

  @PropertyAccessor(UserAccountDef.TITLE_CODE)
  UserAccount titleCode(String titleCode);

  @PropertyAccessor(UserAccountDef.TITLE)
  String title();

  @PropertyAccessor(UserAccountDef.TITLE)
  UserAccount title(String title);

  @PropertyAccessor(UserAccountDef.PRIMARYZIPCODE)
  String primaryZipcode();

  @PropertyAccessor(UserAccountDef.PRIMARYZIPCODE)
  UserAccount primaryZipcode(String primaryZipcode);

  @PropertyAccessor(UserAccountDef.FULLNAME)
  String fullname();

  @PropertyAccessor(UserAccountDef.FULLNAME)
  UserAccount fullname(String fullName);

}
