package org.smartbit4all.api.mdm;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.mdm.bean.MDMSetupEntry;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MDMSetupApiImpl implements MDMSetupApi {

  private static final ObjectMapper OBJECT_MAPPER =
      ObjectSerializerByObjectMapper.getObjectMapper();

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private MasterDataManagementApi mdmApi;

  @Override
  public void loadEntries(BinaryData setupJson) {
    List<MDMSetupEntry> setupObject;
    try (InputStream is = setupJson.inputStream()) {
      byte[] bytes = is.readAllBytes();
      setupObject =
          OBJECT_MAPPER.readValue(bytes, new TypeReference<List<MDMSetupEntry>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }

    setupObject.forEach(setupEntry -> {
      MDMEntryApi api = mdmApi.getApi(setupEntry.getDefinition(), setupEntry.getEntry());
      setupEntry.getEntries().stream().forEach(entry -> {
        ObjectDefinition<?> objectDefinition = objectApi.definition(setupEntry.getTypeClass());
        String[] uniquePath = StringConstant.toArray(setupEntry.getUniquePath());
        ObjectNode newEntryNode =
            objectApi.create(MasterDataManagementApi.SCHEMA, objectDefinition, entry);
        if (api.getList().nodesFromCache().noneMatch(
            n -> Objects.equals(newEntryNode.getValue(
                uniquePath),
                n.getValueAsString(uniquePath)))) {
          api.save(newEntryNode);
        }
      });
    });
  }

  @Override
  public void loadValueLists(BinaryData json) {
    // TODO Auto-generated method stub

  }
}
