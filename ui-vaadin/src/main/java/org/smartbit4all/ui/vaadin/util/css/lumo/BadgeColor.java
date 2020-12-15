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
package org.smartbit4all.ui.vaadin.util.css.lumo;

public enum BadgeColor {

	NORMAL("badge"), NORMAL_PRIMARY("badge primary"), SUCCESS(
			"badge success"), SUCCESS_PRIMARY("badge success primary"), ERROR(
			"badge error"), ERROR_PRIMARY(
			"badge error primary"), CONTRAST(
			"badge contrast"), CONTRAST_PRIMARY(
			"badge contrast primary");

	private String style;

	BadgeColor(String style) {
		this.style = style;
	}

	public String getThemeName() {
		return style;
	}

}
