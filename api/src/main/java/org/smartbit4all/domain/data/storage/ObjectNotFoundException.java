package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;

/**
 * This generic runtime exception is thrown when a given uri is not identifying an object in the
 * given {@link ObjectStorage}.
 *
 * @author Peter Boros
 */
public class ObjectNotFoundException extends RuntimeException {

  public ObjectNotFoundException(URI objectUri, Class<?> clazz, String message) {
    super(message + StringConstant.LEFT_PARENTHESIS + objectUri + StringConstant.COMMA_SPACE
        + (clazz == null ? "unkonwn class"
            : clazz.toString())
        + StringConstant.RIGHT_PARENTHESIS);
  }

  public ObjectNotFoundException(List<URI> objectUris, Class<?> clazz, String message) {
    super(message + StringConstant.LEFT_PARENTHESIS + objectUris + StringConstant.COMMA_SPACE
        + (clazz == null ? "unkonwn class"
            : clazz.toString())
        + StringConstant.RIGHT_PARENTHESIS);
  }

}
