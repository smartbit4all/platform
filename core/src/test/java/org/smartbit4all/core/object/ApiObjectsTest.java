package org.smartbit4all.core.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.core.utility.StringConstant;

public class ApiObjectsTest {

  private static Map<Class<?>, ApiBeanDescriptor> descriptors;

  String br = StringConstant.NEW_LINE;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    descriptors = constructDomain();
  }

  public static Map<Class<?>, ApiBeanDescriptor> constructDomain() {
    Set<Class<?>> domainBeans;
    Map<Class<?>, ApiBeanDescriptor> result;
    domainBeans = new HashSet<>();
    domainBeans.add(MasterBean.class);
    domainBeans.add(MasterDetailBean.class);
    domainBeans.add(ReferredBean.class);
    domainBeans.add(ReferredDetailBean.class);
    domainBeans.add(DetailBeanWithId.class);
    result = ApiBeanDescriptor.of(domainBeans);
    // {
    // ApiBeanDescriptor descriptor = result.get(MasterBean.class);
    // descriptor.getDetailDescriptors().put("Details", result.get(MasterDetailBean.class));
    // }
    // {
    // ApiBeanDescriptor descriptor = result.get(ReferredBean.class);
    // descriptor.getDetailDescriptors().put("Details", result.get(ReferredDetailBean.class));
    // }
    return result;
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
    expectedDescBuilder.append("org.smartbit4all.core.object.MasterBean").append(br)
        .append("\tcounter(long) get: getCounter set: setCounter, counter addItem: -").append(br)
        .append(
            "\tdetails(java.util.List) get: getDetails set: setDetails, - addItem: addDetailsItem")
        .append(br)
        .append("\tenabled(java.lang.Boolean) get: getEnabled set: setEnabled, - addItem: -")
        .append(br)
        .append("\tname(java.lang.String) get: getName set: setName, - addItem: -").append(br)
        .append("\treadOnlyLong(java.lang.Long) get: getReadOnlyLong set: -, - addItem: -")
        .append(br)
        .append(
            "\treferred(org.smartbit4all.core.object.ReferredBean) get: getReferred set: setReferred, - addItem: -")
        .append(br)
        .append("\tstringList(java.util.List) get: getStringList set: setStringList, - addItem: -")
        .append(br)
        .append("\tvalid(boolean) get: isValid set: setValid, - addItem: -");

    Assertions.assertEquals(expectedDescBuilder.toString(), description);
  }

  @Test
  void testSampleBeanModification() {
    MasterBean bean1 = new MasterBean();

    ApiObjectRef bean1Ref =
        new ApiObjectRef(null, bean1, descriptors);

    bean1Ref.setValue("name", "myName");
    bean1Ref.setValue("Counter", 1);
    bean1Ref.setValue("stringList", Arrays.asList("first", "second"));



    Assertions.assertEquals("myName", bean1.getName());
    Assertions.assertEquals(Long.valueOf(1), bean1.getCounter());

    // bean1Ref.setValue(bean1::setName, "myName-2");
    //
    //
    // Assertions.assertEquals("myName-2", bean1.getName());

    bean1Ref.setValue("NAME", "myName-3");
    Assertions.assertEquals("myName-3", bean1.getName());

    ApiObjectCollection details = (ApiObjectCollection) bean1Ref.getValue("details");
    ApiObjectRef masterDetail1 = details.addObject(new MasterDetailBean());
    masterDetail1.setValue("detailName", "detail.1.detailName");


    Optional<ObjectChange> objectChange = bean1Ref.renderAndCleanChanges();
    System.out.println(objectChange.get());
  }

  /**
   * The Bean is already filled with data we check the events.
   */
  @Test
  void testSetFilledBean() {
    // Setup the bean before the managing phase with ApiObjectRef. All the preset properties will
    // appear in the events.
    MasterBean bean1 = constructBean();

    ApiObjectRef bean1Ref =
        new ApiObjectRef(null, bean1, descriptors);

    Optional<ObjectChange> objectChange1 = bean1Ref.renderAndCleanChanges();

    Assertions.assertTrue(objectChange1.isPresent());

    String changesText1 = objectChange1.get().toString();
    System.out.println("Changes if whole bean set:" + br + changesText1);

    String expected = "NEW" + br +
        "counter: (null->1)" + br +
        "name: (null->name)" + br +
        "stringList: (null->[first, second])" + br +
        "valid: (null->false)" + br +
        "referred: {" + br +
        "NEW" + br +
        "name: (null->refname)" + br +
        "details.collection:" + br +
        "details.item - {" + br +
        "NEW" + br +
        "name: (null->refDetailName1)" + br +
        "}" + br +
        "details.item - {" + br +
        "NEW" + br +
        "name: (null->refDetailName2)" + br +
        "}" + br +
        "}" + br +
        "details.collection:" + br +
        "details.item - {" + br +
        "NEW" + br +
        "detailName: (null->detailName1)" + br +
        "}" + br +
        "details.item - {" + br +
        "NEW" + br +
        "detailName: (null->detailName2)" + br +
        "}";
    Assertions.assertEquals(expected, changesText1);

    // Now we produce the same bean by setting all the properties one by one.

    MasterBean bean2 = new MasterBean();

    ApiObjectRef bean2Ref =
        new ApiObjectRef(null, bean2, descriptors);
    constructBeanByRef(bean2Ref);

    Optional<ObjectChange> objectChange2 = bean2Ref.renderAndCleanChanges();

    Assertions.assertTrue(objectChange2.isPresent());

    String changesText2 = objectChange2.get().toString();
    System.out.println("Changes if bean constructed:" + br + changesText2);

    Assertions.assertEquals(changesText1, changesText2);

  }

  /**
   * The Bean is already filled with data we check the events.
   */
  @Test
  void testModificationWithWrapper() {
    // Setup the bean before the managing phase with ApiObjectRef. All the preset properties will
    // appear in the events.
    MasterBean bean1 = new MasterBean();

    ApiObjectRef ref =
        new ApiObjectRef(null, bean1, descriptors);

    MasterBean beanWrapper1 = ref.getWrapper(MasterBean.class);

    beanWrapper1.counter(1).setName("name by wrapper");
    beanWrapper1.setReferred(new ReferredBean());
    beanWrapper1.getReferred().setName("referred name by wrapper");
    beanWrapper1.getReferred()
        .addDetailsItem(new ReferredDetailBean())
        .addDetailsItem(new ReferredDetailBean());
    List<ReferredDetailBean> details = beanWrapper1.getReferred().getDetails();
    {
      ReferredDetailBean referredDetailBean = new ReferredDetailBean();
      details.add(referredDetailBean);
    }

    int number = 1;
    for (ReferredDetailBean refDetailBean : details) {
      refDetailBean.setName("referredDetailBean - name " + number++);
    }

    beanWrapper1.setValid(true);
    beanWrapper1.setEnabled(Boolean.FALSE);

    // check underlying object changes
    assertEquals(1, bean1.getCounter());
    assertEquals("name by wrapper", bean1.getName());
    assertEquals("referred name by wrapper", bean1.getReferred().getName());

    assertEquals(3, bean1.getReferred().getDetails().size());
    assertEquals("referredDetailBean - name 1", bean1.getReferred().getDetails().get(0).getName());

    Optional<ObjectChange> objectChange1 = ref.renderAndCleanChanges();
    Assertions.assertTrue(objectChange1.isPresent());
    String changesText1 = objectChange1.get().toString();
    System.out.println("testModificationWithWrapper - Changes1 >>>>>>>" + br + changesText1
        + br + "testModificationWithWrapper - Changes1 <<<<<<<");

    String expected1 = "NEW" + br +
        "counter: (null->1)" + br +
        "enabled: (null->false)" + br +
        "name: (null->name by wrapper)" + br +
        "valid: (null->true)" + br +
        "referred: {" + br +
        "NEW" + br +
        "name: (null->referred name by wrapper)" + br +
        "details.collection:" + br +
        "details.item - {" + br +
        "NEW" + br +
        "name: (null->referredDetailBean - name 1)" + br +
        "}" + br +
        "details.item - {" + br +
        "NEW" + br +
        "name: (null->referredDetailBean - name 2)" + br +
        "}" + br +
        "details.item - {" + br +
        "NEW" + br +
        "name: (null->referredDetailBean - name 3)" + br +
        "}" + br +
        "}";

    Assertions.assertEquals(expected1, changesText1);

    // setObject
    MasterBean bean2 = new MasterBean();
    bean2.setName("bean2 name");
    bean2.setCounter(2);
    bean2.setValid(true);
    bean2.setEnabled(Boolean.FALSE);
    ref.setObject(bean2);

    Assertions.assertEquals(bean2, ref.getObject());

    ReferredBean bean2referredBean = new ReferredBean();
    beanWrapper1.setReferred(bean2referredBean);
    beanWrapper1.getReferred().setName("bean2 referred by wrapper");
    beanWrapper1.getReferred()
        .addDetailsItem(new ReferredDetailBean().name("new item 1"))
        .addDetailsItem(new ReferredDetailBean().name("new item 2"));

    Assertions.assertEquals(bean2referredBean, bean2.getReferred());

    Optional<ObjectChange> objectChange2 = ref.renderAndCleanChanges();
    Assertions.assertTrue(objectChange2.isPresent());
    String changesText2 = objectChange2.get().toString();
    System.out.println("testModificationWithWrapper - Changes2 >>>>>>>" + br + changesText2
        + br + "testModificationWithWrapper - Changes2 <<<<<<<");

    // TODO assert changes
    String expected2 = "MODIFIED" + br
        + "counter: (1->2)" + br
        + "name: (name by wrapper->bean2 name)" + br
        + "referred: {" + br
        + "MODIFIED" + br
        + "name: (referred name by wrapper->bean2 referred by wrapper)" + br
        + "details.collection:" + br
        + "details.item - {" + br
        + "DELETED" + br
        + "}" + br
        + "details.item - {" + br
        + "DELETED" + br
        + "}" + br
        + "details.item - {" + br
        + "DELETED" + br
        + "}" + br
        + "details.item - {" + br
        + "NEW" + br
        + "name: (null->new item 1)" + br
        + "}" + br
        + "details.item - {" + br
        + "NEW" + br
        + "name: (null->new item 2)" + br
        + "}" + br
        + "}";
    Assertions.assertEquals(expected2, changesText2);

    // mergeObject - use original bean1 as values. bean2 stays as ref.object
    ref.mergeObject(bean1);

    Assertions.assertEquals(bean2, ref.getObject());
    Assertions.assertEquals(bean1.getReferred(), bean2.getReferred());

    Optional<ObjectChange> objectChange3 = ref.renderAndCleanChanges();
    Assertions.assertTrue(objectChange3.isPresent());
    String changesText3 = objectChange3.get().toString();
    System.out.println("testModificationWithWrapper - Changes3 >>>>>>>" + br + changesText3
        + br + "testModificationWithWrapper - Changes3 <<<<<<<");

    String expected3 = "MODIFIED" + br
        + "counter: (2->1)" + br
        + "name: (bean2 name->name by wrapper)" + br
        + "referred: {" + br
        + "MODIFIED" + br
        + "name: (bean2 referred by wrapper->referred name by wrapper)" + br
        + "details.collection:" + br
        + "details.item - {" + br
        + "MODIFIED" + br
        + "name: (new item 1->referredDetailBean - name 1)" + br
        + "}" + br
        + "details.item - {" + br
        + "MODIFIED" + br
        + "name: (new item 2->referredDetailBean - name 2)" + br
        + "}" + br
        + "details.item - {" + br
        + "NEW" + br
        + "name: (null->referredDetailBean - name 3)" + br
        + "}" + br
        + "}";
    Assertions.assertEquals(expected3, changesText3);

  }

  @Test
  void testMap() {
    MasterBean bean1 = new MasterBean();

    ApiObjectRef ref =
        new ApiObjectRef(null, bean1, descriptors);

    MasterBean beanWrapper1 = ref.getWrapper(MasterBean.class);

    beanWrapper1.counter(1).setName("name by wrapper");

    beanWrapper1.setReferred(new ReferredBean());

    beanWrapper1.getReferred().setName("referred name by wrapper");

    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";
    beanWrapper1.getReferred()
        .putDetailsByIdItem(key1, new DetailBeanWithId().id(key1).title("Key 1"))
        .putDetailsByIdItem(key2, new DetailBeanWithId().id(key2).title("Key 2"));
    Map<String, DetailBeanWithId> details = beanWrapper1.getReferred().getDetailsById();
    {
      details.put(key3, new DetailBeanWithId().id(key3).title("Key 3"));
    }

    for (DetailBeanWithId refDetailBean : details.values()) {
      refDetailBean.setTitle(refDetailBean.getTitle() + " - modified");
    }

    beanWrapper1.setValid(true);
    beanWrapper1.setEnabled(Boolean.FALSE);

    Optional<ObjectChange> objectChange1 = ref.renderAndCleanChanges();
    Assertions.assertTrue(objectChange1.isPresent());
    String changesText1 = objectChange1.get().toString();
    System.out.println("testModificationWithWrapper - Changes1 >>>>>>>" + br + changesText1
        + br + "testModificationWithWrapper - Changes1 <<<<<<<");

    String expected1 = "NEW" + br +
        "counter: (null->1)" + br +
        "enabled: (null->false)" + br +
        "name: (null->name by wrapper)" + br +
        "valid: (null->true)" + br +
        "referred: {" + br +
        "NEW" + br +
        "name: (null->referred name by wrapper)" + br +
        "detailsById.collection:" + br
        + "detailsById.item - {" + br
        + "NEW" + br
        + "id: (null->key1)" + br
        + "title: (null->Key 1 - modified)" + br
        + "}" + br
        + "detailsById.item - {" + br
        + "NEW" + br
        + "id: (null->key2)" + br
        + "title: (null->Key 2 - modified)" + br
        + "}" + br
        + "detailsById.item - {" + br
        + "NEW" + br
        + "id: (null->key3)" + br
        + "title: (null->Key 3 - modified)" + br
        + "}" + br
        + "}";

    Assertions.assertEquals(expected1, changesText1);

  }

  @Test
  void testGetValueByPathTest() {
    MasterBean masterBean = constructBean();

    ApiObjectRef masterBeanRef = new ApiObjectRef(null, masterBean, descriptors);

    assertEquals("name", masterBeanRef.getValueByPath("name"));
    assertEquals(1L, masterBeanRef.getValueByPath("counter"));
    assertEquals(Arrays.asList("first", "second"), masterBeanRef.getValueByPath("stringlist"));
    assertEquals("refname", masterBeanRef.getValueByPath("referred/name"));

    // TODO test collection after fixed
    // assertEquals(masterBean.getDetails(), masterBeanRef.getValueByPath("details"));
    // assertEquals(masterBean.getReferred().getDetails(),
    // masterBeanRef.getValueByPath("referred/details"));
  }

  private MasterBean constructBean() {
    MasterBean bean1 = new MasterBean();
    bean1.setCounter(1);
    bean1.setName("name");
    bean1.setStringList(Arrays.asList("first", "second"));
    {
      ReferredBean refBean = new ReferredBean();
      refBean.setName("refname");
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName1");
        refBean.addDetailsItem(detBean);
      }
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName2");
        refBean.addDetailsItem(detBean);
      }
      bean1.setReferred(refBean);
    }
    {
      MasterDetailBean detBean = new MasterDetailBean();
      detBean.setDetailName("detailName1");
      bean1.getDetails().add(detBean);
    }
    {
      MasterDetailBean detBean = new MasterDetailBean();
      detBean.setDetailName("detailName2");
      bean1.getDetails().add(detBean);
    }
    return bean1;
  }

  private void constructBeanByRef(ApiObjectRef objRef) {
    objRef.setValue("Counter", 1);
    objRef.setValue("Name", "name");
    objRef.setValue("StringList", Arrays.asList("first", "second"));
    {
      // Constructs the referred bean and set this as one.
      ReferredBean refBean = new ReferredBean();
      refBean.setName("refname");
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName1");
        refBean.addDetailsItem(detBean);
      }
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName2");
        refBean.addDetailsItem(detBean);
      }
      objRef.setValue("Referred", refBean);
    }
    ApiObjectCollection details = (ApiObjectCollection) objRef.getValue("Details");
    {
      // Add the bean and set the property later.
      MasterDetailBean detBean = new MasterDetailBean();
      ApiObjectRef addedObjectRef = details.addObject(detBean);
      addedObjectRef.setValue("DetailName", "detailName1");
    }
    {
      // Construct the whole bean and add.
      MasterDetailBean detBean = new MasterDetailBean();
      detBean.setDetailName("detailName2");
      details.addObject(detBean);
    }
  }
}
