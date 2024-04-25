package org.smartbit4all.api.object;

import java.util.List;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.domain.data.TableData;

/**
 * This api is an extension point to help to manage the subscriptions. It can be a primary api alter
 * on.
 * 
 * @author Peter Boros
 */
public interface SubscriptionConfigApi extends PrimaryApi<SubscriptionConfigContributionApi> {

  TableData<?> postProcess(TableData<?> td);

  List<String> revokableConfigs();

}
