package org.smartbit4all.api.view.geomap.datasource;

import java.util.List;
import org.smartbit4all.api.geomap.bean.GPSPosition;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;

public final class RectangularBounds {

  static RectangularBounds ofViewport(final GeoMapViewport viewport) {
    final List<GPSPosition> boundingPositions = viewport.getBounds();
    if (boundingPositions == null || boundingPositions.isEmpty()) {
      return RectangularBounds.defaultBounds();
    }

    final RectangularBounds bounds = new RectangularBounds();
    for (final GPSPosition position : boundingPositions) {
      final double lat = position.getLatitude();
      final double lng = position.getLongitude();

      int cmp = Double.compare(bounds.latMin, lat);
      if (cmp > 0) {
        bounds.latMin = lat;
      }

      cmp = Double.compare(bounds.latMax, lat);
      if (cmp < 0) {
        bounds.latMax = lat;
      }

      cmp = Double.compare(bounds.lngMin, lng);
      if (cmp > 0) {
        bounds.lngMin = lng;
      }

      cmp = Double.compare(bounds.lngMax, lng);
      if (cmp < 0) {
        bounds.lngMax = lng;
      }
    }

    return bounds;
  }

  static RectangularBounds defaultBounds() {
    final RectangularBounds bounds = new RectangularBounds();
    bounds.latMin = Double.MIN_VALUE;
    bounds.latMax = Double.MAX_VALUE;
    bounds.lngMin = Double.MIN_VALUE;
    bounds.lngMax = Double.MAX_VALUE;
    return bounds;
  }

  double latMin = Double.MAX_VALUE;
  double latMax = Double.MIN_VALUE;
  double lngMin = Double.MAX_VALUE;
  double lngMax = Double.MIN_VALUE;

  private RectangularBounds() {}

}
