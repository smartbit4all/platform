package org.smartbit4all.api.navigation.restserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Optional;
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@Controller
@RequestMapping("${openapi.smartbit4allNavigation.base-path:/}")
public class NavigationApiController implements NavigationApi {

    private final NavigationApiDelegate delegate;

    public NavigationApiController(@org.springframework.beans.factory.annotation.Autowired(required = false) NavigationApiDelegate delegate) {
        this.delegate = Optional.ofNullable(delegate).orElse(new NavigationApiDelegate() {});
    }

    @Override
    public NavigationApiDelegate getDelegate() {
        return delegate;
    }

}
