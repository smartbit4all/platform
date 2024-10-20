package org.smartbit4all.api.view.geomap.datasource;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.google.common.base.Strings;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.geomap.bean.GPSPosition;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapItem;
import org.smartbit4all.api.geomap.bean.GeoMapItemKind;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertySet;
import org.smartbit4all.domain.service.query.QueryInput;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

final class SearchIndexBasedGeoMapDataLoadingStrategy extends GeoMapDataLoadingStrategy {
  private final ObjectApi           objectApi;
  private final CollectionApi       collectionApi;
  private final FilterExpressionApi filterExpressionApi;

  SearchIndexBasedGeoMapDataLoadingStrategy(
      GeoMapDataSourceDescriptor dataSourceDescriptor,
      ObjectApi objectApi, CollectionApi collectionApi,
      FilterExpressionApi filterExpressionApi) {
    super(dataSourceDescriptor);

    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
    this.filterExpressionApi = filterExpressionApi;
  }

  @Override
  public List<GeoMapItem> load(GeoMapViewport viewport) {
    final GeoMapItemKind itemKind = dataSourceDescriptor.getItemKind();

    final SearchIndex<?> searchIndex = collectionApi.searchIndex(
        dataSourceDescriptor.getSearchIndexSchema(),
        dataSourceDescriptor.getSearchIndexName());
    final EntityDefinition entityDefinition = searchIndex.getDefinition().getDefinition();

    final Property<Double> lat = prop(entityDefinition, dataSourceDescriptor.getLatitudeColumn());
    final Property<Double> lng = prop(entityDefinition, dataSourceDescriptor.getLongitudeColumn());
    final Property<?> id = prop(entityDefinition, dataSourceDescriptor.getIdColumn());
    final Property<String> title = Strings.isNullOrEmpty(dataSourceDescriptor.getTitleColumn())
        ? null
        : prop(entityDefinition, dataSourceDescriptor.getTitleColumn());
    final Property<String> desc = Strings.isNullOrEmpty(dataSourceDescriptor.getDescriptionColumn())
        ? null
        : prop(entityDefinition, dataSourceDescriptor.getDescriptionColumn());

    final RectangularBounds bounds = RectangularBounds.ofViewport(viewport);
    Expression where = Expression.AND(
        lat.ge(bounds.latMin),
        lat.le(bounds.latMax),
        lng.ge(bounds.lngMin),
        lng.le(bounds.lngMax));

    if (dataSourceDescriptor.getFilterExpressionList() != null) {
      final Expression customWhere = filterExpressionApi.constructExpression(
          dataSourceDescriptor.getFilterExpressionList(),
          entityDefinition);
      where = where.BRACKET().AND(customWhere);
    }

    final PropertySet select = new PropertySet(Arrays.asList(id, lat, lng, title, desc).stream()
        .filter(Objects::nonNull)
        .collect(toList()));
    final QueryInput query = new QueryInput();
    query.from(entityDefinition);
    query.select(select);
    query.where(where);
    return searchIndex.executeSearch(query).rows().stream()
        .map(row -> new GeoMapItem()
            .kind(itemKind)
            .id(String.valueOf(row.get(id)))
            .position(new GPSPosition()
                .latitude(row.get(lat).floatValue())
                .longitude(row.get(lng).floatValue()))
            .label((title != null)
                ? row.get(title)
                : null)
            .description((desc != null)
                ? row.get(desc)
                : null))
        .collect(toList());
  }

  @SuppressWarnings({"unchecked"})
  private static <T> Property<T> prop(final EntityDefinition entityDefinition, String property) {
    return (Property<T>) entityDefinition.getProperty(property);
  }



}
