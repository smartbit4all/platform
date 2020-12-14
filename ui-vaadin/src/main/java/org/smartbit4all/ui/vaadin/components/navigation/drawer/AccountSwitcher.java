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
package org.smartbit4all.ui.vaadin.components.navigation.drawer;

import static org.smartbit4all.ui.vaadin.util.UIUtils.IMG_PATH;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;

import org.smartbit4all.ui.vaadin.util.UIUtils;

@CssImport("./styles/components/account-switcher.css")
public class AccountSwitcher extends Div {

	private String CLASS_NAME = "account-switcher";

	private Image avatar;
	private H4 username;
	private Label email;
	private Button dropdown;
	private ContextMenu menu;

	public AccountSwitcher() {
		setClassName(CLASS_NAME);

		initAvatar();
		initUsername();
		initEmail();
	}

	private void initAvatar() {
		avatar = new Image();
		avatar.addClassName(CLASS_NAME + "__avatar");
		avatar.setSrc(IMG_PATH + "avatar.png");
		add(avatar);
	}

	private void initUsername() {
		username = new H4("Carmen Beltrán");
		username.addClassName(CLASS_NAME + "__title");
		add(username);
	}

	private void initEmail() {
		email = new Label("john.smith@gmail.com");
		email.addClassName(CLASS_NAME + "__email");

		dropdown = UIUtils.createButton(VaadinIcon.ANGLE_DOWN, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
		email.add(dropdown);

		menu = new ContextMenu(dropdown);
		menu.setOpenOnClick(true);
		menu.addItem("carmen.beltran@google.com", e -> System.out.println("Testing..."));
		menu.addItem("carmen.beltran@apple.com", e -> System.out.println("Testing..."));

		add(email);
	}
}
