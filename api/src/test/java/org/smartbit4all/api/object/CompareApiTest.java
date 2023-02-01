package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.bean.ObjectChangeData;
import org.smartbit4all.api.object.bean.PropertyChangeData;
import org.smartbit4all.api.object.bean.ReferenceChangeData;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleContainerItem.ItemColorEnum;
import org.smartbit4all.api.sample.bean.SampleInlineObject;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.google.common.base.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {ApplyChangeTestConfig.class})
class CompareApiTest {

  public static final String SCHEME = "compare";

  @Autowired
  CompareApi compareApi;

  @Autowired
  ObjectApi objectApi;

  @Test
  void testSimple() {
    URI uri1 =
        objectApi.saveAsNew(SCHEME, new SampleCategory().name("value1").color(ColorEnum.BLACK));
    URI uri2 =
        objectApi.saveAsNew(SCHEME, new SampleCategory().name("value2").cost(Long.valueOf(100)));
    ObjectChangeData changes = compareApi.changes(objectApi.load(uri1), objectApi.load(uri2));
    PropertyChangeData nameChange = getProperty(changes, SampleCategory.NAME);
    assertEquals("value1", nameChange.getOldValue());
    assertEquals("value2", nameChange.getNewValue());
  }

  @Test
  void testContained() {
    URI uri1 =
        objectApi.saveAsNew(SCHEME,
            new SampleContainerItem().name("value1").itemColor(ItemColorEnum.BLACK)
                .inlineObject(new SampleInlineObject().name("inline1")));
    URI uri2 =
        objectApi.saveAsNew(SCHEME,
            new SampleContainerItem().name("value2").cost(Long.valueOf(100))
                .inlineObject(new SampleInlineObject().name("inline2")));
    ObjectChangeData changes = compareApi.changes(objectApi.load(uri1), objectApi.load(uri2));
    {
      PropertyChangeData properetyChange =
          getProperty(changes, SampleContainerItem.INLINE_OBJECT, SampleInlineObject.NAME);
      assertEquals("inline1", properetyChange.getOldValue());
      assertEquals("inline2", properetyChange.getNewValue());
    }
    System.out.println(changes);
  }

  public static final PropertyChangeData getProperty(ObjectChangeData change, String... pathes) {
    if (pathes.length == 1) {
      return change.getProperties().stream().filter(pc -> Objects.equal(pc.getPath(), pathes[0]))
          .findFirst().get();
    } else {
      ReferenceChangeData referenceChangeData = change.getReferences().stream()
          .filter(rc -> Objects.equal(rc.getPath(), pathes[0])).findFirst().get();
      return getProperty(referenceChangeData.getObjectChange(),
          Arrays.copyOfRange(pathes, 1, pathes.length));
    }
  }

}
