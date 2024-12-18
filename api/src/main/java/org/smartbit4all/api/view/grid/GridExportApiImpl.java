package org.smartbit4all.api.view.grid;

import java.util.UUID;
import java.util.function.Predicate;
import org.smartbit4all.api.attachment.bean.BinaryContentData;
import org.smartbit4all.api.contribution.PrimaryApiImpl;
import org.smartbit4all.api.grid.bean.GridExportDescriptor;
import org.smartbit4all.api.grid.bean.GridModel;
import org.smartbit4all.api.view.ViewApi;
import org.smartbit4all.api.view.bean.DownloadedFile;
import org.smartbit4all.api.view.bean.UiActionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

public class GridExportApiImpl
    extends PrimaryApiImpl<GridExportContributorApi>
    implements GridExportApi {

  @Autowired(required = false)
  private ViewApi viewApi;

  public GridExportApiImpl() {
    super(GridExportContributorApi.class);
  }

  @Override
  public BinaryContentData exportGrid(UUID viewUuid, String gridIdentifier) {
    return exportGridInternal(viewUuid, gridIdentifier, it -> true);
  }

  @Override
  public BinaryContentData exportGrid(UUID viewUuid, String gridIdentifier, String mimetype) {
    return exportGridInternal(viewUuid, gridIdentifier, it -> it.supportsMimetype(mimetype));
  }

  private BinaryContentData exportGridInternal(UUID viewUuid, String gridIdentifier,
      Predicate<GridExportContributorApi> p) {
    if (viewApi == null) {
      return null;
    }

    final GridModel gridModel = viewApi
        .getWidgetModelFromView(
            GridModel.class,
            viewUuid,
            gridIdentifier);

    if (ObjectUtils.isEmpty(gridModel)) {
      return null;
    }

    return getContributionApis()
        .values().stream()
        .filter(p)
        .findFirst()
        .map(api -> api.exportGrid(gridModel, gridIdentifier))
        .orElse(null);
  }

  @Override
  public void exportGridAction(UUID viewUuid, UiActionRequest request, String gridId) {
    GridExportDescriptor descriptor = viewApi
        .getWidgetModelFromView(
            GridModel.class,
            viewUuid,
            gridId)
        .getView().getDescriptor().getExportDescriptor();

    BinaryContentData binaryContentData = null;
    if (ObjectUtils.isEmpty(descriptor.getExportMimeType())) {
      binaryContentData = exportGrid(viewUuid, gridId);
    } else {
      binaryContentData = exportGrid(
          viewUuid, gridId,
          descriptor.getExportMimeType());
    }

    if (!ObjectUtils.isEmpty(binaryContentData)) {

      viewApi.getView(viewUuid).putDownloadableItemsItem(EXPORT_GRID,
          binaryContentData.getDataUri());
      viewApi.downloadFile(new DownloadedFile().uuid(viewUuid).identifier(EXPORT_GRID)
          .filename(binaryContentData.getFileName()
              + "."
              + binaryContentData.getExtension()));
    }

  }
}
