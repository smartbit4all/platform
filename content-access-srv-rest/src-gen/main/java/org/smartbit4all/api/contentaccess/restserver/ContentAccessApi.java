/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.4.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.smartbit4all.api.contentaccess.restserver;

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
@Api(value = "ContentAccess", description = "the ContentAccess API")
public interface ContentAccessApi {

    default ContentAccessApiDelegate getDelegate() {
        return new ContentAccessApiDelegate() {};
    }

    /**
     * GET /download/{uuid}
     *
     * @param uuid  (required)
     * @return File with the given uuid. (status code 200)
     */
    @ApiOperation(
        tags = { "ContentAccess" },
        value = "",
        nickname = "download",
        notes = "",
        response = org.springframework.core.io.Resource.class
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "File with the given uuid.", response = org.springframework.core.io.Resource.class)
    })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/download/{uuid}",
        produces = { "application/octet-stream" }
    )
    default ResponseEntity<org.springframework.core.io.Resource> download(
        @ApiParam(value = "", required = true) @PathVariable("uuid") UUID uuid
    ) throws Exception {
        return getDelegate().download(uuid);
    }


    /**
     * POST /upload
     *
     * @param uuid  (optional)
     * @param file  (optional)
     * @return The file was successfully uploaded. (status code 200)
     */
    @ApiOperation(
        tags = { "ContentAccess" },
        value = "",
        nickname = "upload",
        notes = ""
    )
    @ApiResponses({
        @ApiResponse(code = 200, message = "The file was successfully uploaded.")
    })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/upload",
        consumes = { "multipart/form-data" }
    )
    default ResponseEntity<Void> upload(
        @ApiParam(value = "") @Valid @RequestPart(value = "uuid", required = false) UUID uuid,
        @ApiParam(value = "") @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {
        return getDelegate().upload(uuid, file);
    }

}
