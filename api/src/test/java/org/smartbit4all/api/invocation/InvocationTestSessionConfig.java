package org.smartbit4all.api.invocation;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({InvocationTestConfig.class})
@EnableTransactionManagement
public class InvocationTestSessionConfig {


}
