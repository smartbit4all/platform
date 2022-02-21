package org.smartbit4all.core.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.mapbasedobject.bean.MapBasedObjectData;
import org.smartbit4all.core.object.utility.MapBasedObjectUtil;
import org.smartbit4all.core.utility.StringConstant;

class MapBasedObjectTest {

  @Test
  void mapBasedObjectTest() {

    // initiate test data

    String subNameKey = "subName";
    String subNameKey2 = "subName2";
    String subNameKey3 = "subName3";
    String subNameKey4 = "subName4";
    String subNameKey5 = "subName5";
    String subNameKey6 = "subName6";
    String subObjectKey = "subObject";
    String subObjectsKey = "subObjects";

    String subStringValue = "testSubName";
    String subStringValue2 = "testSubName2";
    String subStringValue3 = "testSubName3";
    String subStringValue4 = "testSubName4";
    String subStringValue5 = "testSubName5";
    String subStringValue6 = "testSubName6";

    MapBasedObjectData subData6 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(subData6, subNameKey6, subStringValue6);

    MapBasedObjectData subData5 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(subData5, subNameKey5, subStringValue5);

    MapBasedObjectData subData4 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(subData4, subNameKey4, subStringValue4);

    MapBasedObjectData subData3 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(subData3, subNameKey3, subStringValue3);

    MapBasedObjectData subData2 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(subData2, subNameKey2, subStringValue2);
    MapBasedObjectUtil.addNewPropertyToData(subData2, subObjectKey, subData6);

    MapBasedObjectData subData = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(subData, subNameKey, subStringValue);
    MapBasedObjectUtil.addNewPropertyToData(subData, subObjectsKey,
        Arrays.asList(subData4, subData5));

    String nameKey = "name";
    String namesKey = "names";
    String ageKey = "age";
    String agesKey = "ages";
    String heightKey = "height";
    String heightsKey = "heights";
    String validKey = "valid";
    String validitiesKey = "validities";
    String dateKey = "date";
    String datesKey = "dates";
    String objectKey = "object";
    String objectsKey = "objects";

    String stringValue = "testName";
    List<String> stringListValue = Arrays.asList("testName2", "testName3");
    Integer integerValue = 10;
    List<Integer> integerListValue = Arrays.asList(20, 30);
    Long longValue = 160l;
    List<Long> longListValue = Arrays.asList(170l, 180l);
    Boolean booleanValue = true;
    List<Boolean> booleanListValue = Arrays.asList(false, true);
    LocalDateTime localDateTimeValue = LocalDateTime.of(2022, 1, 1, 0, 0);
    List<LocalDateTime> localDateTimeListValue =
        Arrays.asList(LocalDateTime.of(2022, 2, 2, 0, 0), LocalDateTime.of(2022, 3, 3, 0, 0));

    MapBasedObjectData data = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(data, nameKey, stringValue);
    MapBasedObjectUtil.addNewPropertyToData(data, namesKey, stringListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, ageKey, integerValue);
    MapBasedObjectUtil.addNewPropertyToData(data, agesKey, integerListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, heightKey, longValue);
    MapBasedObjectUtil.addNewPropertyToData(data, heightsKey, longListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, validKey, booleanValue);
    MapBasedObjectUtil.addNewPropertyToData(data, validitiesKey, booleanListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, dateKey, localDateTimeValue);
    MapBasedObjectUtil.addNewPropertyToData(data, datesKey, localDateTimeListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, objectKey, subData);
    MapBasedObjectUtil.addNewPropertyToData(data, objectsKey, Arrays.asList(subData2, subData3));

    // MapBasedObject.of test

    MapBasedObject object = MapBasedObject.of(data);
    assertNotNull(object);

    Optional<ObjectChange> objectChange = object.renderAndCleanChanges();
    System.out.println(objectChange.get());

    Object value, values;
    List<?> list;

    // getValueByPath test

    assertEquals(stringValue, object.getValueByPath(nameKey));
    assertEquals(integerValue, object.getValueByPath(ageKey));
    assertEquals(longValue, object.getValueByPath(heightKey));
    assertEquals(booleanValue, object.getValueByPath(validKey));
    assertEquals(localDateTimeValue, object.getValueByPath(dateKey));

    values = object.getValueByPath(namesKey);
    assertTrue(values instanceof List);
    assertEquals(stringListValue.size(), ((List) values).size());

    values = object.getValueByPath(agesKey);
    assertTrue(values instanceof List);
    assertEquals(integerListValue.size(), ((List) values).size());

    values = object.getValueByPath(heightsKey);
    assertTrue(values instanceof List);
    assertEquals(longListValue.size(), ((List) values).size());

    values = object.getValueByPath(validitiesKey);
    assertTrue(values instanceof List);
    assertEquals(booleanListValue.size(), ((List) values).size());

    values = object.getValueByPath(datesKey);
    assertTrue(values instanceof List);
    assertEquals(localDateTimeListValue.size(), ((List) values).size());

    value = object.getValueByPath(objectKey);
    assertTrue(value instanceof MapBasedObject);
    assertEquals(subStringValue, ((MapBasedObject) value).getValueByPath(subNameKey));

    values = object.getValueByPath(objectsKey);
    assertTrue(values instanceof List);
    list = (List) values;
    assertTrue(list.get(0) instanceof MapBasedObject);
    assertEquals(subStringValue2, ((MapBasedObject) list.get(0)).getValueByPath(subNameKey2));
    assertEquals(subStringValue3, ((MapBasedObject) list.get(1)).getValueByPath(subNameKey3));

    assertEquals(subStringValue, object.getValueByPath(objectKey + "/" + subNameKey));
    assertEquals(subStringValue2, object.getValueByPath(objectsKey + "/0/" + subNameKey2));
    assertEquals(subStringValue3, object.getValueByPath(objectsKey + "/1/" + subNameKey3));
    assertEquals(subStringValue4,
        object.getValueByPath(objectKey + "/" + subObjectsKey + "/0/" + subNameKey4));
    assertEquals(subStringValue5,
        object.getValueByPath(objectKey + "/" + subObjectsKey + "/1/" + subNameKey5));
    assertEquals(subStringValue6,
        object.getValueByPath(objectsKey + "/0/" + subObjectKey + "/" + subNameKey6));

    // MapBasedObject.toData test

    MapBasedObjectData newData = MapBasedObject.toData(object);

    assertTrue(newData.getStringPropertyMap().containsKey(nameKey));
    assertTrue(newData.getStringListMap().containsKey(namesKey));
    assertTrue(newData.getIntegerPropertyMap().containsKey(ageKey));
    assertTrue(newData.getIntegerListMap().containsKey(agesKey));
    assertTrue(newData.getLongPropertyMap().containsKey(heightKey));
    assertTrue(newData.getLongListMap().containsKey(heightsKey));
    assertTrue(newData.getBooleanPropertyMap().containsKey(validKey));
    assertTrue(newData.getBooleanListMap().containsKey(validitiesKey));
    assertTrue(newData.getLocalDateTimePropertyMap().containsKey(dateKey));
    assertTrue(newData.getLocalDateTimeListMap().containsKey(datesKey));
    assertTrue(newData.getObjectPropertyMap().containsKey(objectKey));
    assertTrue(newData.getObjectListMap().containsKey(objectsKey));

    assertEquals(data.getStringPropertyMap().size(), newData.getStringPropertyMap().size());
    assertEquals(data.getStringListMap().size(), newData.getStringListMap().size());
    assertEquals(data.getIntegerPropertyMap().size(), newData.getIntegerPropertyMap().size());
    assertEquals(data.getIntegerListMap().size(), newData.getIntegerListMap().size());
    assertEquals(data.getLongPropertyMap().size(), newData.getLongPropertyMap().size());
    assertEquals(data.getLongListMap().size(), newData.getLongListMap().size());
    assertEquals(data.getBooleanPropertyMap().size(), newData.getBooleanPropertyMap().size());
    assertEquals(data.getBooleanListMap().size(), newData.getBooleanListMap().size());
    assertEquals(data.getLocalDateTimePropertyMap().size(),
        newData.getLocalDateTimePropertyMap().size());
    assertEquals(data.getLocalDateTimeListMap().size(), newData.getLocalDateTimeListMap().size());
    assertEquals(data.getObjectPropertyMap().size(), newData.getObjectPropertyMap().size());
    assertEquals(data.getObjectListMap().size(), newData.getObjectListMap().size());

    // setValueByPath test

    String newStringValue = "newTestName";
    List<String> newStringListValue = Arrays.asList("newTestName2", "newTestName3", "newTestName4");
    Integer newIntegerValue = 15;
    List<Integer> newIntegerListValue = Arrays.asList(25, 35, 45);
    Long newLongValue = 165l;
    List<Long> newLongListValue = Arrays.asList(175l, 185l, 195l);
    Boolean newBooleanValue = false;
    List<Boolean> newBooleanListValue = Arrays.asList(true, false, true);
    LocalDateTime newLocalDateTimeValue = LocalDateTime.of(2022, 4, 4, 0, 0);
    List<LocalDateTime> newLocalDateTimeListValue =
        Arrays.asList(LocalDateTime.of(2022, 5, 5, 0, 0), LocalDateTime.of(2022, 6, 6, 0, 0),
            LocalDateTime.of(2022, 7, 7, 0, 0));
    String newSubStringValue = "newTestSubName";
    String newStringListItemValue = "newTestListItemName";

    object.setValueByPath(nameKey, newStringValue);
    object.setValueByPath(namesKey, new ArrayList<>(newStringListValue));
    object.setValueByPath(ageKey, newIntegerValue);
    object.setValueByPath(agesKey, new ArrayList<>(newIntegerListValue));
    object.setValueByPath(heightKey, newLongValue);
    object.setValueByPath(heightsKey, new ArrayList<>(newLongListValue));
    object.setValueByPath(validKey, newBooleanValue);
    object.setValueByPath(validitiesKey, new ArrayList<>(newBooleanListValue));
    object.setValueByPath(dateKey, newLocalDateTimeValue);
    object.setValueByPath(datesKey, new ArrayList<>(newLocalDateTimeListValue));
    object.setValueByPath(objectKey + "/" + subNameKey, newSubStringValue);
    object.setValueByPath(objectsKey, new ArrayList<>(Arrays.asList(subData4, subData5)));
    // TODO test MapBasedObject list

    objectChange = object.renderAndCleanChanges();
    System.out.println(StringConstant.NEW_LINE + objectChange.get());

    assertEquals(newStringValue, object.getValueByPath(nameKey));
    assertEquals(newIntegerValue, object.getValueByPath(ageKey));
    assertEquals(newLongValue, object.getValueByPath(heightKey));
    assertEquals(newBooleanValue, object.getValueByPath(validKey));
    assertEquals(newLocalDateTimeValue, object.getValueByPath(dateKey));

    values = object.getValueByPath(namesKey);
    assertTrue(values instanceof List);
    assertEquals(newStringListValue.size(), ((List) values).size());

    values = object.getValueByPath(agesKey);
    assertTrue(values instanceof List);
    assertEquals(newIntegerListValue.size(), ((List) values).size());

    values = object.getValueByPath(heightsKey);
    assertTrue(values instanceof List);
    assertEquals(newLongListValue.size(), ((List) values).size());

    values = object.getValueByPath(validitiesKey);
    assertTrue(values instanceof List);
    assertEquals(newBooleanListValue.size(), ((List) values).size());

    values = object.getValueByPath(datesKey);
    assertTrue(values instanceof List);
    assertEquals(newLocalDateTimeListValue.size(), ((List) values).size());

    value = object.getValueByPath(objectKey);
    assertTrue(value instanceof MapBasedObject);
    assertEquals(newSubStringValue, ((MapBasedObject) value).getValueByPath(subNameKey));

    values = object.getValueByPath(objectsKey);
    assertTrue(values instanceof List);
    list = (List) values;
    assertTrue(list.get(0) instanceof MapBasedObject);
    assertEquals(subStringValue4, ((MapBasedObject) list.get(0)).getValueByPath(subNameKey4));
    assertEquals(subStringValue5, ((MapBasedObject) list.get(1)).getValueByPath(subNameKey5));

    assertEquals(newSubStringValue, object.getValueByPath(objectKey + "/" + subNameKey));
    // TODO test MapBasedObject list

    object.setValueByPath(namesKey + "/2", newStringListItemValue);
    object.setValueByPath(objectsKey + "/1", subData6);

    objectChange = object.renderAndCleanChanges();
    System.out.println(StringConstant.NEW_LINE + objectChange.get());

    // setValueByPath exceptions test

    String addressKey = "address";
    String addressValue1 = "Test street 1.";
    String addressValue2 = "Test street 2.";
    String numberKey = "number";
    String numberDataKey = "numberdata";

    object.setValueByPath(addressKey, addressValue1);
    assertEquals(addressValue1, object.getValueByPath(addressKey));

    object.setValueByPath(addressKey, addressValue2);
    assertEquals(addressValue2, object.getValueByPath(addressKey));

    assertThrows(IllegalArgumentException.class, () -> object.setValueByPath(addressKey, 2));
    assertThrows(IllegalArgumentException.class,
        () -> object.setValueByPath(addressKey + "/" + numberKey, 2));

    object.setValueByPath(numberDataKey + "/" + numberKey, 2);
    assertEquals(2, object.getValueByPath(numberDataKey + "/" + numberKey));

    // setValueByPath null value test

    String userNameKey = "userName";
    String userNameValue = "testUserName";

    object.setValueByPath(userNameKey, null, String.class);
    assertThrows(IllegalArgumentException.class, () -> object.setValueByPath(userNameKey, 2));
    object.setValueByPath(userNameKey, userNameValue);
    assertEquals(userNameValue, object.getValueByPath(userNameKey));
    object.setValueByPath(userNameKey, null);
    assertNull(object.getValueByPath(userNameKey));

    object.setValueByPath(ageKey, null);
    assertNull(object.getValueByPath(ageKey));
    object.setValueByPath(ageKey, 50);
    assertEquals(50, object.getValueByPath(ageKey));

    // addValueByPath test

    String addedNamesValue = "testAddedName";
    object.addValueByPath(namesKey, addedNamesValue);
    list = (List) object.getValueByPath(namesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedNamesValue));
    object.addValueByPath(namesKey, null);
    list = (List) object.getValueByPath(namesKey);
    assertEquals(5, list.size());

