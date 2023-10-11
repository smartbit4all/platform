package org.smartbit4all.sql.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.session.bean.UserActivityLog;
import org.smartbit4all.domain.meta.Property;
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
    assertEquals("ALMOS", almos);
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
                PropertyFunction.build("substring").selfPropertyParam().param("2").param("3")
                    .build()))
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
    assertEquals("AlmosAlmos", almos);
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
    assertEquals("Almos - Almos", almos);
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
                        personDef.id()
                            .function(PropertyFunction.withSelfPropertyArgument("to_char")))
                    .build()))
            .order(personDef.id().asc())
            .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("Almos - 1", almos);
  }

  @Test
  public void testWithRefferedProperties() throws Exception {
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
    assertEquals("Almos - 1", almos);
  }

  @Test
  public void testWithRefferedPropertiesWithWhere() throws Exception {
    Property<String> functionProperty = addressDef.person().name().function(
        PropertyFunction.build("concat")
            .selfPropertyParam()
            .addInnerFunction("TO_CHAR")
            .stringParam(" - ")
            .closeInnerFunction()
            .propertyParam(
                addressDef.person().id().function(
                    PropertyFunction.withSelfPropertyArgument("to_char")))
            .build());

    Optional<String> firstRowValue =
        Crud.read(addressDef)
            .select(functionProperty)
            .where(functionProperty.eq("Almos - 1"))
            .firstRowValue(functionProperty);

    String almos = firstRowValue.get();
    assertEquals("Almos - 1", almos);
  }


  @Test
  // @Disabled
  public void testComplexProperties() throws Exception {
    Optional<UserActivityLog> firstRowComplex =
        Crud.read(addressDef)
            .select(addressDef.created())
            .order(addressDef.id().asc())
            .firstRowValue(addressDef.created());

    UserActivityLog kedves = firstRowComplex.get();

    assertEquals("Kedves", kedves.getName());

    // Optional<UserActivityLog> firstRowString =
    // Crud.read(addressDef)
    // .select(addressDef.created()
    // .function(/* String.class, */
    // PropertyFunction.build("field")
    // .selfPropertyParam()
    // // .params(UserActivityLog.NAME, "otherProperty") // string[]
    // .param(UserActivityLog.NAME)
    // .build()))
    // .order(addressDef.id().asc())
    // .firstRowValue(addressDef.created());

  }
}
