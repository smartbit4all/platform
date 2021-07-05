package org.smartbit4all.ui.api.form;

import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

/**
 * The basic model for the predictive form UI.
 * @author Zsombor Nyilas
 */
public interface PredictiveFormViewModel extends ObjectEditing {
  
  @PublishEvents("MODEL")
  ObservableObject model();

  @NotifyListeners
  void initModel(String uri);
  
}
