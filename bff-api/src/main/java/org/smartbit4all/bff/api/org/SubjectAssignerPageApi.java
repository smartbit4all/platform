package org.smartbit4all.bff.api.org;

import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.grid.bean.GridPage;
import org.smartbit4all.api.org.bean.Subject;
import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ActionHandler;
import org.smartbit4all.api.view.annotation.WidgetActionHandler;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.smartbit4all.bff.api.subjectselector.bean.SubjectAssignerPageModel;


public interface SubjectAssignerPageApi extends PageApi<SubjectAssignerPageModel> {

  static final String PARAM_SUBJECT_PAGE_CONFIG = "PARAM_SUBJECT_PAGE_CONFIG";

  static final String ADD_SUBJECT = "ADD_SUBJECT";
  static final String DELETE_SUBJECT = "DELETE_SUBJECT";
  static final String DELETE_UNDO = "DELETE_UNDO";
  static final String ADD_UNDO = "ADD_UNDO";
  static final String SAVE = "SAVE";



  GridPage addGridActions(GridPage page, UUID viewUuid, String gridId);

  @ActionHandler(ADD_SUBJECT)
  void performAddSubject(UUID viewUuid, UiActionRequest request);

  @ActionHandler(SAVE)
  void performSave(UUID viewUuid, UiActionRequest request);

  @WidgetActionHandler(DELETE_SUBJECT)
  void performDeleteSubject(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  @WidgetActionHandler(DELETE_UNDO)
  void performDeleteUndo(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  @WidgetActionHandler(ADD_UNDO)
  void performAddUndo(UUID viewUuid, String gridId, String rowId, UiActionRequest request);

  void handleSubjectSelected(UUID viewUuid, List<Subject> subjects, String gridId);

}
