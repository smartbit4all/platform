package org.smartbit4all.api.restserver;

import org.smartbit4all.api.navigation.restserver.config.NavigationSrvRestConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The smartbit4all platform api config.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({NavigationSrvRestConfig.class})
public class PlatformApiSrvRestConfig {

}
