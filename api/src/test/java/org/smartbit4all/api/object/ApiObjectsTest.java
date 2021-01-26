package org.smartbit4all.api.object;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ApiObjectsTest {

  private static Set<Class<?>> domainBeans;
  private static Map<Class<?>, ApiBeanDescriptor> descriptors;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    domainBeans = new HashSet<>();
    domainBeans.add(MasterBean.class);
    domainBeans.add(MasterDetailBean.class);
    domainBeans.add(ReferredBean.class);
    domainBeans.add(ReferredDetailBean.class);
    descriptors = ApiBeanDescriptor.of(domainBeans);
    {
      ApiBeanDescriptor descriptor = descriptors.get(MasterBean.class);
      descriptor.getDetailDescriptors().put("Details", descriptors.get(MasterDetailBean.class));
    }
    {
      ApiBeanDescriptor descriptor = descriptors.get(ReferredBean.class);
      descriptor.getDetailDescriptors().put("Details", descriptors.get(ReferredDetailBean.class));
    }
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testSampleBeanRead() throws ExecutionException {
    BeanMeta meta = ApiObjects.meta(MasterBean.class, descriptors);
    String description = meta.getDescription();
    System.out.println(description);
    String br = System.lineSeparator();
    StringBuilder expectedDescBuilder = new StringBuilder();
    expectedDescBuilder.append("org.smartbit4all.api.object.MasterBean").append(br)
        .append("\tClass(java.lang.Class) get: getClass set: -").append(br)
        .append("\tCounter(long) get: getCounter set: setCounter").append(br)
        .append("\tDetails(java.util.List) get: getDetails set: setDetails").append(br)
        .append("\tName(java.lang.String) get: getName set: setName").append(br)
        .append("\tReadOnlyLong(java.lang.Long) get: getReadOnlyLong set: -").append(br)
        .append(
            "\tReferred(org.smartbit4all.api.object.ReferredBean) get: getReferred set: setReferred")
        .append(br)
        .append("\tStringList(java.util.List) get: getStringList set: setStringList");
    Assertions.assertEquals(expectedDescBuilder.toString(), description);
    // fail("Not yet implemented");
  }

  @Test
  void testSampleBeanModification() {
    MasterBean bean1 = new MasterBean();
    ApiObjectRef bean1Ref =
        new ApiObjectRef(bean1, URI.create("myApi:/SampleBean#672356325"), descriptors);
    bean1Ref.setValue("name", "myName");
    bean1Ref.setValue("Counter", 1);
    bean1Ref.setValue("stringList", Arrays.asList("first", "second"));

    Assertions.assertEquals(bean1.getName(), "myName");
    Assertions.assertEquals(bean1.getCounter(), Long.valueOf(1));

    // bean1Ref.setValue(bean1::setName, "myName-2");
    //
    //
    // Assertions.assertEquals("myName-2", bean1.getName());

    bean1Ref.setValue(MasterBean::setName, "myName-3");
    Assertions.assertEquals("myName-3", bean1.getName());

    List<ChangeItem> changes = bean1Ref.renderAndCleanChanges();
    System.out.println(changes);
  }

}
