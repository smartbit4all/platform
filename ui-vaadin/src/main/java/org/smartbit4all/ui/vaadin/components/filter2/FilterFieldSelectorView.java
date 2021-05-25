package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.components.filter.FilterSelectorUI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterFieldSelectorView extends FlexLayout implements DragSource<FilterSelectorUI> {

  private ObjectEditing viewModel;
  private ObservableObject filterSelector;
  private String path;

  public FilterFieldSelectorView(ObjectEditing viewModel, ObservableObject filterSelector,
      String path) {
    this.viewModel = viewModel;
    this.filterSelector = filterSelector;
    this.path = path;
    createUI();
  }

  private void createUI() {
    setDraggable(true);
    Label label = new Label();
    Button button = new Button(new Icon(VaadinIcon.PLUS));
    button.addClickListener(e -> viewModel.executeCommand(path, "CREATE_FILTER"));
    add(label, button);
    addClassName("filtermeta-layout");

    VaadinBinders.bind(label, filterSelector,
        PathUtility.concatPath(path, "labelCode"))
        .setConverterFunction(s -> getTranslation((String) s));

    filterSelector.onPropertyChange(path, "enabled",
        c -> button.setEnabled((Boolean) c.getNewValue()));
  }

  public String getPath() {
    return path;
  }

}
