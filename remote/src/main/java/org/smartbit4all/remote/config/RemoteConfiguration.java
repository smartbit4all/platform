package org.smartbit4all.remote.config;

import org.smartbit4all.remote.service.RemoteCrudServiceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(RemoteCrudServiceConfiguration.class)
@Configuration
public class RemoteConfiguration {

}
