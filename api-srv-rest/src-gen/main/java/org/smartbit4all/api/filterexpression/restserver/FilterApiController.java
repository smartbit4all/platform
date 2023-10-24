package org.smartbit4all.api.filterexpression.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.filterAPI2.base-path:/}")
public class FilterApiController implements FilterApi {

    private final FilterApiDelegate delegate;

    public FilterApiController(@Autowired(required = false) FilterApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new FilterApiDelegate() {});
    }

    @Override
    public FilterApiDelegate getDelegate() {
        return delegate;
    }

}
