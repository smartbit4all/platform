package org.smartbit4all.api.collection;

import java.util.List;

/**
 * This object provides an atomic sequence that provides globally unique incrementing value. Can be
 * used as the classic database sequence.
 * 
 * @author Peter Boros
 */
public interface StoredSequence {

  /**
   * @return Return the next value
   */
  Long next();

  /**
   * Return the next number of sequence in an array.
   * 
   * @param count The number of sequence values we ask.
   * @return The values in order.
   */
  List<Long> next(int count);

}
