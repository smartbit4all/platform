package org.smartbit4all.api.constraint;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.object.ApiBeanDescriptor;
import org.smartbit4all.api.object.ApiObjectRef;
import org.smartbit4all.api.object.ApiObjectsTest;
import org.smartbit4all.api.object.MasterBean;
import org.smartbit4all.api.object.MasterDetailBean;
import org.smartbit4all.api.object.ReferredBean;
import org.smartbit4all.api.object.ReferredDetailBean;
import org.smartbit4all.core.utility.StringConstant;

class HierarchicalConstraintHelperTest {

  private static Map<Class<?>, ApiBeanDescriptor> descriptors;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    descriptors = ApiObjectsTest.constructDomain();
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void testHierarchy() {
    HierarchicalConstraintHelper<Boolean> helper = new HierarchicalConstraintHelper<>(true);
    helper.set("referred/refproperty/property", ConstraintEntryScope.SUBTREE, true);
    String br = StringConstant.NEW_LINE;

    {
      String expected = "(SUBTREE; true)" + br +
          "\treferred (ITEM; null)" + br +
          "\t\trefproperty (ITEM; null)" + br +
          "\t\t\tproperty (ITEM->SUBTREE; null->true)";
      String actual = helper.toString();
      System.out.println(actual);
      Assertions.assertEquals(expected, actual);
    }

    helper.set("referred", ConstraintEntryScope.ITEM, false);
    {
      String expected = "(SUBTREE; true)" + br +
          "\treferred (ITEM; null->false)" + br +
          "\t\trefproperty (ITEM; null)" + br +
          "\t\t\tproperty (ITEM->SUBTREE; null->true)";
      String actual = helper.toString();
      System.out.println(actual);
      Assertions.assertEquals(expected, actual);
    }

    helper.set("", ConstraintEntryScope.SUBTREE, false);
    {
      String expected = "(SUBTREE; true->false)" + br +
          "\treferred (ITEM; null->false)" + br +
          "\t\trefproperty (ITEM; null)" + br +
          "\t\t\tproperty (ITEM->SUBTREE; null->true)";
      String actual = helper.toString();
      System.out.println(actual);
      Assertions.assertEquals(expected, actual);
    }

    helper.set("property", ConstraintEntryScope.ITEM, true);
    {
      String expected = "(SUBTREE; true->false)" + br +
          "\tproperty (ITEM; null->true)" + br +
          "\treferred (ITEM; null->false)" + br +
          "\t\trefproperty (ITEM; null)" + br +
          "\t\t\tproperty (ITEM->SUBTREE; null->true)";
      String actual = helper.toString();
      System.out.println(actual);
      Assertions.assertEquals(expected, actual);
    }

    {
      ConstraintEntry<Boolean> related = helper.findRelated("referred/refproperty/property");
      System.out.println(related);
      Assertions.assertEquals("property (ITEM->SUBTREE; null->true)", related.toString());
    }

    {
      ConstraintEntry<Boolean> related = helper.findRelated("notExisting");
      System.out.println(related);
      Assertions.assertEquals("(SUBTREE; true->false)", related.toString());
    }

    {
      ConstraintEntry<Boolean> related =
          helper.findRelated("referred/refproperty/property/notExisting");
      System.out.println(related);
      Assertions.assertEquals("property (ITEM->SUBTREE; null->true)", related.toString());
    }
  }

  @Test
  void testChanges() {
    MasterBean bean = constructBean();
    ApiObjectRef beanRef = new ApiObjectRef(null, bean, descriptors);
    HierarchicalConstraintHelper<Boolean> editables = new HierarchicalConstraintHelper<>(true);
    editables.set("Referred/Name", ConstraintEntryScope.ITEM, false);
    System.out.println(editables);
    List<ConstraintChange<Boolean>> changes = editables.renderAndCleanChanges(beanRef);
    System.out.println(changes);
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

}
