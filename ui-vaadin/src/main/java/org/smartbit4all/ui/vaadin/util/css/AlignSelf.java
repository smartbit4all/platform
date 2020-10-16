package org.smartbit4all.ui.vaadin.util.css;

public enum AlignSelf {

	BASLINE("baseline"), CENTER("center"), END("end"), START("start"), STRETCH(
			"stretch"),
	FLEX_END("flex-end");

	private String value;

	AlignSelf(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
