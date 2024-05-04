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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformSearchIndexConfig {

  @Bean
  public SearchIndex<ACLSubjectSubscription> searchACLSubjectSubscription(
      SubjectManagementApi subjectManagementApi,
      SubscriptionConfigApi configApi) {
    return new SearchIndexImpl<>(OrgApiStorageImpl.ORG_SCHEME,
        SubscriptionConfigApi.SEARCH_USER_SUBSCRIPTION,
        OrgApiStorageImpl.ORG_SCHEME, ACLSubjectSubscription.class)
            .map(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_TYPE, ACLSubjectSubscription.SUBJECT,
                Subject.TYPE)
            .map(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_TYPE_NAME,
                ACLSubjectSubscription.SUBJECT,
                Subject.TYPE)
            .map(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_MODEL, ACLSubjectSubscription.SUBJECT,
                Subject.MODEL)
            .map(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_URI, ACLSubjectSubscription.SUBJECT,
                Subject.REF)
            .mapComplex(SubscriptionConfigApi.SUBSCRIPTION_SUBJECT_NAME,
                n -> subjectManagementApi.getDisplayValue(
                    n.getValueAsString(ACLSubjectSubscription.SUBJECT, Subject.MODEL),
                    Arrays.asList(n.getValue(Subject.class, ACLSubjectSubscription.SUBJECT)))
                    .get(0))
            .map(SubscriptionConfigApi.SUBSCRIPTION_OPERATION,
                ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.OPERATION)
            .map(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_NAME,
                ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.OPERATION)
            .map(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_COMMENT,
                ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.COMMENT)
            .map(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_ENTITYURI,
                ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.ENTITY_URI)
            .map(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_ENTITYSUMMARY,
                ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.ENTITY_URI)
            .map(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_CONTEXTCONFIG,
                ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.CONFIG)
            .map(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_CONTEXTCONFIG_NAME,
                ACLSubjectSubscription.OPERATION_REFERENCE,
                ACLOperationReference.CONFIG)
            .mapComplex(SubscriptionConfigApi.SUBSCRIPTION_OPERATION_REVOKE_SUPPORTED,
                Boolean.class,
                -1, n -> false)
            .postProcess((td, si) -> {
              return configApi == null ? td : configApi.postProcess(si, td);
            });
  }

}
