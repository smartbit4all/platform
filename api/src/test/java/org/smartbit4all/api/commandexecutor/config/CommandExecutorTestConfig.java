package org.smartbit4all.api.commandexecutor.config;

import org.smartbit4all.api.commandexecutor.CommandExecutorBashApi;
import org.smartbit4all.api.commandexecutor.CommandExecutorFfmpegApi;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.io.TestFSConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PlatformApiConfig.class, TestFSConfig.class})
public class CommandExecutorTestConfig {

  @Bean
  public CommandExecutorBashApi commandExecutorApiBash() {
    return new CommandExecutorBashApi();
  }

  @Bean
  public CommandExecutorFfmpegApi commandExecutorFfmpegApi() {
    return new CommandExecutorFfmpegApi();
  }

}
