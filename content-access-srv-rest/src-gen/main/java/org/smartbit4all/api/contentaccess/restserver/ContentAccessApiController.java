package org.smartbit4all.api.contentaccess.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.contentAccess.base-path:/}")
public class ContentAccessApiController implements ContentAccessApi {

    private final ContentAccessApiDelegate delegate;

    public ContentAccessApiController(@Autowired(required = false) ContentAccessApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new ContentAccessApiDelegate() {});
    }

    @Override
    public ContentAccessApiDelegate getDelegate() {
        return delegate;
    }

}
