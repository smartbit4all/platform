package org.smartbit4all.api.object;

import org.smartbit4all.domain.data.TableData;

public class SubscriptionConfigApiImpl implements SubscriptionConfigApi {

  @Override
  public TableData<?> postProcess(TableData<?> td) {
    return td;
  }

}
