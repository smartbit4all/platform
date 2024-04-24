package org.smartbit4all.api.object;

import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.domain.data.TableData;

public class SubscriptionConfigApiImpl extends PrimaryApiImpl<SubscriptionConfigContributionApi>
    implements SubscriptionConfigApi {

  public SubscriptionConfigApiImpl() {
    super(SubscriptionConfigContributionApi.class);
  }

  @Override
  public TableData<?> postProcess(TableData<?> td) {
    return td;
  }

}
