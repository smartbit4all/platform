package org.smartbit4all.api.kerberosauthentication.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.kerberosAuthentication.base-path:/}")
public class KerberosAuthenticationApiController implements KerberosAuthenticationApi {

    private final KerberosAuthenticationApiDelegate delegate;

    public KerberosAuthenticationApiController(@Autowired(required = false) KerberosAuthenticationApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new KerberosAuthenticationApiDelegate() {});
    }

    @Override
    public KerberosAuthenticationApiDelegate getDelegate() {
        return delegate;
    }

}
