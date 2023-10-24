package org.smartbit4all.api.filterexpression.restserver;

import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderUiModel;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
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
 * A delegate to be called by the {@link FilterApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface FilterApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/addBracket
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionBuilderUiModel  (required)
     * @return  (status code 200)
     * @see FilterApi#addBracket
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> addBracket(UUID uuid,
        String filterIdentifier,
        FilterExpressionBuilderUiModel filterExpressionBuilderUiModel) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/addFilterExpression
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionBuilderField  (required)
     * @return  (status code 200)
     * @see FilterApi#addFilterExpression
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> addFilterExpression(UUID uuid,
        String filterIdentifier,
        FilterExpressionBuilderField filterExpressionBuilderField) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/filterGroups
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionFieldList  (required)
     * @return  (status code 200)
     * @see FilterApi#filterGroups
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> filterGroups(UUID uuid,
        String filterIdentifier,
        FilterExpressionFieldList filterExpressionFieldList) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/removeFilterExpression
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionField  (required)
     * @return  (status code 200)
     * @see FilterApi#removeFilterExpression
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> removeFilterExpression(UUID uuid,
        String filterIdentifier,
        FilterExpressionField filterExpressionField) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/resetFilterWorkspace
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionBuilderUiModel  (required)
     * @return  (status code 200)
     * @see FilterApi#resetFilterWorkspace
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> resetFilterWorkspace(UUID uuid,
        String filterIdentifier,
        FilterExpressionBuilderUiModel filterExpressionBuilderUiModel) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/selectField
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionField  (required)
     * @return  (status code 200)
     * @see FilterApi#selectField
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> selectField(UUID uuid,
        String filterIdentifier,
        FilterExpressionField filterExpressionField) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/update
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionBuilderUiModel  (required)
     * @return  (status code 200)
     * @see FilterApi#update
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> update(UUID uuid,
        String filterIdentifier,
        FilterExpressionBuilderUiModel filterExpressionBuilderUiModel) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /filter/{uuid}/{filterIdentifier}/updateFilterExpression
     *
     * @param uuid  (required)
     * @param filterIdentifier  (required)
     * @param filterExpressionField  (required)
     * @return  (status code 200)
     * @see FilterApi#updateFilterExpression
     */
    default ResponseEntity<FilterExpressionBuilderUiModel> updateFilterExpression(UUID uuid,
        String filterIdentifier,
        FilterExpressionField filterExpressionField) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"groupFilter\" : \"\", \"selectedField\" : \"\", \"showGroups\" : false, \"possibleActions\" : [ \"\", \"\" ], \"selectedFieldEditor\" : { \"possibleActions\" : [ \"\", \"\" ], \"layoutDef\" : \"\" }, \"selectUiAction\" : \"\", \"filterGroupsAction\" : \"\", \"model\" : \"\", \"deselectUiAction\" : \"\", \"groupFilterAction\" : \"\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
