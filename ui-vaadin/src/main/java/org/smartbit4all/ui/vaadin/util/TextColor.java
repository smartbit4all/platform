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

public enum TextColor {

	HEADER("var(--lumo-header-text-color)"),
	BODY("var(--lumo-body-text-color)"),
	SECONDARY("var(--lumo-secondary-text-color)"),
	TERTIARY("var(--lumo-tertiary-text-color)"),
	DISABLED("var(--lumo-disabled-text-color)"),
	PRIMARY("var(--lumo-primary-text-color)"),
	PRIMARY_CONTRAST("var(--lumo-primary-contrast-color)"),
	ERROR("var(--lumo-error-text-color)"),
	ERROR_CONTRAST("var(--lumo-error-contrast-color)"),
	SUCCESS("var(--lumo-success-text-color)"),
	SUCCESS_CONTRAST("var(--lumo-success-contrast-color)");

	private String value;

	TextColor(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
