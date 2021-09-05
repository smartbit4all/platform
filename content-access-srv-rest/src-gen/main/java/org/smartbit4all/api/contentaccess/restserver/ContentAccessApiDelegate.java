package org.smartbit4all.api.contentaccess.restserver;

import java.util.UUID;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A delegate to be called by the {@link ContentAccessApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ContentAccessApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /download/{uuid}
     *
     * @param uuid  (required)
     * @return File with the given uuid. (status code 200)
     * @see ContentAccessApi#download
     */
    default ResponseEntity<org.springframework.core.io.Resource> download(UUID uuid) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /upload
     *
     * @param uuid  (optional)
     * @param file  (optional)
     * @return The file was successfully uploaded. (status code 200)
     * @see ContentAccessApi#upload
     */
    default ResponseEntity<Void> upload(UUID uuid,
        MultipartFile file) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
