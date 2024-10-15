package org.smartbit4all.api.navigation.restserver;

import org.smartbit4all.api.navigation.bean.NavigationEntry;
import org.smartbit4all.api.navigation.bean.NavigationResponse;
import java.net.URI;


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
@RequestMapping("${openapi.smartbit4allNavigation.base-path:}")
public class NavigationApiController implements NavigationApi {

    private final NavigationApiDelegate delegate;

    public NavigationApiController(@Autowired(required = false) NavigationApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new NavigationApiDelegate() {});
    }

    @Override
    public NavigationApiDelegate getDelegate() {
        return delegate;
    }

}
