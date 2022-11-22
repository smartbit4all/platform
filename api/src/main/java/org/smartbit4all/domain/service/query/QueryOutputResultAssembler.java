package org.smartbit4all.domain.service.query;

import org.smartbit4all.domain.meta.Property;

public interface QueryOutputResultAssembler {

  public static final int COLUMNNOTACCEPTED = -1;

  /**
   * A call back function to notify that the fetch began.
   */
  void start();

  /**
   * Adding a new row for the result. The new row will be the iterated so the next
   * {@link #setValue(int, Object)} will work on this.
   * 
   * @return Return true if the result can accept the next line. If it's false then the QueryRequest
   *         will skip the rest of the result and stop fetching.
   */
  boolean startRow();

  /**
   * This ensure the given result that it will accept the given {@link Property} as a fetch result.
   * 
   * @param property The property to accept.
   * @return The index that can be used to set the value of the result.
   */
  int accept(Property<?> property);

  /**
   * When the query is over and every accessible record is fetched or at least the fetch is stopped
   * with any other reason then this function is called. It's a notification that the post process
   * can start.
   */
  void finish();

  /**
   * The query will call back this function when the reading from the data source is ready and all
   * the stored properties are set. At this event the result can filter, compute or do some other
   * post processing.
   */
  void finishRow();

  /**
   * The {@link QueryResult} acts like an iterator for the fetch result. This function set the
   * previously {@link #accept(Property)} property by index.
   * 
   * @param index The index for the {@link Property} generated by the {@link #accept(Property)}
   *        method.
   * @param value The value to set.
   */
  void setValue(int index, Object value);

  /**
   * Finalizes the columns and the meta of the result.
   */
  void finishColumns();



}