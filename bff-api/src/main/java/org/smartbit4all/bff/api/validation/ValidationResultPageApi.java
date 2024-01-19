package org.smartbit4all.bff.api.validation;

import java.util.UUID;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.config.PlatformViewNames;
import org.smartbit4all.bff.api.validationresult.bean.ValidationResultPageModel;

@ViewApi(PlatformViewNames.VALIDATION_RESULT_PAGE)
public interface ValidationResultPageApi extends PageApi<ValidationResultPageModel> {
  String VALIDATION_PAGE_MODEL = "VALIDATION_PAGE_MODEL";
  String VALIDATION_PAGE_INVOCATION_REQUEST = "VALIDATION_PAGE_INVOCATION_REQUEST";

  String OK = "OK";
  String CONTINUE = "CONTINUE";
  String CANCEL = "CANCEL";

  @ActionHandler(CONTINUE)
  void performContinue(UUID viewUuid, UiActionRequest request);

  @ActionHandler({OK, CANCEL})
  void performCancel(UUID viewUuid, UiActionRequest request);

}
