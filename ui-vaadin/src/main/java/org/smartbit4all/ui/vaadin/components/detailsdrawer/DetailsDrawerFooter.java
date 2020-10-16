package org.smartbit4all.ui.vaadin.components.detailsdrawer;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;

import org.smartbit4all.ui.vaadin.components.FlexBoxLayout;
import org.smartbit4all.ui.vaadin.layout.size.Horizontal;
import org.smartbit4all.ui.vaadin.layout.size.Right;
import org.smartbit4all.ui.vaadin.layout.size.Vertical;
import org.smartbit4all.ui.vaadin.util.LumoStyles;
import org.smartbit4all.ui.vaadin.util.UIUtils;

public class DetailsDrawerFooter extends FlexBoxLayout {

	private Button save;
	private Button cancel;

	public DetailsDrawerFooter() {
		setBackgroundColor(LumoStyles.Color.Contrast._5);
		setPadding(Horizontal.RESPONSIVE_L, Vertical.S);
		setSpacing(Right.S);
		setWidthFull();

		save = UIUtils.createPrimaryButton(getTranslation("title.save"));
		cancel = UIUtils.createTertiaryButton(getTranslation("title.cancel"));
		add(save, cancel);
	}

	public Registration addSaveListener(
			ComponentEventListener<ClickEvent<Button>> listener) {
		return save.addClickListener(listener);
	}

	public Registration addCancelListener(
			ComponentEventListener<ClickEvent<Button>> listener) {
		return cancel.addClickListener(listener);
	}

}
