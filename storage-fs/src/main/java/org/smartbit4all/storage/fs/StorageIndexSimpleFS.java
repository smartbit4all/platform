package org.smartbit4all.storage.fs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.filtering.ExpressionEvaluationPlan;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.index.StorageIndex;
import org.smartbit4all.domain.data.storage.index.StorageIndexField;
import org.smartbit4all.domain.data.storage.index.StorageIndexLoader;
import org.smartbit4all.domain.data.storage.index.StorageIndexUtil;
import org.smartbit4all.domain.data.storage.index.StorageIndexer;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Property;

public class StorageIndexSimpleFS<T> implements StorageIndexer<T> {

  private File rootFolder;

  private Storage storage;

  public StorageIndexSimpleFS(
      File rootFolder) {

    this.rootFolder = rootFolder;
  }

  @Override
  public <F> boolean canUseFor(Property<F> valueField, Expression expression) {
    return StorageIndexUtil.twoOperandPropertyIndex(valueField, expression);
  }

  @Override
  public void updateIndex(T object, StorageIndex<T> index) throws Exception {
    URI uri = index.getObjectUriProvider().apply(object);

    List<StorageIndexField<T, ?>> indexFields = index.getFields();

    clearExistingLinks(indexFields, uri);
    createNewLinks(object, uri, indexFields);
  }

  @Override
  public List<URI> listUris(StorageIndex<T> index, Expression expression) throws Exception {
    StorageIndexLoader<T> storageIndexLoader = new StorageIndexLoader<>(
        index.getEntityDef(),
        new ArrayList<>(),
        storage,
        index.getKey());

    for (StorageIndexField<T, ?> storageIndexField : index.getFields()) {
      storageIndexLoader.addIndex(storageIndexField);
    }

    ExpressionEvaluationPlan plan = ExpressionEvaluationPlan.of(storageIndexLoader, expression);
    List<DataRow> rows = plan.execute(Collections.emptyList());

    List<URI> uris = new ArrayList<>();
    for (DataRow row : rows) {
      URI uri = row.get(index.getKey());
      uris.add(uri);
    }

    return uris;
  }

  @Override
  public <V> List<URI> listUris(Property<URI> key, Property<V> indexField, V value)
      throws Exception {

    List<URI> result = new ArrayList<>();

    File indexFolder = FSStorageUtil.indexFolder(
        rootFolder,
        FSStorageUtil.indexPath(
            indexField.getEntityDef().entityDefName(),
            indexField.getName(),
            value));

    if (indexFolder.exists()) {
      List<File> files = Arrays.asList(indexFolder.listFiles());

      for (File file : files) {
        result.add(getLinkFileUri(file));
      }
    }

    return result;
  }

  private void createNewLinks(
      T object,
      URI uri,
      List<StorageIndexField<T, ?>> indexFields) throws URISyntaxException, IOException {

    for (StorageIndexField<T, ?> indexField : indexFields) {
      Optional<?> fieldValue = indexField.getCalculator().apply(object);
      if (fieldValue.isPresent()) {
        createLinkFile(FSStorageUtil.indexValuePath(indexField, fieldValue.get()), uri);
      }
    }
  }

  private void clearExistingLinks(
      List<StorageIndexField<T, ?>> indexFields,
      URI uri) throws IOException {

    Set<String> indexFolders = new HashSet<>();
    for (StorageIndexField<T, ?> indexField : indexFields) {
      indexFolders.add(
          FSStorageUtil.indexPath(
              indexField.getIndexName(),
              indexField.getValueField().getName()));
    }

    String fileName = FSStorageUtil.getLinkFileName(uri);

    for (String indexKey : indexFolders) {
      File indexDir = new File(rootFolder, indexKey);

      if (indexDir.exists() && indexDir.isDirectory()) {
        Files.walk(indexDir.toPath())
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach((file) -> {

              if (fileName.equals(file.getName())) {
                file.delete();
              }

            });
      }
    }
  }

  private File getIndexFile(String path, URI objectUri) {
    String linkFileName = FSStorageUtil.getLinkFileName(objectUri);

    File currentFolder = FSStorageUtil.indexFolder(rootFolder, path);
    File newFile = new File(currentFolder.getPath(), linkFileName);

    return newFile;
  }

  private void createLinkFile(String path, URI objectUri)
      throws URISyntaxException, IOException {

    File newFile = getIndexFile(path, objectUri);
    File parentFile = newFile.getParentFile();

    parentFile.mkdirs();

    try (FileWriter writer = new FileWriter(newFile)) {
      writer.write(objectUri.toString());
    }
  }

  private URI getLinkFileUri(File f) throws Exception {
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
      return new URI(br.readLine());
    } catch (Exception e) {
      // log.error("Error while reading link file", e);
      throw e;
    }
  }

  public void setStorage(Storage storage) {
    this.storage = storage;
  }

}
