/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.components;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.ui.vaadin.components.Card.TagHack;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/components/card.css")
public class Card<T> extends Composite<TagHack> {

  @Tag("sb4-card")
  public static class TagHack extends FlexLayout {

  }

  private ArrayList<Row> rows = new ArrayList<>();
  private ButtonBar buttonBar;
  private VerticalLayout content;
  private Component header;
  private T dataObject;

  public Card(String headerTxt, T dataObject) {
    this(new Label(headerTxt), dataObject);
  }

  public Card(Component header, T dataObject) {
    super();
    this.header = header;
    this.dataObject = dataObject;

    this.getContent().setFlexDirection(FlexDirection.COLUMN);

    content = new VerticalLayout();
    getContent().add(header, content);
  }

  public T getData() {
    return dataObject;
  }

  public void setData(T dataObject) {
    this.dataObject = dataObject;
  }

  public void setComponentAt(int rowNum, int colNum, Component component) {
    if (colNum > 3) {
      throw new IllegalArgumentException(
          "Only four columns can be placed, so colNum can't be more then 3!");
    }
    while (rowNum >= rows.size()) {
      Row newRow = new Row();
      rows.add(newRow);
      content.add(newRow);
    }
    Row row = rows.get(rowNum);
    if (colNum >= row.getComponentCount()) {
      row.addComponentAtIndex(colNum, component);
    } else {
      Component componentAt = row.getComponentAt(colNum);
      row.remove(componentAt);
      row.addComponentAtIndex(colNum, component);
    }
  }

  public void addButton(Button buttonToAdd) {
    if (buttonBar == null) {
      buttonBar = new ButtonBar();
      this.getContent().add(buttonBar);
    }
    buttonBar.add(buttonToAdd);
  }

  public Button getButtonAt(int idx) {
    return buttonBar.getButtonAt(idx);
  }

  public Component getHeader() {
    return header;
  }

  public void setHeader(Component header) {
    getContent().remove(this.header);
    this.header = header;
    getContent().addComponentAsFirst(header);
  }

  public void addRow() {
    Row newRow = new Row();
    rows.add(newRow);
    content.add(newRow);
  }

  public void deleteRow(int idx) {
    content.remove(rows.get(idx));
    rows.remove(idx);
  }

  public Row getRow(int idx) {
    return rows.get(idx);
  }

  public List<Row> getRows() {
    return rows;
  }
}
