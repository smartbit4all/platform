package org.smartbit4all.api.contentaccess.restserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ContentAccessSrvRestTestConfig.class)
public class ContentAccessRestServer {

	public static void main(String[] args) {
		SpringApplication.run(ContentAccessRestServer.class, args); 
	}

}
