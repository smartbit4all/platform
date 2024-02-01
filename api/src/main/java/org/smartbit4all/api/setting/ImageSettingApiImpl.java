package org.smartbit4all.api.setting;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.api.session.SessionApi;
import org.smartbit4all.api.view.bean.ImageResource;
import org.smartbit4all.api.view.bean.UiActionTooltip;
import org.smartbit4all.core.object.ObjectSerializerByObjectMapper;
import org.smartbit4all.core.utility.StringConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * The basic implementation for the api.
 * 
 * @author Peter Boros
 */
public class ImageSettingApiImpl implements ImageSettingApi {

  private static final Logger log = LoggerFactory.getLogger(ImageSettingApiImpl.class);

  @Autowired()
  @Qualifier("smartbit4all.imageresource")
  private MessageSource imageResource;

  @Autowired(required = false)
  private SessionApi sessionApi;

  @Autowired
  private ObjectSerializerByObjectMapper serializer;

  @Override
  public ImageResource get(String... keys) {
    return get(getLocale(), keys);
  }

  @Override
  public ImageResource get(Locale locale, String... keys) {
    if (keys == null || keys.length == 0) {
      return null;
    }
    String key = String.join(StringConstant.DOT, keys);
    ImageResource value = getInternal(locale, key);
    if (value != null && !value.equals(key)) {
      // found a match
      return value;
    }
    if (keys.length == 1) {
      return getDefaultImage(keys);
    }
    String[] subKeys = Arrays.copyOfRange(keys, 1, keys.length);
    return get(locale, subKeys);
  }

  private final ImageResource getInternal(Locale locale, String key) {
    String image = StringConstant.EMPTY;
    try {
      image = imageResource.getMessage(key, null, null, locale);
      if (image == null) {
        return null;
      }
      ImageResource imgResource = serializer.fromString(image, ImageResource.class);
      if (imgResource.getSource() == null) {
        imgResource.setSource(SOURCE_SMART_ICON);
      }
      return imgResource;
    } catch (NoSuchMessageException e) {
      return null;
    } catch (IOException e) {
      log.error("Unable to deserialize the ImageResource " + key + "=" + image, e);
      return null;
    }
  }

  private Locale getLocale() {
    Locale sessionLocale = null;
    if (sessionApi != null) {
      sessionLocale = sessionApi.getLocale();
    }
    return sessionLocale != null ? sessionLocale : Locales.HUNGARIAN;
  }

  private final ImageResource getDefaultImage(String... keys) {
    return new ImageResource().source(SOURCE_SMART_ICON).identifier("X")
        .tooltip(new UiActionTooltip().tooltip("No image defined for " + Arrays.toString(keys)));
  }

}
