package org.smartbit4all.bff.api.pdfviewer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.config.PlatformViewNames;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.view.PageApiImpl;
import org.smartbit4all.api.view.UiActions;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.bff.api.pdfviewer.bean.PdfViewerDialogModel;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Strings;

public class PdfViewerDialogApiImpl
    extends PageApiImpl<PdfViewerDialogModel>
    implements PdfViewerDialogApi {

  private static final Logger log = LoggerFactory.getLogger(PdfViewerDialogApiImpl.class);

  private static final String LOCALE_DIALOG_TITLE = "pdf-viewer.title";
  private static final String LOCALE_ERR_MSG = "pdf-viewer.err.msg";

  @Autowired
  private LocaleSettingApi localeSettingApi;

  public PdfViewerDialogApiImpl() {
    super(PdfViewerDialogModel.class);
  }

  @Override
  public PdfViewerDialogModel initModel(View view) {
    if (view.getObjectUri() == null) {
      log.error(
          "Launched {} without View.objectUri!"
              + " This is an illegal state and the view shall remain empty."
              + " Please supply a BinaryDataObject URI to this view at viewApi.showView(View) time!",
          PlatformViewNames.PDF_VIEWER_DIALOG);
    } else {
      view.putDownloadableItemsItem(
          PREVIEW_FILE,
          view.getObjectUri());
    }

    PdfViewerDialogModel model = objectApi.asType(PdfViewerDialogModel.class, view.getModel());
    UiActions.add(view, DEFAULT_CLOSE);
    return normaliseModel(model);
  }

  private PdfViewerDialogModel normaliseModel(PdfViewerDialogModel model) {
    final PdfViewerDialogModel modelToUse = (model == null) ? new PdfViewerDialogModel() : model;
    if (Strings.isNullOrEmpty(modelToUse.getTitle())) {
      modelToUse.setTitle(localeSettingApi.get(LOCALE_DIALOG_TITLE));
    }

    if (Strings.isNullOrEmpty(modelToUse.getErrorMsg())) {
      modelToUse.setErrorMsg(localeSettingApi.get(LOCALE_ERR_MSG));
    }

    if (modelToUse.getHideSidebar() == null) {
      modelToUse.setHideSidebar(false);
    }

    return modelToUse;
  }

}
