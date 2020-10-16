package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.json.deserializer.DataRowDeserializer;
import org.smartbit4all.json.serializer.DataRowSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(using =  DataRowDeserializer.class)
@JsonSerialize(using =  DataRowSerializer.class)
public interface DataRowMixin<E extends EntityDefinition> {
}
