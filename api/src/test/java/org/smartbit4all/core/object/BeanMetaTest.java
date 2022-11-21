package org.smartbit4all.core.object;

import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanMetaTest {


  @Test
  void testDeepCopy() throws ExecutionException {
    MasterBean masterBean = ApiObjectsTest.constructBean();

    BeanMeta meta = BeanMetaUtil.meta(MasterBean.class);

    MasterBean deepCopy = (MasterBean) meta.deepCopy(masterBean);

    assertEquals(true, meta.deepEquals(masterBean, deepCopy));

  }

}
