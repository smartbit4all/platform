package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.collection.bean.StoredSequenceData;
import org.smartbit4all.domain.data.storage.TransactionalStorage;
import org.springframework.transaction.annotation.Propagation;

/**
 * The sequence management is a bit more complicated because the update of the sequence must be
 * implemented in a separate transaction to avoid cross blocking.
 * 
 * @author Peter Boros
 */
public interface StorageSequenceApi {

  /**
   * @param sequenceURI The uri of the {@link StoredSequenceData}
   * @return Return the next value
   */
  @TransactionalStorage(propagation = Propagation.REQUIRES_NEW)
  Long next(URI sequenceURI);

  /**
   * Return the next number of sequence in an array.
   * 
   * @param sequenceURI The uri of the {@link StoredSequenceData}
   * @param count The number of sequence values we ask.
   * @return The values in order.
   */
  @TransactionalStorage(propagation = Propagation.REQUIRES_NEW)
  List<Long> next(URI sequenceURI, int count);

  /**
   * Retrieves the current value of the sequence.
   * 
   * @param sequenceURI The uri of the {@link StoredSequenceData}
   * @return The current value of the sequence or -1 if the sequence doesn't exist.
   */
  Long current(URI sequenceURI);


}
