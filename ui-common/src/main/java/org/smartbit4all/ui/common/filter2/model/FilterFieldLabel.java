package org.smartbit4all.ui.common.filter2.model;

import org.smartbit4all.ui.common.filter.FilterLabelPosition;

public class FilterFieldLabel {

  private String code;
  private FilterLabelPosition position;
  private int duplicateNum;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public FilterLabelPosition getPosition() {
    return position;
  }

  public void setPosition(FilterLabelPosition position) {
    this.position = position;
  }

  public int getDuplicateNum() {
    return duplicateNum;
  }

  public void setDuplicateNum(int duplicateNum) {
    this.duplicateNum = duplicateNum;
  }

}
