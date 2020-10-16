package org.smartbit4all.domain.security;

import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EventHandlerImpl;
import org.smartbit4all.domain.meta.InputValue;
import org.smartbit4all.domain.meta.OutputValue;
import org.smartbit4all.domain.meta.PropertyWired;

public class UserTitleImpl extends EventHandlerImpl implements UserTitle {

  @PropertyWired(UserAccountDef.TITLE_CODE)
  InputValue<String> titleCode;

  @PropertyWired(UserAccountDef.TITLE)
  OutputValue<String> title;

  protected UserTitleImpl(UserAccountDef entity) {
    super(entity);
  }

  @Override
  public void execute() throws Exception {
    title.set(titleCode.get().toLowerCase() + StringConstant.DOT);
  }

}
