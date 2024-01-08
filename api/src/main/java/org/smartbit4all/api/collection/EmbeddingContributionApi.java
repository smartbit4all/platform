package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.ContributionApi;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

/**
 * The embedding implementation api.
 * 
 * @author Peter Boros
 */
public interface EmbeddingContributionApi extends ContributionApi {

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

}
