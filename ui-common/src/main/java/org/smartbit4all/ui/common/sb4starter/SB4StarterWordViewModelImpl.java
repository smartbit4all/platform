package org.smartbit4all.ui.common.sb4starter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObjectSerializer;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.sb4starter.bean.CommandKind;
import org.smartbit4all.api.sb4starter.bean.SB4Command;
import org.smartbit4all.api.sb4starter.bean.SB4File;
import org.smartbit4all.api.sb4starter.bean.SB4Starter;
import org.smartbit4all.core.object.ApiObjectRef;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.ui.api.sb4starter.SB4StarterWordViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordState;
import io.reactivex.rxjava3.disposables.Disposable;

public class SB4StarterWordViewModelImpl extends ObjectEditingImpl
    implements SB4StarterWordViewModel {

  protected ObservableObjectImpl sb4Starter = new ObservableObjectImpl();

  private String contentAccessApiBaseUrl;

  private BiConsumer<BinaryContent, BinaryContent> acceptHandler;

  private SB4StarterWordFormModel sb4StarterWordFormModel;

  private List<Disposable> eventListeners = new ArrayList<>();

  private ContentAccessApi contentAccessApi;

  private BinaryDataObjectSerializer serializer;

  public SB4StarterWordViewModelImpl(String contentAccessApiBaseUrl,
      BinaryDataObjectSerializer serializer,
      ContentAccessApi contentAccessApi) {
    this.contentAccessApiBaseUrl = contentAccessApiBaseUrl;
    this.serializer = serializer;
    this.contentAccessApi = contentAccessApi;
  }

  @Override
  public void initSb4StarterFormModel(SB4StarterWordFormModel sb4StarterWordFormModel,
      BiConsumer<BinaryContent, BinaryContent> acceptHandler) {
    ref = new ApiObjectRef(null, sb4StarterWordFormModel,
        SB4StarterViewModelUtility.WORD_FORM_DESCRIPTOR);
    sb4Starter.setRef(ref);
    this.sb4StarterWordFormModel = ref.getWrapper(SB4StarterWordFormModel.class);
    this.acceptHandler = acceptHandler;
  }

  @Override
  public ObservableObject sb4Starter() {
    return sb4Starter;
  }

  @Override
  public BinaryData createSB4Starter() throws Exception {
    UUID startWordId = contentAccessApi.share(sb4StarterWordFormModel.getStartContent());
    subscribeToDownloadContent(startWordId);

    UUID resultWordId = contentAccessApi.share(sb4StarterWordFormModel.getResultContent());
    subscribeToUploadContent(resultWordId);

    String wordFileName = sb4StarterWordFormModel.getStartContent().getFileName();
    BinaryData sb4Starter = createSB4Starter(startWordId, wordFileName, resultWordId);

    return sb4Starter;
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

    URI uri = URI.create(contentAccessApiBaseUrl);

    SB4Command downloadCommand =
        createaDefaultSB4Command(CommandKind.CONTENTACCESSDOWNLOAD, uri, startFile);

    SB4Command wordEditCommand =
        createaDefaultSB4Command(CommandKind.WORDEDIT, uri, startFile);

    SB4Command uploadCommand =
        createaDefaultSB4Command(CommandKind.CONTENTACCESSUPLOAD, uri, resultFile);

    SB4Starter starter = new SB4Starter()
        .id(UUID.randomUUID())
        .commands(Arrays.asList(downloadCommand, wordEditCommand, uploadCommand));

    return serializer.toJsonBinaryData(starter, SB4Starter.class);
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
  }

}
