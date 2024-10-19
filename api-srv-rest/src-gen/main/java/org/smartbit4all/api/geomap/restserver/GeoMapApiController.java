package org.smartbit4all.api.geomap.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.geoMap.base-path:/}")
public class GeoMapApiController implements GeoMapApi {

    private final GeoMapApiDelegate delegate;

    public GeoMapApiController(@Autowired(required = false) GeoMapApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new GeoMapApiDelegate() {});
    }

    @Override
    public GeoMapApiDelegate getDelegate() {
        return delegate;
    }

}
