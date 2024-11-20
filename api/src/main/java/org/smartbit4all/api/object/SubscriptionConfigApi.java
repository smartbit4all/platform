package org.smartbit4all.api.object;

import static org.smartbit4all.core.utility.StringConstant.joinUnder;
import java.util.List;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.org.bean.ACLOperationReference;
import org.smartbit4all.api.org.bean.ACLSubjectSubscription;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.domain.data.TableData;

/**
 * This api is an extension point to help to manage the subscriptions. It can be a primary api alter
 * on.
 * 
 * @author Peter Boros
 */
public interface SubscriptionConfigApi extends PrimaryApi<SubscriptionConfigContributionApi> {

  public static final String ENTITY_SUMMARY = "entitySummary";

  public static final String NAME = "name";

  public static final String REVOKE_SUPPORTED = "revokeSupported";

  public static final String MODIFY_SUPPORTED = "modifySupported";

  public static final String SEARCH_USER_SUBSCRIPTION = "UserSubscriptionSearch";

  public static final String SUBSCRIPTION_SUBJECT_TYPE =
      joinUnder(ACLSubjectSubscription.SUBJECT, Subject.TYPE);
  public static final String SUBSCRIPTION_SUBJECT_TYPE_NAME =
      joinUnder(ACLSubjectSubscription.SUBJECT, Subject.TYPE, NAME);
  public static final String SUBSCRIPTION_SUBJECT_MODEL =
      joinUnder(ACLSubjectSubscription.SUBJECT, Subject.MODEL);
  public static final String SUBSCRIPTION_SUBJECT_NAME =
      joinUnder(ACLSubjectSubscription.SUBJECT, NAME);
  public static final String SUBSCRIPTION_SUBJECT_URI =
      joinUnder(ACLSubjectSubscription.SUBJECT, Subject.REF);
  public static final String SUBSCRIPTION_OPERATION_REVOKE_SUPPORTED =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, REVOKE_SUPPORTED);
  public static final String SUBSCRIPTION_OPERATION_MODIFY_SUPPORTED =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, MODIFY_SUPPORTED);
  public static final String SUBSCRIPTION_OPERATION =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.OPERATION);
  public static final String SUBSCRIPTION_OPERATION_NAME =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.OPERATION,
          NAME);
  public static final String SUBSCRIPTION_OPERATION_COMMENT =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.COMMENT);
  public static final String SUBSCRIPTION_OPERATION_ENTITYURI =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.ENTITY_URI);
  public static final String SUBSCRIPTION_OPERATION_ENTITYSUMMARY =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, ENTITY_SUMMARY);
  public static final String SUBSCRIPTION_OPERATION_CONTEXTCONFIG =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.CONFIG);
  public static final String SUBSCRIPTION_OPERATION_CONTEXTCONFIG_NAME =
      joinUnder(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.CONFIG, NAME);

  /**
   * This processing is responsible for the table data of the subscription based on the contribution
   * apis we currently have.
   * 
   * @param td
   * @return
   */
  TableData<?> postProcess(SearchIndex<?> searchIndex, TableData<?> td);

  List<String> revokableConfigs();

}
