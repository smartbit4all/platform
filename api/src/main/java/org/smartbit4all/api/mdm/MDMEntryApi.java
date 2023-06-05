package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.collection.StoredList;
import org.smartbit4all.api.mdm.MDMEntryApiImpl.BranchStrategy;
import org.smartbit4all.core.object.ObjectApi;

/**
 * This is the interface of the Master Data Management entry APIs. The base implementation of the
 * master data management api. The basic feature is that if we configure an api like into the spring
 * context then it will be managed by the {@link MasterDataManagementApi}.
 * 
 * It is based on a domain bean class (assigned to the {@link #getClazz()} as constructor parameter.
 * It will have a name that is coming from the simple name of the clazz or we can set a different
 * name. In this way we can have many entries from the same clazz with different names.
 * 
 * The key property of the given object must be set also as a String[] path that can be used by the
 * {@link ObjectApi#getValueFromObjectMap(Map, String...)} or similar methods. It is used to extract
 * the key property from the object and identify the given instance uniquely. So one entry can have
 * exactly one object published in a moment with the same identifier.
 * 
 * The entries can have
 * 
 * @author Peter Boros
 * @param <O> The class of the object managed by this API.
 */
public interface MDMEntryApi<O> {

  String getName();

  Class<O> getClazz();

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
   * @return Return the published {@link StoredList} if the published list name was set.
   */
  StoredList getPublishedList();

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
  List<MDMObjectEntry<O>> getPublishedAndDraftObjects();

  /**
   * @return The branching strategy of the given entry that can be {@link BranchStrategy#LOCAL} if
   *         the draft management of the given entry defines a local private branch for the given
   *         entry. Or it can be {@link BranchStrategy#GLOBAL} if modification of the given entry
   *         joins to a global branch.
   */
  BranchStrategy getBranchStrategy();

}
