package org.smartbit4all.api.diagram.restserver;

import org.smartbit4all.api.diagram.bean.DiagramWidgetModel;
import java.util.UUID;
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
 * A delegate to be called by the {@link DiagramApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
public interface DiagramApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /geomap/{uuid}/{identifier}/load : 
     *
     * @param uuid  (required)
     * @param identifier  (required)
     * @return  (status code 200)
     * @see DiagramApi#load
     */
    default ResponseEntity<DiagramWidgetModel> load(UUID uuid,
        String identifier) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"diagramModel\" : { \"diagramData\" : { \"uri\" : \"https://openapi-generator.tech\", \"items\" : [ { \"pointStyle\" : \"pointStyle\", \"dataSetType\" : \"dataSetType\", \"dataSetcolor\" : \"dataSetcolor\", \"label\" : \"label\", \"items\" : [ { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 }, { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 } ] }, { \"pointStyle\" : \"pointStyle\", \"dataSetType\" : \"dataSetType\", \"dataSetcolor\" : \"dataSetcolor\", \"label\" : \"label\", \"items\" : [ { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 }, { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 } ] } ], \"labels\" : [ \"labels\", \"labels\" ] }, \"descriptor\" : { \"showLegendForChart\" : true, \"showLabelsForYAxis\" : true, \"backgroundColor\" : { \"red\" : 5, \"green\" : 2, \"blue\" : 7, \"alpha\" : 9.301444 }, \"diagramMinX\" : 0.8008282, \"diagramMaxY\" : 5.962134, \"diagramMinY\" : 1.4658129, \"diagramMaxX\" : 6.0274563, \"showLabelsForXAxis\" : true, \"uri\" : \"https://openapi-generator.tech\", \"diagramType\" : \"diagramType\", \"diagramShapes\" : [ { \"shapeType\" : \"shapeType\", \"yMin\" : 4.145608, \"backgroundColor\" : { \"red\" : 5, \"green\" : 2, \"blue\" : 7, \"alpha\" : 9.301444 }, \"borderColor\" : { \"red\" : 5, \"green\" : 2, \"blue\" : 7, \"alpha\" : 9.301444 }, \"yMax\" : 7.386282, \"borderWidth\" : 1, \"borderDashGap\" : 1, \"drawTime\" : \"beforeDraw\", \"labelText\" : { \"color\" : { \"red\" : 5, \"green\" : 2, \"blue\" : 7, \"alpha\" : 9.301444 }, \"size\" : 6.846853, \"textAlign\" : \"textAlign\", \"weight\" : \"weight\", \"style\" : \"style\", \"family\" : \"family\", \"content\" : \"content\" }, \"borderDashLength\" : 1, \"xMax\" : 2.027123, \"xMin\" : 3.6160767 }, { \"shapeType\" : \"shapeType\", \"yMin\" : 4.145608, \"backgroundColor\" : { \"red\" : 5, \"green\" : 2, \"blue\" : 7, \"alpha\" : 9.301444 }, \"borderColor\" : { \"red\" : 5, \"green\" : 2, \"blue\" : 7, \"alpha\" : 9.301444 }, \"yMax\" : 7.386282, \"borderWidth\" : 1, \"borderDashGap\" : 1, \"drawTime\" : \"beforeDraw\", \"labelText\" : { \"color\" : { \"red\" : 5, \"green\" : 2, \"blue\" : 7, \"alpha\" : 9.301444 }, \"size\" : 6.846853, \"textAlign\" : \"textAlign\", \"weight\" : \"weight\", \"style\" : \"style\", \"family\" : \"family\", \"content\" : \"content\" }, \"borderDashLength\" : 1, \"xMax\" : 2.027123, \"xMin\" : 3.6160767 } ] }, \"uri\" : \"https://openapi-generator.tech\" }, \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
