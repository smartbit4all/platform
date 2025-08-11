package org.smartbit4all.bff.api.mdm.invocation;

import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.bff.api.mdm.MDMEntryEditPageApi;

@ViewApi(value = PlatformViewNames.SCHEDULED_JOB_DEFINITION_EDITOR)
public interface ScheduledJobDefinitionEditorPageApi extends MDMEntryEditPageApi {

}
