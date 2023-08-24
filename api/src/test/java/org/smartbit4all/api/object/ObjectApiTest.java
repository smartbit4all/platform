package org.smartbit4all.api.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectMapHelper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = {ObjectApiTestConfig.class})
class ObjectApiTest {

  private static final String ADDED_WITHTYPECLASSNAME = "ADDED_WITHTYPECLASSNAME";
  private static final String ADDED_WITHTYPECLASS = "ADDED_WITHTYPECLASS";

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

  @Autowired
  private ObjectLayoutApi objectLayoutApi;

  @Test
  void testPredefinedDefinition() throws IOException {
    ObjectDefinition<DomainObjectTestBean> definition =
        objectApi.definition(DomainObjectTestBean.class);
    assertTrue(definition.isExplicitUri());
    assertEquals(ObjectMapper.class.getName(), definition.getDefaultSerializer().getName());

    DomainObjectTestBean myBean = new DomainObjectTestBean();

    myBean.setCounter(1);
    myBean.setEnabled(false);
    // Add specific characters to check utf-8 save and load.
    myBean.setName("árvíztűrőtükörfúrógép");

    BinaryData binaryData =
        definition.getDefaultSerializer().serialize(myBean, DomainObjectTestBean.class);

    Optional<DomainObjectTestBean> deserializeResult =
        definition.getDefaultSerializer().deserialize(binaryData, DomainObjectTestBean.class);

    Assertions.assertTrue(deserializeResult.isPresent());

    DomainObjectTestBean reloadedBean = deserializeResult.get();

    assertEquals(myBean.getName(), reloadedBean.getName());

  }

  @Test
  void testUndefinedBeanDefinition() throws IOException {
    ObjectDefinition<BinaryContent> definition = objectApi.definition(BinaryContent.class);
    assertEquals(BinaryContent.class.getName().replace('.', '_'),
        definition.getAlias());
    assertEquals(ObjectMapper.class.getName(), definition.getDefaultSerializer().getName());

    BinaryContent myBean = new BinaryContent();

    myBean.setExtension("txt");
    myBean.setFileName("árvíztűrőtükörfúrógép.txt");
    myBean.setMimeType("application/text");
    myBean.setSize(Long.valueOf(1024));

    URI uri = URI.create("scheme:/path#fragment");
    definition.setUri(myBean, uri);

    BinaryData binaryData =
        definition.getDefaultSerializer().serialize(myBean, BinaryContent.class);

    Optional<BinaryContent> deserializeResult =
        definition.getDefaultSerializer().deserialize(binaryData, BinaryContent.class);

    Assertions.assertTrue(deserializeResult.isPresent());

    BinaryContent reloadedBean = deserializeResult.get();

    assertEquals(myBean.getFileName(), reloadedBean.getFileName());
    assertEquals(myBean.getUri(), definition.getUri(reloadedBean));


  }

  @Test
  void extendExistingObjectDefinition() {
    ObjectDefinition<SampleCategory> definition = objectApi.definition(SampleCategory.class);
    List<String> propertyList = new ArrayList<>(Arrays.asList(SampleCategory.URI,
        SampleCategory.NAME, SampleCategory.COLOR, SampleCategory.CONTAINER_ITEMS,
        SampleCategory.COST, SampleCategory.CREATED_AT, SampleCategory.KEY_WORDS,
        SampleCategory.LINKS, SampleCategory.SUB_CATEGORIES));
    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);
    definition.builder().addProperty(ADDED_WITHTYPECLASS, String.class)
        .addProperty(ADDED_WITHTYPECLASSNAME, Long.class.getName()).commit();

    propertyList.add(ADDED_WITHTYPECLASS);
    propertyList.add(ADDED_WITHTYPECLASSNAME);

    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

    ObjectDefinitionData savedDefinition =
        objectApi.loadLatest(definition.getDefinitionData().getUri())
            .getObject(ObjectDefinitionData.class);

    org.assertj.core.api.Assertions.assertThat(savedDefinition.getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

  }

  @Test
  void createBrandNewObjectDefinition() {
    ObjectDefinition<?> definition =
        objectDefinitionApi
            .definition(
                SampleCategory.class.getPackage().getName() + StringConstant.DOT + "Extension");
    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties())
        .isEmpty();
    definition.builder().addProperty(ADDED_WITHTYPECLASS, String.class)
        .addProperty(ADDED_WITHTYPECLASSNAME, Long.class.getName()).commit();

