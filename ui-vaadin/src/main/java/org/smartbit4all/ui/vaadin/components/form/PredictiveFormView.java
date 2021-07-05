package org.smartbit4all.ui.vaadin.components.form;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.ui.api.form.PredictiveFormViewModel;
import org.smartbit4all.ui.api.form.model.FormDataContent;
import com.vaadin.flow.component.HasComponents;

/**
 * The main view for the predictive form, which contains the selector and content layouts, and the
 * model and ViewModel as well.
 * 
 * @author Zsombor Nyilas
 */
public class PredictiveFormView {

  private PredictiveFormViewModel viewModel;

  /**
   * It contains the visual components for the {@link FormDataContent}.
   */
  private HasComponents contentHolder;

  /**
   * The visual components for the {@link FormLayoutPredictive#getAvailableChoices()} list.
   */
  private HasComponents availableChoicesHolder;

  /**
   * The model itself, which is an ObservableObject, containing the source of truth.
   */
  private ObservableObject model;

  /**
   * The view of the root content.
   */
  private FormDataContentView root;
  
  private Map<String, PredictiveFormAvailableInputView> availableInputsByPath;

  public PredictiveFormView(PredictiveFormViewModel viewModel, HasComponents contentHolder,
      HasComponents availableChoicesHolder) {
    this.viewModel = viewModel;
    this.model = viewModel.model();
    this.contentHolder = contentHolder;
    this.availableChoicesHolder = availableChoicesHolder;
    availableInputsByPath = new HashMap<>();
    subscribeToViewEvents();
  }
  
  /**
   * The method, which subscribes to the change listeners.
   */
  private void subscribeToViewEvents() {
    model.onCollectionObjectChange(null, "availableChoices", this::onAvailableChoicesChange);
    model.onReferencedObjectChange(null, "content", this::onContentChange);
  }

  /**
   * Method to be executed on the change of choices.
   * 
   * @param changes the list of changes that happened on the choices.
   */
  private void onAvailableChoicesChange(CollectionObjectChange changes) {
    for (ObjectChangeSimple change : changes.getChanges()) {
      String path = change.getPath();
      if (change.getOperation() == ChangeState.NEW) {
        PredictiveFormAvailableInputView availableInputView =
            new PredictiveFormAvailableInputView(viewModel, model, path);
        availableChoicesHolder.add(availableInputView);
        availableInputsByPath.put(path, availableInputView);
      } else if (change.getOperation() == ChangeState.DELETED) {
        PredictiveFormAvailableInputView availableInputView = availableInputsByPath.get(path);
        availableChoicesHolder.remove(availableInputView);
        availableInputsByPath.remove(path);
      }
    }
  }

  /**
   * Method to be executed on the change of the root content.
   * 
   * @param changes the list of changes that happened on the content.
   */
  private void onContentChange(ReferencedObjectChange changes) {
    ChangeState operation = changes.getChange().getOperation();
    if (operation == ChangeState.NEW) {
      removeRootContent();
      createRootContent();
    } else if (operation == ChangeState.DELETED) {
      removeRootContent();
    }
  }

  /**
   * Method that creates the root {@link FormDataContentView} and adds it to the content holder.
   */
  private void createRootContent() {
    root = new FormDataContentView(viewModel, model, "root");
    contentHolder.add(root);
  }

  /**
   * Method that removes the root {@link FormDataContentView} from the content holder.
   */
  private void removeRootContent() {
    if (root != null) {
      contentHolder.remove(root);
      root = null;
    }
  }

}
