package org.smartbit4all.bff.api.pdfviewer;

import org.smartbit4all.api.view.PageApi;
import org.smartbit4all.api.view.annotation.ViewApi;
import org.smartbit4all.bff.api.config.PlatformViewNames;
import org.smartbit4all.bff.api.pdfviewer.bean.PdfViewerDialogModel;

@ViewApi(PlatformViewNames.PDF_VIEWER_DIALOG)
public interface PdfViewerDialogApi
    extends PageApi<PdfViewerDialogModel> {

  String PREVIEW_FILE = "PREVIEW_FILE";

}
