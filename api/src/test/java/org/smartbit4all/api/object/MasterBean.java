package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.List;

public class MasterBean {

  private String name;

  private long counter;

  private List<String> stringList;

  private Long readOnlyLong;

  private List<MasterDetailBean> details = new ArrayList<>();

  private ReferredBean referred;

  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public final long getCounter() {
    return counter;
  }

  public final void setCounter(long counter) {
    this.counter = counter;
  }

  public final List<String> getStringList() {
    return stringList;
  }

  public final void setStringList(List<String> stringList) {
    this.stringList = stringList;
  }

  public final Long getReadOnlyLong() {
    return readOnlyLong;
  }

  public final List<MasterDetailBean> getDetails() {
    return details;
  }

  public final void setDetails(List<MasterDetailBean> details) {
    this.details = details;
  }

  public final ReferredBean getReferred() {
    return referred;
  }

  public final void setReferred(ReferredBean referred) {
    this.referred = referred;
  }

}
