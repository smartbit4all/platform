package org.smartbit4all.api.contentaccess;

import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.contentaccess.bean.ContentAccessEventData;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public interface ContentAccessApi {

  public static final String SCHEME = "ContentAccessApi";

  Disposable subscribeToContentAccessEvent(UUID uuid, Consumer<ContentAccessEventData> handler);

  /**
   * Creates a UUID for the given binaryContent.
   * 
   * @param binaryContent
   * @return The given UUID.
   * @throws Exception
   */
  UUID share(BinaryContent binaryContent) throws Exception;

  /**
   * Gives the BinaryContent with its given UUID.
   * 
   * @param uuid
   * @return The found BinaryContent.
   * @throws Exception
   */
  BinaryData download(UUID uuid) throws Exception;

  /**
   * Saves binaryData into the BinaryContent with this UUID.
   * 
   * @param uuid
   * @param binaryData
   * @throws Exception
   */
  void upload(UUID uuid, BinaryData binaryData) throws Exception;

  UUID share(BinaryContentData binaryContentData) throws Exception;
}
