package org.smartbit4all.core.object;

import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ObjectApiTestConfig.class})
public class ObjectApiTest {

  @Autowired
  private ObjectApi objectApi;

  @Test
  public void testPredefinedDefinition() {
    ObjectDefinition<MasterBean> definition = objectApi.definition(MasterBean.class);
    assertEquals(MasterBean.class.getName().replace('.', '-'), definition.getAlias());
    assertEquals(ObjectMapper.class.getName(), definition.getDefaultSerializer().getName());

    MasterBean myBean = new MasterBean();

    myBean.setCounter(1);
    myBean.setEnabled(false);
    // Add specific characters to check utf-8 save and load.
    myBean.setName("árvíztűrőtükörfúrógép");

    BinaryData binaryData =
        definition.getDefaultSerializer().serialize(myBean, MasterBean.class);

    Optional<MasterBean> deserializeResult =
        definition.getDefaultSerializer().deserialize(binaryData, MasterBean.class);

    Assertions.assertTrue(deserializeResult.isPresent());

    MasterBean reloadedBean = deserializeResult.get();

    assertEquals(myBean.getName(), reloadedBean.getName());

  }

  @Test
  public void testUndefinedBeanDefinition() {
    ObjectDefinition<BinaryContent> definition = objectApi.definition(BinaryContent.class);
    assertEquals(BinaryContent.class.getName().replace('.', '-'),
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

}
