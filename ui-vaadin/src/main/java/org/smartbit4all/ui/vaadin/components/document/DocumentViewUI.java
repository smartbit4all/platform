package org.smartbit4all.ui.vaadin.components.document;

import org.smartbit4all.api.documentview.bean.DisplayMode;
import org.smartbit4all.api.documentview.bean.DocumentViewProcess;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.ui.api.components.document.editing.DocumentViewProcessEditing;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

/**
 * Decides and displays the correct panel, according to the document's format. Can also display a
 * loading screen or warning message if there is no available content to show.
 */
@CssImport(value = "./styles/components/details-drawer.css")
public class DocumentViewUI extends FlexLayout {

  private DocumentViewImageUI imageView;
  private DocumentViewTextUI textView;
  private HorizontalLayout messagePanel;
  private FlexLayout loadingSpinner;

  private DocumentViewProcessEditing processEditing;

  public DocumentViewUI(DocumentViewProcessEditing processEditing) {
    this.processEditing = processEditing;
    setId("right-panel");

    imageView = new DocumentViewImageUI(this.processEditing);
    textView = new DocumentViewTextUI(this.processEditing);
    loadingSpinner = createLoadingSpinner();

    this.processEditing.process().onPropertyChange(
        this::onLoadingSpinnerDisplayedChanged,
        DocumentViewProcess.LOADING_SPINNER_DISPLAYED);
    this.processEditing.process().onPropertyChange(
        this::onMessageChanged,
        DocumentViewProcess.MESSAGE);
    this.processEditing.process().onReferencedObjectChange(
        this::onDisplayModeChanged,
        DocumentViewProcess.DISPLAY_MODE);
  }

  /**
   * According to the given displayMode, adds the correct layout and sets it to full width.
   */
  public void onDisplayModeChanged(ReferencedObjectChange referencedObjectChange) {
    Object object = referencedObjectChange.getChange().getObject();
    if (object != null) {
      DisplayMode displayMode = (DisplayMode) object;
      removeAll();
      if (displayMode == DisplayMode.IMAGE) {
        add(imageView);
        imageView.setWidthFull();
      } else if (displayMode == DisplayMode.TEXT) {
        add(textView);
        textView.setWidthFull();
      }
    }
  }

  /**
   * Displays the given message in the correct translation.
   */
  public void onMessageChanged(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object != null) {
      String newMessage = (String) object;
      Label messageLabel = new Label(newMessage);
      messageLabel.getStyle().set("margin", "auto");
      messagePanel = new HorizontalLayout(messageLabel);
      messagePanel.setSizeFull();
      removeAll();
      add(messagePanel);
    }
  }

  /**
   * Displays the loading screen.
   */
  public void onLoadingSpinnerDisplayedChanged(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object != null) {
      boolean loadingSpinnerDisplayed = (boolean) object;
      if (loadingSpinnerDisplayed) {
        removeAll();
        add(loadingSpinner);
      }
    }
  }

  private FlexLayout createLoadingSpinner() {
    ProgressBar progress = new ProgressBar();
    progress.setIndeterminate(true);
    progress.setId("progress-bar");
    FlexLayout loadingSpinner = new FlexLayout(progress);
    loadingSpinner.setId("loading-spinner");
    return loadingSpinner;
  }

}
