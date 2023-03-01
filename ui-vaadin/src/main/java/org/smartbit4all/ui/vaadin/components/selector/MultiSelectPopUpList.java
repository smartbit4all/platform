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
package org.smartbit4all.ui.vaadin.components.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.smartbit4all.core.object.ApiObjectCollection;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.ui.vaadin.util.Buttons;
import org.smartbit4all.ui.vaadin.util.Css;
import org.smartbit4all.ui.vaadin.util.Css.AlignSelf;
import org.smartbit4all.ui.vaadin.util.Css.IconSize;
import org.smartbit4all.ui.vaadin.util.Css.Space;
import org.smartbit4all.ui.vaadin.util.Css.SpaceType;
import org.smartbit4all.ui.vaadin.util.Css.TextColor;
import org.smartbit4all.ui.vaadin.util.Icons;
import org.smartbit4all.ui.vaadin.util.Labels;
import org.springframework.util.ObjectUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.HasDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.selection.MultiSelect;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiPredicate;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.shared.Registration;
import dev.mett.vaadin.tooltip.Tooltips;

/**
 * TODO This is a major hack, since ApiObjectRef doesn't support Set yet (there's no ApiObjectSet,
 * only ApiObjectCollection, which is a List). When ApiObjectSet will be supported, we will delete
 * this class and use MultiSelectPopup instead.
 * 
 * @author Attila Mate
 * @since 2021.06.07.
 * @param <T>
 */
public class MultiSelectPopUpList<T> extends CustomField<List<T>> implements HasDataProvider<T> {

  private static final long serialVersionUID = 958524394032196451L;

  private static final String COL_DISPLAY = "COL_DISPLAY";

  private TextField displayField;
  private FlexLayout buttonBox;
  private Button btnClear;
  private Icon icnInfo;

  private Dialog dialog;
  private Label dialogHeader;
  private TextField filterField;
  private Grid<T> grid;
  private Button btndDialogSave;
  private Button btnDialogCancel;

  private SerializableBiPredicate<T, Object> filter;
  private ValueProvider<T, ?> itemDisplayValueProvider;

  private boolean tooltipComponentsEnabled = false;
  private boolean tooltipEnabled = true;
  private boolean simpleSelectAllPropertiesText = true;

  private List<T> selectedItems = Collections.emptyList();

  private Function<T, Boolean> itemDisableCalculator;

  private List<Consumer<List<T>>> onSaveListeners = new ArrayList<>();

  protected FlexLayout dialogLayout;

  public MultiSelectPopUpList() {
    super(Collections.emptyList());
    initOuterComponents();
    initDialogComponents();
    setupComponents();
  }

  private void initOuterComponents() {
    displayField = new TextField();
    displayField.setPlaceholder(getPlaceHolder());

    btnClear = Buttons.createTertiaryButton(VaadinIcon.CLOSE_SMALL);
    icnInfo = Icons.createIcon(IconSize.S, TextColor.TERTIARY, VaadinIcon.INFO_CIRCLE_O);
    ((Icon) btnClear.getIcon()).setColor(TextColor.TERTIARY);
    btnClear.getElement().getStyle().set("margin-top", "0px");
    btnClear.getElement().getStyle().set("margin-bottom", "0px");
    btnClear.getElement().getStyle().set("padding-right", "0px");
    icnInfo.getElement().getStyle().set("margin-top", "8px");
    icnInfo.getElement().getStyle().set("padding-right", "5px");
    buttonBox = new FlexLayout(btnClear, icnInfo);
    Css.setAlignSelf(AlignSelf.FLEX_END, buttonBox);

    FlexLayout layout = new FlexLayout();
    layout.setWidthFull();
    layout.setFlexDirection(FlexDirection.COLUMN);
    layout.getStyle().set("position", "relative");


    displayField.setSuffixComponent(buttonBox);
    // displayField.setClearButtonVisible(true);
    displayField.addValueChangeListener(e -> {
      if (e.isFromClient() && ObjectUtils.isEmpty(e.getValue())) {
        this.clear();
      }
    });

    layout.add(displayField);
    add(layout);
  }

