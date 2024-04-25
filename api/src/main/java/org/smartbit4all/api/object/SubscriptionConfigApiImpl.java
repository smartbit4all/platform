package org.smartbit4all.api.object;

import java.util.List;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.domain.data.TableData;
import static java.util.stream.Collectors.toList;

public class SubscriptionConfigApiImpl extends PrimaryApiImpl<SubscriptionConfigContributionApi>
    implements SubscriptionConfigApi {

  public SubscriptionConfigApiImpl() {
    super(SubscriptionConfigContributionApi.class);
  }

  @Override
  public TableData<?> postProcess(TableData<?> td) {
    return td;
  }

  @Override
  public List<String> revokableConfigs() {
    return getContributionApis().values().stream().flatMap(c -> c.getRevokableConfigs().stream())
        .distinct().collect(toList());
  }

}
