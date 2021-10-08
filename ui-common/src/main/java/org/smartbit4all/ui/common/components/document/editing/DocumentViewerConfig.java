package org.smartbit4all.ui.common.components.document.editing;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.smartbit4all.api.documentview.bean.DisplayMode;
import org.smartbit4all.api.documentview.bean.DocumentViewProcess;
import org.smartbit4all.api.documentview.bean.ImageWithAlt;
import org.smartbit4all.api.mimetype.MimeTypeHandlerApi;
import org.smartbit4all.api.mimetype.config.MimeTypeConfig;
import org.smartbit4all.core.object.ApiBeanDescriptor;
import org.smartbit4all.ui.api.components.document.editing.DocumentViewProcessEditing;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

@Configuration
@Import(MimeTypeConfig.class)
public class DocumentViewerConfig {

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public DocumentViewProcessEditing documentViewProcessEditing(
      MimeTypeHandlerApi mimeTypeHandlerApi) {
    return new DocumentViewProcessEditingImpl(createDocumentViewDescriptor(), mimeTypeHandlerApi);
  }

  private Map<Class<?>, ApiBeanDescriptor> createDocumentViewDescriptor() {
    Set<Class<?>> processDomainBeans = new HashSet<>();

    processDomainBeans.add(DocumentViewProcess.class);
    processDomainBeans.add(ImageWithAlt.class);
    processDomainBeans.add(DisplayMode.class);

    return ApiBeanDescriptor.of(processDomainBeans);
  }
}