  private void initDialogComponents() {
    grid = new Grid<>();
    grid.setSelectionMode(SelectionMode.MULTI);
    grid.setWidthFull();
    grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

    filterField = new TextField();
    filterField.setValueChangeMode(ValueChangeMode.EAGER);
    filterField.setClearButtonVisible(true);

    dialog = new Dialog();
    dialogLayout = new FlexLayout();
    dialogLayout.setSizeFull();
    dialogLayout.setFlexDirection(FlexDirection.COLUMN);

    dialogHeader = Labels.createH4Label(getPlaceHolder());
    Css.setMargin(SpaceType.BOTTOM, Space.Uniform.M, dialogHeader);

    FlexLayout dialogButtonLayout = new FlexLayout();
    dialogButtonLayout.setJustifyContentMode(JustifyContentMode.EVENLY);
    dialogButtonLayout.getStyle().set("margin-top", "var(--lumo-space-m)");
    btndDialogSave = Buttons.createPrimaryButton(getTranslation("title.ok"));
    btnDialogCancel = Buttons.createButton(getTranslation("title.cancel"));
    dialogButtonLayout.add(btndDialogSave, btnDialogCancel);

    dialogLayout.add(dialogHeader, grid, dialogButtonLayout);
    dialog.add(dialogLayout);
    dialog.setResizable(true);
    dialog.setWidth("25rem");

  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private void addDialogOpenerClickListener() {
    // !!! DO NOT MODIFY !!!
    // SuppressWarnings and weird casting is on purpose, otherwise compile error on
    // command line build!
    // !!! DO NOT MODIFY !!!
    ComponentUtil.addListener((Component) displayField, ClickEvent.class,
        (ComponentEventListener) e -> dialog.open());
  }

  private void setupComponents() {
    // user can't write into the text field instead the dialog opens
    addDialogOpenerClickListener();
    // clear button and info icon only visible if there are selected items
    buttonBox.setVisible(false);
    btnClear.addClickListener(e -> {
      this.clear();
    });
    btnClear.getElement().addEventListener("click", e -> {
    }).addEventData("event.stopPropagation()");

    // select/unselect items on rowclick
    grid.addItemClickListener(e -> {
      T item = e.getItem();
      MultiSelect<Grid<T>, T> multiSelect = e.getSource().asMultiSelect();
      if (item != null) {
        if (multiSelect.isSelected(item)) {
          multiSelect.deselect(item);
        } else {
          multiSelect.select(item);
        }
      }
    });

    grid.addSelectionListener(event -> {
      if (itemDisableCalculator != null) {
        event.getAllSelectedItems().forEach(item -> {

          // Revert selection if item cannot be selected
          if (itemDisableCalculator.apply(item)) {
            grid.deselect(item);
          }
        });
        grid.getDataProvider().refreshAll();
      }
    });

    /*
     * When the filter field is cleared and the select all cb was checked, it remains checked after
     * no matter if there are unchecked items in the whole list. To avoid this, we deselect and
     * select one item causing an update on the checkbox ui.
     */
    filterField.addValueChangeListener(e -> {
      if (!grid.getSelectedItems().isEmpty()) {
        T item = grid.getSelectedItems().stream().findAny().get();
        grid.asMultiSelect().deselect(item);
        grid.asMultiSelect().select(item);
      }
    });

    filterField.addValueChangeListener(e -> {
      if (this.filter != null) {
        DataProvider<T, ?> dataProvider = grid.getDataProvider();
        if (dataProvider instanceof ListDataProvider<?>) {
          ListDataProvider<T> listDataProvider = ((ListDataProvider<T>) dataProvider);
          listDataProvider.addFilter(element -> this.filter.test(element, filterField.getValue()));
        }
      }
    });


    btndDialogSave.addClickListener(e -> {
      filterField.clear();
      updateSelection();
      onSaveListeners.forEach(onSaveListener -> onSaveListener.accept(selectedItems));
      dialog.close();
    });

    /*
     * because of a vaadin grid bug that causes js exceprions poping up on the ui the grid selection
     * has to be manually cleared before detach (before closing the dialog) and reset on open... the
     * bug is fixed, but not released in vaadin platform yet :(
     * https://github.com/vaadin/vaadin-grid-flow/issues/763
     */
    btnDialogCancel.addClickListener(e -> {
      grid.asMultiSelect().deselectAll();
      dialog.close();
    });
    dialog.addOpenedChangeListener(e -> {
      if (e.isOpened()) {
        if (!selectedItems.isEmpty()) {
          grid.asMultiSelect().select(selectedItems);
        }
        if (filter != null) {
          filterField.focus();
        }
      } else {
        filterField.clear();
      }
    });
    dialog.addDialogCloseActionListener(e -> {
      grid.asMultiSelect().deselectAll();
      dialog.close();
    });

  }

  protected void updateSelection() {
    selectedItems = new ArrayList<>(grid.getSelectedItems());
    int selectionSize = selectedItems.size();
    if (selectionSize == 0) {
      displayField.clear();
      buttonBox.setVisible(false);
    } else {
      displayField.setValue(getDisplayText(selectionSize));
      buttonBox.setVisible(true);
      updateTooltip();
    }
    updateValue();
  }

  private String getDisplayText(int selectionSize) {
    if (selectionSize == 0) {
      return "";
    } else if (selectionSize == 1) {
      return getSelectedItemsDisplayText();
    } else if (simpleSelectAllPropertiesText
        && grid.getDataProvider().size(new Query<>()) == selectionSize) {
      String allSelectedTxt = getTranslation("multiselect.allselected");
      String placeHolder = getPlaceHolder();
      if (placeHolder != null && !placeHolder.isEmpty()) {
        allSelectedTxt = allSelectedTxt + " - " + getPlaceHolder();
      }
      return allSelectedTxt;
    } else {
      String placeHolder = getPlaceHolder();
      if (placeHolder == null || placeHolder.isEmpty()) {
        return String.valueOf(selectionSize) + " - " + getSelectedItemsDisplayText(", ");
      } else {
        return String.valueOf(selectionSize) + " - " + getPlaceHolder();
      }
    }
  }

  private void updateTooltip() {
    if (tooltipEnabled) {
      if (tooltipComponentsEnabled) {
        Component tooltipDisplay = getSelectedItemsDisplayComponent();
        // the current tooltip implementation handles only string parameters
        // but as a workaround we give the dom of the component
        String htmlOfDisplay = tooltipDisplay.getElement().getOuterHTML();
        htmlOfDisplay = htmlOfDisplay.replaceAll("\\r|\\n", "");
        Tooltips.getCurrent().setTooltip(icnInfo, htmlOfDisplay);
      } else {
        // Tooltips.getCurrent().setTooltip(icnInfo, getSelectedItemsDisplayText());
        icnInfo.getElement().setAttribute("title", getSelectedItemsDisplayText());
      }
    }
  }

  private String getSelectedItemsDisplayText() {
    return getSelectedItemsDisplayText("\n");
  }

  private String getSelectedItemsDisplayText(String separator) {
    List<?> displayItems = getSelectedItemsDisplay();

    List<String> displayItemsAsString = displayItems.stream().map(itemDisplay -> {
      if (itemDisplay instanceof Component) {
        String componentHtml = ((Component) itemDisplay).getElement().getOuterHTML();
        String componentHtmlWithoutTagsAndNewlines = componentHtml.replaceAll("\\<[^>]*>", "")
            .replaceAll("\\r|\\n", "");
        return componentHtmlWithoutTagsAndNewlines;
      } else {
        return String.valueOf(itemDisplay);
      }
    }).collect(Collectors.toList());

    return String.join(separator, displayItemsAsString);
  }

  /**
   * Returns the display components of the selected items. If only String values are displayed then
   * it is wrapped in a {@link Text}.
   */
  private Component getSelectedItemsDisplayComponent() {
    List<?> displayItems = getSelectedItemsDisplay();

    Div itemsDiv = new Div();
    for (Object itemDisplay : displayItems) {
      if (itemDisplay instanceof Component) {
        itemsDiv.add((Component) itemDisplay);
      } else {
        itemsDiv.add(new Text(String.valueOf(itemDisplay)));
      }
      itemsDiv.add(new HtmlComponent("br"));
    }
    return itemsDiv;
  }

  private List<?> getSelectedItemsDisplay() {
    List<?> displayItems = grid.getSelectedItems().stream().map(itemDisplayValueProvider)
        .collect(Collectors.toList());

    // if the displays are comparable, sort them
    if (displayItems.stream().allMatch(Comparable.class::isInstance)) {
      List<Comparable> comparableList = (List<Comparable>) displayItems;
      Collections.sort(comparableList);
    }
    return displayItems;
  }

  private void addGridColumn(ValueProvider<T, ?> itemDisplayValueProvider, String columnName) {
    Column<T> column = grid.addColumn(new ComponentRenderer<Component, T>(item -> {
      Object display = itemDisplayValueProvider.apply(item);
      if (display instanceof Component) {
        return (Component) display;
      } else {
        Div div = new Div();
        div.add(String.valueOf(display));
        return div;
      }
    })).setAutoWidth(true).setKey(COL_DISPLAY);
    if (filter != null) {
      column.setHeader(filterField);
    }
    if (columnName != null) {
      column.setHeader(columnName);
    }
  }

  public void setItemDisplayValueProvider(ValueProvider<T, ?> itemDisplayValueProvider) {
    setItemDisplayValueProvider(itemDisplayValueProvider, null);
  }

  public void setItemDisplayValueProvider(ValueProvider<T, ?> itemDisplayValueProvider,
      String columnName) {
    Objects.requireNonNull(itemDisplayValueProvider);
    this.itemDisplayValueProvider = itemDisplayValueProvider;

    if (grid.getColumnByKey(COL_DISPLAY) != null) {
      grid.removeColumnByKey(COL_DISPLAY);
    }
    addGridColumn(itemDisplayValueProvider, columnName);
  }

  public void addColumn(ValueProvider<T, ?> columnValueProvider, String columnName) {
    Objects.requireNonNull(columnValueProvider);
    grid.addColumn(item -> columnValueProvider.apply(item)).setHeader(columnName).setAutoWidth(true)
        .setResizable(true);
  }

  public SerializableBiPredicate<T, Object> getFilter() {
    return filter;
  }

  public void setFilter(SerializableBiPredicate<T, Object> filter) {
    this.filter = filter;
    filterField.clear();
    Column<T> column = grid.getColumnByKey(COL_DISPLAY);
    if (column != null) {
      if (filter == null) {
        column.setHeader("");
      } else {
        column.setHeader(filterField);
      }
    }
  }

  public void setItemDisableCalculator(Function<T, Boolean> itemSelectCalculator) {
    Objects.requireNonNull(itemSelectCalculator, "itemSelectCalculator can not be null!");
    itemDisableCalculator = itemSelectCalculator;
    grid.setClassNameGenerator(
        item -> itemSelectCalculator.apply(item) ? "no-select"
            : null);
  }

  @Override
  public void setDataProvider(DataProvider<T, ?> dataProvider) {
    grid.setDataProvider(dataProvider);
  }

  public DataProvider<T, ?> getDataProvider() {
    return grid.getDataProvider();
  }

  @Override
  protected List<T> generateModelValue() {
    return new ArrayList<>(grid.getSelectedItems());
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void setPresentationValue(List<T> newPresentationValue) {
    if (newPresentationValue == null) {
      clear();
    }
    if (newPresentationValue instanceof ApiObjectCollection) {
      newPresentationValue = (List<T>) ((ApiObjectCollection) newPresentationValue).stream()
          .map(ApiObjectRef::getObject)
          .collect(Collectors.toList());
    }
    grid.asMultiSelect().setValue(new HashSet<>(newPresentationValue));
    updateSelection();
  }

  @Override
  public void clear() {
    super.clear();
    selectedItems = Collections.emptyList();
    buttonBox.setVisible(false);
    grid.asMultiSelect().deselectAll();
    displayField.clear();
  }

  public boolean isTooltipComponentsEnabled() {
    return tooltipComponentsEnabled;
  }

  public void setTooltipComponentsEnabled(boolean tooltipComponentsEnabled) {
    this.tooltipComponentsEnabled = tooltipComponentsEnabled;
  }

  public boolean isTooltipEnabled() {
    return tooltipEnabled;
  }

  public void setTooltipEnabled(boolean tooltipEnabled) {
    this.tooltipEnabled = tooltipEnabled;
  }

  public String getPlaceHolder() {
    return displayField.getPlaceholder();
  }

  public void setPlaceholder(String placeholder) {
    displayField.setPlaceholder(placeholder);
    dialogHeader.setText(placeholder);
  }

  public void setRequired(boolean required) {
    displayField.setRequired(required);
  }

  public boolean isRequired() {
    return displayField.isRequired();
  }

  @Override
  public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
    displayField.setRequiredIndicatorVisible(requiredIndicatorVisible);
  }

  @Override
  public boolean isRequiredIndicatorVisible() {
    return displayField.isRequiredIndicatorVisible();
  }

  public Registration addDisplayValueChangeListener(
      ValueChangeListener<? super ComponentValueChangeEvent<TextField, String>> listener) {
    return displayField.addValueChangeListener(listener);
  }

  @Override
  public List<T> getValue() {
    return selectedItems;
  }

  public Set<T> getGridValues() {
    return grid.asMultiSelect().getSelectedItems();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    displayField.setEnabled(enabled);
    btnClear.setEnabled(enabled);
  }

  public Registration addOnSaveListener(Consumer<List<T>> listener) {
    return Registration.addAndRemove(onSaveListeners, listener);
  }

  public void openDialog() {
    dialog.open();
  }

  public void setSelection(List<T> selection) {
    grid.asMultiSelect().select(selection);
    updateSelection();
  }

  public MultiSelect<Grid<T>, T> asMultiSelect() {
    return grid.asMultiSelect();
  }

  public boolean isSimpleSelectAllPropertiesText() {
    return simpleSelectAllPropertiesText;
  }

  public void setSimpleSelectAllPropertiesText(boolean simpleSelectAllPropertiesText) {
    this.simpleSelectAllPropertiesText = simpleSelectAllPropertiesText;
  }

  public void setDialogMinWidth(String ertek) {
    dialog.setMinWidth(ertek);
  }

  @Override
  public void setHeight(String ertek) {
    dialog.setHeight(ertek);
  }

  @Override
  public void setWidth(String ertek) {
    dialog.setWidth(ertek);
  }

}
