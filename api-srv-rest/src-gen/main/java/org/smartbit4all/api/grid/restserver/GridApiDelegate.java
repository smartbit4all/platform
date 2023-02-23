package org.smartbit4all.api.grid.restserver;

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
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"view\" : { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, \"accessConfig\" : { \"apiClass\" : \"apiClass\", \"kind\" : \"TABLEDATA\", \"dataUri\" : \"https://openapi-generator.tech\", \"propertyPath\" : \"propertyPath\" }, \"pageSize\" : 5, \"availableViews\" : [ { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] } ], \"page\" : { \"upperBound\" : 6, \"lowerBound\" : 0, \"rows\" : [ { \"data\" : \"{}\", \"id\" : \"id\", \"actions\" : [ \"\", \"\" ] }, { \"data\" : \"{}\", \"id\" : \"id\", \"actions\" : [ \"\", \"\" ] } ] }, \"totalRowCount\" : 1 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
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
