package org.smartbit4all.ui.common.filter;

public enum StatisticsTimeFilterOptions {

	LAST_WEEK("statistics.filter.time.last_week"), THIS_MONTH("statistics.filter.time.this_month"),
	LAST_MONTH("statistics.filter.time.last_month"), YESTERDAY("statistics.filter.time.yesterday"),
	TODAY("statistics.filter.time.today"), OTHER("statistics.filter.time.other");

	private String label;

	private StatisticsTimeFilterOptions(String filerLabel) {
		this.label = filerLabel;
	}

	public String getLabel() {
		return label;
	}
}
