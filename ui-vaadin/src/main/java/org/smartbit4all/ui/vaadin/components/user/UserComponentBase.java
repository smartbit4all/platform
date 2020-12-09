package org.smartbit4all.ui.vaadin.components.user;

import java.io.InputStream;
import java.util.Collection;
import org.smartbit4all.ui.common.components.user.UserComponentBaseController;
import org.smartbit4all.ui.common.components.user.UserComponentBaseView;
import org.smartbit4all.ui.common.components.user.UserComponentConfiguration.NavigationActionItem;
import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import org.smartbit4all.ui.vaadin.localization.TranslationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.server.StreamResource;

@CssImport("./styles/components/user/user-component.css")
public class UserComponentBase extends Composite<FlexBoxLayout> implements UserComponentBaseView {

  private UserComponentBaseController controller;
  
  private Image photo;
  private Div label;
  private ContextMenu contextMenu;
  
  
  public UserComponentBase(UserComponentBaseController controller) {
    this.controller = controller;
    
    init();
    
    controller.setUi(this);
  }
  
  
  private void init() {
    photo = new Image();
    label = new Div();
    this.getContent().add(photo, label);
    
    contextMenu = new ContextMenu(this.getContent());
    contextMenu.setOpenOnClick(true);
    
    photo.setVisible(false);
    label.setVisible(false);
    
    this.getContent().setClassName("user-component");
    photo.setClassName("user-component-photo");
    label.setClassName("user-component-label");
  }


  @Override
  public void setLabel(String label) {
    if(label != null && !label.isEmpty()) {
      this.label.setText(label);
      this.label.setVisible(true);
    } else {
      this.label.setVisible(false);
    }
  }

  @Override
  public void setPicture(final InputStream inputStream) {
    if(inputStream != null) {
      photo.setSrc(new StreamResource("user.png", () -> inputStream));
      photo.setVisible(true);
    } else {
      photo.setVisible(false);
    }
    
  }

  @Override
  public void setItems(Collection<NavigationActionItem> values) {
    contextMenu.removeAll();
    values.forEach(item -> {
      contextMenu.addItem(createItem(item), e -> controller.navigate(item.getNavigationRoute()));
    });
  }

  protected Component createItem(NavigationActionItem item) {
    Div itemWrapper = new Div();
    itemWrapper.setClassName("user-component-menuitem");
    String iconCode = item.getIconCode();
    if(iconCode != null) {
      Icon icon = new Icon(iconCode);
      icon.setClassName("user-component-menuitem-icon");
      itemWrapper.add(icon);
    }
    String label = item.getLabel();
    if(label != null) {
      String labelTxt = TranslationUtil.INSTANCE().getPossibleTranslation(label);
      Div labelDiv = new Div();
      labelDiv.setClassName("user-component-menuitem-label");
      labelDiv.setText(labelTxt);
      itemWrapper.add(label);
    }
    return itemWrapper;
  }


  @Override
  public void navigate(String route) {
    if("logout".equals(route)) {
      UI.getCurrent().getPage().executeJs("window.open(\"/logout\", \"_self\");");
    }
  }

}
