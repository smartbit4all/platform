package org.smartbit4all.api.documentation;

import java.net.URI;
import org.smartbit4all.api.documentation.bean.DocumentationData;

/**
 * The {@link Documentation} is a building block for the platform based applications. This special
 * data type can manage the complex hyper linked documentations for the domain objects. To use this
 * you must add the type mapping to the build.gradle of the given domain like this:
 *
 * <pre>
 * smartbit4all {
 *  openApi {
 *   apiDescriptorPath = "$rootDir/domain/src/main/resources/document/descriptor/"
 *   modelPackage = "org.smartbit4all.module.document.domain.model"
 *   dateTimeMapping = "java.time.LocaleDateTime"
 *   importMappings = [
 *       "Value": "org.smartbit4all.api.value.bean.Value",
 *       "BinaryData": "org.smartbit4all.api.binarydata.BinaryData",
 *       "BinaryContent": "org.smartbit4all.api.binarydata.BinaryContent",
 *       <b>"Documentation": "org.smartbit4all.api.documentation.bean.DocumentationData"</b>
 *     ]
 *  }
 * }
 * </pre>
 * 
 * @author Peter Boros
 */
public interface DocumentationApi {

  /**
   * Load the {@link Documentation} by the URI of the {@link DocumentationData#URI}. Recurse the
   * reference hierarchy and load every related data. We can adjust the whole reference tree and
   * every documentation in one editing session.
   * 
   * @param uri An existing URI.
   * @return
   */
  Documentation load(URI uri);

  /**
   * Save as new or update the given {@link Documentation}.
   * 
   * @param doc The {@link Documentation} object. If it already has uri then it's an update else
   *        it's a new instance.
   * @return
   */
  URI save(Documentation doc);

}
