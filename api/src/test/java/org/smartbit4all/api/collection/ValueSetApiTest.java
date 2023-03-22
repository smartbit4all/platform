package org.smartbit4all.api.collection;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.value.ValueSetApi;
import org.smartbit4all.api.value.bean.Value;
import org.smartbit4all.api.value.bean.ValueSet;
import org.smartbit4all.api.value.bean.ValueSetDefinitionData;
import org.smartbit4all.api.value.bean.ValueSetDefinitionKind;
import org.smartbit4all.api.value.bean.ValueSetExpression;
import org.smartbit4all.api.value.bean.ValueSetOperand;
import org.smartbit4all.api.value.bean.ValueSetOperation;
import org.smartbit4all.core.object.ObjectApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    CollectionTestConfig.class
})
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ValueSetApiTest {



  @Autowired
  private CollectionApi collectionApi;

  @Autowired
  private ObjectApi objectApi;

  @Autowired
  private ValueSetApi valueSetApi;

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

    ValueSet valueSetDif = valueSetApi.valuesOf("INLINE-DIF");

    Assertions.assertThat(valueSetApi.getValues(String.class, valueSetDif, Value.CODE))
        .containsExactlyInAnyOrder("B", "C");

    ValueSet valueSetUnion =
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
    valueSetApi.save(new ValueSetDefinitionData().qualifiedName("ALL-CATEGORY")
        .kind(ValueSetDefinitionKind.ALLOF).typeClass(SampleCategory.class.getName()));

    // Create SamplCategory objects to fill the set.

    // objectApi.saveAsNew(null, objectApi)


    valueSetApi.save(new ValueSetDefinitionData().qualifiedName("INLINE-NOTINCLUDED")
        .kind(ValueSetDefinitionKind.INLINE).typeClass(Value.class.getName())
        .addInlineValuesItem(new Value().code("A").displayValue("apple")));

    valueSetApi.save(new ValueSetDefinitionData().qualifiedName("INLINE-DIF")
        .kind(ValueSetDefinitionKind.COMPOSITE).typeClass(Value.class.getName())
        .expression(new ValueSetExpression().operation(ValueSetOperation.DIF)
            .addOperandsItem(new ValueSetOperand().name("INLINE-BASE"))
            .addOperandsItem(new ValueSetOperand().name("INLINE-NOTINCLUDED"))));

    ValueSet valueSetDif = valueSetApi.valuesOf("INLINE-DIF");

    Assertions.assertThat(valueSetApi.getValues(String.class, valueSetDif, Value.CODE))
        .containsExactlyInAnyOrder("B", "C");

    ValueSet valueSetUnion =
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

}
