package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.Collection;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This generic runtime exception is thrown when a given uri is not identifying an object in the
 * given {@link ObjectStorage}.
 *
 * @author Peter Boros
 */
public class ObjectNotFoundException extends RuntimeException {

  private static final String UNKNOWN_CLASS = "unknown class";

  public ObjectNotFoundException(URI objectUri, Class<?> clazz, String message) {
    super(message + StringConstant.LEFT_PARENTHESIS + objectUri + StringConstant.COMMA_SPACE
        + (clazz == null ? UNKNOWN_CLASS
            : clazz.toString())
        + StringConstant.RIGHT_PARENTHESIS);
  }

  public ObjectNotFoundException(Collection<String> objectUris, Class<?> clazz, String message) {
    super(message + StringConstant.LEFT_PARENTHESIS + objectUris + StringConstant.COMMA_SPACE
        + (clazz == null ? UNKNOWN_CLASS
            : clazz.toString())
        + StringConstant.RIGHT_PARENTHESIS);
  }

}
