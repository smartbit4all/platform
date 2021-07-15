package org.smartbit4all.sql.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.utility.crud.Crud;

public class BasicFunctionTests extends FunctionTestBase {

  @Test
  public void testAvg() throws Exception {
    Long avg =
        Crud.read(personDef)
            .select(personDef.id().avg())
            .where(personDef.id().le(7l))
            .listData()
            .rows().get(0)
            .get(personDef.id().avg());

    assertEquals(4l, avg);
  }

  @Test
  public void testSum() throws Exception {
    Long sum =
        Crud.read(personDef)
            .select(personDef.id().sum())
            .where(personDef.id().le(7l))
            .listData()
            .rows().get(0)
            .get(personDef.id().sum());

    assertEquals(28l, sum);
  }

  @Test
  public void testMin() throws Exception {
    Long min  =
        Crud.read(personDef)
          .select(personDef.id().min())
          .where(personDef.id().le(7l))
          .listData()
          .rows().get(0)
          .get(personDef.id().min());

    assertEquals(1l, min);
  }

  @Test
  public void testMax() throws Exception {
    Long max =
        Crud.read(personDef)
            .select(personDef.id().max())
            .where(personDef.id().le(7l))
            .listData()
            .rows().get(0)
            .get(personDef.id().max());

    assertEquals(7l, max);
  }

  @Test
  public void testCount() throws Exception {
    Long count =
        Crud.read(personDef)
            .select(personDef.count())
            .where(personDef.id().le(7l))
            .listData()
            .rows().get(0)
            .get(personDef.count());

    assertEquals(7l, count);
  }

}