    Integer addedAgesValue = 50;
    object.addValueByPath(agesKey, addedAgesValue);
    list = (List) object.getValueByPath(agesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedAgesValue));
    object.addValueByPath(agesKey, null);
    list = (List) object.getValueByPath(agesKey);
    assertEquals(5, list.size());

    Long addedHeightsValue = 200l;
    object.addValueByPath(heightsKey, addedHeightsValue);
    list = (List) object.getValueByPath(heightsKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedHeightsValue));
    object.addValueByPath(heightsKey, null);
    list = (List) object.getValueByPath(heightsKey);
    assertEquals(5, list.size());

    Boolean addedValiditiesValue = true;
    object.addValueByPath(validitiesKey, addedValiditiesValue);
    list = (List) object.getValueByPath(validitiesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedValiditiesValue));
    object.addValueByPath(validitiesKey, null);
    list = (List) object.getValueByPath(validitiesKey);
    assertEquals(5, list.size());

    LocalDateTime addedDatesValue = LocalDateTime.of(2022, 6, 6, 0, 0);
    object.addValueByPath(datesKey, addedDatesValue);
    list = (List) object.getValueByPath(datesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedDatesValue));
    object.addValueByPath(datesKey, null);
    list = (List) object.getValueByPath(datesKey);
    assertEquals(5, list.size());

    objectChange = object.renderAndCleanChanges();
    System.out.println(StringConstant.NEW_LINE + objectChange.get());

    // TODO test MapBasedObject list

    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath("notExistingKey", addedNamesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(nameKey, addedNamesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(nameKey, addedAgesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(nameKey, addedHeightsValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(nameKey, addedValiditiesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(nameKey, addedDatesValue));

    // removeValueByPath test

    object.removeValueByPath(namesKey + "/3");
    list = (List) object.getValueByPath(namesKey);
    assertTrue(!list.contains(addedNamesValue));
    assertEquals(4, list.size());
    object.removeValueByPath(namesKey + "/2");
    assertEquals(3, list.size());

    assertThrows(IllegalArgumentException.class,
        () -> object.removeValueByPath(namesKey));
    assertThrows(IllegalArgumentException.class,
        () -> object.removeValueByPath(namesKey + "/notnumber"));

    // TODO more types, incorrect parameters

    // embedded empty list test

    String emptyStringListKey = "emptyStringList";
    String embeddedEmptyStringListKey = objectKey + "/" + emptyStringListKey;
    object.setValueByPath(embeddedEmptyStringListKey, new ArrayList<>(Arrays.asList("tmp")));
    object.removeValueByPath(embeddedEmptyStringListKey + "/0");

    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(embeddedEmptyStringListKey, 123));

    object.addValueByPath(embeddedEmptyStringListKey, addedNamesValue);
    list = (List) object.getValueByPath(embeddedEmptyStringListKey);
    assertEquals(1, list.size());
    assertTrue(list.contains(addedNamesValue));
  }

  // @Test
  // void apiObjectRefTest() {
  // List<ReferredBean> beans = new ArrayList<>();
  // ReferredBean bean1 = new ReferredBean();
  // bean1.setName("bean1");
  // beans.add(bean1);
  //
  // Set<Class<?>> domainBeans;
  // Map<Class<?>, ApiBeanDescriptor> descriptors;
  // domainBeans = new HashSet<>();
  // domainBeans.add(TestObject.class);
  // domainBeans.add(ReferredBean.class);
  // descriptors = ApiBeanDescriptor.of(domainBeans);
  //
  // TestObject testObj = new TestObject(beans);
  // ApiObjectRef ref = new ApiObjectRef(null, testObj, descriptors);
  // Optional<ObjectChange> change = ref.renderAndCleanChanges();
  //
  // ReferredBean bean2 = new ReferredBean();
  // bean2.setName("bean2");
  // beans.add(bean2);
  // ref.setObject(testObj);
  // Optional<ObjectChange> change2 = ref.renderAndCleanChanges();
  //
  // ReferredBean bean3 = new ReferredBean();
  // bean3.setName("bean3");
  // beans.add(bean3);
  // ref.setObject(testObj);
  // Optional<ObjectChange> change3 = ref.renderAndCleanChanges();
  //
  // beans.remove(1);
  // ref.setObject(testObj);
  // Optional<ObjectChange> change4 = ref.renderAndCleanChanges();
  //
  // System.out.println();
  // }

  public class TestObject {
    private List<ReferredBean> beans;

    public TestObject(List<ReferredBean> beans) {
      this.beans = beans;
    }

    public List<ReferredBean> getBeans() {
      return beans;
    }

    public void setBeans(List<ReferredBean> beans) {
      this.beans = beans;
    }
  }

}
