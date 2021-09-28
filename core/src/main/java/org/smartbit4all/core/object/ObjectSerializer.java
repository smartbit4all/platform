package org.smartbit4all.core.object;

import java.util.Optional;
import org.smartbit4all.api.binarydata.BinaryData;

/**
 * The domain objects must be serialized by definition. The serialization method could be different
 * for object definitions and storages. This interface contains the two method for the
 * serialization. It can be configured for every {@link ObjectDefinition} but the {@link ObjectApi}
 * will define a default one also.
 * 
 * @author Peter Boros
 */
public interface ObjectSerializer {

  String getName();

  BinaryData serialize(Object obj, Class<?> clazz);

  <T> Optional<T> deserialize(BinaryData data, Class<T> clazz);

}
