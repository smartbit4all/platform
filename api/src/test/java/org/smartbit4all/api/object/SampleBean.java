package org.smartbit4all.api.object;

import java.util.List;

public class SampleBean {

  private String name;

  private long counter;

  private List<String> stringList;

  private Long readOnlyLong;

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

}
