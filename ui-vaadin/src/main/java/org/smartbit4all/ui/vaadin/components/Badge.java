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
package org.smartbit4all.ui.vaadin.components;

import static org.smartbit4all.ui.vaadin.util.css.lumo.BadgeShape.PILL;
import java.util.StringJoiner;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeColor;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeShape;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeSize;
import com.vaadin.flow.component.html.Span;

public class Badge extends Span {

  public Badge(String text) {
    this(text, BadgeColor.NORMAL);
  }

  public Badge(String text, BadgeColor color) {
    super(text);
    UIUtils.setTheme(color.getThemeName(), this);
  }

  public Badge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
    super(text);
    StringJoiner joiner = new StringJoiner(" ");
    joiner.add(color.getThemeName());
    if (shape.equals(PILL)) {
      joiner.add(shape.getThemeName());
    }
    if (size.equals(BadgeSize.S)) {
      joiner.add(size.getThemeName());
    }
    UIUtils.setTheme(joiner.toString(), this);
  }

}
