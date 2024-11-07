package org.smartbit4all.api.view.geomap.datasource;

import java.util.ArrayList;
import java.util.List;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;

final class InlineItemBasedGeoMapDataLoadingStrategy extends GeoMapDataLoadingStrategy {

  InlineItemBasedGeoMapDataLoadingStrategy(
      GeoMapDataSourceDescriptor dataSourceDescriptor) {
    super(dataSourceDescriptor);
  }

  @Override
  public List<GeoMapItem> load(GeoMapViewport viewport) {
    return new ArrayList<>(dataSourceDescriptor.getInlineItems());
  }

}
