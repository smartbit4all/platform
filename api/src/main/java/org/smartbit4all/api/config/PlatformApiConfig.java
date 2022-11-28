package org.smartbit4all.api.config;

import org.smartbit4all.api.documentation.DocumentationApi;
import org.smartbit4all.api.documentation.DocumentationApiImpl;
import org.smartbit4all.api.filter.util.FilterService;
import org.smartbit4all.api.invocation.InvocationApi;
import org.smartbit4all.api.invocation.InvocationApiImpl;
import org.smartbit4all.api.invocation.bean.ApiData;
import org.smartbit4all.api.invocation.bean.ApiRegistryData;
import org.smartbit4all.api.navigation.NavigationApi;
import org.smartbit4all.api.navigation.NavigationFeatureApi;
import org.smartbit4all.api.navigation.NavigationFeatureApiImpl;
import org.smartbit4all.api.navigation.NavigationPrimary;
import org.smartbit4all.api.navigation.ObjectNavigation;
import org.smartbit4all.api.object.ApplyChangeApi;
import org.smartbit4all.api.object.ApplyChangeApiImpl;
import org.smartbit4all.api.object.BranchApi;
import org.smartbit4all.api.object.BranchApiImpl;
import org.smartbit4all.api.object.BranchContributionApiStorageImpl;
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
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.setting.LocaleUsage;
import org.smartbit4all.api.setting.LocaleUsageImpl;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApiImpl;
import org.smartbit4all.domain.config.DomainConfig;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.service.transfer.TransferService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 * The smartbit4all platform api config.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({DomainConfig.class, PlatformApiScheduledConfig.class})
public class PlatformApiConfig {

  @Bean
  public InvocationApi invocationApi() {
    return new InvocationApiImpl();
  }

  @Bean
  public LocaleSettingApi localSettingApi() {
    return new LocaleSettingApi();
  }

  @Bean
  @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public LocaleUsage localeUsage() {
    return new LocaleUsageImpl();
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
  public FilterService filtersService(EntityManager entityManager,
      TransferService transferService) {
    return new FilterService(entityManager, transferService);
  }

  @Bean
  public NavigationFeatureApi navigationFeatureApi() {
    return new NavigationFeatureApiImpl();
  }

  @Bean
  public DocumentationApi documentationApi() {
    return new DocumentationApiImpl();
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
  public BranchContributionApiStorageImpl branchApiStorageImpl() {
    return new BranchContributionApiStorageImpl();
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

}
