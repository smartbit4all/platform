package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;

/**
 * Facilitates registration and management of object layout archetypes.
 *
 * @author Szabolcs Bazil Papp
 *
 */
public interface ObjectLayoutApi {

  String SCHEMA = "object-layout";

  String SESSION_CONTEXT = "session";
  String USER_CONTEXT = "user";
  String THIS_CONTEXT = "this";

  String DEFAULT_LAYOUT = "default";


  Stream<ObjectNode> findAllObjectLayoutDescriptors();

  Optional<ObjectNode> findObjectLayoutDescriptorByName(String name);

  ObjectLayoutBuilder create(String name);

  ObjectLayoutBuilder update(String name);

  URI saveNewLayoutDescriptor(ObjectLayoutDescriptor descriptor);

  ObjectDisplay getObjectDisplay(URI objectUri, String name);

  ObjectDisplay getObjectDisplay(URI objectUri, URI descriptorUri);

  ObjectDisplay getObjectDisplay(URI objectUri, ObjectLayoutDescriptor descriptor);

  ObjectDisplay getSketchDisplay(ObjectNode objectNode, ObjectLayoutDescriptor descriptor);

}
