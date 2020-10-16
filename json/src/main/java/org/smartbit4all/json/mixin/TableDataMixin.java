package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.json.deserializer.TableDataDeserializer;
import org.smartbit4all.json.serializer.TableDataSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using = TableDataDeserializer.class)
@JsonSerialize(using = TableDataSerializer.class)
public interface TableDataMixin<E extends EntityDefinition> {
}
