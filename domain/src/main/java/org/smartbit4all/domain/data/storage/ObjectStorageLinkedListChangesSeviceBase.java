package org.smartbit4all.domain.data.storage;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.storage.bean.ObjectMap;
import org.smartbit4all.api.storage.bean.ObjectMapRequest;
import org.smartbit4all.api.storage.bean.StorageSaveEventObject;
import org.smartbit4all.api.storage.bean.StorageSettings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class ObjectStorageLinkedListChangesSeviceBase
    implements ObjectStorageLinkListChangesService, ObjectStorageSaveSucceedListener,
    InitializingBean {

  private static final String SETTINGS_LINKEDEVENTS = "LINKEDEVENTS";
  private static final String SETTINGS_LINKEDEVENTS_FIRST = "FIRST";
  private static final String SETTINGS_LINKEDEVENTS_LAST = "LAST";

  @Autowired
  private StorageApi storageApi;

  private Storage llcStorage;
  private StorageSettings llcSettings;

  private List<ObjectStorageLinkedListChangeListener> linkedListChangeListeners = new ArrayList<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    llcSettings = getLlcStorage().settings().getObject();
    checkFirstEmptyEvent();
  }

  @Override
  public void doOnSave(StorageSaveEvent saveEvent) {
    if (StorageSaveEventObject.class.equals(saveEvent.getType())) {
      // skip the StorageSaveEventObject saves to avoid loops
      return;
    }

    URI oldObjUri = saveEvent.getOldVersionUri();
    URI newObjUri = saveEvent.getNewVersionUri();
    StorageSaveEventObject saveEventObject = new StorageSaveEventObject()
        .oldVersion(oldObjUri)
        .newVersion(newObjUri);

    // TODO we should check the if the last saved version is same as the current object to save
    // it can happen if multiple ObjectStorageSaveSucceedListeners apply to an object

    StorageObject<StorageSaveEventObject> saveEventObjectSo =
        getLlcStorage().instanceOf(StorageSaveEventObject.class);
    saveEventObjectSo.setObject(saveEventObject).onSucceed(e -> handleSaveEventObjectSave(e));
    getLlcStorage().save(saveEventObjectSo);
  }

  private void handleSaveEventObjectSave(StorageSaveEvent e) {
    StorageSaveEventObject saveEventObject = (StorageSaveEventObject) e.getNewVersion();
    if (saveEventObject == null || saveEventObject.getNewVersion() == null) {
      return;
    }

    updateLinkedList(saveEventObject);

    linkedListChangeListeners.forEach(l -> l.onChange(saveEventObject, this));
  }

  private void updateLinkedList(StorageSaveEventObject saveEventObject) {
    URI newSeoUri = saveEventObject.getUri();
    URI lastEventUri = getLinkedListMapUri(SETTINGS_LINKEDEVENTS_LAST);
    StorageObject<StorageSaveEventObject> lastSeo =
        getLlcStorage().load(lastEventUri, StorageSaveEventObject.class);
    lastSeo.getObject().setNextEvent(newSeoUri);
    lastSeo.onSucceed(e -> updateLinkedListMap(SETTINGS_LINKEDEVENTS_LAST, newSeoUri));
    getLlcStorage().save(lastSeo);
  }

  private void checkFirstEmptyEvent() {
    URI firstEventUri = getLinkedListMapUri(SETTINGS_LINKEDEVENTS_FIRST);
    if (firstEventUri == null) {
      firstEventUri = getLlcStorage().saveAsNew(new StorageSaveEventObject());
      updateLinkedListMap(SETTINGS_LINKEDEVENTS_FIRST, firstEventUri);
      updateLinkedListMap(SETTINGS_LINKEDEVENTS_LAST, firstEventUri);
    }
  }

  private Storage getLlcStorage() {
    if (llcStorage == null) {
      llcStorage = storageApi.get("storageSaveEventObject");
    }
    return llcStorage;
  }

  private void updateLinkedListMap(String key, URI firstEventUri) {
    updateSettingsMap(SETTINGS_LINKEDEVENTS, key, firstEventUri);
  }

  private URI getLinkedListMapUri(String key) {
    ObjectMap linkedEventsMap = getSettingsMap(SETTINGS_LINKEDEVENTS);
    URI firstEventUri = linkedEventsMap.getUris().get(key);
    return firstEventUri;
  }

  private ObjectMap getSettingsMap(String map) {
    ObjectMap linkedEventsMap =
        getLlcStorage().getAttachedMap(llcSettings.getUri(), map);
    return linkedEventsMap;
  }

  private void updateSettingsMap(String map, String key, URI firstEventUri) {
    getLlcStorage().updateAttachedMap(llcSettings.getUri(), new ObjectMapRequest()
        .mapName(map)
        .putUrisToAddItem(key, firstEventUri));
  }

  @Override
  public void addNewChangeListener(ObjectStorageLinkedListChangeListener listener) {
    Objects.requireNonNull(listener, "listener can not be null!");
    linkedListChangeListeners.add(listener);
  }

  @Override
  public void addNewChangeListener(ObjectStorageLinkedListChangeListener listener,
      StorageSaveEventObject applyFrom) {
    Objects.requireNonNull(applyFrom, "applyFrom can not be null!");

    addNewChangeListener(listener);
    doChangesOnListFrom(getFirst(), listener);
  }

  @Override
  public StorageSaveEventObject getFirst() {
    URI firstEventUri = getLinkedListMapUri(SETTINGS_LINKEDEVENTS_FIRST);
    return getLlcStorage().read(firstEventUri, StorageSaveEventObject.class);
  }

  @Override
  public StorageSaveEventObject getLast() {
    URI firstEventUri = getLinkedListMapUri(SETTINGS_LINKEDEVENTS_LAST);
    return getLlcStorage().read(firstEventUri, StorageSaveEventObject.class);
  }

  @Override
  public StorageSaveEventObject getNext(StorageSaveEventObject currentEventObject) {
    Objects.requireNonNull(currentEventObject, "currentEventObject can not be null!");

    URI nextEventUri = currentEventObject.getNextEvent();
    if (nextEventUri == null) {
      return null;
    }
    return getLlcStorage().read(nextEventUri, StorageSaveEventObject.class);
  }

  @Override
  public void doChangesOnListFrom(StorageSaveEventObject fromEvent,
      ObjectStorageLinkedListChangeListener chengeListener) {
    Objects.requireNonNull(fromEvent, "fromEvent can not be null!");
    Objects.requireNonNull(chengeListener, "chengeListener can not be null!");

    StorageSaveEventObject eventIter = fromEvent;
    while (eventIter != null) {
      chengeListener.onChange(eventIter, this);
      eventIter = getNext(eventIter);
    }
  }

}
