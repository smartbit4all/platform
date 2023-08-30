package org.smartbit4all.api.object;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.formdefinition.bean.SmartFormWidgetType;
import org.smartbit4all.api.formdefinition.bean.SmartWidgetDefinition;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ObjectDescriptor;
import org.smartbit4all.api.object.bean.ObjectLayoutDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor;
import org.smartbit4all.api.object.bean.ObjectPropertyDescriptor.PropertyKindEnum;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.ComponentType;
import org.smartbit4all.api.smartcomponentlayoutdefinition.bean.SmartComponentLayoutDefinition;
import org.smartbit4all.api.value.bean.ValueSetDefinitionIdentifier;
import org.smartbit4all.core.object.ObjectApi;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApi;
import org.smartbit4all.core.object.ObjectExtensionApi;
import org.smartbit4all.core.object.ObjectLayoutApi;
import org.smartbit4all.core.object.ObjectNode;
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
  @DisplayName("When an object definition is extended with an inline property, the new definition"
      + " contains the old and new properties, and the generated layout contains all of them as"
      + " well.")
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

  @Test
  @DisplayName("Instances of a definition extended by a reference property can be saved, loaded and navigated through their references.")
  void extendedObjectDefinitionWithRefProperty_RefPropertyIsPresentOnNewOne_andNewInstanceIsNavigable()
      throws Exception {
    final String schema = "extension-test";

    final String extensionName = SampleCategory.class.getName() + ".Owned";
    final String ownerProp = "owner";
    // there is no real value set, but that is no concern. We just want to test if the default
    // layout is indeed generated with a combobox:
    final String userValueSetName = "users";

    final URI ownedBeanDescriptorUri = objectExtensionApi.create(
        extensionName,
        objectDefinitionApi.definition(SampleCategory.class),
        Collections.singletonList(
            new ObjectPropertyDescriptor()
                .propertyName(ownerProp)
                .propertyKind(PropertyKindEnum.REFERENCE)
                .propertyStructure(ReferencePropertyKind.REFERENCE)
                .propertyQualifiedName(URI.class.getName())
                .referencedTypeQualifiedName(User.class.getName())
                .aggregation(AggregationKind.NONE)
                .builtIn(true)
                .valueSet(new ValueSetDefinitionIdentifier().qualifiedName(userValueSetName))));
    assertThat(ownedBeanDescriptorUri).isNotNull();

    final ObjectDescriptor ownedBeanDescriptor = objectApi
        .loadLatest(ownedBeanDescriptorUri)
        .getObject(ObjectDescriptor.class);
    final ObjectLayoutDescriptor ownedBeanLayout = objectApi
        .loadLatest(ownedBeanDescriptor.getLayoutDescriptor())
        .getObject(ObjectLayoutDescriptor.class);

    Map<String, SmartComponentLayoutDefinition> layoutsByName = ownedBeanLayout.getLayouts();
    assertThat(layoutsByName).isNotNull().hasSize(1).containsKey("default");
    SmartComponentLayoutDefinition layout = layoutsByName.get("default");
    // TODO: Assertions


    final ObjectDefinition<?> ownedBeanDef = objectDefinitionApi.definition(extensionName);
    // TODO: Assertions

    ObjectNode ownedBeanNode = objectExtensionApi.newInstance(ownedBeanDescriptorUri, schema);

    final URI userUri = objectApi.saveAsNew(schema, new User()
        .username("test-user")
        .email("test@user.va")
        .password("nil"));

    ownedBeanNode.ref(ownerProp).set(userUri);
    URI ownedBeanUri = objectApi.save(ownedBeanNode);

    ownedBeanNode = objectApi.loadLatest(ownedBeanUri);
    assertThat(ownedBeanNode.getValueAsString(ownerProp, User.USERNAME)).isEqualTo("test-user");
  }

}
