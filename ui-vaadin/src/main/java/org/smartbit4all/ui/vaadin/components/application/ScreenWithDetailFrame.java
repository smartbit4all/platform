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

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;

/**
 * A screen frame for SPA design. It consists of four parts:
 * <ul>
 * <li>Top {@link #setScreenHeader(Component...) header}</li>
 * <li>Center {@link #setScreenContent(Component...) content}</li>
 * <li>Center {@link #setScreenDetails(Component...) details}</li>
 * <li>Bottom {@link #setScreenFooter(Component...) footer}</li>
 * </ul>
 */
@CssImport("./styles/components/application/screen-frame.css")
public class ScreenWithDetailFrame extends Composite<Div> implements HasStyle {

  private String CLASS_NAME = "screen-frame";

  private Div header;

  private FlexLayout wrapper;
  private Div content;
  private Div details;

  private Div footer;

  public enum Position {
    RIGHT, BOTTOM
  }

  public ScreenWithDetailFrame() {
    setClassName(CLASS_NAME);

    header = new Div();
    header.setClassName(CLASS_NAME + "__header");

    wrapper = new FlexLayout();
    wrapper.setClassName(CLASS_NAME + "__wrapper");

    content = new Div();
    content.setClassName(CLASS_NAME + "__content");

    details = new Div();
    details.setClassName(CLASS_NAME + "__details");

    footer = new Div();
    footer.setClassName(CLASS_NAME + "__footer");

    wrapper.add(content, details);
    getContent().add(header, wrapper, footer);
  }

  /**
   * Sets the header slot's components.
   */
  public void setScreenHeader(Component... components) {
    header.removeAll();
    header.add(components);
  }

  /**
   * Sets the content slot's components.
   */
  public void setScreenContent(Component... components) {
    content.removeAll();
    content.add(components);
  }

  /**
   * Sets the detail slot's components.
   */
  public void setScreenDetails(Component... components) {
    details.removeAll();
    details.add(components);
  }

  public void setDetailsPosition(Position position) {
    if (position.equals(Position.RIGHT)) {
      wrapper.setFlexDirection(FlexDirection.ROW);

    } else if (position.equals(Position.BOTTOM)) {
      wrapper.setFlexDirection(FlexDirection.COLUMN);
    }
  }

  /**
   * Sets the footer slot's components.
   */
  public void setScreenFooter(Component... components) {
    footer.removeAll();
    footer.add(components);
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    MainToolbar toolbar = MainScreen.get().getToolbar();
    if (toolbar != null) {
      toolbar.reset();
    }
  }
}
