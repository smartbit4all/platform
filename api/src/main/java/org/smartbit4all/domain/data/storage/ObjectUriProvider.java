package org.smartbit4all.domain.data.storage;

import java.net.URI;

/**
 * A storage can be an URI provider also. In this case it contains an algorithm for generating a
 * storage URI for a given object.
 * 
 * @author Peter Boros
 */
public interface ObjectUriProvider<T> {

  URI constructUri(T object);

}
