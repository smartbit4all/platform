package org.smartbit4all.ui.vaadin.components.document;

import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.ui.api.components.document.editing.DocumentViewProcessEditing;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextArea;

/**
 * Displays documents with text-only data in a plain text area.
 */
@CssImport(value = "./styles/components/details-drawer.css")
public class DocumentViewTextUI extends FlexLayout {

  private FlexLayout textPanel;
  private TextArea textArea;
  
  private DocumentViewProcessEditing processEditing;
  
  public DocumentViewTextUI(DocumentViewProcessEditing processEditing) {
    this.processEditing = processEditing;
    textPanel = createTextPanel();
    add(textPanel);
    VaadinBinders.bind(textArea, processEditing.process(), "text");
  }

  public void onTextChanged(PropertyChange propertyChange) {
    String newText = (String) propertyChange.getNewValue();
    textArea.setValue(newText);
  }

  /**
   * Creates the text panel, sets it to read-only and full width.
   */
  public FlexLayout createTextPanel() {
    FlexLayout layout = new FlexLayout();
    layout.setId("text-panel");
    layout.setWidthFull();
    textArea = new TextArea();
    textArea.setReadOnly(true);
    textArea.setWidthFull();
    textArea.setValue("editing elotti szoveg");
    layout.add(textArea);
    return layout;
  }
  
}
