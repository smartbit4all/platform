package org.smartbit4all.api.contentaccess;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.contentaccess.bean.ContentAccessEventData;
import org.smartbit4all.api.contentaccess.bean.Direction;
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.api.sb4starter.bean.SB4Command;
import org.smartbit4all.api.sb4starter.bean.SB4Starter;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectSerializer;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ContentAccessApiImpl implements ContentAccessApi {

  private static final Logger log = LoggerFactory.getLogger(ContentAccessApiImpl.class);

  private ObjectShareApi objectShareApi;

  private StorageApi storageApi;
  private Storage storage;

  private BinaryContentApi binaryContentApi;

  private PublishSubject<ContentAccessEventData> publisher;

  @Autowired
  @Qualifier("objectMapperSerializer")
  private ObjectSerializer serializer;

  @Autowired
  private ObjectApi objectApi;

  @Value("${sb4starter.url.host:sb4starter}")
  private String sb4starterUrlHost;

  @Value("${openapi.contentAccess.base-path:}")
  private String contentAccessBasePath;

  public ContentAccessApiImpl(
      ObjectShareApi objectShareApi,
      StorageApi storageApi,
      BinaryContentApi binaryContentApi) {

    this.objectShareApi = objectShareApi;
    this.storageApi = storageApi;
    this.binaryContentApi = binaryContentApi;
    publisher = PublishSubject.create();
  }

  @Override
  public Disposable subscribeToContentAccessEvent(UUID uuid,
      Consumer<ContentAccessEventData> handler) {
    if (uuid == null) {
      return publisher.subscribe(handler);
    } else {
      return publisher.filter(kikuldesEventData -> kikuldesEventData.getUuid().equals(uuid))
          .subscribe(handler);
    }
  }

  @Override
  public UUID share(BinaryContent binaryContent) throws Exception {
    StorageObject<BinaryContent> storageObject = getStorage().instanceOf(BinaryContent.class);
    storageObject.setObject(binaryContent);
    URI savedBinaryContentUri = getStorage().save(storageObject);
    return objectShareApi.registerUri(savedBinaryContentUri);
  }

  @Override
  public UUID share(BinaryContentData binaryContentData) throws Exception {
    return objectShareApi.registerUri(
        getStorage().saveAsNew(new BinaryContent().dataUri(binaryContentData.getDataUri())
            .fileName(binaryContentData.getFileName()).extension(binaryContentData.getExtension())
            .mimeType(binaryContentData.getMimeType())
            .size(binaryContentData.getSize())));
  }

  @Override
  public BinaryData download(UUID uuid) throws Exception {
    URI contentUri = objectShareApi.resolveUUID(uuid);

    if (contentUri != null) {
      BinaryContent content = getStorage().read(contentUri, BinaryContent.class);
      BinaryData data = binaryContentApi.getBinaryData(content);

      publisher.onNext(new ContentAccessEventData()
          .binaryContent(content)
          .direction(Direction.DOWNLOAD)
          .uuid(uuid));

      return data;
    } else {
      throw new NoSuchElementException("The content was not found with the given uuid");
    }
  }

  @Override
  public void upload(UUID uuid, BinaryData binaryData) throws Exception {
    URI contentUri = objectShareApi.resolveUUID(uuid);

    if (contentUri != null) {
      StorageObject<BinaryContent> contentSO = getStorage().load(contentUri, BinaryContent.class);

      StorageObject<BinaryDataObject> storageObj =
          getStorage().instanceOf(BinaryDataObject.class);
      storageObj.setObject(binaryData.asObject());
      URI savedDataUri = getStorage().save(storageObj);

      contentSO.getObject().dataUri(savedDataUri);
      getStorage().save(contentSO);

      publisher.onNext(new ContentAccessEventData()
          .binaryContent(contentSO.getObject())
          .direction(Direction.UPLOAD)
          .uuid(uuid));

    } else {
      throw new NoSuchElementException("The content was not found with the given uuid");
    }
  }

  private Storage getStorage() {
    if (storage == null) {
      storage = storageApi.get(SCHEME);
    }
    return storage;
  }


  @Override
  public URI shareSb4StarterFile(List<SB4Command> commands, URI baseUri) {
    UUID sb4StarterId = UUID.randomUUID();
    // keep working directory should be configurable?
    SB4Starter sb4Starter = new SB4Starter()
        .id(sb4StarterId)
        .keepWorkingDirectory(false);

    sb4Starter.commands(commands);
    try {
      URI sb4StarterUri = saveSB4Starter(sb4Starter);
      BinaryContentData sb4StarterData = new BinaryContentData()
          .fileName(UUID.randomUUID() + ".sb4starter")
          .dataUri(sb4StarterUri);

      UUID sb4StarterSharedUUID = share(sb4StarterData);

      URI contentAccessBaseUri = URI.create(baseUri + contentAccessBasePath);

      return URI
          .create(sb4starterUrlHost + ":?url=" + contentAccessBaseUri + "&uuid="
              + sb4StarterSharedUUID);
    } catch (Exception e) {
      log.error("Cannot create sb4starter object ", e);
      throw new IllegalStateException("Unable to create sb4starterobject.", e);
    }
  }


  private URI saveSB4Starter(SB4Starter sb4Starter) {
    BinaryData serializedSb4Starter = serializer.serialize(sb4Starter, SB4Starter.class);
    BinaryDataObject sb4StarterObject = serializedSb4Starter.asObject();
    return objectApi.saveAsNew(SCHEME, sb4StarterObject);
  }

}
