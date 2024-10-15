package org.smartbit4all.ui.api.navigation.restserver;

import org.smartbit4all.ui.api.navigation.model.CommandData;
import org.smartbit4all.ui.api.navigation.model.CommandResult;
import org.smartbit4all.ui.api.navigation.model.MessageResult;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import java.util.UUID;
import org.smartbit4all.ui.api.navigation.model.ViewModelData;


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
@RequestMapping("${openapi.navigation.base-path:}")
public class ViewModelApiController implements ViewModelApi {

    private final ViewModelApiDelegate delegate;

    public ViewModelApiController(@Autowired(required = false) ViewModelApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ViewModelApiDelegate() {});
    }

    @Override
    public ViewModelApiDelegate getDelegate() {
        return delegate;
    }

}
