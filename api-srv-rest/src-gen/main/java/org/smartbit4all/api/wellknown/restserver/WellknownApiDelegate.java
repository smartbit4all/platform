package org.smartbit4all.api.wellknown.restserver;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

/**
 * A delegate to be called by the {@link WellknownApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface WellknownApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /.well-known/{id}
     * Get well known content.
     *
     * @param id content id. (required)
     * @return Successful operation (status code 200)
     *         or Content not found (status code 404)
     * @see WellknownApi#getWellknown
     */
    default ResponseEntity<org.springframework.core.io.Resource> getWellknown(String id) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
