package org.smartbit4all.api.retrieval;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.api.applychange.ApplyChangeRequest;

/**
 * As a result of the subsequent {@link RetrievalApi} calls we can construct an {@link ObjectModel}.
 * It contains the Object nodes and their references as a directed graph. We can use the model to
 * make modifications and construct the {@link ApplyChangeRequest} for a data modification.
 * 
 * @author Peter Boros
 */
public class ObjectModel {

  /**
   * All the object contained by the model available in this map. They can be accessed via their URI
   * (let it be version or not). The nodes contains the map representation of the object or the java
   * bean. But on the other hand we van access the referred object nodes also. An important
   * information about the {@link ObjectNode} if it is loaded or not.
   */
  Map<URI, ObjectNode> objects = new HashMap<>();

  public final Map<URI, ObjectNode> getObjects() {
    return objects;
  }

  /**
   * Recursively counts all the objects in the model.
   * 
   * @return
   */
  public long size() {
    return objects.values().stream().map(ObjectNode::allNodes).flatMap(s -> s).count();
  }

}
