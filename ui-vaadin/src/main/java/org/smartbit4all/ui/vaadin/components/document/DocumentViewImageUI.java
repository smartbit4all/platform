package org.smartbit4all.ui.vaadin.components.document;

import org.smartbit4all.api.documentview.bean.ImageWithAlt;
import org.smartbit4all.core.object.ChangeState;
import org.smartbit4all.core.object.CollectionObjectChange;
import org.smartbit4all.core.object.ObjectChangeSimple;
import org.smartbit4all.core.object.PropertyChange;
import org.smartbit4all.core.object.ReferencedObjectChange;
import org.smartbit4all.ui.api.components.document.editing.DocumentViewProcessEditing;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

/**
 * Displays documents converted into images as one big actual page shown and other small images next
 * to the big one.
 */
@CssImport(value = "./styles/components/details-drawer.css")
public class DocumentViewImageUI extends FlexLayout {

  private FlexLayout documentPanel;
  private FlexLayout thumbnailsPanel;

  private Image showedImage = new Image();
  private HorizontalLayout navPanel;
  private Button leftButton;
  private Button rightButton;
  private Label allPage;
  private TextField actualPageField;
  private Button thumbnailButton;
  private ThumbnailsDrawer thumbnailsDrawer;

  private DocumentViewProcessEditing processEditing;

  public DocumentViewImageUI(DocumentViewProcessEditing documentViewprocessEditing) {
    this.processEditing = documentViewprocessEditing;

    createUI();

    processEditing.process().onReferencedObjectChange(this::onMainImageChanged, "mainImage");
    processEditing.process().onPropertyChange(this::onZoomValueChanged, "zoomValue");

    processEditing.process().onCollectionObjectChange(this::onThumbnailsChanged, "thumbnails");
    processEditing.process().onPropertyChange(this::onPageCountChanged, "pageCount");
    processEditing.process().onPropertyChange(this::onPageIndexChanged, "pageIndex");
    processEditing.process().onPropertyChange(
        this::onLeftButtonEnabledChanged, "leftButtonEnabled");
    processEditing.process().onPropertyChange(
        this::onRightButtonEnabledChanged, "rightButtonEnabled");
  }

  private void createUI() {
    documentPanel = createDocumentPanel();
    thumbnailsPanel = createThumbnailsPanel();
  }

  /**
   * Creates the document panel used for showing the actual document page, under that the scaler and
   * navigation panel.
   */
  public FlexLayout createDocumentPanel() {
    FlexLayout layout = new FlexLayout();
    layout.setId("document-panel");
    layout.setClassName("document-panel");

    Div imageHolder = new Div();
    imageHolder.setId("image-holder");
    imageHolder.add(showedImage);
    showedImage.setId("showed-image");
    layout.add(imageHolder);

    FlexLayout controlPanel = new FlexLayout();
    HorizontalLayout scalerPanel = createScalerPanel();
    controlPanel.add(scalerPanel);

    navPanel = createNavPanel();
    controlPanel.add(navPanel);

    layout.add(controlPanel);
    controlPanel.setId("control-panel");

    return layout;
  }

  /**
   * Create the panel used for holding the small images showing the document pages.
   */
  public FlexLayout createThumbnailsPanel() {
    FlexLayout layout = new FlexLayout();
    layout.setId("thumbnails-panel");
    return layout;
  }

