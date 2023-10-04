package org.smartbit4all.api.config;

import org.smartbit4all.api.binarydata.BinaryDataSorageApi;
import org.smartbit4all.api.binarydata.BinaryDataSorageApiImpl;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.CollectionApiStorageImpl;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.FilterExpressionApiImpl;
import org.smartbit4all.api.collection.StorageSequenceApi;
import org.smartbit4all.api.collection.StorageSequenceApiImpl;
import org.smartbit4all.api.collection.bean.StoredListData;
import org.smartbit4all.api.collection.bean.StoredMapData;
import org.smartbit4all.api.collection.bean.StoredReferenceData;
import org.smartbit4all.api.collection.bean.StoredSequenceData;
import org.smartbit4all.api.filter.util.FilterService;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationApiImpl;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.MasterDataManagementApiImpl;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationFeatureApi;
import org.smartbit4all.api.navigation.NavigationFeatureApiImpl;
import org.smartbit4all.api.navigation.NavigationPrimary;
import org.smartbit4all.api.navigation.ObjectNavigation;
import org.smartbit4all.api.object.AccessControlInternalApi;
import org.smartbit4all.api.object.AccessControlInternalApiImpl;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.ApplyChangeApiImpl;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.BranchApiImpl;
import org.smartbit4all.api.object.CompareApi;
import org.smartbit4all.api.object.CompareApiImpl;
import org.smartbit4all.api.object.CompareContributionApiStorageImpl;
import org.smartbit4all.api.object.CopyApi;
import org.smartbit4all.api.object.CopyApiImpl;
import org.smartbit4all.api.object.CopyContributionApiStorageImpl;
import org.smartbit4all.api.object.ModifyApi;
import org.smartbit4all.api.object.ModifyApiImpl;
import org.smartbit4all.api.object.ModifyContributionApiStorageImpl;
import org.smartbit4all.api.object.RetrievalApi;
import org.smartbit4all.api.object.RetrievalApiImpl;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.SubjectContributionApi;
import org.smartbit4all.api.org.SubjectContributionByGroup;
import org.smartbit4all.api.org.SubjectContributionByUser;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.SubjectManagementApiImpl;
import org.smartbit4all.api.rdbms.DatabaseDefinitionApi;
import org.smartbit4all.api.rdbms.DatabaseDefinitionApiImpl;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.ValueSetApiImpl;
import org.smartbit4all.api.view.SmartLinkApi;
import org.smartbit4all.api.view.SmartLinkApiImpl;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.ViewPublisherApi;
import org.smartbit4all.api.view.ViewPublisherApiImpl;
import org.smartbit4all.api.view.grid.GridModelApi;
import org.smartbit4all.api.view.grid.GridModelApiImpl;
import org.smartbit4all.api.view.layout.SmartLayoutApi;
import org.smartbit4all.api.view.layout.SmartLayoutApiImpl;
import org.smartbit4all.api.view.tree.TreeApi;
import org.smartbit4all.api.view.tree.TreeApiImpl;
import org.smartbit4all.api.view.tree.TreeSetupApi;
import org.smartbit4all.api.view.tree.TreeSetupApiImpl;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectDefinitionApiImpl;
import org.smartbit4all.core.object.ObjectDefinitionProvidedApi;
import org.smartbit4all.core.object.ObjectDefinitionProvidedApiImpl;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject.VersionPolicy;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * The smartbit4all platform api config.
 *
 * @author Peter Boros
 */
@Configuration
@Import({DomainConfig.class, PlatformApiScheduledConfig.class})
public class PlatformApiConfig {

  /**
   * This constant is usually used for the definition of the ACL subject model. It contains all the
   * grouping mechanism that helps to define the access control lists.
   */
  public static final String SUBJECT_ACL = "aclSubjectModel";

  @Bean
  public InvocationApi invocationApi() {
    return new InvocationApiImpl();
  }

  @Bean
  public LocaleSettingApi localSettingApi() {
    return new LocaleSettingApi();
  }

  @Bean
  @Primary
  public NavigationApi navigationApi() {
    return new NavigationPrimary();
  }

  @Bean
  public NavigationApi objectNavigationApi() {
    return new ObjectNavigation();
  }

  @Bean
  @Primary
  public TreeApi treeApi() {
    return new TreeApiImpl();
  }

