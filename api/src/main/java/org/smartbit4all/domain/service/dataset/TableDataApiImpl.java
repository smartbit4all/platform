package org.smartbit4all.domain.service.dataset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.RandomAccess;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.core.io.utility.FileIO;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.data.TableDatas;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.service.CrudApi;
import org.smartbit4all.domain.service.entity.EntityManager;
import org.smartbit4all.domain.utility.serialize.TableDataPager;
import org.smartbit4all.domain.utility.serialize.TableDataSerializer;
import org.smartbit4all.storage.fs.StorageFS;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * The table data is always temporary data set that is used to save a database table like set of
 * data that can be used in SQL expressions based on the features of the {@link CrudApi}. The table
 * data api is based on file system {@link RandomAccess} to optimize the write and read of the huge
 * tables.
 * 
 * @author Peter Boros
 *
 */
public class TableDataApiImpl implements TableDataApi {

  private static final String TABLEDATACONTENTS = "tabledatacontents";

  private static final String TABLEDATAFILEEXTESION = ".td";

  private static final Logger log = LoggerFactory.getLogger(TableDataApiImpl.class);

  private Map<URI, TableData<?>> tableDatas = new HashMap<>();

  @Autowired
  private EntityManager entityManager;

  /**
   * The root folder of the file system storage. If null then the storage of the table data files
   * will be located in the temporary directory of the jvm.
   */
  private File rootFolder = null;

  public TableDataApiImpl(@Autowired(required = false) StorageApi storageApi) {
    super();
    if (storageApi != null && storageApi.getDefaultObjectStorage() instanceof StorageFS) {
      rootFolder = new File(((StorageFS) storageApi.getDefaultObjectStorage()).getRootFolder(),
          TABLEDATACONTENTS);
    } else {
      try {
        File parentFile = File.createTempFile("tabledata", "root").getParentFile();
        rootFolder =
            new File(parentFile, TABLEDATACONTENTS);
        Files.delete(parentFile.toPath());
      } catch (IOException e) {
        log.error("Unable to initiate the temp directory to save table data serialized contents.");
      }
    }
    if (rootFolder != null && rootFolder.exists()) {
      rootFolder.mkdirs();
    }
  }

  @Override
  public URI save(TableData<?> tableData) {
    if (tableData == null) {
      return null;
    }
    URI uri = constructUri(UUID.randomUUID());
    if (rootFolder != null) {
      File fileByUri = FileIO.getFileByUri(rootFolder, uri, TABLEDATAFILEEXTESION);
      fileByUri.getParentFile().mkdirs();
      try (FileOutputStream os = new FileOutputStream(fileByUri)) {
        TableDataSerializer.save(tableData, os);
      } catch (IOException e) {
        throw new IllegalStateException("Unable to construct the file for the table data.", e);
      }
    } else {
      tableDatas.put(uri, tableData);
    }

    return uri;
  }

  @Override
  public TableData<?> read(URI uri) {
    if (rootFolder != null) {
      try {
        TableDataPager<?> pager = TableDataPager
            .create(FileIO.getFileByUri(rootFolder, uri, TABLEDATAFILEEXTESION), entityManager);
        return pager.fetchAll();
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to read the " + uri + " table data.", e);
      }
    } else {
      return tableDatas.get(uri);
    }
  }

  @Override
  public TableData<?> readPage(URI uri, int lowerBound, int upperBound) {
    if (rootFolder != null) {
      try {
        TableDataPager<?> pager = TableDataPager
            .create(FileIO.getFileByUri(rootFolder, uri, TABLEDATAFILEEXTESION), entityManager);
        return pager.fetch(lowerBound, upperBound);
      } catch (Exception e) {
        throw new IllegalArgumentException("Unable to read the " + uri + " table data.", e);
      }
    } else {
      return TableDatas.copyRows(tableDatas.get(uri), lowerBound, upperBound);
    }
  }

  @Override
  public void delete(URI uri) {
    tableDatas.remove(uri);
  }

  private final URI constructUri(UUID uuid) {
    LocalDateTime now = LocalDateTime.now();
    return URI.create(TABLEDATACONTENTS + StringConstant.COLON + StringConstant.SLASH
        + now.getYear() + StringConstant.SLASH + now.getMonthValue() + StringConstant.SLASH
        + now.getDayOfMonth() + StringConstant.SLASH + now.getHour() + StringConstant.SLASH
        + now.getMinute() + StringConstant.SLASH
        + now.getSecond() + StringConstant.SLASH
        + uuid);
  }

}
