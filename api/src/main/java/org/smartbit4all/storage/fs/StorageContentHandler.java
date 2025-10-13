package org.smartbit4all.storage.fs;

import java.io.File;
import java.util.List;
import java.util.RandomAccess;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.storage.bean.StorageStrategy;

/**
 * The {@link StorageFS} is built on the top of file system. In the {@link StorageStrategy#CLASSIC}
 * operation mode the content is a simple {@link File} that contains contents of
 * {@link BinaryData}s. By introducing different kind of {@link StorageStrategy} this is not so
 * simple bevcause in a {@link RandomAccess} indexed file this handler is the File itself but also
 * the index position. The subclasses represents the different handlers for this strategies.
 */
public abstract class StorageContentHandler {

  public abstract void writeMultipart(BinaryData[] contents);

  public abstract List<BinaryData> readMultipart();

}