  @Bean
  public GridModelApi gridApi() {
    return new GridModelApiImpl();
  }

  @Bean
  @Primary
  public TreeSetupApi treeSetupApi() {
    return new TreeSetupApiImpl();
  }

  @Bean
  public FilterService filtersService(EntityManager entityManager,
      TransferService transferService) {
    return new FilterService(entityManager, transferService);
  }

  @Bean
  public ViewPublisherApi viewPublisherApi() {
    return new ViewPublisherApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<ViewPublisherApi> viewPublisherApiProvider(
      ViewPublisherApi api) {
    return Invocations.asProvider(ViewPublisherApi.class, ViewPublisherApi.class.getName(), api);
  }

  @Bean
  public NavigationFeatureApi navigationFeatureApi() {
    return new NavigationFeatureApiImpl();
  }

  @Bean
  public ObjectDefinition<ApiData> apiDataObjectDefinition() {
    ObjectDefinition<ApiData> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(ApiData.class);
    result.setExplicitUri(true);
    return result;
  }

  @Bean
  public ObjectDefinition<ApiRegistryData> apiRegistryDataDefinition() {
    ObjectDefinition<ApiRegistryData> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(ApiRegistryData.class);
    result.setExplicitUri(true);
    return result;
  }

  @Bean
  public ObjectDefinition<ObjectDefinitionData> objectDefinitionData() {
    ObjectDefinition<ObjectDefinitionData> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(ObjectDefinitionData.class);
    result.setExplicitUri(true);
    return result;
  }

  @Bean
  public CopyApi copyApi() {
    return new CopyApiImpl();
  }

  @Bean
  public CopyContributionApiStorageImpl copyApiStorageImpl() {
    return new CopyContributionApiStorageImpl();
  }

  @Bean
  public CompareApi compareApi() {
    return new CompareApiImpl();
  }

  @Bean
  public CompareContributionApiStorageImpl compareApiStorageImpl() {
    return new CompareContributionApiStorageImpl();
  }

  @Bean
  public BranchApi branchApi() {
    return new BranchApiImpl();
  }

  @Bean
  public MasterDataManagementApi masterDataManagementApi() {
    return new MasterDataManagementApiImpl();
  }

  @Bean
  public ModifyApi modifyApi() {
    return new ModifyApiImpl();
  }

  @Bean
  public ModifyContributionApiStorageImpl modifyApiStorageImpl() {
    return new ModifyContributionApiStorageImpl();
  }

  @Bean
  public ApplyChangeApi applyChangeApi() {
    return new ApplyChangeApiImpl();
  }

  @Bean
  public RetrievalApi retrievalApi() {
    return new RetrievalApiImpl();
  }

  @Bean
  public CollectionApi collectionApi() {
    return new CollectionApiStorageImpl();
  }

  @Bean
  public DatabaseDefinitionApi databaseDefinitionApi() {
    return new DatabaseDefinitionApiImpl();
  }

  @Bean
  public FilterExpressionApi filterExpressionApi() {
    return new FilterExpressionApiImpl();
  }

  @Bean
  public SmartLinkApi smartLinkApi() {
    return new SmartLinkApiImpl();
  }

  @Bean
  public ValueSetApi valueSetApi() {
    return new ValueSetApiImpl();
  }

  @Bean
  public SmartLayoutApi smartLayoutApi() {
    return new SmartLayoutApiImpl();
  }

  @Bean
  public ObjectDefinitionProvidedApi objectDefinitionProvidedApi() {
    return new ObjectDefinitionProvidedApiImpl();
  }

  @Bean
  public SubjectManagementApi subjectManagementApi() {
    return new SubjectManagementApiImpl();
  }

  @Bean
  public SubjectContributionApi subjectContributionByGroup() {
    return new SubjectContributionByGroup();
  }

  @Bean
  public SubjectContributionApi subjectContributionByUser() {
    return new SubjectContributionByUser();
  }

  @Bean
  public AccessControlInternalApi accessControlInternalApi() {
    return new AccessControlInternalApiImpl();
  }

  @Bean
  public ProviderApiInvocationHandler<ObjectDefinitionProvidedApi> objectDefinitionProvidedApiProvider(
      ObjectDefinitionProvidedApi api) {
    return Invocations.asProvider(ObjectDefinitionProvidedApi.class,
        ObjectDefinitionProvidedApi.class.getName(), api);
  }

  @Bean(name = "smartbit4all.messagesource")
  public MessageSource messageResource(
      @Value("${spring.messages.basename:messages-platform}") String baseName,
      @Value("${spring.messages.encoding:UTF-8}") String defaultEncoding) {
    ResourceBundleMessageSource messageBundleResrc = new ResourceBundleMessageSource();
    messageBundleResrc.addBasenames(baseName.split(","));
    messageBundleResrc.setDefaultEncoding(defaultEncoding);
    return messageBundleResrc;
  }

  @Bean
  public ObjectDefinition<StoredMapData> storedMapEntrySingleVersion() {
    ObjectDefinition<StoredMapData> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(StoredMapData.class);
    result.setExplicitUri(true);
    return result;
  }

  @Bean
  public ObjectDefinition<StoredListData> storedListEntrySingleVersion() {
    ObjectDefinition<StoredListData> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(StoredListData.class);
    result.setExplicitUri(true);
    return result;
  }

  @Bean
  public ObjectDefinition<StoredReferenceData> storedReferenceEntrySingleVersion() {
    ObjectDefinition<StoredReferenceData> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(StoredReferenceData.class);
    result.setExplicitUri(true);
    return result;
  }

  @Bean
  public ObjectDefinition<StoredSequenceData> storedSequenceDataSingleVersion() {
    ObjectDefinition<StoredSequenceData> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(StoredSequenceData.class);
    result.setExplicitUri(true);
    return result;
  }

  @Bean
  public BinaryDataSorageApi binaryDataSorageApi() {
    return new BinaryDataSorageApiImpl();
  }

  @Bean
  public StorageSequenceApi storageSequenceApi() {
    return new StorageSequenceApiImpl();
  }

  @Bean
  public Storage viewContextServiceStorage(@Autowired(required = false) ObjectStorage objectStorage,
      ObjectDefinitionApi objectDefinitionApi) {
    Storage storage = new Storage(ViewContextService.SCHEMA, objectDefinitionApi, objectStorage);
    storage.setVersionPolicy(VersionPolicy.SINGLEVERSION);
    return storage;
  }

  @Bean
  public Storage sessionStorage(@Autowired(required = false) ObjectStorage objectStorage,
      ObjectDefinitionApi objectDefinitionApi) {
    Storage storage = new Storage(SessionManagementApi.SCHEMA, objectDefinitionApi, objectStorage);
    storage.setVersionPolicy(VersionPolicy.SINGLEVERSION);
    return storage;
  }

  @Bean
  public Storage asynInvocationStorage(@Autowired(required = false) ObjectStorage objectStorage,
      ObjectDefinitionApi objectDefinitionApi) {
    Storage storage =
        new Storage(Invocations.ASYNCINVOCATION_SCHEME, objectDefinitionApi, objectStorage);
    storage.setVersionPolicy(VersionPolicy.SINGLEVERSION);
    return storage;
  }

  @Bean
  public Storage asynChannelRegistryStorage(
      @Autowired(required = false) ObjectStorage objectStorage,
      ObjectDefinitionApi objectDefinitionApi) {
    Storage storage =
        new Storage(Invocations.ASYNC_CHANNEL_REGISTRY, objectDefinitionApi, objectStorage);
    storage.setVersionPolicy(VersionPolicy.SINGLEVERSION);
    return storage;
  }

  @Bean
  public Storage branchStorage(@Autowired(required = false) ObjectStorage objectStorage,
      ObjectDefinitionApi objectDefinitionApi) {
    Storage storage = new Storage(BranchApiImpl.SCHEME, objectDefinitionApi, objectStorage);
    storage.setVersionPolicy(VersionPolicy.SINGLEVERSION);
    return storage;
  }

  @Bean
  public ObjectReferenceConfigs invocationReferences() {
    return new ObjectReferenceConfigs().ref(AsyncInvocationRequest.class,
        AsyncInvocationRequest.AND_THEN, AsyncInvocationRequest.class, ReferencePropertyKind.LIST,
        AggregationKind.SHARED);
  }

  @Bean
  public ObjectReferenceConfigs objectReferenceConfigsPlatform() {
    return new ObjectReferenceConfigs()
        .ref(MDMDefinition.class,
            MDMDefinition.STATE,
            MDMDefinitionState.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.NONE);
  }
}
