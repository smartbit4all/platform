package org.smartbit4all.storage.fs;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.smartbit4all.domain.data.storage.index.StorageReindexer;

public class StorageReindexerFS implements StorageReindexer {

  private String uriSchema;

  private String fileExtension;

  private File rootFolder;
  
  public StorageReindexerFS(String uriSchema, File rootFolder, String fileExtension) {
    this.uriSchema = uriSchema;
    this.fileExtension = fileExtension;
    this.rootFolder = rootFolder;
  }

  @Override
  public List<URI> listAllUris() throws Exception {
    List<URI> result = new ArrayList<>();

    Files.walk(rootFolder.toPath())
        .sorted(Comparator.reverseOrder())
        .map(Path::toFile)
        .forEach((file) -> {

          if (file.isFile() && file.getName().endsWith("." + fileExtension)) {
            try {

              URI uri = getUri(file);
              result.add(uri);

            } catch (URISyntaxException e) {
              throw new IllegalStateException("Cannot create uri for file: " + file);
            }
          }

        });

    return result;
  }

  private URI getUri(File file) throws URISyntaxException {
    Path relativize = rootFolder.toPath().relativize(file.toPath());
    URI uri = new URI(uriSchema + ":/" + relativize.toString().replace("\\", "/"));

    return uri;
  }
  
}
