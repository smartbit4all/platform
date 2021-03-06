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

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

@CssImport("./styles/components/application/screen-detail.css")
public class ScreenDetail extends FlexLayout {

  private String CLASS_NAME = "screen-detail";

  private FlexLayout header;
  private FlexLayout content;
  private FlexLayout footer;

  public enum Position {
    BOTTOM, RIGHT
  }

  public ScreenDetail(Position position, Component... components) {
    setClassName(CLASS_NAME);
    setPosition(position);

    header = new FlexLayout();
    header.setClassName(CLASS_NAME + "__header");

    content = new FlexLayout(components);
    content.setClassName(CLASS_NAME + "__content");
    content.setFlexDirection(FlexDirection.COLUMN);

    footer = new FlexLayout();
    footer.setClassName(CLASS_NAME + "__footer");

    add(header, content, footer);
  }

  public void setHeader(Component... components) {
    this.header.removeAll();
    this.header.add(components);
  }

  public FlexLayout getHeader() {
    return this.header;
  }

  public void setContent(Component... components) {
    this.content.removeAll();
    this.content.add(components);
  }

  public FlexLayout getContent() {
    return this.content;
  }

  public void setFooter(Component... components) {
    this.footer.removeAll();
    this.footer.add(components);
  }

  public FlexLayout getFooter() {
    return this.footer;
  }

  public void clearComponents() {
    this.header.removeAll();
    this.content.removeAll();
    this.footer.removeAll();
  }

  public void setPosition(Position position) {
    getElement().setAttribute("position", position.name().toLowerCase());
  }

  public void hide() {
    getElement().setAttribute("open", false);
  }

  public void show() {
    getElement().setAttribute("open", true);
  }
}
