package org.smartbit4all.ui.vaadin.components;

import java.net.URI;
import org.smartbit4all.ui.api.viewmodel.ViewModel;
import org.smartbit4all.ui.vaadin.components.navigation.Navigations;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;

public class View<T extends ViewModel> extends FlexLayout
    implements HasUrlParameter<String>, BeforeLeaveObserver {

  protected transient T viewModel;

  public View(T viewModel) {
    this.viewModel = viewModel;
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    URI uri = Navigations.getUriParameter(event);
    viewModel.init(uri);
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    viewModel.onCloseWindow();
  }

}
