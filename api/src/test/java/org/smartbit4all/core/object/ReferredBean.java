package org.smartbit4all.core.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReferredBean {

  private String name;

  private List<ReferredDetailBean> details = null; // new ArrayList<>();

  private Map<String, DetailBeanWithId> detailsById = null;

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

  public ReferredBean putDetailsByIdItem(String key, DetailBeanWithId detailsByIdItem) {
    if (detailsById == null) {
      detailsById = new HashMap<>();
    }
    detailsById.put(key, detailsByIdItem);
    return this;
  }

  public Map<String, DetailBeanWithId> getDetailsById() {
    return detailsById;
  }

  public void setDetailsById(Map<String, DetailBeanWithId> detailsById) {
    this.detailsById = detailsById;
  }

}
