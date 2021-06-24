package org.smartbit4all.ui.api.navigation;

import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;

/**
 * The editing model for the navigation. It contains a navigation
 * 
 * @author Peter Boros
 */
public interface NavigationViewModel extends ObjectEditing {

  @PublishEvents("MODEL")
  ObservableObject model();

}
