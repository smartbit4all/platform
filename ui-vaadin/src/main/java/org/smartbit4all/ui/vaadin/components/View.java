package org.smartbit4all.ui.vaadin.components;

import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import org.smartbit4all.ui.api.viewmodel.ViewModel;
import org.smartbit4all.ui.vaadin.components.navigation.ParameterMissingException;
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

  public View(UINavigationApi uiNavigationApiApi, Class<T> vmClazz) {
    this.viewModel =
        uiNavigationApiApi.createViewModel(ObjectEditing.currentNavigationTarget.get(), vmClazz);
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    NavigationTarget navigationTarget = ObjectEditing.currentNavigationTarget.get();
    if (navigationTarget != null) {
      viewModel.initByNavigationTarget(navigationTarget);
    } else {
      throw new ParameterMissingException("View initialized without NavigationTarget!");
    }
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    viewModel.onCloseWindow();
  }

}
