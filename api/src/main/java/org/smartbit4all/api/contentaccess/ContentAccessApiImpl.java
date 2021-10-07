package org.smartbit4all.api.contentaccess;

import java.net.URI;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.contentaccess.bean.ContentAccessEventData;
import org.smartbit4all.api.contentaccess.bean.Direction;
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageObject;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class ContentAccessApiImpl implements ContentAccessApi {

  private ObjectShareApi objectShareApi;

  private Storage storage;

  private BinaryContentApi binaryContentApi;

  private PublishSubject<ContentAccessEventData> publisher;

  public ContentAccessApiImpl(ObjectShareApi objectShareApi, Storage storage,
      BinaryContentApi binaryContentApi) {

    this.objectShareApi = objectShareApi;
    this.storage = storage;
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
  public UUID share() throws Exception {
    BinaryContent binaryContent = new BinaryContent();
    binaryContent.setDataUri(new URI(SCHEME, null, "/" + UUID.randomUUID(), null));
    return share(binaryContent);
  }

  @Override
  public UUID share(BinaryContent binaryContent) throws Exception {
    StorageObject<BinaryContent> storageObject = storage.instanceOf(BinaryContent.class);
    storageObject.setObject(binaryContent);
    URI savedBinaryContentUri = storage.save(storageObject);
    return objectShareApi.registerUri(savedBinaryContentUri);
  }

  @Override
  public BinaryData download(UUID uuid) throws Exception {
    URI contentUri = objectShareApi.resolveUUID(uuid);

    if (contentUri != null) {
      BinaryContent content = storage.read(contentUri, BinaryContent.class).get();
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
      BinaryContent content = storage.read(contentUri, BinaryContent.class).get();
      binaryContentApi.uploadContent(content, binaryData, content.getDataUri());

      publisher.onNext(new ContentAccessEventData()
          .binaryContent(content)
          .direction(Direction.UPLOAD)
          .uuid(uuid));

    } else {
      throw new NoSuchElementException("The content was not found with the given uuid");
    }
  }
}
