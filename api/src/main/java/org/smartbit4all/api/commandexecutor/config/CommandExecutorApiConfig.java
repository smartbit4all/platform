package org.smartbit4all.api.commandexecutor.config;

import org.smartbit4all.api.commandexecutor.CommandExecutorFfmpegApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandExecutorApiConfig {

  @Bean
  public CommandExecutorFfmpegApi commandExecutorFfmpegApi() {
    return new CommandExecutorFfmpegApi();
  }

}
