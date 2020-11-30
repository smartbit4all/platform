package org.smartbit4all.ui.vaadin.components.filter;

import java.util.List;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public abstract class DynamicFilterOperationUI extends FlexLayout {

  public abstract void setPlaceholder(String placeHolderText);

  public abstract String getFilterName();

  public abstract List<String> getPossibleOperations();

}
