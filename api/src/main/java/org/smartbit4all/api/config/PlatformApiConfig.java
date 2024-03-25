package org.smartbit4all.api.config;

import org.smartbit4all.api.binarydata.BinaryDataSorageApi;
import org.smartbit4all.api.binarydata.BinaryDataSorageApiImpl;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.CollectionApiStorageImpl;
import org.smartbit4all.api.collection.EmbeddingApi;
import org.smartbit4all.api.collection.EmbeddingApiImpl;
import org.smartbit4all.api.collection.FilterExpressionApi;
import org.smartbit4all.api.collection.FilterExpressionApiImpl;
import org.smartbit4all.api.collection.StorageSequenceApi;
import org.smartbit4all.api.collection.StorageSequenceApiImpl;
import org.smartbit4all.api.collection.VectorDBApi;
import org.smartbit4all.api.collection.VectorDDBApiImpl;
import org.smartbit4all.api.collection.bean.StoredListData;
import org.smartbit4all.api.collection.bean.StoredMapData;
import org.smartbit4all.api.collection.bean.StoredReferenceData;
import org.smartbit4all.api.collection.bean.StoredSequenceData;
import org.smartbit4all.api.filter.util.FilterService;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationApiImpl;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.invocation.ServiceConnectionApi;
import org.smartbit4all.api.invocation.ServiceConnectionApiImpl;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.invocation.bean.AsyncInvocationRequest;
import org.smartbit4all.api.invocation.bean.ServiceConnection;
import org.smartbit4all.api.mdm.MDMConstants;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.MasterDataManagementApiImpl;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMDefinitionState;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.mimetype.ContentConversionApi;
import org.smartbit4all.api.mimetype.config.MimeTypeConfig;
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
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.ObjectValidationOperation;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.SecurityOption;
import org.smartbit4all.api.org.SubjectContributionApi;
import org.smartbit4all.api.org.SubjectContributionByGroup;
import org.smartbit4all.api.org.SubjectContributionByUser;
import org.smartbit4all.api.org.SubjectManagementApi;
import org.smartbit4all.api.org.SubjectManagementApiImpl;
import org.smartbit4all.api.rdbms.DatabaseDefinitionApi;
import org.smartbit4all.api.rdbms.DatabaseDefinitionApiImpl;
import org.smartbit4all.api.session.SessionManagementApi;
import org.smartbit4all.api.setting.ApplicationInfo;
import org.smartbit4all.api.setting.ImageSettingApi;
import org.smartbit4all.api.setting.ImageSettingApiImpl;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.toolbar.bean.ActionDefinition;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.ValueSetApiImpl;
import org.smartbit4all.api.view.ActionDefinitionApi;
import org.smartbit4all.api.view.ActionDefinitionApiImpl;
import org.smartbit4all.api.view.ActionManagementApi;
import org.smartbit4all.api.view.ActionManagementApiImpl;
import org.smartbit4all.api.view.SmartLinkApi;
import org.smartbit4all.api.view.SmartLinkApiImpl;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.ViewPublisherApi;
import org.smartbit4all.api.view.ViewPublisherApiImpl;
import org.smartbit4all.api.view.bean.UiAction;
import org.smartbit4all.api.view.constraint.ViewConstraintManagementApi;
import org.smartbit4all.api.view.constraint.ViewConstraintManagementApiImpl;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApi;
import org.smartbit4all.api.view.filterexpression.FilterExpressionBuilderApiImpl;
import org.smartbit4all.api.view.filterexpression.FilterExpressionFieldUiConverter;
import org.smartbit4all.api.view.filterexpression.FilterExpressionFieldUiConverterImpl;
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
@Import({DomainConfig.class, PlatformApiScheduledConfig.class, MimeTypeConfig.class})
public class PlatformApiConfig {

  public static final String EMBEDDING_CONNECTIONS = "embeddingConnections";

  public static final String VECTOR_DB_CONNECTIONS = "vectorDbConnections";

  public static final String TICKETING_CONNECTIONS = "ticketingConnections";

