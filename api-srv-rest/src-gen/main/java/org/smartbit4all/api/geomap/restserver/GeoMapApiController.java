package org.smartbit4all.api.geomap.restserver;

import org.smartbit4all.api.geomap.bean.GeoMapChange;
import org.smartbit4all.api.geomap.bean.GeoMapInteraction;
import org.smartbit4all.api.geomap.bean.GeoMapModel;
import org.smartbit4all.api.geomap.bean.GeoMapViewState;
import org.smartbit4all.api.geomap.bean.GeoMapViewport;
import java.util.UUID;
import org.smartbit4all.api.view.bean.ViewContextChange;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
@Controller
@RequestMapping("${openapi.geoMap.base-path:}")
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
