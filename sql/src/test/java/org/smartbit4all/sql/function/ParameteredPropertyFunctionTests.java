package org.smartbit4all.sql.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.meta.PropertyFunction;
import org.smartbit4all.domain.utility.crud.Crud;

public class ParameteredPropertyFunctionTests extends FunctionTestBase {

  @Test
  public void testWithOnlySelfParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function(
              PropertyFunction.build("upper").selfPropertyParam().build()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁLMOS", almos);
  }
  
  @Test
  public void testWithOneConstParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function(
              PropertyFunction.build("substring").selfPropertyParam().param("2").build()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("lmos", almos);
  }
  
  @Test
  public void testWithTwoConstParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function(
              PropertyFunction.build("substring").selfPropertyParam().param("2").param("3").build()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("lmo", almos);
  }
  
  @Test
  public void testWithTwoPropParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function(
              PropertyFunction.build("concat")
                .selfPropertyParam()
                .propertyParam(personDef.name())
                .build()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁlmosÁlmos", almos);
  }
  
  @Test
  public void testWithTwoPropAndInnerFunctionParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function(
            PropertyFunction.build("concat")
            .selfPropertyParam()
            .addInnerFunction("TO_CHAR")
              .stringParam(" - ")
            .closeInnerFunction()
            .propertyParam(personDef.name())
            .build()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - Álmos", almos);
  }
  
  @Test
  public void testWithTwoPropAndEnclosedFunctionParameter() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().function(
            PropertyFunction.build("concat")
            .selfPropertyParam()
            .addInnerFunction("TO_CHAR")
              .stringParam(" - ")
            .closeInnerFunction()
            .propertyParam(
                personDef.id().function(PropertyFunction.withSelfPropertyArgument("to_char")))
            .build()))
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - 1", almos);
  }
  
  @Test
  public void testWithRefferedProperies() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
          .select(addressDef.person().name().function(
            PropertyFunction.build("concat")
              .selfPropertyParam()
              .addInnerFunction("TO_CHAR")
                .stringParam(" - ")
              .closeInnerFunction()
              .propertyParam(
                  addressDef.person().id().function(
                      PropertyFunction.withSelfPropertyArgument("to_char")))
              .build()))
          .firstRowValue(addressDef.person().name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - 1", almos);
  }
  
  @Test
  public void testWithRefferedProperiesWithWhere() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
          .select(addressDef.person().name().function(
          PropertyFunction.build("concat")
          .selfPropertyParam()
          .addInnerFunction("TO_CHAR")
            .stringParam(" - ")
          .closeInnerFunction()
          .propertyParam(
              addressDef.person().id().function(
                  PropertyFunction.withSelfPropertyArgument("to_char")))
          .build()))
          .where(addressDef.person().name().function(
              PropertyFunction.build("concat")
              .selfPropertyParam()
              .addInnerFunction("TO_CHAR")
                .stringParam(" - ")
              .closeInnerFunction()
              .propertyParam(
                  addressDef.person().id().function(
                      PropertyFunction.withSelfPropertyArgument("to_char")))
              .build()
              ).eq("Álmos - 1"))
          .firstRowValue(addressDef.person().name());

    String almos = firstRowValue.get();
    assertEquals("Álmos - 1", almos);
  }
  
}