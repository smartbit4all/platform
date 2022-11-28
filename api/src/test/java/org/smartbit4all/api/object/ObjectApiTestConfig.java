package org.smartbit4all.api.object;

import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Import({PlatformApiConfig.class})
public class ObjectApiTestConfig {

  @Bean
  public ObjectDefinition<DomainObjectTestBean> masterBeanDef() {
    ObjectDefinition<DomainObjectTestBean> result =
        new ObjectDefinition<>(DomainObjectTestBean.class);
    result.setAlias(DomainObjectTestBean.class.getName().replace('.', '-'));
    result.setPreferredSerializerName(ObjectMapper.class.getName());
    result.setUriGetter(DomainObjectTestBean::getUri);
    result.setUriSetter(DomainObjectTestBean::setUri);
    return result;
  }

}
