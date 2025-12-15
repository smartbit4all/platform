package org.smartbit4all.api.contentaccess;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.contentaccess.bean.ContentAccessEventData;
import org.smartbit4all.api.sb4starter.bean.SB4Command;
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

  /**
   * Share an sb4Starter object which is runnable with the Docu360Starter.
   * 
   * @param commands Sb4Starter commands.
   * @param baseUri Base uri of the server application.
   * @return Openable link which can be processed by the browser if the Docu360Starter is installed.
   */
  URI shareSb4StarterFile(List<SB4Command> commands, URI baseUri);
}
