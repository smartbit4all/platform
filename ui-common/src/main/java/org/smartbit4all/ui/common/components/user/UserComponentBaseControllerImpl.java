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
package org.smartbit4all.ui.common.components.user;

public class UserComponentBaseControllerImpl implements UserComponentBaseController {

  private UserComponentConfiguration config;
  
  private UserComponentBaseView ui;
  
  public UserComponentBaseControllerImpl(UserComponentConfiguration config) {
    this.config = config;
  }
  
  @Override
  public void setUi(UserComponentBaseView ui) {
    this.ui = ui;
    loadConfig();
  }

  private void loadConfig() {
    ui.setLabel(config.getLabelFactory().get());
    ui.setPicture(config.getPictureFactory().get());
    ui.setItems(config.getItemsByCode().values());
  }
  
  @Override
  public void navigate(String route) {
    ui.navigate(route);
  }
  
}
