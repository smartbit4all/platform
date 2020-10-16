package org.smartbit4all.json.config;

import java.util.HashMap;
import java.util.Map;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.TableData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.Expression2Operand;
import org.smartbit4all.domain.meta.ExpressionBetween;
import org.smartbit4all.domain.meta.ExpressionBoolean;
import org.smartbit4all.domain.meta.ExpressionBracket;
import org.smartbit4all.domain.meta.ExpressionClause;
import org.smartbit4all.domain.meta.ExpressionIn;
import org.smartbit4all.domain.meta.ExpressionIsNull;
import org.smartbit4all.domain.meta.JDBCDataConverter;
import org.smartbit4all.domain.meta.Operand;
import org.smartbit4all.domain.meta.OperandLiteral;
import org.smartbit4all.domain.meta.OperandProperty;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.domain.meta.PropertyComputed;
import org.smartbit4all.domain.meta.PropertyOwned;
import org.smartbit4all.domain.meta.PropertyRef;
import org.smartbit4all.domain.meta.SortOrderProperty;
import org.smartbit4all.domain.service.query.QueryInput;
import org.smartbit4all.domain.service.query.QueryOutput;
import org.smartbit4all.json.deserializer.CollectionTypeDeserializer;
import org.smartbit4all.json.mixin.DataRowMixin;
import org.smartbit4all.json.mixin.EntityDefinitionMixin;
import org.smartbit4all.json.mixin.Expression2OperandMixin;
import org.smartbit4all.json.mixin.ExpressionBetweenMixin;
import org.smartbit4all.json.mixin.ExpressionBooleanMixin;
import org.smartbit4all.json.mixin.ExpressionBracketMixin;
import org.smartbit4all.json.mixin.ExpressionClauseMixin;
import org.smartbit4all.json.mixin.ExpressionInMixin;
import org.smartbit4all.json.mixin.ExpressionIsNullMixin;
import org.smartbit4all.json.mixin.ExpressionMixin;
import org.smartbit4all.json.mixin.JDBCDataConverterMixin;
import org.smartbit4all.json.mixin.OperandLiteralMixin;
import org.smartbit4all.json.mixin.OperandMixin;
import org.smartbit4all.json.mixin.OperandPropertyMixin;
import org.smartbit4all.json.mixin.PropertyComputedMixin;
import org.smartbit4all.json.mixin.PropertyMixin;
import org.smartbit4all.json.mixin.PropertyOwnedMixin;
import org.smartbit4all.json.mixin.PropertyRefMixin;
import org.smartbit4all.json.mixin.QueryInputMixin;
import org.smartbit4all.json.mixin.QueryOutputMixin;
import org.smartbit4all.json.mixin.SortOrderPropertyMixin;
import org.smartbit4all.json.mixin.TableDataMixin;
import org.smartbit4all.json.serializer.CollectionTypeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;


@Configuration
public class JsonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer addMixin() {
    return new Jackson2ObjectMapperBuilderCustomizer() {

      @Override
      public void customize(Jackson2ObjectMapperBuilder jacksonObjectMapperBuilder) {
        // Registration of the Smartbit4all classes that we want to customize the serialization.

        Map<Class<?>, Class<?>> mixIns = new HashMap<>();

        // Query Input
        mixIns.put(QueryInput.class, QueryInputMixin.class);

        // Query Output
        mixIns.put(QueryOutput.class, QueryOutputMixin.class);

        // TableData
        mixIns.put(TableData.class, TableDataMixin.class);

        // DataRow
        mixIns.put(DataRow.class, DataRowMixin.class);

        // Property
        mixIns.put(Property.class, PropertyMixin.class);
        mixIns.put(PropertyOwned.class, PropertyOwnedMixin.class);
        mixIns.put(PropertyRef.class, PropertyRefMixin.class);
        mixIns.put(PropertyComputed.class, PropertyComputedMixin.class);

        mixIns.put(SortOrderProperty.class, SortOrderPropertyMixin.class);
        mixIns.put(EntityDefinition.class, EntityDefinitionMixin.class);

        // Expression
        mixIns.put(Expression.class, ExpressionMixin.class);
        mixIns.put(Expression2Operand.class, Expression2OperandMixin.class);
        mixIns.put(ExpressionBetween.class, ExpressionBetweenMixin.class);
        mixIns.put(ExpressionBoolean.class, ExpressionBooleanMixin.class);
        mixIns.put(ExpressionBracket.class, ExpressionBracketMixin.class);
        mixIns.put(ExpressionClause.class, ExpressionClauseMixin.class);
        mixIns.put(ExpressionIn.class, ExpressionInMixin.class);
        mixIns.put(ExpressionIsNull.class, ExpressionIsNullMixin.class);

        // Operand
        mixIns.put(Operand.class, OperandMixin.class);
        mixIns.put(OperandProperty.class, OperandPropertyMixin.class);
        mixIns.put(OperandLiteral.class, OperandLiteralMixin.class);

        mixIns.put(JDBCDataConverter.class, JDBCDataConverterMixin.class);

        jacksonObjectMapperBuilder.mixIns(mixIns);

      }
    };
  }

  /*
   * Module registration to handle custom collection serializations
   */
  @Bean
  public Module collectionTypeSerializerModule() {
    return new Module() {

      @Override
      public String getModuleName() {
        return "CollectionTypeSerializerModule";
      }

      @Override
      public Version version() {
        return new Version(1, 0, 0, "SNAPSHOT", null, null);
      }

      @Override
      public void setupModule(SetupContext context) {
        context.addSerializers(new CollectionTypeSerializer());
        context.addDeserializers(new CollectionTypeDeserializer());
      }

    };
  }

}


