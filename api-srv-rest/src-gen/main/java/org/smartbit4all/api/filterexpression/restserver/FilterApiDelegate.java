package org.smartbit4all.api.filterexpression.restserver;

import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import java.util.UUID;
import org.smartbit4all.api.view.bean.UiActionRequest;
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
 * A delegate to be called by the {@link FilterApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface FilterApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /filter/{uuid}/{filterIdentifier}/load
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @return  (status code 200)
     * @see FilterApi#load
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> load(UUID uuid,
        String filterIdentifier) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"extarnalDatabase\" : false, \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"readOnly\" : false, \"type\" : \"SIMPLE\", \"selectedField\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\", \"config\" : { \"availableActions\" : [ \"availableActions\", \"availableActions\" ], \"extarnalDatabase\" : false, \"readOnly\" : false } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/performWidgetAction
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param body  (required)
     * @return  (status code 200)
     * @see FilterApi#performWidgetAction
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> performWidgetAction(UUID uuid,
        String filterIdentifier,
        UiActionRequest body) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"extarnalDatabase\" : false, \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"readOnly\" : false, \"type\" : \"SIMPLE\", \"selectedField\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\", \"config\" : { \"availableActions\" : [ \"availableActions\", \"availableActions\" ], \"extarnalDatabase\" : false, \"readOnly\" : false } }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
