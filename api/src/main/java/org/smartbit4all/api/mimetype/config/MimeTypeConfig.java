package org.smartbit4all.api.mimetype.config;

import org.smartbit4all.api.mimetype.ContentConversionApi;
import org.smartbit4all.api.mimetype.ContentConversionApiImpl;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.mimetype.MimeTypeHandler;
import org.smartbit4all.api.mimetype.MimeTypeHandlerApi;
import org.smartbit4all.api.mimetype.MimeTypeHandlerImage;
import org.smartbit4all.api.mimetype.MimeTypeHandlerText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MimeTypeConfig {

  @Bean
  MimeTypeApi mimeTypeApi() {
    return new MimeTypeApi();
  }

  @Autowired
  ContentConversionApi conversionApi() {
    return new ContentConversionApiImpl();
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
