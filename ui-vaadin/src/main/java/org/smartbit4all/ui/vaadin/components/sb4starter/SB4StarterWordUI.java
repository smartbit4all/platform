package org.smartbit4all.ui.vaadin.components.sb4starter;

import java.security.InvalidParameterException;

import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.ui.api.sb4starter.SB4StarterWordViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordState;
import org.smartbit4all.ui.vaadin.components.binder.VaadinBinders;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;

public class SB4StarterWordUI extends Dialog {

	private SB4StarterWordViewModel viewModel;

	private Label lblState;

	private Button btnAccept;

	private Button btnClose;

	private VerticalLayout main;

	public SB4StarterWordUI(SB4StarterWordViewModel viewModel) {
		this.viewModel = viewModel;
		createUI();
	}

	private void createUI() {
		setWidth("400px");
		main = new VerticalLayout();
		main.setSizeFull();

		lblState = new Label();
		VaadinBinders.bind(lblState, viewModel.sb4Starter(), SB4StarterWordFormModel.STATE,
				state -> {
					ApiObjectRef ref = (ApiObjectRef) state;
					return ConvertStateToString((SB4StarterWordState) ref.getObject());
				});

		btnAccept = new Button("Szerkesztés megerősítése");
		btnAccept.setEnabled(false);
		btnAccept.addClickListener(e -> {
			viewModel.accept();
			close();
		});
		
		viewModel.sb4Starter().onPropertyChange(null , SB4StarterWordFormModel.STATE, e -> {
			ApiObjectRef ref = (ApiObjectRef) e.getNewValue();
			if ((SB4StarterWordState) ref.getObject() == SB4StarterWordState.UPLOADED) {
				btnAccept.setEnabled(true);
			}
		});
		
		btnClose = new Button("Elvet");
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

	private String ConvertStateToString(SB4StarterWordState state) {
		switch (state) {
		case DOWNLOAD:
			return "Letöltés folyamatban";
		case EDIT:
			return "Szerkesztés folyamatban";
		case UPLOADED:
			return "Szerkesztés befejezve";
		default:
			throw new InvalidParameterException("Converter got invalid state.");
		}
	}
}
