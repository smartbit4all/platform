package org.smartbit4all.api.diagram.restserver;

import org.smartbit4all.api.diagram.bean.DiagramModel;
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
 * A delegate to be called by the {@link DiagramServiceApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
public interface DiagramServiceApiDelegate {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * POST /diagram/{uuid}/{identifier}/load : 
     *
     * @param uuid  (required)
     * @param identifier  (required)
     * @return  (status code 200)
     * @see DiagramServiceApi#load
     */
    default ResponseEntity<DiagramModel> load(UUID uuid,
        String identifier) throws Exception {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"identifier\" : \"identifier\", \"diagramData\" : { \"uri\" : \"https://openapi-generator.tech\", \"items\" : [ { \"pointStyle\" : \"pointStyle\", \"dataSetType\" : \"dataSetType\", \"dataSetcolor\" : \"dataSetcolor\", \"label\" : \"label\", \"items\" : [ { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 }, { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 } ] }, { \"pointStyle\" : \"pointStyle\", \"dataSetType\" : \"dataSetType\", \"dataSetcolor\" : \"dataSetcolor\", \"label\" : \"label\", \"items\" : [ { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 }, { \"itemColor\" : \"itemColor\", \"xValue\" : 7.457744773683766, \"label\" : \"label\", \"yValue\" : 1.1730742509559433, \"uri\" : \"https://openapi-generator.tech\", \"rValue\" : 4.965218492984954 } ] } ], \"labels\" : [ \"labels\", \"labels\" ] }, \"viewUuid\" : \"046b6c7f-0b8a-43b9-b35d-6489e6daee91\", \"descriptor\" : { \"showLegendForChart\" : true, \"showLabelsForYAxis\" : true, \"backgroundColor\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"minY\" : 9.301444, \"minX\" : 2.302136, \"maxY\" : 3.6160767, \"shapes\" : [ { \"shapeType\" : \"shapeType\", \"yMin\" : 7.386282, \"backgroundColor\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"borderColor\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"yMax\" : 1.2315135, \"borderWidth\" : 1, \"borderDashGap\" : 6, \"drawTime\" : \"beforeDraw\", \"labelText\" : { \"color\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"size\" : 5.637377, \"textAlign\" : \"textAlign\", \"weight\" : \"weight\", \"style\" : \"style\", \"family\" : \"family\", \"content\" : \"content\" }, \"borderDashLength\" : 1, \"xMax\" : 4.145608, \"xMin\" : 2.027123 }, { \"shapeType\" : \"shapeType\", \"yMin\" : 7.386282, \"backgroundColor\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"borderColor\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"yMax\" : 1.2315135, \"borderWidth\" : 1, \"borderDashGap\" : 6, \"drawTime\" : \"beforeDraw\", \"labelText\" : { \"color\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"size\" : 5.637377, \"textAlign\" : \"textAlign\", \"weight\" : \"weight\", \"style\" : \"style\", \"family\" : \"family\", \"content\" : \"content\" }, \"borderDashLength\" : 1, \"xMax\" : 4.145608, \"xMin\" : 2.027123 } ], \"maxX\" : 7.0614014, \"type\" : \"type\", \"title\" : { \"color\" : { \"red\" : 0, \"green\" : 6, \"blue\" : 1, \"alpha\" : 5.962134 }, \"size\" : 5.637377, \"textAlign\" : \"textAlign\", \"weight\" : \"weight\", \"style\" : \"style\", \"family\" : \"family\", \"content\" : \"content\" }, \"showLabelsForXAxis\" : true, \"uri\" : \"https://openapi-generator.tech\" }, \"uri\" : \"https://openapi-generator.tech\" }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
