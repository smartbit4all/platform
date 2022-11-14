package org.smartbit4all.api.session.restserver;

import org.smartbit4all.api.session.bean.GetAuthenticationProvidersResponse;
import org.smartbit4all.api.session.bean.SessionInfoData;
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
 * A delegate to be called by the {@link SessionApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface SessionApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /authenticationProviders : Returns the available authentication providers
     *
     * @return Returns the available authentication providers (status code 200)
     *         or Missing JWT token (status code 400)
     *         or The session does not exists with the given token (status code 404)
     *         or Error occured while fetching the session data (status code 500)
     * @see SessionApi#getAuthenticationProviders
     */
    default ResponseEntity<GetAuthenticationProvidersResponse> getAuthenticationProviders() throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"authenticationProviders\" : [ { \"kind\" : \"kind\", \"parameters\" : { \"key\" : \"parameters\" } }, { \"kind\" : \"kind\", \"parameters\" : { \"key\" : \"parameters\" } } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /session : Returns the existing session info
     *
     * @return Returns the existing session info (status code 200)
     *         or Missing JWT token (status code 400)
     *         or The session does not exists with the given token (status code 404)
     *         or Error occured while fetching the session data (status code 500)
     * @see SessionApi#getSession
     */
    default ResponseEntity<SessionInfoData> getSession() throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"expiration\" : \"2000-01-23T04:56:07.000+00:00\", \"locale\" : \"locale\", \"sid\" : \"sid\", \"authentications\" : [ { \"imageContent\" : \"imageContent\", \"imageFormat\" : \"imageFormat\", \"kind\" : \"kind\", \"displayName\" : \"displayName\", \"roles\" : [ \"roles\", \"roles\" ], \"userName\" : \"userName\", \"parameters\" : { \"key\" : \"parameters\" } }, { \"imageContent\" : \"imageContent\", \"imageFormat\" : \"imageFormat\", \"kind\" : \"kind\", \"displayName\" : \"displayName\", \"roles\" : [ \"roles\", \"roles\" ], \"userName\" : \"userName\", \"parameters\" : { \"key\" : \"parameters\" } } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * PUT /session : Creates a new session
     *
     * @return The session has started (status code 200)
     *         or Error during session creation (status code 500)
     * @see SessionApi#startSession
     */
    default ResponseEntity<SessionInfoData> startSession() throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"expiration\" : \"2000-01-23T04:56:07.000+00:00\", \"locale\" : \"locale\", \"sid\" : \"sid\", \"authentications\" : [ { \"imageContent\" : \"imageContent\", \"imageFormat\" : \"imageFormat\", \"kind\" : \"kind\", \"displayName\" : \"displayName\", \"roles\" : [ \"roles\", \"roles\" ], \"userName\" : \"userName\", \"parameters\" : { \"key\" : \"parameters\" } }, { \"imageContent\" : \"imageContent\", \"imageFormat\" : \"imageFormat\", \"kind\" : \"kind\", \"displayName\" : \"displayName\", \"roles\" : [ \"roles\", \"roles\" ], \"userName\" : \"userName\", \"parameters\" : { \"key\" : \"parameters\" } } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
