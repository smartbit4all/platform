package org.smartbit4all.api.view.restserver;

import org.smartbit4all.api.view.bean.ComponentModel;
import org.smartbit4all.api.view.bean.DataChangeEvent;
import org.smartbit4all.api.view.bean.MessageResult;
import java.util.UUID;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.smartbit4all.api.view.bean.ViewContextData;
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
    default ResponseEntity<ViewContextData> createViewContext() throws Exception {
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
     * POST /component/{uuid}/data
     * Notification of a data change event.. 
     *
     * @param uuid  (required)
     * @param dataChangeEvent  (required)
     * @return  (status code 200)
     * @see ViewApi#dataChanged
     */
    default ResponseEntity<ViewContextChange> dataChanged(UUID uuid,
        DataChangeEvent dataChangeEvent) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"changes\" : [ { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" }, { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" } ], \"viewContext\" : { \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /component/{uuid}/download/{item}
     *
     * @param uuid  (required)
     * @param item  (required)
     * @return  (status code 200)
     *         or The component does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the downloadable item (status code 500)
     * @see ViewApi#downloadItem
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadItem(UUID uuid,
        String item) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /view/{uuid}/download/{item}
     *
     * @param uuid  (required)
     * @param item  (required)
     * @return  (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the downloadable item (status code 500)
     * @see ViewApi#downloadItemDeprecated
     */
    default ResponseEntity<org.springframework.core.io.Resource> downloadItemDeprecated(UUID uuid,
        String item) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /component/{uuid} : Returns component&#39;s model.
     *
     * @param uuid Component&#39;s unique identifier. (required)
     * @return Returns ComponentModel by unique identifier (status code 200)
     *         or The component does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the component data (status code 500)
     * @see ViewApi#getComponentModel
     */
    default ResponseEntity<ComponentModel> getComponentModel(UUID uuid) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"data\" : \"{}\", \"name\" : \"name\", \"valueSets\" : { \"key\" : \"\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"constraints\" : [ { \"visible\" : true, \"valueSet\" : \"\", \"dataName\" : \"dataName\", \"mandatory\" : false, \"enabled\" : true }, { \"visible\" : true, \"valueSet\" : \"\", \"dataName\" : \"dataName\", \"mandatory\" : false, \"enabled\" : true } ], \"layouts\" : { \"key\" : \"\" }, \"actions\" : [ { \"confirm\" : false, \"path\" : \"path\", \"identifier\" : \"identifier\", \"code\" : \"code\", \"submit\" : true, \"params\" : { \"key\" : \"{}\" } }, { \"confirm\" : false, \"path\" : \"path\", \"identifier\" : \"identifier\", \"code\" : \"code\", \"submit\" : true, \"params\" : { \"key\" : \"{}\" } } ], \"widgetModels\" : { \"key\" : \"{}\" } }";
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
                    String exampleString = "{ \"componentConstraints\" : [ { \"visible\" : true, \"valueSet\" : \"\", \"dataName\" : \"dataName\", \"mandatory\" : false, \"enabled\" : true }, { \"visible\" : true, \"valueSet\" : \"\", \"dataName\" : \"dataName\", \"mandatory\" : false, \"enabled\" : true } ] }";
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
    default ResponseEntity<ViewContextData> getViewContext(UUID uuid) throws Exception {
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
     * POST /component/{uuid}/action
     * Performs a generic UI action. 
     *
     * @param uuid  (required)
     * @param body  (required)
     * @return  (status code 200)
     * @see ViewApi#performAction
     */
    default ResponseEntity<ViewContextChange> performAction(UUID uuid,
        UiActionRequest body) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"changes\" : [ { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" }, { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" } ], \"viewContext\" : { \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /component/{uuid}/{widgetId}/{nodeId}/action
     * Performs a widget UI action. 
     *
     * @param uuid  (required)
     * @param widgetId  (required)
     * @param nodeId  (required)
     * @param body  (required)
     * @return  (status code 200)
     * @see ViewApi#performWidgetAction
     */
    default ResponseEntity<ViewContextChange> performWidgetAction(UUID uuid,
        String widgetId,
        String nodeId,
        UiActionRequest body) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"changes\" : [ { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" }, { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" } ], \"viewContext\" : { \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /component/{uuid}/{widgetId}/action
     * Performs a widget UI action. 
     *
     * @param uuid  (required)
     * @param widgetId  (required)
     * @param body  (required)
     * @return  (status code 200)
     * @see ViewApi#performWidgetMainAction
     */
    default ResponseEntity<ViewContextChange> performWidgetMainAction(UUID uuid,
        String widgetId,
        UiActionRequest body) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"changes\" : [ { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" }, { \"path\" : \"path\", \"changes\" : { \"key\" : \"{}\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"value\" : \"{}\" } ], \"viewContext\" : { \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"views\" : [ { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, { \"containerUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"viewName\" : \"viewName\", \"message\" : { \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"options\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } ] } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /smartlink/{channel}/{uuid} : null
     *
     * @param channel Smartlink&#39;s channel. (required)
     * @param uuid Smartlink&#39;s unique identifier (required)
     * @return  (status code 200)
     * @see ViewApi#showPublishedView
     */
    default ResponseEntity<Void> showPublishedView(String channel,
        UUID uuid) throws Exception {
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
    default ResponseEntity<ViewContextData> updateViewContext(ViewContextUpdate viewContextUpdate) throws Exception {
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
