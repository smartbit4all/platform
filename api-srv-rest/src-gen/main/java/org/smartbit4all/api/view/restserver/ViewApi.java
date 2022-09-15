/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.3.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.smartbit4all.api.view.restserver;

import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewContext;
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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
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
    @ApiOperation(value = "Creates a new ViewContext", nickname = "createViewContext", notes = "", response = ViewContext.class, tags={ "View", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Context created", response = ViewContext.class) })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/context",
        produces = { "application/json" }
    )
    default ResponseEntity<ViewContext> createViewContext() throws Exception {
        return getDelegate().createViewContext();
    }


    /**
     * GET /context : Returns the existing session info
     *
     * @param uuid ViewContext&#39;s unique identifier. (required)
     * @return Returns ViewContext by unique identifier (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error occured while fetching the session data (status code 500)
     */
    @ApiOperation(value = "Returns the existing session info", nickname = "getViewContext", notes = "", response = ViewContext.class, tags={ "View", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Returns ViewContext by unique identifier", response = ViewContext.class),
        @ApiResponse(code = 404, message = "The context does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error occured while fetching the session data") })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/context",
        produces = { "application/json" }
    )
    default ResponseEntity<ViewContext> getViewContext(@ApiParam(value = "ViewContext's unique identifier.", required = true) @PathVariable("uuid") UUID uuid) throws Exception {
        return getDelegate().getViewContext(uuid);
    }


    /**
     * PUT /context : Updates an existing ViewContext
     *
     * @param viewContextUpdate  (required)
     * @return Context updated (status code 200)
     *         or The context does not exists with the given uuid (status code 404)
     *         or Error during context update (status code 500)
     */
    @ApiOperation(value = "Updates an existing ViewContext", nickname = "updateViewContext", notes = "", response = ViewContext.class, tags={ "View", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Context updated", response = ViewContext.class),
        @ApiResponse(code = 404, message = "The context does not exists with the given uuid"),
        @ApiResponse(code = 500, message = "Error during context update") })
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/context",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<ViewContext> updateViewContext(@ApiParam(value = "", required = true) @Valid @RequestBody ViewContextUpdate viewContextUpdate) throws Exception {
        return getDelegate().updateViewContext(viewContextUpdate);
    }

}
