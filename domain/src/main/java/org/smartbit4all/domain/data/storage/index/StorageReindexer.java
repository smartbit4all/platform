package org.smartbit4all.domain.data.storage.index;

import java.net.URI;
import java.util.List;

/**
 * 
 * Reindexer can list all of the content URIs from the database, 
 * and call update index on each of the contents.
 * 
 * @author Zoltan Szegedi
 * 
 */
public interface StorageReindexer {

  public List<URI> listAllUris() throws Exception;
  
}
