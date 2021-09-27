package org.smartbit4all.api.apiregister.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.apiregister.base-path:/}")
public class ApiRegisterApiController implements ApiRegisterApi {

    private final ApiRegisterApiDelegate delegate;

    public ApiRegisterApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) ApiRegisterApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ApiRegisterApiDelegate() {});
    }

    @Override
    public ApiRegisterApiDelegate getDelegate() {
        return delegate;
    }

}
