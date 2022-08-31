package org.smartbit4all.api.localauthentication.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.localAuthentication.base-path:/}")
public class LocalAuthenticationApiController implements LocalAuthenticationApi {

    private final LocalAuthenticationApiDelegate delegate;

    public LocalAuthenticationApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) LocalAuthenticationApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new LocalAuthenticationApiDelegate() {});
    }

    @Override
    public LocalAuthenticationApiDelegate getDelegate() {
        return delegate;
    }

}
