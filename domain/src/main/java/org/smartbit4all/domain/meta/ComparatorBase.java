package org.smartbit4all.domain.meta;

import java.util.Comparator;
import java.util.Objects;

public class ComparatorBase<T extends Comparable<?>> implements Comparator<T> {

  /**
   * Says if the compare is in ascending order or not.
   */
  private boolean ascending = true;

  /**
   * Defines if the null values should be at the end of the list if it's sorted by this comparator.
   */
  private boolean nullAtTheEnd = true;

  public ComparatorBase(boolean ascending, boolean nullAtTheEnd) {
    setAscending(ascending);
    setNullAtTheEnd(nullAtTheEnd);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public int compare(T o1, T o2) throws ClassCastException {
    if (Objects.equals(o1, o2)) {
      return 0;
    }
    // If the two value is not equal then we examine the cases
    if (o1 == null) {
      return -1;
    } else if (o2 == null) {
      return 1;
    } else {
      return ascending ? ((Comparable) o1).compareTo(o2) : ((Comparable) o2).compareTo(o1);
    }
  }

  /**
   * @return Returns the ascending.
   */
  public boolean isAscending() {
    return ascending;
  }

  /**
   * @param ascending The ascending to set.
   */
  public void setAscending(boolean ascending) {
    this.ascending = ascending;
  }

  /**
   * @param result The result of the compare.
   * @return The readable sign for the result.
   *         <ul>
   *         <li>=0 : <b>=</b></li>
   *         <li><0 : <b><</b></li>
   *         <li>>0 : <b>></b></li>
   *         </ul>
   */
  public static String getComparisonResultChar(int result) {
    if (result == 0) {
      return "=";
    }
    return result < 0 ? "<" : ">";
  }

  public final boolean isNullAtTheEnd() {
    return nullAtTheEnd;
  }

  public final void setNullAtTheEnd(boolean nullAtTheEnd) {
    this.nullAtTheEnd = nullAtTheEnd;
  }


}
