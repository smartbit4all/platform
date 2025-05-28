package org.smartbit4all.api.mdm.restserver;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link MasterDataManagementApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
public interface MasterDataManagementApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /mdm/{definition}/{entry}/{id}/getAccessToken : Returns a unique token that identifies the MDM entry. 
     *
     * @param definition Unique identifier of the MDM namespace. (required)
     * @param entry Unique identifier of the MDM entry list. (required)
     * @param id Unique identifier of the MDM entry. (required)
     * @return Data fetch successful. (status code 200)
     *         or Resource cannot be found. (status code 404)
     *         or There was an error during the data fetch. (status code 500)
     * @see MasterDataManagementApi#getAccessToken
     */
    default ResponseEntity<String> getAccessToken(String definition,
        String entry,
        String id) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
