package org.smartbit4all.ui.vaadin.components.storage.history;

import java.net.URI;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

public abstract class ObjectVersionView extends FlexLayout {
  
  public abstract void createUI(URI versionUri);
}
