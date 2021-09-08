package org.smartbit4all.api.contentaccess.restserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT, classes = {ContentAccessSrvRestTestConfig.class})
public class ContentAccessSrvRestTest {
	
	@Autowired
	protected org.smartbit4all.api.contentaccess.ContentAccessApi contentAccessApi;
	
  @LocalServerPort
  protected int port;
  
  @Autowired
  protected TestRestTemplate restTemplate;
  
  protected String basePath() {
  	return "http://localhost:"+ port + "/";
  }
}
