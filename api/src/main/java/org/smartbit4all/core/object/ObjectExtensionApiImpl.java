package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.object.bean.ObjectExtensionDescriptor;
import org.springframework.beans.factory.annotation.Autowired;

public class ObjectExtensionApiImpl implements ObjectExtensionApi {

  private static final String MAP = "extension-descriptors";
  private static final Logger log = LoggerFactory.getLogger(ObjectExtensionApiImpl.class);

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;
  @Autowired
  private CollectionApi collectionApi;

  @Override
  public Stream<ObjectNode> findAllObjectExtensionDescriptors() {
    return collectionApi.map(SCHEMA, MAP).uris().values().stream().map(objectApi::loadLatest);
  }

  @Override
  public Optional<ObjectNode> findObjectExtensionDescriptorByName(String name) {
    return Optional
        .ofNullable(collectionApi
            .map(SCHEMA, MAP)
            .uris()
            .get(name))
        .map(objectApi::loadLatest);
  }

  @Override
  public ObjectExtensionBuilder create() {
    return new ObjectExtensionBuilder(this, objectApi);
  }

  @Override
  public ObjectExtensionBuilder update(String extensionName) {
    return findObjectExtensionDescriptorByName(extensionName)
        .map(n -> n.getObject(ObjectExtensionDescriptor.class))
        .map(descriptor -> new ObjectExtensionBuilder(this, objectApi, descriptor))
        .orElseGet(() -> create().name(extensionName));
  }

  @Override
  public URI saveNewExtensionDescriptor(ObjectExtensionDescriptor descriptor) {
    URI uri = objectApi.saveAsNew(SCHEMA, descriptor);
    collectionApi.map(SCHEMA, MAP).put(descriptor.getName(), objectApi.getLatestUri(uri));
    return uri;
  }


}
