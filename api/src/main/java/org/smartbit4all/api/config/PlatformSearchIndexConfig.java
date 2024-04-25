package org.smartbit4all.api.config;

import java.util.Arrays;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.object.SubscriptionConfigApi;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACLOperationReference;
import org.smartbit4all.api.org.bean.ACLSubjectSubscription;
import org.smartbit4all.api.org.bean.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.smartbit4all.core.utility.StringConstant.joinDot;

@Configuration
public class PlatformSearchIndexConfig {

  public static final String SUBSCRIPTION_SUBJECT_TYPE =
      joinDot(ACLSubjectSubscription.SUBJECT, Subject.TYPE);
  public static final String SUBSCRIPTION_SUBJECT_MODEL =
      joinDot(ACLSubjectSubscription.SUBJECT, Subject.MODEL);
  public static final String SUBSCRIPTION_SUBJECT_NAME =
      joinDot(ACLSubjectSubscription.SUBJECT, "NAME");
  public static final String SUBSCRIPTION_OPERATION =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.OPERATION);
  public static final String SUBSCRIPTION_OPERATION_NAME =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.OPERATION,
          "NAME");
  public static final String SUBSCRIPTION_OPERATION_COMMENT =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.COMMENT);
  public static final String SUBSCRIPTION_OPERATION_ENTITYURI =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.ENTITY_URI);
  public static final String SUBSCRIPTION_OPERATION_ENTITYSUMMARY =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, "ENTITYSUMMARY");
  public static final String SUBSCRIPTION_OPERATION_CONTEXTCONFIG =
      joinDot(ACLSubjectSubscription.OPERATION_REFERENCE, ACLOperationReference.CONFIG);

  @Bean
  public SearchIndex<ACLSubjectSubscription> searchACLSubjectSubscription(
      SubjectManagementApi subjectManagementApi,
      @Autowired(required = false) SubscriptionConfigApi configApi) {
    return new SearchIndexImpl<>(OrgApiStorageImpl.ORG_SCHEME,
        ACLSubjectSubscription.class.getSimpleName(),
        OrgApiStorageImpl.ORG_SCHEME, ACLSubjectSubscription.class)
            .map(SUBSCRIPTION_SUBJECT_TYPE, ACLSubjectSubscription.SUBJECT, Subject.TYPE)
            .map(SUBSCRIPTION_SUBJECT_MODEL, ACLSubjectSubscription.SUBJECT, Subject.MODEL)
            .mapComplex(SUBSCRIPTION_SUBJECT_NAME,
                n -> subjectManagementApi.getDisplayValue(
                    n.getValueAsString(ACLSubjectSubscription.SUBJECT, Subject.MODEL),
                    Arrays.asList(n.getValue(Subject.class, ACLSubjectSubscription.SUBJECT)))
                    .get(0))
            .map(SUBSCRIPTION_OPERATION, ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.OPERATION)
            .map(SUBSCRIPTION_OPERATION_NAME, ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.OPERATION)
            .map(SUBSCRIPTION_OPERATION_COMMENT, ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.COMMENT)
            .map(SUBSCRIPTION_OPERATION_ENTITYURI, ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.ENTITY_URI)
            .map(SUBSCRIPTION_OPERATION_ENTITYSUMMARY, ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.ENTITY_URI)
            .map(SUBSCRIPTION_OPERATION_CONTEXTCONFIG, ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.CONFIG)
            .postProcess((td, si) -> {
              return configApi == null ? td : configApi.postProcess(td);
            });
  }

}
