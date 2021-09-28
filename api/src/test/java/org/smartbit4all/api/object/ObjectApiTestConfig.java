package org.smartbit4all.api.object;

import org.smartbit4all.core.config.CoreConfig;
import org.smartbit4all.core.object.MasterBean;
import org.smartbit4all.core.object.ObjectDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@Import({CoreConfig.class})
public class ObjectApiTestConfig {

  @Bean
  public ObjectDefinition<MasterBean> masterBeanDef() {
    ObjectDefinition<MasterBean> result = new ObjectDefinition<>(MasterBean.class);
    result.setAlias(MasterBean.class.getName().replace('.', '-'));
    result.setPreferredSerializerName(ObjectMapper.class.getName());
    result.setUriGetter(MasterBean::getUri);
    result.setUriSetter(MasterBean::setUri);
    return result;
  }

}
