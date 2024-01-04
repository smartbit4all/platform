package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

/**
 * Generic primary api for creating vector from the values of an object.
 */
public interface EmbeddingApi extends PrimaryApi<EmbeddingContributionApi> {

  /**
   * Creates a Vector from the object based on the property pathes provided.
   * 
   * @param service The service to use for the embedding. The service defines the algorithm also.
   * @param object The values from an object. Not the
   * @param pathes Optionally can define the property pathes like <b>obj:/#prop</b> or even the
   *        simple <b> prop </b> is working.
   * @return The Vector itself where the conversion algorithm is defined by the service parameter.
   */
  VectorValue embed(ServiceConnection service, Map<String, Object> object, List<String> pathes);

  VectorValue embed(ServiceConnection service, Map<String, Object> object);

  /**
   * Call this if the whole object is serialized into a String.
   * 
   * @param service
   * @param text The textual representation of the object.
   * @return
   */
  VectorValue embed(ServiceConnection service, String text);

}
