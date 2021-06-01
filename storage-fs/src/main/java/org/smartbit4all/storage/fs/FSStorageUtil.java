package org.smartbit4all.storage.fs;

import java.io.File;
import java.net.URI;
import org.smartbit4all.domain.data.storage.index.StorageIndexField;

public class FSStorageUtil {

  public static String link = ".doc.lnk";

  public static <T> String indexValuePath(StorageIndexField<T, ?> index, Object value) {
    return indexPath(index.getIndexName(), index.getValueField().getName(), value);
  }
  
  public static String indexPath(String indexName, String indexKey) {
    return indexName + "/" + indexKey;
  }

  public static String indexPath(String indexName, String indexKey, Object value) {
    return indexName + "/" + indexKey + "/" + value.toString();
  }
  
  public static File indexFolder(File rootFolder, String path) {
    return new File(rootFolder, path);
  }
  
  public static String getLinkFileName(URI objectUri) {
    String name = getFileName(objectUri);
    return name + link;
  }
  
  public static String getFileName(URI objectUri) {
    return objectUri.getPath().substring(objectUri.getPath().lastIndexOf('/') + 1);
  }
  
}
