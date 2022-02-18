/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.3.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.smartbit4all.ui.api.navigation.restserver;

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
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Validated
@Api(value = "ViewModel", description = "the ViewModel API")
public interface ViewModelApi {

    default ViewModelApiDelegate getDelegate() {
        return new ViewModelApiDelegate() {};
    }

    /**
     * POST /createViewModel
     *
     * @param orgSmartbit4allUiApiNavigationModelNavigationTarget  (required)
     * @return  (status code 201)
     */
    @ApiOperation(value = "", nickname = "createViewModel", notes = "", response = org.smartbit4all.ui.api.navigation.model.NavigationTarget.class, tags={ "view-model", })
    @ApiResponses(value = { 
        @ApiResponse(code = 201, message = "", response = org.smartbit4all.ui.api.navigation.model.NavigationTarget.class) })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/createViewModel",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<org.smartbit4all.ui.api.navigation.model.NavigationTarget> createViewModel(@ApiParam(value = "", required = true) @Valid @RequestBody org.smartbit4all.ui.api.navigation.model.NavigationTarget orgSmartbit4allUiApiNavigationModelNavigationTarget) throws Exception {
        return getDelegate().createViewModel(orgSmartbit4allUiApiNavigationModelNavigationTarget);
    }


    /**
     * POST /executeCommand/{uuid}
     *
     * @param uuid  (required)
     * @param orgSmartbit4allUiApiNavigationModelCommandData  (required)
     * @return  (status code 200)
     */
    @ApiOperation(value = "", nickname = "executeCommand", notes = "", tags={ "view-model", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "") })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/executeCommand/{uuid}",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> executeCommand(@ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,@ApiParam(value = "", required = true) @Valid @RequestBody org.smartbit4all.ui.api.navigation.model.CommandData orgSmartbit4allUiApiNavigationModelCommandData) throws Exception {
        return getDelegate().executeCommand(uuid, orgSmartbit4allUiApiNavigationModelCommandData);
    }


    /**
     * GET /getData/{uuid}
     *
     * @param uuid  (required)
     * @return  (status code 200)
     */
    @ApiOperation(value = "", nickname = "getData", notes = "", response = Object.class, tags={ "view-model", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "", response = Object.class) })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/getData/{uuid}",
        produces = { "application/json" }
    )
    default ResponseEntity<Object> getData(@ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid) throws Exception {
        return getDelegate().getData(uuid);
    }


    /**
     * POST /setData/{uuid}
     *
     * @param uuid  (required)
     * @param body  (required)
     * @return  (status code 200)
     */
    @ApiOperation(value = "", nickname = "setData", notes = "", tags={ "view-model", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "") })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/setData/{uuid}",
        consumes = { "application/json" }
    )
    default ResponseEntity<Void> setData(@ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid,@ApiParam(value = "", required = true) @Valid @RequestBody Object body) throws Exception {
        return getDelegate().setData(uuid, body);
    }

}