package org.smartbit4all.ui.api.grid.restserver;

import org.smartbit4all.api.grid.bean.GridModel;
import java.util.UUID;
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
 * A delegate to be called by the {@link GridApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface GridApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /grid/{uuid}/{gridIdentifier}/load
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @return  (status code 200)
     * @see GridApi#load
     */
    default ResponseEntity<GridModel> load(UUID uuid,
        String gridIdentifier) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /grid/{uuid}/{gridIdentifier}/performAction/{actionIdentifier}
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @param actionIdentifier  (required)
     * @return OK (status code 200)
     *         or NOK (status code 404)
     * @see GridApi#performAction
     */
    default ResponseEntity<Void> performAction(UUID uuid,
        String gridIdentifier,
        String actionIdentifier) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /grid/{uuid}/{gridIdentifier}/page/{offset}/{limit}
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @param offset  (required)
     * @param limit  (required)
     * @return  (status code 200)
     * @see GridApi#setPage
     */
    default ResponseEntity<Void> setPage(UUID uuid,
        String gridIdentifier,
        String offset,
        String limit) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
