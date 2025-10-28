package org.smartbit4all.api.mdm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.collection.bean.VectorCollectionDescriptor;
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
            .equals(mdmApi.entryDescriptorExists(e.getDefinition(), e.getCode(), null)) ||
            mdmApi.getApi(e.getDefinition(), e.getCode()).getList().uris().isEmpty())
        .forEach(e -> {
          if (Boolean.FALSE
              .equals(mdmApi.entryDescriptorExists(e.getDefinition(), e.getCode(), null))) {
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
          }
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
    String separator = ObjectUtils.isEmpty(csvSeparator) ? StringConstant.COMMA
        : csvSeparator;
    List<Map<String, String>> items = new ArrayList<>();
    try (InputStream is = csvFile.inputStream()) {
      byte[] allBytes = is.readAllBytes();
      List<String> lines = Arrays.asList(new String(allBytes).split("\n"))
          .stream()
          .map(s -> s.replaceAll("\r", StringConstant.EMPTY))
          .toList();
      List<String> keySet = Arrays.asList(lines.get(0).split(separator));
      if (lines.size() > 1) {
        IntStream.range(1, lines.size()).forEach(i -> {
          String line = lines.get(i);
          String[] fields = line.split(separator);
          Map<String, String> item = new HashMap<>();
          for (int j = 0; j < keySet.size(); ++j) {
            if (j < fields.length) {
              item.put(keySet.get(j), fields[j]);
            } else {
              item.put(keySet.get(j), StringConstant.EMPTY);
            }
          }
          items.add(item);
        });
      } else {
        log.error("The csv file only contained the header");
        return;
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new IllegalStateException(e);
    }
    MDMModificationRequest mdmModRequest =
        new MDMModificationRequest()
            .data(new MDMModificationRequestData().definition(items));
    mdmApi.importData(definition,
        entry, mdmModRequest,
        branchUri);
    MDMEntryApi entryApi = mdmApi.getApi(definition, entry);
    if (entryApi.getDescriptor().getVectorCollection() != null
        && !ObjectUtils
            .isEmpty(entryApi.getDescriptor().getVectorCollection().getVectorCollectionName())) {
      entryApi.updateAllIndices();
    }
  }

}
