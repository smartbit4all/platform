package org.smartbit4all.ui.vaadin.components.filter2;

import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public class FilterFieldSelectorView extends FlexLayout
    implements DragSource<FilterFieldSelectorView> {

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
    setDragData(this);
    Label label = new Label();
    Button button = new Button(new Icon(VaadinIcon.PLUS));
    button.addClickListener(e -> viewModel.executeCommand(path, "CREATE_FILTER"));
    add(label, button);
    addClassName("filtermeta-layout");

    VaadinBinders.bindLabel(label, filterSelector,
        s -> getTranslation((String) s),
        path, "labelCode");

    filterSelector.onPropertyChange(
        c -> button.setEnabled((Boolean) c.getNewValue()),
        path, "enabled");
  }

  public String getPath() {
    return path;
  }

}
