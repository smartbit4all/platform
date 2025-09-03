package org.smartbit4all.api.diagram.restserver;

import org.smartbit4all.api.diagram.bean.DiagramWidgetModel;
import java.util.UUID;


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
@RequestMapping("${openapi.smartchart.base-path:}")
public class DiagramApiController implements DiagramApi {

    private final DiagramApiDelegate delegate;

    public DiagramApiController(@Autowired(required = false) DiagramApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new DiagramApiDelegate() {});
    }

    @Override
    public DiagramApiDelegate getDelegate() {
        return delegate;
    }

}
