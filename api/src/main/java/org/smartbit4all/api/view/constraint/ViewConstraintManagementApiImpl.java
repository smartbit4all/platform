package org.smartbit4all.api.view.constraint;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import java.net.URI;
import java.util.function.Function;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectNode;

public class ViewConstraintManagementApiImpl
    extends PrimaryApiImpl<ViewConstraintSupplierApi>
    implements ViewConstraintManagementApi {

  public ViewConstraintManagementApiImpl() {
    super(ViewConstraintSupplierApi.class);
  }

  @Override
  public ViewConstraint calculateViewConstraint(final View view, final ObjectNode domainObject,
      final URI currentUserUri, final Object viewModel) {
    return calculateViewConstraintInternal(view,
        api -> api.calculateViewConstraint(view, domainObject, currentUserUri, viewModel));
  }

  @Override
  public ViewConstraint calculateViewConstraint(View view,
      ViewConstraintConfigurer viewConstraintConfigurer) {
    return calculateViewConstraintInternal(view,
        api -> api.calculateViewConstraint(viewConstraintConfigurer));
  }

  private ViewConstraint calculateViewConstraintInternal(final View view,
      final Function<ViewConstraintSupplierApi, ViewConstraint> calculation) {
    if (apiByName == null || apiByName.isEmpty()) {
      return null;
    }

    return apiByName.values().stream()
        .filter(api -> api.supports(view))
        .sorted()
        .map(calculation)
        .flatMap(it -> it.getComponentConstraints().stream())
        .collect(collectingAndThen(toList(), new ViewConstraint()::componentConstraints));
  }

}
