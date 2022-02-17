package org.smartbit4all.ui.api.navigation.restserver;

import java.util.UUID;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A delegate to be called by the {@link ViewModelApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface ViewModelApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /createViewModel
     *
     * @param orgSmartbit4allUiApiNavigationModelNavigationTarget  (required)
     * @return  (status code 201)
     * @see ViewModelApi#createViewModel
     */
    default ResponseEntity<org.smartbit4all.ui.api.navigation.model.NavigationTarget> createViewModel(org.smartbit4all.ui.api.navigation.model.NavigationTarget orgSmartbit4allUiApiNavigationModelNavigationTarget) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /executeCommand/{uuid}
     *
     * @param uuid  (required)
     * @param orgSmartbit4allUiApiNavigationModelCommandData  (required)
     * @return  (status code 200)
     * @see ViewModelApi#executeCommand
     */
    default ResponseEntity<Void> executeCommand(UUID uuid,
        org.smartbit4all.ui.api.navigation.model.CommandData orgSmartbit4allUiApiNavigationModelCommandData) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /getData/{uuid}
     *
     * @param uuid  (required)
     * @return  (status code 200)
     * @see ViewModelApi#getData
     */
    default ResponseEntity<Object> getData(UUID uuid) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /setData/{uuid}
     *
     * @param uuid  (required)
     * @param body  (required)
     * @return  (status code 200)
     * @see ViewModelApi#setData
     */
    default ResponseEntity<Void> setData(UUID uuid,
        Object body) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
