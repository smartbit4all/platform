package org.smartbit4all.api.invocation;

import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.contribution.ContributionApiImpl;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ScriptEngineContributionApiImpl extends ContributionApiImpl
    implements ScriptEngineContributionApi {

  @Autowired
  protected ObjectApi objectApi;
  @Autowired
  protected CollectionApi collectionApi;
  @Autowired
  protected ObjectSerializerByObjectMapper objectSerizalizer;

  protected ScriptEngineContributionApiImpl(String apiName) {
    super(apiName);
  }

}
