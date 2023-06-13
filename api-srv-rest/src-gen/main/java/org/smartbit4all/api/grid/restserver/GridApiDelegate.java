package org.smartbit4all.api.grid.restserver;

import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewContextChange;
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
     * POST /grid/{uuid}/{gridId}/{rowId}/expand
     *
     * @param uuid  (required)
     * @param gridId  (required)
     * @param rowId  (required)
     * @return  (status code 200)
     * @see GridApi#expand
     */
    default ResponseEntity<Object> expand(UUID uuid,
        String gridId,
        String rowId) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

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
                    String exampleString = "{ \"view\" : { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, \"pageSizeOptions\" : [ 2, 2 ], \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"allRowsSelected\" : true, \"accessConfig\" : { \"apiClass\" : \"apiClass\", \"kind\" : \"TABLEDATA\", \"dataUri\" : \"https://openapi-generator.tech\", \"propertyPath\" : \"propertyPath\" }, \"pageSize\" : 5, \"availableViews\" : [ { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] } ], \"page\" : { \"upperBound\" : 6, \"lowerBound\" : 0, \"rows\" : [ { \"data\" : \"{}\", \"selectable\" : true, \"id\" : \"id\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"selectable\" : true, \"id\" : \"id\", \"actions\" : [ \"\", \"\" ], \"selected\" : true } ] }, \"totalRowCount\" : 1, \"selectedRowCount\" : 5, \"defaultRowActions\" : [ \"defaultRowActions\", \"defaultRowActions\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /grid/{uuid}/{gridId}/{rowId}/select
     *
     * @param uuid  (required)
     * @param gridId  (required)
     * @param rowId  (required)
     * @param selected  (optional)
     * @return  (status code 200)
     * @see GridApi#select
     */
    default ResponseEntity<ViewContextChange> select(UUID uuid,
        String gridId,
        String rowId,
        Boolean selected) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /grid/{uuid}/{gridId}/select
     *
     * @param uuid  (required)
     * @param gridId  (required)
     * @param selected  (optional)
     * @return  (status code 200)
     * @see GridApi#selectAll
     */
    default ResponseEntity<ViewContextChange> selectAll(UUID uuid,
        String gridId,
        Boolean selected) throws Exception {
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

    /**
     * POST /grid/{uuid}/{gridIdentifier}/update
     *
     * @param uuid  (required)
     * @param gridIdentifier  (required)
     * @param gridUpdateData  (required)
     * @return  (status code 200)
     * @see GridApi#update
     */
    default ResponseEntity<GridModel> update(UUID uuid,
        String gridIdentifier,
        GridUpdateData gridUpdateData) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"view\" : { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, \"pageSizeOptions\" : [ 2, 2 ], \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"allRowsSelected\" : true, \"accessConfig\" : { \"apiClass\" : \"apiClass\", \"kind\" : \"TABLEDATA\", \"dataUri\" : \"https://openapi-generator.tech\", \"propertyPath\" : \"propertyPath\" }, \"pageSize\" : 5, \"availableViews\" : [ { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"kind\" : \"TABLE\", \"columns\" : [ { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" }, { \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"label\" : \"label\" } ], \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"label\" : \"label\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] } ], \"page\" : { \"upperBound\" : 6, \"lowerBound\" : 0, \"rows\" : [ { \"data\" : \"{}\", \"selectable\" : true, \"id\" : \"id\", \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"selectable\" : true, \"id\" : \"id\", \"actions\" : [ \"\", \"\" ], \"selected\" : true } ] }, \"totalRowCount\" : 1, \"selectedRowCount\" : 5, \"defaultRowActions\" : [ \"defaultRowActions\", \"defaultRowActions\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
