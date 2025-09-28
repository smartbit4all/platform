package org.smartbit4all.api.mdm.restserver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.mdm.MDMSetupApi;
import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.smartbit4all.api.mdm.bean.MDMImportEntryCsvDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public class MasterDataManagementApiDelegateImpl implements MasterDataManagementApiDelegate {

  private static final Logger log =
      LoggerFactory.getLogger(MasterDataManagementApiDelegateImpl.class);


  @Autowired
  private MasterDataManagementApi mdmApi;

  @Autowired
  protected MDMSetupApi mdmSetupApi;


  @Override
  public ResponseEntity<String> getAccessToken(String definition, String entry, String id)
      throws Exception {
    try {
      String accessToken = mdmApi.getAccessToken(definition, entry, id);
      if (accessToken == null) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("There was an error during the data fetch.");
      }
      return ResponseEntity.ok(accessToken);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("There was an error during the data fetch.");
    }
  }

  @Override
  public ResponseEntity<Void> importEntriesFromCsvFile(MDMImportEntryCsvDescriptor data,
      MultipartFile content) throws Exception {
    try {
      String definition = data.getDefinition();
      String entry = data.getEntry();
      String csvSeparator = data.getCsvSeparator();
      // FIXME: fill branchUri
      URI branchUri = null;
      processFile(content, binaryData -> mdmSetupApi.importEntriesFromCsvFile(
          definition, entry, binaryData, csvSeparator, branchUri));
    } catch (IllegalArgumentException iae) {
      log.error(iae.getMessage(), iae);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  @Override
  public ResponseEntity<Void> loadEntries(MultipartFile content) throws Exception {
    try {
      processFile(content, binaryData -> mdmSetupApi.loadEntries(binaryData));
    } catch (IllegalArgumentException iae) {
      log.error(iae.getMessage(), iae);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  @Override
  public ResponseEntity<Void> loadValueLists(MultipartFile content) throws Exception {
    try {
      processFile(content, binaryData -> mdmSetupApi.loadValueLists(binaryData));
    } catch (IllegalArgumentException iae) {
      log.error(iae.getMessage(), iae);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    return ResponseEntity.status(HttpStatus.OK).body(null);
  }

  private void processFile(MultipartFile uploadedFile, Consumer<BinaryData> processor)
      throws Exception {
    try (InputStream inputStream = uploadedFile.getInputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(inputStream.readAllBytes())) {
      byte[] bytes = bais.readAllBytes();
      BinaryData binaryData = new BinaryData(bytes);
      processor.accept(binaryData);
    }
  }
}
