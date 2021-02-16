package org.smartbit4all.ui.vaadin.components.form;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.FlexLayout.FlexDirection;

public class DynamicFormCustomCol<BEAN> extends DynamicForm<BEAN> {

  int colNum;
  private boolean fillVerticalFirst;
  private List<FlexLayout> columns;

  public DynamicFormCustomCol(Class<BEAN> beanClazz, int colNum, boolean fillVerticalFirst) {
    // super(beanClazz);
    this.colNum = colNum;
    this.fillVerticalFirst = fillVerticalFirst;
    super.getContent().addClassName("col-based");
    initBean(beanClazz);
  }



  @Override
  protected void addComponentsToContent(List<Component> componentList) {
    int rowNum = componentList.size() / colNum;
    if(componentList.size() % colNum != 0) {
      rowNum++;
    }
    columns = new ArrayList<>();

    for (int i = 0; i < colNum; i++) {
      FlexLayout column = new FlexLayout();
      column.setFlexDirection(FlexDirection.COLUMN);
      columns.add(column);
      super.getContent().add(column);
    }

    if (fillVerticalFirst) {
      for (int i = 0; i < colNum; i++) {
        FlexLayout column = columns.get(i);
        for (int j = 0; j < rowNum && !componentList.isEmpty(); j++) {
          column.add(componentList.remove(0));
        }
      }
    } else {
      for (int i = 0; i < rowNum; i++) {
        for (int j = 0; j < colNum && !componentList.isEmpty(); j++) {
          columns.get(j).add(componentList.remove(0));
        }
      }
    }
    if(!componentList.isEmpty()) {
      
    }
  }


}
