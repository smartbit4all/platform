/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.localization;

import java.util.List;
import java.util.Locale;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.VaadinService;

public class TranslationUtil {

  private static TranslationUtil instance;
  
  private static PossibleTranslationProvider possibleTranslationProvider = new PossibleTranslationProvider(); 
  
  private TranslationUtil() {
  }
  
  public static TranslationUtil INSTANCE() {
    if(instance == null) {
      instance = new TranslationUtil(); 
    }
    return instance;
  }
  
  public String getTranslation(String key, Object... params) {
      return getTranslation(key, getLocale(), params);
  }

  public String getTranslation(String key, Locale locale, Object... params) {
      if (getI18NProvider() == null) {
          return "!{" + key + "}!";
      }
      return getI18NProvider().getTranslation(key, locale, params);
  }
  
  public String getPossibleTranslation(String key, Object... params) {
    return possibleTranslationProvider.getTranslation(key, getLocale(), params);
  }

  private I18NProvider getI18NProvider() {
      return VaadinService.getCurrent().getInstantiator().getI18NProvider();
  }

  protected Locale getLocale() {
      UI currentUi = UI.getCurrent();
      Locale locale = currentUi == null ? null : currentUi.getLocale();
      if (locale == null) {
          List<Locale> locales = getI18NProvider().getProvidedLocales();
          if (locales != null && !locales.isEmpty()) {
              locale = locales.get(0);
          } else {
              locale = Locale.getDefault();
          }
      }
      return locale;
  }
}
