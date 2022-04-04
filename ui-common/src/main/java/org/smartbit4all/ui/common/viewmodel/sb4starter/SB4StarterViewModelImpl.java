package org.smartbit4all.ui.common.viewmodel.sb4starter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.sb4starter.bean.CommandKind;
import org.smartbit4all.api.sb4starter.bean.SB4Command;
import org.smartbit4all.api.sb4starter.bean.SB4File;
import org.smartbit4all.api.sb4starter.bean.SB4Starter;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObservablePublisherWrapper;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.navigation.model.NavigationTarget;
import org.smartbit4all.ui.api.sb4starter.SB4StarterViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterState;
import org.smartbit4all.ui.api.viewmodel.ViewModelImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.disposables.Disposable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SB4StarterViewModelImpl extends ViewModelImpl<SB4StarterModel>
    implements SB4StarterViewModel {

  @Value("${openapi.contentAccess.base-path}")
  private String contentAccessBasePath;

  @Autowired
  private ContentAccessApi contentAccessApi;

  @Autowired
  private UINavigationApi uiNavigationApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  private BiConsumer<BinaryContent, BinaryContent> acceptHandler;

  private List<Disposable> eventListeners = new ArrayList<>();

  private Storage storage;

  private String baseUrl;

  protected SB4StarterViewModelImpl(ObservablePublisherWrapper publisherWrapper) {
    super(publisherWrapper, SB4StarterViewModelUtility.SB4_STARTER_DESCRIPTOR,
        SB4StarterModel.class);
  }

  @Override
  protected void initCommands() {
    registerCommand(ACCEPT, this::accept);
    registerCommand(DECLINE, this::close);
    registerCommand(DOWNLOAD, this::download);
  }

  @Override
  protected SB4StarterModel load(NavigationTarget navigationTarget) {
    Map<String, Object> parameters = navigationTarget.getParameters();
    this.baseUrl = (String) parameters.get(PARAM_BASELOCATION) + getContentAccesBasePath();
    this.acceptHandler =
        (BiConsumer<BinaryContent, BinaryContent>) parameters.get(PARAM_ACCEPTHANDLER);
    SB4StarterModel formModel = (SB4StarterModel) parameters.get(PARAM_FORMMODEL);

    try {
      createSB4Starter(formModel);
    } catch (Exception e) {
    }
    return formModel;
  }

  private String getContentAccesBasePath() {
    if (contentAccessBasePath.startsWith("/")) {
      return contentAccessBasePath.substring(1);
    }
    return contentAccessBasePath;
  }

  private void createSB4Starter(SB4StarterModel formModel) throws Exception {

    UUID startWordId = contentAccessApi.share(formModel.getStartContent());
    subscribeToDownloadContent(startWordId);

    UUID resultWordId = contentAccessApi.share(formModel.getResultContent());
    subscribeToUploadContent(resultWordId);

    String wordFileName = formModel.getStartContent().getFileName();
    BinaryData sb4Starter =
        createSB4Starter(startWordId, wordFileName, resultWordId, formModel.getEditCommandKind());

    StorageObject<BinaryDataObject> sb4StarterObjectSo =
        getStorage().instanceOf(BinaryDataObject.class);

    sb4StarterObjectSo.setObject(sb4Starter.asObject());

    URI sb4Uri = getStorage().save(sb4StarterObjectSo);

    BinaryContent sb4starterContent = new BinaryContent()
        .fileName(UUID.randomUUID() + ".sb4starter")
        .dataUri(sb4Uri);

    UUID sb4StarterSharedUUID = contentAccessApi.share(sb4starterContent);

    String url =
        "sb4starter:?url=" + baseUrl + "&uuid="
            + sb4StarterSharedUUID.toString();
    formModel.setSb4StarterUrl(url);
  }

  private void subscribeToUploadContent(UUID resultWordId) throws Exception {
    Disposable uploadListener = contentAccessApi.subscribeToContentAccessEvent(resultWordId,
        contentAccess -> {
          model.setState(SB4StarterState.UPLOADED);
          model.setResultContent(contentAccess.getBinaryContent());
          notifyAllListeners();
        });
    eventListeners.add(uploadListener);
  }

  private void subscribeToDownloadContent(UUID wordToEditId) {
    Disposable downloadListener = contentAccessApi.subscribeToContentAccessEvent(wordToEditId,
        contentAccess -> {
          model.setState(SB4StarterState.EDITING);
          notifyAllListeners();
        });
    eventListeners.add(downloadListener);
  }

  private BinaryData createSB4Starter(UUID startWordId, String wordFileName, UUID resultWordId,
      CommandKind editCommandKind)
      throws IOException, Exception {
    SB4File startFile = new SB4File().filename(wordFileName).id(startWordId);
    SB4File resultFile = new SB4File().filename(wordFileName).id(resultWordId);

    URI uri = URI.create(baseUrl);

    SB4Command downloadCommand =
        createaDefaultSB4Command(CommandKind.CONTENTACCESSDOWNLOAD, uri, startFile);

    SB4Command wordEditCommand =
        createaDefaultSB4Command(editCommandKind, uri, startFile);

    SB4Command uploadCommand =
        createaDefaultSB4Command(CommandKind.CONTENTACCESSUPLOAD, uri, resultFile);

    SB4Starter starter = new SB4Starter()
        .id(UUID.randomUUID())
        .commands(Arrays.asList(downloadCommand, wordEditCommand, uploadCommand))
        .keepWorkingDirectory(true);

    ObjectDefinition<SB4Starter> sb4StarterDefinition = objectApi.definition(SB4Starter.class);
    return sb4StarterDefinition.serialize(starter);
  }

  protected SB4Command createaDefaultSB4Command(CommandKind commandKind, URI uri, SB4File file) {
    return new SB4Command()
        .commandKind(commandKind)
        .id(UUID.randomUUID())
        .restUrl(uri)
        .command("")
        .sb4Files(Arrays.asList(file));
  }

  private void accept() {
    acceptHandler.accept(
        model.getStartContent(),
        model.getResultContent());
    close();
  }

  private void close() {
    eventListeners.forEach(listener -> listener.dispose());
    uiNavigationApi.close(navigationTargetUUID);
  }

  private void download() {

  }

  private Storage getStorage() {
    if (storage == null) {
      storage = storageApi.get(SCHEMA);
    }
    return storage;
  }

  @Override
  protected URI getUri() {
    // TODO Auto-generated method stub
    return null;
  }

}
