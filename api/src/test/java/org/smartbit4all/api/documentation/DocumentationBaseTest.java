package org.smartbit4all.api.documentation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.ListIterator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.documentation.bean.ParagraphKind;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
    DocumentationTestConfig.class
})
class DocumentationBaseTest {

  private Storage storage;

  @Autowired
  private DocumentationApi documentationApi;

  @Autowired
  private StorageApi storageApi;

  @BeforeEach
  void setUpBefore() throws Exception {
    storage = storageApi.get("test");
  }

  @AfterAll
  static void tearDownAfterClass() throws Exception {}

  @Test
  void test() throws FileNotFoundException {
    // We have a bean with a documentationUri (it can have more than one URI for documentation!)
    TestDocumentedBean bean = new TestDocumentedBean();
    bean.setSimpleText("apple");
    URI uri = storage.saveAsNew(bean);
    StorageObject<TestDocumentedBean> storageObject = storage.load(uri, TestDocumentedBean.class);
    Assertions.assertEquals(bean.getSimpleText(), storageObject.getObject().getSimpleText());

    // Now we construct a documentation. Until we save this it's only an in memory structure!
    // But we already know the URI!
    Documentation documentation = documentationApi.newInstance(uri, "documentUri", storage);
    {
      // Add text paragraph
      Paragraph paragraph = documentation.addParagraph(ParagraphKind.RICHTEXT);
      paragraph.setText("apple");
    }
    {
      // Add image paragraph
      Paragraph paragraph = documentation.addParagraph(ParagraphKind.IMAGE);
      BinaryDataObject image =
          BinaryData.of(new FileInputStream("src/test/resources/logo.jpg")).asObject();
      URI imageUri = storage.saveAsNew(image);
      paragraph.setImage(image);
    }
    {
      Paragraph paragraph = documentation.addParagraph(ParagraphKind.RICHTEXT);
      paragraph.setText("peach");
    }

    // Save the documentation and update it into the bean
    storage.update(uri, TestDocumentedBean.class, t -> {
      t.setDocumentUri(storage.saveAsNew(documentation.getData()));
      return t;
    });

    // Read the bean and the documentation
    TestDocumentedBean beanRead = storage.read(uri, TestDocumentedBean.class);
    Documentation docRead = documentationApi.load(beanRead.getDocumentUri());

    // Asserts
    Assertions.assertEquals(bean.getDocumentUri(), beanRead.getDocumentUri());

    Assertions.assertEquals(documentation.getParagraphs().size(), docRead.getParagraphs().size());
    ListIterator<Paragraph> iterParagraph = docRead.getParagraphs().listIterator();
    for (Paragraph paragraph : documentation.getParagraphs()) {
      Paragraph paragraphRead = iterParagraph.next();
      Assertions.assertEquals(paragraph.getData().toString(), paragraphRead.getData().toString());
    }

  }

}
