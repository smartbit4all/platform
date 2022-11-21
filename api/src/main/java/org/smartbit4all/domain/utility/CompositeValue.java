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
package org.smartbit4all.domain.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.smartbit4all.core.utility.StringConstant;

/**
 * At java level we need the representation for a composite values where the this can be used as
 * comparable and can be used as key in a map. At the end of the day at SQL level we can add equal
 * in this way:
 * 
 * (ID1 = 1 AND ID2 = 'apple')
 * 
 * Or if we need to add an IN operation then we can add this in this way:
 * 
 * (ID1, ID2) IN ((1, 'apple'), (2, 'pear'), (3, 'peach'))
 * 
 * If we have more values in the IN list that supported by the database engine then we will use the
 * tempset functionality of the smartbit platform. In this case the values are inserted into a temp
 * table before the query itself and we will use this table as a set and examine the existence of
 * the keys.
 * 
 * WHERE ... AND EXISTS (SELECT 1 from TEMPSET1 TS1 WHERE TS1.VALUE1 = T1.ID1 and TS2.VALUE2 =
 * T1.ID2)
 * 
 * @author Peter Boros
 */
public final class CompositeValue implements Comparable<CompositeValue> {

  /**
   * The values in the composite value strictly in the order of the properties.
   */
  @SuppressWarnings("rawtypes")
  final List<Comparable> values;

  /**
   * Constructs a new {@link CompositeValue} object with the parameters.
   * 
   * @param values Beware of the order of the values!
   */
  public CompositeValue(Comparable<?>... values) {
    super();
    this.values = Arrays.asList(values);
  }

  /**
   * Constructs a new {@link CompositeValue} object with the parameters.
   * 
   * @param values Beware of the order of the values!
   */
  @SuppressWarnings("rawtypes")
  public CompositeValue(List<Comparable> values) {
    super();
    this.values = values;
  }

  @SuppressWarnings("unchecked")
  @Override
  public int compareTo(CompositeValue o) {
    if (o == null || o.values.size() != values.size()) {
      // The null values will be at the end of the ordered list.
      return -1;
    }
    for (int i = 0; i < values.size(); i++) {
      if (values.get(i) == null && o.values.get(i) != null) {
        return 1;
      } else if (values.get(i) != null && o.values.get(i) == null) {
        return -1;
      } else if (values.get(i) != null && o.values.get(i) != null) {
        int compareTo = values.get(i).compareTo(o.values.get(i));
        if (compareTo != 0) {
          return compareTo;
        }
      }
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && obj instanceof CompositeValue) {
      return compareTo((CompositeValue) obj) == 0;
    }
    return false;
  }

  @Override
  public int hashCode() {
    if (values.isEmpty()) {
      // The hash code for null will be 0
      return 0;
    }
    return values.hashCode();
  }

  @SuppressWarnings("rawtypes")
  public final List<Comparable> getValues() {
    return Collections.unmodifiableList(values);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(StringConstant.LEFT_CURLY);
    boolean first = true;
    for (Comparable<?> comparable : values) {
      if (!first) {
        sb.append(StringConstant.COMMA_SPACE);
      }
      sb.append(comparable.toString());
      first = false;
    }
    return sb.toString();
  }

}
