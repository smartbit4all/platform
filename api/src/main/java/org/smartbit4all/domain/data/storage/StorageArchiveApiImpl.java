package org.smartbit4all.domain.data.storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.archive.bean.ArchiveConfigData;
import org.smartbit4all.api.archive.bean.BeforeVersionData;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

/*
 * Delete unnecessary files from file system
 */
public class StorageArchiveApiImpl implements StorageArchiveApi {
  private static final Logger log = LoggerFactory.getLogger(StorageApiImpl.class);

  @Autowired
  private ObjectApi objectApi;

  @Override
  public void startArchive(String archiveConfigFile) {

    if (!ObjectUtils.isEmpty(archiveConfigFile)) {
      try {

        final File file = new File(archiveConfigFile);

        ObjectDefinition<ArchiveConfigData> objectDefinition =
            objectApi.definition(ArchiveConfigData.class);
        ArchiveConfigData archiveConfigData =
            objectDefinition.deserialize(BinaryData.of(new FileInputStream(file))).get();

        String folder = archiveConfigData.getRootDir();

        List<String> deleteAll = archiveConfigData.getDeleteAll();

        for (String path : deleteAll) {
          String deletePath = concatenatePath(folder, path);
          deleteFiles(deletePath);
        }
        // "/linked-changes/org_smartbit4all_api_storage_bean_StorageSaveEventObject/2022"
        // "/SB4STARTER/org_smartbit4all_api_binarydata_BinaryDataObject/2022"
        // "/transaction/org_smartbit4all_api_storage_bean_TransactionData/2022"

        List<BeforeVersionData> deleteBeforeVersion = archiveConfigData.getDeleteBeforeVersion();

        for (BeforeVersionData data : deleteBeforeVersion) {
          String deletePath = concatenatePath(folder, data.getPath());
          int version = data.getVersion();
          deleteOldVersions(deletePath, version);
        }
        // "/linked-changes/org_smartbit4all_api_storage_bean_ObjectMap"
        // "/utemezes/hu_it4all_nmhh_lrl_domain_settings_model_UtemezettFutas"

      } catch (Exception e) {
        log.error(e.getMessage());
      }
    }

  }

  private String concatenatePath(String base, String path) {
    if ((base.endsWith(SLASH) || base.endsWith(BACKSLASH))
        && (path.startsWith(SLASH) || path.startsWith(BACKSLASH))) {
      return base + path.substring(1);
    }
    if (!(base.endsWith(SLASH) || base.endsWith(BACKSLASH))
        && !(path.startsWith(SLASH) || path.startsWith(BACKSLASH))) {
      return base + SLASH + path;
    }
    return base + path;
  }

  private void deleteFiles(String folder) {
    File file = new File(folder);
    File[] list = file.listFiles();

    if (list != null)
      for (File fil : list) {
        if (fil.isDirectory()) {
          deleteFiles(fil.getAbsolutePath());
        } else {
          deleteFile(fil);
        }
      }
  }

  private void deleteOldVersions(String folder, int versionBefore) {
    List<String> files = findFile(".o", new File(folder));

    for (String fileName : files) {
      folder = fileName.substring(0, fileName.length() - 2);
      try (FileReader reader = new FileReader(fileName)) {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        if (line != null) {
          String[] lines = line.split(",");
          if (lines.length < 2) {
            continue;
          }
          String serialNo = lines[1].substring(lines[1].indexOf("\"serialNoData\":"));
          String no = serialNo.substring(serialNo.indexOf(':') + 1);
          int version = Integer.parseInt(no);

          for (int i = 0; i < version - versionBefore; i++) {
            String id = FileIO.constructObjectPathByIndexWithHexaStructure(i);
            File file = new File(folder + id);
            deleteFile(file);
          }
        }
      } catch (IOException | NullPointerException | NumberFormatException e) {
        e.printStackTrace();
      }
    }
  }

  private void deleteFile(File file) {
    try {
      Path path = Paths.get(file.getPath());
      Files.delete(path);
    } catch (SecurityException | IOException e) {
      log.error(e.getMessage());
    }
  }

  private List<String> findFile(String name, File file) {
    File[] list = file.listFiles();
    List<String> fileNames = new ArrayList<>();

    if (list != null)
      for (File fil : list) {
        if (fil.isDirectory()) {
          fileNames.addAll(findFile(name, fil));
        } else if (fil.getName().indexOf(name) >= 0) {
          fileNames.add(fil.getAbsolutePath());
        }
      }

    return fileNames;
  }
}
