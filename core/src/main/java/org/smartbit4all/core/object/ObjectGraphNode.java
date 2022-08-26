package org.smartbit4all.core.object;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link ObjectGraphNode} is a node of domain object graph. The RetrievalApi and the
 * ApplyChangeApi are using this structure. We can ask a subgraph to load with the RetrievalApi. It
 * can be added to an existing graph. All the objects are immutable in the graph so we have to ask a
 * modifiable copy to have a new version. It will be registered by this ObjectRef and we still have
 * the old version from the object. So at the end we can ask a difference or whatever else.
 * 
 * @author Peter Boros
 */
public class ObjectGraphNode {

  /**
   * The object graph is always representing a baseline version. So we need to remember the baseline
   * to know if there was any change. The baseline can be null if the given object is brand new in
   * the graph, for example we add a new object to a collection.
   */
  private Object baselineObject;

  /**
   * The working copy of the object that
   */
  private Object workingCopy;

  /**
   * 
   */
  private ObjectDefinition<?> definition;

  /**
   * The references named by the name of the {@link ReferenceDefinition}.
   */
  private Map<String, ObjectGraphEdge> references = new HashMap<>();

  private Map<String, ObjectGraphEdgeList> referenceLists = new HashMap<>();

  /**
   * Get the working copy and create it if it's not exists.
   * 
   * @return
   */
  public Object get() {
    return workingCopy;
  }

  /**
   * Set the working copy as a whole value. The programmer must be careful to set a proper object.
   * 
   * @param workingCopy
   */
  public void setWorkingCopy(Object workingCopy) {
    this.workingCopy = workingCopy;
  }

}
