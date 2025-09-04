package org.smartbit4all.api.mdm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
import org.smartbit4all.api.invocation.exception.BusinessLogicException;
import org.smartbit4all.api.mdm.bean.MDMDefinition;
import org.smartbit4all.api.mdm.bean.MDMEntryDescriptor;
import org.smartbit4all.api.mdm.bean.MDMModificationRequest;
import org.smartbit4all.api.mdm.bean.MDMModificationRequestData;
import org.smartbit4all.api.mdm.bean.MDMSetupEntry;
import org.smartbit4all.api.mdm.bean.MDMTableColumnDescriptor;
import org.smartbit4all.api.mdm.bean.MDMValueSetSetupEntry;
import org.smartbit4all.api.object.bean.LangString;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.value.bean.GenericValue;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MDMSetupApiImpl implements MDMSetupApi {

  private static final Logger log = LoggerFactory.getLogger(MDMSetupApiImpl.class);

  private static final ObjectMapper OBJECT_MAPPER =
      ObjectSerializerByObjectMapper.getObjectMapper();

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private MasterDataManagementApi mdmApi;
  @Autowired
  private LocaleSettingApi localeSettingApi;

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
    List<MDMValueSetSetupEntry> setupObject;
    try (InputStream is = json.inputStream()) {
      byte[] bytes = is.readAllBytes();
      setupObject =
          OBJECT_MAPPER.readValue(bytes, new TypeReference<List<MDMValueSetSetupEntry>>() {});
    } catch (Exception e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }

    setupObject
        .stream()
        .filter(e -> Boolean.FALSE
            .equals(mdmApi.entryDescriptorExists(e.getDefinition(), e.getCode(), null)))
        .forEach(e -> {
          MDMDefinition definition = mdmApi.getDefinition(e.getDefinition());
          MDMDefinitionOption option = new MDMDefinitionOption(definition);
          MDMEntryDescriptor descriptor =
              option.addDefaultDescriptor(GenericValue.class, e.getCode())
                  .tableColumns(
                      e.getDisplayedColumns().stream()
                          .map(s -> new MDMTableColumnDescriptor().name(s)
                              .addPathItem(s))
                          .toList())
                  .displayNameForm(new LangString().defaultValue(e.getName()))
                  .displayNameList(new LangString().defaultValue(e.getName()))
                  .displayNamePropertyPath(Arrays.asList(GenericValue.NAME))
                  .listPageGridViews(Collections.emptyList())
                  .isValueSet(Boolean.TRUE)
                  .vectorCollection(e.getVectorCollection() != null ? e.getVectorCollection()
                      : new VectorCollectionDescriptor())
                  .importable(Boolean.TRUE.equals(e.getImportable()))
                  .csvSeparator(e.getCsvSeparator())
                  .uniquePropertyPaths(Arrays.asList(Arrays.asList(GenericValue.CODE)));
          MDMDefinitionOption.addCreatedUpdatedExtraProperties(descriptor);
          // clear descriptors to not add already created descriptions again
          option.getDefinition().getDescriptors().clear();
          option.addDescriptor(descriptor);
          mdmApi.addNewEntries(option, null);

          if (!ObjectUtils.isEmpty(e.getFullCsvPath())) {
            try (InputStream is = new FileInputStream(e.getFullCsvPath())) {
              byte[] bytes = is.readAllBytes();
              BinaryData binaryData = new BinaryData(bytes);
              importEntriesFromCsvFile(e.getDefinition(), e.getCode(), binaryData,
                  e.getCsvSeparator(),
                  null);
            } catch (Exception e2) {
              throw new IllegalStateException(e2);
            }
          }
        });
  }

  @Override
  public void importEntriesFromCsvFile(String definition, String entry, BinaryData csvFile,
      String csvSeparator, URI branchUri) {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
        new BufferedInputStream(csvFile.inputStream()), Charset.defaultCharset()));
    String separator = ObjectUtils.isEmpty(csvSeparator) ? StringConstant.COMMA
        : csvSeparator;
    List<Map<String, String>> items = new ArrayList<>();
    int index = 1;
    try {
      String line = bufferedReader.readLine();
      List<String> keySet = Arrays.asList(line.split(separator));
      line = bufferedReader.readLine();
      while (line != null) {
        String[] fields = line.split(separator);
        Map<String, String> item = new HashMap<>();
        for (int i = 0; i < fields.length; ++i) {
          item.put(keySet.get(i), fields[i]);
        }
        items.add(item);
        line = bufferedReader.readLine();
        ++index;
      }
      MDMModificationRequest mdmModRequest =
          new MDMModificationRequest()
              .data(new MDMModificationRequestData().definition(items));

      mdmApi.importData(definition,
          entry, mdmModRequest,
          branchUri);
    } catch (Exception e) {
      throw new BusinessLogicException(
          MessageFormat.format(localeSettingApi.get("importEntriesError"), index), e);
    }
  }


}
