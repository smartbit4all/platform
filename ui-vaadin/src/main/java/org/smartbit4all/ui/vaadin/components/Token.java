package org.smartbit4all.ui.vaadin.components;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import org.smartbit4all.ui.vaadin.layout.size.Left;
import org.smartbit4all.ui.vaadin.layout.size.Right;
import org.smartbit4all.ui.vaadin.util.FontSize;
import org.smartbit4all.ui.vaadin.util.LumoStyles;
import org.smartbit4all.ui.vaadin.util.TextColor;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import org.smartbit4all.ui.vaadin.util.css.BorderRadius;
import org.smartbit4all.ui.vaadin.util.css.Display;

public class Token extends FlexBoxLayout {

	private final String CLASS_NAME = "token";

	public Token(String text) {
		setAlignItems(FlexComponent.Alignment.CENTER);
		setBackgroundColor(LumoStyles.Color.Primary._10);
		setBorderRadius(BorderRadius.M);
		setClassName(CLASS_NAME);
		setDisplay(Display.INLINE_FLEX);
		setPadding(Left.S, Right.XS);
		setSpacing(Right.XS);

		Label label = UIUtils.createLabel(FontSize.S, TextColor.BODY, text);
		Button button = UIUtils.createButton(VaadinIcon.CLOSE_SMALL, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE);
		add(label, button);
	}

}
