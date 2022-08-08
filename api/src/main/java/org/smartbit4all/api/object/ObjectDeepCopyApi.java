package org.smartbit4all.api.object;

import java.net.URI;
import java.util.List;
import org.smartbit4all.core.object.ReferenceDefinition;

/**
 * This api is responsible for the save as (deep copy) of an object.
 * 
 * @author Peter Boros
 */
public interface ObjectDeepCopyApi {

  /**
   * Constructs a brand new object based on the URI.
   * 
   * @param rootObject The original object that is the root of the operation.
   * @param includedReferences All the references that must be included in the copy. The rest of the
   *        potential references are skipped their reference remains the same after the operation.
   * @return The result will have a URI (it's a new instance) and all the references will be
   *         replaced with copied new instances.
   */
  URI copy(URI rootObject, List<ReferenceDefinition> includedReferences);

}
