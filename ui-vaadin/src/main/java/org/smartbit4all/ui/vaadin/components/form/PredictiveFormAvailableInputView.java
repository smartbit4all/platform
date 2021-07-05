package org.smartbit4all.ui.vaadin.components.form;

import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.utility.PathUtility;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

/**
 * The view that represent an available choice on the predictive template. These can be seen on the
 * lower part of the screen. It's a clickable button, with an optional logo.
 * 
 * @author Zsombor Nyilas
 *
 */
public class PredictiveFormAvailableInputView extends FlexLayout {

  private ObjectEditing viewModel;
  private ObservableObject formInput;
  private String path;

  public PredictiveFormAvailableInputView(ObjectEditing viewModel, ObservableObject formInput,
      String path) {
    this.viewModel = viewModel;
    this.formInput = formInput;
    this.path = path;
    createUI();
  }

  /**
   * The UI creation method, which consists of assigning the basic UI elements and subscribing to
   * the necessary listeners.
   */
  private void createUI() {
    Button button = new Button();
    button.setClassName("selection-button");
    // Icon icon = RootFragmentIconGenerator.getIconCode(documentFragment.getCaption()).create();
    // button.setIcon(icon);
    button.getStyle().set("margin", "5px");
    button.addClickListener(e -> {
      viewModel.executeCommand(path, "CREATE_FRAGMENT");
      viewModel.executeCommand(path, "SET_VALUE");
    });
    add(button);

    VaadinBinders.bind(button, formInput,
        PathUtility.concatPath(path, "label"), s -> getTranslation((String) s));

    formInput.onPropertyChange(path, "enabled",
        c -> button.setEnabled((Boolean) c.getNewValue()));
  }

  public String getPath() {
    return path;
  }

}
