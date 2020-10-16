package org.smartbit4all.ui.vaadin;

import org.smartbit4all.ui.vaadin.util.sb4.VaadinComponentFactory;
import org.smartbit4all.ui.vaadin.util.sb4.VaadinComponentFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UiVaadinConfiguration {

	@Bean
	public VaadinComponentFactory vaadinComponentFactory() {
		return new VaadinComponentFactoryImpl();
	}

}
