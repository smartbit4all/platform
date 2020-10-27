package org.smartbit4all.ui.vaadin.components.detailsdrawer;

import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import org.smartbit4all.ui.vaadin.layout.size.Horizontal;
import org.smartbit4all.ui.vaadin.layout.size.Right;
import org.smartbit4all.ui.vaadin.layout.size.Vertical;
import org.smartbit4all.ui.vaadin.util.LumoStyles;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;

public class DetailsDrawerFooter extends FlexBoxLayout {

	private Button view;
	private Button delete;
	private Button save;
    private Button cancel;

	public DetailsDrawerFooter() {
		setBackgroundColor(LumoStyles.Color.Contrast._5);
		setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
		setSpacing(Right.S);
		setJustifyContentMode(JustifyContentMode.EVENLY);
		setWidthFull();

		view = UIUtils.createPrimaryButton(getTranslation("title.view"));
		delete = UIUtils.createTertiaryButton(getTranslation("title.delete"));
		save = UIUtils.createPrimaryButton(getTranslation("title.save"));
		cancel = UIUtils.createTertiaryButton(getTranslation("title.cancel"));
	}

	public Registration addViewListener(
			ComponentEventListener<ClickEvent<Button>> listener) {
	    add(view);
		return view.addClickListener(listener);
	}

	public Registration addDeleteListener(
			ComponentEventListener<ClickEvent<Button>> listener) {
	    add(delete);
		return delete.addClickListener(listener);
	}
	
	public Registration addSaveListener(
	    ComponentEventListener<ClickEvent<Button>> listener) {
	  add(save);
	  return save.addClickListener(listener);
	}
	
	public Registration addCancelListener(
	    ComponentEventListener<ClickEvent<Button>> listener) {
	  add(cancel);
	  return cancel.addClickListener(listener);
	}

}
