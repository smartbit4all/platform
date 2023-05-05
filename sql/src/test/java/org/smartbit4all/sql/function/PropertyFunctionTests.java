package org.smartbit4all.sql.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.utility.crud.Crud;


public class PropertyFunctionTests extends FunctionTestBase {

  @Test
  public void selectWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().upper())
            .order(personDef.id().asc())
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereEQWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().upper())
            .where(personDef.name().eq("Almos"))
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereEQWithUpperInWhere() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().upper())
            .where(personDef.name().upper().eq("Almos"))
            .firstRowValue(personDef.name().upper());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereEQWithUpperInWhere2() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().upper())
            .where(personDef.name().upper().eq("ALMOS"))
            .firstRowValue(personDef.name().upper());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereLIKEWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().upper())
            .where(personDef.name().like("%lmo%"))
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void selectFK_WithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
            .select(addressDef.person().name().upper())
            .firstRowValue(addressDef.person().name().upper());

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }

  @Test
  public void whereFK_EQWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
            .select(addressDef.city().upper(), addressDef.person().name().upper())
            .where(addressDef.person().name().upper().eq("ond"))
            .firstRowValue(addressDef.person().name().upper());

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }

  @Test
  public void whereFK_LIKEWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
            .select(addressDef.city().upper(), addressDef.person().name().upper())
            .where(addressDef.person().name().upper().like("on%"))
            .firstRowValue(addressDef.person().name().upper());

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }

  @Test
  public void selectWithFunction() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().function(PropertyFunction.UPPER))
            .order(personDef.id().asc())
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void selectWithFunctionShortcut() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().function("UPPER"))
            .order(personDef.id().asc())
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereEQWithFunction() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().function(PropertyFunction.UPPER))
            .where(personDef.name().eq("Almos"))
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereEQWithFunctionInWhere() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().function(PropertyFunction.UPPER))
            .where(personDef.name().function(PropertyFunction.UPPER)
                .eq("Almos"))
            .firstRowValue(
                personDef.name().function(PropertyFunction.UPPER));

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereEQWithFunctionInWhere2() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().function(PropertyFunction.UPPER))
            .where(personDef.name().function(PropertyFunction.UPPER)
                .eq("almos"))
            .firstRowValue(
                personDef.name().function(PropertyFunction.UPPER));

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void whereLIKEWithFunction() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
            .select(personDef.name().function(PropertyFunction.UPPER))
            .where(personDef.name().like("%lmo%"))
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ALMOS", almos);
  }

  @Test
  public void selectFK_WithFunction() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
            .select(addressDef.person().name()
                .function(PropertyFunction.UPPER))
            .firstRowValue(addressDef.person().name()
                .function(PropertyFunction.UPPER));

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }

  @Test
  public void whereFK_EQWithFunction() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
            .select(addressDef.city().function(PropertyFunction.UPPER),
                addressDef.person().name()
                    .function(PropertyFunction.UPPER))
            .where(addressDef.person().name()
                .function(PropertyFunction.UPPER).eq("ond"))
            .firstRowValue(addressDef.person().name()
                .function(PropertyFunction.UPPER));

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }

  @Test
  public void whereFK_LIKEWithFunction() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
            .select(addressDef.city().function(PropertyFunction.UPPER),
                addressDef.person().name()
                    .function(PropertyFunction.UPPER))
            .where(addressDef.person().name()
                .function(PropertyFunction.UPPER).like("on%"))
            .firstRowValue(addressDef.person().name()
                .function(PropertyFunction.UPPER));

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }

}
