package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import java.util.List;

public class ACT_C {

  String cf3;

  String ff1;

  private List<ACT_D> actdList;

  public ACT_C() {}

  public ACT_C(String cf3, String ff1, List<ACT_D> dList) {
    super();
    this.cf3 = cf3;
    this.ff1 = ff1;
    this.setActdList(dList);
  }

  public String getCf3() {
    return cf3;
  }

  public void setCf3(String cf3) {
    this.cf3 = cf3;
  }

  public String getFf1() {
    return ff1;
  }

  public void setFf1(String ff1) {
    this.ff1 = ff1;
  }

  public List<ACT_D> getActdList() {
    return actdList;
  }

  public void setActdList(List<ACT_D> actdList) {
    this.actdList = actdList;
  }

}
