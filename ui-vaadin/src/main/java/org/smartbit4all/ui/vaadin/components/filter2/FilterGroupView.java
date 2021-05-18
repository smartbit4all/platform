package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.ui.common.filter2.model.FilterGroupLabel;
import org.smartbit4all.ui.vaadin.util.Css;
import org.smartbit4all.ui.vaadin.util.Css.IconSize;
import org.smartbit4all.ui.vaadin.util.Css.TextColor;
import org.smartbit4all.ui.vaadin.util.Icons;
import org.smartbit4all.ui.vaadin.util.Labels;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DropEvent;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterGroupView extends FlexLayout implements DropTarget<FlexLayout> {

  private static final String VISIBLE_GROUP = "filtergroup";
  private static final String INVISIBLE_GROUP = "filtergroup-transparent";
  private static final String ACTIVE_GROUP = "active-group";

  private ObservableObject filterGroup;

  private FlexLayout iconLayout;
  private FlexLayout filtersLayout;
  private FlexLayout buttonsLayout;
  private Button btnAddChildGroup;
  private Button btnRemoveGroup;
  private Button btnGroupType;
  private FlexLayout ctrlButtonsLayout;
  private Div flexibleSeparator;
  // private FilterGroupType groupType;

  public FilterGroupView(ObservableObject filterGroup) {
    this.filterGroup = filterGroup;
    createUI();
  }

  private void createUI() {
    setActive(true);
    iconLayout = new FlexLayout();
    iconLayout.addClassName("icon-layout");

    addDropListener(createDropListener());

    filtersLayout = new FlexLayout();
    filtersLayout.addClassName("filters-group-layout");
    add(filtersLayout);
    flexibleSeparator = new Div();
    flexibleSeparator.addClassName("filter-buttons-separator");

    btnGroupType = new Button();
    btnGroupType.addClickListener(groupTypeChangeListener());
    Css.stopClickEventPropagation(btnGroupType);

    btnAddChildGroup = new Button("+");
    btnAddChildGroup.addClickListener(addChildGroupListener());
    btnAddChildGroup.addClassName("filter-addchildgroup");
    Css.stopClickEventPropagation(btnAddChildGroup);

    btnRemoveGroup = new Button("x");
    btnRemoveGroup.addClickListener(removeGroupListener());
    btnRemoveGroup.addClassName("filter-removegroup");
    Css.stopClickEventPropagation(btnRemoveGroup);

    buttonsLayout = new FlexLayout();
    buttonsLayout.addClassName("filter-buttons");
    ctrlButtonsLayout = new FlexLayout();


    filterGroup.onPropertyChange(null, "childGroupAllowed", this::onChildGroupAllowedChange);
    filterGroup.onPropertyChange(null, "groupType", this::onGroupType);
    filterGroup.onPropertyChange(null, "groupTypeChangeEnabled", this::onGroupTypeChangeEnabled);
    filterGroup.onPropertyChange(null, "closeable", this::onCloseable);
    filterGroup.onPropertyChange(null, "visible", this::onVisible);
    filterGroup.onPropertyChange(null, "active", this::onActive);
    filterGroup.onReferencedObjectChange(null, "label", this::labelChange);

  }

  private ComponentEventListener<ClickEvent<Button>> removeGroupListener() {
    // TODO Auto-generated method stub
    return null;
  }

  private ComponentEventListener<ClickEvent<Button>> addChildGroupListener() {
    // TODO Auto-generated method stub
    return null;
  }

  private ComponentEventListener<ClickEvent<Button>> groupTypeChangeListener() {
    // TODO Auto-generated method stub
    return null;
  }

  private ComponentEventListener<ClickEvent<FlexLayout>> groupChangeListener() {
    // TODO Auto-generated method stub
    return null;
  }

  private ComponentEventListener<DropEvent<FlexLayout>> createDropListener() {
    return e -> {
      // TODO
      // e.getDragData().ifPresent(data -> {
      // if (data instanceof FilterFieldUI) {
      // controller.changeGroup(((FilterFieldUI) data).getGroup().groupId, groupId,
      // ((FilterFieldUI) data).getFilterId());
      // } else if (data instanceof FilterSelectorUI) {
      // controller.activeFilterGroupChanged(groupId);
      // controller.addFilterField(((FilterSelectorUI) data).getSelectorId());
      // }
      // });
    };
  }


  private void onChildGroupAllowedChange(PropertyChange change) {
    Boolean childGroupAllowed = (Boolean) change.getNewValue();
    if (Boolean.TRUE.equals(childGroupAllowed)) {
      addClassName("child-group");
      addToLayout(ctrlButtonsLayout, btnAddChildGroup);
      addClickListener(groupChangeListener());
      Css.stopClickEventPropagation(this);
    } else {
      removeClassName("child-group");
      removeFromLayout(ctrlButtonsLayout, btnAddChildGroup);
    }
  }

  private void addToLayout(FlexLayout layout, Component... comps) {
    boolean anyChange = false;
    for (int i = 0; i < comps.length; i++) {
      Component comp = comps[i];
      if (!comp.getParent().isPresent() || comp.getParent().get() != layout) {
        layout.add(comp);
        anyChange = true;
      }
    }
    if (anyChange) {
      sanitizeLayouts();
    }
  }

  private void removeFromLayout(FlexLayout layout, Component... comps) {
    boolean anyChange = false;
    for (int i = 0; i < comps.length; i++) {
      Component comp = comps[i];
      if (comp.getParent().isPresent() && comp.getParent().get() == layout) {
        layout.remove(comp);
        anyChange = true;
      }
    }
    if (anyChange) {
      sanitizeLayouts();
    }
  }

  private void sanitizeLayouts() {
    if (ctrlButtonsLayout.getComponentCount() > 0) {
      addToLayout(buttonsLayout, ctrlButtonsLayout);
    } else {
      removeFromLayout(buttonsLayout, ctrlButtonsLayout);
    }
    if (buttonsLayout.getComponentCount() > 0) {
      addToLayout(filtersLayout, flexibleSeparator, buttonsLayout);
    } else {
      removeFromLayout(filtersLayout, flexibleSeparator, buttonsLayout);
    }
    if (iconLayout.getComponentCount() != 0) {
      if (!iconLayout.getParent().isPresent()) {
        addComponentAsFirst(iconLayout);
      }
    } else {
      if (iconLayout.getParent().isPresent()) {
        remove(iconLayout);
      }
    }

  }

  private void onGroupType(PropertyChange change) {
    FilterGroupType groupType = (FilterGroupType) change.getNewValue();
    btnGroupType.setText(groupType.getValue());
  }

  private void onGroupTypeChangeEnabled(PropertyChange change) {
    Boolean groupTypeChangeEnabled = (Boolean) change.getNewValue();
    if (Boolean.TRUE.equals(groupTypeChangeEnabled)) {
      addToLayout(buttonsLayout, btnGroupType);
    } else {
      removeFromLayout(buttonsLayout, btnGroupType);
    }
  }

  private void onCloseable(PropertyChange change) {
    Boolean closeable = (Boolean) change.getNewValue();
    if (Boolean.TRUE.equals(closeable)) {
      addToLayout(ctrlButtonsLayout, btnRemoveGroup);
    } else {
      removeFromLayout(ctrlButtonsLayout, btnRemoveGroup);
    }
  }

  private void onVisible(PropertyChange change) {
    Boolean visible = (Boolean) change.getNewValue();
    if (Boolean.TRUE.equals(visible)) {
      removeClassName(INVISIBLE_GROUP);
      addClassName(VISIBLE_GROUP);
      if (iconLayout.getComponentCount() != 0) {
        addComponentAsFirst(iconLayout);
      }
    } else {
      removeClassName(VISIBLE_GROUP);
      addClassName(INVISIBLE_GROUP);
      iconLayout.removeAll();
      remove(iconLayout);
    }
  }

  private void onActive(PropertyChange change) {
    Boolean active = (Boolean) change.getNewValue();
    if (Boolean.TRUE.equals(active)) {
      addClassName(ACTIVE_GROUP);
    } else {
      removeClassName(ACTIVE_GROUP);
    }
  }

  private void labelChange(ReferencedObjectChange change) {
    FilterGroupLabel groupLabel = (FilterGroupLabel) change.getChange().getObject();
    String icon = groupLabel.getIconCode();
    String label = groupLabel.getLabelCode();
    iconLayout.removeAll();
    if (icon != null && icon.length() > 0) {
      VaadinIcon vaadinIcon = VaadinIcon.valueOf(icon.toUpperCase());
      Icon filterIcon = Icons.createIcon(IconSize.S, TextColor.TERTIARY, vaadinIcon);
      filterIcon.addClassName("filter-icon");
      iconLayout.add(filterIcon);
    }

    if (label != null && label.length() > 0) {
      Label filterLabel = Labels.createH4Label(getTranslation(label));
      filterLabel.addClassName("filter-label");
      iconLayout.add(filterLabel);
    }
    sanitizeLayouts();

  }


}
