/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components.navigation.bar;

import java.util.ArrayList;
import org.smartbit4all.ui.vaadin.components.navigation.tab.NaviTab;
import org.smartbit4all.ui.vaadin.components.navigation.tab.NaviTabs;
import org.smartbit4all.ui.vaadin.util.Buttons;
import org.smartbit4all.ui.vaadin.util.Css;
import org.smartbit4all.ui.vaadin.view.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

@CssImport("./styles/components/app-bar.css")
public class AppBar extends FlexLayout {

  private String CLASS_NAME = "app-bar";

  private FlexLayout container;

  private Button menuIcon;
  private Button contextIcon;

  private H4 title;
  private FlexLayout actionItems;
  private Icon avatar;
  private Button themeButton;

  private FlexLayout tabContainer;
  private NaviTabs tabs;
  private ArrayList<Registration> tabSelectionListeners;
  private Button addTab;

  private TextField search;
  private Registration searchRegistration;

  private Class<? extends Component> homeView;

  public enum NaviMode {
    MENU, CONTEXTUAL
  }

  public AppBar(Class<? extends Component> homeView, String title, NaviTab... tabs) {
    setClassName(CLASS_NAME);

    this.homeView = homeView;

    initMenuIcon();
    initContextIcon();
    initTitle(title);
    initSearch();
    initThemeButton();
    initAvatar();
    initActionItems();
    initContainer();
    initTabs(tabs);
  }

  private void initThemeButton() {
    themeButton = new Button(new Icon(VaadinIcon.ADJUST));
    themeButton.addClassName(CLASS_NAME + "__theme-button");

  }

  public void setNaviMode(NaviMode mode) {
    if (mode.equals(NaviMode.MENU)) {
      menuIcon.setVisible(true);
      contextIcon.setVisible(false);
    } else {
      menuIcon.setVisible(false);
      contextIcon.setVisible(true);
    }
  }

  private void initMenuIcon() {
    menuIcon = Buttons.createTertiaryInlineButton(VaadinIcon.MENU);
    menuIcon.addClassName(CLASS_NAME + "__navi-icon");
    menuIcon.addClickListener(e -> MainLayout.get().getNaviDrawer().toggle());
    Css.setAriaLabel("Menu", menuIcon);
    Css.setLineHeight("1", menuIcon);
  }

  private void initContextIcon() {
    contextIcon = Buttons.createTertiaryInlineButton(VaadinIcon.ARROW_LEFT);
    contextIcon.addClassNames(CLASS_NAME + "__context-icon");
    contextIcon.setVisible(false);
    Css.setAriaLabel("Back", contextIcon);
    Css.setLineHeight("1", contextIcon);
  }

  private void initTitle(String title) {
    this.title = new H4(title);
    this.title.setClassName(CLASS_NAME + "__title");
  }

  private void initSearch() {
    search = new TextField();
    search.setPlaceholder("Search");
    search.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
    search.setVisible(false);
  }

  private void initAvatar() {
    avatar = VaadinIcon.USER_CARD.create();
    avatar.setClassName(CLASS_NAME + "__avatar");

    ContextMenu contextMenu = new ContextMenu(avatar);
    contextMenu.setOpenOnClick(true);
    contextMenu.addItem(getTranslation(
        "appbar.logout"),
        e -> UI.getCurrent().getPage().executeJs("window.open(\"/logout\", \"_self\");"));
  }

  private void initActionItems() {
    actionItems = new FlexLayout();
    actionItems.addClassName(CLASS_NAME + "__action-items");
    actionItems.setVisible(false);
  }

  private void initContainer() {
    container = new FlexLayout(menuIcon, contextIcon, this.title, search, actionItems,
        themeButton, avatar);
    container.addClassName(CLASS_NAME + "__container");
    container.setAlignItems(FlexComponent.Alignment.CENTER);
    container.setFlexGrow(1, search);
    add(container);
  }

  private void initTabs(NaviTab... tabs) {
    addTab = Buttons.createSmallButton(VaadinIcon.PLUS);
    addTab.addClickListener(e -> this.tabs.setSelectedTab(addClosableNaviTab("New Tab", homeView)));
    addTab.setVisible(false);

    this.tabs = tabs.length > 0 ? new NaviTabs(homeView, tabs) : new NaviTabs(homeView);
    this.tabs.setClassName(CLASS_NAME + "__tabs");
    this.tabs.setVisible(false);
    for (NaviTab tab : tabs) {
      configureTab(tab);
    }

    this.tabSelectionListeners = new ArrayList<>();

    tabContainer = new FlexLayout(this.tabs, addTab);
    tabContainer.addClassName(CLASS_NAME + "__tab-container");
    tabContainer.setAlignItems(FlexComponent.Alignment.CENTER);
    add(tabContainer);
  }

