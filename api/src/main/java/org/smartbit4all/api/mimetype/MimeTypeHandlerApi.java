package org.smartbit4all.api.mimetype;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Collects the {@link MimeTypeHandler}s available in the current application. They can be used to
 * process a given content: extract the pages or the text of the document an so on.
 * 
 * @author András Palló
 *
 */
public class MimeTypeHandlerApi implements InitializingBean {

  private Map<String, MimeTypeHandler> handlersByMimeType = new HashMap<>();

  /**
   * The available handlers autowired by the Spring. If we include a new functional module adding
   * new handler they will be available immediately.
   */
  @Autowired
  private List<MimeTypeHandler> handlers;

  public MimeTypeHandlerApi() {}

  @Override
  public void afterPropertiesSet() throws Exception {
    if (handlers != null) {
      for (MimeTypeHandler handler : handlers) {
        for (String mimeType : handler.getAcceptedMimeTypes()) {
          handlersByMimeType.put(mimeType, handler);
        }
      }
    }
  }

  public MimeTypeHandler getHandler(String mimeType) {
    return handlersByMimeType.get(mimeType);
  }
}
