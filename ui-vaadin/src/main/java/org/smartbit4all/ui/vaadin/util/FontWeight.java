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

public enum FontWeight {

	LIGHTER("lighter"),
	NORMAL("normal"),
	BOLD("bold"),
	BOLDER("bolder"),
	_100("100"),
	_200("200"),
	_300("300"),
	_400("400"),
	_500("500"),
	_600("600"),
	_700("700"),
	_800("800"),
	_900("900");

	private String value;

	FontWeight(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}

