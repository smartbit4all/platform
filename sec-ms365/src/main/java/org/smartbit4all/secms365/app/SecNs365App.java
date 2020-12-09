package org.smartbit4all.secms365.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication()
public class SecNs365App extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(new Class[]{SecNs365App.class}, args);
  }

}
