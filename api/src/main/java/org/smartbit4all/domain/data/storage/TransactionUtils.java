package org.smartbit4all.domain.data.storage;

import java.util.function.Supplier;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionUtils {

  private TransactionUtils() {}

  @SuppressWarnings("unchecked")
  public static <T extends TransactionSynchronization> T getOrRegisterTransactionHandler(
      String handlerId, Class<T> clazz, Supplier<T> handlerSupplier) {
    if (!TransactionSynchronizationManager.hasResource(handlerId)) {
      TransactionSynchronizationManager
          .registerSynchronization(handlerSupplier.get());
      TransactionSynchronizationManager.bindResource(handlerId, true);
    }
    Object handler = TransactionSynchronizationManager.getSynchronizations().stream()
        .filter(clazz::isInstance)
        .findFirst()
        .orElse(null);

    if (handler == null) {
      throw new IllegalStateException("No " + handlerId + " registered when requesting it");
    }
    return (T) handler;
  }

}
