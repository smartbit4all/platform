package org.smartbit4all.api.invocation.restserver;

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
 * A delegate to be called by the {@link InvocationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface InvocationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /invokeApi
     *
     * @param orgSmartbit4allApiInvocationBeanInvocationRequestData  (required)
     * @return  (status code 200)
     * @see InvocationApi#invokeApi
     */
    default ResponseEntity<org.smartbit4all.api.invocation.bean.InvocationParameterData> invokeApi(org.smartbit4all.api.invocation.bean.InvocationRequestData orgSmartbit4allApiInvocationBeanInvocationRequestData) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"typeClass\" : \"typeClass\", \"value\" : \"value\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
