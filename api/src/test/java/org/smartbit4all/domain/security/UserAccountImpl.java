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
public class UserAccountImpl {

  Long id;

  String name;

  String firstName;

  private String lastName;

  private Long primaryAddressId;

  private String titleCode;

  private String title;

  private String primaryZipcode;

  private String fullname;

  @PropertyAccessor(UserAccountDef.ID)
  public Long id() {
    return id;
  }

  @PropertyAccessor(UserAccountDef.ID)
  public UserAccountImpl id(Long id) {
    this.id = id;
    return this;
  }

  @PropertyAccessor(UserAccountDef.NAME)
  public String getName() {
    return name;
  }

  @PropertyAccessor(UserAccountDef.NAME)
  public UserAccountImpl name(String name) {
    this.name = name;
    return this;
  }

  @PropertyAccessor(UserAccountDef.FIRSTNAME)
  public String firstname() {
    return firstName;
  }

  @PropertyAccessor(UserAccountDef.FIRSTNAME)
  public UserAccountImpl firstname(String firstname) {
    this.firstName = firstname;
    return this;
  }

  @PropertyAccessor(UserAccountDef.LASTNAME)
  public String lastname() {
    return lastName;
  }

  @PropertyAccessor(UserAccountDef.LASTNAME)
  public UserAccountImpl lastname(String lastname) {
    this.lastName = lastname;
    return this;
  }

  @PropertyAccessor(UserAccountDef.PRIMARYADDRESS_ID)
  public Long primaryAddressId() {
    return primaryAddressId;
  }

  @PropertyAccessor(UserAccountDef.PRIMARYADDRESS_ID)
  public UserAccountImpl primaryAddressId(Long primaryAddressId) {
    this.primaryAddressId = primaryAddressId;
    return this;
  }

  @PropertyAccessor(UserAccountDef.TITLE_CODE)
  public String titleCode() {
    return titleCode;
  }

  @PropertyAccessor(UserAccountDef.TITLE_CODE)
  public UserAccountImpl titleCode(String titleCode) {
    this.titleCode = titleCode;
    return this;
  }

  @PropertyAccessor(UserAccountDef.TITLE)
  public String title() {
    return title;
  }

  @PropertyAccessor(UserAccountDef.TITLE)
  public UserAccountImpl title(String title) {
    this.title = title;
    return this;
  }

  @PropertyAccessor(UserAccountDef.PRIMARYZIPCODE)
  public String primaryZipcode() {
    return primaryZipcode;
  }

  @PropertyAccessor(UserAccountDef.PRIMARYZIPCODE)
  public UserAccountImpl primaryZipcode(String primaryZipcode) {
    this.primaryZipcode = primaryZipcode;
    return this;
  }

  @PropertyAccessor(UserAccountDef.FULLNAME)
  public String fullname() {
    return fullname;
  }

  @PropertyAccessor(UserAccountDef.FULLNAME)
  public UserAccountImpl fullname(String fullName) {
    this.fullname = fullName;
    return this;
  }

}
