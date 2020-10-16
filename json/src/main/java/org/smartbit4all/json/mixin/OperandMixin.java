package org.smartbit4all.json.mixin;

import org.smartbit4all.domain.meta.OperandLiteral;
import org.smartbit4all.domain.meta.OperandProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
@JsonSubTypes.Type(value = OperandLiteral.class, name = "OperandLiteral"),
@JsonSubTypes.Type(value = OperandProperty.class, name = "OperandProperty")
})
public interface OperandMixin <T>{
}
