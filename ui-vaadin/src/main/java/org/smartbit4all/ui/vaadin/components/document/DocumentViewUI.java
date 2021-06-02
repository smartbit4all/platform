package org.smartbit4all.ui.vaadin.components.document;

import java.lang.reflect.Proxy;
import java.util.List;
import org.smartbit4all.api.mimetype.DisplayMode;
import org.smartbit4all.ui.common.components.document.DocumentView;
import org.smartbit4all.ui.common.components.document.DocumentViewController;
import org.smartbit4all.ui.common.components.document.ImageWithAlt;
import org.smartbit4all.ui.vaadin.service.UIViewInvocationHandler;
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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.StreamResource;

/**
 * The Vaadin based implementation of {@link DocumentView}. Displays the document in a plain text
 * area or in images based on the displayMode. Besides that, in the IMAGE displayMode it shows the
 * scaler panel used for zoom in/out and if there are more than one image it also shows the
 * navigation and thumbnails panel.
 */
@CssImport(value = "./smartbit4all/styles/components/custom/details-drawer.css")
public class DocumentViewUI extends FlexLayout implements DocumentView {

  private DocumentViewController controller;
  private DisplayMode displayMode;
  private FlexLayout documentPanel;
  private Image showedImage = new Image();
  private FlexLayout thumbnailsPanel;
  private HorizontalLayout navPanel;
  private Button leftButton;
  private Button rightButton;
  private Label allPage;
  private TextField actualPageField;
  private ThumbnailsDrawer thumbnailsDrawer;
  private Button thumbnailButton;
  private FlexLayout textPanel;
  private TextArea textArea;

  public DocumentViewUI(DocumentViewController controller) {
    this.controller = controller;
    this.displayMode = controller.getMimeTypeHandler().getDisplayMode();
    controller.setUI((DocumentView) Proxy.newProxyInstance(getClass().getClassLoader(),
        new Class[] {DocumentView.class}, new UIViewInvocationHandler(this, UI.getCurrent())));
    setId("right-panel");
    displayLoadingSpinner();
  }

  /**
   * Displays the actual document page, the navigation and thumbnails panel or the document's
   * content in a text area based on the displayMode.
   */
  @Override
  public void displayData() {
    if (displayMode == DisplayMode.IMAGE) {
      controller.showNavPanel();
      controller.showThumbnails();
      controller.showPage(0);
    } else if (displayMode == DisplayMode.TEXT) {
      controller.showTextArea();
    }
  }

  /**
   * Creates the document panel or text panel based on the displayMode, and the thumbnails panel if
   * the given parameter is true.
   */
  @Override
  public void createUI(boolean createThumbPanel) {
    removeAll();
    if (displayMode == DisplayMode.IMAGE) {
      documentPanel = createDocumentPanel();
      if (createThumbPanel) {
        thumbnailsPanel = createThumbnailsPanel();
        addDocumentAndThumbnailsPanelInOneLayout();
      } else {
        add(documentPanel);
      }
    } else if (displayMode == DisplayMode.TEXT) {
      textPanel = createTextPanel();
      add(textPanel);
    }
  }

