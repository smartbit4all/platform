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
package org.smartbit4all.ui.vaadin.util;

public enum FontSize {

	XXS("var(--lumo-font-size-xxs)"),
	XS("var(--lumo-font-size-xs)"),
	S("var(--lumo-font-size-s)"),
	M("var(--lumo-font-size-m)"),
	L("var(--lumo-font-size-l)"),
	XL("var(--lumo-font-size-xl)"),
	XXL("var(--lumo-font-size-xxl)"),
	XXXL("var(--lumo-font-size-xxxl)");

	private String value;

	FontSize(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
