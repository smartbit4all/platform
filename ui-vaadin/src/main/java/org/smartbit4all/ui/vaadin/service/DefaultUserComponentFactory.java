package org.smartbit4all.ui.vaadin.service;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Image;

public class DefaultUserComponentFactory implements UserComponentFactory {

  @Override
  public Component createUserComponent() {
    Image avatarImage = new Image("images/avatar.png", "Avatar");
    avatarImage.setId("avatar");
    return avatarImage;
  }

}
