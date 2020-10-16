package org.smartbit4all.json.mixin;

import org.smartbit4all.json.deserializer.JDBCDataConverterDeserializer;
import org.smartbit4all.json.serializer.JDBCDataConverterSerializer;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = JDBCDataConverterDeserializer.class)
@JsonSerialize(using = JDBCDataConverterSerializer.class)
public interface JDBCDataConverterMixin {
}
