package org.smartbit4all.api.view;

import java.net.URI;
import java.util.UUID;
import org.smartbit4all.api.collection.CollectionApi;
import org.smartbit4all.api.collection.StoredMap;
import org.smartbit4all.api.view.bean.SmartLinkData;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;

public class SmartLinkApiImpl implements SmartLinkApi {

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Override
  public URI publishView(String channel, View view) {
    // TODO sanitize channel name
    // TODO basePaht in url?
    StoredMap linkMap = collectionApi.map(ViewContextService.SCHEMA, channel);
    UUID uuid = UUID.randomUUID();
    SmartLinkData smartLinkData = new SmartLinkData()
        .uuid(uuid)
        .view(view)
        .url("/" + channel + "/" + uuid.toString());
    ObjectNode smartLinkNode = objectApi.create(channel, smartLinkData);
    URI smartLinkUri = objectApi.save(smartLinkNode);
    linkMap.put(uuid.toString(), smartLinkUri);
    return smartLinkUri;
  }

  @Override
  public ObjectNode getSmartLink(String channel, UUID smartLinkUuid) {
    StoredMap linkMap = collectionApi.map(ViewContextService.SCHEMA, channel);
    URI linkUri = linkMap.uris().get(smartLinkUuid.toString());
    if (linkUri == null) {
      return null;
    }
    return objectApi.loadLatest(linkUri);
  }

}
