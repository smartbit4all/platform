package org.smartbit4all.domain.security;

import org.smartbit4all.domain.meta.EntityConfiguration;
import org.smartbit4all.domain.service.transfer.BeanEntityBindingConfig;
import org.smartbit4all.domain.service.transfer.convert.Converter;
import org.smartbit4all.domain.service.transfer.convert.ConverterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityEntityConfiguration extends EntityConfiguration {

  private static final String MY_ID_CONVERTER = "MyIdConverter";

  @Bean(UserAccountDef.ENTITY_NAME)
  public UserAccountDef userAccountDef() {
    UserAccountDef userAccDef = createEntityProxy(UserAccountDef.class);
    return userAccDef;
  }

  @Bean(AddressDef.ENTITY_NAME)
  public AddressDef addressDef() {
    AddressDef addressDef = createEntityProxy(AddressDef.class);
    return addressDef;
  }


  @Bean
  public BeanEntityBindingConfig addressBinding()
      throws NoSuchMethodException, SecurityException {
    BeanEntityBindingConfig bindingConfig =
        new BeanEntityBindingConfig(Address.class, AddressDef.ENTITY_NAME);
    bindingConfig.bind(AddressDef.ID, "id", "id", MY_ID_CONVERTER)
        .bind(AddressDef.ZIPCODE, "zipCode", "zipCode");
    return bindingConfig;
  }

  @Bean
  public Converter<Long, String> myIdConverter() {
    return new ConverterImpl<Long, String>(String.class,
        (Long l) -> l.toString(), Long.class, (String s) -> Long.valueOf(s)).name(MY_ID_CONVERTER);
  }


}
