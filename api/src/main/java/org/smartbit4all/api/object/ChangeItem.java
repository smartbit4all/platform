package org.smartbit4all.api.object;

import java.util.UUID;

/**
 * The super class of item change events of an object. They can be {@link PropertyChange},
 * {@link ReferenceChange} and {@link CollectionChange} instances.
 * 
 * @author Peter Boros
 *
 */
public abstract class ChangeItem {

  /**
   * The unique identifier of the object that embeds the given property, reference or collection.
   * It's the parent reference of these items. Inside an object we can see only our parent not the
   * grannies. If we no that we have grandparents then we have to subscribe for their references or
   * collections to have higher level of notification about them.
   */
  private final UUID parentID;

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
  ChangeItem(UUID parentID, String name) {
    super();
    this.parentID = parentID;
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
  public final UUID getParentId() {
    return parentID;
  }

  /**
   * The name of the given item. All the items inside an object are in the same name space so they
   * must have unique names inside an object.
   * 
   * @return
   */
  public final String getName() {
    return name;
  }

}
