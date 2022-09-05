package org.smartbit4all.core.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class ObjectMapperRawMapTest {

  private static ObjectMapper objectMapper;

  @BeforeAll
  static void prepare() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    // Task 5641: Storage - OffsetDateTime formatting with default serializer
    // objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  }

  @Test
  void testReadWriteComplexMap() throws JsonGenerationException, JsonMappingException, IOException {

    final int listCounter = 3;

    Map<String, Object> rootObject = new HashMap<>();
    rootObject.put("propertyString", "it4all");
    rootObject.put("propertyLong", Long.valueOf(2020));
    rootObject.put("propertyDouble", Double.valueOf(100.0));
    rootObject.put("propertyOffsetDateTime", OffsetDateTime.now());
    rootObject.put("propertyURI", URI.create(
        "testscheme:/org_smartbit4all_api_binarydata_BinaryDataObject/2022/8/30/15/47/dfec75ac-74d8-45aa-b0f7-e559396b3e00"));
    rootObject.put("propertyUUID", UUID.randomUUID());
    rootObject.put("propertyEnum", OffsetDateTime.now());

    rootObject.put("containedBeanProperty", ((Supplier<Object>) () -> {
      Map<String, Object> map = new HashMap<>();
      map.put("detailString", "it4all - detail");
      return map;
    }).get());

    rootObject.put("listOfContainedBean", ((Supplier<Object>) () -> {
      List<Object> list = new ArrayList<>();
      for (int i = 0; i < listCounter; i++) {
        final int index = i;
        list.add(((Supplier<Object>) () -> {
          Map<String, Object> map = new HashMap<>();
          map.put("detailString", String.valueOf(index));
          map.put("type", ContainedBeanType.RED);
          return map;
        }).get());
      }
      return list;
    }).get());

    rootObject.put("mapOfContainedBean", ((Supplier<Object>) () -> {
      Map<String, Object> map = new HashMap<>();
      for (int i = 0; i < listCounter; i++) {
        final int index = i;
        map.put(String.valueOf(index), ((Supplier<Object>) () -> {
          Map<String, Object> map1 = new HashMap<>();
          map1.put("detailString", String.valueOf(index));
          map1.put("type", ContainedBeanType.RED);
          return map1;
        }).get());
      }
      return map;
    }).get());


    ByteArrayOutputStream os = new ByteArrayOutputStream();
    // We write and read in UTF-8 to enable international characters.
    OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
    objectMapper.writeValue(osw, rootObject);
    osw.close();
    os.close();

    System.out.println(os.toString());

    RootBean rootBean;
    {
      InputStreamReader isr = null;
      // We write and read in UTF-8 to enable international characters.
      isr = new InputStreamReader(new ByteArrayInputStream(os.toByteArray()),
          StandardCharsets.UTF_8);
      rootBean = objectMapper.readValue(isr, RootBean.class);
    }

    Map readMap;
    {
      InputStreamReader isr = null;
      // We write and read in UTF-8 to enable international characters.
      isr = new InputStreamReader(new ByteArrayInputStream(os.toByteArray()),
          StandardCharsets.UTF_8);
      readMap = objectMapper.readValue(isr, Map.class);
    }

    Assertions.assertEquals(rootObject.get("propertyURI").toString(), readMap.get("propertyURI"));

    assertion(listCounter, rootObject, rootBean);

    // Convert the object directly from map
    RootBean convertValue = objectMapper.convertValue(rootObject, RootBean.class);

    assertion(listCounter, rootObject, convertValue);
  }

  private void assertion(final int listCounter, Map<String, Object> rootObject, RootBean rootBean) {
    Assertions.assertEquals(rootObject.get("propertyString"), rootBean.getPropertyString());
    Assertions.assertEquals(rootObject.get("propertyLong"), rootBean.getPropertyLong());
    Assertions.assertEquals(rootObject.get("propertyDouble"), rootBean.getPropertyDouble());
    Assertions.assertEquals(true, ((OffsetDateTime) rootObject.get("propertyOffsetDateTime"))
        .isEqual(rootBean.getPropertyOffsetDateTime()));

    Assertions.assertEquals(((Map) rootObject.get("containedBeanProperty")).get("detailString"),
        rootBean.getContainedBeanProperty().getDetailString());

    List<Map<String, Object>> list =
        (List<Map<String, Object>>) rootObject.get("listOfContainedBean");
    for (int i = 0; i < listCounter; i++) {
      Object expectedString = list.get(i).get("detailString");
      Object expectedType = list.get(i).get("type");
      Assertions.assertEquals(expectedString,
          rootBean.getListOfContainedBean().get(i).getDetailString());
      Assertions.assertEquals(expectedType,
          rootBean.getListOfContainedBean().get(i).getType());
      Assertions.assertEquals(expectedString,
          rootBean.getMapOfContainedBean().get(expectedString).getDetailString());
      Assertions.assertEquals(expectedType,
          rootBean.getMapOfContainedBean().get(expectedString).getType());
    }
  }

}
