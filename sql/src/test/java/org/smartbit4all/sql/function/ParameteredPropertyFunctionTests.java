package org.smartbit4all.sql.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.utility.crud.Crud;

public class ParameteredPropertyFunctionTests extends FunctionTestBase {

  @Test
  public void testWithOnlySelfParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function("upper", "{0}"))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁLMOS", almos);
  }
  
  @Test
  public void testWithOneConstParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function("substring", "{0}, 2"))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("lmos", almos);
  }
  
  @Test
  public void testWithTwoConstParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function("substring", "{0}, 2, 3"))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("lmo", almos);
  }
  
  @Test
  public void testWithTwoPropParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function("concat", "{0}, {1}", personDef.name()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁlmosÁlmos", almos);
  }
  
  @Test
  public void testWithTwoPropAndInnerFunctionParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function("concat", "{0}, TO_CHAR('' - ''), {1}", personDef.name()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - Álmos", almos);
  }
  
  @Test
  public void testWithTwoPropAndEnclosedFunctionParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function("concat", "{0}, TO_CHAR('' - ''), {1}", personDef.id().function("to_char")))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - 1", almos);
  }
  
  @Test
  public void testWithRefferedProperies() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
          .select(addressDef.person().name().function("concat", "{0}, TO_CHAR('' - ''), {1}", addressDef.person().id().function("to_char")))
          .firstRowValue(addressDef.person().name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - 1", almos);
  }
  
  @Test
  public void testWithRefferedProperiesWithWhere() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
          .select(addressDef.person().name().function("concat", "{0}, TO_CHAR('' - ''), {1}", addressDef.person().id().function("to_char")))
          .where(addressDef.person().name().function("concat", "{0}, TO_CHAR('' - ''), {1}", addressDef.person().id().function("to_char")).eq("Álmos - 1"))
          .firstRowValue(addressDef.person().name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - 1", almos);
  }
  
}
