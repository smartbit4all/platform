package org.smartbit4all.api.platformevent;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.platformevent.bean.PlatformEvent;
import org.smartbit4all.api.session.SessionApi;
import org.springframework.util.ObjectUtils;

public class PlatformEventBuilder {

  private final OrgApi orgApi;
  private final SessionApi sessionApi;
  private final PlatformEventApi platformEventApi;

  private URI uri;
  private URI sessionUri;
  private URI userUri;
  private OffsetDateTime timestamp;
  private String eventCode;
  private String eventCategory;
  private Exception exception;
  private String eventMessage;
  private Map<String, Object> parameters;
  private Map<String, URI> relatedObjects;


  PlatformEventBuilder(OrgApi orgApi, SessionApi sessionApi,
      PlatformEventApi platformEventApi) {
    Objects.requireNonNull(orgApi, "orgApi cannot be null!");
    Objects.requireNonNull(sessionApi, "sessionApi cannot be null!");
    Objects.requireNonNull(platformEventApi, "platformEventApi cannot be null!");

    this.orgApi = orgApi;
    this.sessionApi = sessionApi;
    this.platformEventApi = platformEventApi;

    this.parameters = new HashMap<>();
    this.relatedObjects = new HashMap<>();
  }

  public PlatformEventBuilder create(String eventCode) {
    Objects.requireNonNull(eventCode, "eventCode cannot be null!");

    this.eventCode = eventCode;
    return this;
  }

  public PlatformEventBuilder category(String eventCategory) {
    Objects.requireNonNull(eventCategory, "eventCategory cannot be null!");

    this.eventCategory = eventCategory;
    return this;
  }

  public PlatformEventBuilder exception(Exception exception) {
    Objects.requireNonNull(exception, "exception cannot be null!");

    this.exception = exception;
    return this;
  }

  public PlatformEventBuilder message(String eventMessage) {
    Objects.requireNonNull(eventMessage, "eventMessage cannot be null!");

    this.eventMessage = eventMessage;
    return this;
  }

  public PlatformEventBuilder addParameter(String key, Object parametersItem) {
    Objects.requireNonNull(parametersItem, "parametersItem cannot be null!");
    Objects.requireNonNull(key, "key cannot be null!");

    this.parameters.put(key, parametersItem);
    return this;
  }

  public PlatformEventBuilder addRelatedItem(String key, URI relatedUri) {
    Objects.requireNonNull(relatedUri, "relatedUri cannot be null!");
    Objects.requireNonNull(key, "key cannot be null!");

    this.relatedObjects.put(key, relatedUri);
    return this;
  }

  public PlatformEvent build() {
    Objects.requireNonNull(this.eventCode, "eventCode cannot be null!");
    PlatformEvent event = new PlatformEvent();

    event.setSessionUri(this.sessionApi.getSessionUri());
    event.setUserUri(this.sessionApi.getUserUri());
    event.setTimestamp(OffsetDateTime.now());
    event.eventCode(this.eventCode);

    if (!ObjectUtils.isEmpty(this.eventCategory)) {
      event.eventCategory(this.eventCategory);
    }

    if (!ObjectUtils.isEmpty(this.eventMessage)) {
      event.eventMessage(this.eventMessage);
    }

    // Set exception, overwrite message if it was set
    if (!ObjectUtils.isEmpty(this.exception)) {
      event.eventMessage(this.exception.getMessage());
      event.stackTrace(this.exception.getStackTrace().toString());
    }

    event.parameters(this.parameters);
    event.relatedObjects(this.relatedObjects);

    return event;
  }

  public boolean publish() throws Exception {
    if (this.platformEventApi == null) {
      throw new Exception("platformEventApi cannot be null!");
    }

    return this.platformEventApi.publish(this.build());
  }

}