    List<String> propertyList = new ArrayList<>();
    propertyList.add(ADDED_WITHTYPECLASS);
    propertyList.add(ADDED_WITHTYPECLASSNAME);

    org.assertj.core.api.Assertions.assertThat(definition.getDefinitionData().getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

    ObjectDefinitionData savedDefinition =
        objectApi.loadLatest(definition.getDefinitionData().getUri())
            .getObject(ObjectDefinitionData.class);

    org.assertj.core.api.Assertions.assertThat(savedDefinition.getProperties()
        .stream().map(PropertyDefinitionData::getName))
        .containsAll(propertyList);

  }

  @Test
  void objectMapHelper() {

    Map<String, Object> baseMap = new HashMap<>();
    baseMap.put("nullvalue", null);
    baseMap.put(String.class.getSimpleName(), "My string");
    baseMap.put(SampleCategoryType.class.getSimpleName(),
        new SampleCategoryType().code("CODE 1").name("Name 1"));
    baseMap.put(Double.class.getSimpleName(), "1.0");

    {
      Map<String, Object> categoryTypeAsMap = new HashMap<>();
      categoryTypeAsMap.put(SampleCategoryType.CODE, "CODE 2");
      categoryTypeAsMap.put(SampleCategoryType.NAME, "Name 2");
      baseMap.put(Map.class.getSimpleName(),
          categoryTypeAsMap);
    }
    baseMap.put(List.class.getSimpleName(),
        Arrays.asList(new SampleCategoryType().code("LIST CODE 1").name("List Name 1"),
            new SampleCategoryType().code("LIST CODE 2").name("List Name 2")));

    {
      List<Map<String, Object>> list = new ArrayList<>();
      {
        Map<String, Object> categoryTypeAsMap = new HashMap<>();
        categoryTypeAsMap.put(SampleCategoryType.CODE, "LIST CODE 1");
        categoryTypeAsMap.put(SampleCategoryType.NAME, "List Name 1");
        list.add(categoryTypeAsMap);
      }
      {
        Map<String, Object> categoryTypeAsMap = new HashMap<>();
        categoryTypeAsMap.put(SampleCategoryType.CODE, "LIST CODE 2");
        categoryTypeAsMap.put(SampleCategoryType.NAME, "List Name 2");
        list.add(categoryTypeAsMap);
      }
      baseMap.put("listWithMap",
          list);
    }

    ObjectMapHelper mapHelper = new ObjectMapHelper(baseMap, objectApi, "test map");

    {
      String require = mapHelper.require("nullvalue", String.class);
      Assertions.assertNull(require);
    }
    {
      String require = mapHelper.requireNonNullElse("nullvalue", String.class, "apple");
      Assertions.assertEquals("apple", require);
    }

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> mapHelper.requireNonNull("nullvalue", String.class));

    {
      String require = mapHelper.require(String.class.getSimpleName(), String.class);
      Assertions.assertEquals("My string", require);
    }

    {
      SampleCategoryType require =
          mapHelper.require(SampleCategoryType.class.getSimpleName(), SampleCategoryType.class);
      Assertions.assertEquals("CODE 1", require.getCode());
      Assertions.assertEquals("Name 1", require.getName());
    }

    {
      SampleCategoryType require =
          mapHelper.require(Map.class.getSimpleName(), SampleCategoryType.class);
      Object object = mapHelper.getMap().get(Map.class.getSimpleName());

      Assertions.assertEquals(object, require);
    }

    {
      List<String> require = mapHelper.requireNonNullElseAsList("not existing key", String.class,
          Arrays.asList("s1", "s2", "s3"));
      org.assertj.core.api.Assertions.assertThat(require).containsExactly("s1", "s2", "s3");
    }

    Assertions.assertThrows(IllegalArgumentException.class,
        () -> mapHelper.requireNonNullAsList("not existing key", String.class));

    {
      List<SampleCategoryType> require =
          mapHelper.requireNonNullAsList(List.class.getSimpleName(), SampleCategoryType.class);
      org.assertj.core.api.Assertions.assertThat(require.stream().map(ct -> ct.getCode()))
          .containsExactly("LIST CODE 1", "LIST CODE 2");
    }

    {
      List<SampleCategoryType> require =
          mapHelper.requireNonNullAsList("listWithMap", SampleCategoryType.class);
      List<SampleCategoryType> listFromMap =
          (List<SampleCategoryType>) mapHelper.getMap().get("listWithMap");
      org.assertj.core.api.Assertions.assertThat(require).containsSequence(listFromMap);
    }

  }

}
