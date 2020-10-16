package org.smartbit4all.domain.meta;

import org.smartbit4all.domain.meta.jdbc.JDBCDataConverterConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(JDBCDataConverterConfig.class)
@Configuration
public class MetaConfiguration {

}