  public void addComponentToTabContainer(Component componentToAdd) {
    // getElement().appendChild(componentToAdd.get);
    // tabContainer.add(componentToAdd);
    tabContainer.getElement().appendChild(componentToAdd.getElement());
  }

  public void removeComponentFromTabContainer(Component componentToAdd) {
    tabContainer.getElement().removeChild(componentToAdd.getElement());
  }

  /* === MENU ICON === */

  public Button getMenuIcon() {
    return menuIcon;
  }

  /* === CONTEXT ICON === */

  public Button getContextIcon() {
    return contextIcon;
  }

  public void setContextIcon(Icon icon) {
    contextIcon.setIcon(icon);
  }

  /* === TITLE === */

  public String getTitle() {
    return this.title.getText();
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  /* === ACTION ITEMS === */

  public Component addActionItem(Component component) {
    actionItems.add(component);
    updateActionItemsVisibility();
    return component;
  }

  public Button addActionItem(VaadinIcon icon) {
    Button button =
        Buttons.createButton(icon, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
    addActionItem(button);
    return button;
  }

  public void removeAllActionItems() {
    actionItems.removeAll();
    updateActionItemsVisibility();
  }

  /* === AVATAR == */

  public Icon getAvatar() {
    return avatar;
  }

  /* === THEME BUTTON == */

  public Button getThemeButton() {
    return themeButton;
  }

  /* === TABS === */

  public void centerTabs() {
    tabs.getStyle().set("margin-left", "auto");
    tabs.getStyle().set("margin-right", "auto");
  }

  private void configureTab(Tab tab) {
    tab.addClassName(CLASS_NAME + "__tab");
    updateTabsVisibility();
  }

  public Tab addTab(String text) {
    Tab tab = tabs.addTab(text);
    configureTab(tab);
    return tab;
  }

  public Tab addTab(String text, Class<? extends Component> navigationTarget) {
    Tab tab = tabs.addTab(text, navigationTarget);
    configureTab(tab);
    return tab;
  }

  public Tab addClosableNaviTab(String text, Class<? extends Component> navigationTarget) {
    Tab tab = tabs.addClosableTab(text, navigationTarget);
    configureTab(tab);
    return tab;
  }

  public Tab getSelectedTab() {
    return tabs.getSelectedTab();
  }

  public void setSelectedTab(Tab selectedTab) {
    tabs.setSelectedTab(selectedTab);
  }

  public void updateSelectedTab(String text, Class<? extends Component> navigationTarget) {
    tabs.updateSelectedTab(text, navigationTarget);
  }

  public void navigateToSelectedTab() {
    tabs.navigateToSelectedTab();
  }

  public void addTabSelectionListener(ComponentEventListener<Tabs.SelectedChangeEvent> listener) {
    Registration registration = tabs.addSelectedChangeListener(listener);
    tabSelectionListeners.add(registration);
  }

  public int getTabCount() {
    return tabs.getTabCount();
  }

  public void removeAllTabs() {
    tabSelectionListeners.forEach(registration -> registration.remove());
    tabSelectionListeners.clear();
    tabs.removeAll();
    updateTabsVisibility();
  }

  /* === ADD TAB BUTTON === */

  public void setAddTabVisible(boolean visible) {
    addTab.setVisible(visible);
  }

  /* === SEARCH === */

  public void searchModeOn() {
    menuIcon.setVisible(false);
    title.setVisible(false);
    actionItems.setVisible(false);
    tabContainer.setVisible(false);

    contextIcon.setIcon(new Icon(VaadinIcon.ARROW_BACKWARD));
    contextIcon.setVisible(true);
    searchRegistration = contextIcon.addClickListener(e -> searchModeOff());

    search.setVisible(true);
    search.focus();
  }

  public void addSearchListener(HasValue.ValueChangeListener listener) {
    search.addValueChangeListener(listener);
  }

  public void setSearchPlaceholder(String placeholder) {
    search.setPlaceholder(placeholder);
  }

  private void searchModeOff() {
    menuIcon.setVisible(true);
    title.setVisible(true);
    tabContainer.setVisible(true);

    updateActionItemsVisibility();
    updateTabsVisibility();

    contextIcon.setVisible(false);
    searchRegistration.remove();

    search.clear();
    search.setVisible(false);
  }

  /* === RESET === */

  public void reset() {
    title.setText("");
    setNaviMode(AppBar.NaviMode.MENU);
    removeAllActionItems();
    removeAllTabs();
  }

  /* === UPDATE VISIBILITY === */

  private void updateActionItemsVisibility() {
    actionItems.setVisible(actionItems.getComponentCount() > 0);
  }

  private void updateTabsVisibility() {
    tabs.setVisible(tabs.getComponentCount() > 0);
  }
}
