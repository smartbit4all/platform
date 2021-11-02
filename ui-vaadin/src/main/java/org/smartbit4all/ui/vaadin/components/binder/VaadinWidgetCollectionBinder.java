package org.smartbit4all.ui.vaadin.components.binder;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.binder.CollectionBinder;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

public class VaadinWidgetCollectionBinder<T, WIDGET extends Component>
    extends CollectionBinder<T> {

  private Map<String, WIDGET> widgetsByPath;
  private HasComponents container;
  private BiFunction<String, T, WIDGET> createWidget;

  public VaadinWidgetCollectionBinder(HasComponents container, ObservableObject observableObject,
      BiFunction<String, T, WIDGET> createWidget, String... collectionPath) {
    super(observableObject, collectionPath);
    this.container = container;
    this.createWidget = createWidget;
    widgetsByPath = new HashMap<>();
    registerModelObserver();
  }

  protected void addWidget(WIDGET widget) {
    if (container != null) {
      container.add(widget);
    }
  }

  protected void removeWidget(WIDGET widget) {
    if (container != null) {
      container.remove(widget);
    }
  }

  @Override
  protected void addItem(String itemPath, T item) {
    super.addItem(itemPath, item);
    WIDGET widget = createWidget.apply(itemPath, item);
    addWidget(widget);
    widgetsByPath.put(itemPath, widget);
  }

  @Override
  protected T deleteItem(String itemPath) {
    WIDGET widget = widgetsByPath.get(itemPath);
    if (widget != null) {
      removeWidget(widget);
      widgetsByPath.remove(itemPath);
    }
    return super.deleteItem(itemPath);
  }

  public WIDGET getWidgetByPath(String path) {
    return widgetsByPath.get(path);
  }
}
