package org.smartbit4all.domain.data.storage;

import java.net.URI;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessConfig;
import org.smartbit4all.api.storage.bean.StorageArchiveProcessExecution;
import org.smartbit4all.domain.config.DomainConfig;

public interface StorageArchiveApi {
  public static final String SLASH = "/";
  public static final String BACKSLASH = "\\";
  public static final String ZERO = "0";

  String SCHEMA_ARCHIVAL = "storage-archival";

  void startArchive(String archiveConfigFile);

  /**
   * The operation is reading the {@link DomainConfig#ENTRY_ARCHIVE_CONFIGS} MDM managed list. All
   * the entries are evaluated in each application instance. Every archive configuration have its
   * own history object in a named reference. The latest version represents the last run of the
   * archival. If the archival is ready to run because the last run was finished then this
   * application instance will initiate a new archival. If the archival is not finished and the
   * application runtime is not active then this runtime can catch this run and finish it. Els we
   * skip the given configuration.
   */
  void scheduleArchival();

  /**
   * Try to execute the configuration in the parameter. The executions of the configuration is
   * tracked in the {@link StorageArchiveProcessExecution} object referred. This archival is
   * executing when the execution object is missing or the execution object is ready to run or if we
   * have an ongoing execution but the runtime is not alive any more.
   * 
   * @param config
   * @return Number of archived objects.
   */
  int executeArchive(URI config);

  URI createConfig(StorageArchiveProcessConfig config, URI branchUri);

}
