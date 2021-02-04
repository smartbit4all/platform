package org.smartbit4all.api.object;

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

    String br = StringConstant.NEW_LINE;
    String changesText1 = objectChange1.get().toString();
    System.out.println("Changes if whole bean set:" + br + changesText1);

    String expected = "NEW" + br +
        "Counter: (null->1)" + br +
        "Name: (null->name)" + br +
        "StringList: (null->[first, second])" + br +
        "Referred: {" + br +
        "NEW" + br +
        "Name: (null->refname)" + br +
        "Details.collection:" + br +
        "Details.item - {" + br +
        "NEW" + br +
        "Name: (null->refDetailName1)" + br +
        "}" + br +
        "Details.item - {" + br +
        "NEW" + br +
        "Name: (null->refDetailName2)" + br +
        "}" + br +
        "}" + br +
        "Details.collection:" + br +
        "Details.item - {" + br +
        "NEW" + br +
        "DetailName: (null->detailName1)" + br +
        "}" + br +
        "Details.item - {" + br +
        "NEW" + br +
        "DetailName: (null->detailName2)" + br +
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

    ApiObjectRef bean1Ref =
        new ApiObjectRef(null, bean1, descriptors);

    MasterBean beanWrapper1 = bean1Ref.getWrapper(MasterBean.class);

    beanWrapper1.setCounter(1);
    beanWrapper1.setName("name by wrapper");

    beanWrapper1.setReferred(new ReferredBean());

    beanWrapper1.getReferred().setName("referred name by wrapper");

    List<ReferredDetailBean> details = beanWrapper1.getReferred().getDetails();
    {
      ReferredDetailBean referredDetailBean = new ReferredDetailBean();
      details.add(referredDetailBean);
    }
    {
      ReferredDetailBean referredDetailBean = new ReferredDetailBean();
      details.add(referredDetailBean);
    }
    {
      ReferredDetailBean referredDetailBean = new ReferredDetailBean();
      details.add(referredDetailBean);
    }

    int number = 1;
    for (ReferredDetailBean refDetailBean : details) {
      refDetailBean.setName("referredDetailBean - name " + number++);
    }

    Optional<ObjectChange> objectChange1 = bean1Ref.renderAndCleanChanges();

    Assertions.assertTrue(objectChange1.isPresent());

    String br = StringConstant.NEW_LINE;
    String changesText1 = objectChange1.get().toString();
    System.out.println("testModificationWithWrapper - Changes1:" + br + changesText1);

    String expected = "NEW" + br +
        "Counter: (null->1)" + br +
        "Name: (null->name by wrapper)" + br +
        "Referred: {" + br +
        "NEW" + br +
        "Name: (null->referred name by wrapper)" + br +
        "Details.collection:" + br +
        "Details.item - {" + br +
        "NEW" + br +
        "Name: (null->referredDetailBean - name 1)" + br +
        "}" + br +
        "Details.item - {" + br +
        "NEW" + br +
        "Name: (null->referredDetailBean - name 2)" + br +
        "}" + br +
        "Details.item - {" + br +
        "NEW" + br +
        "Name: (null->referredDetailBean - name 3)" + br +
        "}" + br +
        "}";

    Assertions.assertEquals(expected, changesText1);

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
        refBean.getDetails().add(detBean);
      }
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName2");
        refBean.getDetails().add(detBean);
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
        refBean.getDetails().add(detBean);
      }
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName2");
        refBean.getDetails().add(detBean);
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
