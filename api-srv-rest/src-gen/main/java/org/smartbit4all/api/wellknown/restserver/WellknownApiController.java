package org.smartbit4all.api.wellknown.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.wellknow.base-path:/}")
public class WellknownApiController implements WellknownApi {

    private final WellknownApiDelegate delegate;

    public WellknownApiController(@Autowired(required = false) WellknownApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new WellknownApiDelegate() {});
    }

    @Override
    public WellknownApiDelegate getDelegate() {
        return delegate;
    }

}
