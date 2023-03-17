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
package org.smartbit4all.domain.meta.jdbc.impl;

import java.util.Objects;
import org.smartbit4all.domain.meta.jdbc.JDBCBooleanString;
import org.springframework.beans.factory.annotation.Value;

/**
 * Default implementation of the {@link Boolean} value based types.
 * 
 * @author Zoltán Suller
 */
public class JDBCBooleanStringImpl implements JDBCBooleanString {

  @Value("${sb4.jdbc.boolean.trueValue:true}")
  private String trueValue;

  @Value("${sb4.jdbc.boolean.falseValue:false}")
  private String falseValue;

  @Override
  public String app2ext(Boolean appValue) {
    return appValue == null ? null : (appValue ? trueValue : falseValue);
  }

  @Override
  public Boolean ext2app(String extValue) {
    return extValue == null ? null : fromString(extValue);
  }

  private final Boolean fromString(String value) {
    if (value == null) {
      return null;
    }
    if (Objects.equals(value, trueValue)) {
      return Boolean.TRUE;
    }
    if (Objects.equals(value, falseValue)) {
      return Boolean.FALSE;
    }
    switch (value.toUpperCase()) {
      case "TRUE":
        return Boolean.TRUE;
      case "FALSE":
        return Boolean.FALSE;
      case "Y":
        return Boolean.TRUE;
      case "I":
        return Boolean.TRUE;
      case "N":
        return Boolean.FALSE;
      case "1":
        return Boolean.TRUE;
      case "0":
        return Boolean.FALSE;
      default:
        return Boolean.FALSE;
    }
  }

}
