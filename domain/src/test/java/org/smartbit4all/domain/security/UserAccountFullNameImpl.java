package org.smartbit4all.domain.security;

import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EventHandlerImpl;
import org.smartbit4all.domain.meta.InputProvider;
import org.smartbit4all.domain.meta.InputValue;
import org.smartbit4all.domain.meta.OutputValue;
import org.smartbit4all.domain.meta.PropertyDynamic;
import org.smartbit4all.domain.meta.PropertyWired;
import com.google.common.base.Strings;

public class UserAccountFullNameImpl extends EventHandlerImpl implements UserAccountFullName {

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
