package org.smartbit4all.core.json;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class RootBean {

  private String propertyString;
  private Long propertyLong;
  private Double propertyDouble;
  private OffsetDateTime propertyOffsetDateTime;
  private ContainedBean containedBeanProperty;
  private List<ContainedBean> listOfContainedBean;
  private Map<String, ContainedBean> mapOfContainedBean;


  final String getPropertyString() {
    return propertyString;
  }

  final void setPropertyString(String propertyString) {
    this.propertyString = propertyString;
  }

  final Long getPropertyLong() {
    return propertyLong;
  }

  final void setPropertyLong(Long propertyLong) {
    this.propertyLong = propertyLong;
  }

  final Double getPropertyDouble() {
    return propertyDouble;
  }

  final void setPropertyDouble(Double propertyDouble) {
    this.propertyDouble = propertyDouble;
  }

  final OffsetDateTime getPropertyOffsetDateTime() {
    return propertyOffsetDateTime;
  }

  final void setPropertyOffsetDateTime(OffsetDateTime propertyOffsetDateTime) {
    this.propertyOffsetDateTime = propertyOffsetDateTime;
  }

  final ContainedBean getContainedBeanProperty() {
    return containedBeanProperty;
  }

  final void setContainedBeanProperty(ContainedBean containedBeanProperty) {
    this.containedBeanProperty = containedBeanProperty;
  }

  final List<ContainedBean> getListOfContainedBean() {
    return listOfContainedBean;
  }

  final void setListOfContainedBean(List<ContainedBean> listOfContainedBean) {
    this.listOfContainedBean = listOfContainedBean;
  }

  final Map<String, ContainedBean> getMapOfContainedBean() {
    return mapOfContainedBean;
  }

  final void setMapOfContainedBean(Map<String, ContainedBean> mapOfContainedBean) {
    this.mapOfContainedBean = mapOfContainedBean;
  }

}
