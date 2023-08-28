package org.smartbit4all.api.object;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.ObjectDescriptor;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor.PropertyKindEnum;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.ComponentType;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectExtensionApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ObjectApiTestConfig.class})
class ObjectExtensionTest {

  @Autowired
  private ObjectApi objectApi;
  @Autowired
  private ObjectDefinitionApi objectDefinitionApi;
  @Autowired
  private ObjectExtensionApi objectExtensionApi;
  @Autowired
  private ObjectLayoutApi objectLayoutApi;

  @Test
  void extendingASingleObjectDefinitionWithAStringPropertyWorks() throws Exception {
    final String extensionName = SampleCategory.class.getName() + ".ColouredBean";
    final String favouriteColourProp = "favouriteColour";

    final ObjectDefinition<SampleCategory> sampleCategoryDef = objectDefinitionApi
        .definition(SampleCategory.class);
    final int originalPropertyCount = sampleCategoryDef.getProperties().size();

    final URI colouredBeanDescriptorUri = objectExtensionApi.create(
        extensionName,
        sampleCategoryDef,
        Arrays.asList(new ObjectPropertyDescriptor()
            .propertyName(favouriteColourProp)
            .propertyQualifiedName(String.class.getName())
            .propertyKind(PropertyKindEnum.INLINE)
            .propertyStructure(ReferencePropertyKind.REFERENCE)));
    assertThat(colouredBeanDescriptorUri).isNotNull();

    final ObjectDescriptor colouredBeanDescriptor = objectApi
        .loadLatest(colouredBeanDescriptorUri)
        .getObject(ObjectDescriptor.class);
    assertThat(colouredBeanDescriptor).isNotNull();
    assertThat(colouredBeanDescriptor.getLayoutDescriptor()).isNotNull();

    ObjectDefinition<?> extendedDef = objectDefinitionApi.definition(extensionName);
    assertThat(extendedDef).isNotNull();
    assertThat(extendedDef.getProperties()).hasSize(originalPropertyCount + 1);
    assertThat(extendedDef.getPropertiesByName())
        .containsKeys(sampleCategoryDef.getPropertiesByName().keySet().toArray(new String[0]));

    final ObjectLayoutDescriptor colouredBeanLayout = objectApi
        .loadLatest(colouredBeanDescriptor.getLayoutDescriptor())
        .getObject(ObjectLayoutDescriptor.class);

    // we expect the following form elements to be present: NAME, COST, CREATED_AT &
    // "favouriteColour"
    Map<String, SmartComponentLayoutDefinition> layoutsByName = colouredBeanLayout.getLayouts();
    assertThat(layoutsByName).isNotNull().hasSize(1).containsKey("default");
    SmartComponentLayoutDefinition layout = layoutsByName.get("default");
    assertThat(layout.getType()).isEqualTo(ComponentType.FORM);
    assertThat(layout.getForm())
        .isNotNull()
        .isNotEmpty()
        .hasSize(4)
        .anySatisfy(w -> assertThat(w)
            .returns(SampleCategory.NAME, SmartWidgetDefinition::getKey)
            .returns(SmartFormWidgetType.TEXT_FIELD, SmartWidgetDefinition::getType))
        .anySatisfy(w -> assertThat(w)
            .returns(SampleCategory.COST, SmartWidgetDefinition::getKey)
            .returns(SmartFormWidgetType.TEXT_FIELD, SmartWidgetDefinition::getType)
            .satisfies(x -> assertThat(x.getMask()).isNotEmpty()))
        .anySatisfy(w -> assertThat(w)
            .returns(SampleCategory.CREATED_AT, SmartWidgetDefinition::getKey)
            .returns(SmartFormWidgetType.DATE_TIME_PICKER, SmartWidgetDefinition::getType))
        .anySatisfy(w -> assertThat(w)
            .returns(favouriteColourProp, SmartWidgetDefinition::getKey)
            .returns(SmartFormWidgetType.TEXT_FIELD, SmartWidgetDefinition::getType));
  }

}
