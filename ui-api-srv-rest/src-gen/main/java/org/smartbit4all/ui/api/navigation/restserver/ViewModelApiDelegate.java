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
    default ResponseEntity<org.smartbit4all.ui.api.navigation.model.ViewModelData> createViewModel(org.smartbit4all.ui.api.navigation.model.NavigationTarget orgSmartbit4allUiApiNavigationModelNavigationTarget) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { \"key\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /download/{vmUuid}/{dataIdentifier}
     *
     * @param vmUuid  (required)
     * @param dataIdentifier  (required)
     * @return Data in given ViewModel (vmUuid) with the given dataUuid. (status code 200)
     * @see ViewModelApi#download
     */
    default ResponseEntity<org.springframework.core.io.Resource> download(UUID vmUuid,
        String dataIdentifier) throws Exception {
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
    default ResponseEntity<org.smartbit4all.ui.api.navigation.model.CommandResult> executeCommand(UUID uuid,
        org.smartbit4all.ui.api.navigation.model.CommandData orgSmartbit4allUiApiNavigationModelCommandData) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"view\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { \"key\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"messageToOpen\" : { \"possibleResults\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"selectResult\" : { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, \"viewModelUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uiToOpen\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * GET /getModel/{uuid}
     *
     * @param uuid  (required)
     * @return  (status code 200)
     * @see ViewModelApi#getModel
     */
    default ResponseEntity<org.smartbit4all.ui.api.navigation.model.ViewModelData> getModel(UUID uuid) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { \"key\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /message/{uuid}
     *
     * @param uuid Message UUID. (required)
     * @param orgSmartbit4allUiApiNavigationModelMessageResult  (required)
     * @return  (status code 200)
     * @see ViewModelApi#message
     */
    default ResponseEntity<org.smartbit4all.ui.api.navigation.model.CommandResult> message(UUID uuid,
        org.smartbit4all.ui.api.navigation.model.MessageResult orgSmartbit4allUiApiNavigationModelMessageResult) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"view\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { \"key\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"messageToOpen\" : { \"possibleResults\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"selectResult\" : { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, \"viewModelUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uiToOpen\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /setModel/{uuid}
     *
     * @param uuid  (required)
     * @param body  (required)
     * @return  (status code 200)
     * @see ViewModelApi#setModel
     */
    default ResponseEntity<Void> setModel(UUID uuid,
        Object body) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /upload : Executes a command similar to executeCommand, but appends a BinaryData to it params
     *
     * @param uuid  (optional)
     * @param command  (optional)
     * @param content  (optional)
     * @return  (status code 200)
     * @see ViewModelApi#upload
     */
    default ResponseEntity<org.smartbit4all.ui.api.navigation.model.CommandResult> upload(String uuid,
        org.smartbit4all.ui.api.navigation.model.CommandData command,
        MultipartFile content) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"view\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { \"key\" : { \"path\" : \"path\", \"navigationTarget\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } }, \"children\" : { }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" } }, \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"messageToOpen\" : { \"possibleResults\" : [ { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" } ], \"selectResult\" : { \"code\" : \"code\", \"icon\" : \"icon\", \"label\" : \"label\" }, \"viewModelUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"header\" : \"header\", \"text\" : \"text\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }, \"uiToOpen\" : { \"viewName\" : \"viewName\", \"fullSize\" : false, \"icon\" : \"icon\", \"objectUri\" : \"https://openapi-generator.tech\", \"title\" : \"title\", \"uuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"parameters\" : { \"key\" : \"{}\" } } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
