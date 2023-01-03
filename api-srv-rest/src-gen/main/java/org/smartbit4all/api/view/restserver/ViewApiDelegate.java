package org.smartbit4all.api.view.restserver;

import org.smartbit4all.api.view.bean.MessageResult;
import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewContext;
import org.smartbit4all.api.view.bean.ViewContextUpdate;
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
 * A delegate to be called by the {@link ViewApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ViewApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /context : Creates a new ViewContext
     *
     * @return Context created (status code 200)
     * @see ViewApi#createViewContext
     */
    default ResponseEntity<ViewContext> createViewContext() throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /view/{uuid}/constraint : Returns the view constraint object belongs to the given view
     *
     * @param uuid View&#39;s unique identifier. (required)
     * @return Returns ViewConstraint object for the view identified by unique identifier (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the constraints data (status code 500)
     * @see ViewApi#getViewConstraint
     */
    default ResponseEntity<ViewConstraint> getViewConstraint(UUID uuid) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"componentConstraints\" : [ { \"visible\" : true, \"dataName\" : \"dataName\", \"mandatory\" : false, \"enabled\" : true }, { \"visible\" : true, \"dataName\" : \"dataName\", \"mandatory\" : false, \"enabled\" : true } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /context/{uuid} : Returns the existing session info
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
                    String exampleString = "{ \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /message/{viewUuid}/{messageUuid}
     *
     * @param viewUuid View UUID. (required)
     * @param messageUuid Message UUID. (required)
     * @param messageResult  (required)
     * @return  (status code 200)
     * @see ViewApi#message
     */
    default ResponseEntity<Void> message(UUID viewUuid,
        UUID messageUuid,
        MessageResult messageResult) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /context : Updates an existing ViewContext
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
                    String exampleString = "{ \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
