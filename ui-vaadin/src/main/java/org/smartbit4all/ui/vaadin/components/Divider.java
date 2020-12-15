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

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import org.smartbit4all.ui.vaadin.util.LumoStyles;
import org.smartbit4all.ui.vaadin.util.UIUtils;

public class Divider extends FlexBoxLayout implements HasSize, HasStyle {

	private String CLASS_NAME = "divider";

	private Div divider;

	public Divider(String height) {
		this(FlexComponent.Alignment.CENTER, height);
	}

	public Divider(FlexComponent.Alignment alignItems, String height) {
		setAlignItems(alignItems);
		setClassName(CLASS_NAME);
		setHeight(height);

		divider = new Div();
		UIUtils.setBackgroundColor(LumoStyles.Color.Contrast._10, divider);
		divider.setHeight("1px");
		divider.setWidthFull();
		add(divider);
	}

}
