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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCounter() {
    return counter;
  }

  public void setCounter(long counter) {
    this.counter = counter;
  }

  public List<String> getStringList() {
    return stringList;
  }

  public void setStringList(List<String> stringList) {
    this.stringList = stringList;
  }

  public Long getReadOnlyLong() {
    return readOnlyLong;
  }

  public List<MasterDetailBean> getDetails() {
    return details;
  }

  public void setDetails(List<MasterDetailBean> details) {
    this.details = details;
  }

  public ReferredBean getReferred() {
    return referred;
  }

  public void setReferred(ReferredBean referred) {
    this.referred = referred;
  }

}
