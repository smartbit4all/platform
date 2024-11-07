package org.smartbit4all.api.geomap.restserver;

import org.smartbit4all.api.geomap.bean.GeoMapChange;
import org.smartbit4all.api.geomap.bean.GeoMapInteraction;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
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
 * A delegate to be called by the {@link GeoMapApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
public interface GeoMapApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /geomap/{uuid}/{identifier}/interact : 
     *
     * @param uuid  (required)
     * @param identifier  (required)
     * @param geoMapInteraction  (required)
     * @return  (status code 200)
     * @see GeoMapApi#interact
     */
    default ResponseEntity<ViewContextChange> interact(UUID uuid,
        String identifier,
        GeoMapInteraction geoMapInteraction) throws Exception {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /geomap/{uuid}/{identifier}/load : 
     *
     * @param uuid  (required)
     * @param identifier  (required)
     * @return  (status code 200)
     * @see GeoMapApi#load
     */
    default ResponseEntity<GeoMapModel> load(UUID uuid,
        String identifier) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"viewport\" : { \"zoomLevel\" : 0, \"center\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ] }, \"defaultItemActions\" : [ \"defaultItemActions\", \"defaultItemActions\" ], \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"qualifier\" : \"qualifier\", \"layers\" : [ { \"code\" : \"code\", \"items\" : [ { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ] }, { \"code\" : \"code\", \"items\" : [ { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ] } ], \"viewState\" : { \"visibleLayers\" : [ \"visibleLayers\", \"visibleLayers\" ], \"centerOnItemClick\" : false, \"fitToInitialItems\" : false, \"label\" : \"label\", \"layerDescriptors\" : [ { \"operationMode\" : \"ACTION\", \"code\" : \"code\", \"preserveSelection\" : false, \"icon\" : \"icon\", \"description\" : \"description\", \"label\" : \"label\", \"selectionMode\" : \"NONE\" }, { \"operationMode\" : \"ACTION\", \"code\" : \"code\", \"preserveSelection\" : false, \"icon\" : \"icon\", \"description\" : \"description\", \"label\" : \"label\", \"selectionMode\" : \"NONE\" } ], \"selectedItems\" : [ \"selectedItems\", \"selectedItems\" ] }, \"pendingItems\" : [ \"pendingItems\", \"pendingItems\" ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /goemap/{uuid}/{identifier}/move : 
     *
     * @param uuid  (required)
     * @param identifier  (required)
     * @param geoMapViewport  (required)
     * @return  (status code 200)
     * @see GeoMapApi#move
     */
    default ResponseEntity<GeoMapChange> move(UUID uuid,
        String identifier,
        GeoMapViewport geoMapViewport) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"code\" : \"code\", \"items\" : [ { \"toAdd\" : [ { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ], \"code\" : \"code\", \"toRemove\" : [ \"toRemove\", \"toRemove\" ] }, { \"toAdd\" : [ { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ], \"code\" : \"code\", \"toRemove\" : [ \"toRemove\", \"toRemove\" ] } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /geomap/{uuid}/{identifier}/update : 
     *
     * @param uuid  (required)
     * @param identifier  (required)
     * @param geoMapViewState  (required)
     * @return  (status code 200)
     * @see GeoMapApi#update
     */
    default ResponseEntity<GeoMapChange> update(UUID uuid,
        String identifier,
        GeoMapViewState geoMapViewState) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"code\" : \"code\", \"items\" : [ { \"toAdd\" : [ { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ], \"code\" : \"code\", \"toRemove\" : [ \"toRemove\", \"toRemove\" ] }, { \"toAdd\" : [ { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true }, { \"data\" : \"{}\", \"kind\" : \"MARKER\", \"selectable\" : true, \"bounds\" : [ { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 } ], \"description\" : \"description\", \"style\" : \"\", \"id\" : \"id\", \"label\" : \"label\", \"position\" : { \"latitude\" : 40.7128, \"longitude\" : -74.006, \"referenceSea\" : \"Baltic, Adriatic\", \"height\" : 174.6 }, \"icons\" : { \"key\" : [ \"\", \"\" ] }, \"actions\" : [ \"\", \"\" ], \"selected\" : true } ], \"code\" : \"code\", \"toRemove\" : [ \"toRemove\", \"toRemove\" ] } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
