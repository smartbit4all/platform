package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.bean.EntityLookupParameter;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.PrimaryApi;

/**
 * Generic primary api for creating vector from the values of an object.
 */
public interface EmbeddingApi extends PrimaryApi<EmbeddingContributionApi> {

  /**
   * Creates a Vector from the object based on the property pathes provided.
   * 
   * @param serviceConnectionName The service to use for the embedding. The service defines the
   *        algorithm also.
   * @param object The values from an object. Not the
   * @param pathes Optionally can define the property pathes like <b>obj:/#prop</b> or even the
   *        simple <b> prop </b> is working.
   * @return The Vector itself where the conversion algorithm is defined by the service parameter.
   */
  VectorValue embed(String serviceConnectionName, Map<String, Object> object, List<String> pathes);

  VectorValue embed(String serviceConnectionName, Map<String, Object> object);

  /**
   * Call this if the whole object is serialized into a String.
   * 
   * @param serviceConnectionName
   * @param text The textual representation of the object.
   * @return
   */
  VectorValue embed(String serviceConnectionName, String text);

  List<Object> lookupEntities(String serviceConnectionName, EntityLookupParameter parameter);

}
