package org.smartbit4all.api.collection;

import java.net.URI;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.collection.SearchEntityDefinition.DetailDefinition;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleAttachement;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCompany;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleDepartment;
import org.smartbit4all.api.sample.bean.SampleEmployee;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.core.utility.StringConstant;
import org.smartbit4all.domain.data.DataRow;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.meta.Expression;
import org.smartbit4all.domain.meta.ExpressionClause;
import org.smartbit4all.domain.meta.Property;
import org.smartbit4all.storage.fs.StorageFS;
import org.smartbit4all.storage.fs.StorageTransactionManagerFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
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
            .mapProcessed(TestFilter.PROCESSED, String.class, 1, n -> "",
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
                (caption, entityDef, searchIndexMappingObject) -> {
                  ExpressionClause exp = Expression.createAndClause()
                      .add(((Property<String>) entityDef.getProperty(TestFilter.CAPTION))
                          .like("%" + caption.toString() + "%"));
                  String[] captionParts = caption.toString().split("\\" + StringConstant.DOT);
                  if (captionParts.length != 0) {
                    exp.add(((Property<String>) entityDef.getProperty(TestFilter.NAME))
                        .like("%" + captionParts[0] + "%"));
                  }
                  return exp;
                })
            .postProcess((tableData, searchIndex) -> {

              Property processed = searchIndex.getDefinition().getDefinition()
                  .getProperty(TestFilter.PROCESSED);
              Property name = searchIndex.getDefinition().getDefinition()
                  .getProperty(TestFilter.NAME);
              for (DataRow row : tableData.rows()) {
                Object n = row.get(name);
                row.set(processed, n + "-" + n);
              }
              return tableData;
            })
            .preProcessFilters((filters, searchIndex) -> {
              // Java 8 is incapable to infer types of lambda parameters even if explicit type
              // notation is used, we must explicitly cast to let the compiler pass, or upgrade to
              // JDK 9+!
              for (FilterExpressionData expression : ((FilterExpressionList) filters)
                  .getExpressions()) {
                if (TestFilter.NAME.equals(expression.getOperand1().getValueAsString())
                    && "process".equals(expression.getOperand2().getValueAsString())) {
                  expression.getOperand2().setValueAsString("odd");
                }
              }

              return filters;
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

  private Comparator<Object> categoryNameComparator() {
    return (Comparator<Object>) (local, other) -> {
      if (local == null && other == null) {
        return 0;
      } else if (local == null) {
        return -1;
      } else if (other == null) {
        return 1;
      } else {
        Integer localNumber = Integer.parseInt(local.toString().split("\\s+")[1]);
        Integer otherNumber = Integer.parseInt(other.toString().split("\\s+")[1]);
        return localNumber.compareTo(otherNumber);
      }
    };
  }

  @Bean
  public SearchIndex<SampleCategory> sampleMasterDetailIndex() {
    SearchIndexImpl<SampleCategory> index = new SearchIndexImpl<>(
        CollectionApiTest.SCHEMA,
        CollectionApiTest.SAMPLE_CATEGORY, CollectionApiTest.SCHEMA, SampleCategory.class)
            .map(SampleCategory.NAME, String.class, categoryNameComparator(), SampleCategory.NAME)
            .map(SampleCategory.URI, SampleCategory.URI)
            .detailListOfValue(TestCategoryFilter.KEYWORDS, SampleCategory.URI, String.class,
                120, SampleCategory.KEY_WORDS)
            .mapComplex(TestFilter.CAPTION,
                on -> {
                  return on.getValueAsString(SampleCategory.NAME)
                      + StringConstant.DOT
                      + on.getValueAsString(SampleCategory.NAME);
                })
            .detailListOfValue(SampleCategory.KEY_WORDS, SampleCategory.URI, String.class, 120,
                SampleCategory.KEY_WORDS);
    index.detail(SampleCategory.CONTAINER_ITEMS, SampleCategory.URI, SampleCategory.CONTAINER_ITEMS)
        // .MAP(SAMPLECONTAINERITEM.NAME, STRING.CLASS, 150,
        // SAMPLECONTAINERITEM.NAME)
        .map(SampleContainerItem.DATASHEET, String.class, 150, SampleContainerItem.DATASHEET,
            SampleDataSheet.NAME);
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
  public SearchIndex<SampleEmployee> sampleEmployee() {
    SearchIndexImpl<SampleEmployee> index = new SearchIndexImpl<>(
        CollectionApiTest.SCHEMA,
        CollectionApiTest.SAMPLE_EMPLOYEE, CollectionApiTest.SCHEMA, SampleEmployee.class)
            .map(SampleEmployee.NAME, String.class, SampleEmployee.NAME)
            .map(SampleEmployee.ID, String.class, SampleEmployee.ID)
            .map(SampleEmployee.DEPARTMENT, URI.class, SampleEmployee.DEPARTMENT)
            .map(SampleEmployee.URI, SampleEmployee.URI);
    index.reference(CollectionApiTest.SAMPLE_DEPARTMENT_REF,
        CollectionApiTest.SCHEMA,
        CollectionApiTest.SAMPLE_DEPARTMENT,
        SampleEmployee.DEPARTMENT,
        SampleDepartment.URI);

    return index;
  }

  @Bean
  public SearchIndex<SampleDepartment> sampleDepartment() {
    SearchIndexImpl<SampleDepartment> index = new SearchIndexImpl<>(
        CollectionApiTest.SCHEMA,
        CollectionApiTest.SAMPLE_DEPARTMENT, CollectionApiTest.SCHEMA, SampleDepartment.class)
            .map(SampleDepartment.NAME, String.class, SampleDepartment.NAME)
            .map(SampleDepartment.ID, String.class, SampleDepartment.ID)
            .map(SampleDepartment.COMPANY, URI.class, SampleDepartment.COMPANY)
            .map(SampleDepartment.URI, SampleDepartment.URI);
    index.reference(CollectionApiTest.SAMPLE_COMPANY_REF,
        CollectionApiTest.SCHEMA,
        CollectionApiTest.SAMPLE_COMPANY,
        SampleDepartment.COMPANY,
        SampleCompany.URI);
    return index;
  }

  @Bean
  public SearchIndex<SampleCompany> sampleCompany() {
    SearchIndexImpl<SampleCompany> index = new SearchIndexImpl<>(
        CollectionApiTest.SCHEMA,
        CollectionApiTest.SAMPLE_COMPANY, CollectionApiTest.SCHEMA, SampleCompany.class)
            .map(SampleCompany.NAME, String.class, SampleCompany.NAME)
            .map(SampleCompany.ID, String.class, SampleCompany.ID)
            .map(SampleCompany.URI, SampleCompany.URI);
    return index;
  }

  @Bean
  @Primary
  public DefaultComparatorProvider defaultComparatorProvider() {
    return new DefaultComparatorProvider() {

      @Override
      public Map<String, Comparator<Object>> getComparators() {
        Map<String, Comparator<Object>> map = new HashMap<>();
        map.put(List.class.getName(), (Comparator<Object>) (local, other) -> {
          if (local == null && other == null) {
            return 0;
          } else if (local == null) {
            return -1;
          } else if (other == null) {
            return 1;
          } else {
            return Collator.getInstance().compare(local.toString(), other.toString());
          }
        });
        return map;
      }
    };
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
