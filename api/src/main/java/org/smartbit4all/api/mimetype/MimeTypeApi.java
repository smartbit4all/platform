package org.smartbit4all.api.mimetype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class MimeTypeApi implements InitializingBean {

  private Map<String, MimeTypeHandler> handlersByMimeType = new HashMap<>();

  @Autowired
  private List<MimeTypeHandler> handlers;
  
  public MimeTypeApi() {
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (handlers != null) {
      for (MimeTypeHandler handler : handlers) {
        for(String mimeType : handler.getAcceptedMimeTypes()) {
          handlersByMimeType.put(mimeType, handler);
        }
      }
    }
  }
  
  public MimeTypeHandler getHandler(String mimeType) {
    return handlersByMimeType.get(mimeType);
  }
}
