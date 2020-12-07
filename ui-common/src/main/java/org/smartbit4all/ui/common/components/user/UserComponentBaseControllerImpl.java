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
