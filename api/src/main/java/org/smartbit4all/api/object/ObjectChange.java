package org.smartbit4all.api.object;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The instance change means a new or deleted instance. The modification is not necessary as an
 * individual event because it consists of several other change item. If we have a new instance then
 * we get all the property changes also. There is no need to figure out what to in case of new
 * instance. In case of deleted instance we won't get any other change just the delete itself. The
 * root object is also produce this kind of event and we can identify it by it's URI.
 * 
 * This change event contains the property and referential changes. The object change itself can be
 * a new instance a modification and a deletion.
 * 
 * @author Peter Boros
 */
public class ObjectChange {

  URI uri;

  /**
   * The changes of the properties.
   */
  List<PropertyChange<?>> properties = new ArrayList<>();

  /**
   * The changes of the references.
   */
  List<ReferenceChange> references = new ArrayList<>();

  /**
   * The changes of the collections.
   */
  List<CollectionChange> collections = new ArrayList<>();

  /**
   * @param uri
   */
  ObjectChange(URI uri) {
    this.uri = uri;
  }

}
