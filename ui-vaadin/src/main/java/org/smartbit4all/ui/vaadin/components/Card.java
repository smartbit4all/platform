package org.smartbit4all.ui.vaadin.components;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/components/card.css")
public class Card<T> extends FlexLayout {

  private String CLASS_NAME = "sb4-card";
  private Label label;
  private ArrayList<Row> rows;
  private ButtonBar buttonBar;
  private VerticalLayout verticalLayout;
  private FlexLayout header;
  private T dataObject;

  public Card(Label label, T controllObject) {
    setClassName(CLASS_NAME);
    this.dataObject = controllObject;
    rows = new ArrayList<Row>();
    rows.add(new Row());
    this.label = label;

    header = new FlexLayout();
    header.add(label);

    verticalLayout = new VerticalLayout();
    verticalLayout.add(header, rows.get(0));
    add(verticalLayout);
  }

  public Label getLabel() {
    return label;
  }

  public T getData() {
    return dataObject;
  }

  public void setData(T dataObject) {
    this.dataObject = dataObject;
  }

  public void setComponentsAt(int rowNum, int colNum, Component... components) {
    FlexLayout wrapper;
    if (colNum >= rows.get(rowNum).getComponentCount()) {
      wrapper = new FlexLayout();
      rows.get(rowNum).addComponentAtIndex(colNum, wrapper);
    } else {
      wrapper = (FlexLayout) rows.get(rowNum).getComponentAt(colNum);
    }

    wrapper.removeAll();
    wrapper.add(components);
  }

  public void addButton(Button buttonToAdd) {
    if (buttonBar == null) {
      buttonBar = new ButtonBar();
      verticalLayout.add(buttonBar);
    }
    buttonBar.add(buttonToAdd);
  }

  public Button getButtonAt(int idx) {
    return buttonBar.getButtonAt(idx);
  }

  public FlexLayout getHeader() {
    return header;
  }

  public void addRow() {
    Row newRow = new Row();
    rows.add(newRow);
    verticalLayout.add(newRow);
    verticalLayout.remove(buttonBar);
    verticalLayout.add(buttonBar);
  }

  public void deleteRow(int idx) {
    verticalLayout.remove(rows.get(idx));
    rows.remove(idx);
  }

  public Row getRow(int idx) {
    return rows.get(idx);
  }

  public List<Row> getRows() {
    return rows;
  }
}
