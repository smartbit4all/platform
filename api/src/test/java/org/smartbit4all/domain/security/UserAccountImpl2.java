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

/**
 * The Bean interface for the {@link UserAccountDef}.
 * 
 * @author Peter Boros
 */
public class UserAccountImpl2 {

  Long id;

  String name;

  String firstName;

  private String lastName;

  private Long primaryAddressId;

  private String titleCode;

  private String title;

  private String primaryZipcode;

  private String fullname;

  public Long id() {
    return id;
  }

  public UserAccountImpl2 id(Long id) {
    this.id = id;
    return this;
  }

  public String name() {
    return name;
  }

  public UserAccountImpl2 name(String name) {
    this.name = name;
    return this;
  }

  public String firstname() {
    return firstName;
  }

  public UserAccountImpl2 firstname(String firstname) {
    this.firstName = firstname;
    return this;
  }

  public String lastname() {
    return lastName;
  }

  public UserAccountImpl2 lastname(String lastname) {
    this.lastName = lastname;
    return this;
  }

  public Long primaryAddressId() {
    return primaryAddressId;
  }

  public UserAccountImpl2 primaryAddressId(Long primaryAddressId) {
    this.primaryAddressId = primaryAddressId;
    return this;
  }

  public String titleCode() {
    return titleCode;
  }

  public UserAccountImpl2 titleCode(String titleCode) {
    this.titleCode = titleCode;
    return this;
  }

  public String title() {
    return title;
  }

  public UserAccountImpl2 title(String title) {
    this.title = title;
    return this;
  }

  public String primaryZipcode() {
    return primaryZipcode;
  }

  public UserAccountImpl2 primaryZipcode(String primaryZipcode) {
    this.primaryZipcode = primaryZipcode;
    return this;
  }

  public String fullname() {
    return fullname;
  }

  public UserAccountImpl2 fullname(String fullName) {
    this.fullname = fullName;
    return this;
  }

}
