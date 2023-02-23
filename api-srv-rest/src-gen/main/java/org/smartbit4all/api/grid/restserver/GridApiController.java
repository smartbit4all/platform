package org.smartbit4all.api.grid.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.grid.base-path:/}")
public class GridApiController implements GridApi {

    private final GridApiDelegate delegate;

    public GridApiController(@Autowired(required = false) GridApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new GridApiDelegate() {});
    }

    @Override
    public GridApiDelegate getDelegate() {
        return delegate;
    }

}
