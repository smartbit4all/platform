package org.smartbit4all.api.mdm.restserver;

import org.smartbit4all.api.mdm.MasterDataManagementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MasterDataManagementApiDelegateImpl implements MasterDataManagementApiDelegate {

  @Autowired
  private MasterDataManagementApi mdmApi;

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

}
