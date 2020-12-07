package org.smartbit4all.ui.common.components.user;

import java.io.InputStream;
import java.util.Collection;
import org.smartbit4all.ui.common.components.user.UserComponentConfiguration.NavigationActionItem;
import org.smartbit4all.ui.common.view.UIView;

public interface UserComponentBaseView extends UIView {

  void setLabel(String string);

  void setPicture(InputStream inputStream);

  void setItems(Collection<NavigationActionItem> values);

  void navigate(String route);

}
