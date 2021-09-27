package org.smartbit4all.api.apiregister.restserver;

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
 * A delegate to be called by the {@link ApiRegisterApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ApiRegisterApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /register
     *
     * @param body  (required)
     * @return  (status code 200)
     * @see ApiRegisterApi#register
     */
    default ResponseEntity<org.smartbit4all.api.apiregister.bean.Registration> register(org.smartbit4all.api.apiregister.bean.ApiInfo body) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf(""))) {
                    String exampleString = "";
                    ApiUtil.setExampleResponse(request, "", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /unregister
     *
     * @param body  (required)
     * @return  (status code 200)
     * @see ApiRegisterApi#unregister
     */
    default ResponseEntity<Void> unregister(org.smartbit4all.api.apiregister.bean.Registration body) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
