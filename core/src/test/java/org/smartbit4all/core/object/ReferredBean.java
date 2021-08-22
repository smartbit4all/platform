package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.List;

public class ReferredBean {

  private String name;

  private List<ReferredDetailBean> details = null; // new ArrayList<>();

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

  public ReferredBean addDetailsItem(ReferredDetailBean detailsItem) {
    if (this.details == null) {
      this.details = new ArrayList<>();
    }
    this.details.add(detailsItem);
    return this;
  }

}
