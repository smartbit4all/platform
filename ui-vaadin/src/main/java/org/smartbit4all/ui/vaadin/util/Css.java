/*******************************************************************************
 * Copyright (C) 2020 - 2020 it4all Hungary Kft.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.smartbit4all.ui.vaadin.util;

import java.util.HashMap;
import java.util.Map;
import com.vaadin.flow.component.Component;

/**
 * <p>
 * Utility methods and constants for handling CSS in Java code. Main focus is on using LUMO
 * variables.
 * <p>
 * Sources:
 * <li>https://vaadin.com/docs/v14/themes/lumo/lumo-overview.html
 * <li>https://cdn.vaadin.com/vaadin-lumo-styles/1.0.0/demo/
 * 
 * @author Attila Mate
 *
 */
public class Css {

  public static final String IMG_PATH = "images/";

  public class AlignSelf {
    public static final String BASELINE = "baseline";
    public static final String CENTER = "center";
    public static final String END = "end";
    public static final String START = "start";
    public static final String STRETCH = "stretch";
    public static final String FLEX_END = "flex-end";
  }

  public class BorderRadius {
    public static final String S = "var(--lumo-border-radius-s)";
    public static final String M = "var(--lumo-border-radius-m)";
    public static final String L = "var(--lumo-border-radius-l)";
  }

  public class Color {
    public static final String BASE_COLOR = "var(--lumo-base-color)";

    public class Primary {
      public static final String _10 = "var(--lumo-primary-color-10pct)";
      public static final String _50 = "var(--lumo-primary-color-50pct)";
      public static final String _100 = "var(--lumo-primary-color)";
    }

    public class Error {
      public static final String _10 = "var(--lumo-error-color-10pct)";
      public static final String _50 = "var(--lumo-error-color-50pct)";
      public static final String _100 = "var(--lumo-error-color)";
    }

    public class Success {
      public static final String _10 = "var(--lumo-success-color-10pct)";
      public static final String _50 = "var(--lumo-success-color-50pct)";
      public static final String _100 = "var(--lumo-success-color)";
    }

    public class Tint {
      public static final String _5 = "var(--lumo-tint-5pct)";
      public static final String _10 = "var(--lumo-tint-10pct)";
      public static final String _20 = "var(--lumo-tint-20pct)";
      public static final String _30 = "var(--lumo-tint-30pct)";
      public static final String _40 = "var(--lumo-tint-40pct)";
      public static final String _50 = "var(--lumo-tint-50pct)";
      public static final String _60 = "var(--lumo-tint-60pct)";
      public static final String _70 = "var(--lumo-tint-70pct)";
      public static final String _80 = "var(--lumo-tint-80pct)";
      public static final String _90 = "var(--lumo-tint-90pct)";
      public static final String _100 = "var(--lumo-tint)";
    }

    public class Shade {
      public static final String _5 = "var(--lumo-shade-5pct)";
      public static final String _10 = "var(--lumo-shade-10pct)";
      public static final String _20 = "var(--lumo-shade-20pct)";
      public static final String _30 = "var(--lumo-shade-30pct)";
      public static final String _40 = "var(--lumo-shade-40pct)";
      public static final String _50 = "var(--lumo-shade-50pct)";
      public static final String _60 = "var(--lumo-shade-60pct)";
      public static final String _70 = "var(--lumo-shade-70pct)";
      public static final String _80 = "var(--lumo-shade-80pct)";
      public static final String _90 = "var(--lumo-shade-90pct)";
      public static final String _100 = "var(--lumo-shade)";
    }

    public class Contrast {
      public static final String _5 = "var(--lumo-contrast-5pct)";
      public static final String _10 = "var(--lumo-contrast-10pct)";
      public static final String _20 = "var(--lumo-contrast-20pct)";
      public static final String _30 = "var(--lumo-contrast-30pct)";
      public static final String _40 = "var(--lumo-contrast-40pct)";
      public static final String _50 = "var(--lumo-contrast-50pct)";
      public static final String _60 = "var(--lumo-contrast-60pct)";
      public static final String _70 = "var(--lumo-contrast-70pct)";
      public static final String _80 = "var(--lumo-contrast-80pct)";
      public static final String _90 = "var(--lumo-contrast-90pct)";
      public static final String _100 = "var(--lumo-contrast)";
    }
  }

  public class Display {
    public static final String BLOCK = "block";
    public static final String INLINE = "inline";
    public static final String FLEX = "flex";
    public static final String INLINE_FLEX = "inline-flex";
  }

