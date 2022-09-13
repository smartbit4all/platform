package org.smartbit4all.api.view.restserver;

import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
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
 * A delegate to be called by the {@link ViewApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ViewApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /context : Returns the existing session info
     *
     * @param uuid ViewContext&#39;s unique identifier. (required)
     * @return Returns ViewContext by unique identifier (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the session data (status code 500)
     * @see ViewApi#getViewContext
     */
    default ResponseEntity<ViewContext> getViewContext(UUID uuid) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"uri\" : \"https://openapi-generator.tech\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"branchUri\" : \"https://openapi-generator.tech\", \"objectUri\" : \"https://openapi-generator.tech\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"branchUri\" : \"https://openapi-generator.tech\", \"objectUri\" : \"https://openapi-generator.tech\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /context : Updates an existing view context
     *
     * @param viewContextUpdate  (required)
     * @return Context updated (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error during context update (status code 500)
     * @see ViewApi#updateViewContext
     */
    default ResponseEntity<ViewContext> updateViewContext(ViewContextUpdate viewContextUpdate) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"uri\" : \"https://openapi-generator.tech\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"branchUri\" : \"https://openapi-generator.tech\", \"objectUri\" : \"https://openapi-generator.tech\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"branchUri\" : \"https://openapi-generator.tech\", \"objectUri\" : \"https://openapi-generator.tech\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
