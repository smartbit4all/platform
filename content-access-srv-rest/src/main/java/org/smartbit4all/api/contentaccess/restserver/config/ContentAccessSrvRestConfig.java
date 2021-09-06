package org.smartbit4all.api.contentaccess.restserver.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryContentApi;
import org.smartbit4all.api.binarydata.BinaryContentApiImpl;
import org.smartbit4all.api.binarydata.BinaryDataApi;
import org.smartbit4all.api.binarydata.BinaryDataApiPrimary;
import org.smartbit4all.api.binarydata.fs.BinaryDataApiFS;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.contentaccess.ContentAccessApiImpl;
import org.smartbit4all.api.contentaccess.restserver.ContentAccessApiDelegate;
import org.smartbit4all.api.contentaccess.restserver.impl.ContentAccessApiDelegateImpl;
import org.smartbit4all.api.objectshare.ObjectShareApi;
import org.smartbit4all.api.objectshare.ObjectShareApiInMemoryImpl;
import org.smartbit4all.domain.data.storage.ObjectStorage;
import org.smartbit4all.domain.data.storage.ObjectStorageInMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ContentAccessSrvRestConfig {
		
	@Bean
	ContentAccessApiDelegate contentAccessApiDelegate() {
		return new ContentAccessApiDelegateImpl();
	}
	
	@Bean
	ContentAccessApi contentAccessApi() {
		return new ContentAccessApiImpl();
	}
	
	@Bean
	ObjectShareApi objectShareApi() {
		return new ObjectShareApiInMemoryImpl();
	}
	
	@Bean
	BinaryContentApi binaryContentApi(BinaryDataApi binaryDataApi) {
		return new BinaryContentApiImpl(binaryDataApi);
	}
	
	@Bean
	@Primary
	BinaryDataApi binaryDataApiPrimary() {
		return new BinaryDataApiPrimary();
	}
	
	@Bean
	List<BinaryDataApi> binaryDataApi() {
		return Arrays.asList(new BinaryDataApiFS(ContentAccessApi.SCHEME, getBinaryDataApiRootFolder()));
	}
	
	
	@Bean
	ObjectStorage<BinaryContent> objectStorage(){
		return new ObjectStorageInMemory<BinaryContent>(
				binaryContent -> binaryContent.getUri(), null);
	}

	protected File getBinaryDataApiRootFolder() {
		File file =  new File("./src/main/resources/contentAccessData");
		if (!file.exists()) {
			try {
				Files.createDirectory(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return file;
	}
	
}
