package org.smartbit4all.ui.vaadin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;

public class Layouts {

  private Layouts() {}

  public static boolean addToLayout(HasComponents layout, Component... comps) {
    boolean anyChange = false;
    for (int i = 0; i < comps.length; i++) {
      Component comp = comps[i];
      if (!comp.getParent().isPresent() || comp.getParent().get() != layout) {
        layout.add(comp);
        anyChange = true;
      }
    }
    return anyChange;
  }

  public static boolean addToLayout(HasComponents layout, int index, Component... comps) {
    boolean anyChange = false;
    for (int i = 0; i < comps.length; i++) {
      Component comp = comps[i];
      layout.addComponentAtIndex(index, comp);
      anyChange = true;
    }
    return anyChange;
  }

  public static boolean removeFromLayout(HasComponents layout, Component... comps) {
    boolean anyChange = false;
    for (int i = 0; i < comps.length; i++) {
      Component comp = comps[i];
      if (comp.getParent().isPresent() && comp.getParent().get() == layout) {
        layout.remove(comp);
        anyChange = true;
      }
    }
    return anyChange;
  }

  public static void setStyleForAllChildren(Component parent, String style) {
    parent.getChildren().forEach(
        c -> ((HasStyle) c).addClassName(style));
  }

  public static void setAllChildrenReadOnly(Component parent, boolean readOnly) {
    parent.getChildren().forEach(
        c -> {
          if (c instanceof HasValue) {
            ((HasValue<?, ?>) c).setReadOnly(readOnly);
          }
        });
  }

}
