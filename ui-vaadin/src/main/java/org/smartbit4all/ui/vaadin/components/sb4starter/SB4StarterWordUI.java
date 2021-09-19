package org.smartbit4all.ui.vaadin.components.sb4starter;

import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.ui.api.sb4starter.SB4StarterWordViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordState;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;
import org.smartbit4all.ui.vaadin.object.VaadinPublisherWrapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SB4StarterWordUI extends Dialog {

  private SB4StarterWordViewModel viewModel;

  private Button btnAccept;

  public SB4StarterWordUI(SB4StarterWordViewModel viewModel) {
    this.viewModel = viewModel;

    viewModel.sb4Starter().setPublisherWrapper(VaadinPublisherWrapper.create());
    viewModel.sb4Starter().onPropertyChange(null, SB4StarterWordFormModel.STATE,
        this::onStateChanged);

    createUI();
  }

  private void createUI() {
    setWidth("400px");
    VerticalLayout main = new VerticalLayout();
    main.setSizeFull();

    Label lblState = new Label();
    VaadinBinders.bind(lblState, viewModel.sb4Starter(), SB4StarterWordFormModel.STATE);

    btnAccept = new Button("Elfogad");
    btnAccept.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
    btnAccept.setEnabled(false);
    btnAccept.addClickListener(e -> {
      viewModel.accept();
      close();
    });

    Button btnClose = new Button("Elvet");
    btnClose.addThemeVariants(ButtonVariant.LUMO_ERROR);
    btnClose.addClickListener(e -> {
      viewModel.close();
      close();
    });

    FlexLayout buttonArea = new FlexLayout();
    buttonArea.setWidthFull();
    buttonArea.setJustifyContentMode(JustifyContentMode.BETWEEN);
    buttonArea.add(btnAccept, btnClose);

    main.add(lblState, buttonArea);

    add(main);
  }

  private void onStateChanged(PropertyChange propertyChange) {
    SB4StarterWordState state = (SB4StarterWordState) propertyChange.getNewValue();
    if (state == SB4StarterWordState.UPLOADED) {
      btnAccept.setEnabled(true);
    }
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
