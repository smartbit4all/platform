package org.smartbit4all.api.view.geomap.datasource;

import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.geomap.bean.GPSPosition;
import org.smartbit4all.api.geomap.bean.GPSRoute;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapItemKind;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;

final class StoredCollectionBasedGeoMapDataLoadingStrategy extends GeoMapDataLoadingStrategy {

  private static final Logger log =
      LoggerFactory.getLogger(StoredCollectionBasedGeoMapDataLoadingStrategy.class);

  private final ObjectApi objectApi;
  private final CollectionApi collectionApi;
  private final InvocationApi invocationApi;

  StoredCollectionBasedGeoMapDataLoadingStrategy(
      GeoMapDataSourceDescriptor dataSourceDescriptor,
      ObjectApi objectApi,
      CollectionApi collectionApi, InvocationApi invocationApi) {
    super(dataSourceDescriptor);

    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
    this.invocationApi = invocationApi;
  }

  @Override
  public List<GeoMapItem> load(GeoMapViewport viewport) {
    final StoredCollectionDescriptor collectionDescriptor =
        dataSourceDescriptor.getSourceCollection();
    final URI scope = collectionDescriptor.getScopeUri();
    final Set<URI> uris;
    switch (collectionDescriptor.getCollectionType()) {
      case MAP:
        final StoredMap map = (scope != null)
            ? collectionApi.map(
                scope,
                collectionDescriptor.getSchema(),
                collectionDescriptor.getName())
            : collectionApi.map(
                collectionDescriptor.getSchema(),
                collectionDescriptor.getName());
        uris = map.exists()
            ? new HashSet<>(map.uris().values())
            : Collections.emptySet();
        break;
      case LIST:
        final StoredList list = (scope != null)
            ? collectionApi.list(
                scope,
                collectionDescriptor.getSchema(),
                collectionDescriptor.getName())
            : collectionApi.list(
                collectionDescriptor.getSchema(),
                collectionDescriptor.getName());
        uris = list.exists()
            ? new HashSet<>(list.uris())
            : Collections.emptySet();
        break;
      case REFERENCE:
        throw new UnsupportedOperationException(
            "Stored references may not serve as a GeoMap DataSource!");
      default:
        throw new AssertionError("unexpected Stored Collection: " + dataSourceDescriptor);
    }

    final String[] idPath =
        (dataSourceDescriptor.getPathToId() == null || dataSourceDescriptor.getPathToId().isEmpty())
            ? null
            : dataSourceDescriptor.getPathToId().toArray(new String[0]);
    final String[] titlePath =
        (dataSourceDescriptor.getPathToTitle() == null || dataSourceDescriptor.getPathToTitle()
            .isEmpty())
                ? null
                : dataSourceDescriptor.getPathToTitle().toArray(new String[0]);
    final String[] descPath =
        (dataSourceDescriptor.getPathToDescription() == null
            || dataSourceDescriptor.getPathToDescription()
                .isEmpty())
                    ? null
                    : dataSourceDescriptor.getPathToDescription().toArray(new String[0]);
    final GeoMapItemKind itemKind = dataSourceDescriptor.getItemKind();
    final String[] posPath;
    final String[] boundsPath;
    if (GeoMapItemKind.MARKER == itemKind) {
      posPath = dataSourceDescriptor.getPathToPosition().toArray(new String[0]);
      boundsPath = null;
    } else {
      posPath = null;
      boundsPath = dataSourceDescriptor.getPathToBounds().toArray(new String[0]);
    }

    return uris.stream()
        .map(objectApi::load)
        .filter(it -> isInViewport(it, viewport))
        .filter(inclusionPredicate(dataSourceDescriptor))
        .map(it -> new GeoMapItem()
            .kind(itemKind)
            .id((idPath == null) ? it.getObjectUri().toString() : it.getValueAsString(idPath))
            .label((titlePath == null) ? null : it.getValueAsString(titlePath))
            .description((descPath == null) ? null : it.getValueAsString(descPath))
            .position((posPath == null) ? null : it.getValue(GPSPosition.class, posPath))
            .route(new GPSRoute().points((boundsPath == null)
                ? null
                : it.getValueAsList(GPSPosition.class, boundsPath))))
        .collect(toList());
  }

  private boolean isInViewport(
      final ObjectNode node,
      final GeoMapViewport viewport) {
    final RectangularBounds bounds = RectangularBounds.ofViewport(viewport);
    switch (dataSourceDescriptor.getItemKind()) {
      case MARKER:
        final String[] path = dataSourceDescriptor.getPathToPosition().toArray(new String[0]);
        final GPSPosition position = node.getValue(GPSPosition.class, path);
        if (position.getLatitude() == null || position.getLongitude() == null) {
          log.error("Encountered null-ish position of node [ {} ] at path: [ {} ]",
              node.getObjectUri(), path);
          return false;
        }

        final double lat = position.getLatitude();
        final double lng = position.getLongitude();
        return lat >= bounds.latMin
            && lat <= bounds.latMax
            && lng >= bounds.lngMin
            && lng <= bounds.lngMax;
      case POLYGON:
        // TODO: Implement intersection-check!
        // FALL-THROUGH for now...
      case LINE:
        // TODO: Implement intersection-check!
        return true;
      default:
        throw new AssertionError("unexpected ItemKind: " + dataSourceDescriptor);
    }
  }

  private Predicate<ObjectNode> inclusionPredicate(
      GeoMapDataSourceDescriptor dataSourceDescriptor) {
    final List<String> includeIfPath = dataSourceDescriptor.getIncludeIf();
    final InvocationRequest inclusionPredicate = dataSourceDescriptor.getInclusionPredicate();
    if ((includeIfPath == null || includeIfPath.isEmpty()) && inclusionPredicate == null) {
      return it -> true;
    }

    if (includeIfPath != null && !includeIfPath.isEmpty()) {
      final String[] path = includeIfPath.toArray(new String[0]);
      return it -> Boolean.TRUE.equals(it.getValue(Boolean.class, path));
    }

    return it -> {
      try {
        final InvocationParameter res = invocationApi.invoke(inclusionPredicate, it.getObjectUri());
        return Boolean.TRUE.equals(objectApi.asType(Boolean.class, res.getValue()));
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        return false;
      }
    };
  }

}
