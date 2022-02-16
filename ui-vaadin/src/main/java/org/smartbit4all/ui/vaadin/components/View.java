package org.smartbit4all.ui.vaadin.components;

import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import org.smartbit4all.ui.api.viewmodel.ViewModel;
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

  // public View(ViewModelApi viewModelApi, Class<T> vmClazz) {
  // this.viewModel =
  // viewModelApi.get(vmClazz, ObjectEditing.currentNavigationTarget.get());
  // }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    NavigationTarget navigationTarget = ObjectEditing.currentNavigationTarget.get();
    if (navigationTarget != null) {
      viewModel.initByNavigationTarget(navigationTarget);
    } else {
      event.rerouteTo("");
    }
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    viewModel.onCloseWindow();
  }

}
