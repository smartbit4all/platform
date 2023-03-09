/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.4.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.smartbit4all.api.grid.restserver;

import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import java.util.UUID;
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
@Api(value = "Grid", description = "the Grid API")
public interface GridApi {

    default GridApiDelegate getDelegate() {
        return new GridApiDelegate() {};
    }

    /**
     * POST /grid/{uuid}/{gridIdentifier}/load
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "grid" },
        value = "",
        nickname = "load",
        notes = "",
        response = GridModel.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = GridModel.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/grid/{uuid}/{gridIdentifier}/load",
        produces = { "application/json" }
    )
    default ResponseEntity<GridModel> load(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("gridIdentifier") String gridIdentifier
    ) throws Exception {
        return getDelegate().load(uuid, gridIdentifier);
    }


    /**
     * POST /grid/{uuid}/{gridIdentifier}/performAction/{actionIdentifier}
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @param actionIdentifier  (required)
     * @return OK (status code 200)
     *         or NOK (status code 404)
     */
    @ApiOperation(
        tags = { "grid" },
        value = "",
        nickname = "performAction",
        notes = ""
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "NOK")
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/grid/{uuid}/{gridIdentifier}/performAction/{actionIdentifier}"
    )
    default ResponseEntity<Void> performAction(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("gridIdentifier") String gridIdentifier,
        @ApiParam(value = "", required = true) @PathVariable("actionIdentifier") String actionIdentifier
    ) throws Exception {
        return getDelegate().performAction(uuid, gridIdentifier, actionIdentifier);
    }


    /**
     * POST /grid/{uuid}/{gridIdentifier}/page/{offset}/{limit}
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @param offset  (required)
     * @param limit  (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "grid" },
        value = "",
        nickname = "setPage",
        notes = ""
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "")
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/grid/{uuid}/{gridIdentifier}/page/{offset}/{limit}"
    )
    default ResponseEntity<Void> setPage(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("gridIdentifier") String gridIdentifier,
        @ApiParam(value = "", required = true) @PathVariable("offset") String offset,
        @ApiParam(value = "", required = true) @PathVariable("limit") String limit
    ) throws Exception {
        return getDelegate().setPage(uuid, gridIdentifier, offset, limit);
    }


    /**
     * POST /grid/{uuid}/{gridIdentifier}/update
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @param gridUpdateData  (required)
     * @return  (status code 200)
     */
    @ApiOperation(
        tags = { "grid" },
        value = "",
        nickname = "update",
        notes = "",
        response = GridModel.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "", response = GridModel.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/grid/{uuid}/{gridIdentifier}/update",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<GridModel> update(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,
        @ApiParam(value = "", required = true) @PathVariable("gridIdentifier") String gridIdentifier,
        @ApiParam(value = "", required = true) @Valid @RequestBody GridUpdateData gridUpdateData
    ) throws Exception {
        return getDelegate().update(uuid, gridIdentifier, gridUpdateData);
    }

}
