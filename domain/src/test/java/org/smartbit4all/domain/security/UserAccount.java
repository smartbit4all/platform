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
