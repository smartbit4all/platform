package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.bean.ObjectLookupParameter;
import org.smartbit4all.api.collection.bean.ObjectLookupResult;
import org.smartbit4all.api.object.bean.ObjectMappingDefinition;

/**
 * The {@link ObjectLookup} is a generic abstract class for lookup a collection by the values of an
 * object. The lookup could be necessary if the original object has some values but not all and we
 * need to fill the extended the properties via the values of the object found by the lookup
 * mechanism.
 * 
 * @author Peter Boros
 */
public abstract class ObjectLookup {

  public abstract ObjectLookupResult lookup(Object object, ObjectLookupParameter parameter);

  public abstract void fillObjects(List<Map<String, Object>> objects,
      ObjectLookupParameter parameter, ObjectMappingDefinition mapping);

}
