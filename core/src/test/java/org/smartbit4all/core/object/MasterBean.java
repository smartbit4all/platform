package org.smartbit4all.core.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MasterBean {

  private URI uri;

  private String name;

  private long counter;

  private List<String> stringList;

  private Long readOnlyLong;

  private List<MasterDetailBean> details = new ArrayList<>();

  private ReferredBean referred;

  private boolean valid;

  private Boolean enabled;

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getCounter() {
    return counter;
  }

  public MasterBean counter(long counter) {
    this.counter = counter;
    return this;
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

  public MasterBean addDetailsItem(MasterDetailBean detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

  public final URI getUri() {
    return uri;
  }

  public final void setUri(URI uri) {
    this.uri = uri;
  }

}
