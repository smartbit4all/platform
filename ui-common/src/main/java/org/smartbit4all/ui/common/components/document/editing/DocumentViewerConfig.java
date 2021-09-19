package org.smartbit4all.ui.common.components.document.editing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.documentview.bean.DisplayMode;
import org.smartbit4all.api.documentview.bean.DocumentViewProcess;
import org.smartbit4all.api.documentview.bean.ImageWithAlt;
import org.smartbit4all.api.mimetype.MimeTypeApi;
import org.smartbit4all.api.mimetype.MimeTypeHandler;
import org.smartbit4all.api.mimetype.MimeTypeHandlerApi;
import org.smartbit4all.api.mimetype.MimeTypeHandlerImage;
import org.smartbit4all.api.mimetype.MimeTypeHandlerText;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.components.document.editing.DocumentViewProcessEditing;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class DocumentViewerConfig {
  
  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public DocumentViewProcessEditing documentViewProcessEditing(MimeTypeHandlerApi mimeTypeHandlerApi) {
    return new DocumentViewProcessEditingImpl(createDocumentViewDescriptor(), mimeTypeHandlerApi);
  }

  private Map<Class<?>, ApiBeanDescriptor> createDocumentViewDescriptor() {
    Set<Class<?>> processDomainBeans = new HashSet<>();

    processDomainBeans.add(DocumentViewProcess.class);
    processDomainBeans.add(ImageWithAlt.class);
    processDomainBeans.add(DisplayMode.class);

    return ApiBeanDescriptor.of(processDomainBeans);
  }
  
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
