package org.smartbit4all.api.object;

import java.util.List;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.config.PlatformApiConfig;
import org.smartbit4all.api.object.bean.AggregationKind;
import org.smartbit4all.api.object.bean.ReferencePropertyKind;
import org.smartbit4all.api.org.OrgApi;
import org.smartbit4all.api.org.OrgApiStorageImpl;
import org.smartbit4all.api.org.SecurityOption;
import org.smartbit4all.api.org.SubjectContributionByGroup;
import org.smartbit4all.api.org.SubjectContributionByUser;
import org.smartbit4all.api.org.bean.Group;
import org.smartbit4all.api.org.bean.SubjectModel;
import org.smartbit4all.api.org.bean.SubjectTypeDescriptor;
import org.smartbit4all.api.org.bean.User;
import org.smartbit4all.api.sample.bean.SampleAttachement;
import org.smartbit4all.api.sample.bean.SampleCategory;
import org.smartbit4all.api.sample.bean.SampleCategoryType;
import org.smartbit4all.api.sample.bean.SampleContainerItem;
import org.smartbit4all.api.sample.bean.SampleDataSheet;
import org.smartbit4all.api.sample.bean.SampleInlineObject;
import org.smartbit4all.api.sample.bean.SampleLinkObject;
import org.smartbit4all.core.io.TestFSConfig;
import org.smartbit4all.core.io.TestFileUtil;
import org.smartbit4all.core.object.ObjectDefinition;
import org.smartbit4all.core.object.ObjectDefinitionApiImpl;
import org.smartbit4all.core.object.ObjectReferenceConfigs;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

@Configuration
@Import({PlatformApiConfig.class, TestFSConfig.class})
public class ObjectApiTestConfig {

  public static final String SAMPLE_SUBJECT_MODEL = "Sample";

  @Bean
  public ObjectDefinition<DomainObjectTestBean> masterBeanDef() {
    ObjectDefinition<DomainObjectTestBean> result =
        ObjectDefinitionApiImpl.constructDefinitionBase(DomainObjectTestBean.class);
    result.setExplicitUri(true);
    return result;
  }

  @EventListener(ContextRefreshedEvent.class)
  public void clearFS(ContextRefreshedEvent event) throws Exception {
    TestFileUtil.clearTestDirectory();
    System.out.println("Test FS cleared...");
  }

  @Bean
  OrgApi orgApi(StorageApi storageApi, List<SecurityOption> securityOptions) throws Exception {
    return new OrgApiStorageImpl(storageApi, securityOptions);
  }

  @Bean
  SampleSubjectContributionApi sampleSubjectContributionApi() {
    return new SampleSubjectContributionApi();
  }

  @Bean
  SubjectModel sampleSubjectModel() {
    return new SubjectModel().name(SAMPLE_SUBJECT_MODEL)
        .putDescriptorsItem(Group.class.getName(),
            new SubjectTypeDescriptor()
                .apiName(SubjectContributionByGroup.class.getName()).name(Group.class.getName()))
        .putDescriptorsItem(User.class.getName(),
            new SubjectTypeDescriptor()
                .apiName(SubjectContributionByUser.class.getName()).name(User.class.getName()))
        .putDescriptorsItem(SampleCategory.class.getName(),
            new SubjectTypeDescriptor()
                .apiName(SampleCategory.class.getName()).name(SampleCategory.class.getName()));
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
            SampleCategory.LINKS,
            SampleLinkObject.class,
            ReferencePropertyKind.LIST,
            AggregationKind.INLINE)
        .ref(SampleCategory.class,
            SampleCategory.SINGLE_LINK,
            SampleLinkObject.class,
            ReferencePropertyKind.REFERENCE,
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
        .ref(SampleInlineObject.class,
            SampleInlineObject.CATEGORY_TYPE,
            SampleCategoryType.class,
            ReferencePropertyKind.REFERENCE,
            AggregationKind.NONE)
        .ref(SampleAttachement.class,
            SampleAttachement.CONTENT,
            BinaryContent.class,
            ReferencePropertyKind.REFERENCE);
  }
}
