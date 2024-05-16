package org.smartbit4all.api.config;

import static org.smartbit4all.core.utility.StringConstant.joinCamel;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMModification;
import org.smartbit4all.api.mdm.bean.MDMModificationState;
import org.smartbit4all.api.object.SubscriptionConfigApi;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.bean.ACLOperationReference;
import org.smartbit4all.api.org.bean.ACLSubjectSubscription;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformSearchIndexConfig {

  public static final String MODIFICATION_SENT_TO_APPROVAL_AT =
      MDMModification.SENT_TO_APPROVAL + StringConstant.DOT + UserActivityLog.TIMESTAMP;
  public static final String MODIFICATION_APPROVED_AT =
      MDMModification.APPROVED + StringConstant.DOT + UserActivityLog.TIMESTAMP;
  public static final String MODIFICATION_CREATED_AT =
      MDMModification.CREATED + StringConstant.DOT + UserActivityLog.TIMESTAMP;

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


  public static final String STATE_NAME = joinCamel(MDMModification.STATE, GenericValue.NAME);

  @Bean
  public SearchIndex<MDMModification> searchMDMModification(
      OrgApi orgApi,
      MasterDataManagementApi masterDataManagementApi,
      LocaleSettingApi localeSettingApi,
      @Autowired(required = false) SessionApi sessionApi) {
    return new SearchIndexImpl<>(PlatformApiConfig.DEFAULT_SCHEME,
        MDMModification.class.getSimpleName(),
        MasterDataManagementApi.SCHEMA,
        MDMModification.class)
            .map(MDMModification.ID, MDMModification.ID)
            .map(MDMModification.NAME, MDMModification.NAME)
            .map(MDMModification.DESCRIPTION, MDMModification.DESCRIPTION)
            .map(MDMModification.CREATED, UserActivityLog.class, MDMModification.CREATED)
            .map(MODIFICATION_CREATED_AT, OffsetDateTime.class, MDMModification.CREATED,
                UserActivityLog.TIMESTAMP)
            .map(MDMModification.APPROVED, UserActivityLog.class, MDMModification.APPROVED)
            .map(MODIFICATION_APPROVED_AT, OffsetDateTime.class, MDMModification.APPROVED,
                UserActivityLog.TIMESTAMP)
            .map(MDMModification.SENT_TO_APPROVAL, UserActivityLog.class,
                MDMModification.SENT_TO_APPROVAL)
            .map(MODIFICATION_SENT_TO_APPROVAL_AT, OffsetDateTime.class,
                MDMModification.SENT_TO_APPROVAL, UserActivityLog.TIMESTAMP)
            .map(MDMModification.BRANCH_URI, URI.class, MDMModification.BRANCH_URI)
            .mapComplex(MDMModification.APPROVER, String.class, 500,
                modificationNode -> {
                  if (modificationNode == null) {
                    return StringConstant.EMPTY;
                  }
                  URI approverUri = modificationNode.getValue(URI.class, MDMModification.APPROVER);
                  if (approverUri == null) {
                    return StringConstant.EMPTY;
                  }
                  User user = orgApi.getUser(approverUri);
                  return user == null ? "N/A" : user.getName();
                })
            .map(MDMModification.STATE, MDMModificationState.class, MDMModification.STATE)
            .map(STATE_NAME, String.class, MDMModification.STATE)
            .postProcess((tableData, searchIndex) -> {
              EntityDefinition definition = searchIndex.getDefinition().getDefinition();
              Property<MDMModificationState> stateProp =
                  (Property<MDMModificationState>) definition.getProperty(MDMModification.STATE);
              Property<String> stateNameProp =
                  (Property<String>) definition.getProperty(STATE_NAME);
              tableData.rows().forEach(row -> {
                row.setObject(stateNameProp,
                    localeSettingApi.get(row.get(stateProp)));
              });
              return tableData;
            })
    // .mapComplex("isActive", Boolean.class, -1, n -> {
    // if (sessionApi == null) {
    // return false;
    // }
    // MDMModificationApi modificationApi = masterDataManagementApi.getModificationApi(null,
    // n.getValueAsString(MDMModification.ID));
    // // modificationApi.
    // return true;
    // })


    ;
  }
}
