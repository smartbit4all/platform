package org.smartbit4all.api.view.restserver;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;
import jakarta.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.8.0")
@Controller
@RequestMapping("${openapi.view.base-path:}")
public class ViewApiController implements ViewApi {

    private final ViewApiDelegate delegate;

    public ViewApiController(@Autowired(required = false) ViewApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ViewApiDelegate() {});
    }

    @Override
    public ViewApiDelegate getDelegate() {
        return delegate;
    }

}
