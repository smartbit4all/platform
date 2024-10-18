package org.smartbit4all.api.view.geomap;

import org.smartbit4all.api.collection.bean.SearchIndexDescriptor;
import org.smartbit4all.api.collection.bean.StoredCollectionDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceDescriptor;
import org.smartbit4all.api.geomap.bean.GeoMapDataSourceType;
import org.smartbit4all.api.invocation.bean.InvocationRequest;

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
      InvocationRequest invocationRequest) {
    return ofInvocationRequest(id, GeoMapApi.LAYER_DEFAULT, invocationRequest);
  }
  
  public static GeoMapDataSourceDescriptor ofInvocationRequest(
      String id,
      String targetLayer,
      InvocationRequest invocationRequest) {
    Objects.requireNonNull(id, "GeoMap DataSource id cannot be null!");
    Objects.requireNonNull(targetLayer, "GeoMap DataSource cannot target null layer!");
    Objects.requireNonNull(invocationRequest, "invocationRequest cannot be null!");

    return new GeoMapDataSourceDescriptor()
        .id(id)
        .targetLayer(targetLayer)
        .sourceType(GeoMapDataSourceType.INVOCATION_REQUEST)
        .invocationRequest(invocationRequest);
  }
  
  protected final GeoMapDataSourceDescriptor descriptor;

  protected GeoMapDataSourceConfigurer(GeoMapDataSourceDescriptor descriptor) {
    this.descriptor = descriptor;
  }

  public static final class StoredCollectionBasedGeoMapDataSourceConfigurer
      extends GeoMapDataSourceConfigurer {


    private StoredCollectionBasedGeoMapDataSourceConfigurer(GeoMapDataSourceDescriptor descriptor) {
      super(descriptor);
    }

    public MetadataConfigurationStep withMarkers(String... pathToPosition) {
      if (pathToPosition == null || pathToPosition.length == 0) {
        throw new IllegalArgumentException("pathToPosition cannot be null or empty!");
      }

      descriptor.setPathToDescription(Arrays.asList(pathToPosition));
      return new MetadataConfigurationStep();
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

      public GeoMapDataSourceDescriptor includeIfTruthy(String... pathToSentinelValue) {
        if (pathToSentinelValue == null || pathToSentinelValue.length == 0) {
          throw new IllegalArgumentException(
              "pathToSentinelValue cannot be null or empty if specified!");
        }

        descriptor.includeIf(Arrays.asList(pathToSentinelValue));
        return descriptor;
      }
      
      public GeoMapDataSourceDescriptor includeIfMatches(InvocationRequest predicate) {
        Objects.requireNonNull(predicate, "predicate cannot be null!");

        descriptor.inclusionPredicate(predicate);
        return descriptor;
      }

      public GeoMapDataSourceDescriptor includeAlways() {
        return descriptor;
      }

    }

  }

  public static final class SearchIndexBasedGeoMapDataSourceConfigurer
      extends GeoMapDataSourceConfigurer {

    private SearchIndexBasedGeoMapDataSourceConfigurer(GeoMapDataSourceDescriptor descriptor) {
      super(descriptor);
    }

  }

}
