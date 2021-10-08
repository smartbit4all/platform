package org.smartbit4all.api.mimetype.config;

import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.mimetype.MimeTypeHandler;
import org.smartbit4all.api.mimetype.MimeTypeHandlerApi;
import org.smartbit4all.api.mimetype.MimeTypeHandlerImage;
import org.smartbit4all.api.mimetype.MimeTypeHandlerText;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MimeTypeConfig {

  @Bean
  MimeTypeApi mimeTypeApi() {
    return new MimeTypeApi();
  }

  @Bean
  MimeTypeHandlerApi mimeTypeHandlerApi() {
    return new MimeTypeHandlerApi();
  }

  @Bean
  public MimeTypeHandler mimeTypeHandlerImage() {
    return new MimeTypeHandlerImage();
  }

  @Bean
  public MimeTypeHandler mimeTypeHandlerText() {
    return new MimeTypeHandlerText();
  }
}
