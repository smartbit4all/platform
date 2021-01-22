package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApiObjectsTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testSampleBeanRead() throws ExecutionException {
    BeanMeta meta = ApiObjects.meta(SampleBean.class);
    String description = meta.getDescription();
    System.out.println(description);
    Assertions.assertEquals(description, "org.smartbit4all.api.object.SampleBean\r\n" +
        "\tClass(java.lang.Class) get: getClass set: -\r\n" +
        "\tCounter(long) get: getCounter set: setCounter\r\n" +
        "\tName(java.lang.String) get: getName set: setName\r\n" +
        "\tReadOnlyLong(java.lang.Long) get: getReadOnlyLong set: -\r\n" +
        "\tStringList(java.util.List) get: getStringList set: setStringList");
    // fail("Not yet implemented");
  }

  @Test
  void testSampleBeanModification() {
    SampleBean bean1 = new SampleBean();
    ApiObjectRef<SampleBean> bean1Ref =
        new ApiObjectRef<SampleBean>(bean1, URI.create("myApi:/SampleBean#672356325"));
    bean1Ref.setValue("name", "myName");
    bean1Ref.setValue("Counter", 1);
    bean1Ref.setValue("stringList", Arrays.asList("first", "second"));

    Assertions.assertEquals(bean1.getName(), "myName");
    Assertions.assertEquals(bean1.getCounter(), Long.valueOf(1));

    bean1Ref.setValue(bean1::setName, "myName-2");
    

    Assertions.assertEquals("myName-2", bean1.getName());
    
    bean1Ref.setValue(SampleBean::setName, "myName-3");
    Assertions.assertEquals("myName-3", bean1.getName());
  }

}
