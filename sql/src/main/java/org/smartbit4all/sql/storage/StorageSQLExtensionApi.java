package org.smartbit4all.sql.storage;

import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageLoadOption;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.sql.storage.StorageSQL.UriInfo;

/**
 * This API can define special save options for schema and object definition.
 */
public interface StorageSQLExtensionApi {

  public static class ManagedObject {

    public String schema;

    public String qualifiedName;

    public ManagedObject(String schema, String qualifiedName) {
      super();
      this.schema = schema;
      this.qualifiedName = qualifiedName;
    }

  }

  /**
   * @return The schema:objectdefinition strings that identifies the managed objects.
   */
  List<ManagedObject> getManagedObjects();

  /**
   * Can be delegated here to load the objects from sepcial table.
   * 
   * @param <T>
   * @param storage
   * @param uriInfos
   * @param clazz
   * @param options
   * @return
   */
  <T> List<StorageObject<T>> loadBatch(Storage storage, List<UriInfo> uriInfos, Class<T> clazz,
      StorageLoadOption... options);

  /**
   * In case of the database the save process is almost the same. We select the object record for
   * update or insert this
   *
   * @param object
   * @param relationBinaryData
   * @return The version number of the newly saved object.
   */
  Long saveObject(StorageObject<?> object, BinaryData relationBinaryData);

}
