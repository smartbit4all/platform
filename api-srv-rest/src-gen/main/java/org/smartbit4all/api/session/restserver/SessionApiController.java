package org.smartbit4all.api.session.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.session.base-path:/}")
public class SessionApiController implements SessionApi {

    private final SessionApiDelegate delegate;

    public SessionApiController(@Autowired(required = false) SessionApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new SessionApiDelegate() {});
    }

    @Override
    public SessionApiDelegate getDelegate() {
        return delegate;
    }

}
