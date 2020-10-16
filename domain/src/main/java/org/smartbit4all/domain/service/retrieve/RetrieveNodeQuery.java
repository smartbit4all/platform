package org.smartbit4all.domain.service.retrieve;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.core.SB4Function;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.utility.CompositeValue;

/**
 * The request for a retrieve node that contains the list of keys to use during the query. The next
 * round will produce the results and at the end of the round we set the references to complete the
 * operation by adding the new associations. In the mean time we collect the
 * {@link RetrieveExecutionNode#newRecordsToProcess} for the next round.
 * 
 * @author Peter Boros
 */
public abstract class RetrieveNodeQuery implements SB4Function {

  /**
   * The list of keys we have to use to query the next nodes for the node. In case of composite key
   * we must use the {@link CompositeValue} object with the values.
   */
  Set<Object> keyValues = new HashSet<>();

  /**
   * The result records after the query next round. It can be used to merge the result to the source
   * and instantiate all the references they need. The key of the map is the same as in the
   * {@link #keyValues} set. In case of composite key we must use the {@link CompositeValue} object
   * with the values.
   */
  Map<Object, DataRow> results = null;

}
