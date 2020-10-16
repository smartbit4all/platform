package org.smartbit4all.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * The configuration for the platform domain. It's not a business domain it's the platform basic
 * package.
 * 
 * @author Peter Boros
 */

/**
 * The configuration for the platform core module. It's not a business domain it's the platform
 * basic package to support the core execution framework.
 * 
 * @author Peter Boros
 */
@Configuration
@Import({CoreServiceConfig.class})
public class CoreConfig {


}
