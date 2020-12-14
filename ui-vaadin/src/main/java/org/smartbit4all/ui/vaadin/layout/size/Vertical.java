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
package org.smartbit4all.ui.vaadin.layout.size;

public enum Vertical implements Size {

	AUTO("auto", null),

	XS("var(--lumo-space-xs)", "spacing-v-xs"), S("var(--lumo-space-s)",
			"spacing-v-s"), M("var(--lumo-space-m)", "spacing-v-m"), L(
			"var(--lumo-space-l)",
			"spacing-v-l"), XL("var(--lumo-space-xl)", "spacing-v-xl"),

	RESPONSIVE_M("var(--lumo-space-r-m)", null), RESPONSIVE_L(
			"var(--lumo-space-r-l)",
			null), RESPONSIVE_X("var(--lumo-space-r-x)", null);

	private String variable;
	private String spacingClassName;

	Vertical(String variable, String spacingClassName) {
		this.variable = variable;
		this.spacingClassName = spacingClassName;
	}

	@Override
	public String[] getMarginAttributes() {
		return new String[]{"margin-bottom", "margin-top"};
	}

	@Override
	public String[] getPaddingAttributes() {
		return new String[]{"padding-bottom", "padding-top"};
	}

	@Override
	public String getSpacingClassName() {
		return this.spacingClassName;
	}

	@Override
	public String getVariable() {
		return this.variable;
	}
}
