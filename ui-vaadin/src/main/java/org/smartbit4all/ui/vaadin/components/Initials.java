package org.smartbit4all.ui.vaadin.components;

import com.vaadin.flow.component.orderedlayout.FlexComponent;

import org.smartbit4all.ui.vaadin.util.FontSize;
import org.smartbit4all.ui.vaadin.util.FontWeight;
import org.smartbit4all.ui.vaadin.util.LumoStyles;
import org.smartbit4all.ui.vaadin.util.UIUtils;
import org.smartbit4all.ui.vaadin.util.css.BorderRadius;

public class Initials extends FlexBoxLayout {

	private String CLASS_NAME = "initials";

	public Initials(String initials) {
		setAlignItems(FlexComponent.Alignment.CENTER);
		setBackgroundColor(LumoStyles.Color.Contrast._10);
		setBorderRadius(BorderRadius.L);
		setClassName(CLASS_NAME);
		UIUtils.setFontSize(FontSize.S, this);
		UIUtils.setFontWeight(FontWeight._600, this);
		setHeight(LumoStyles.Size.M);
		setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
		setWidth(LumoStyles.Size.M);

		add(initials);
	}

}
