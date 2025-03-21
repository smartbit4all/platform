package org.smartbit4all.api.commandexecutor.config;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFSConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class, CommandExecutorApiConfig.class, TestFSConfig.class})
public class CommandExecutorTestConfig {

}
