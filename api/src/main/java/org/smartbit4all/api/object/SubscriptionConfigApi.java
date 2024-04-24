package org.smartbit4all.api.object;

import org.smartbit4all.domain.data.TableData;

/**
 * This api is an extension point to help to manage the subscriptions. It can be a primary api alter
 * on.
 * 
 * @author Peter Boros
 */
public interface SubscriptionConfigApi {

  TableData<?> postProcess(TableData<?> td);

}
