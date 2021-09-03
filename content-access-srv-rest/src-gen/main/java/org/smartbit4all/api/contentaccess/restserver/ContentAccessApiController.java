package org.smartbit4all.api.contentaccess.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.contentAccess.base-path:/}")
public class ContentAccessApiController implements ContentAccessApi {

    private final ContentAccessApiDelegate delegate;

    public ContentAccessApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) ContentAccessApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ContentAccessApiDelegate() {});
    }

    @Override
    public ContentAccessApiDelegate getDelegate() {
        return delegate;
    }

}
