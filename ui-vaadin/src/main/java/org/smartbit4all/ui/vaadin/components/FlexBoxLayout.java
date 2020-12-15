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

import java.util.ArrayList;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.theme.lumo.Lumo;

import org.smartbit4all.ui.vaadin.layout.size.Size;
import org.smartbit4all.ui.vaadin.util.css.BorderRadius;
import org.smartbit4all.ui.vaadin.util.css.BoxSizing;
import org.smartbit4all.ui.vaadin.util.css.Display;
import org.smartbit4all.ui.vaadin.util.css.Overflow;
import org.smartbit4all.ui.vaadin.util.css.Position;
import org.smartbit4all.ui.vaadin.util.css.Shadow;

public class FlexBoxLayout extends FlexLayout {

	public static final String BACKGROUND_COLOR = "background-color";
	public static final String BORDER_RADIUS = "border-radius";
	public static final String BOX_SHADOW = "box-shadow";
	public static final String BOX_SIZING = "box-sizing";
	public static final String DISPLAY = "display";
	public static final String FLEX_DIRECTION = "flex-direction";
	public static final String FLEX_WRAP = "flex-wrap";
	public static final String MAX_WIDTH = "max-width";
	public static final String OVERFLOW = "overflow";
	public static final String POSITION = "position";

	private ArrayList<Size> spacings;

	public FlexBoxLayout(Component... components) {
		super(components);
		spacings = new ArrayList<>();
	}

	public void setBackgroundColor(String value) {
		getStyle().set(BACKGROUND_COLOR, value);
	}

	public void setBackgroundColor(String value, String theme) {
		getStyle().set(BACKGROUND_COLOR, value);
		setTheme(theme);
	}

	public void removeBackgroundColor() {
		getStyle().remove(BACKGROUND_COLOR);
	}

	public void setBorderRadius(BorderRadius radius) {
		getStyle().set(BORDER_RADIUS, radius.getValue());
	}

	public void removeBorderRadius() {
		getStyle().remove(BORDER_RADIUS);
	}

	public void setBoxSizing(BoxSizing sizing) {
		getStyle().set(BOX_SIZING, sizing.getValue());
	}

	public void removeBoxSizing() {
		getStyle().remove(BOX_SIZING);
	}

	public void setDisplay(Display display) {
		getStyle().set(DISPLAY, display.getValue());
	}

	public void removeDisplay() {
		getStyle().remove(DISPLAY);
	}

	public void setFlex(String value, Component... components) {
		for (Component component : components) {
			component.getElement().getStyle().set("flex", value);
		}
	}

	public void setMargin(Size... sizes) {
		for (Size size : sizes) {
			for (String attribute : size.getMarginAttributes()) {
				getStyle().set(attribute, size.getVariable());
			}
		}
	}

	public void removeMargin() {
		getStyle().remove("margin");
		getStyle().remove("margin-bottom");
		getStyle().remove("margin-left");
		getStyle().remove("margin-right");
		getStyle().remove("margin-top");
	}

	@Override
	public void setMaxWidth(String value) {
		getStyle().set(MAX_WIDTH, value);
	}

	public void removeMaxWidth() {
		getStyle().remove(MAX_WIDTH);
	}

	public void setOverflow(Overflow overflow) {
		getStyle().set(OVERFLOW, overflow.getValue());
	}

	public void removeOverflow() {
		getStyle().remove(OVERFLOW);
	}

	public void setPadding(Size... sizes) {
		removePadding();
		for (Size size : sizes) {
			for (String attribute : size.getPaddingAttributes()) {
				getStyle().set(attribute, size.getVariable());
			}
		}
	}

	public void removePadding() {
		getStyle().remove("padding");
		getStyle().remove("padding-bottom");
		getStyle().remove("padding-left");
		getStyle().remove("padding-right");
		getStyle().remove("padding-top");
	}

	public void setPosition(Position position) {
		getStyle().set(POSITION, position.getValue());
	}

	public void removePosition() {
		getStyle().remove(POSITION);
	}

	public void setShadow(Shadow shadow) {
		getStyle().set(BOX_SHADOW, shadow.getValue());
	}

	public void removeShadow() {
		getStyle().remove(BOX_SHADOW);
	}

	public void setSpacing(Size... sizes) {
		// Remove old styles (if applicable)
		for (Size spacing : spacings) {
			removeClassName(spacing.getSpacingClassName());
		}
		spacings.clear();

		// Add new
		for (Size size : sizes) {
			addClassName(size.getSpacingClassName());
			spacings.add(size);
		}
	}

	public void setTheme(String theme) {
		if (Lumo.DARK.equals(theme)) {
			getElement().setAttribute("theme", "dark");
		} else {
			getElement().removeAttribute("theme");
		}
	}
}
