package org.smartbit4all.api.uitree.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.uitree.base-path:/}")
public class TreeApiController implements TreeApi {

    private final TreeApiDelegate delegate;

    public TreeApiController(@Autowired(required = false) TreeApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new TreeApiDelegate() {});
    }

    @Override
    public TreeApiDelegate getDelegate() {
        return delegate;
    }

}
