package org.smartbit4all.api.grid.restserver;

import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.grid.bean.GridSelectionChange;
import org.smartbit4all.api.grid.bean.GridUpdateData;
import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewContextChange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

/**
 * A delegate to be called by the {@link GridApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
public interface GridApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /grid/{uuid}/{gridId}/{rowId}/expand : 
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
     * POST /grid/{uuid}/{gridIdentifier}/load : 
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
                    String exampleString = "{ \"identifier\" : \"identifier\", \"pageSizeOptions\" : [ 7, 7 ], \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"pageSize\" : 2, \"availableViews\" : [ { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"selectionType\" : \"row\", \"exportDescriptor\" : { \"isExportable\" : true, \"columnsToExport\" : [ \"columnsToExport\", \"columnsToExport\" ], \"uiActionDescriptor\" : \"\", \"exportMimeType\" : \"exportMimeType\", \"buttonToolbar\" : \"buttonToolbar\" }, \"kind\" : \"TABLE\", \"columns\" : [ { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false }, { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false } ], \"showEditColumns\" : true, \"highlightProperty\" : \"highlightProperty\", \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"highlightClass\" : \"highlightClass\", \"label\" : \"label\", \"selectionMode\" : \"none\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"selectionType\" : \"row\", \"exportDescriptor\" : { \"isExportable\" : true, \"columnsToExport\" : [ \"columnsToExport\", \"columnsToExport\" ], \"uiActionDescriptor\" : \"\", \"exportMimeType\" : \"exportMimeType\", \"buttonToolbar\" : \"buttonToolbar\" }, \"kind\" : \"TABLE\", \"columns\" : [ { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false }, { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false } ], \"showEditColumns\" : true, \"highlightProperty\" : \"highlightProperty\", \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"highlightClass\" : \"highlightClass\", \"label\" : \"label\", \"selectionMode\" : \"none\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] } ], \"defaultRowActions\" : [ \"defaultRowActions\", \"defaultRowActions\" ], \"title\" : \"title\", \"view\" : { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"selectionType\" : \"row\", \"exportDescriptor\" : { \"isExportable\" : true, \"columnsToExport\" : [ \"columnsToExport\", \"columnsToExport\" ], \"uiActionDescriptor\" : \"\", \"exportMimeType\" : \"exportMimeType\", \"buttonToolbar\" : \"buttonToolbar\" }, \"kind\" : \"TABLE\", \"columns\" : [ { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false }, { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false } ], \"showEditColumns\" : true, \"highlightProperty\" : \"highlightProperty\", \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"highlightClass\" : \"highlightClass\", \"label\" : \"label\", \"selectionMode\" : \"none\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, \"pageIndex\" : 5, \"qualifier\" : \"qualifier\", \"allRowsSelected\" : true, \"accessConfig\" : { \"idProperty\" : \"idProperty\", \"apiClass\" : \"apiClass\", \"kind\" : \"TABLEDATA\", \"dataUri\" : \"https://openapi-generator.tech\", \"identifierPath\" : [ \"identifierPath\", \"identifierPath\" ], \"parentIdProperty\" : \"parentIdProperty\" }, \"page\" : { \"upperBound\" : 6, \"lowerBound\" : 0, \"rows\" : [ { \"parent\" : \"parent\", \"data\" : \"{}\", \"children\" : [ \"children\", \"children\" ], \"selectable\" : true, \"style\" : \"\", \"id\" : \"id\", \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"columnActions\" : { \"key\" : [ \"columnActions\", \"columnActions\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"parent\" : \"parent\", \"data\" : \"{}\", \"children\" : [ \"children\", \"children\" ], \"selectable\" : true, \"style\" : \"\", \"id\" : \"id\", \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"columnActions\" : { \"key\" : [ \"columnActions\", \"columnActions\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ] }, \"totalRowCount\" : 1, \"selectedRowCount\" : 5, \"paginator\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /grid/{uuid}/{gridId}/{rowId}/select : 
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
     * POST /grid/{uuid}/{gridId}/select : 
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
     * POST /grid/{uuid}/{gridId}/selects : 
     *
     * @param uuid  (required)
     * @param gridId  (required)
     * @param gridSelectionChange  (required)
     * @return  (status code 200)
     * @see GridApi#selectRows
     */
    default ResponseEntity<ViewContextChange> selectRows(UUID uuid,
        String gridId,
        GridSelectionChange gridSelectionChange) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /grid/{uuid}/{gridIdentifier}/page/{offset}/{limit} : 
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
     * POST /grid/{uuid}/{gridIdentifier}/update : 
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
                    String exampleString = "{ \"identifier\" : \"identifier\", \"pageSizeOptions\" : [ 7, 7 ], \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"pageSize\" : 2, \"availableViews\" : [ { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"selectionType\" : \"row\", \"exportDescriptor\" : { \"isExportable\" : true, \"columnsToExport\" : [ \"columnsToExport\", \"columnsToExport\" ], \"uiActionDescriptor\" : \"\", \"exportMimeType\" : \"exportMimeType\", \"buttonToolbar\" : \"buttonToolbar\" }, \"kind\" : \"TABLE\", \"columns\" : [ { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false }, { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false } ], \"showEditColumns\" : true, \"highlightProperty\" : \"highlightProperty\", \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"highlightClass\" : \"highlightClass\", \"label\" : \"label\", \"selectionMode\" : \"none\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"selectionType\" : \"row\", \"exportDescriptor\" : { \"isExportable\" : true, \"columnsToExport\" : [ \"columnsToExport\", \"columnsToExport\" ], \"uiActionDescriptor\" : \"\", \"exportMimeType\" : \"exportMimeType\", \"buttonToolbar\" : \"buttonToolbar\" }, \"kind\" : \"TABLE\", \"columns\" : [ { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false }, { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false } ], \"showEditColumns\" : true, \"highlightProperty\" : \"highlightProperty\", \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"highlightClass\" : \"highlightClass\", \"label\" : \"label\", \"selectionMode\" : \"none\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] } ], \"defaultRowActions\" : [ \"defaultRowActions\", \"defaultRowActions\" ], \"title\" : \"title\", \"view\" : { \"orderByList\" : [ \"\", \"\" ], \"descriptor\" : { \"selectionType\" : \"row\", \"exportDescriptor\" : { \"isExportable\" : true, \"columnsToExport\" : [ \"columnsToExport\", \"columnsToExport\" ], \"uiActionDescriptor\" : \"\", \"exportMimeType\" : \"exportMimeType\", \"buttonToolbar\" : \"buttonToolbar\" }, \"kind\" : \"TABLE\", \"columns\" : [ { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false }, { \"alwaysHidden\" : false, \"propertyName\" : \"propertyName\", \"typeClass\" : \"typeClass\", \"style\" : \"\", \"label\" : \"label\", \"sortOrderPropertyName\" : \"sortOrderPropertyName\", \"typeFormat\" : \"typeFormat\", \"alwaysShow\" : false, \"contentType\" : \"text\", \"hideLabel\" : false } ], \"showEditColumns\" : true, \"highlightProperty\" : \"highlightProperty\", \"icon\" : \"icon\", \"preserveSelectionOnPageChange\" : true, \"highlightClass\" : \"highlightClass\", \"label\" : \"label\", \"selectionMode\" : \"none\" }, \"orderedColumnNames\" : [ \"orderedColumnNames\", \"orderedColumnNames\" ] }, \"pageIndex\" : 5, \"qualifier\" : \"qualifier\", \"allRowsSelected\" : true, \"accessConfig\" : { \"idProperty\" : \"idProperty\", \"apiClass\" : \"apiClass\", \"kind\" : \"TABLEDATA\", \"dataUri\" : \"https://openapi-generator.tech\", \"identifierPath\" : [ \"identifierPath\", \"identifierPath\" ], \"parentIdProperty\" : \"parentIdProperty\" }, \"page\" : { \"upperBound\" : 6, \"lowerBound\" : 0, \"rows\" : [ { \"parent\" : \"parent\", \"data\" : \"{}\", \"children\" : [ \"children\", \"children\" ], \"selectable\" : true, \"style\" : \"\", \"id\" : \"id\", \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"columnActions\" : { \"key\" : [ \"columnActions\", \"columnActions\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"parent\" : \"parent\", \"data\" : \"{}\", \"children\" : [ \"children\", \"children\" ], \"selectable\" : true, \"style\" : \"\", \"id\" : \"id\", \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"columnActions\" : { \"key\" : [ \"columnActions\", \"columnActions\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ] }, \"totalRowCount\" : 1, \"selectedRowCount\" : 5, \"paginator\" : true }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
