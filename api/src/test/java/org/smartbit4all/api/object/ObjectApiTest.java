package org.smartbit4all.api.object;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.object.bean.ObjectDefinitionData;
import org.smartbit4all.api.object.bean.PropertyDefinitionData;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ObjectApiTestConfig.class})
class ObjectApiTest {

  private static final String ADDED_WITHTYPECLASSNAME = "ADDED_WITHTYPECLASSNAME";
  private static final String ADDED_WITHTYPECLASS = "ADDED_WITHTYPECLASS";

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;

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
            .definition(SampleCategory.class.getPackageName() + StringConstant.DOT + "Extension");
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

}
