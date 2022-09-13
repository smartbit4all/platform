package org.smartbit4all.api.navigation.restserver;

import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationResponse;
import java.net.URI;
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
 * A delegate to be called by the {@link NavigationApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public interface NavigationApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /entry : Retrieve the entries from the navigations.
     *
     * @param entryMetaUri  (required)
     * @param objectUri  (required)
     * @return The navigation entry if we found it or null if missing (status code 200)
     * @see NavigationApi#getEntry
     */
    default ResponseEntity<NavigationEntry> getEntry(URI entryMetaUri,
        URI objectUri) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"metaUri\" : \"https://openapi-generator.tech\", \"parentObjectUri\" : \"https://openapi-generator.tech\", \"name\" : \"name\", \"icon\" : \"icon\", \"styles\" : [ \"styles\", \"styles\" ], \"objectUri\" : \"https://openapi-generator.tech\", \"parentAssocMetaUri\" : \"https://openapi-generator.tech\", \"actions\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"views\" : [ { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } }, { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } } ] }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

    /**
     * POST /navigate : Queries all data sources to populate the associations starting from the given entry.
     *
     * @param objectUri The URI of the api object that is the starting point of the navigation. It must be a valid URI that can be the starting point of the associations we provided. (required)
     * @param associationMetaUris The list of associations to identify the direction we want to navigate. If we skip this parameter (null) then we will have all the associations defined in the meta (optional)
     * @return The map of the references by the URI of association meta we passed in the associations parameter. (status code 200)
     * @see NavigationApi#navigate
     */
    default ResponseEntity<List<NavigationResponse>> navigate(URI objectUri,
        List<URI> associationMetaUris) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"referenceEntries\" : [ { \"associationEntry\" : { \"metaUri\" : \"https://openapi-generator.tech\", \"parentObjectUri\" : \"https://openapi-generator.tech\", \"name\" : \"name\", \"icon\" : \"icon\", \"styles\" : [ \"styles\", \"styles\" ], \"objectUri\" : \"https://openapi-generator.tech\", \"parentAssocMetaUri\" : \"https://openapi-generator.tech\", \"actions\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"views\" : [ { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } }, { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } } ] }, \"startEntryUri\" : \"https://openapi-generator.tech\", \"id\" : \"id\", \"endEntry\" : { \"metaUri\" : \"https://openapi-generator.tech\", \"parentObjectUri\" : \"https://openapi-generator.tech\", \"name\" : \"name\", \"icon\" : \"icon\", \"styles\" : [ \"styles\", \"styles\" ], \"objectUri\" : \"https://openapi-generator.tech\", \"parentAssocMetaUri\" : \"https://openapi-generator.tech\", \"actions\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"views\" : [ { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } }, { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } } ] } }, { \"associationEntry\" : { \"metaUri\" : \"https://openapi-generator.tech\", \"parentObjectUri\" : \"https://openapi-generator.tech\", \"name\" : \"name\", \"icon\" : \"icon\", \"styles\" : [ \"styles\", \"styles\" ], \"objectUri\" : \"https://openapi-generator.tech\", \"parentAssocMetaUri\" : \"https://openapi-generator.tech\", \"actions\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"views\" : [ { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } }, { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } } ] }, \"startEntryUri\" : \"https://openapi-generator.tech\", \"id\" : \"id\", \"endEntry\" : { \"metaUri\" : \"https://openapi-generator.tech\", \"parentObjectUri\" : \"https://openapi-generator.tech\", \"name\" : \"name\", \"icon\" : \"icon\", \"styles\" : [ \"styles\", \"styles\" ], \"objectUri\" : \"https://openapi-generator.tech\", \"parentAssocMetaUri\" : \"https://openapi-generator.tech\", \"actions\" : [ \"https://openapi-generator.tech\", \"https://openapi-generator.tech\" ], \"views\" : [ { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } }, { \"name\" : \"name\", \"parameters\" : { \"key\" : \"{}\" } } ] } } ], \"associationMetaUri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
