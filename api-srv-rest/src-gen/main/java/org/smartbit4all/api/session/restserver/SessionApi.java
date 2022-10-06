/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (5.3.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package org.smartbit4all.api.session.restserver;

import org.smartbit4all.api.session.bean.ApiError;
import org.smartbit4all.api.session.bean.GetAuthenticationProvidersResponse;
import org.smartbit4all.api.session.bean.RefreshSessionRequest;
import org.smartbit4all.api.session.bean.SessionInfoData;
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
@Api(value = "Session", description = "the Session API")
public interface SessionApi {

    default SessionApiDelegate getDelegate() {
        return new SessionApiDelegate() {};
    }

    /**
     * GET /authenticationProviders : Returns the available authentication providers
     *
     * @return Returns the available authentication providers (status code 200)
     *         or Missing or expired JWT token (status code 400)
     *         or The session does not exists with the given token (status code 404)
     *         or Error occurred while fetching the session data (status code 500)
     */
    @ApiOperation(value = "Returns the available authentication providers", nickname = "getAuthenticationProviders", notes = "", response = GetAuthenticationProvidersResponse.class, tags={ "Session", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Returns the available authentication providers", response = GetAuthenticationProvidersResponse.class),
        @ApiResponse(code = 400, message = "Missing or expired JWT token", response = ApiError.class),
        @ApiResponse(code = 404, message = "The session does not exists with the given token", response = ApiError.class),
        @ApiResponse(code = 500, message = "Error occurred while fetching the session data", response = ApiError.class) })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/authenticationProviders",
        produces = { "application/json" }
    )
    default ResponseEntity<GetAuthenticationProvidersResponse> getAuthenticationProviders() throws Exception {
        return getDelegate().getAuthenticationProviders();
    }


    /**
     * GET /session : Returns the existing session info
     *
     * @return Returns the existing session info (status code 200)
     *         or Missing or expired JWT token (status code 400)
     *         or The session does not exists with the given token (status code 404)
     *         or Error occurred while fetching the session data (status code 500)
     */
    @ApiOperation(value = "Returns the existing session info", nickname = "getSession", notes = "", response = SessionInfoData.class, tags={ "Session", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Returns the existing session info", response = SessionInfoData.class),
        @ApiResponse(code = 400, message = "Missing or expired JWT token", response = ApiError.class),
        @ApiResponse(code = 404, message = "The session does not exists with the given token", response = ApiError.class),
        @ApiResponse(code = 500, message = "Error occurred while fetching the session data", response = ApiError.class) })
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/session",
        produces = { "application/json" }
    )
    default ResponseEntity<SessionInfoData> getSession() throws Exception {
        return getDelegate().getSession();
    }


    /**
     * POST /refresh : Refreshes the session
     *
     * @param refreshSessionRequest  (required)
     * @return Refreshes the expired session with the refresh token (status code 200)
     *         or Missing or expired JWT token (status code 400)
     *         or Error occurred while fetching the session data (status code 500)
     */
    @ApiOperation(value = "Refreshes the session", nickname = "refreshSession", notes = "", response = SessionInfoData.class, tags={ "Session", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Refreshes the expired session with the refresh token", response = SessionInfoData.class),
        @ApiResponse(code = 400, message = "Missing or expired JWT token", response = ApiError.class),
        @ApiResponse(code = 500, message = "Error occurred while fetching the session data", response = ApiError.class) })
    @RequestMapping(
        method = RequestMethod.POST,
        value = "/refresh",
        produces = { "application/json" },
        consumes = { "application/json" }
    )
    default ResponseEntity<SessionInfoData> refreshSession(@ApiParam(value = "", required = true) @Valid @RequestBody RefreshSessionRequest refreshSessionRequest) throws Exception {
        return getDelegate().refreshSession(refreshSessionRequest);
    }


    /**
     * PUT /session : Creates a new session
     *
     * @return The session has started (status code 200)
     *         or Error occurred while fetching the session data (status code 500)
     */
    @ApiOperation(value = "Creates a new session", nickname = "startSession", notes = "", response = SessionInfoData.class, tags={ "Session", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "The session has started", response = SessionInfoData.class),
        @ApiResponse(code = 500, message = "Error occurred while fetching the session data", response = ApiError.class) })
    @RequestMapping(
        method = RequestMethod.PUT,
        value = "/session",
        produces = { "application/json" }
    )
    default ResponseEntity<SessionInfoData> startSession() throws Exception {
        return getDelegate().startSession();
    }

}
