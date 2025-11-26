package org.smartbit4all.api.collection;

import java.util.List;
import java.util.Map;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionBuilderField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionData;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionField;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionFieldList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionList;
import org.smartbit4all.api.filterexpression.bean.FilterExpressionOperandData;
import org.smartbit4all.domain.meta.EntityDefinition;
import org.smartbit4all.domain.meta.Expression;

/**
 *
 * The api that is responsible for the {@link FilterExpressionList} and {@link Expression}
 * translation. The difference between the two domain is that the {@link FilterExpressionList} and
 * {@link FilterExpressionData}.
 * 
 * @author Attila Mate
 *
 */
public interface FilterExpressionApi {

  FilterExpressionList of(FilterExpressionFieldList filterExpressionFieldList);

  /**
   * Creates a human readable description of the provided {@link FilterExpressionFieldList}'s dirty
   * fields.
   * 
   * <p>
   * The descriptions are rendered in the current {@link User}'s locale.
   * 
   * <p>
   * Nested expressions currently not supported.
   * 
   * @param filterExpressionFieldList a {@link FilterExpressionFieldList} containing filter fields
   *        the user may interact with
   * @return a {@link List} of {@String} descriptions of the dirty fields in the field list, or a
   *         "no filter set" message if the field list is pristine
   * @see #isPristine(FilterExpressionFieldList)
   */
  List<String> describe(FilterExpressionFieldList filterExpressionFieldList);

  /**
   * Creates a human readable description of the provided {@link FilterExpressionFieldList}'s dirty
   * fields.
   * 
   * <p>
   * Nested expressions currently not supported.
   * 
   * @param filterExpressionFieldList a {@link FilterExpressionFieldList} containing filter fields
   *        the user may interact with
   * @param locale the {@link String} locale in which the descriptions should be written, nullable;
   *        if null, the current session's locale shall be used
   * @return a {@link List} of {@String} descriptions of the dirty fields in the field list, or a
   *         "no filter set" message if the field list is pristine
   * @see #isPristine(FilterExpressionFieldList)
   */
  List<String> describe(FilterExpressionFieldList filterExpressionFieldList, String locale);

  /**
   * Determines whether a {@link FilterExpressionFieldList} is pristine.
   * 
   * <p>
   * A pristine field list is one the user has not entered any data into. In other words, at least
   * one of the following must be true, to have a field list be considered pristine:
   * 
   * <ul>
   * <li>The {@code FilterExpressionFieldList} is {@code null}.</li>
   * <li>The list of fields found at {@link FilterExpressionFieldList#getFilters()} is
   * {@code null}.</li>
   * <li>The list of fields found at {@link FilterExpressionFieldList#getFilters()} is empty.</li>
   * <li>For all fields found at {@link FilterExpressionFieldList#getFilters()} one of the following
   * is true:
   * <ul>
   * <li>The {@link FilterExpressionField} is null.
   * <li>The expression data found at {@link FilterExpressionField#getExpressionData()} is null.
   * <li>None of the {@link FilterExpressionOperandData} which are NOT marked as
   * {@code isDataNam == true} has any value present in either
   * {@link FilterExpressionOperandData#getSelectedValues()} or
   * {@link FilterExpressionOperandData#getValueAsString()}
   * </ul>
   * </li>
   * </ul>
   * 
   * @param filterExpressionFieldList a {@link FilterExpressionFieldList} to examine
   * @return true if the field list is pristine, false otherwise
   */
  boolean isPristine(FilterExpressionFieldList filterExpressionFieldList);

  boolean operandHasValue(FilterExpressionOperandData operandData);

  FilterExpressionBuilderField builderFieldForString(String propertyName);

  Expression constructExpression(FilterExpressionList filterExpressions,
      EntityDefinition entityDef);

  Expression constructExpression(FilterExpressionList filterExpressions,
      SearchEntityDefinition searchEntityDefinition,
      SearchIndexMappingObject searchIndexMappingObject,
      Map<String, CustomExpressionMapping> expressionByPropertyName);


}
