package org.smartbit4all.api.constraint;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.smartbit4all.core.utility.StringConstant;

class HierarchicalConstraintHelperTest {

  @BeforeAll
  static void setUpBeforeClass() throws Exception {}

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void test() {
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

}
