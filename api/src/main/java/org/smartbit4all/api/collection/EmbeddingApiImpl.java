package org.smartbit4all.api.collection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.bean.VectorValue;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.invocation.bean.ServiceConnection;

/**
 * The default implementation of the {@link EmbeddingApi}.
 * 
 * @author Peter Boros
 */
public final class EmbeddingApiImpl extends PrimaryApiImpl<EmbeddingContributionApi>
    implements EmbeddingApi {

  private static final String TEXT_PROPERTY = "TEXT";

  public EmbeddingApiImpl() {
    super(EmbeddingContributionApi.class);
  }

  @Override
  public VectorValue embed(ServiceConnection service, Map<String, Object> object,
      List<String> pathes) {
    return getContributionApi(service.getApiName()).embed(service, object, pathes);
  }

  @Override
  public VectorValue embed(ServiceConnection service, Map<String, Object> object) {
    return embed(service, object, null);
  }

  @Override
  public VectorValue embed(ServiceConnection service, String text) {
    Map<String, Object> obj = new LinkedHashMap<>();
    obj.put(TEXT_PROPERTY, text);
    return embed(service, obj);
  }

}
