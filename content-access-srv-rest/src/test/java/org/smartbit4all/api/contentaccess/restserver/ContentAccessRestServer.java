package org.smartbit4all.api.contentaccess.restserver;

import org.smartbit4all.api.contentaccess.restserver.config.ContentAccessSrvRestForDotNetTestConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

@Import(ContentAccessSrvRestForDotNetTestConfig.class)
public class ContentAccessRestServer {

	public static void main(String[] args) {
		SpringApplication.run(ContentAccessRestServer.class, args); 
	}

}
