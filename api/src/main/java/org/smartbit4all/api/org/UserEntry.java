package org.smartbit4all.api.org;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.types.binarydata.BinaryData;

public class UserEntry {

  private static final Logger log = LoggerFactory.getLogger(UserEntry.class);

  private User user;

  /**
   * The image to be calculated.
   */
  private FutureTask<BinaryData> imageFuture;

  private final Map<URI, GroupEntry> groups = new HashMap<>();

  public UserEntry(User user, Supplier<BinaryData> imageSupplier) {
    super();
    this.user = user;
    imageFuture = new FutureTask<>(() -> imageSupplier.get());
  }

  public final User getUser() {
    return user;
  }

  public final Map<URI, GroupEntry> getGroups() {
    return groups;
  }

  public final BinaryData getImage() {
    try {
      return imageFuture.get();
    } catch (Exception e) {
      log.error("Unable to get the avatar image for the " + user + " user.", e);
      return null;
    }
  }

}
