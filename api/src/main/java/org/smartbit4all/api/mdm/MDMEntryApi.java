package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * This is the super interface of the Master Data Management APIs.
 * 
 * @author Peter Boros
 * @param <O> The class of the object managed by this API.
 */
public interface MDMEntryApi<O> {

  String getName();

  Class<O> clazz();

  String getId(O object);

  /**
   * @return Returns the version URI of the currently published objects.
   */
  Map<String, URI> getPublishedMap();

  /**
   * @return Return all the published objects loaded.
   */
  Map<String, O> getPublishedObjects();

  /**
   * Save the given object as published.
   * 
   * @param object The object to save.
   * @return The URI of the currently saved draft version.
   */
  URI saveAsNewPublished(O object);

  /**
   * Save the given object as draft. To be able to identify the published version of the draft
   * object we need to have a unique business identifier for the object that can identify the given
   * object in the published list.
   * 
   * @param object The object to save.
   * @return The URI of the currently saved draft version.
   */
  URI saveAsDraft(O object);

  void cancelDraft(URI draftUri);

  /**
   * This will discard all the modified draft objects of the given given master data list.
   */
  void discardCurrentModifications();

  void publishCurrentModifications();

  /**
   * @return Returns the version URI of the current draft objects.
   */
  List<MDMDraftObjectURIEntry> getDraftUris();

  /**
   * @return Return all the draft objects loaded.
   */
  List<MDMObjectEntry<O>> getDraftObjects();

  /**
   * @return Return all the published objects loaded.
   */
  List<MDMObjectEntry<O>> publishedAndDraftObjects();

}
