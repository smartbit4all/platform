package org.smartbit4all.api.org;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.types.binarydata.BinaryData;

public class GroupEntry {

  private static final Logger log = LoggerFactory.getLogger(GroupEntry.class);

  private Group group;

  /**
   * The image to be calculated.
   */
  private FutureTask<BinaryData> imageFuture;

  private final Map<URI, UserEntry> users = new HashMap<>();

  public GroupEntry(Group group, Supplier<BinaryData> imageSupplier) {
    super();
    this.group = group;
    imageFuture = new FutureTask<>(() -> imageSupplier.get());
  }

  public final Group getGroup() {
    return group;
  }

  public final Map<URI, UserEntry> getUsers() {
    return users;
  }

  public final BinaryData getImage() {
    try {
      return imageFuture.get();
    } catch (Exception e) {
      log.error("Unable to get the avatar image for the " + group + " group.", e);
      return null;
    }
  }

}
