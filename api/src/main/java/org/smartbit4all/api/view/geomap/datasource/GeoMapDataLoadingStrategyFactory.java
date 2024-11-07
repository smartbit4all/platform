package org.smartbit4all.api.view.geomap.datasource;

import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;

public interface GeoMapDataLoadingStrategyFactory {

  GeoMapDataLoadingStrategy create(final GeoMapDataSourceDescriptor dataSourceDescriptor);

}
