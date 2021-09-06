package org.smartbit4all.api.contentaccess; 

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {ContentAccessTestConfig.class})
public class ContentAccessTest {

	@Autowired
	ContentAccessApi contentAccessApi;
	
	@BeforeEach
	void clearContentAccessRootFolder() {
		/*
		 * File root = ContentAccessTestConfig.getBinaryDataApiRootFolder(); for (String
		 * innerFilePath : root.list()) { File innerFile = new File(innerFilePath); if
		 * (innerFile.isDirectory()) innerFile.delete();
		 */
		//}
	}
	
	@Test
	void  shareTest() throws Exception {
		UUID uuidWithNotAddedUri = contentAccessApi.share();
		assertNotNull(uuidWithNotAddedUri);
		
		BinaryContent content = new BinaryContent();
		content.setUri(new URI("asd"));
		UUID uuidWithAddedUri = contentAccessApi.share(content);
		assertNotNull(uuidWithAddedUri);
	}
	
	@Test
	void downloadTest() throws Exception {
		BinaryContent content = new BinaryContent();
		content.setDataUri(new URI(ContentAccessApi.SCHEME , null, "/lorem-ipsum.pdf", null));
		UUID uuid = contentAccessApi.share(content);
		BinaryData data = contentAccessApi.download(uuid);
		assertNotNull(data);
	}
	
	@Test
	void uploadTest() throws Exception {
		BinaryContent content = new BinaryContent();
		content.setFileName("asd.pdf");
		content.setDataUri(new URI(ContentAccessApi.SCHEME, null, "/upload/asd.pdf", null));
		UUID uuid = contentAccessApi.share(content);
		
		contentAccessApi.upload(uuid, BinaryData.of(new FileInputStream("./src/test/resources/lorem-ipsum.pdf")));
		assertThrows(NoSuchElementException.class, () -> contentAccessApi.download(uuid));
	}
}
