package org.smartbit4all.api.view.geomap;

import com.google.common.base.Strings;
import org.smartbit4all.api.collection.bean.SearchIndexDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapDataLoadingMode;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceType;
import org.smartbit4all.api.geomap.bean.GeoMapItemKind;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.domain.annotation.property.Id;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class GeoMapDataSourceConfigurer {

  public static StoredCollectionBasedGeoMapDataSourceConfigurer ofStoredCollection(
      String id,
      StoredCollectionDescriptor storedCollectionDescriptor) {
    return ofStoredCollection(id, GeoMapApi.LAYER_DEFAULT, storedCollectionDescriptor);
  }

  public static StoredCollectionBasedGeoMapDataSourceConfigurer ofStoredCollection(
      String id,
      String targetLayer,
      StoredCollectionDescriptor storedCollectionDescriptor) {
    Objects.requireNonNull(id, "GeoMap DataSource id cannot be null!");
    Objects.requireNonNull(targetLayer, "GeoMap DataSource cannot target null layer!");
    Objects.requireNonNull(
        storedCollectionDescriptor,
        "GeoMap DataSource storedCollectionDescriptor cannot be null!");
    Objects.requireNonNull(
        storedCollectionDescriptor.getCollectionType(),
        "storedCollectionDescriptor.collectionType cannot be null!");
    Objects.requireNonNull(
        storedCollectionDescriptor.getSchema(),
        "storedCollectionDescriptor.schema cannot be null!");
    Objects.requireNonNull(
        storedCollectionDescriptor.getName(),
        "storedCollectionDescriptor.name cannot be null!");

    return new StoredCollectionBasedGeoMapDataSourceConfigurer(new GeoMapDataSourceDescriptor()
        .id(id)
        .targetLayer(targetLayer)
        .sourceType(GeoMapDataSourceType.STORED_COLLECTION)
        .sourceCollection(storedCollectionDescriptor));
  }

  public static SearchIndexBasedGeoMapDataSourceConfigurer ofSearchIndex(
      String id,
      SearchIndexDescriptor searchIndexDescriptor) {
    return ofSearchIndex(id, GeoMapApi.LAYER_DEFAULT, searchIndexDescriptor);
  }

  public static SearchIndexBasedGeoMapDataSourceConfigurer ofSearchIndex(
      String id,
      String targetLayer,
      SearchIndexDescriptor searchIndexDescriptor) {
    Objects.requireNonNull(id, "GeoMap DataSource id cannot be null!");
    Objects.requireNonNull(targetLayer, "GeoMap DataSource cannot target null layer!");
    Objects.requireNonNull(searchIndexDescriptor, "searchIndexDescriptor cannot be null!");
    Objects.requireNonNull(
        searchIndexDescriptor.getSchema(),
        "searchIndexDescriptor.schema cannot be null!");
    Objects.requireNonNull(
        searchIndexDescriptor.getName(),
        "searchIndexDescriptor.name cannot be null!");

    return new SearchIndexBasedGeoMapDataSourceConfigurer(new GeoMapDataSourceDescriptor()
        .id(id)
        .targetLayer(targetLayer)
        .sourceType(GeoMapDataSourceType.SEARCH_INDEX)
        .searchIndexSchema(searchIndexDescriptor.getSchema())
        .searchIndexName(searchIndexDescriptor.getName()));
  }

  public static GeoMapDataSourceDescriptor ofInvocationRequest(
      String id,
      InvocationRequest invocationRequest,
      GeoMapDataLoadingMode loadingMode) {
    return ofInvocationRequest(id, GeoMapApi.LAYER_DEFAULT, invocationRequest, loadingMode);
  }

  public static GeoMapDataSourceDescriptor ofInvocationRequest(
      String id,
      String targetLayer,
      InvocationRequest invocationRequest,
      GeoMapDataLoadingMode loadingMode) {
    Objects.requireNonNull(id, "GeoMap DataSource id cannot be null!");
    Objects.requireNonNull(targetLayer, "GeoMap DataSource cannot target null layer!");
    Objects.requireNonNull(invocationRequest, "invocationRequest cannot be null!");
    Objects.requireNonNull(loadingMode, "loadingMode cannot be null!");

    return new GeoMapDataSourceDescriptor()
        .id(id)
        .targetLayer(targetLayer)
        .loadingMode(loadingMode)
        .sourceType(GeoMapDataSourceType.INVOCATION_REQUEST)
        .invocationRequest(invocationRequest);
  }

  protected final GeoMapDataSourceDescriptor descriptor;

  protected GeoMapDataSourceConfigurer(GeoMapDataSourceDescriptor descriptor) {
    this.descriptor = descriptor;
  }

  public final class LoadingModeConfigurationStep {

    public GeoMapDataSourceDescriptor withLoadingMode(GeoMapDataLoadingMode loadingMode) {
      if (loadingMode == null) {
        throw new IllegalArgumentException("loadingMode cannot be null!");
      }

      return descriptor.loadingMode(loadingMode);
    }
  }

  public static final class StoredCollectionBasedGeoMapDataSourceConfigurer
      extends GeoMapDataSourceConfigurer {


    private StoredCollectionBasedGeoMapDataSourceConfigurer(GeoMapDataSourceDescriptor descriptor) {
      super(descriptor);
    }

    public IdConfigurationStep withMarkers(String... pathToPosition) {
      if (pathToPosition == null || pathToPosition.length == 0) {
        throw new IllegalArgumentException("pathToPosition cannot be null or empty!");
      }

      descriptor.setItemKind(GeoMapItemKind.MARKER);
      descriptor.setPathToPosition(Arrays.asList(pathToPosition));
      return new IdConfigurationStep();
    }

    public IdConfigurationStep withPolygons(String... pathToBounds) {
      return withBoundedShapes(GeoMapItemKind.POLYGON, pathToBounds);
    }

    public IdConfigurationStep withLines(String... pathToBounds) {
      return withBoundedShapes(GeoMapItemKind.LINE, pathToBounds);
    }

    private IdConfigurationStep withBoundedShapes(GeoMapItemKind kind, String... pathToBounds) {
      if (pathToBounds == null || pathToBounds.length == 0) {
        throw new IllegalArgumentException("pathToBounds cannot be null or empty!");
      }

      descriptor.setItemKind(kind);
      descriptor.setPathToBounds(Arrays.asList(pathToBounds));
      return new IdConfigurationStep();
    }

    public final class IdConfigurationStep {
      public MetadataConfigurationStep withId(String... pathToId) {
        if (pathToId == null || pathToId.length == 0) {
          throw new IllegalArgumentException("pathToId cannot be null or empty!");
        }

        descriptor.setPathToId(Arrays.asList(pathToId));
        return new MetadataConfigurationStep();
      }

      public MetadataConfigurationStep withUriAsId() {
        return new MetadataConfigurationStep();
      }
    }

    public final class MetadataConfigurationStep {

      public InclusionConfigurationStep withMetadata(MetadataConfigurer metadataConfigurer) {
        Objects.requireNonNull(metadataConfigurer, "metadataConfigurer cannot be null!");

        final MetadataConfiguration config = new MetadataConfiguration();
        metadataConfigurer.accept(config);
        if (config.pathToTitle != null) {
          descriptor.pathToTitle(Arrays.asList(config.pathToTitle));
        }

        if (config.pathToDescription != null) {
          descriptor.pathToDescription(Arrays.asList(config.pathToDescription));
        }

        return new InclusionConfigurationStep();
      }

    }

    public static final class MetadataConfiguration {
      private String[] pathToTitle;
      private String[] pathToDescription;

      public MetadataConfiguration pathToTitle(String... pathToTitle) {
        if (pathToTitle == null || pathToTitle.length == 0) {
          throw new IllegalArgumentException("pathToTitle cannot be null or empty if specified!");
        }

        this.pathToTitle = pathToTitle;
        return this;
      }

      public MetadataConfiguration pathToDescription(String... pathToDescription) {
        if (pathToDescription == null || pathToDescription.length == 0) {
          throw new IllegalArgumentException(
              "pathToDescription cannot be null or empty if specified!");
        }

        this.pathToDescription = pathToDescription;
        return this;
      }
    }

    public interface MetadataConfigurer extends Consumer<MetadataConfiguration> {
      MetadataConfigurer NONE = it -> {};
    }

    public final class InclusionConfigurationStep {

      public LoadingModeConfigurationStep includeIfTruthy(String... pathToSentinelValue) {
        if (pathToSentinelValue == null || pathToSentinelValue.length == 0) {
          throw new IllegalArgumentException(
              "pathToSentinelValue cannot be null or empty if specified!");
        }

        descriptor.includeIf(Arrays.asList(pathToSentinelValue));
        return new LoadingModeConfigurationStep();
      }

      public LoadingModeConfigurationStep includeIfMatches(InvocationRequest predicate) {
        Objects.requireNonNull(predicate, "predicate cannot be null!");

        descriptor.inclusionPredicate(predicate);
        return new LoadingModeConfigurationStep();
      }

      public LoadingModeConfigurationStep includeAlways() {
        return new LoadingModeConfigurationStep();
      }

    }

  }

  public static final class SearchIndexBasedGeoMapDataSourceConfigurer
      extends GeoMapDataSourceConfigurer {

    private SearchIndexBasedGeoMapDataSourceConfigurer(GeoMapDataSourceDescriptor descriptor) {
      super(descriptor);
    }

    public IdColumnConfigurationStep withPosition(String latitudeCol, String longitudeCol) {
      if (Strings.isNullOrEmpty(latitudeCol)) {
        throw new IllegalArgumentException("latitudeCol cannot be null or empty!");
      }

      if (Strings.isNullOrEmpty(longitudeCol)) {
        throw new IllegalArgumentException("longitudeCol cannot be null or empty!");
      }

      descriptor.setLatitudeColumn(latitudeCol);
      descriptor.setLongitudeColumn(longitudeCol);

      return new IdColumnConfigurationStep();
    }

    public final class IdColumnConfigurationStep {

      public MetadataColumnConfigurationStep withId(String idCol) {
        if (Strings.isNullOrEmpty(idCol)) {
          throw new IllegalArgumentException("idCol cannot be null or empty!");
        }

        descriptor.setIdColumn(idCol);
        return new MetadataColumnConfigurationStep();
      }

      public MetadataColumnConfigurationStep withUriAsId() {
        return withId("uri");
      }

    }


    public final class MetadataColumnConfigurationStep {

      public GeoMapDataSourceDescriptor withMetadata(
          MetadataColumnConfigurer metadataColumnConfigurer) {
        Objects.requireNonNull(
            metadataColumnConfigurer,
            "metadataColumnConfigurer cannot be null!");

        final MetadataColumnConfiguration config = new MetadataColumnConfiguration();
        metadataColumnConfigurer.accept(config);
        if (config.titleCol != null) {
          descriptor.setTitleColumn(config.titleCol);
        }

        if (config.descriptionCol != null) {
          descriptor.setDescriptionColumn(config.descriptionCol);
        }

        return descriptor;
      }
    }

    public static final class MetadataColumnConfiguration {
      private String titleCol;
      private String descriptionCol;

      public MetadataColumnConfiguration titleCol(String titleCol) {
        if (Strings.isNullOrEmpty(titleCol)) {
          throw new IllegalArgumentException("titleCol cannot be null or empty!");
        }

        this.titleCol = titleCol;
        return this;
      }

      public MetadataColumnConfiguration descriptionCol(String descriptionCol) {
        if (Strings.isNullOrEmpty(descriptionCol)) {
          throw new IllegalArgumentException("descriptionCol cannot be null or empty!");
        }

        this.descriptionCol = descriptionCol;
        return this;
      }

    }

    public interface MetadataColumnConfigurer extends Consumer<MetadataColumnConfiguration> {
      MetadataColumnConfigurer NONE = it -> {};
    }


  }

}
