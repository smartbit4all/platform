package org.smartbit4all.ui.common.viewmodel.sb4starter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.smartbit4all.ui.api.navigation.UINavigationApi;
import org.smartbit4all.ui.api.sb4starter.SB4StarterWordViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import io.reactivex.rxjava3.disposables.Disposable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SB4StarterWordViewModelImpl extends ObjectEditingImpl
    implements SB4StarterWordViewModel {

  protected ObservableObjectImpl sb4Starter = new ObservableObjectImpl();

  @Value("${contentaccess.client.base-uri}")
  private String contentAccessBaseUri;

  @Autowired
  private ContentAccessApi contentAccessApi;

  @Autowired
  private UINavigationApi uiNavigationApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private StorageApi storageApi;

  private BiConsumer<BinaryContent, BinaryContent> acceptHandler;

  private SB4StarterWordFormModel sb4StarterWordFormModel;

  private List<Disposable> eventListeners = new ArrayList<>();

  private Storage storage;

  @Override
  public void initSb4StarterFormModel(SB4StarterWordFormModel sb4StarterWordFormModel,
      BiConsumer<BinaryContent, BinaryContent> acceptHandler) throws Exception {
    ref = new ApiObjectRef(null, sb4StarterWordFormModel,
        SB4StarterViewModelUtility.WORD_FORM_DESCRIPTOR);
    sb4Starter.setRef(ref);
    this.sb4StarterWordFormModel = ref.getWrapper(SB4StarterWordFormModel.class);
    this.acceptHandler = acceptHandler;

    createSB4Starter();
    sb4Starter.notifyListeners();
  }

  @Override
  public ObservableObject sb4Starter() {
    return sb4Starter;
  }

  @Override
  public void executeCommand(String commandPath, String command, Object... params) {
    switch (command) {
      case ACCEPT:
        accept();
        break;
      case DECLINE:
        close();
        break;
      case DOWNLOAD:
        break;
      default:
        super.executeCommand(commandPath, command, params);
        break;
    }
  }

  @Override
  public void createSB4Starter() throws Exception {

    UUID startWordId = contentAccessApi.share(sb4StarterWordFormModel.getStartContent());
    subscribeToDownloadContent(startWordId);

    UUID resultWordId = contentAccessApi.share(sb4StarterWordFormModel.getResultContent());
    subscribeToUploadContent(resultWordId);

    String wordFileName = sb4StarterWordFormModel.getStartContent().getFileName();
    BinaryData sb4Starter = createSB4Starter(startWordId, wordFileName, resultWordId);

    StorageObject<BinaryDataObject> sb4StarterObjectSo =
        getStorage().instanceOf(BinaryDataObject.class);

    sb4StarterObjectSo.setObject(sb4Starter.asObject());

    URI sb4Uri = getStorage().save(sb4StarterObjectSo);

    BinaryContent sb4starterContent = new BinaryContent()
        .fileName(UUID.randomUUID() + ".sb4starter")
        .dataUri(sb4Uri);

    UUID sb4StarterSharedUUID = contentAccessApi.share(sb4starterContent);

    String url =
        "sb4starter:?url=" + contentAccessBaseUri + "&uuid=" + sb4StarterSharedUUID.toString();
    sb4StarterWordFormModel.setSb4StarterUrl(url);
  }

  protected void subscribeToUploadContent(UUID resultWordId) throws Exception {
    Disposable uploadListener = contentAccessApi.subscribeToContentAccessEvent(resultWordId,
        contentAccess -> {
          sb4StarterWordFormModel.setState(SB4StarterWordState.UPLOADED);
          sb4StarterWordFormModel.setResultContent(contentAccess.getBinaryContent());
          sb4Starter.notifyListeners();
        });
    eventListeners.add(uploadListener);
  }

  protected void subscribeToDownloadContent(UUID wordToEditId) {
    Disposable downloadListener = contentAccessApi.subscribeToContentAccessEvent(wordToEditId,
        contentAccess -> {
          sb4StarterWordFormModel.setState(SB4StarterWordState.EDITING);
          sb4Starter.notifyListeners();
        });
    eventListeners.add(downloadListener);
  }

  private BinaryData createSB4Starter(UUID startWordId, String wordFileName, UUID resultWordId)
      throws IOException, Exception {
    SB4File startFile = new SB4File().filename(wordFileName).id(startWordId);
    SB4File resultFile = new SB4File().filename(wordFileName).id(resultWordId);

    URI uri = URI.create(contentAccessBaseUri);

    SB4Command downloadCommand =
        createaDefaultSB4Command(CommandKind.CONTENTACCESSDOWNLOAD, uri, startFile);

    SB4Command wordEditCommand =
        createaDefaultSB4Command(CommandKind.WORDEDIT, uri, startFile);

    SB4Command uploadCommand =
        createaDefaultSB4Command(CommandKind.CONTENTACCESSUPLOAD, uri, resultFile);

    SB4Starter starter = new SB4Starter()
        .id(UUID.randomUUID())
        .commands(Arrays.asList(downloadCommand, wordEditCommand, uploadCommand));

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

  @Override
  public void accept() {
    acceptHandler.accept(
        sb4StarterWordFormModel.getStartContent(),
        sb4StarterWordFormModel.getResultContent());
    close();
  }

  @Override
  public void close() {
    eventListeners.forEach(listener -> listener.dispose());
    uiNavigationApi.close(navigationTargetUUID);
  }

  private Storage getStorage() {
    if (storage == null) {
      storage = storageApi.get(SCHEMA);
    }
    return storage;
  }

}
