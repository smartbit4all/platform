package org.smartbit4all.api.mdm;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMErrorLog;
import org.smartbit4all.api.mdm.bean.MDMErrorLogData;
import org.smartbit4all.api.mdm.bean.MDMModificationRequest;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.springframework.util.ObjectUtils;

public class MDMImportApiImpl implements MDMImportApi {
  public static final String PATH_SEPARATOR = "/";

  private static final Logger log = LoggerFactory.getLogger(MDMImportApi.class);

  private MasterDataManagementApi api;
  private ObjectApi objectApi;

  public MDMImportApiImpl(MasterDataManagementApi api, ObjectApi objectApi) {
    this.api = api;
    this.objectApi = objectApi;
  }

  @Override
  public <T> MDMErrorLog importData(MDMDefinition definition, MDMEntryDescriptor descriptor,
      MDMModificationRequest modificationRequest, Class<T> clazz) {
    MDMErrorLog errorLog = new MDMErrorLog();
    try {
      MDMEntryApi entryApi = api.getApi(definition.getName(), descriptor.getName());
      api.initiateGlobalBranch(definition.getName(), "Import session 1");

      URI branchUri = entryApi.getBranchUri();

      if (!ObjectUtils.isEmpty(descriptor.getUniquePropertyPaths())) {

        // TODO: unlimited identifier
        String identifier = descriptor.getUniquePropertyPaths().get(0).get(0);
        // Collect exist entries
        Map<String, ObjectNode> existEntries = entryApi.getBranchingList().stream()
            .map(u -> u.getOriginalUri()).map(u -> objectApi.load(u, branchUri)).collect(
                Collectors.toMap(
                    n -> n.getValueAsString(identifier),
                    n -> n));

        List<Map<String, String>> datas = modificationRequest.getData().getDefinition();
        int rowNum = 1;

        // new instance of base class
        T object = clazz.newInstance();

        // collect declared fields of base class
        Map<String, Field> declaredFields = Arrays.stream(object.getClass().getDeclaredFields())
            .collect(Collectors.toMap(Member::getName, f -> f));

        // process the lines
        for (Map<String, String> data : datas) {

          // get exists entry or create a new one
          ObjectNode existNode = existEntries.get(identifier);
          if (existNode == null) {
            existNode = objectApi.create(definition.getName(), clazz.newInstance());
          }

          Map<String, Object> subTypes = new HashMap<>();
          Map<String, Map<String, Field>> subTypesDeclaredFields = new HashMap<>();

          // process a line's entries
          for (Entry<String, String> entry : data.entrySet()) {
            try {

              // split string with PATH_SEPARATOR if it can
              String[] fields = entry.getKey().split(PATH_SEPARATOR);

              // if there is a sub class
              if (fields.length > 1) {

                // TODO: unlimited depth in object structure
                Object subType = subTypes.get(fields[0]);
                Map<String, Field> subTypeDeclaredFields = subTypesDeclaredFields.get(fields[0]);

                // if subtype not exists create subtype and subtype's fields and stored them in the
                // declared maps above
                if (subType == null) {
                  subType = declaredFields.get(fields[0]).getType().newInstance();
                  subTypes.put(fields[0], subType);
                  subTypeDeclaredFields = Arrays.stream(subType.getClass().getDeclaredFields())
                      .collect(Collectors.toMap(Member::getName, f -> f));
                  subTypesDeclaredFields.put(fields[0], subTypeDeclaredFields);
                }
                // create an object as a type of field
                Object value = objectApi.asType(subTypeDeclaredFields.get(fields[1]).getType(),
                    entry.getValue());
                // set field value
                String methodName =
                    "set" + fields[1].substring(0, 1).toUpperCase() + fields[1].substring(1);
                subType.getClass()
                    .getMethod(methodName, subTypeDeclaredFields.get(fields[1]).getType())
                    .invoke(subType, value);
              } else {
                // if field type not equal String, create a new object with specified type
                Object value = objectApi.asType(declaredFields.get(entry.getKey()).getType(),
                    entry.getValue());
                existNode.setValue(value, entry.getKey());
              }
            } catch (Exception e) {
              errorLog
                  .addDataItem(new MDMErrorLogData().rowNum(rowNum).column(entry.getKey())
                      .error(e.getMessage()));
            }
          }
          // add subtypes to the ObjectNode
          for (String key : subTypes.keySet()) {
            existNode.setValue(subTypes.get(key), key);
          }

          entryApi.save(existNode);
          rowNum++;
        }
      }
      if (errorLog.getData().isEmpty()) {
        api.mergeGlobal(definition.getName());
      } else {
        api.dropGlobal(definition.getName());
      }
    } catch (Exception e) {
      log.error(e.getLocalizedMessage(), e);
      errorLog.addDataItem(new MDMErrorLogData().error(e.getMessage()));
      api.dropGlobal(definition.getName());
    }
    return errorLog;
  }

  private Object getValue(Class<?> type, String value) {
    Object retVal = null;
    if (URI.class.equals(type)) {
      String[] fields = value.split(":");
      if (fields.length >= 3) {
        // TODO: get uri from specified mdm list
      }
    } else {
      retVal = objectApi.asType(type, value);
    }
    return retVal;
  }

}
