package org.smartbit4all.ui.api.navigation.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.navigation.base-path:/}")
public class ViewModelApiController implements ViewModelApi {

    private final ViewModelApiDelegate delegate;

    public ViewModelApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) ViewModelApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ViewModelApiDelegate() {});
    }

    @Override
    public ViewModelApiDelegate getDelegate() {
        return delegate;
    }

}
