package org.smartbit4all.sql.function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.smartbit4all.domain.utility.crud.Crud;
import org.smartbit4all.sql.testmodel.AddressDef;
import org.smartbit4all.sql.testmodel.PersonDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(classes = {
    PropertyFunctionTestConfig.class,
})
@Sql({"/script/exists_schema.sql", "/script/exists_data_01.sql"})
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class PropertyFunctionTests {

  @Autowired
  private AddressDef addressDef;
  
  @Autowired
  private PersonDef personDef;
  
  @Test
  public void selectWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().upper())
          .order(personDef.id().asc())
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁLMOS", almos);
  }
  
  @Test
  public void whereEQWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().upper())
          .where(personDef.name().eq("Álmos"))
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁLMOS", almos);
  }
  
  @Test
  public void whereEQWithUpperInWhere() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().upper())
          .where(personDef.name().upper().eq("Álmos"))
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁLMOS", almos);
  }
  
  @Test
  public void whereEQWithUpperInWhere2() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().upper())
          .where(personDef.name().upper().eq("ÁLMOS"))
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁLMOS", almos);
  }
  
  @Test
  public void whereLIKEWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(personDef)
          .select(personDef.name().upper())
          .where(personDef.name().like("%lmo%"))
          .firstRowValue(personDef.name());

    String almos = firstRowValue.get();
    assertEquals("ÁLMOS", almos);
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
        .firstRowValue(addressDef.person().name());

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }
  
  @Test
  public void whereFK_LIKEWithUpper() throws Exception {
    Optional<String> firstRowValue =
        Crud.read(addressDef)
        .select(addressDef.city().upper(), addressDef.person().name().upper())
        .where(addressDef.person().name().upper().like("on%"))
        .firstRowValue(addressDef.person().name());

    String firstValue = firstRowValue.get();
    assertEquals(firstValue.toUpperCase(), firstValue);
  }
  
}
