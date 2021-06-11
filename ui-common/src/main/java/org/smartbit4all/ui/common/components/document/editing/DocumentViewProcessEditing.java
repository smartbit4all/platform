package org.smartbit4all.ui.common.components.document.editing;

import java.net.URI;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.smartbit4all.types.binarydata.BinaryData;
import org.springframework.scheduling.annotation.Async;

public interface DocumentViewProcessEditing {

  @PublishEvents("OBJECT")
  ObservableObject process();

  @NotifyListeners
  void initDocumentViewProcess();

  @Async
  void setDocument(BinaryData document, URI uri, String mimeType);

  @NotifyListeners
  void showPrevPage();

  @NotifyListeners
  void showNextPage();

  @NotifyListeners
  void setPage(int index);

  @NotifyListeners
  void setPage(String value);

  @NotifyListeners
  void zoomOut();

  @NotifyListeners
  void zoomIn();

  @NotifyListeners
  void showMessage(String message);

  @NotifyListeners
  void displayLoadingSpinner();
  
  void setActualPageWidth(int pageWidth);
  
}
