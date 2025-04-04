/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.domain.config;

import org.smartbit4all.api.config.PlatformSecurityOption;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.mdm.MDMDefinitionOption;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint;
import org.smartbit4all.api.mdm.bean.MDMEntryConstraint.KindEnum;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.storage.bean.StorageArchiveBatch;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessExecution;
import org.smartbit4all.core.config.CoreConfig;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.domain.data.storage.StorageArchiveApi;
import org.smartbit4all.domain.meta.MetaConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The configuration for the platform domain. It's not a business domain it's the platform basic
 * package.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({CoreConfig.class, MetaConfiguration.class, DomainServiceConfig.class})
public class DomainConfig {

  public static final String ENTRY_ARCHIVE_CONFIGS = "archiveConfigurations";

  @Bean
  DomainAPI domainAPI() {
    return new DomainAPIImpl();
  }

  @Bean
  MDMDefinitionOption systemIntegrationDomainMdmOption(LocaleSettingApi localeSettingApi) {
    MDMDefinition mdmDefinition =
        new MDMDefinition().name(MasterDataManagementApi.MDM_DEFINITION_SYSTEM_INTEGRATION)
            .adminGroupName(PlatformSecurityOption.admin.getName());
    MDMDefinitionOption result =
        new MDMDefinitionOption(mdmDefinition);
    {
      MDMEntryDescriptor entry = new MDMEntryDescriptor()
          .schema(StorageArchiveApi.SCHEMA_ARCHIVAL)
          .publishedListName(ENTRY_ARCHIVE_CONFIGS)
          .name(ENTRY_ARCHIVE_CONFIGS)
          .adminGroupName(PlatformSecurityOption.storageArchiveConfigEditor.getName())
          .addConstraintsItem(new MDMEntryConstraint()
              .kind(KindEnum.UNIQUECASEINSENSITIVE)
              .addPathItem(StorageArchiveProcessConfig.CODE))
          .editorViewName(PlatformViewNames.STORAGE_ARCHIVE_PROCESS_EDITOR)
          .displayNameList(new LangString().defaultValue("Storage archive configurations")
              .putValueByLocaleItem("hu", "Archiválási beállítások")
              .putValueByLocaleItem("en", "Storage archive configurations"))
          .displayNameForm(new LangString().defaultValue("Storage archive configuration")
              .putValueByLocaleItem("hu", "Archiválási beállítás")
              .putValueByLocaleItem("en", "Storage archive configuration"))
          .order(200l)
          .typeQualifiedName(StorageArchiveProcessConfig.class.getName())
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Code")
                  .addPathItem(StorageArchiveProcessConfig.CODE))
          .addTableColumnsItem(
              new MDMTableColumnDescriptor()
                  .name("Storage")
                  .addPathItem(StorageArchiveProcessConfig.STORAGE));
      result.addDescriptor(entry);
    }
    return result;
  }

  @Bean
  public ObjectReferenceConfigs objectReferenceConfigsPlatformDomain() {
    return new ObjectReferenceConfigs()
        .ref(StorageArchiveProcessExecution.class,
            StorageArchiveProcessExecution.CONFIG,
            StorageArchiveProcessConfig.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.NONE)
        .ref(StorageArchiveProcessExecution.class,
            StorageArchiveProcessExecution.ARCHIVE_BATCH,
            StorageArchiveBatch.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.NONE);
  }

}
