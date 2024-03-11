package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;

public class DomainObjectTestBean {

  private URI uri;

  private String name;

  private long counter;

  private List<String> stringList;

  private Long readOnlyLong;

  private boolean valid;

  private Boolean enabled;

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public DomainObjectTestBean enabled(Boolean enabled) {
    this.enabled = enabled;
    return this;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }

  public DomainObjectTestBean valid(boolean valid) {
    this.valid = valid;
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DomainObjectTestBean name(String name) {
    this.name = name;
    return this;
  }

  public long getCounter() {
    return counter;
  }

  public void setCounter(long counter) {
    this.counter = counter;
  }

  public DomainObjectTestBean counter(long counter) {
    this.counter = counter;
    return this;
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

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

}
