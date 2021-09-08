package org.smartbit4all.api.contentaccess.restserver.config;

import org.smartbit4all.api.contentaccess.restserver.ContentAccessApiDelegate;
import org.smartbit4all.api.contentaccess.restserver.impl.ContentAccessApiDelegateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContentAccessSrvRestConfig {
		
	@Bean
	ContentAccessApiDelegate contentAccessApiDelegate() {
		return new ContentAccessApiDelegateImpl();
	}
	
}
