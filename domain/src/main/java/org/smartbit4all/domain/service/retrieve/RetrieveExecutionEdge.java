package org.smartbit4all.domain.service.retrieve;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.Reference;
import org.smartbit4all.domain.meta.Reference.Join;

/**
 * This edge connects two node of the retrieval execution. It points to a node that is triggered and
 * contains the {@link DataReference} and the direction. If we trigger by the source or by the
 * target.
 * 
 * @author Peter Boros
 *
 */
public abstract class RetrieveExecutionEdge {

  /**
   * The referred node that is pointed by the edge. The starting node contains this edge in a list
   * {@link RetrieveExecutionNode#edges}.
   */
  final RetrieveExecutionNode node;

  /**
   * The {@link Reference} that defines the edge.
   */
  Reference<?, ?> reference;

  /**
   * Tells if the given edge go forward as the {@link Reference}. So the records of the
   * {@link DataReference#appType()} trigger the next node. If it's false then the records of the
   * {@link DataReference#extType()} triggers back.
   */
  boolean forward;

  /**
   * The keys that have been used so far. They won't be queried again. So if the record reference
   * graph is an acyclic graph then the algorithm will be ended within finite steps.
   */
  final Set<Object> keysAlreadyUsed = new HashSet<>();

  public RetrieveExecutionEdge(RetrieveExecutionNode node, Reference<?, ?> reference,
      boolean forward) {
    super();
    this.node = node;
    this.reference = reference;
    this.forward = forward;
    setupNode();
  }

  public Set<Object> getKeysAlreadyUsed() {
    return keysAlreadyUsed;
  }

  /**
   * The setup of the node means that we need a record index to access the record for this
   * reference.
   */
  private final void setupNode() {
    // We setup a key for the index in the node. It's the ordered list of the
    List<Property<?>> referredProperties = new ArrayList<>();
    for (Join<?> join : reference.joins()) {
      referredProperties.add(join.getTargetProperty());
    }
    referredProperties.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));

  }

}
