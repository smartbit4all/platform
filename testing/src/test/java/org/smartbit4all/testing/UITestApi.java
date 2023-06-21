package org.smartbit4all.testing;

import java.util.UUID;
import org.smartbit4all.api.view.bean.View;

/**
 * Api functions to accelerate the UI testing with
 * 
 * @author Peter Boros
 */
public interface UITestApi {

  View getView(String viewName);

  void runInViewContext(UUID viewContextUUID, Runnable process) throws Exception;

}
