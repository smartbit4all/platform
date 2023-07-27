package org.smartbit4all.core.object;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectExtensionDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;

public interface ObjectExtensionApi {

  String SCHEMA = "object-extension";

  String SESSION_CONTEXT = "session";
  String USER_CONTEXT = "user";
  String THIS_CONTEXT = "this";

  Stream<ObjectNode> findAllObjectExtensionDescriptors();

  Optional<ObjectNode> findObjectExtensionDescriptorByName(String name);

  ObjectExtensionBuilder create();

  ObjectExtensionBuilder update(String extensionName);

  Map<String, List<ObjectPropertyDescriptor>> getPropertiesByOwnerTypes(String extensionName);

  URI saveNewExtensionDescriptor(ObjectExtensionDescriptor descriptor);

  ObjectDisplay getObjectDisplay(URI objectUri, String extensionName);

}