  public class FontSize {
    public static final String XXS = "var(--lumo-font-size-xxs)";
    public static final String XS = "var(--lumo-font-size-xs)";
    public static final String S = "var(--lumo-font-size-s)";
    public static final String M = "var(--lumo-font-size-m)";
    public static final String L = "var(--lumo-font-size-l)";
    public static final String XL = "var(--lumo-font-size-xl)";
    public static final String XXL = "var(--lumo-font-size-xxl)";
    public static final String XXXL = "var(--lumo-font-size-xxxl)";
  }

  public class FontWeight {
    public static final String LIGHTER = "lighter";
    public static final String NORMAL = "normal";
    public static final String BOLD = "bold";
    public static final String BOLDER = "bolder";
    public static final String _100 = "100";
    public static final String _200 = "200";
    public static final String _300 = "300";
    public static final String _400 = "400";
    public static final String _500 = "500";
    public static final String _600 = "600";
    public static final String _700 = "700";
    public static final String _800 = "800";
    public static final String _900 = "900";
  }

  public class Heading {
    public static final String H1 = "h1";
    public static final String H2 = "h2";
    public static final String H3 = "h3";
    public static final String H4 = "h4";
    public static final String H5 = "h5";
    public static final String H6 = "h6";
  }

  public class IconSize {
    public static final String S = "var(--lumo-icon-size-s)";
    public static final String M = "var(--lumo-icon-size-m)";
    public static final String L = "var(--lumo-icon-size-l)";
  }

  public class Overflow {
    public static final String AUTO = "auto";
    public static final String HIDDEN = "hidden";
    public static final String SCROLL = "scroll";
    public static final String VISIBLE = "visible";
  }

  public class Shadow {
    public static final String XS = "var(--lumo-box-shadow-xs)";
    public static final String S = "var(--lumo-box-shadow-s)";
    public static final String M = "var(--lumo-box-shadow-m)";
    public static final String L = "var(--lumo-box-shadow-l)";
    public static final String XL = "var(--lumo-box-shadow-xl)";
  }

  public class Size {
    public static final String XS = "var(--lumo-size-xs)";
    public static final String S = "var(--lumo-size-s)";
    public static final String M = "var(--lumo-size-m)";
    public static final String L = "var(--lumo-size-l)";
    public static final String XL = "var(--lumo-size-xl)";
  }

  public enum SizeType {
    ALL, TOP, BOTTOM, LEFT, RIGHT, HORIZONTAL, VERTICAL
  }

  public class Space {
    public class Uniform {
      public static final String XS = "var(--lumo-space-xs)";
      public static final String S = "var(--lumo-space-s)";
      public static final String M = "var(--lumo-space-m)";
      public static final String L = "var(--lumo-space-l)";
      public static final String XL = "var(--lumo-space-xl)";
      public static final String RESP_X = "var(--lumo-space-resp-x)";
      public static final String RESP_M = "var(--lumo-space-resp-m)";
      public static final String RESP_L = "var(--lumo-space-resp-l)";
    }

    public class Wide {
      public static final String XS = "var(--lumo-space-wide-xs)";
      public static final String S = "var(--lumo-space-wide-s)";
      public static final String M = "var(--lumo-space-wide-m)";
      public static final String L = "var(--lumo-space-wide-l)";
      public static final String XL = "var(--lumo-space-wide-xl)";
      public static final String RESP_M = "var(--lumo-space-wide-resp-m)";
      public static final String RESP_L = "var(--lumo-space-wide-resp-l)";
    }

    public class Tall {
      public static final String XS = "var(--lumo-space-tall-xs)";
      public static final String S = "var(--lumo-space-tall-s)";
      public static final String M = "var(--lumo-space-tall-m)";
      public static final String L = "var(--lumo-space-tall-l)";
      public static final String XL = "var(--lumo-space-tall-xl)";
    }

  }

  public class TextColor {
    public static final String HEADER = "var(--lumo-header-text-color)";
    public static final String BODY = "var(--lumo-body-text-color)";
    public static final String SECONDARY = "var(--lumo-secondary-text-color)";
    public static final String TERTIARY = "var(--lumo-tertiary-text-color)";
    public static final String DISABLED = "var(--lumo-disabled-text-color)";
    public static final String PRIMARY = "var(--lumo-primary-text-color)";
    public static final String PRIMARY_CONTRAST = "var(--lumo-primary-contrast-color)";
    public static final String ERROR = "var(--lumo-error-text-color)";
    public static final String ERROR_CONTRAST = "var(--lumo-error-contrast-color)";
    public static final String SUCCESS = "var(--lumo-success-text-color)";
    public static final String SUCCESS_CONTRAST = "var(--lumo-success-contrast-color)";
  }

