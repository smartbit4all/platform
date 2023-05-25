package org.smartbit4all.api.collection;

import java.net.URI;
import java.util.List;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.collection.SearchEntityDefinition.DetailDefinition;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleAttachement;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionClause;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import({PlatformApiConfig.class,
    TestFSConfig.class})
@EnableTransactionManagement
public class CollectionTestConfig {

  public static final String SHADOW_ITEMS = "shadowItems";

  @Bean(Storage.STORAGETX)
  public StorageTransactionManagerFS transactionManager(StorageFS storageFS) {
    return new StorageTransactionManagerFS(storageFS);
  }

  @Bean
  public SearchIndex<SampleDataSheet> sampleDatasheetIndex() {
    return new SearchIndexWithFilterBeanImpl<>(
        CollectionApiTest.SCHEMA,
        CollectionApiTest.MY_SEARCH,
        TestFilter.class, CollectionApiTest.SCHEMA, SampleDataSheet.class)
            .map(TestFilter.NAME, String.class, 150, SampleDataSheet.NAME)
            .map(TestFilter.URI, URI.class, 500, SampleDataSheet.URI)
            .mapProcessed(TestFilter.ISODD, Boolean.class, 1, n -> Boolean.valueOf("odd".equals(n)),
                SampleDataSheet.NAME)
            .mapComplex(TestFilter.CAPTION, String.class, 300,
                on -> {
                  return on.getValueAsString(SampleDataSheet.NAME)
                      + StringConstant.DOT
                      + on.getValueAsString(SampleDataSheet.NAME);
                })
            .expression(TestFilter.NAME,
                (name, nameProp) -> ((Property<String>) nameProp)
                    .like("%" + name.toString() + "%"))
            .expressionComplex(TestFilter.CAPTION,
                (caption, entityDef) -> {
                  ExpressionClause exp = Expression.createAndClause()
                      .add(((Property<String>) entityDef.getProperty(TestFilter.CAPTION))
                          .like("%" + caption.toString() + "%"));
                  String[] captionParts = caption.toString().split("\\" + StringConstant.DOT);
                  if (captionParts.length != 0) {
                    exp.add(((Property<String>) entityDef.getProperty(TestFilter.NAME))
                        .like("%" + captionParts[0] + "%"));
                  }
                  return exp;
                });
  }

  @Bean
  public SearchIndex<SampleCategory> sampleCategoryWithValueDetails() {
    SearchIndexImpl<SampleCategory> index =
        new SearchIndexWithFilterBeanImpl<>(
            CollectionApiTest.SCHEMA,
            CollectionApiTest.MY_SEARCHDETAILVALUES,
            TestCategoryFilter.class, CollectionApiTest.SCHEMA, SampleCategory.class)
                .map(TestCategoryFilter.URI, URI.class, 500, SampleCategory.URI)
                .map(TestCategoryFilter.NAME, String.class, 150, SampleCategory.NAME)
                .detailListOfValue(TestCategoryFilter.KEYWORDS, SampleCategory.URI, String.class,
                    120, SampleCategory.KEY_WORDS);
    index.expression(TestCategoryFilter.NAME, (o, p) -> ((Property<String>) p).like((String) o));
    index.expressionDetail(TestCategoryFilter.KEYWORDS,
        (obj, objectMapping) -> {
          SearchIndexMappingObject detailMapping =
              ((SearchIndexMappingObject) objectMapping.mappingsByPropertyName
                  .get(TestCategoryFilter.KEYWORDS));
          DetailDefinition detailDefinition =
              objectMapping.entityDefinition.detailsByName.get(TestCategoryFilter.KEYWORDS);
          List<Object> list = (List<Object>) obj;
          Expression existsExpression = null;
          if (list.size() > 1) {
            existsExpression = detailMapping.getDefinition().definition
                .getPropertyObject(SearchIndexMappingObject.VALUE_COLUMN).in(list);
          } else {
            existsExpression = detailMapping.getDefinition().definition
                .getPropertyObject(SearchIndexMappingObject.VALUE_COLUMN).like(list.get(0));
          }
          // Add the exists to the current entity and return the exists expression as is.
          return objectMapping.entityDefinition.definition
              .exists(detailDefinition.masterJoin, existsExpression)
              .name(SampleCategory.KEY_WORDS);
        });
    return index;
  }

  @Bean
  public SearchIndex<SampleCategory> sampleMasterDetailIndex() {
    SearchIndexImpl<SampleCategory> index = new SearchIndexImpl<>(
        CollectionApiTest.SCHEMA,
        CollectionApiTest.SAMPLE_CATEGORY, CollectionApiTest.SCHEMA, SampleCategory.class)
            .map(SampleCategory.NAME, SampleCategory.NAME)
            .map(SampleCategory.URI, SampleCategory.URI)
            .mapComplex(TestFilter.CAPTION,
                on -> {
                  return on.getValueAsString(SampleCategory.NAME)
                      + StringConstant.DOT
                      + on.getValueAsString(SampleCategory.NAME);
                })
            .detailListOfValue(SampleCategory.KEY_WORDS, SampleCategory.URI, String.class, 120,
                SampleCategory.KEY_WORDS);
    index.detail(SampleCategory.CONTAINER_ITEMS, SampleCategory.URI, SampleCategory.CONTAINER_ITEMS)
        .map(SampleContainerItem.NAME, String.class, 150,
            SampleContainerItem.NAME)
        .map(SampleContainerItem.DATASHEET, String.class, 150, SampleContainerItem.DATASHEET,
            SampleDataSheet.NAME);
    return index;
  }

  @Bean
  public ObjectReferenceConfigs refDefs() {
    return new ObjectReferenceConfigs()
        .ref(SampleCategory.class,
            SampleCategory.SUB_CATEGORIES,
            SampleCategory.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SampleCategory.CONTAINER_ITEMS,
            SampleContainerItem.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SHADOW_ITEMS,
            SampleContainerItem.class,
            ReferencePropertyKind.LIST)
        .ref(SampleCategory.class,
            SampleCategory.LINKS,
            SampleLinkObject.class,
            ReferencePropertyKind.LIST,
            AggregationKind.INLINE)
        .ref(SampleLinkObject.class,
            SampleLinkObject.CATEGORY,
            SampleCategory.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleLinkObject.class,
            SampleLinkObject.ITEM,
            SampleContainerItem.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.USER_URI,
            User.class,
            ReferencePropertyKind.REFERENCE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.ATTACHMENTS,
            SampleAttachement.class,
            ReferencePropertyKind.LIST,
            AggregationKind.COMPOSITE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.MAIN_DOCUMENT,
            SampleAttachement.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.COMPOSITE)
        .ref(SampleContainerItem.class,
            SampleContainerItem.DATASHEET,
            SampleDataSheet.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.COMPOSITE)
        .ref(SampleAttachement.class,
            SampleAttachement.CONTENT,
            BinaryContent.class,
            ReferencePropertyKind.REFERENCE);
  }

}
