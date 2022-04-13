package org.smartbit4all.ui.vaadin.components;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;

@CssImport("./styles/components/rich-text-editor-with-label.css")
public class RichTextEditorWithLabel extends FlexLayout {

  private RichTextEditor editor = new RichTextEditor();
  private Label label = new Label();

  public RichTextEditor getEditor() {
    return this.editor;
  }

  public void setLabel(String text) {
    this.label.setText(text);
  }

  public RichTextEditorWithLabel(String text) {
    this();
    label.setText(text);
  }

  public RichTextEditorWithLabel() {
    this.add(label, editor);
    this.setClassName("rich-text-editor-with-label");
  }
}
