/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.4.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.smartbit4all.api.navigation.restserver;

import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationResponse;
import java.net.URI;
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
@Api(value = "Navigation", description = "the Navigation API")
public interface NavigationApi {

    default NavigationApiDelegate getDelegate() {
        return new NavigationApiDelegate() {};
    }

    /**
     * POST /entry : Retrieve the entries from the navigations.
     *
     * @param entryMetaUri  (required)
     * @param objectUri  (required)
     * @return The navigation entry if we found it or null if missing (status code 200)
     */
    @ApiOperation(
        tags = { "Navigation" },
        value = "Retrieve the entries from the navigations.",
        nickname = "getEntry",
        notes = "",
        response = NavigationEntry.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "The navigation entry if we found it or null if missing", response = NavigationEntry.class)
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/entry",
        produces = { "application/json" },
        consumes = { "application/x-www-form-urlencoded" }
    )
    default ResponseEntity<NavigationEntry> getEntry(
        @ApiParam(value = "", required = true) @Valid @RequestPart(value = "entryMetaUri", required = true) URI entryMetaUri,
        @ApiParam(value = "", required = true) @Valid @RequestPart(value = "objectUri", required = true) URI objectUri
    ) throws Exception {
        return getDelegate().getEntry(entryMetaUri, objectUri);
    }


    /**
     * POST /navigate : Queries all data sources to populate the associations starting from the given entry.
     *
     * @param objectUri The URI of the api object that is the starting point of the navigation. It must be a valid URI that can be the starting point of the associations we provided. (required)
     * @param associationMetaUris The list of associations to identify the direction we want to navigate. If we skip this parameter (null) then we will have all the associations defined in the meta (optional)
     * @return The map of the references by the URI of association meta we passed in the associations parameter. (status code 200)
     */
    @ApiOperation(
        tags = { "Navigation" },
        value = "Queries all data sources to populate the associations starting from the given entry.",
        nickname = "navigate",
        notes = "",
        response = NavigationResponse.class,
        responseContainer = "List"
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "The map of the references by the URI of association meta we passed in the associations parameter.", response = NavigationResponse.class, responseContainer = "List")
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/navigate",
        produces = { "application/json" },
        consumes = { "application/x-www-form-urlencoded" }
    )
    default ResponseEntity<List<NavigationResponse>> navigate(
        @ApiParam(value = "The URI of the api object that is the starting point of the navigation. It must be a valid URI that can be the starting point of the associations we provided.", required = true) @Valid @RequestPart(value = "objectUri", required = true) URI objectUri,
        @ApiParam(value = "The list of associations to identify the direction we want to navigate. If we skip this parameter (null) then we will have all the associations defined in the meta") @Valid @RequestPart(value = "associationMetaUris", required = false) List<URI> associationMetaUris
    ) throws Exception {
        return getDelegate().navigate(objectUri, associationMetaUris);
    }

}
