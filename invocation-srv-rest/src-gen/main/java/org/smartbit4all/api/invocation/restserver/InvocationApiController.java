package org.smartbit4all.api.invocation.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.invocation.base-path:/}")
public class InvocationApiController implements InvocationApi {

    private final InvocationApiDelegate delegate;

    public InvocationApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) InvocationApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new InvocationApiDelegate() {});
    }

    @Override
    public InvocationApiDelegate getDelegate() {
        return delegate;
    }

}
