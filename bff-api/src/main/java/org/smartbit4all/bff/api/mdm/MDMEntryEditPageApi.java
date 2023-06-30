package org.smartbit4all.bff.api.mdm;

import java.util.UUID;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMEntryEditingObject;
import org.smartbit4all.api.object.bean.BranchedObjectEntry;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.ViewEventApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;

/**
 * 
 * This is the default page api with dynamically generated layout and action. If we have an average
 * master data entry with normal properties and references to other entries then we can use this
 * page. It is activated when we do not set any specific view in the {@link MDMEntryDescriptor}. All
 * properties of the given object and its contained references appear on the editor page.
 * Additionally all the references let it be {@link ReferencePropertyKind#REFERENCE},
 * {@link ReferencePropertyKind#LIST} or {@link ReferencePropertyKind#MAP} will be selectors for the
 * given master data entry.
 * 
 * The model of the editor page is always a {@link BranchedObjectEntry} based
 * {@link MDMEntryEditingObject} with the state and the original / branch uri of the
 * 
 * Right now the page doesn't manage the references point out from the MDM subsystem.
 * 
 * @author Peter Boros
 */
public interface MDMEntryEditPageApi extends PageApi<Object> {

  /**
   * The generic save action that will we replaced when the application
   */
  static final String ACTION_SAVE = "SAVE";

  /**
   * The cancel action that will close the editing without save.
   */
  static final String ACTION_CANCEL = "CANCEL";

  /**
   * Executes the save action of the editing page. It is always overridden with
   * {@link ViewEventApi#add(org.smartbit4all.api.view.bean.ViewEventHandler)} to replace the save
   * behavior.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_SAVE)
  void performSave(UUID viewUuid, UiActionRequest request);

  /**
   * Cancel the editing of the given object. Normally it closes the view and return to the parent.
   * 
   * @param viewUuid The unique identifier of the view in the current context.
   * @param request The action request that contains every information about the triggering action.
   */
  @ActionHandler(ACTION_CANCEL)
  void performCancel(UUID viewUuid, UiActionRequest request);

}