  public static final String OBJECT_VALIDATION_OPERATIONS = "objectValidationOperations";

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
  public ImageSettingApi imageSettingApi() {
    return new ImageSettingApiImpl();
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
  public ProviderApiInvocationHandler<TreeApi> treeApiProvider(
      TreeApi api) {
    return Invocations.asProvider(TreeApi.class, api);
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
  MDMDefinitionOption systemIntegrationPlatformMdmOption(LocaleSettingApi localeSettingApi) {
    MDMDefinition mdmDefinition =
        new MDMDefinition().name(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION)
            .adminGroupName(PlatformSecurityOption.admin.getName());
    MDMDefinitionOption result =
        new MDMDefinitionOption(mdmDefinition);
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(EMBEDDING_CONNECTIONS)
          .name(EMBEDDING_CONNECTIONS)
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ServiceConnection.NAME))
          .editorViewName(MDMConstants.MDM_EDIT)
          .displayNameList(new LangString().defaultValue("Embedding connections")
              .putValueByLocaleItem("hu", "Beágyazó kapcsolatok")
              .putValueByLocaleItem("en", "Embedding connections"))
          .displayNameForm(new LangString().defaultValue("Embedding connection")
              .putValueByLocaleItem("hu", "Beágyazó kapcsolat")
              .putValueByLocaleItem("en", "Embedding connection"))
          .order(200l)
          .typeQualifiedName(ServiceConnection.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Name")
                  .addPathItem(ServiceConnection.NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API name")
                  .addPathItem(ServiceConnection.API_NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Endpoint")
                  .addPathItem(ServiceConnection.ENDPOINT))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API version")
                  .addPathItem(ServiceConnection.API_VERSION))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Authentication token")
                  .addPathItem(ServiceConnection.AUTH_TOKEN));
      result.addDescriptor(entry);
    }
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(VECTOR_DB_CONNECTIONS)
          .name(VECTOR_DB_CONNECTIONS)
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ServiceConnection.NAME))
          .editorViewName(MDMConstants.MDM_EDIT)
          .displayNameList(new LangString().defaultValue("Vector database connections")
              .putValueByLocaleItem("hu", "Vektor adatbázis kapcsolatok")
              .putValueByLocaleItem("en", "Vector database connections"))
          .displayNameForm(new LangString().defaultValue("Vector database connection")
              .putValueByLocaleItem("hu", "Vektor adatbázis kapcsolat")
              .putValueByLocaleItem("en", "Vector database connection"))
          .order(200l)
          .typeQualifiedName(ServiceConnection.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Name")
                  .addPathItem(ServiceConnection.NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API name")
                  .addPathItem(ServiceConnection.API_NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Endpoint")
                  .addPathItem(ServiceConnection.ENDPOINT))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API version")
                  .addPathItem(ServiceConnection.API_VERSION))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Authentication token")
                  .addPathItem(ServiceConnection.AUTH_TOKEN));
      result.addDescriptor(entry);
    }
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(TICKETING_CONNECTIONS)
          .name(TICKETING_CONNECTIONS)
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ServiceConnection.NAME))
          .editorViewName(MDMConstants.MDM_EDIT)
          .displayNameList(new LangString().defaultValue("Ticketing service connections")
              .putValueByLocaleItem("hu", "Hibajegykezelő alkalmazás kapcsolatok")
              .putValueByLocaleItem("en", "Ticketing service connections"))
          .displayNameForm(new LangString().defaultValue("Ticketing service connection")
              .putValueByLocaleItem("hu", "Hibajegykezelő alkalmazás kapcsolat")
              .putValueByLocaleItem("en", "Ticketing service connection"))
          .order(200l)
          .typeQualifiedName(ServiceConnection.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Name")
                  .addPathItem(ServiceConnection.NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API name")
                  .addPathItem(ServiceConnection.API_NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Endpoint")
                  .addPathItem(ServiceConnection.ENDPOINT))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API version")
                  .addPathItem(ServiceConnection.API_VERSION))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Authentication token")
                  .addPathItem(ServiceConnection.AUTH_TOKEN));
      result.addDescriptor(entry);
    }
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(OBJECT_VALIDATION_OPERATIONS)
          .name(OBJECT_VALIDATION_OPERATIONS)
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ObjectValidationOperation.CODE))
          .editorViewName(MDMConstants.MDM_EDIT)
          .displayNameList(new LangString().defaultValue("Object Validation Operations")
              .putValueByLocaleItem("hu", "Objektumvalidációs műveletek")
              .putValueByLocaleItem("en", "Object Validation Operations"))
          .displayNameForm(new LangString().defaultValue("Object Validation Operation")
              .putValueByLocaleItem("hu", "Objektumvalidációs művelet")
              .putValueByLocaleItem("en", "Object Validation Operation"))
          .order(200L)
          .typeQualifiedName(ObjectValidationOperation.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Code")
                  .addPathItem(ObjectValidationOperation.CODE))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Name")
                  .addPathItem(ObjectValidationOperation.NAME)
                  .addPathItem(LangString.DEFAULT_VALUE));
      result.addDescriptor(entry);
    }
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(ActionDefinitionApi.MDM_ACTION_DEFINITIONS)
          .name(ActionDefinitionApi.MDM_ACTION_DEFINITIONS)
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ActionDefinition.QUALIFIED_NAME))
          .editorViewName(MDMConstants.MDM_EDIT)
          .displayNameList(new LangString().defaultValue("Action Definitions")
              .putValueByLocaleItem("hu", "Műveletleírók")
              .putValueByLocaleItem("en", "Action Definitions"))
          .displayNameForm(new LangString().defaultValue("Action Definition")
              .putValueByLocaleItem("hu", "Műveletleíró")
              .putValueByLocaleItem("en", "Action Definition"))
          .order(200L)
          .typeQualifiedName(ActionDefinition.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Qualified name")
                  .addPathItem(ActionDefinition.QUALIFIED_NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("ActionCode")
                  .addPathItem(ActionDefinition.ACTION)
                  .addPathItem(UiAction.CODE));
      result.addDescriptor(entry);
    }
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(ServiceConnectionApi.SERVICE_CONNECTIONS)
          .name(ServiceConnectionApi.SERVICE_CONNECTIONS)
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ServiceConnection.NAME))
          .editorViewName(MDMConstants.MDM_EDIT)
          .displayNameList(new LangString().defaultValue("Service Connections")
              .putValueByLocaleItem("hu", "Kiszolgáló kapcsolatok")
              .putValueByLocaleItem("en", "Service Connections"))
          .displayNameForm(new LangString().defaultValue("Service Connection")
              .putValueByLocaleItem("hu", "Kiszolgáló kapcsolat")
              .putValueByLocaleItem("en", "Service Connection"))
          .order(200L)
          .typeQualifiedName(ServiceConnection.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Name")
                  .addPathItem(ServiceConnection.NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API name")
                  .addPathItem(ServiceConnection.API_NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Endpoint")
                  .addPathItem(ServiceConnection.ENDPOINT))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API version")
                  .addPathItem(ServiceConnection.API_VERSION))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Authentication token")
                  .addPathItem(ServiceConnection.AUTH_TOKEN));
      result.addDescriptor(entry);
    }
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(ContentConversionApi.MDM_CONVERSION_SERVICES)
          .name(ContentConversionApi.MDM_CONVERSION_SERVICES)
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ServiceConnection.NAME))
          .editorViewName(MDMConstants.MDM_EDIT)
          .displayNameList(new LangString().defaultValue("Conversion Services")
              .putValueByLocaleItem("hu", "Konverzió kiszolgálók")
              .putValueByLocaleItem("en", "Conversion Services"))
          .displayNameForm(new LangString().defaultValue("Service Connection")
              .putValueByLocaleItem("hu", "Konverzió kiszolgáló")
              .putValueByLocaleItem("en", "Conversion Service"))
          .order(200L)
          .typeQualifiedName(ServiceConnection.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Name")
                  .addPathItem(ServiceConnection.NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API name")
                  .addPathItem(ServiceConnection.API_NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Endpoint")
                  .addPathItem(ServiceConnection.ENDPOINT))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("API version")
                  .addPathItem(ServiceConnection.API_VERSION))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Authentication token")
                  .addPathItem(ServiceConnection.AUTH_TOKEN));
      result.addDescriptor(entry);
    }
    return result;
  }

  @Bean
  public ProviderApiInvocationHandler<MasterDataManagementApi> masterDataManagementApiProvider(
      MasterDataManagementApi api) {
    return Invocations.asProvider(MasterDataManagementApi.class, api);
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

  @Bean(name = "smartbit4all.imageresource")
  public MessageSource imagesResource(
      @Value("${spring.images.basename:images-platform}") String baseName,
      @Value("${spring.images.encoding:UTF-8}") String defaultEncoding) {
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
  public VectorDBApi vectorDbManagementApi() {
    return new VectorDDBApiImpl();
  }

  @Bean
  public EmbeddingApi embeddingManagementApi() {
    return new EmbeddingApiImpl();
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

  @Bean
  public ApplicationInfo ApplicationInfo() {
    return new ApplicationInfo();
  }

  @Bean
  public FilterExpressionBuilderApi filterExpressionBuilderApi() {
    return new FilterExpressionBuilderApiImpl();
  }

  @Bean
  public FilterExpressionFieldUiConverter filterExpressionFieldUiConverter() {
    return new FilterExpressionFieldUiConverterImpl();
  }

  @Bean
  public ActionManagementApi documentActionManagementApi() {
    return new ActionManagementApiImpl();
  }

  @Bean
  public ViewConstraintManagementApi viewConstraintManagementApi() {
    return new ViewConstraintManagementApiImpl();
  }

  @Bean
  public ActionDefinitionApi actionDefinitionApi() {
    return new ActionDefinitionApiImpl();
  }

  @Bean
  public SecurityOption platformSecurityOption() {
    return new PlatformSecurityOption();
  }

  @Bean
  ServiceConnectionApi serviceConnectionApi() {
    return new ServiceConnectionApiImpl();
  }

}
