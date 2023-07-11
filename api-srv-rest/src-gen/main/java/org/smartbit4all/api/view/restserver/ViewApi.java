/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.4.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
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
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Api(value = "View", description = "the View API")
public interface ViewApi {

    default ViewApiDelegate getDelegate() {
        return new ViewApiDelegate() {};
    }

    /**
     * POST /context : Creates a new ViewContext
     *
     * @return Context created (status code 200)
     */
    @ApiOperation(
        tags = { "View" },
        value = "Creates a new ViewContext",
        nickname = "createViewContext",
        notes = "",
        response = ViewContextData.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Context created", response = ViewContextData.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/context",
        produces = { "application/json" }
    )
    default ResponseEntity<ViewContextData> createViewContext(
        
    ) throws Exception {
        return getDelegate().createViewContext();
    }


    /**
     * POST /component/{uuid}/data
     * Notification of a data change event.. 
     *
     * @param uuid  (required)
     * @param dataChangeEvent  (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "View" },
        value = "",
        nickname = "dataChanged",
        notes = "Notification of a data change event.. ",
        response = ViewContextChange.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = ViewContextChange.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/component/{uuid}/data",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<ViewContextChange> dataChanged(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @Valid @RequestBody DataChangeEvent dataChangeEvent
    ) throws Exception {
        return getDelegate().dataChanged(uuid, dataChangeEvent);
    }


    /**
     * GET /component/{uuid}/download/{item}
     *
     * @param uuid  (required)
     * @param item  (required)
     * @return  (status code 200)
     *         or The component does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the downloadable item (status code 500)
     */
    @ApiOperation(
        tags = { "View" },
        value = "",
        nickname = "downloadItem",
        notes = "",
        response = org.springframework.core.io.Resource.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = org.springframework.core.io.Resource.class),
        @ApiResponse(code = 404, message = "The component does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error occured while fetching the downloadable item")
    })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/component/{uuid}/download/{item}",
        produces = { "application/octet-stream" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadItem(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("item") String item
    ) throws Exception {
        return getDelegate().downloadItem(uuid, item);
    }


    /**
     * GET /view/{uuid}/download/{item}
     *
     * @param uuid  (required)
     * @param item  (required)
     * @return  (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the downloadable item (status code 500)
     */
    @ApiOperation(
        tags = { "View" },
        value = "",
        nickname = "downloadItemDeprecated",
        notes = "",
        response = org.springframework.core.io.Resource.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = org.springframework.core.io.Resource.class),
        @ApiResponse(code = 404, message = "The context does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error occured while fetching the downloadable item")
    })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/view/{uuid}/download/{item}",
        produces = { "application/octet-stream" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> downloadItemDeprecated(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("item") String item
    ) throws Exception {
        return getDelegate().downloadItemDeprecated(uuid, item);
    }


    /**
     * GET /component/{uuid} : Returns component&#39;s model.
     *
     * @param uuid Component&#39;s unique identifier. (required)
     * @return Returns ComponentModel by unique identifier (status code 200)
     *         or The component does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the component data (status code 500)
     */
    @ApiOperation(
        tags = { "View" },
        value = "Returns component's model.",
        nickname = "getComponentModel",
        notes = "",
        response = ComponentModel.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Returns ComponentModel by unique identifier", response = ComponentModel.class),
        @ApiResponse(code = 404, message = "The component does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error occured while fetching the component data")
    })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/component/{uuid}",
        produces = { "application/json" }
    )
    default ResponseEntity<ComponentModel> getComponentModel(
        @ApiParam(value = "Component's unique identifier.", required = true) @PathVariable("uuid") UUID uuid
    ) throws Exception {
        return getDelegate().getComponentModel(uuid);
    }


    /**
     * GET /view/{uuid}/constraint : Returns the view constraint object belongs to the given view
     *
     * @param uuid View&#39;s unique identifier. (required)
     * @return Returns ViewConstraint object for the view identified by unique identifier (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the constraints data (status code 500)
     */
    @ApiOperation(
        tags = { "View" },
        value = "Returns the view constraint object belongs to the given view",
        nickname = "getViewConstraint",
        notes = "",
        response = ViewConstraint.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Returns ViewConstraint object for the view identified by unique identifier", response = ViewConstraint.class),
        @ApiResponse(code = 404, message = "The context does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error occured while fetching the constraints data")
    })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/view/{uuid}/constraint",
        produces = { "application/json" }
    )
    default ResponseEntity<ViewConstraint> getViewConstraint(
        @ApiParam(value = "View's unique identifier.", required = true) @PathVariable("uuid") UUID uuid
    ) throws Exception {
        return getDelegate().getViewConstraint(uuid);
    }


    /**
     * GET /context/{uuid} : Returns the existing session info
     *
     * @param uuid ViewContext&#39;s unique identifier. (required)
     * @return Returns ViewContext by unique identifier (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the session data (status code 500)
     */
    @ApiOperation(
        tags = { "View" },
        value = "Returns the existing session info",
        nickname = "getViewContext",
        notes = "",
        response = ViewContextData.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Returns ViewContext by unique identifier", response = ViewContextData.class),
        @ApiResponse(code = 404, message = "The context does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error occured while fetching the session data")
    })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/context/{uuid}",
        produces = { "application/json" }
    )
    default ResponseEntity<ViewContextData> getViewContext(
        @ApiParam(value = "ViewContext's unique identifier.", required = true) @PathVariable("uuid") UUID uuid
    ) throws Exception {
        return getDelegate().getViewContext(uuid);
    }


    /**
     * POST /message/{viewUuid}/{messageUuid}
     *
     * @param viewUuid View UUID. (required)
     * @param messageUuid Message UUID. (required)
     * @param messageResult  (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "View" },
        value = "",
        nickname = "message",
        notes = ""
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "")
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/message/{viewUuid}/{messageUuid}",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> message(
        @ApiParam(value = "View UUID.", required = true) @PathVariable("viewUuid") UUID viewUuid,
        @ApiParam(value = "Message UUID.", required = true) @PathVariable("messageUuid") UUID messageUuid,
        @ApiParam(value = "", required = true) @Valid @RequestBody MessageResult messageResult
    ) throws Exception {
        return getDelegate().message(viewUuid, messageUuid, messageResult);
    }


    /**
     * POST /component/{uuid}/action
     * Performs a generic UI action. 
     *
     * @param uuid  (required)
     * @param body  (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "View" },
        value = "",
        nickname = "performAction",
        notes = "Performs a generic UI action. ",
        response = ViewContextChange.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = ViewContextChange.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/component/{uuid}/action",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<ViewContextChange> performAction(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @Valid @RequestBody UiActionRequest body
    ) throws Exception {
        return getDelegate().performAction(uuid, body);
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
     */
    @ApiOperation(
        tags = { "View" },
        value = "",
        nickname = "performWidgetAction",
        notes = "Performs a widget UI action. ",
        response = ViewContextChange.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = ViewContextChange.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/component/{uuid}/{widgetId}/{nodeId}/action",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<ViewContextChange> performWidgetAction(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("widgetId") String widgetId,
        @ApiParam(value = "", required = true) @PathVariable("nodeId") String nodeId,
        @ApiParam(value = "", required = true) @Valid @RequestBody UiActionRequest body
    ) throws Exception {
        return getDelegate().performWidgetAction(uuid, widgetId, nodeId, body);
    }


    /**
     * POST /component/{uuid}/{widgetId}/action
     * Performs a widget UI action. 
     *
     * @param uuid  (required)
     * @param widgetId  (required)
     * @param body  (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "View" },
        value = "",
        nickname = "performWidgetMainAction",
        notes = "Performs a widget UI action. ",
        response = ViewContextChange.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = ViewContextChange.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/component/{uuid}/{widgetId}/action",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<ViewContextChange> performWidgetMainAction(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("widgetId") String widgetId,
        @ApiParam(value = "", required = true) @Valid @RequestBody UiActionRequest body
    ) throws Exception {
        return getDelegate().performWidgetMainAction(uuid, widgetId, body);
    }


    /**
     * PUT /smartlink/{channel}/{uuid} : null
     *
     * @param channel Smartlink&#39;s channel. (required)
     * @param uuid Smartlink&#39;s unique identifier (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "View" },
        value = "null",
        nickname = "showPublishedView",
        notes = ""
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "")
    })
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/smartlink/{channel}/{uuid}"
    )
    default ResponseEntity<Void> showPublishedView(
        @ApiParam(value = "Smartlink's channel.", required = true) @PathVariable("channel") String channel,
        @ApiParam(value = "Smartlink's unique identifier", required = true) @PathVariable("uuid") UUID uuid
    ) throws Exception {
        return getDelegate().showPublishedView(channel, uuid);
    }


    /**
     * PUT /context : Updates an existing ViewContext
     *
     * @param viewContextUpdate  (required)
     * @return Context updated (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error during context update (status code 500)
     */
    @ApiOperation(
        tags = { "View" },
        value = "Updates an existing ViewContext",
        nickname = "updateViewContext",
        notes = "",
        response = ViewContextData.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "Context updated", response = ViewContextData.class),
        @ApiResponse(code = 404, message = "The context does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error during context update")
    })
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/context",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<ViewContextData> updateViewContext(
        @ApiParam(value = "", required = true) @Valid @RequestBody ViewContextUpdate viewContextUpdate
    ) throws Exception {
        return getDelegate().updateViewContext(viewContextUpdate);
    }

}
