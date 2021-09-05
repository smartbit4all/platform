package org.smartbit4all.storage.fs;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.ObjectUriProvider;

public class ObjectUriProviderFSDefault<T> implements ObjectUriProvider<T> {

  private static final Logger log = LoggerFactory.getLogger(ObjectUriProviderFSDefault.class);

  /**
   * The prefix of the URI - the scheme and the host. We need to add the path and fragment only.
   */
  private final String uriPrefix;

  private final String extension;

  public ObjectUriProviderFSDefault(String uriPrefix, String extension) {
    super();
    this.uriPrefix = uriPrefix;
    this.extension = extension;
  }

  @Override
  public URI constructUri(T object) {
    // We doesn't use the object itself.
    String uuid = UUID.randomUUID().toString();
    LocalDateTime now = LocalDateTime.now();
    // The URI of the approval in this case is the physical location by the file system. There is
    // one folder for every year / month / day. The name of the file that contains the data is a
    // UUID generated on the fly.
    try {
      return new URI(uriPrefix + now.getYear() + StringConstant.SLASH
          + now.getMonthValue() + StringConstant.SLASH + now.getDayOfMonth() + StringConstant.SLASH
          + uuid + StringConstant.DOT + extension);
    } catch (URISyntaxException e) {
      log.error("Unabe to construct URI", e);
      return null;
    }
  }

}
