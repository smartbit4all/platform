package org.smartbit4all.api.mdm;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMErrorLog;
import org.smartbit4all.api.mdm.bean.MDMErrorLogData;
import org.smartbit4all.api.mdm.bean.MDMModificationRequest;
import org.smartbit4all.api.object.bean.ObjectPropertyValue;
import org.smartbit4all.core.object.ObjectApi;
import static java.util.stream.Collectors.toList;

public class MDMImportApiImpl implements MDMImportApi {
  public static final String PATH_SEPARATOR = "/";

  private static final Logger log = LoggerFactory.getLogger(MDMImportApi.class);

  private MasterDataManagementApi masterDataManagementApi;
  private ObjectApi objectApi;

  public MDMImportApiImpl(MasterDataManagementApi masterDataManagementApi, ObjectApi objectApi) {
    this.masterDataManagementApi = masterDataManagementApi;
    this.objectApi = objectApi;
  }

  @Override
  public <T> MDMErrorLog importData(MDMDefinition definition, MDMEntryDescriptor descriptor,
      MDMModificationRequest modificationRequest, Class<T> clazz) {
    MDMErrorLog errorLog = new MDMErrorLog();
    boolean globalBranchInit = false;
    try {
      MDMEntryApi entryApi =
          masterDataManagementApi.getApi(definition.getName(), descriptor.getName());

      URI branchUri = masterDataManagementApi.getGlobalBranch(definition.getName());

      if (branchUri == null) {
        branchUri =
            masterDataManagementApi.initiateGlobalBranch(definition.getName(), "Import session 1");
        globalBranchInit = true;
      }

      entryApi.updateList(null, modificationRequest.getData().getDefinition().stream()
          .map(objMap -> constructHierarchicalMap(objMap)).collect(toList()));

      if (globalBranchInit) {
        if (errorLog.getData().isEmpty()) {
          masterDataManagementApi.mergeGlobal(definition.getName());
        } else {
          masterDataManagementApi.dropGlobal(definition.getName());
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      errorLog.addDataItem(new MDMErrorLogData().error(e.getMessage()));
      if (globalBranchInit) {
        masterDataManagementApi.dropGlobal(definition.getName());
      }
    }
    return errorLog;
  }

  /**
   * This function restructure the map and create sub maps if the key of a value is a path. The path
   * looks like this innerobject/another/property. In this case we will have an innerobject key in
   * the root map that is map and a another map again and the property will be placed into this.
   * 
   * @param data The original flatten map.
   * @return The resulting map with the inner structure.
   */
  public static final Map<String, Object> constructHierarchicalMap(Map<String, String> data) {
    Objects.requireNonNull(data);
    Map<String, Object> result = new HashMap<>();
    for (Entry<String, String> entry : data.entrySet()) {
      String path = entry.getKey();
      String value = entry.getValue();
      addValue(result, path, path, value);
    }
    return result;
  }

  private static void addValue(Map<String, Object> result, String originalPath, String path,
      String value) {
    if (!path.contains(PATH_SEPARATOR)) {
      result.put(path, value);
    } else {
      // split string with PATH_SEPARATOR if it can
      int firstSepIndex = path.indexOf(PATH_SEPARATOR);
      String subMapName = path.substring(0, firstSepIndex);
      Object subMap = result.getOrDefault(subMapName, new HashMap<>());
      if (subMap instanceof Map) {
        addValue((Map<String, Object>) subMap, originalPath, path.substring(firstSepIndex + 1),
            value);
      } else {
        log.error(
            "Unable to {} property because the {} is a sub object and a simple property at the same time.",
            originalPath, subMapName);
      }
    }
  }

  private Object getValue(Class<?> type, String value) {
    Object retVal = null;
    // if type is URI
    if (URI.class.equals(type)) {
      // value = definition:class:identifier:value
      String[] fields = value.split(":");
      if (fields.length >= 4) {
        // create an MDM api for specified class
        MDMEntryApi entryApi = masterDataManagementApi.getApi(fields[0], fields[1]);
        // locate ObjectNode by identifier and value
        // ObjectNode existEntry = entryApi.getList().nodes()
        // .filter(n -> fields[3].equals(n.getValueAsString(fields[2]))).findFirst().orElse(null);
        Map<String, Object> existEntry = entryApi.lookup().findByUnique(
            new ObjectPropertyValue().path(Arrays.asList(fields[2])).value(fields[3]));
        // if found it set retVal
        if (existEntry != null) {
          // retVal = existEntry.getObjectUri();
          retVal = existEntry.get("uri");
        }
      }
    } else {
      retVal = objectApi.asType(type, value);
    }
    return retVal;
  }

}
