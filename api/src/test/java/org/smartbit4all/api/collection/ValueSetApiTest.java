package org.smartbit4all.api.collection;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum;
import org.smartbit4all.api.setting.LocaleSettingApi;
import org.smartbit4all.api.setting.Locales;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSetData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.api.value.bean.ValueSetExpression;
import org.smartbit4all.api.value.bean.ValueSetOperand;
import org.smartbit4all.api.value.bean.ValueSetOperation;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.google.common.collect.Streams;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@SpringBootTest(classes = {
    CollectionTestConfig.class
})
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ValueSetApiTest {

  private static final String ACTIVE_CATEGORIES = "ACTIVE-CATEGORIES";

  private static final String INACTIVE_CATEGORIES = "INACTIVE-CATEGORIES";

  private static final String ALL_CATEGORIES = "ALL-CATEGORIES";

  static final String SCHEMA = "valueSet";

  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ValueSetApi valueSetApi;

  @Autowired
  private LocaleSettingApi localeSettingApi;

  @Test
  void testInlineValuesSets() {
    valueSetApi.save(new ValueSetDefinitionData().qualifiedName("INLINE-BASE")
        .kind(ValueSetDefinitionKind.INLINE).typeClass(Value.class.getName())
        .addInlineValuesItem(new Value().code("A").displayValue("apple"))
        .addInlineValuesItem(new Value().code("B").displayValue("Banana"))
        .addInlineValuesItem(new Value().code("C").displayValue("Carot")));
    valueSetApi.save(new ValueSetDefinitionData().qualifiedName("INLINE-NOTINCLUDED")
        .kind(ValueSetDefinitionKind.INLINE).typeClass(Value.class.getName())
        .addInlineValuesItem(new Value().code("A").displayValue("apple")));

    valueSetApi.save(new ValueSetDefinitionData().qualifiedName("INLINE-DIF")
        .kind(ValueSetDefinitionKind.COMPOSITE).typeClass(Value.class.getName())
        .expression(new ValueSetExpression().operation(ValueSetOperation.DIF)
            .addOperandsItem(new ValueSetOperand().name("INLINE-BASE"))
            .addOperandsItem(new ValueSetOperand().name("INLINE-NOTINCLUDED"))));

    ValueSetData valueSetDif = valueSetApi.valuesOf("INLINE-DIF");

    Assertions.assertThat(valueSetApi.getValues(String.class, valueSetDif, Value.CODE))
        .containsExactlyInAnyOrder("B", "C");

    ValueSetData valueSetUnion =
        valueSetApi.valuesOf(new ValueSetDefinitionData().qualifiedName("INLINE-UNION")
            .kind(ValueSetDefinitionKind.COMPOSITE).typeClass(Value.class.getName())
            .expression(new ValueSetExpression().operation(ValueSetOperation.UNION)
                .addOperandsItem(new ValueSetOperand().name("INLINE-BASE"))
                .addOperandsItem(new ValueSetOperand().data(new ValueSetDefinitionData()
                    .kind(ValueSetDefinitionKind.INLINE).qualifiedName("RUNTIME_DEFINED")
                    .typeClass(Value.class.getName())
                    .addInlineValuesItem(new Value().code("D").displayValue("Dewberry"))
                    .addInlineValuesItem(new Value().code("C").displayValue("Carot"))))));

    Assertions.assertThat(valueSetApi.getValues(String.class, valueSetUnion, Value.CODE))
        .containsExactlyInAnyOrder("A", "B", "C", "D");

  }

  @Test
  void testObjectBasedSet() {
    valueSetApi.save(new ValueSetDefinitionData().qualifiedName(ALL_CATEGORIES)
        .kind(ValueSetDefinitionKind.ALLOF).typeClass(SampleCategory.class.getName())
        .storageSchema(SCHEMA));

    // Create SamplCategory objects to fill the set.
    OffsetDateTime now = OffsetDateTime.now();
    List<String> categoryNames = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      categoryNames
          .add("category " + i);
    }
    StoredList activeCategories = collectionApi.list(SCHEMA, ACTIVE_CATEGORIES);
    List<URI> categories = categoryNames.stream().map(n -> {
      SampleCategory result = new SampleCategory().name(n)
          .color(EnumRandomizer.randomEnum(ColorEnum.class)).createdAt(now);
      URI uri = objectApi.saveAsNew(SCHEMA, result);
      if (result.getColor() == ColorEnum.RED) {
        activeCategories.add(uri);
      }
      return uri;
    }).collect(toList());

    valueSetApi.save(new ValueSetDefinitionData().qualifiedName(ACTIVE_CATEGORIES)
        .kind(ValueSetDefinitionKind.LIST).typeClass(SampleCategory.class.getName())
        .storageSchema(SCHEMA).containerName(ACTIVE_CATEGORIES));
    valueSetApi.save(new ValueSetDefinitionData().qualifiedName(INACTIVE_CATEGORIES)
        .kind(ValueSetDefinitionKind.COMPOSITE).typeClass(SampleCategory.class.getName())
        .expression(new ValueSetExpression().operation(ValueSetOperation.DIF)
            .addOperandsItem(new ValueSetOperand().name(ALL_CATEGORIES))
            .addOperandsItem(new ValueSetOperand().name(ACTIVE_CATEGORIES))));

    ValueSetData allValues = valueSetApi.valuesOf(ALL_CATEGORIES);
    ValueSetData activeValues = valueSetApi.valuesOf(ACTIVE_CATEGORIES);
    ValueSetData inactiveValues = valueSetApi.valuesOf(INACTIVE_CATEGORIES);

    List<String> inactiveNames =
        valueSetApi.getValues(String.class, inactiveValues, SampleCategory.NAME);
    List<String> activeNames =
        valueSetApi.getValues(String.class, activeValues, SampleCategory.NAME);

    Assertions
        .assertThat(Streams.concat(inactiveNames.stream(), activeNames.stream()).collect(toList()))
        .containsExactlyInAnyOrderElementsOf(categoryNames);

    // objectApi.saveAsNew(null, objectApi)
  }

  @Test
  void testEnums() {
    ValueSetData colorValues =
        valueSetApi.valuesOf(org.smartbit4all.api.sample.bean.SampleCategory.ColorEnum.class);

    Assertions.assertThat(valueSetApi.getValues(String.class, colorValues, Value.CODE))
        .containsExactlyInAnyOrder("RED", "BLACK", "GREEN", "WHITE");

    localeSettingApi.setDefaultLocale(Locales.HUNGARIAN);

    Assertions.assertThat(valueSetApi.getValues(String.class, colorValues, Value.DISPLAY_VALUE))
        .containsExactlyInAnyOrder("Piros", "Fekete", "Zöld", "Fehér");

  }

  void testSearches() {
    ExecutorService exec =
        new ThreadPoolExecutor(28, 56, 500, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>());
    List<Future<String>> result = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      result.add(exec.submit(() -> testSearchesRun()));
    }
    List<String> list = result.stream().map(f -> {
      try {
        return f.get();
      } catch (InterruptedException | ExecutionException e) {
        return "Failed";
      }
    }).collect(toList());
    Assertions.assertThat(list).allMatch(s -> "OK".equals(s));
  }

  String testSearchesRun() {
    int bound = 1000000;
    int accessNumber = 1000;
    String[] values = new String[bound];
    for (int i = 0; i < values.length; i++) {
      values[i] = "value " + i;
    }

    Random rnd = new Random();
    {
      // Access by reference
      long start = System.currentTimeMillis();
      for (int i = 0; i < accessNumber; i++) {
        int nextInt = rnd.nextInt(bound);
        String result = values[nextInt];
      }
      long end = System.currentTimeMillis();
      System.out
          .println("Access by reference " + accessNumber + " from " + bound + ":" + (end - start));
    }

    {
      //
      long startPrep = System.currentTimeMillis();
      Set<String> valueSet = Stream.of(values).collect(toSet());
      long endPrep = System.currentTimeMillis();
      System.out
          .println("Construct set from list size " + bound + ":" + (endPrep - startPrep));

      // Access by Map
      long start = System.currentTimeMillis();
      for (int i = 0; i < accessNumber; i++) {
        int nextInt = rnd.nextInt(bound);
        String nextValue = "value " + nextInt;
        valueSet.contains(nextValue);
      }
      long end = System.currentTimeMillis();
      System.out
          .println("Access by reference " + accessNumber + " from " + bound + ":" + (end - start));
    }

    {
      List<String> valueList = Arrays.asList(values);
      // Access by sequential search
      long start = System.currentTimeMillis();
      for (int i = 0; i < accessNumber; i++) {
        int nextInt = rnd.nextInt(bound);
        String nextValue = "value " + nextInt;
        String result = valueList.stream().filter(s -> s.equals(nextValue)).findFirst().get();
      }
      long end = System.currentTimeMillis();
      System.out
          .println("Access by sequential search " + accessNumber + " from " + bound + ":"
              + (end - start));
    }
    return "OK";
  }

}
