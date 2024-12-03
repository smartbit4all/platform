package org.smartbit4all.api.object;

import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@Import({
    TestFSConfig.class,
})
public class ObjectApiTestConfig extends ObjectApiTestConfigBase {

  @EventListener(ContextRefreshedEvent.class)
  public void clearFS(ContextRefreshedEvent event) throws Exception {
    TestFileUtil.clearTestDirectory();
    System.out.println("Test FS cleared...");
  }

}
