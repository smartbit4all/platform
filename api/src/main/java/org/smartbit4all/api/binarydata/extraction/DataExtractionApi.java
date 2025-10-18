package org.smartbit4all.api.binarydata.extraction;

import java.net.URI;
import org.smartbit4all.api.attachment.bean.DataExtractionDescriptor;

public interface DataExtractionApi {

  boolean hasDataExtractor(URI objectUri);

  void setDataExtractor(URI objectUri, DataExtractionDescriptor descriptor);

  void extractData(URI objectUri);

}
