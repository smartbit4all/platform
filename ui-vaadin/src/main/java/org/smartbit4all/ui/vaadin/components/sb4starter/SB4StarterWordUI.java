package org.smartbit4all.ui.vaadin.components.sb4starter;

import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.sb4starter.SB4StarterWordViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordState;
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

public class SB4StarterWordUI extends View<SB4StarterWordViewModel> {

  private Button btnAccept;

  private Anchor downloadAnchor;

  public SB4StarterWordUI(SB4StarterWordViewModel viewModel) {
    super(viewModel);
    viewModel.data().onPropertyChange(this::onStateChanged, SB4StarterWordFormModel.STATE);
    viewModel.data().onPropertyChange(this::onStarterUrlChanged,
        SB4StarterWordFormModel.SB4_STARTER_URL);

    createUI();
  }

  @Override
  public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
    NavigationTarget navigationTarget = ObjectEditing.currentNavigationTarget.get();
    if (navigationTarget != null) {
      VaadinServletRequest request = (VaadinServletRequest) VaadinService.getCurrentRequest();
      StringBuffer uriString = request.getRequestURL();
      String baseLocation = uriString.toString();

      navigationTarget.putParametersItem(SB4StarterWordViewModel.PARAM_BASELOCATION, baseLocation);
    }
    super.setParameter(event, parameter);

  }

  private void createUI() {
    // setWidth("400px");
    VerticalLayout main = new VerticalLayout();
    main.setSizeFull();

    Label lblState = new Label();
    VaadinBinders.bindLabel(lblState, viewModel.data(), SB4StarterWordFormModel.STATE);

    btnAccept = createButton("Elfogad", SB4StarterWordViewModel.ACCEPT);
    btnAccept.setEnabled(false);

    Button btnClose = createButton("Elvet", SB4StarterWordViewModel.DECLINE);

    downloadAnchor = new Anchor();
    Button btnDownload = createButton("Letölt", SB4StarterWordViewModel.DOWNLOAD);
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
    SB4StarterWordState state = (SB4StarterWordState) propertyChange.getNewValue();
    if (state == SB4StarterWordState.UPLOADED) {
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
      viewModel.executeCommand(null, SB4StarterWordViewModel.DECLINE);
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
