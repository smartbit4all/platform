package org.smartbit4all.core.object;

import org.smartbit4all.api.mapbasedobject.bean.MapBasedObjectData;

public class HybridBean {

  private MasterBean masterBean;

  private MapBasedObjectData data;

  public MasterBean getMasterBean() {
    return masterBean;
  }

  public void setMasterBean(MasterBean masterBean) {
    this.masterBean = masterBean;
  }

  public MapBasedObjectData getData() {
    return data;
  }

  public void setData(MapBasedObjectData data) {
    this.data = data;
  }

}
