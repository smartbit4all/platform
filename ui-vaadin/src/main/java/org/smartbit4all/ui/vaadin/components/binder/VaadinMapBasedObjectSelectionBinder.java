package org.smartbit4all.ui.vaadin.components.binder;

import org.smartbit4all.api.mapbasedobject.bean.MapBasedObjectSelection;
import org.smartbit4all.core.object.MapBasedObject;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.ObservableObject;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.selection.MultiSelect;

public class VaadinMapBasedObjectSelectionBinder<C extends Component>
    extends VaadinMultiSelectBinder<C, MapBasedObjectSelection> {

  public VaadinMapBasedObjectSelectionBinder(MultiSelect<C, MapBasedObjectSelection> list,
      ObservableObject observableObject,
      String... collectionPath) {
    super(list, observableObject, collectionPath);
    registerModelObserver();
  }

  @Override
  protected MapBasedObjectSelection getChangeObject(ObjectChangeSimple change) {
    String value = (String) ((MapBasedObject) change.getObject()).getValueByPath("stringValue");
    return new MapBasedObjectSelection().stringValue(value);
  }
}
