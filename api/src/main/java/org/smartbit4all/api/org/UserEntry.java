package org.smartbit4all.api.org;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.org.bean.User;

public class UserEntry {

  private static final Logger log = LoggerFactory.getLogger(UserEntry.class);

  private User user;

  private BinaryData image;
  
  /**
   * The image to be calculated.
   */
//  private FutureTask<BinaryData> imageFuture;

  private final Map<URI, GroupEntry> groups = new HashMap<>();

  private Supplier<BinaryData> imageSupplier;

  public UserEntry(User user, Supplier<BinaryData> imageSupplier) {
    super();
    this.user = user;
    this.imageSupplier = imageSupplier;
//    imageFuture = new FutureTask<>(() -> imageSupplier.get());
  }

  public final User getUser() {
    return user;
  }

  public final Map<URI, GroupEntry> getGroups() {
    return groups;
  }

  public final BinaryData getImage() {
    try {
//      return imageFuture.get();
      if (image == null && imageSupplier != null) {
        image = imageSupplier.get();
        imageSupplier = null;
      }
      return image;
    } catch (Exception e) {
      log.error("Unable to get the avatar image for the " + user + " user.", e);
      return null;
    }
  }

}
