package org.smartbit4all.api.invocation.restserver;

import org.smartbit4all.api.invocation.bean.InvocationParameter;
import org.smartbit4all.api.invocation.bean.InvocationRequest;
import java.util.UUID;
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
 * A delegate to be called by the {@link InvocationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface InvocationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /invokeApi
     *
     * @param invocationRequest  (required)
     * @return  (status code 200)
     *         or The api was not found. (status code 404)
     * @see InvocationApi#invokeApi
     */
    default ResponseEntity<InvocationParameter> invokeApi(InvocationRequest invocationRequest) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"name\" : \"name\", \"typeClass\" : \"typeClass\", \"innerTypeClass\" : \"innerTypeClass\", \"value\" : \"{}\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /invokeDownload
     *
     * @param invocationRequest  (required)
     * @return  (status code 200)
     *         or The api was not found. (status code 404)
     *         or Error occured while fetching the downloadable item (status code 500)
     * @see InvocationApi#invokeDownload
     */
    default ResponseEntity<org.springframework.core.io.Resource> invokeDownload(InvocationRequest invocationRequest) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /invokeUploadDownloadMultiple
     * Performs a generic invocation with contents to upload. These uploaded contents are identified by their position by the parameters. The result is a content also. 
     *
     * @param uuid  (required)
     * @param invocationRequest Stringify-d InvocationRequest where the upcoming contents are referred by the parameters. (optional)
     * @param contents  (optional)
     * @return  (status code 200)
     *         or The api was not found. (status code 404)
     *         or Error occured while fetching the downloadable item (status code 500)
     * @see InvocationApi#invokeUploadDownloadMultiple
     */
    default ResponseEntity<org.springframework.core.io.Resource> invokeUploadDownloadMultiple(UUID uuid,
        String invocationRequest,
        List<MultipartFile> contents) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /invokeUploadMultiple
     * Performs a generic invocation with contents to upload. These uploaded contents are identified by their position by the parameters. 
     *
     * @param invocationRequest Stringify-d InvocationRequest where the upcoming contents are referred by the parameters. (optional)
     * @param contents  (optional)
     * @return  (status code 200)
     *         or The api was not found. (status code 404)
     * @see InvocationApi#invokeUploadMultiple
     */
    default ResponseEntity<InvocationParameter> invokeUploadMultiple(String invocationRequest,
        List<MultipartFile> contents) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"name\" : \"name\", \"typeClass\" : \"typeClass\", \"innerTypeClass\" : \"innerTypeClass\", \"value\" : \"{}\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
