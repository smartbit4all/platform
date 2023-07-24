package org.smartbit4all.api.object;

import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;

public class BranchListApi extends BranchContainerApi {

  /**
   * The object api.
   */
  private ObjectApi objectApi;

  /**
   * The collection api.
   */
  private CollectionApi collectionApi;

  /**
   * The object node that owns the given reference / list / map. We have a path to access the given
   * reference container.
   */
  private ObjectNode containerObjectNode;

  /**
   * The path to access the given reference container in the {@link #containerObjectNode}.
   */
  private String[] path;

  /**
   * The collection api container object.
   */
  private StoredList storedList;

  public BranchListApi(ObjectApi objectApi, CollectionApi collectionApi) {
    super();
    this.objectApi = objectApi;
    this.collectionApi = collectionApi;
  }

  public final ObjectNode getContainerObjectNode() {
    return containerObjectNode;
  }

  public final BranchListApi containerObjectNode(ObjectNode containerObjectNode) {
    this.containerObjectNode = containerObjectNode;
    return this;
  }

  public final String[] getPath() {
    return path;
  }

  public final BranchListApi path(String[] path) {
    this.path = path;
    return this;
  }

  public final StoredList getStoredList() {
    return storedList;
  }

  public final BranchListApi storedList(StoredList storedList) {
    this.storedList = storedList;
    return this;
  }

}
