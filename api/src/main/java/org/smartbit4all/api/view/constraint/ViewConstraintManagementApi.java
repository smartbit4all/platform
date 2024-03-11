package org.smartbit4all.api.view.constraint;

import java.net.URI;
import org.smartbit4all.api.contribution.PrimaryApi;
import org.smartbit4all.api.view.bean.View;
import org.smartbit4all.api.view.bean.ViewConstraint;
import org.smartbit4all.core.object.ObjectNode;

public interface ViewConstraintManagementApi extends PrimaryApi<ViewConstraintSupplierApi> {

  default ViewConstraint calculateViewConstraint(final View view, final ObjectNode domainObject,
      final URI currentUserUri) {
    return calculateViewConstraint(view, domainObject, currentUserUri,
        view == null ? null : view.getModel());
  }

  ViewConstraint calculateViewConstraint(final View view, final ObjectNode domainObject,
      final URI currentUserUri, final Object viewModel);

  ViewConstraint calculateViewConstraint(final View view,
      final ViewConstraintConfigurer viewConstraintConfigurer);

}
