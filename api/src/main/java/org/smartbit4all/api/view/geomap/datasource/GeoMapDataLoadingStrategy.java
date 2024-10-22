package org.smartbit4all.api.view.geomap.datasource;

import java.util.List;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;

public abstract class GeoMapDataLoadingStrategy {

  protected final GeoMapDataSourceDescriptor dataSourceDescriptor;

  protected GeoMapDataLoadingStrategy(GeoMapDataSourceDescriptor dataSourceDescriptor) {
    this.dataSourceDescriptor = dataSourceDescriptor;
  }

  public abstract List<GeoMapItem> load(final GeoMapViewport viewport);

}
