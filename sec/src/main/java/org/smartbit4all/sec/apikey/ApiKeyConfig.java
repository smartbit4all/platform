package org.smartbit4all.sec.apikey;

import java.util.stream.Collectors;
import org.smartbit4all.api.collection.SearchIndex;
import org.smartbit4all.api.collection.SearchIndexImpl;
import org.smartbit4all.api.config.PlatformSecurityOption;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MDMEntryApi;
import org.smartbit4all.api.mdm.MDMSearchIndexApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMBranchingStrategy;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.security.bean.ApiKey;
import org.smartbit4all.api.security.bean.ApiKeyScope;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyConfig {


  public static final String API_KEYS = "apiKeys";
  public static final String API_KEY_SCOPES = "apiKeyScopes";

  @Bean
  public ApiKeyApi apiKeyApi() {
    return new ApiKeyApiImpl();
  }

  @Bean
  public ApiKeyInnerApi apiKeyInnerApi() {
    return new ApiKeyInnerApiImpl();
  }

  @Bean
  public ApiKeyAuthenticationProvider apiKeyAuthenticationProvider() {
    return new ApiKeyAuthenticationProvider();
  }

  @Bean
  public SessionApiKeyAuthenticationProvider sessionApiKeyAuthenticationProvider() {
    return new SessionApiKeyAuthenticationProvider(apiKeyAuthenticationProvider());
  }

  @Bean
  MDMDefinitionOption apiKeyMdmOption() {
    MDMDefinition mdmDefinition =
        new MDMDefinition().name(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION)
            .adminGroupName(PlatformSecurityOption.admin.getName());
    MDMDefinitionOption result = new MDMDefinitionOption(mdmDefinition);

    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .order(9L)
          .schema(MasterDataManagementApi.SCHEMA)
          .publishedListName(API_KEYS)
          .name(API_KEYS)
          .adminGroupName(PlatformSecurityOption.apiKeyEditor.getName())
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(ApiKey.TOKEN))
          .editorViewName(PlatformViewNames.API_KEY_EDITOR)
          .displayNameList(new LangString().defaultValue("Api keys")
              .putValueByLocaleItem("hu", "Api kulcsok")
              .putValueByLocaleItem("en", "Api keys"))
          .displayNameForm(new LangString().defaultValue("Api key")
              .putValueByLocaleItem("hu", "Api kulcs")
              .putValueByLocaleItem("en", "Api key"))
          .typeQualifiedName(ApiKey.class.getName())
          .searchIndexForEntries(API_KEYS + "_admin")
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name(ApiKey.USER)
                  .addPathItem(ApiKey.USER)
                  .addPathItem(User.USERNAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name(ApiKey.SCOPE)
                  .addPathItem(ApiKey.SCOPE)) // search index is overridden on this column
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name(ApiKey.CREATED)
                  .addPathItem(ApiKey.CREATED)
                  .addPathItem(UserActivityLog.TIMESTAMP))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name(ApiKey.EXPIRATION)
                  .addPathItem(ApiKey.EXPIRATION))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name(ApiKey.REVOKED)
                  .addPathItem(ApiKey.REVOKED)
                  .addPathItem(UserActivityLog.TIMESTAMP))
          .putPropertyMappingsItem(MDMEntryApi.Props.CREATED, ApiKey.CREATED)
          .putPropertyMappingsItem(MDMEntryApi.Props.REMOVED, ApiKey.REVOKED);
      result.addDescriptor(entry);
    }
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .order(Long.MAX_VALUE)
          .schema(MasterDataManagementApi.SCHEMA)
          .branchingStrategy(MDMBranchingStrategy.NONE)
          .publishedListName(API_KEY_SCOPES)
          .name(API_KEY_SCOPES)
          .adminGroupName(PlatformSecurityOption.apiKeyEditor.getName())
          .hidden(Boolean.TRUE)
          .typeQualifiedName(ApiKeyScope.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name(ApiKeyScope.NAME)
                  .addPathItem(ApiKeyScope.NAME))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name(ApiKeyScope.PATH_PATTERN)
                  .addPathItem(ApiKeyScope.PATH_PATTERN));
      result.addDescriptor(entry);
    }
    return result;
  }

  @Bean
  public SearchIndex<ApiKey> apiKeySearchIndex(MDMSearchIndexApi mdmSearchIndexApi) {
    MDMDefinition definition = apiKeyMdmOption().getDefinition();
    MDMEntryDescriptor apiKeyDesc = definition.getDescriptors().get(API_KEYS);
    SearchIndexImpl<?> apiKeyPublishedSI =
        mdmSearchIndexApi.createSearchIndexForEntry(apiKeyDesc, definition.getName());
    apiKeyPublishedSI.mapComplex(ApiKey.SCOPE, String.class, 500, this::concatenateScopeNames);
    return (SearchIndex<ApiKey>) apiKeyPublishedSI;
  }

  @Bean
  public SearchIndex<BranchedObjectEntry> apiKeySearchIndexAdmin(
      MDMSearchIndexApi mdmSearchIndexApi) {
    MDMDefinition definition = apiKeyMdmOption().getDefinition();
    MDMEntryDescriptor apiKeyDesc = definition.getDescriptors().get(API_KEYS);
    SearchIndexImpl<BranchedObjectEntry> apiKeyAdminSI =
        mdmSearchIndexApi.createSearchIndexForEntryInstance(apiKeyDesc, definition.getName());
    apiKeyAdminSI.mapComplex(ApiKey.SCOPE, String.class, 500,
        branchingNode -> {
          ObjectNode apiKeyNode =
              mdmSearchIndexApi.getActualObjectNodeOfBranchedNode(branchingNode);
          return concatenateScopeNames(apiKeyNode);
        });

    return apiKeyAdminSI;
  }

  private String concatenateScopeNames(ObjectNode apiKeyNode) {
    return apiKeyNode.list(ApiKey.SCOPE).nodeStream()
        .map(scopeNode -> scopeNode.getValueAsString(ApiKeyScope.NAME))
        .collect(Collectors.joining(", "));
  }

}
