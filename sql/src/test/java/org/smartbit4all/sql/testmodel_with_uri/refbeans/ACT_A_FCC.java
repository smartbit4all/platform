package org.smartbit4all.sql.testmodel_with_uri.refbeans;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ACT_A_FCC {

  String uid;

  String af1;

  String cf1;

  ACT_B actB;

  ACT_C actC;

  private List<URI> buids = new ArrayList<>();

  public ACT_A_FCC() {}

  public ACT_A_FCC(String uid, String af1, String cf1, ACT_B actB, ACT_C actC) {
    super();
    this.uid = uid;
    this.af1 = af1;
    this.cf1 = cf1;
    this.actB = actB;
    this.actC = actC;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getAf1() {
    return af1;
  }

  public void setAf1(String af1) {
    this.af1 = af1;
  }

  public String getCf1() {
    return cf1;
  }

  public void setCf1(String cf1) {
    this.cf1 = cf1;
  }

  public ACT_B getActB() {
    return actB;
  }

  public void setActB(ACT_B actB) {
    this.actB = actB;
  }

  public ACT_C getActC() {
    return actC;
  }

  public void setActC(ACT_C actC) {
    this.actC = actC;
  }

  public List<URI> getBuids() {
    return buids;
  }

  public void setBuids(List<URI> buids) {
    this.buids = buids;
  }

}
