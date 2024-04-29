package org.smartbit4all.api.object;

import java.util.List;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.org.bean.ACLOperationReference;
import org.smartbit4all.api.org.bean.ACLSubjectSubscription;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.domain.data.TableData;
import static org.smartbit4all.core.utility.StringConstant.joinDot;

/**
 * This api is an extension point to help to manage the subscriptions. It can be a primary api alter
 * on.
 * 
 * @author Peter Boros
 */
public interface SubscriptionConfigApi extends PrimaryApi<SubscriptionConfigContributionApi> {

  public static final String ENTITY_SUMMARY = "entitySummary";

  public static final String NAME = "name";

  public static final String SEARCH_USER_SUBSCRIPTION = "UserSubscriptionSearch";

  public static final String SUBSCRIPTION_SUBJECT_TYPE =
      joinDot(ACLSubjectSubscription.SUBJECT, Subject.TYPE);
  public static final String SUBSCRIPTION_SUBJECT_TYPE_NAME =
      joinDot(ACLSubjectSubscription.SUBJECT, Subject.TYPE, NAME);
  public static final String SUBSCRIPTION_SUBJECT_MODEL =
      joinDot(ACLSubjectSubscription.SUBJECT, Subject.MODEL);
  public static final String SUBSCRIPTION_SUBJECT_NAME =
      joinDot(ACLSubjectSubscription.SUBJECT, NAME);
  public static final String SUBSCRIPTION_SUBJECT_URI =
      joinDot(ACLSubjectSubscription.SUBJECT, Subject.REF);
  public static final String SUBSCRIPTION_OPERATION =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.OPERATION);
  public static final String SUBSCRIPTION_OPERATION_NAME =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.OPERATION,
          NAME);
  public static final String SUBSCRIPTION_OPERATION_COMMENT =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.COMMENT);
  public static final String SUBSCRIPTION_OPERATION_ENTITYURI =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.ENTITY_URI);
  public static final String SUBSCRIPTION_OPERATION_ENTITYSUMMARY =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ENTITY_SUMMARY);
  public static final String SUBSCRIPTION_OPERATION_CONTEXTCONFIG =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.CONFIG);

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
