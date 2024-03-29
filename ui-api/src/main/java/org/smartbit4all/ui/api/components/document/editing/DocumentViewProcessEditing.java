package org.smartbit4all.ui.api.components.document.editing;

import java.net.URI;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.object.NotifyListeners;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.PublishEvents;
import org.springframework.scheduling.annotation.Async;

public interface DocumentViewProcessEditing {

  @PublishEvents("OBJECT")
  ObservableObject process();

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
