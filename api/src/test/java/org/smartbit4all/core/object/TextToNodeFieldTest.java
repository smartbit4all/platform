package org.smartbit4all.core.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
import org.smartbit4all.core.object.utility.TextToNodeFieldProcessor;
import org.smartbit4all.core.object.utility.TextToNodeFieldProcessor.TextProcessorConverter;
import org.smartbit4all.core.object.utility.TextToNodeFieldProcessor.TextProcessorDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {PlatformApiConfig.class, TestFSConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
class TextToNodeFieldTest {

  public ObjectNode node;

  @Autowired
  private ObjectApi objectApi;



  @BeforeEach
  void setUp() {
    SampleCategory sampleCategory =
        new SampleCategory().name("Dog").cost(1L).createdAt(OffsetDateTime.now());
    node = objectApi.create("NodeFieldTest", sampleCategory);
  }

  @AfterAll
  void tearDown() throws Exception {
    TestFileUtil.clearTestDirectory();
  }


  @Test
  void FieldFromText() {
    TextToNodeFieldProcessor.setFieldFromText("name: Cat", "(?<=name: )[a-zA-Z]+", null, node,
        SampleCategory.NAME);

    TextToNodeFieldProcessor.setFieldFromText("Átvétel kért ideje: 2023. 11. 27. 13:00",
        "(?<=Átvétel kért ideje: )\\d{4}. \\d{2}. \\d{2}. \\d{2}:\\d{2}",
        new TextProcessorOffsetDateTimeConverter(),
        node,
        SampleCategory.CREATED_AT);

    assertEquals("Cat", node.getValue(String.class, SampleCategory.NAME));
    assertEquals("2023-11-27T13:00Z",
        node.getValue(OffsetDateTime.class, SampleCategory.CREATED_AT).toString());
  }

  @Test
  void testFieldFromTextWithProcessDescriptors() {
    TextProcessorDescriptor nameProcessorDescriptor = new TextProcessorDescriptor(
        String.class.getName(), "(?<=name: )[a-zA-Z ]+", SampleCategory.NAME);

    TextProcessorDescriptor costProcessorDescriptor = new TextProcessorDescriptor(
        "org.smartbit4all.core.object.TextToNodeFieldTest$TextProcessorOffsetDateTimeConverter",
        "(?<=Átvétel kért ideje: )\\d{4}. \\d{2}. \\d{2}. \\d{2}:\\d{2}",
        SampleCategory.CREATED_AT);

    List<TextProcessorDescriptor> processors = new ArrayList<>();
    processors.add(nameProcessorDescriptor);
    processors.add(costProcessorDescriptor);
    TextToNodeFieldProcessor.setFieldFromText(
        "name: Cat Dog, Átvétel kért ideje: 2023. 11. 27. 13:00", node,
        processors);

    assertEquals("Cat Dog", node.getValue(String.class, SampleCategory.NAME));
    assertEquals("2023-11-27T13:00Z",
        node.getValue(OffsetDateTime.class, SampleCategory.CREATED_AT).toString());
  }

  public static class TextProcessorOffsetDateTimeConverter
      implements TextProcessorConverter<OffsetDateTime> {

    @Override
    public OffsetDateTime convert(String textValue) {
      String[] parts = textValue.split("\\.| ");
      int year = Integer.parseInt(parts[0]);
      int month = Integer.parseInt(parts[2]);
      int day = Integer.parseInt(parts[4]);
      int hour = Integer.parseInt(parts[6].split(":")[0]);
      int minute = Integer.parseInt(parts[6].split(":")[1]);

      return OffsetDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC);


    }


  }
}
