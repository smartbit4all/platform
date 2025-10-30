package org.smartbit4all.bff.api.config;

import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.api.value.bean.ValueTransformationMappingItem;
import org.smartbit4all.bff.api.acl.AclEditingPageApi;
import org.smartbit4all.bff.api.acl.AclEditingPageApiImpl;
import org.smartbit4all.bff.api.acl.NoPermissionPageApi;
import org.smartbit4all.bff.api.acl.NoPermissionPageApiImpl;
import org.smartbit4all.bff.api.acl.SubjectSelectorPageApi;
import org.smartbit4all.bff.api.acl.SubjectSelectorPageApiImpl;
import org.smartbit4all.bff.api.acl.UserSelectorPageApi;
import org.smartbit4all.bff.api.acl.UserSelectorPageApiImpl;
import org.smartbit4all.bff.api.assoc.AssociationGridApi;
import org.smartbit4all.bff.api.assoc.AssociationGridApiImpl;
import org.smartbit4all.bff.api.attachment.AttachmentListPageApi;
import org.smartbit4all.bff.api.attachmentgrid.AttachmentGridApi;
import org.smartbit4all.bff.api.attachmentgrid.AttachmentGridApiImpl;
import org.smartbit4all.bff.api.attachmentgrid.AttachmentGridInvocationApi;
import org.smartbit4all.bff.api.attachmentgrid.AttachmentGridInvocationApiImpl;
import org.smartbit4all.bff.api.generic.GenericPageApi;
import org.smartbit4all.bff.api.generic.GenericPageApiImpl;
import org.smartbit4all.bff.api.generic.InvalidSmartLinkPageApi;
import org.smartbit4all.bff.api.generic.InvalidSmartLinkPageApiImpl;
import org.smartbit4all.bff.api.mdm.MDMEntryListPageApi;
import org.smartbit4all.bff.api.mdm.apikey.ApiKeyEditorPageApi;
import org.smartbit4all.bff.api.mdm.apikey.ApiKeyEditorPageApiImpl;
import org.smartbit4all.bff.api.mdm.archiveprocess.StorageArchiceProcessEditorPageApi;
import org.smartbit4all.bff.api.mdm.archiveprocess.StorageArchiceProcessEditorPageApiImpl;
import org.smartbit4all.bff.api.mdm.invocation.JobDefinitionEditorPageApi;
import org.smartbit4all.bff.api.mdm.invocation.JobDefinitionEditorPageApiImpl;
import org.smartbit4all.bff.api.mdm.invocation.MethodTemplateEditorPageApi;
import org.smartbit4all.bff.api.mdm.invocation.MethodTemplateEditorPageApiImpl;
import org.smartbit4all.bff.api.mdm.invocation.ScheduledJobDefinitionEditorPageApi;
import org.smartbit4all.bff.api.mdm.invocation.ScheduledJobDefinitionEditorPageApiImpl;
import org.smartbit4all.bff.api.mdm.oauth.DynamicOAuthPropertiesEditorPageApi;
import org.smartbit4all.bff.api.mdm.oauth.DynamicOAuthPropertiesEditorPageApiImpl;
import org.smartbit4all.bff.api.mdm.relation.MDMRelationEditorService;
import org.smartbit4all.bff.api.mdm.relation.MDMRelationEditorServiceImpl;
import org.smartbit4all.bff.api.mdm.relation.RelationManagedMultiComboBoxService;
import org.smartbit4all.bff.api.mdm.relation.RelationManagedMultiComboBoxServiceImpl;
import org.smartbit4all.bff.api.mdm.usersecurity.UserSecurityPolicyEditorPageApi;
import org.smartbit4all.bff.api.mdm.usersecurity.UserSecurityPolicyEditorPageApiImpl;
import org.smartbit4all.bff.api.mdm.valuemapping.MDMValueTransformationEditorPageApi;
import org.smartbit4all.bff.api.mdm.valuemapping.MDMValueTransformationEditorPageApiImpl;
import org.smartbit4all.bff.api.object.ObjectDescriptorEditorPageApi;
import org.smartbit4all.bff.api.object.ObjectDescriptorEditorPageApiImpl;
import org.smartbit4all.bff.api.object.ObjectPropertyDescriptorPageApi;
import org.smartbit4all.bff.api.object.ObjectPropertyDescriptorPageApiImpl;
import org.smartbit4all.bff.api.search.GenericSearchPageApi;
import org.smartbit4all.bff.api.search.GenericSearchPageApiImpl;
import org.smartbit4all.bff.api.serviceconnection.ServiceConnectionEditorPageApi;
import org.smartbit4all.bff.api.serviceconnection.ServiceConnectionEditorPageApiImpl;
import org.smartbit4all.bff.api.utils.BffUtilsApi;
import org.smartbit4all.bff.api.utils.BffUtilsApiImpl;
import org.smartbit4all.bff.api.validation.ValidationResultPageApi;
import org.smartbit4all.bff.api.validation.ValidationResultPageApiImpl;
import org.smartbit4all.sec.apikey.ApiKeyInnerApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlatformBffApiConfig {

  @Bean
  BffUtilsApi bffUtilsAPi() {
    return new BffUtilsApiImpl();
  }

  @Bean
  public SearchIndex<User> userSearch() {
    return new SearchIndexImpl<>(OrgApiStorageImpl.ORG_SCHEME, User.class.getSimpleName(),
        OrgApiStorageImpl.ORG_SCHEME, User.class)
            .map(User.URI, User.URI)
            .map(User.NAME, User.NAME)
            .map(User.USERNAME, User.USERNAME)
            .map(User.EMAIL, User.EMAIL)
            .map(User.PASSWORD, User.PASSWORD)
            .map(User.INACTIVE, User.INACTIVE)
            .map(User.ATTRIBUTES, User.ATTRIBUTES);
  }

  @Bean
  public SearchIndex<Group> groupSearch() {
    return new SearchIndexImpl<>(OrgApiStorageImpl.ORG_SCHEME, Group.class.getSimpleName(),
        OrgApiStorageImpl.ORG_SCHEME, Group.class)
            .map(Group.TITLE, Group.TITLE)
            .map(Group.NAME, Group.NAME)
            .map(Group.DESCRIPTION, Group.DESCRIPTION)
            .map(Group.URI, Group.URI)
            .map(Group.KIND_CODE, Group.KIND_CODE)
            .map(Group.BUILT_IN, Group.BUILT_IN)
            .map(Group.CHILDREN, Group.CHILDREN);
  }

  @Bean
  public SearchIndex<ValueTransformationMappingItem> valueTransformationSearchIndex() {
    return new SearchIndexImpl<>(MDMValueTransformationEditorPageApi.SCHEMA,
        ValueTransformationMappingItem.class.getSimpleName(),
        MDMValueTransformationEditorPageApi.SCHEMA, ValueTransformationMappingItem.class)
            .map(ValueTransformationMappingItem.SOURCE_VALUE, String.class,
                ValueTransformationMappingItem.SOURCE_VALUE)
            .map(ValueTransformationMappingItem.TARGET_VALUE, String.class,
                ValueTransformationMappingItem.TARGET_VALUE);
  }

  @Bean
  AssociationGridApi associationGridApi() {
    return new AssociationGridApiImpl();
  }

  @Bean
  ProviderApiInvocationHandler<AssociationGridApi> associationGridApiProvider() {
    return Invocations.asProvider(AssociationGridApi.class, associationGridApi());
  }

  @Bean("genericPageApi")
  GenericPageApi genericPageApiImpl() {
    return new GenericPageApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<GenericPageApi> genericPageApiProvider(
      @Qualifier("genericPageApi") GenericPageApi genericPageApi) {
    return Invocations.asProvider(GenericPageApi.class, genericPageApi);
  }

  @Bean
  GenericSearchPageApi genericSearchPageApi() {
    return new GenericSearchPageApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<GenericSearchPageApi> genericSearchPageApiProvider(
      GenericSearchPageApi genericSearchPageApi) {
    return Invocations.asProvider(GenericSearchPageApi.class, genericSearchPageApi);
  }

  @Bean
  public SubjectSelectorPageApi subjectSelectorPageApi() {
    return new SubjectSelectorPageApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<SubjectSelectorPageApi> subjectSelectorPageApiProvider(
      SubjectSelectorPageApi api) {
    return Invocations.asProvider(SubjectSelectorPageApi.class, api);
  }

  @Bean
  public UserSelectorPageApi userSelectorPageApi() {
    return new UserSelectorPageApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<UserSelectorPageApi> userSelectorPageApiProvider(
      UserSelectorPageApi api) {
    return Invocations.asProvider(UserSelectorPageApi.class, api);
  }

  @Bean
  public AclEditingPageApi aclEditingPageApi() {
    return new AclEditingPageApiImpl();
  }

  @Bean
  public NoPermissionPageApi noPermissionPageApi() {
    return new NoPermissionPageApiImpl();
  }

  @Bean
  public InvalidSmartLinkPageApi invalidSmartLinkPageApi() {
    return new InvalidSmartLinkPageApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<AclEditingPageApi> aclEditingPageApiProvider(
      AclEditingPageApi api) {
    return Invocations.asProvider(AclEditingPageApi.class, api);
  }

  @Bean
  public ValidationResultPageApi validationResultPageApi() {
    return new ValidationResultPageApiImpl();
  }

  @Bean
  public SearchIndex<BinaryContentData> binaryContentDataSearchIndex() {
    return new SearchIndexImpl<>(AttachmentListPageApi.SCHEMA,
        BinaryContentData.class.getSimpleName(),
        AttachmentListPageApi.SCHEMA, BinaryContentData.class)
            .map(BinaryContentData.DATA_URI, BinaryContentData.DATA_URI)
            .map(BinaryContentData.FILE_NAME, BinaryContentData.FILE_NAME)
            .map(BinaryContentData.EXTENSION, BinaryContentData.EXTENSION)
            .map(BinaryContentData.MIME_TYPE, BinaryContentData.MIME_TYPE)
            .map(BinaryContentData.CREATED, BinaryContentData.CREATED, UserActivityLog.TIMESTAMP)
            .map(BinaryContentData.UPDATED, BinaryContentData.UPDATED, UserActivityLog.TIMESTAMP)
            .map(BinaryContentData.SIZE, BinaryContentData.SIZE)
            .map(BinaryContentData.CONTENT_HASH, BinaryContentData.CONTENT_HASH);
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  ServiceConnectionEditorPageApi serviceConnectionEditorPageApi() {
    return new ServiceConnectionEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(ServiceConnectionEditorPageApi.class)
  public ProviderApiInvocationHandler<ServiceConnectionEditorPageApi> serviceConnectionEditorPageApiProvider(
      ServiceConnectionEditorPageApi api) {
    return Invocations.asProvider(ServiceConnectionEditorPageApi.class, api);
  }

  @Bean
  @ConditionalOnBean(value = {MDMEntryListPageApi.class, ApiKeyInnerApi.class})
  ApiKeyEditorPageApi apiKeyEditorPageApi() {
    return new ApiKeyEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(ApiKeyEditorPageApi.class)
  public ProviderApiInvocationHandler<ApiKeyEditorPageApi> apiKeyEditorPageApiProvider(
      ApiKeyEditorPageApi api) {
    return Invocations.asProvider(ApiKeyEditorPageApi.class, api);
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  JobDefinitionEditorPageApi jobDefinitionEditPageApi() {
    return new JobDefinitionEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  ScheduledJobDefinitionEditorPageApi scheduledJobDefinitionEditPageApi() {
    return new ScheduledJobDefinitionEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  MethodTemplateEditorPageApi methodTemplateEditorPageApi() {
    return new MethodTemplateEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  DynamicOAuthPropertiesEditorPageApi dynamicOAuthPropertiesEditorPageApi() {
    return new DynamicOAuthPropertiesEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(DynamicOAuthPropertiesEditorPageApi.class)
  public ProviderApiInvocationHandler<DynamicOAuthPropertiesEditorPageApi> dynamicOAuthPropertiesEditorPageApiProvider(
      DynamicOAuthPropertiesEditorPageApi api) {
    return Invocations.asProvider(DynamicOAuthPropertiesEditorPageApi.class, api);
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  StorageArchiceProcessEditorPageApi storageArchiceProcessEditorPageApi() {
    return new StorageArchiceProcessEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(StorageArchiceProcessEditorPageApi.class)
  public ProviderApiInvocationHandler<StorageArchiceProcessEditorPageApi> storageArchiceProcessEditorPageApiProvider(
      StorageArchiceProcessEditorPageApi api) {
    return Invocations.asProvider(StorageArchiceProcessEditorPageApi.class, api);
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  MDMValueTransformationEditorPageApi valueTransformationEditorPageApi() {
    return new MDMValueTransformationEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(MDMValueTransformationEditorPageApi.class)
  public ProviderApiInvocationHandler<MDMValueTransformationEditorPageApi> mdmValueTransformationEditorPageApiProvider(
      MDMValueTransformationEditorPageApi api) {
    return Invocations.asProvider(MDMValueTransformationEditorPageApi.class, api);
  }

  @Bean
  UserSecurityPolicyEditorPageApi userSecurityPolicyEditorPageApi() {
    return new UserSecurityPolicyEditorPageApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<UserSecurityPolicyEditorPageApi> userSecurityPolicyEditorPageApiProvider(
      UserSecurityPolicyEditorPageApi api) {
    return Invocations.asProvider(UserSecurityPolicyEditorPageApi.class, api);
  }

  @Bean
  @ConditionalOnBean(MDMEntryListPageApi.class)
  ObjectDescriptorEditorPageApi objectDescriptorEditorPageApi() {
    return new ObjectDescriptorEditorPageApiImpl();
  }

  @Bean
  @ConditionalOnBean(ObjectDescriptorEditorPageApi.class)
  public ProviderApiInvocationHandler<ObjectDescriptorEditorPageApi> objectDescriptorEditorPageApiProvider(
      ObjectDescriptorEditorPageApi api) {
    return Invocations.asProvider(ObjectDescriptorEditorPageApi.class, api);
  }

  @Bean
  @ConditionalOnBean(ObjectDescriptorEditorPageApi.class)
  ObjectPropertyDescriptorPageApi objectPropertyDescriptorPageApi() {
    return new ObjectPropertyDescriptorPageApiImpl();
  }

  @Bean
  AttachmentGridApi attachmentGridApi() {
    return new AttachmentGridApiImpl();
  }

  @Bean
  AttachmentGridInvocationApi attachmentGridInvocationApi() {
    return new AttachmentGridInvocationApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<AttachmentGridInvocationApi> attachmentGridInvocationApiProvider(
      AttachmentGridInvocationApi api) {
    return Invocations.asProvider(AttachmentGridInvocationApi.class, api);
  }

  @Bean
  MDMRelationEditorService mdmRelationEditorService() {
    return new MDMRelationEditorServiceImpl();
  }

  @Bean
  RelationManagedMultiComboBoxService relationManagedMultiComboBoxService() {
    return new RelationManagedMultiComboBoxServiceImpl();
  }

}
