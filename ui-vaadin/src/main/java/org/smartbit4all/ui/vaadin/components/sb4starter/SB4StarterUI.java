package org.smartbit4all.ui.vaadin.components.sb4starter;

import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.sb4starter.SB4StarterViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterState;
import org.smartbit4all.ui.api.viewmodel.ObjectEditing;
import org.smartbit4all.ui.vaadin.components.View;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;

public class SB4StarterUI extends View<SB4StarterViewModel> {

  private Button btnAccept;

  private Anchor downloadAnchor;

  public SB4StarterUI(SB4StarterViewModel viewModel) {
    super(viewModel);
    viewModel.data().onPropertyChange(this::onStateChanged, SB4StarterModel.STATE);
    viewModel.data().onPropertyChange(this::onStarterUrlChanged,
        SB4StarterModel.SB4_STARTER_URL);

    createUI();
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    NavigationTarget navigationTarget = ObjectEditing.currentNavigationTarget.get();
    if (navigationTarget != null) {
      VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
      StringBuffer uriString = request.getRequestURL();
      String baseLocation = uriString.toString();

      navigationTarget.putParametersItem(SB4StarterViewModel.PARAM_BASELOCATION, baseLocation);
    }
    super.setParameter(event, parameter);

  }

  private void createUI() {
    // setWidth("400px");
    VerticalLayout main = new VerticalLayout();
    main.setSizeFull();

    Label lblState = new Label();
    VaadinBinders.bindLabel(lblState, viewModel.data(), SB4StarterModel.STATE);

    btnAccept = createButton("Módosítás elfogadása", SB4StarterViewModel.ACCEPT);
    btnAccept.setEnabled(false);

    Button btnClose = createButton("Módosítás elvetése", SB4StarterViewModel.DECLINE);

    downloadAnchor = new Anchor();
    Button btnDownload = createButton("Dokumentum letöltése", SB4StarterViewModel.DOWNLOAD);
    btnDownload.addClickListener(e -> btnDownload.setEnabled(false));
    downloadAnchor.add(btnDownload);

    FlexLayout buttonArea = new FlexLayout();
    buttonArea.setWidthFull();
    buttonArea.setJustifyContentMode(JustifyContentMode.BETWEEN);
    buttonArea.add(downloadAnchor, btnAccept, btnClose);

    main.add(lblState, buttonArea);

    add(main);
  }

  private Button createButton(String title, String commandCode) {
    Button button = new Button(title);
    button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    button.addClickListener(e -> viewModel.executeCommand(null, commandCode));
    return button;
  }

  private void onStateChanged(PropertyChange propertyChange) {
    SB4StarterState state = (SB4StarterState) propertyChange.getNewValue();
    if (state == SB4StarterState.UPLOADED) {
      btnAccept.setEnabled(true);
    }
  }

  private void onStarterUrlChanged(PropertyChange propertyChange) {
    String starterUrl = (String) propertyChange.getNewValue();
    downloadAnchor.setHref(starterUrl);
  }

  @Override
  public void beforeLeave(BeforeLeaveEvent event) {
    if (viewModel != null) {
      viewModel.executeCommand(null, SB4StarterViewModel.DECLINE);
    }
    super.beforeLeave(event);
  }

  // private String convertStateToString(SB4StarterWordState state) {
  // switch (state) {
  // case DOWNLOADING:
  // return "Letöltés folyamatban";
  // case EDITING:
  // return "Szerkesztés folyamatban";
  // case UPLOADED:
  // return "Szerkesztés befejezve";
  // default:
  // throw new InvalidParameterException("Converter got invalid state.");
  // }
  // }
}
