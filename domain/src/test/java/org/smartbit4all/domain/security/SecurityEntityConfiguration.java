/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