  /**
   * In case of a big screen, adds the document and thumbnails panel in one layout. Otherwise adds
   * the document panel, then creates and adds a ThumbnailsDrawer to be able to show the other pages
   * in small sized screen too.
   */
  public void addDocumentPanelAndThumbnails() {
    UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
      int screenWidth = receiver.getScreenWidth();
      if (screenWidth > 720) {
        SplitLayout documentAndThumbnailsPanelLayout =
            new SplitLayout(documentPanel, thumbnailsPanel);
        documentAndThumbnailsPanelLayout.addToPrimary(documentPanel);
        documentAndThumbnailsPanelLayout.setWidthFull();
        documentAndThumbnailsPanelLayout.setSplitterPosition(70);
        add(documentAndThumbnailsPanelLayout);
        thumbnailButton.setVisible(false);
      } else {
        add(documentPanel);
        thumbnailsDrawer = new ThumbnailsDrawer(thumbnailsPanel);
        thumbnailsDrawer.hide();
        add(thumbnailsDrawer);
        thumbnailButton.setVisible(true);
      }
    });
  }

  /**
   * Creates the navigation panel and adds the required Label, TextField and Buttons, also adds
   * listeners to the Buttons.
   */
  private HorizontalLayout createNavPanel() {
    HorizontalLayout navPanel = new HorizontalLayout();
    navPanel.setId("navigation-panel");
    leftButton = new Button("", new Icon(VaadinIcon.ANGLE_LEFT));
    rightButton = new Button("", new Icon(VaadinIcon.ANGLE_RIGHT));

    leftButton.addClickListener(i -> leftButtonClick());

    rightButton.addClickListener(i -> rightButtonClick());

    navPanel.add(leftButton);
    actualPageField = new TextField();
    actualPageField.setId("actual-page-field");

    actualPageField.addValueChangeListener(t -> actualPageFieldValueChangedHandler(t));

    navPanel.add(actualPageField);
    allPage = new Label();
    navPanel.add(allPage);

    navPanel.add(rightButton);

    thumbnailButton = new Button("", new Icon(VaadinIcon.GRID));
    thumbnailButton.addClickListener(t -> thumbnailsDrawer.show());

    navPanel.add(thumbnailButton);
    navPanel.setVerticalComponentAlignment(Alignment.CENTER, allPage, actualPageField, leftButton,
        rightButton, thumbnailButton);

    return navPanel;
  }

  private void leftButtonClick() {
    processEditing.showPrevPage();
  }

  private void rightButtonClick() {
    processEditing.showNextPage();
  }

  private void actualPageFieldValueChangedHandler(ComponentValueChangeEvent<TextField, String> t) {
    if (t.isFromClient()) {
      String value = t.getValue();
      processEditing.setPage(value);
    }
  }

  /**
   * Creates the scaler panel and adds the required Buttons, then adds listeners to them.
   */
  private HorizontalLayout createScalerPanel() {
    HorizontalLayout layout = new HorizontalLayout();
    layout.setId("scaler-panel");
    Button minusButton = new Button("", new Icon(VaadinIcon.MINUS));
    minusButton.addClickListener(m -> minusButtonClickHandler());
    Button plusButton = new Button("", new Icon(VaadinIcon.PLUS));
    plusButton.addClickListener(p -> plusButtonClickHandler());
    Button fitToWidthButton = new Button("", new Icon(VaadinIcon.VIEWPORT));
    fitToWidthButton.addClickListener(e -> fitShowedImageWidthToClientWidth());
    layout.add(minusButton, plusButton, fitToWidthButton);
    return layout;
  }

  private void minusButtonClickHandler() {
    processEditing.zoomOut();
  }

  private void plusButtonClickHandler() {
    processEditing.zoomIn();
  }

  /**
   * Sets the showed image width 90%, then gets the image's actual width in pixels, and sets the
   * value in the attached viewmodel.
   */
  private void fitShowedImageWidthToClientWidth() {
    showedImage.setWidth("90%");
    showedImage.getElement().executeJs("return $0.clientWidth", showedImage.getElement())
        .then(width -> {
          processEditing.setActualPageWidth((int) width.asNumber());
        });
  }

  /**
   * Displays the new given image in the document panel.
   */
  public void onMainImageChanged(ReferencedObjectChange referencedObjectChange) {
    Object object = referencedObjectChange.getChange().getObject();
    if (object != null) {
      ImageWithAlt newImage = (ImageWithAlt) object;
      showedImage.setSrc(getImageResource(newImage));
      showedImage.setAlt(newImage.getAlt());
      fitShowedImageWidthToClientWidth();
    }
  }

  /**
   * Adds the given images in the thumbnails panel, and adds ClickListener to the images, that way
   * the clicked thumbnail will be shown as the big actual page.
   */
  public void onThumbnailsChanged(CollectionObjectChange collectionChange) {
    int pageIndex = 0;
    thumbnailsPanel.removeAll();
    for (ObjectChangeSimple change : collectionChange.getChanges()) {
      Object object = change.getObject();
      if (object != null && change.getOperation().equals(ChangeState.NEW)) {
        final int currentPageIndex = pageIndex;
        ImageWithAlt thumbnail = (ImageWithAlt) object;
        Image image = new Image(getImageResource(thumbnail), thumbnail.getAlt());
        image.addClickListener(clickImage -> thumbnailClickHandler(thumbnail, currentPageIndex));
        image.setId("thumbnail-image");
        thumbnailsPanel.add(image);
        pageIndex++;
      }
    }
  }

  /**
   * Displays the clicked image in the document panel, and hides the thumbnailsDrawer if it was
   * used.
   */
  private void thumbnailClickHandler(ImageWithAlt thumbnail, int pageIndex) {
    processEditing.setPage(pageIndex);
    UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
      int screenWidth = receiver.getScreenWidth();
      if (screenWidth < 720) {
        thumbnailsDrawer.hide();
      }
    });
  }

  /**
   * If the showed document is one page long, displays only the documentPanel, hides the
   * thumbnailButton and navPanel. Otherwise, displays the document- and thumbnailPanel in a
   * SplitLayout sets the allPage label, shows the thumbnailButton and navPanel.
   */
  public void onPageCountChanged(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object != null) {
      int newPageCount = (int) object;
      if (newPageCount > 1) {
        removeAll();
        addDocumentPanelAndThumbnails();
        allPage.setText("/" + newPageCount);
        navPanel.setVisible(true);
      } else {
        removeAll();
        add(documentPanel);
        documentPanel.setWidthFull();
        thumbnailButton.setVisible(false);
        navPanel.setVisible(false);
      }
    }
  }

  /**
   * Sets the showed image width in pixel to the given value.
   */
  public void onZoomValueChanged(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object != null) {
      int newZoomValue = (int) object;
      showedImage.setWidth(newZoomValue + "px");
    }
  }

  public void onPageIndexChanged(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object != null) {
      String newPageIndex = (String) object;
      actualPageField.setValue(newPageIndex);
    }
  }

  public void onLeftButtonEnabledChanged(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object != null) {
      boolean newLeftButtonEnabled = (boolean) object;
      leftButton.setEnabled(newLeftButtonEnabled);
    }
  }

  public void onRightButtonEnabledChanged(PropertyChange propertyChange) {
    Object object = propertyChange.getNewValue();
    if (object != null) {
      boolean newRightButtonEnabled = (boolean) object;
      rightButton.setEnabled(newRightButtonEnabled);
    }
  }

  private StreamResource getImageResource(ImageWithAlt image) {
    StreamResource streamResource =
        new StreamResource(image.getFileName(), () -> image.getImage().inputStream());
    return streamResource;
  }

  /**
   * In case of a small screen, it is used for displaying/hiding the images, that are stored in the
   * thumbnailsPanel, in front of the documentPanel for a clearer view.
   */
  private class ThumbnailsDrawer extends FlexLayout {

    private Button close;

    private String CLASS_NAME = "details-drawer";

    private FlexLayout content;

    public ThumbnailsDrawer(Component... components) {
      setFlexDirection(FlexDirection.COLUMN);
      setClassName(CLASS_NAME);
      getElement().setAttribute("position", "right");
      content = new FlexLayout(components);
      content.setClassName(CLASS_NAME + "__content");

      close = new Button(new Icon(VaadinIcon.CLOSE));
      close.addClickListener(c -> hide());
      FlexLayout header = new FlexLayout(close);
      header.setClassName(CLASS_NAME + "__header");

      add(header);
      add(content);
    }

    public void hide() {
      getElement().setAttribute("open", false);
    }

    public void show() {
      getElement().setAttribute("open", true);
    }
  }
}
