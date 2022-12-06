package org.smartbit4all.domain.data.storage;

import java.io.File;
import java.net.URI;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryData;

/**
 * This interface encapsulate the blob based (binary save format) access api for the
 * {@link ObjectStorage} implementations. The blob is a well defined binary object identified by the
 * storage specific way. The object storage implementation is working with this identifier and
 * transform the URI of the object to and from.
 * 
 * TODO The fists implementation is going to be used over the file system so for the sake of
 * simplicity the api will have File based functions. As we introduce the new kind of blob storages
 * then we can replace it with blob specific identifiers.
 * 
 * @author Peter Boros
 */
public interface BlobObjectStorageAccessApi {

  void writeVersion(File newFile, URI versionUri, BinaryData... contents);

  List<BinaryData> readVersion(File file, URI versionUri);

  boolean exists(File objectFile, URI latestUri);

}
