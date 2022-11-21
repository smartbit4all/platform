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

import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.ComputationLogicImpl;
import org.smartbit4all.domain.meta.InputProvider;
import org.smartbit4all.domain.meta.InputValue;
import org.smartbit4all.domain.meta.OutputValue;
import org.smartbit4all.domain.meta.PropertyDynamic;
import org.smartbit4all.domain.meta.PropertyWired;
import com.google.common.base.Strings;

public class UserAccountFullNameImpl extends ComputationLogicImpl implements UserAccountFullName {

  @PropertyWired(UserAccountDef.TITLE)
  InputValue<String> title;

  @PropertyWired(UserAccountDef.FIRSTNAME)
  InputValue<String> firstName;

  @PropertyDynamic
  InputValue<String> lastName;

  @PropertyWired(UserAccountDef.FULLNAME)
  OutputValue<String> fullName;

  @InputProvider
  UserTitle userTitle;

  protected void compute() {}

  UserAccountFullNameImpl(UserAccountDef userAccountDef) {
    super(userAccountDef);
    // TODO Auto-generated constructor stub - Spring
    lastName = userAccountDef.lastname().input();
  }

  @Override
  public void execute() throws Exception {
    String titleText = title.get();
    fullName.set(
        (Strings.isNullOrEmpty(titleText) ? StringConstant.EMPTY : titleText + StringConstant.SPACE)
            + firstName.get() + StringConstant.SPACE + lastName.get());
  }

  // private void transactionCompleted(RecordIterator records) {
  // List<String> names = new ArrayList<String>(records.size());
  // while (records.hasNext()) {
  // Integer currentIndex = records.next();
  // names.add(firstName.get());
  // fullName.set(lastName.get() + firstName.get());
  // }
  // List<String> firtsNames = records.asList(firstName);
  // records.set(fullName, firtsNames);
  // // records.set(firstName, names);
  // }

  // public final void compute(InputValue<String> firstName, InputValue<String> lastName,
  // OutputValue<String> fullName) {
  // this.firstName = firstName;
  // this.lastName = lastName;
  // this.fullName = fullName;
  // compute();
  // }

  // public final String compute(String firstName, String lastName) {
  // this.firstName = new InputValue<String>() {
  //
  // @Override
  // public String get() {
  // return firstName;
  // } };
  //
  // this.lastName = lastName;
  // this.fullName = new ;
  // compute();
  // return fullName
  // }

}
