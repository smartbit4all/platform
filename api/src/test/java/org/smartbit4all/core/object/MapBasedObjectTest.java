package org.smartbit4all.core.object;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    String weightKey = "weight";
    String weightsKey = "weights";
    String validKey = "valid";
    String validitiesKey = "validities";
    String uriKey = "uri";
    String urisKey = "uris";
    String dateKey = "date";
    String datesKey = "dates";
    String timeKey = "time";
    String timesKey = "times";
    String dateTimeKey = "datetime";
    String dateTimesKey = "datetimes";
    String objectKey = "object";
    String objectsKey = "objects";

    String stringValue = "testName";
    List<String> stringListValue = Arrays.asList("testName2", "testName3");
    Integer integerValue = 10;
    List<Integer> integerListValue = Arrays.asList(20, 30);
    Long longValue = 160l;
    List<Long> longListValue = Arrays.asList(170l, 180l);
    Double doubleValue = 45.5;
    List<Double> doubleListValue = Arrays.asList(50.5, 55.5);
    Boolean booleanValue = true;
    List<Boolean> booleanListValue = Arrays.asList(false, true);
    URI uriValue = URI.create("/testUri");
    List<URI> uriListValue = Arrays.asList(URI.create("/testUri2"), URI.create("/testUri3"));
    LocalDate localDateValue = LocalDate.of(2022, 1, 1);
    List<LocalDate> localDateListValue =
        Arrays.asList(LocalDate.of(2022, 2, 2), LocalDate.of(2022, 3, 3));
    LocalTime localTimeValue = LocalTime.of(1, 1, 1, 0);
    List<LocalTime> localTimeListValue =
        Arrays.asList(LocalTime.of(2, 2, 2, 0), LocalTime.of(3, 3, 3, 0));
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
    MapBasedObjectUtil.addNewPropertyToData(data, weightKey, doubleValue);
    MapBasedObjectUtil.addNewPropertyToData(data, weightsKey, doubleListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, validKey, booleanValue);
    MapBasedObjectUtil.addNewPropertyToData(data, validitiesKey, booleanListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, uriKey, uriValue);
    MapBasedObjectUtil.addNewPropertyToData(data, urisKey, uriListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, dateKey, localDateValue);
    MapBasedObjectUtil.addNewPropertyToData(data, datesKey, localDateListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, timeKey, localTimeValue);
    MapBasedObjectUtil.addNewPropertyToData(data, timesKey, localTimeListValue);
    MapBasedObjectUtil.addNewPropertyToData(data, dateTimeKey, localDateTimeValue);
    MapBasedObjectUtil.addNewPropertyToData(data, dateTimesKey, localDateTimeListValue);
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
    assertEquals(doubleValue, object.getValueByPath(weightKey));
    assertEquals(booleanValue, object.getValueByPath(validKey));
    assertEquals(uriValue, object.getValueByPath(uriKey));
    assertEquals(localDateValue, object.getValueByPath(dateKey));
    assertEquals(localTimeValue, object.getValueByPath(timeKey));
    assertEquals(localDateTimeValue, object.getValueByPath(dateTimeKey));

    values = object.getValueByPath(namesKey);
    assertTrue(values instanceof List);
    assertEquals(stringListValue.size(), ((List) values).size());

    values = object.getValueByPath(agesKey);
    assertTrue(values instanceof List);
    assertEquals(integerListValue.size(), ((List) values).size());

    values = object.getValueByPath(heightsKey);
    assertTrue(values instanceof List);
    assertEquals(longListValue.size(), ((List) values).size());

    values = object.getValueByPath(weightsKey);
    assertTrue(values instanceof List);
    assertEquals(longListValue.size(), ((List) values).size());

    values = object.getValueByPath(validitiesKey);
    assertTrue(values instanceof List);
    assertEquals(booleanListValue.size(), ((List) values).size());

    values = object.getValueByPath(urisKey);
    assertTrue(values instanceof List);
    assertEquals(uriListValue.size(), ((List) values).size());

    values = object.getValueByPath(datesKey);
    assertTrue(values instanceof List);
    assertEquals(localDateTimeListValue.size(), ((List) values).size());

    values = object.getValueByPath(timesKey);
    assertTrue(values instanceof List);
    assertEquals(localDateTimeListValue.size(), ((List) values).size());

    values = object.getValueByPath(dateTimesKey);
    assertTrue(values instanceof List);
    assertEquals(localDateTimeListValue.size(), ((List) values).size());

    value = object.getValueByPath(objectKey);
    assertTrue(value instanceof MapBasedObjectData);
    assertEquals(subStringValue, ((MapBasedObjectData) value).getStringPropertyMap()
        .get(subNameKey.toUpperCase()).getValue());

    values = object.getValueByPath(objectsKey);
    assertTrue(values instanceof List);
    list = (List) values;
    assertTrue(list.get(0) instanceof MapBasedObjectData);
    assertEquals(subStringValue2, ((MapBasedObjectData) list.get(0)).getStringPropertyMap()
        .get(subNameKey2.toUpperCase()).getValue());
    assertEquals(subStringValue3, ((MapBasedObjectData) list.get(1)).getStringPropertyMap()
        .get(subNameKey3.toUpperCase()).getValue());

    assertEquals(subStringValue, object.getValueByPath(objectKey + "/" + subNameKey));
    assertEquals(subStringValue2, object.getValueByPath(objectsKey + "/0/" + subNameKey2));
    assertEquals(subStringValue3, object.getValueByPath(objectsKey + "/1/" + subNameKey3));
    assertEquals(subStringValue4,
        object.getValueByPath(objectKey + "/" + subObjectsKey + "/0/" + subNameKey4));
    assertEquals(subStringValue5,
        object.getValueByPath(objectKey + "/" + subObjectsKey + "/1/" + subNameKey5));
    assertEquals(subStringValue6,
        object.getValueByPath(objectsKey + "/0/" + subObjectKey + "/" + subNameKey6));

    // getValueRefByPath test

    assertEquals(object, object.getValueRefByPath(""));
    assertEquals(object, object.getValueRefByPath("/"));
    assertEquals(subStringValue, object.getValueRefByPath(objectKey).getValueByPath(subNameKey));
    assertEquals(subStringValue2,
        object.getValueRefByPath(objectsKey + "/0").getValueByPath(subNameKey2));
    assertEquals(subStringValue3,
        object.getValueRefByPath(objectsKey + "/1").getValueByPath(subNameKey3));
    assertEquals(subStringValue4, object.getValueRefByPath(objectKey + "/" + subObjectsKey + "/0")
        .getValueByPath(subNameKey4));
    assertEquals(subStringValue5, object.getValueRefByPath(objectKey + "/" + subObjectsKey + "/1")
        .getValueByPath(subNameKey5));
    assertEquals(subStringValue6,
        object.getValueRefByPath(objectsKey + "/0/" + subObjectKey).getValueByPath(subNameKey6));

    // MapBasedObject.toData test

    MapBasedObjectData newData = MapBasedObject.toData(object);

    assertTrue(newData.getStringPropertyMap().containsKey(nameKey.toUpperCase()));
    assertTrue(newData.getStringListMap().containsKey(namesKey.toUpperCase()));
    assertTrue(newData.getIntegerPropertyMap().containsKey(ageKey.toUpperCase()));
    assertTrue(newData.getIntegerListMap().containsKey(agesKey.toUpperCase()));
    assertTrue(newData.getLongPropertyMap().containsKey(heightKey.toUpperCase()));
    assertTrue(newData.getLongListMap().containsKey(heightsKey.toUpperCase()));
    assertTrue(newData.getDoublePropertyMap().containsKey(weightKey.toUpperCase()));
    assertTrue(newData.getDoubleListMap().containsKey(weightsKey.toUpperCase()));
    assertTrue(newData.getBooleanPropertyMap().containsKey(validKey.toUpperCase()));
    assertTrue(newData.getBooleanListMap().containsKey(validitiesKey.toUpperCase()));
    assertTrue(newData.getUriPropertyMap().containsKey(uriKey.toUpperCase()));
    assertTrue(newData.getUriListMap().containsKey(urisKey.toUpperCase()));
    assertTrue(newData.getLocalDatePropertyMap().containsKey(dateKey.toUpperCase()));
    assertTrue(newData.getLocalDateListMap().containsKey(datesKey.toUpperCase()));
    assertTrue(newData.getLocalTimePropertyMap().containsKey(timeKey.toUpperCase()));
    assertTrue(newData.getLocalTimeListMap().containsKey(timesKey.toUpperCase()));
    assertTrue(newData.getLocalDateTimePropertyMap().containsKey(dateTimeKey.toUpperCase()));
    assertTrue(newData.getLocalDateTimeListMap().containsKey(dateTimesKey.toUpperCase()));
    assertTrue(newData.getObjectPropertyMap().containsKey(objectKey.toUpperCase()));
    assertTrue(newData.getObjectListMap().containsKey(objectsKey.toUpperCase()));

    assertEquals(data.getStringPropertyMap().size(), newData.getStringPropertyMap().size());
    assertEquals(data.getStringListMap().size(), newData.getStringListMap().size());
    assertEquals(data.getIntegerPropertyMap().size(), newData.getIntegerPropertyMap().size());
    assertEquals(data.getIntegerListMap().size(), newData.getIntegerListMap().size());
    assertEquals(data.getLongPropertyMap().size(), newData.getLongPropertyMap().size());
    assertEquals(data.getLongListMap().size(), newData.getLongListMap().size());
    assertEquals(data.getDoublePropertyMap().size(), newData.getDoublePropertyMap().size());
    assertEquals(data.getDoubleListMap().size(), newData.getDoubleListMap().size());
    assertEquals(data.getBooleanPropertyMap().size(), newData.getBooleanPropertyMap().size());
    assertEquals(data.getBooleanListMap().size(), newData.getBooleanListMap().size());
    assertEquals(data.getUriPropertyMap().size(), newData.getUriPropertyMap().size());
    assertEquals(data.getUriListMap().size(), newData.getUriListMap().size());
    assertEquals(data.getLocalDatePropertyMap().size(), newData.getLocalDatePropertyMap().size());
    assertEquals(data.getLocalTimePropertyMap().size(), newData.getLocalTimePropertyMap().size());
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
    Double newDoubleValue = 65.5;
    List<Double> newDoubleListValue = Arrays.asList(70.5, 75.5, 80.5);
    Boolean newBooleanValue = false;
    List<Boolean> newBooleanListValue = Arrays.asList(true, false, true);
    URI newUriValue = URI.create("/newTestUri");
    List<URI> newUriListValue = Arrays.asList(URI.create("/newTestUri1"),
        URI.create("/newTestUri2"), URI.create("/newTestUri3"));
    LocalDate newLocalDateValue = LocalDate.of(2022, 4, 4);
    List<LocalDate> newLocalDateListValue =
        Arrays.asList(LocalDate.of(2022, 5, 5), LocalDate.of(2022, 6, 6), LocalDate.of(2022, 7, 7));
    LocalTime newLocalTimeValue = LocalTime.of(4, 4, 4, 0);
    List<LocalTime> newLocalTimeListValue =
        Arrays.asList(LocalTime.of(5, 5, 5, 0), LocalTime.of(6, 6, 6, 0), LocalTime.of(7, 7, 7, 0));
    LocalDateTime newLocalDateTimeValue = LocalDateTime.of(2022, 4, 4, 0, 0);
    List<LocalDateTime> newLocalDateTimeListValue =
        Arrays.asList(LocalDateTime.of(2022, 5, 5, 0, 0), LocalDateTime.of(2022, 6, 6, 0, 0),
            LocalDateTime.of(2022, 7, 7, 0, 0));
    String newSubStringValue = "newTestSubName";
    String newStringListItemValue = "newTestListItemName";
    TestEnum newEnumValue = TestEnum.VALUE1;

    object.setValueByPath(nameKey, newStringValue);
    object.setValueByPath(namesKey, new ArrayList<>(newStringListValue));
    object.setValueByPath(ageKey, newIntegerValue);
    object.setValueByPath(agesKey, new ArrayList<>(newIntegerListValue));
    object.setValueByPath(heightKey, newLongValue);
    object.setValueByPath(heightsKey, new ArrayList<>(newLongListValue));
    object.setValueByPath(weightKey, newDoubleValue);
    object.setValueByPath(weightsKey, new ArrayList<>(newDoubleListValue));
    object.setValueByPath(validKey, newBooleanValue);
    object.setValueByPath(validitiesKey, new ArrayList<>(newBooleanListValue));
    object.setValueByPath(uriKey, newUriValue);
    object.setValueByPath(urisKey, new ArrayList<>(newUriListValue));
    object.setValueByPath(dateKey, newLocalDateValue);
    object.setValueByPath(datesKey, new ArrayList<>(newLocalDateListValue));
    object.setValueByPath(timeKey, newLocalTimeValue);
    object.setValueByPath(timesKey, new ArrayList<>(newLocalTimeListValue));
    object.setValueByPath(dateTimeKey, newLocalDateTimeValue);
    object.setValueByPath(dateTimesKey, new ArrayList<>(newLocalDateTimeListValue));
    object.setValueByPath(objectKey + "/" + subNameKey, newSubStringValue);
    object.setValueByPath(objectsKey, new ArrayList<>(Arrays.asList(subData4, subData5)));

    objectChange = object.renderAndCleanChanges();
    System.out.println(StringConstant.NEW_LINE + objectChange.get());

    assertEquals(newStringValue, object.getValueByPath(nameKey));
    assertEquals(newIntegerValue, object.getValueByPath(ageKey));
    assertEquals(newLongValue, object.getValueByPath(heightKey));
    assertEquals(newDoubleValue, object.getValueByPath(weightKey));
    assertEquals(newUriValue, object.getValueByPath(uriKey));
    assertEquals(newBooleanValue, object.getValueByPath(validKey));
    assertEquals(newLocalDateValue, object.getValueByPath(dateKey));
    assertEquals(newLocalTimeValue, object.getValueByPath(timeKey));
    assertEquals(newLocalDateTimeValue, object.getValueByPath(dateTimeKey));

    object.setValueByPath(nameKey, newEnumValue);
    assertEquals(newEnumValue.toString(), object.getValueByPath(nameKey));

    values = object.getValueByPath(namesKey);
    assertTrue(values instanceof List);
    assertEquals(newStringListValue.size(), ((List) values).size());

    values = object.getValueByPath(agesKey);
    assertTrue(values instanceof List);
    assertEquals(newIntegerListValue.size(), ((List) values).size());

    values = object.getValueByPath(heightsKey);
    assertTrue(values instanceof List);
    assertEquals(newLongListValue.size(), ((List) values).size());

    values = object.getValueByPath(weightsKey);
    assertTrue(values instanceof List);
    assertEquals(newDoubleListValue.size(), ((List) values).size());

    values = object.getValueByPath(validitiesKey);
    assertTrue(values instanceof List);
    assertEquals(newBooleanListValue.size(), ((List) values).size());

    values = object.getValueByPath(urisKey);
    assertTrue(values instanceof List);
    assertEquals(newUriListValue.size(), ((List) values).size());

    values = object.getValueByPath(datesKey);
    assertTrue(values instanceof List);
    assertEquals(newLocalDateListValue.size(), ((List) values).size());

    values = object.getValueByPath(timesKey);
    assertTrue(values instanceof List);
    assertEquals(newLocalTimeListValue.size(), ((List) values).size());

    values = object.getValueByPath(dateTimesKey);
    assertTrue(values instanceof List);
    assertEquals(newLocalDateTimeListValue.size(), ((List) values).size());

    value = object.getValueByPath(objectKey);
    assertTrue(value instanceof MapBasedObjectData);
    assertEquals(newSubStringValue, ((MapBasedObjectData) value).getStringPropertyMap()
        .get(subNameKey.toUpperCase()).getValue());

    values = object.getValueByPath(objectsKey);
    assertTrue(values instanceof List);
    list = (List) values;
    assertTrue(list.get(0) instanceof MapBasedObjectData);
    assertEquals(subStringValue4, ((MapBasedObjectData) list.get(0)).getStringPropertyMap()
        .get(subNameKey4.toUpperCase()).getValue());
    assertEquals(subStringValue5, ((MapBasedObjectData) list.get(1)).getStringPropertyMap()
        .get(subNameKey5.toUpperCase()).getValue());

    // get/setValueByPath test with longer paths, indexes

    assertEquals(newSubStringValue, object.getValueByPath(objectKey + "/" + subNameKey));

    object.setValueByPath(namesKey + "/2", newStringListItemValue);
    assertEquals(newStringListItemValue, object.getValueByPath(namesKey + "/2"));

    object.setValueByPath(objectsKey + "/1", subData6);
    assertEquals(subStringValue6, object.getValueByPath(objectsKey + "/1/" + subNameKey6));

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

    Double addedWeightsValue = 85.5;
    object.addValueByPath(weightsKey, addedWeightsValue);
    list = (List) object.getValueByPath(weightsKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedWeightsValue));
    object.addValueByPath(weightsKey, null);
    list = (List) object.getValueByPath(weightsKey);
    assertEquals(5, list.size());

    Boolean addedValiditiesValue = true;
    object.addValueByPath(validitiesKey, addedValiditiesValue);
    list = (List) object.getValueByPath(validitiesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedValiditiesValue));
    object.addValueByPath(validitiesKey, null);
    list = (List) object.getValueByPath(validitiesKey);
    assertEquals(5, list.size());

    URI addedUrisValue = URI.create("/testAddedUri");
    object.addValueByPath(urisKey, addedUrisValue);
    list = (List) object.getValueByPath(urisKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedUrisValue));
    object.addValueByPath(urisKey, null);
    list = (List) object.getValueByPath(urisKey);
    assertEquals(5, list.size());

    LocalDate addedDatesValue = LocalDate.of(2022, 8, 8);
    object.addValueByPath(datesKey, addedDatesValue);
    list = (List) object.getValueByPath(datesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedDatesValue));
    object.addValueByPath(datesKey, null);
    list = (List) object.getValueByPath(datesKey);
    assertEquals(5, list.size());

    LocalTime addedTimesValue = LocalTime.of(8, 8, 8);
    object.addValueByPath(timesKey, addedTimesValue);
    list = (List) object.getValueByPath(timesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedTimesValue));
    object.addValueByPath(timesKey, null);
    list = (List) object.getValueByPath(timesKey);
    assertEquals(5, list.size());

    LocalDateTime addedDateTimesValue = LocalDateTime.of(2022, 8, 8, 0, 0);
    object.addValueByPath(dateTimesKey, addedDateTimesValue);
    list = (List) object.getValueByPath(dateTimesKey);
    assertEquals(4, list.size());
    assertTrue(list.contains(addedDateTimesValue));
    object.addValueByPath(dateTimesKey, null);
    list = (List) object.getValueByPath(dateTimesKey);
    assertEquals(5, list.size());

    object.addValueByPath(objectsKey, subData);
    list = (List) object.getValueByPath(objectsKey);
    assertEquals(3, list.size());
    assertEquals(subStringValue, ((MapBasedObjectData) list.get(2)).getStringPropertyMap()
        .get(subNameKey.toUpperCase()).getValue());
    assertThrows(IllegalArgumentException.class, () -> object.addValueByPath(objectsKey, null));
    list = (List) object.getValueByPath(objectsKey);
    assertEquals(3, list.size());

    objectChange = object.renderAndCleanChanges();
    System.out.println(StringConstant.NEW_LINE + objectChange.get());

    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath("notExistingKey", addedNamesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(namesKey + "/1", addedNamesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(nameKey, addedNamesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(ageKey, addedAgesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(heightKey, addedHeightsValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(weightKey, addedWeightsValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(validKey, addedValiditiesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(uriKey, addedUrisValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(dateKey, addedDatesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(timeKey, addedTimesValue));
    assertThrows(IllegalArgumentException.class,
        () -> object.addValueByPath(dateTimeKey, addedDateTimesValue));

    // removeValueByPath test

    object.removeValueByPath(namesKey + "/3");
    list = (List) object.getValueByPath(namesKey);
    assertTrue(!list.contains(addedNamesValue));
    assertEquals(4, list.size());
    object.removeValueByPath(namesKey + "/2");
    assertEquals(3, list.size());

    object.removeValueByPath(agesKey + "/3");
    list = (List) object.getValueByPath(agesKey);
    assertTrue(!list.contains(addedAgesValue));
    assertEquals(4, list.size());
    object.removeValueByPath(agesKey + "/2");
    assertEquals(3, list.size());

    object.removeValueByPath(heightsKey + "/3");
    list = (List) object.getValueByPath(heightsKey);
    assertTrue(!list.contains(addedHeightsValue));
    assertEquals(4, list.size());
    object.removeValueByPath(heightsKey + "/2");
    assertEquals(3, list.size());

    object.removeValueByPath(weightsKey + "/3");
    list = (List) object.getValueByPath(weightsKey);
    assertTrue(!list.contains(addedWeightsValue));
    assertEquals(4, list.size());
    object.removeValueByPath(weightsKey + "/2");
    assertEquals(3, list.size());

    list = (List) object.getValueByPath(validitiesKey);
    object.removeValueByPath(validitiesKey + "/1");
    list = (List) object.getValueByPath(validitiesKey);
    assertTrue(!list.contains(false));
    assertEquals(4, list.size());
    object.removeValueByPath(validitiesKey + "/2");
    assertEquals(3, list.size());

    object.removeValueByPath(urisKey + "/3");
    list = (List) object.getValueByPath(urisKey);
    assertTrue(!list.contains(addedUrisValue));
    assertEquals(4, list.size());
    object.removeValueByPath(urisKey + "/2");
    assertEquals(3, list.size());

    object.removeValueByPath(datesKey + "/3");
    list = (List) object.getValueByPath(datesKey);
    assertTrue(!list.contains(addedDatesValue));
    assertEquals(4, list.size());
    object.removeValueByPath(datesKey + "/2");
    assertEquals(3, list.size());

    object.removeValueByPath(timesKey + "/3");
    list = (List) object.getValueByPath(timesKey);
    assertTrue(!list.contains(addedTimesValue));
    assertEquals(4, list.size());
    object.removeValueByPath(timesKey + "/2");
    assertEquals(3, list.size());

    object.removeValueByPath(dateTimesKey + "/3");
    list = (List) object.getValueByPath(dateTimesKey);
    assertTrue(!list.contains(addedDateTimesValue));
    assertEquals(4, list.size());
    object.removeValueByPath(dateTimesKey + "/2");
    assertEquals(3, list.size());

    assertThrows(IllegalArgumentException.class,
        () -> object.removeValueByPath(namesKey));
    assertThrows(IllegalArgumentException.class,
        () -> object.removeValueByPath(namesKey + "/notnumber"));
    assertThrows(IndexOutOfBoundsException.class,
        () -> object.removeValueByPath(namesKey + "/8"));

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

  @Test
  void compareWithApiObjectRefTest() {

    // render changes from ApiObjectRef

    MasterBean beanToRef = constructMasterBean();

    Map<Class<?>, ApiBeanDescriptor> descriptors;
    Set<Class<?>> domainBeans = new HashSet<>();
    domainBeans.add(MasterBean.class);
    domainBeans.add(MasterDetailBean.class);
    domainBeans.add(ReferredBean.class);
    domainBeans.add(ReferredDetailBean.class);
    descriptors = ApiBeanDescriptor.of(domainBeans);

    ApiObjectRef ref = new ApiObjectRef(null, beanToRef, descriptors);
    ObjectChange refChanges = ref.renderAndCleanChanges().get();

    // render changes from MapBasedObject

    MapBasedObjectData beanToObj = constructMasterBeanData();

    MapBasedObject obj = MapBasedObject.of(beanToObj);
    ObjectChange objChanges = obj.renderAndCleanChanges().get();

    // compare the changes from the two sources

    // refChanges contains the map itself
    assertEquals(refChanges.getProperties().size() - 1, objChanges.getProperties().size());
    assertEquals(refChanges.getReferences().size(), objChanges.getReferences().size());
    assertEquals(refChanges.getCollections().size(), objChanges.getCollections().size());
    assertEquals(refChanges.getReferencedObjects().size(),
        objChanges.getReferencedObjects().size());
    assertEquals(refChanges.getCollectionObjects().size(),
        objChanges.getCollectionObjects().size());

    // make new changes and render them

    ref.setValueByPath("name", "newName");
    ref.setValueByPath("referred/name", "newName");
    ref.setValueByPath("referred/details/0/name", "newName");

    // ReferredBean refBean = new ReferredBean();
    // refBean.setName("newRefName");
    // ref.setValueByPath("referred", refBean);

    // MasterDetailBean detBean = new MasterDetailBean();
    // detBean.setDetailName("newAddedDetail");
    // ref.setValueByPath("details/1", detBean);

    // ReferredDetailBean refDetBean = new ReferredDetailBean();
    // refDetBean.setName("newAddedDetail");
    // ref.setValueByPath("referred/details/1", refDetBean);

    // List<MasterDetailBean> detBeans = new ArrayList<>();
    // MasterDetailBean detBean = new MasterDetailBean();
    // detBean.setDetailName("detailName1");
    // MasterDetailBean detBean2 = new MasterDetailBean();
    // detBean2.setDetailName("detailName2");
    // MasterDetailBean detBean3 = new MasterDetailBean();
    // detBean3.setDetailName("detailName3");
    // MasterDetailBean detBean4 = new MasterDetailBean();
    // detBean4.setDetailName("detailName4");
    // ref.setValueByPath("details", Arrays.asList(detBean));
    // ref.setValueByPath("details", Arrays.asList(detBean, detBean2, detBean3));

    refChanges = ref.renderAndCleanChanges().get();

    obj.setValueByPath("name", "newName");
    obj.setValueByPath("referred/name", "newName");
    obj.setValueByPath("referred/details/0/name", "newName");

    // MapBasedObjectData refBeanData = new MapBasedObjectData();
    // MapBasedObjectUtil.addNewPropertyToData(refBeanData, "name", "newRefName");
    // obj.setValueByPath("referred", refBeanData);

    // MapBasedObjectData detData = new MapBasedObjectData();
    // MapBasedObjectUtil.addNewPropertyToData(detData, "detailName", "newAddedDetail");
    // obj.setValueByPath("details/1", detData);

    // MapBasedObjectData refDetData = new MapBasedObjectData();
    // MapBasedObjectUtil.addNewPropertyToData(refDetData, "name", "newAddedDetail");
    // obj.setValueByPath("referred/details/1", refDetData);

    // MapBasedObjectData detBeanData = new MapBasedObjectData();
    // MapBasedObjectUtil.addNewPropertyToData(detBeanData, "detailName", "detailName1");
    // MapBasedObjectData detBeanData2 = new MapBasedObjectData();
    // MapBasedObjectUtil.addNewPropertyToData(detBeanData2, "detailName", "detailName2");
    // MapBasedObjectData detBeanData3 = new MapBasedObjectData();
    // MapBasedObjectUtil.addNewPropertyToData(detBeanData3, "detailName", "detailName3");
    // MapBasedObjectData detBeanData4 = new MapBasedObjectData();
    // MapBasedObjectUtil.addNewPropertyToData(detBeanData4, "detailName", "detailName4");
    // obj.setValueByPath("details", Arrays.asList(detBeanData));
    // obj.setValueByPath("details",
    // Arrays.asList(detBeanData, detBeanData2, detBeanData3));

    objChanges = obj.renderAndCleanChanges().get();

    // compare the changes again from the two sources

    assertEquals(refChanges.getProperties().size(), objChanges.getProperties().size());
    assertEquals(refChanges.getReferences().size(), objChanges.getReferences().size());
    assertEquals(refChanges.getCollections().size(), objChanges.getCollections().size());
    assertEquals(refChanges.getReferencedObjects().size(),
        objChanges.getReferencedObjects().size());
    assertEquals(refChanges.getCollectionObjects().size(),
        objChanges.getCollectionObjects().size());
  }

  private enum TestEnum {
    VALUE1, VALUE2, VALUE3;
  }

  private MasterBean constructMasterBean() {
    MasterBean bean1 = new MasterBean();
    bean1.setCounter(1);
    bean1.setName("name");
    bean1.setValid(true);
    bean1.setStringList(Arrays.asList("first", "second"));
    {
      ReferredBean refBean = new ReferredBean();
      refBean.setName("refName");
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName1");
        refBean.addDetailsItem(detBean);
      }
      {
        ReferredDetailBean detBean = new ReferredDetailBean();
        detBean.setName("refDetailName2");
        refBean.addDetailsItem(detBean);
      }
      bean1.setReferred(refBean);
    }
    {
      MasterDetailBean detBean = new MasterDetailBean();
      detBean.setDetailName("detailName1");
      bean1.getDetails().add(detBean);
    }
    {
      MasterDetailBean detBean = new MasterDetailBean();
      detBean.setDetailName("detailName2");
      bean1.getDetails().add(detBean);
    }
    return bean1;
  }

  private MapBasedObjectData constructMasterBeanData() {
    MapBasedObjectData refDetailData1 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(refDetailData1, "name", "refDetailName1");

    MapBasedObjectData refDetailData2 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(refDetailData2, "name", "refDetailName2");

    MapBasedObjectData refData = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(refData, "name", "refName");
    MapBasedObjectUtil.addNewPropertyToData(refData, "details",
        Arrays.asList(refDetailData1, refDetailData2));

    MapBasedObjectData masterDetailData1 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(masterDetailData1, "detailName", "detailName1");

    MapBasedObjectData masterDetailData2 = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(masterDetailData2, "detailName", "detailName2");

    MapBasedObjectData masterBeanData = new MapBasedObjectData();
    MapBasedObjectUtil.addNewPropertyToData(masterBeanData, "counter", 1);
    MapBasedObjectUtil.addNewPropertyToData(masterBeanData, "name", "name");
    MapBasedObjectUtil.addNewPropertyToData(masterBeanData, "valid", true);
    MapBasedObjectUtil.addNewPropertyToData(masterBeanData, "stringList",
        Arrays.asList("first", "second"));
    MapBasedObjectUtil.addNewPropertyToData(masterBeanData, "referred", refData);
    MapBasedObjectUtil.addNewPropertyToData(masterBeanData, "details",
        Arrays.asList(masterDetailData1, masterDetailData2));

    return masterBeanData;
  }
}