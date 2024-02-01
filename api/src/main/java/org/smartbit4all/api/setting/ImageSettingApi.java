package org.smartbit4all.api.setting;

import java.util.Locale;
import org.smartbit4all.api.view.bean.ImageResource;

/**
 * This api is responsible for retrieving the relevant {@link ImageResource} for business
 * identifiers like code and so. The ImageResources can be injected...
 * 
 * @author Peter Boros
 */
public interface ImageSettingApi {

  /**
   * Source icon constant.
   */
  static final String SOURCE_SMART_ICON = "smart-icon";

  /**
   * The key can be a hiararchical path of codes where the image api tries to find the longest path
   * and if it is missing then remove the first context key to search again. So if we have a code in
   * a object and we try to find the relevant icon with the object.code key. If it is set then we
   * return this image. If it is missing then we try to find the code itself.
   * 
   * @param keys
   * @return
   */
  ImageResource get(String... keys);

  /**
   * The key can be a hiararchical path of codes where the image api tries to find the longest path
   * and if it is missing then remove the first context key to search again. So if we have a code in
   * a object and we try to find the relevant icon with the object.code key. If it is set then we
   * return this image. If it is missing then we try to find the code itself.
   *
   * @param locale The local parameter to get the language specific image if it exists.
   * @param keys
   * @return
   */
  ImageResource get(Locale locale, String... keys);

}
