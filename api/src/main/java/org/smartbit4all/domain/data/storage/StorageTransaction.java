package org.smartbit4all.domain.data.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.storage.bean.TransactionData;

/**
 * The storage transaction is a generic transaction object for the transactions managed by the
 * {@link ObjectStorage} implementations. It contains the {@link TransactionData} object that is the
 * stored domain object about a transaction and it contains some additional transients to manage the
 * successful and failed transactions.
 * 
 * @author Peter Boros
 */
public class StorageTransaction {

  private final TransactionData data;

  private Map<StorageObject<?>, List<StorageSaveEvent>> saveEvents = null;

  public StorageTransaction(TransactionData data) {
    super();
    this.data = data;
  }

  public final TransactionData getData() {
    return data;
  }

  public final Map<StorageObject<?>, List<StorageSaveEvent>> getSaveEvents() {
    return saveEvents;
  }

  public final void addSaveEventItem(StorageObject<?> object, StorageSaveEvent saveEvent) {
    if (saveEvents == null) {
      saveEvents = new HashMap<>();
    }
    List<StorageSaveEvent> list = saveEvents.computeIfAbsent(object, o -> new ArrayList<>());
    list.add(saveEvent);
  }

}
