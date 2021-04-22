package org.smartbit4all.ui.vaadin.components.navigation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

@Tag(Tag.DIV)
public class ParameterMissingHandler extends Component
    implements HasErrorParameter<ParameterMissingException> {


  private static final Logger log = LoggerFactory.getLogger(ParameterMissingHandler.class);

  @Override
  public int setErrorParameter(BeforeEnterEvent event,
      ErrorParameter<ParameterMissingException> parameter) {
    log.error("Unhandled ParameterMissingException!", parameter.getCaughtException());
    event.forwardTo("");
    return 200;
  }

}
