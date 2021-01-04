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
package org.smartbit4all.ui.vaadin.util;

import com.vaadin.flow.component.html.Label;

/**
 * Utility for creating Vaadin labels.
 * 
 * @author Attila Mate
 *
 */
public class Labels {

  public static Label createLabel(String size, String color, String text) {
    Label label = new Label(text);
    Css.setFontSize(size, label);
    Css.setTextColor(color, label);
    return label;
  }

  public static Label createLabelWithSize(String size, String text) {
    return createLabel(size, Css.TextColor.BODY, text);
  }

  public static Label createLabelWithColor(String color, String text) {
    return createLabel(Css.FontSize.M, color, text);
  }

  public static Label createH1Label(String text) {
    Label label = new Label(text);
    label.addClassName(Css.Heading.H1);
    return label;
  }

  public static Label createH2Label(String text) {
    Label label = new Label(text);
    label.addClassName(Css.Heading.H2);
    return label;
  }

  public static Label createH3Label(String text) {
    Label label = new Label(text);
    label.addClassName(Css.Heading.H3);
    return label;
  }

  public static Label createH4Label(String text) {
    Label label = new Label(text);
    label.addClassName(Css.Heading.H4);
    return label;
  }

  public static Label createH5Label(String text) {
    Label label = new Label(text);
    label.addClassName(Css.Heading.H5);
    return label;
  }

  public static Label createH6Label(String text) {
    Label label = new Label(text);
    label.addClassName(Css.Heading.H6);
    return label;
  }

}
