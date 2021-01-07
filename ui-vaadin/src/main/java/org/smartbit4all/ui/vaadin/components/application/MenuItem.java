/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.application;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.vaadin.util.Buttons;
import org.smartbit4all.ui.vaadin.util.Css;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

@CssImport("./styles/components/application/menu-item.css")
public class MenuItem extends Div {

  private String CLASS_NAME = "menu-item";

  private int level = 0;

  private Component link;
  private Class<? extends Component> screen;
  private String text;

  protected Button expandCollapse;

  private List<MenuItem> subItems;
  private boolean subItemsVisible;

  public MenuItem(VaadinIcon icon, String text, Class<? extends Component> screen) {
    this(text, screen);
    link.getElement().insertChild(0, new Icon(icon).getElement());
  }

  public MenuItem(Image image, String text, Class<? extends Component> screen) {
    this(text, screen);
    link.getElement().insertChild(0, image.getElement());
  }

  public MenuItem(String text, Class<? extends Component> screen) {
    setClassName(CLASS_NAME);
    setLevel(0);

    this.text = text;
    this.screen = screen;

    if (screen != null) {
      RouterLink routerLink = new RouterLink(null, screen);
      routerLink.add(new Span(text));
      routerLink.setClassName(CLASS_NAME + "__link");
      routerLink.setHighlightCondition(HighlightConditions.sameLocation());
      this.link = routerLink;

    } else {
      Div div = new Div(new Span(text));
      div.addClickListener(e -> expandCollapse.click());
      div.setClassName(CLASS_NAME + "__link");
      this.link = div;
    }

    expandCollapse = Buttons.createButton(VaadinIcon.CARET_UP, ButtonVariant.LUMO_SMALL,
        ButtonVariant.LUMO_TERTIARY);
    expandCollapse.addClickListener(event -> setSubItemsVisible(!subItemsVisible));
    expandCollapse.setVisible(false);

    subItems = new ArrayList<>();
    subItemsVisible = true;
    updateAriaLabel();

    add(link, expandCollapse);
  }

  private void updateAriaLabel() {
    String action = (subItemsVisible ? "Collapse " : "Expand ") + text;
    Css.setAriaLabel(action, expandCollapse);
  }

  public boolean isHighlighted(AfterNavigationEvent e) {
    return link instanceof RouterLink && ((RouterLink) link)
        .getHighlightCondition().shouldHighlight((RouterLink) link, e);
  }

  public void setLevel(int level) {
    this.level = level;
    if (level > 0) {
      getElement().setAttribute("level", Integer.toString(level));
    }
  }

  public int getLevel() {
    return level;
  }

  public Class<? extends Component> getScreen() {
    return screen;
  }

  public void addSubMenuItem(MenuItem item) {
    if (!expandCollapse.isVisible()) {
      expandCollapse.setVisible(true);
    }
    item.setLevel(getLevel() + 1);
    subItems.add(item);
  }

  private void setSubItemsVisible(boolean visible) {
    if (level == 0) {
      expandCollapse.setIcon(new Icon(visible ? VaadinIcon.CARET_UP : VaadinIcon.CARET_DOWN));
    }
    subItems.forEach(item -> item.setVisible(visible));
    subItemsVisible = visible;
    updateAriaLabel();
  }

  @Override
  public String getText() {
    return text;
  }

  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);

    // If true, we only update the icon. If false, we hide all the sub items
    if (visible) {
      if (level == 0) {
        expandCollapse.setIcon(new Icon(VaadinIcon.CARET_DOWN));
      }
    } else {
      setSubItemsVisible(visible);
    }
  }
}
