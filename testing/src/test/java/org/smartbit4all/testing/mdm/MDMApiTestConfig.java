package org.smartbit4all.testing.mdm;

import java.util.Arrays;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.invocation.Invocations;
import org.smartbit4all.api.invocation.ProviderApiInvocationHandler;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleAttachement;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.ViewApiImpl;
import org.smartbit4all.api.view.ViewContextService;
import org.smartbit4all.api.view.ViewContextServiceImpl;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;
import org.smartbit4all.bff.api.mdm.MDMEntryListPageApi;
import org.smartbit4all.bff.api.search.SearchPageApi;
import org.smartbit4all.bff.api.search.SearchPageApiImplTest;
import org.smartbit4all.core.io.TestFSCleaner;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.sec.config.SecurityLocalTestConfig;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.smartbit4all.testing.UITestApi;
import org.smartbit4all.testing.UITestApiImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class,
    TestFSConfig.class, SecurityLocalTestConfig.class})
@EnableTransactionManagement
public class MDMApiTestConfig extends TestFSCleaner {


  static final String TEST = "Test";
  static final String SAMPLE = "sample";

  public static final String MDM_EDITING_PAGE = "MDMEditingPage";
  public static final String MDM_LIST_PAGE = "MDMListPage";
  public static final String MDM_MAIN_PAGE = "MDMMainPage";
  public static final String SEARCHINDEX_LIST_PAGE = "SearchIndexListPage";
  public static final String SI_SAMPLECATEGORY = "SampleCategorySearch";

  @Bean
  MDMDefinitionOption testMDMOption() {
    MDMDefinitionOption result =
        new MDMDefinitionOption(new MDMDefinition().name(TEST)
            .adminGroupName("org.smartbit4all.testing.mdm.MDMSecurityOptions.admin"));
    result.addDefaultDescriptor(SampleCategoryType.class)
        .uniqueIdentifierPath(Arrays.asList(SampleCategoryType.CODE))
        .editorViewName(MDM_EDITING_PAGE);
    result.addDefaultDescriptor(SampleCategory.class)
        .uniqueIdentifierPath(Arrays.asList(SampleCategory.NAME)).editorViewName(MDM_EDITING_PAGE);
    result.addDefaultDescriptor(PropertyDefinitionData.class);
    result.addObjectDefinitionData();
    return result;
  }

  @Bean
  MDMSecurityOptions mdmSecurityOptions() {
    return new MDMSecurityOptions();
  }

  public static final String SHADOW_ITEMS = "shadowItems";

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }

  @Bean
  public UITestApi uiTestApi() {
    return new UITestApiImpl();
  }

  @Bean
  MDMEntryListPageApi mdmEntryListPageApiImplTest() {
    return new MDMEntryListPageApiImplTest();
  }

  @Bean
  ProviderApiInvocationHandler<MDMEntryListPageApi> mdmEntryListPageApiImplTestProvider(
      MDMEntryListPageApi api) {
    return Invocations.asProvider(MDMEntryListPageApi.class, api);
  }

  @Bean
  MDMEntryEditPageApi mdmEntryEditPageApi() {
    return new MEMEntryEditingPageApiImplTest();
  }

  @Bean
  SearchPageApi searchIndexResultPageApiImplTest() {
    return new SearchPageApiImplTest();
  }

  @Bean
  SearchIndex<SampleCategory> searchIndexSampleCategory() {
    return new SearchIndexImpl<>(TEST, SI_SAMPLECATEGORY, TEST, SampleCategory.class)
        .map(SampleCategory.NAME, SampleCategory.NAME)
        .map(SampleCategory.COLOR, SampleCategory.COLOR)
        .map(SampleCategory.URI, SampleCategory.URI);
  }

  @Bean
  @ConditionalOnMissingBean
  public ViewApi viewApi() {
    return new ViewApiImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public ViewContextService viewContextService() {
    return new ViewContextServiceImpl();
  }

  @Bean
  public ObjectReferenceConfigs refDefs() {
    return new ObjectReferenceConfigs()
        .ref(SampleCategory.class,
            SampleCategory.SUB_CATEGORIES,
            SampleCategory.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SampleCategory.CONTAINER_ITEMS,
            SampleContainerItem.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SHADOW_ITEMS,
            SampleContainerItem.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SampleCategory.LINKS,
            SampleLinkObject.class,
            ReferencePropertyKind.LIST,
            AggregationKind.INLINE)
        .ref(SampleLinkObject.class,
            SampleLinkObject.CATEGORY,
            SampleCategory.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleLinkObject.class,
            SampleLinkObject.ITEM,
            SampleContainerItem.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.USER_URI,
            User.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.ATTACHMENTS,
            SampleAttachement.class,
            ReferencePropertyKind.LIST,
            AggregationKind.COMPOSITE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.MAIN_DOCUMENT,
            SampleAttachement.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.COMPOSITE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.DATASHEET,
            SampleDataSheet.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.COMPOSITE)
        .ref(SampleAttachement.class,
            SampleAttachement.CONTENT,
            BinaryContent.class,
            ReferencePropertyKind.REFERENCE);
  }


}
