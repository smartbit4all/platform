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
