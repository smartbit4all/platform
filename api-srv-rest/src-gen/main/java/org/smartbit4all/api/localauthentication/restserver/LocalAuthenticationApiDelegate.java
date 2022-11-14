package org.smartbit4all.api.localauthentication.restserver;

import org.smartbit4all.api.localauthentication.bean.LocalAuthenticationLoginRequest;
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
 * A delegate to be called by the {@link LocalAuthenticationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface LocalAuthenticationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /login : Logs in the user with the credentails given.
     *
     * @param localAuthenticationLoginRequest  (required)
     * @return User successfully logged in (status code 200)
     *         or Unable to log in user with the given credentials (status code 400)
     *         or Error during login process (status code 500)
     * @see LocalAuthenticationApi#login
     */
    default ResponseEntity<Void> login(LocalAuthenticationLoginRequest localAuthenticationLoginRequest) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /logout : Logs out the currently logged in user
     *
     * @return User successfully logged out (status code 200)
     *         or The user is not logged in (status code 400)
     *         or Error during logout process (status code 500)
     * @see LocalAuthenticationApi#logout
     */
    default ResponseEntity<Void> logout() throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
