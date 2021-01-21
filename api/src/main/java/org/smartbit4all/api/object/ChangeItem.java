package org.smartbit4all.api.object;

import java.net.URI;

public abstract class ChangeItem {

  /**
   * The URI of the API object that embeds the given property, reference or collection. It's the
   * parent reference of these items. Inside an object we can see only our parent not the grannies.
   * If we no that we have grandparents then we have to subscribe for their references or
   * collections to have higher level of notification about them.
   */
  private final URI parentObjectURI;

  /**
   * The item name inside the given object. Properties, references and collections are also
   * represented as a name property inside an object.
   */
  protected final String name;

  /**
   * Constructing a new instance with the give parent URI.
   * 
   * @param uri
   */
  ChangeItem(URI parentObjectURI, String name) {
    super();
    this.parentObjectURI = parentObjectURI;
    this.name = name;
  }

  /**
   * The URI of the API object that embeds the given property, reference or collection. It's the
   * parent reference of these items. Inside an object we can see only our parent not the grannies.
   * If we no that we have grandparents then we have to subscribe for their references or
   * collections to have higher level of notification about them.
   * 
   * @return
   */
  public final URI getParentObjectURI() {
    return parentObjectURI;
  }

  public final String getName() {
    return name;
  }

}
