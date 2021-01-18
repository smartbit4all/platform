package org.smartbit4all.ui.common.action;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Actions implements InitializingBean, ApplicationContextAware {

  Map<String, Action> actionsByName = new HashMap<>();

  private ApplicationContext applicationContext;

  @Override
  public void afterPropertiesSet() throws Exception {
    Map<String, Action> actions = applicationContext.getBeansOfType(Action.class);
    for (Entry<String, Action> entry : actions.entrySet()) {
      actionsByName.put(entry.getValue().getName(), entry.getValue());
    }
  }
    
  public final Action get(URI uri) {
    return actionsByName.get(uri.getScheme());
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
  
}
