package org.smartbit4all.core.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectNode;
import org.smartbit4all.core.utility.TextToNodeFieldProcessor.TextProcessorDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {PlatformApiConfig.class, TestFSConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
class TextToNodeFieldTest {

  public ObjectNode node;

  @Autowired
  private ObjectApi objectApi;

  private TextToNodeFieldProcessor textToNodeFieldProcessor;


  @BeforeEach
  void setUp() {
    SampleCategory sampleCategory =
        new SampleCategory().name("Dog").cost(1L).createdAt(OffsetDateTime.now());
    node = objectApi.create("NodeFieldTest", sampleCategory);
    textToNodeFieldProcessor = new TextToNodeFieldProcessor();
  }

  @AfterAll
  void tearDown() throws Exception {
    TestFileUtil.clearTestDirectory();
  }


  @Test
  void FieldFromText() {
    textToNodeFieldProcessor.setFieldFromText("name: Cat", "(?<=name: )[a-zA-Z]+", null, node,
        SampleCategory.NAME);

    textToNodeFieldProcessor.setFieldFromText("cost: 1500", "(?<=cost: )(0|[1-9][0-9]*)$",
        Long::parseLong,
        node,
        SampleCategory.COST);

    assertEquals("Cat", node.getValue(String.class, SampleCategory.NAME));
    assertEquals(1500, node.getValue(Long.class, SampleCategory.COST));
  }

  @Test
  void testFieldFromTextWithProcessDescriptors() {
    TextProcessorDescriptor nameProcessorDescriptor = new TextProcessorDescriptor(
        String.class.getName(), "(?<=name: )[a-zA-Z ]+", SampleCategory.NAME);
    TextProcessorDescriptor costProcessorDescriptor = new TextProcessorDescriptor(
        Long.class.getName(), "(?<=cost: )(0|[1-9][0-9]*)$", SampleCategory.COST);

    List<TextProcessorDescriptor> processors = new ArrayList<>();
    processors.add(nameProcessorDescriptor);
    processors.add(costProcessorDescriptor);
    textToNodeFieldProcessor.setFieldFromText("name: Cat Dog, cost: 1500", node,
        processors);

    assertEquals("Cat Dog", node.getValue(String.class, SampleCategory.NAME));
    assertEquals(1500, node.getValue(Long.class, SampleCategory.COST));
  }
}
