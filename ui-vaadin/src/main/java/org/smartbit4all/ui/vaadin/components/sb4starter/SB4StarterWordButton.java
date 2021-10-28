package org.smartbit4all.ui.vaadin.components.sb4starter;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;

public class SB4StarterWordButton extends Anchor {

  public SB4StarterWordButton(Runnable sb4StarterRun,
      String btnText) {
    Button btnInner = new Button(btnText);
    btnInner.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnInner.setSizeFull();
    btnInner.setIcon(VaadinIcon.PENCIL.create());
    btnInner.addClickListener(e -> sb4StarterRun.run());
    add(btnInner);
  }
}
