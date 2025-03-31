package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Supplier;
import org.smartbit4all.core.utility.StringConstant;

/**
 * A kind of iterator supplying the basement of the time series Stream to process the content of the
 * storage in the sequence of the time it was created.
 */
final class StorageTimeSeriesIterator implements Supplier<List<URI>> {

  Storage storage;

  String setName;

  String clazzName;

  LocalDateTime from;

  LocalDateTime to;

  LocalDateTime currentFragment;

  ChronoUnit gradient;

  public StorageTimeSeriesIterator(Storage storage, String setName, String clazzName,
      LocalDateTime from,
      LocalDateTime to,
      ChronoUnit gradient) {
    super();
    this.storage = storage;
    this.setName = setName;
    this.gradient = gradient;
    this.clazzName = clazzName;
    this.from = truncateToGradient(from);
    this.to = truncateToGradient(to);
    this.currentFragment = from;
  }

  @Override
  public List<URI> get() {
    if (currentFragment == null) {
      return null;
    }
    String setToRead =
        (setName == null ? StringConstant.EMPTY : (setName + StringConstant.SLASH + setName))
            + storage.constructTimePath(currentFragment, gradient) + StringConstant.SLASH;
    List<URI> allUris = storage.readAllUris(setToRead, clazzName);
    LocalDateTime nextFragment = currentFragment.plus(1, gradient);
    if ((to != null && (nextFragment.isEqual(to) || nextFragment.isAfter(to)))
        || (to == null && allUris.isEmpty())) {
      currentFragment = null;
    } else {
      currentFragment = nextFragment;
    }
    return allUris;
  }

  LocalDateTime truncateToGradient(LocalDateTime dateTime) {
    switch (gradient) {
      case YEARS:
        return dateTime.withDayOfYear(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
      case MONTHS:
        return dateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
      case DAYS:
        return dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
      case HOURS:
        return dateTime.withMinute(0).withSecond(0).withNano(0);
      case MINUTES:
        return dateTime.withSecond(0).withNano(0);
      case SECONDS:
        return dateTime.withNano(0);
      default:
        throw new IllegalArgumentException("Unsupported ChronoUnit: " + gradient);
    }
  }

}
