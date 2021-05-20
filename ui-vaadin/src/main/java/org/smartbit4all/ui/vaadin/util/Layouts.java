package org.smartbit4all.ui.vaadin.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;

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

}
