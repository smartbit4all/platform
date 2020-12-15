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

import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeColor;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeShape;
import org.smartbit4all.ui.vaadin.util.css.lumo.BadgeSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;

@CssImport("./styles/components/closeable-badge.css")
public class CloseableBadge extends Badge {

  private String CLASS_NAME = "sb4-closeablebadge";
  
  private Button btnClose;

  public CloseableBadge(String text, BadgeColor color, BadgeSize size, BadgeShape shape) {
    super(text, color, size, shape);
    setClassName(CLASS_NAME);
    btnClose = new Button("x");
    this.add(btnClose);
  }

  public Button getCloseButton() {
    return btnClose;
  }

}
