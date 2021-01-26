package org.smartbit4all.api.object;

import java.util.ArrayList;
import java.util.List;

public class ReferredBean {

  private String name;

  private List<ReferredDetailBean> details = new ArrayList<>();

  public final String getName() {
    return name;
  }

  public final void setName(String name) {
    this.name = name;
  }

  public final List<ReferredDetailBean> getDetails() {
    return details;
  }

  public final void setDetails(List<ReferredDetailBean> details) {
    this.details = details;
  }



}
