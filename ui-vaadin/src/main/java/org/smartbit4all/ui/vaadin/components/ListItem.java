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
package org.smartbit4all.ui.vaadin.components;

import org.smartbit4all.ui.vaadin.util.FontSize;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import org.smartbit4all.ui.vaadin.util.css.WhiteSpace;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

@CssImport("./styles/components/list-item.css")
public class ListItem extends FlexLayout {

  private final String CLASS_NAME = "list-item";

  private Div prefix;
  private Div suffix;

  private FlexLayout content;

  private Label primary;
  private Label secondary;

  public ListItem(String primary, String secondary) {
    addClassName(CLASS_NAME);

    setAlignItems(FlexComponent.Alignment.CENTER);
    getStyle().set("padding", "var(--lumo-space-wide-r-l)");
    addClassName("spacing-r-l");

    this.primary = new Label(primary);
    this.secondary = UIUtils.createLabel(FontSize.S, TextColor.SECONDARY,
        secondary);

    content = new FlexLayout(this.primary, this.secondary);
    content.setClassName(CLASS_NAME + "__content");
    content.setFlexDirection(FlexDirection.COLUMN);
    add(content);
  }

  public ListItem(String primary) {
    this(primary, "");
  }

  /* === PREFIX === */

  public ListItem(Component prefix, String primary, String secondary) {
    this(primary, secondary);
    setPrefix(prefix);
  }

  public ListItem(Component prefix, String primary) {
    this(prefix, primary, "");
  }

  /* === SUFFIX === */

  public ListItem(String primary, String secondary, Component suffix) {
    this(primary, secondary);
    setSuffix(suffix);
  }

  public ListItem(String primary, Component suffix) {
    this(primary, null, suffix);
  }

  /* === PREFIX & SUFFIX === */

  public ListItem(Component prefix, String primary, String secondary,
      Component suffix) {
    this(primary, secondary);
    setPrefix(prefix);
    setSuffix(suffix);
  }

  public ListItem(Component prefix, String primary, Component suffix) {
    this(prefix, primary, "", suffix);
  }

  /* === MISC === */

  public FlexLayout getContent() {
    return content;
  }

  public void setWhiteSpace(WhiteSpace whiteSpace) {
    UIUtils.setWhiteSpace(whiteSpace, this);
  }

  public void setReverse(boolean reverse) {
    if (reverse) {
      content.setFlexDirection(FlexDirection.COLUMN_REVERSE);
    } else {
      content.setFlexDirection(FlexDirection.COLUMN);
    }
  }

  public void setHorizontalPadding(boolean horizontalPadding) {
    if (horizontalPadding) {
      getStyle().remove("padding-left");
      getStyle().remove("padding-right");
    } else {
      getStyle().set("padding-left", "0");
      getStyle().set("padding-right", "0");
    }
  }

  public void setPrimaryText(String text) {
    primary.setText(text);
  }

  public Label getPrimary() {
    return primary;
  }

  public void setSecondaryText(String text) {
    secondary.setText(text);
  }

  public void setPrefix(Component... components) {
    if (prefix == null) {
      prefix = new Div();
      prefix.setClassName(CLASS_NAME + "__prefix");
      getElement().insertChild(0, prefix.getElement());
      getElement().setAttribute("with-prefix", true);
    }
    prefix.removeAll();
    prefix.add(components);
  }

  public void setSuffix(Component... components) {
    if (suffix == null) {
      suffix = new Div();
      suffix.setClassName(CLASS_NAME + "__suffix");
      getElement().insertChild(getElement().getChildCount(),
          suffix.getElement());
      getElement().setAttribute("with-suffix", true);
    }
    suffix.removeAll();
    suffix.add(components);
  }

  public void setDividerVisible(boolean visible) {
    if (visible) {
      getElement().setAttribute("with-divider", true);
    } else {
      getElement().removeAttribute("with-divider");
    }
  }

}
