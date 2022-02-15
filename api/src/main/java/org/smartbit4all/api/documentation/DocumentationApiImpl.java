package org.smartbit4all.api.documentation;

import java.net.URI;
import org.smartbit4all.api.binarydata.BinaryDataObject;
import org.smartbit4all.api.documentation.bean.DocumentationData;
import org.smartbit4all.api.documentation.bean.ParagraphData;
import org.smartbit4all.api.documentation.bean.ParagraphKind;
import org.smartbit4all.domain.data.storage.Storage;
import org.smartbit4all.domain.data.storage.StorageApi;
import org.smartbit4all.domain.data.storage.StorageObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The {@link StorageApi} based implementation of the documentation management.
 * 
 * @author Peter Boros
 */
public class DocumentationApiImpl implements DocumentationApi {

  /**
   * Storage Api to access the stored objects.
   */
  @Autowired
  private StorageApi storageApi;

  @Autowired
  private DocumentationApi documentationApi;

  @Override
  public Documentation load(URI uri) {
    StorageObject<DocumentationData> oDocumentationData =
        storageApi.load(uri, DocumentationData.class);
    DocumentationData data = oDocumentationData.getObject();
    Documentation result = new Documentation(this, data);
    Integer lastId = 0;
    for (ParagraphData paragraphData : data.getParagraphs()) {
      Paragraph paragraph = new Paragraph(paragraphData);
      lastId = Math.max(lastId, paragraphData.getId());
      // The text is saved even if the kind is not RICHTEXT
      paragraph.setText(paragraphData.getText());
      if (paragraphData.getKind() == ParagraphKind.IMAGE) {
        StorageObject<BinaryDataObject> oBinaryDataObj =
            storageApi.load(paragraphData.getContentUri(), BinaryDataObject.class);
        paragraph.setImage(oBinaryDataObj.getObject());
      } else if (paragraphData.getKind() == ParagraphKind.MSWORD) {
        StorageObject<BinaryDataObject> oBinaryDataObj =
            storageApi.load(paragraphData.getContentUri(), BinaryDataObject.class);
        paragraph.setImage(oBinaryDataObj.getObject());
      } else if (paragraphData.getKind() == ParagraphKind.DOCUMENTATIONREF) {
        // We should load the referred documentation and the paragraphs.
        StorageObject<Documentation> oDocumentation =
            storageApi.load(paragraphData.getContentUri(), Documentation.class);
        paragraph.setDocumentation(oDocumentation.getObject());
      }
      result.paragraphList().add(paragraph);
    }
    result.setLastId(lastId);
    return result;
  }

  @Override
  public URI save(Storage storage, Documentation doc) {
    URI uri = doc.getUri();
    if (storage.exists(uri)) {
      storage.update(uri, DocumentationData.class, d -> doc.getData());
    } else {
      storage.saveAsNew(doc.getData());
    }
    return uri;
  }

  @Override
  public Documentation newInstance(URI containerUri, String containerPropertyName,
      Storage storage) {
    StorageObject<DocumentationData> instanceOf = storage.instanceOf(DocumentationData.class);
    instanceOf.setObject(new DocumentationData().containerObjectUri(containerUri)
        .containerPropertyName(containerPropertyName));
    return new Documentation(documentationApi, instanceOf.getObject());
  }

}