  private static Map<SizeType, String[]> marginBySizeType = new HashMap<>();
  private static Map<SizeType, String[]> paddingBySizeType = new HashMap<>();

  static {
    marginBySizeType.put(SizeType.ALL, new String[] {"margin"});
    marginBySizeType.put(SizeType.TOP, new String[] {"margin-top"});
    marginBySizeType.put(SizeType.BOTTOM, new String[] {"margin-bottom"});
    marginBySizeType.put(SizeType.LEFT, new String[] {"margin-left"});
    marginBySizeType.put(SizeType.RIGHT, new String[] {"margin-right"});
    marginBySizeType.put(SizeType.HORIZONTAL, new String[] {"margin-left", "margin-right"});
    marginBySizeType.put(SizeType.VERTICAL, new String[] {"margin-top", "margin-bottom"});
    paddingBySizeType.put(SizeType.ALL, new String[] {"padding"});
    paddingBySizeType.put(SizeType.TOP, new String[] {"padding-top"});
    paddingBySizeType.put(SizeType.BOTTOM, new String[] {"padding-bottom"});
    paddingBySizeType.put(SizeType.LEFT, new String[] {"padding-left"});
    paddingBySizeType.put(SizeType.RIGHT, new String[] {"padding-right"});
    paddingBySizeType.put(SizeType.HORIZONTAL, new String[] {"padding-left", "padding-right"});
    paddingBySizeType.put(SizeType.VERTICAL, new String[] {"padding-top", "padding-bottom"});
  }

  // CSS UTILITIES

  public static void setAlignSelf(String alignSelf, Component... components) {
    setStyle("align-self", alignSelf, components);
  }

  public static void setAriaLabel(String value, Component... components) {
    setAttribute("aria-label", value, components);
  }

  public static void setBackgroundColor(String backgroundColor, Component... components) {
    setStyle("background-color", backgroundColor, components);
  }

  public static void setBorderRadius(String borderRadius, Component... components) {
    setStyle("border-radius", borderRadius, components);
  }

  public static void setColSpan(int span, Component... components) {
    setAttribute("colspan", Integer.toString(span), components);
  }

  public static void setDisplay(String display, Component... components) {
    setStyle("display", display, components);
  }

  public static void setFontSize(String fontSize, Component... components) {
    setStyle("font-size", fontSize, components);
  }

  public static void setFontWeight(String fontWeight, Component... components) {
    setStyle("font-weight", fontWeight, components);
  }

  public static void setLineHeight(String lineHeight, Component... components) {
    setStyle("line-height", lineHeight, components);
  }

  public static void setMargin(SizeType type, String size, Component... components) {
    setStyles(marginBySizeType.get(type), size, components);
  }

  public static void setMaxWidth(String maxWidth, Component... components) {
    setStyle("max-width", maxWidth, components);
  }

  public static void setOverflow(String overflow, Component... components) {
    setStyle("overflow", overflow, components);
  }

  public static void setPadding(SizeType type, String size, Component... components) {
    setStyles(paddingBySizeType.get(type), size, components);
  }

  public static void setShadow(String shadow, Component... components) {
    setStyle("box-shadow", shadow, components);
  }

  public static void setTextColor(String textColor, Component... components) {
    setStyle("color", textColor, components);
  }

  public static void setTheme(String theme, Component... components) {
    setAttribute("theme", theme, components);
  }

  public static void setTooltip(String tooltip, Component... components) {
    setProperty("title", tooltip, components);
  }

  public static void setWidth(String width, Component... components) {
    setStyle("width", width, components);
  }

  public static void setHeight(String height, Component... components) {
    setStyle("height", height, components);
  }

  public static void setSize(String size, Component... components) {
    setStyles(new String[] {"width", "height"}, size, components);
  }

  public static void setZIndex(int index, Component... components) {
    setStyle("z-index", String.valueOf(index), components);
  }

  public static void stopClickEventPropagation(Component component) {
    component.getElement().addEventListener("click", e -> {
    }).addEventData("event.stopPropagation()");
  }

  // INTERNAL

  private static void setStyles(String[] attributes, String value, Component... components) {
    for (Component component : components) {
      for (String attribute : attributes) {
        component.getElement().getStyle().set(attribute, value);
      }
    }
  }

  private static void setStyle(String name, String value, Component... components) {
    for (Component component : components) {
      component.getElement().getStyle().set(name, value);
    }
  }

  private static void setAttribute(String attribute, String value, Component... components) {
    for (Component component : components) {
      component.getElement().setAttribute(attribute, value);
    }
  }

  private static void setProperty(String property, String value, Component... components) {
    for (Component component : components) {
      component.getElement().setProperty(property, value);
    }
  }



}
