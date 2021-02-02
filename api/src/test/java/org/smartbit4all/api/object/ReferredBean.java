package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.List;

public class ReferredBean {

  private String name;

  private List<ReferredDetailBean> details = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ReferredDetailBean> getDetails() {
    return details;
  }

  public void setDetails(List<ReferredDetailBean> details) {
    this.details = details;
  }



}