  /**
   * Creates the document panel used for showing the actual document page, under that the scaler and
   * navigation panel.
   */
  public FlexLayout createDocumentPanel() {
    FlexLayout layout = new FlexLayout();
    layout.setId("document-panel");

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
   * In case of a big screen, adds the document and thumbnails panel in one layout. Otherwise add
   * the document panel, then creates and adds a ThumbnailsDrawer to be able to show the other pages
   * in small sized screen too.
   */
  public void addDocumentAndThumbnailsPanelInOneLayout() {
    UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
      int screenWidth = receiver.getScreenWidth();
      if (screenWidth > 720) {
        SplitLayout layout = new SplitLayout();
        layout.addToPrimary(documentPanel);
        layout.addToSecondary(thumbnailsPanel);
        layout.setSplitterPosition(75);
        add(layout);
      } else {
        add(documentPanel);
        thumbnailsDrawer = new ThumbnailsDrawer(thumbnailsPanel);
        thumbnailsDrawer.hide();
        add(thumbnailsDrawer);
      }
    });
  }

  /**
   * Creates the navigation panel and adds the required Label, TextField and Buttons, then adds
   * listeners to them.
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
    actualPageField.setValue("1");

    navPanel.add(actualPageField);
    allPage = new Label();
    navPanel.add(allPage);

    navPanel.add(rightButton);
    thumbnailButton = new Button(new Icon(VaadinIcon.GRID));
    thumbnailButton.setId("thumbnail-button");
    thumbnailButton.addClickListener(t -> thumbnailsDrawer.show());
    navPanel.setVerticalComponentAlignment(Alignment.CENTER, allPage, actualPageField, leftButton,
        rightButton, thumbnailButton);

    navPanel.add(thumbnailButton);
    return navPanel;
  }

  private void leftButtonClick() {
    controller.showPrevPage();
  }

  private void rightButtonClick() {
    controller.showNextPage();
  }

  private void actualPageFieldValueChangedHandler(ComponentValueChangeEvent<TextField, String> t) {
    if (t.isFromClient()) {
      String value = t.getValue();
      controller.showPage(value);
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
    showedImage.getElement().executeJs("return $0.clientWidth", showedImage.getElement())
        .then(width -> {
          int showedImageClientWidth = (int) width.asNumber();
          int newValue = showedImageClientWidth - 100;
          if (isZoomValueValid(newValue)) {
            showedImageClientWidth = newValue;
            showedImage.setWidth(showedImageClientWidth + "px");
          }
        });
  }

  private void plusButtonClickHandler() {
    showedImage.getElement().executeJs("return $0.clientWidth", showedImage.getElement())
        .then(width -> {
          int showedImageClientWidth = (int) width.asNumber();
          int newValue = showedImageClientWidth + 100;
          if (isZoomValueValid(newValue)) {
            showedImageClientWidth = newValue;
            showedImage.setWidth(showedImageClientWidth + "px");
          }
        });
  }

  private boolean isZoomValueValid(int zoomValue) {
    if (zoomValue >= 200 && zoomValue <= 3000)
      return true;
    return false;
  }

  private void fitShowedImageWidthToClientWidth() {
    showedImage.setWidth("90%");
  }

  /**
   * Creates the text panel, sets it to read-only and full width.
   */
  public FlexLayout createTextPanel() {
    FlexLayout layout = new FlexLayout();
    layout.setId("text-panel");
    layout.setWidthFull();
    textArea = new TextArea();
    textArea.setReadOnly(true);
    textArea.setWidthFull();
    layout.add(textArea);
    return layout;
  }

  /**
   * Displays the new given image in the document panel.
   */
  @Override
  public void displayImage(ImageWithAlt image) {
    showedImage.setSrc(getImageResource(image));
    showedImage.setAlt(image.getAlt());
  }

  private StreamResource getImageResource(ImageWithAlt image) {
    StreamResource streamResource = new StreamResource(image.getFilename(), () -> image.getImage());
    return streamResource;
  }

  /**
   * Adds the given images in the thumbnails panel, and adds ClickListener to the images.
   */
  @Override
  public void setThumbnails(List<ImageWithAlt> thumbnails) {
    for (ImageWithAlt thumbnail : thumbnails) {
      Image image = new Image(getImageResource(thumbnail), thumbnail.getAlt());
      image.addClickListener(clickImage -> thumbnailClickEventHandler(thumbnails, thumbnail));
      image.setId("thumbnail-image");
      thumbnailsPanel.add(image);
    }
  }

  /**
   * Displays the clicked image in the document panel, and hides the thumbnailsDrawer if it was
   * used.
   */
  private void thumbnailClickEventHandler(List<ImageWithAlt> thumbnails, ImageWithAlt thumbnail) {
    controller.showPage(thumbnails.indexOf(thumbnail));
    UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
      int screenWidth = receiver.getScreenWidth();
      if (screenWidth < 720) {
        thumbnailsDrawer.hide();
      }
    });
  }

  @Override
  public void setLeftArrowEnable(boolean enabled) {
    leftButton.setEnabled(enabled);
  }

  @Override
  public void setRightArrowEnable(boolean enabled) {
    rightButton.setEnabled(enabled);
  }

  @Override
  public void displayAllPageCount(int allPageCount) {
    allPage.setText("/" + allPageCount);
  }

  @Override
  public void setNavPanelVisible(boolean isVisible) {
    navPanel.setVisible(isVisible);
  }

  @Override
  public void setActualPageLabel(String actualPage) {
    actualPageField.setValue(actualPage);
  }

  /**
   * In case of a big screen, removes everything except the documentPanel from the viewer.
   * (Otherwise it is needless, because the thumbnailsDrawer is used for displaying thumbnails, and
   * the thumbnailsPanel is hidden anyways.)
   */
  @Override
  public void setThumbnailsVisible(boolean isVisible) {
    if (!isVisible) {
      UI.getCurrent().getPage().retrieveExtendedClientDetails(receiver -> {
        int screenWidth = receiver.getScreenWidth();
        if (screenWidth > 720) {
          removeAll();
          add(documentPanel);
        }
      });
    } else {
      // TODO removeAll(), then add the documentPanel with the thumbnails some way
    }
  }

  @Override
  public void setThumbnailButtonVisible(boolean isvisible) {
    thumbnailButton.setVisible(isvisible);
  }

  @Override
  public void displayText(String text) {
    textArea.setValue(text);
  }

  private void displayLoadingSpinner() {
    add(createProgressBar());
  }

  private Component createProgressBar() {
    ProgressBar progress = new ProgressBar();
    progress.setIndeterminate(true);
    progress.setId("progress-bar");
    FlexLayout loadingSpinner = new FlexLayout(progress);
    loadingSpinner.setId("loading-spinner");
    return loadingSpinner;
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
