package org.smartbit4all.ui.vaadin.components;

import java.util.UUID;
import java.util.function.Consumer;
import org.smartbit4all.ui.api.viewmodel.ViewModel;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;

/**
 * Generic Anchor component for download binary contents. This component will use
 * {@link ViewModel#getDownloadData(String)} to acquire BinaryData's input stream for StreamResource
 * so it can download using Vaadin's dynamic content handling.
 * 
 * An example: <br>
 * <br>
 * <code>
      Anchor download = new DownloadAnchor(viewModel, downloadButton, row.getFileName(),<br>
        identifier -> viewModel.executeCommand(<br>
            null, PolicyFolderVM.PREPARE_DOWNLOAD, row.getDataUri(), identifier));<br>
 * <br>
 * </code>
 * 
 * @author matea
 *
 */
public class DownloadAnchor extends Anchor {

  /**
   * Creates the anchor.
   * 
   * @param viewModel ViewModel to use
   * @param downloadComponent Component which will be displayed in the UI.
   * @param fileName Filename for downloading.
   * @param command This command will receive the generated identifier and it has to make sure, that
   *        it will register it in the ViewModel via a command or any other means.
   */
  public DownloadAnchor(ViewModel viewModel, Component downloadComponent, String fileName,
      Consumer<String> command) {
    String identifier = UUID.randomUUID().toString();
    command.accept(identifier);

    this.add(downloadComponent);
    this.getElement().setAttribute("download", true);

    StreamResource streamResource = new StreamResource(fileName,
        () -> viewModel.getDownloadData(identifier).inputStream());
    this.setHref(streamResource);
  }


}
