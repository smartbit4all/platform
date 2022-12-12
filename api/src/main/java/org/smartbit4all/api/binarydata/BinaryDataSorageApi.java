package org.smartbit4all.api.binarydata;

import java.net.URI;

public interface BinaryDataSorageApi {

  URI save(String logicalSchema, BinaryData data);

  URI update(URI uri, BinaryData data);

  BinaryData load(URI uri);

  BinaryData loadLatest(URI uri);

}
