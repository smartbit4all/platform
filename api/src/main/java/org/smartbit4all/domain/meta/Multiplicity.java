package org.smartbit4all.domain.meta;

import com.google.common.base.Objects;

public class Multiplicity {

  /**
   * The lower bound of the multiplicity. If null then it's unbound.
   */
  private final Integer lowerBound;

  /**
   * The upper bound of the multiplicity. If null then it's unbound.
   */
  private final Integer upperBound;

  public Multiplicity(Integer lowerBound, Integer upperBound) {
    super();
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  public static final Multiplicity ZERO_OR_ONE = new Multiplicity(0, 1);

  public static final Multiplicity ONE = new Multiplicity(1, 1);

  public static final Multiplicity ONE_OR_MORE = new Multiplicity(1, null);

  public static final Multiplicity UNDEFINED = new Multiplicity(null, null);

  @Override
  public boolean equals(Object obj) {
    return obj instanceof Multiplicity
        ? (Objects.equal(lowerBound, ((Multiplicity) obj).lowerBound))
            && (Objects.equal(upperBound, ((Multiplicity) obj).upperBound))
        : false;
  }

}
