package org.smartbit4all.ui.vaadin.components.filter2;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.api.filter.bean.FilterGroupType;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.ui.common.filter2.model.FilterGroupLabel;
import org.smartbit4all.ui.vaadin.util.Css;
import org.smartbit4all.ui.vaadin.util.Css.IconSize;
import org.smartbit4all.ui.vaadin.util.Css.TextColor;
import org.smartbit4all.ui.vaadin.util.Icons;
import org.smartbit4all.ui.vaadin.util.Labels;
import org.smartbit4all.ui.vaadin.util.Layouts;
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
import com.vaadin.flow.shared.Registration;

public class FilterGroupView extends FlexLayout implements DropTarget<FlexLayout> {

  private static final String VISIBLE_GROUP = "filtergroup";
  private static final String INVISIBLE_GROUP = "filtergroup-transparent";
  private static final String ACTIVE_GROUP = "active-group";

  private ObservableObject filterGroup;
  private ObjectEditing viewModel;
  private String path;

  private FlexLayout iconLayout;
  private FlexLayout filtersLayout;
  private FlexLayout buttonsLayout;
  private Button btnAddChildGroup;
  private Button btnRemoveGroup;
  private Button btnGroupType;
  private FlexLayout ctrlButtonsLayout;
  private Div flexibleSeparator;

  private final Map<String, FilterFieldView> filterFields = new HashMap<>();
  private final Map<String, FilterGroupView> filterGroups = new HashMap<>();
  private Registration groupClickListener;

  public FilterGroupView(ObjectEditing viewModel, ObservableObject filterGroup, String path) {
    this.viewModel = viewModel;
    this.filterGroup = filterGroup;
    this.path = path;
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
    btnGroupType.addClickListener(groupTypeClickListener());
    Css.stopClickEventPropagation(btnGroupType);

    btnAddChildGroup = new Button("+");
    btnAddChildGroup.addClickListener(addChildGroupClickListener());
    btnAddChildGroup.addClassName("filter-addchildgroup");
    Css.stopClickEventPropagation(btnAddChildGroup);

    btnRemoveGroup = new Button("x");
    btnRemoveGroup.addClickListener(removeGroupClickListener());
    btnRemoveGroup.addClassName("filter-removegroup");
    Css.stopClickEventPropagation(btnRemoveGroup);

    buttonsLayout = new FlexLayout();
    buttonsLayout.addClassName("filter-buttons");
    ctrlButtonsLayout = new FlexLayout();

    filterGroup.onPropertyChange(path, "childGroupAllowed", this::onChildGroupAllowedChange);
    filterGroup.onPropertyChange(path, "groupType", this::onGroupType);
    filterGroup.onPropertyChange(path, "groupTypeChangeEnabled", this::onGroupTypeChangeEnabled);
    filterGroup.onPropertyChange(path, "closeable", this::onCloseable);
    filterGroup.onPropertyChange(path, "visible", this::onVisible);
    filterGroup.onPropertyChange(path, "active", this::onActive);
    filterGroup.onReferencedObjectChange(path, "label", this::labelChange);

    filterGroup.onCollectionObjectChange(path, "filters", this::filtersChange);
    filterGroup.onCollectionObjectChange(path, "groups", this::groupsChange);
  }

  private ComponentEventListener<ClickEvent<Button>> removeGroupClickListener() {
    return e -> viewModel.executeCommand(path, "CLOSE_FILTERGROUP");
  }

  private ComponentEventListener<ClickEvent<Button>> addChildGroupClickListener() {
    return e -> viewModel.executeCommand(path, "ADD_CHILDGROUP");
  }

  private ComponentEventListener<ClickEvent<Button>> groupTypeClickListener() {
    return e -> viewModel.executeCommand(path, "CHANGE_GROUPTYPE");
  }

  private ComponentEventListener<ClickEvent<FlexLayout>> groupClickListener() {
    return e -> viewModel.executeCommand(path, "GROUP_CLICKED");
  }

  private ComponentEventListener<DropEvent<FlexLayout>> createDropListener() {
    return e -> e.getDragData().ifPresent(data -> {
      if (data instanceof FilterFieldView) {
        viewModel.executeCommand(path, "FILTER_DROPPED", ((FilterFieldView) data).getPath());
      }
      // TODO add selector drag handling
      // else if (data instanceof FilterSelectorView) {
      // // is it really necessary here? controller.activeFilterGroupChanged(groupId);
      // viewModel.executeCommand(path, "SELECTOR_DROPPED", ((FilterSelectorView) data).getPath());
      // }
    });
  }

  private void onChildGroupAllowedChange(PropertyChange change) {
    Boolean childGroupAllowed = (Boolean) change.getNewValue();
    if (Boolean.TRUE.equals(childGroupAllowed)) {
      addClassName("child-group");
      addToLayout(ctrlButtonsLayout, btnAddChildGroup);
      if (groupClickListener == null) {
        groupClickListener = addClickListener(groupClickListener());
      }
      Css.stopClickEventPropagation(this);
    } else {
      if (groupClickListener != null) {
        groupClickListener.remove();
        groupClickListener = null;
      }
      removeClassName("child-group");
      removeFromLayout(ctrlButtonsLayout, btnAddChildGroup);
    }
  }

  private void addToLayout(FlexLayout layout, Component... comps) {
    if (Layouts.addToLayout(layout, comps)) {
      sanitizeLayouts();
    }
  }


  private void removeFromLayout(FlexLayout layout, Component... comps) {
    if (Layouts.removeFromLayout(layout, comps)) {
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
      if (Layouts.addToLayout(buttonsLayout, 0, btnGroupType)) {
        sanitizeLayouts();
      }
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

  private void filtersChange(CollectionObjectChange changes) {
    for (ObjectChangeSimple change : changes.getChanges()) {
      String fieldPath = change.getPath();
      if (change.getOperation() == ChangeState.NEW) {
        if (filterFields.get(fieldPath) == null) {
          FilterFieldView childFilterField = new FilterFieldView(viewModel, filterGroup, fieldPath);
          filtersLayout.add(childFilterField);
          filterFields.put(fieldPath, childFilterField);
        }
      } else if (change.getOperation() == ChangeState.MODIFIED) {
        // NOP, child should receive change property events
      } else if (change.getOperation() == ChangeState.DELETED) {
        FilterFieldView childFilterField = filterFields.get(fieldPath);
        if (childFilterField != null) {
          filtersLayout.remove(childFilterField);
          filterFields.remove(fieldPath);
        }
      }
    }
  }

  private void groupsChange(CollectionObjectChange changes) {
    for (ObjectChangeSimple change : changes.getChanges()) {
      String groupPath = change.getPath();
      if (change.getOperation() == ChangeState.NEW) {
        FilterGroupView childFilterGroup = new FilterGroupView(viewModel, filterGroup, groupPath);
        this.add(childFilterGroup);
        filterGroups.put(groupPath, childFilterGroup);
      } else if (change.getOperation() == ChangeState.MODIFIED) {
        // NOP, child should receive change property events
      } else if (change.getOperation() == ChangeState.DELETED) {
        FilterGroupView childFilterGroup = filterGroups.get(groupPath);
        if (childFilterGroup != null) {
          this.remove(childFilterGroup);
          filterGroups.remove(groupPath);
        }
      }
    }

  }

}
