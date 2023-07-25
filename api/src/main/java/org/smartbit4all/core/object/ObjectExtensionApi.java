package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectExtensionDescriptor;

public interface ObjectExtensionApi {

  String SCHEMA = "object-extension";

  Stream<ObjectNode> findAllObjectExtensionDescriptors();

  Optional<ObjectNode> findObjectExtensionDescriptorByName(String name);

  ObjectExtensionBuilder create();

  ObjectExtensionBuilder update(String extensionName);

  URI saveNewExtensionDescriptor(ObjectExtensionDescriptor descriptor);

}
