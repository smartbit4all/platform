package org.smartbit4all.ui.vaadin.components.form2.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartbit4all.ui.api.form.model.WidgetDescriptor;
import org.smartbit4all.ui.api.form.model.WidgetInstance;
import org.smartbit4all.ui.vaadin.components.form2.PredictiveFormInstanceViewUI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

public class SurveyComboDialog extends Dialog{

  private static final Logger log = LoggerFactory.getLogger(SurveyComboDialog.class);

  public SurveyComboDialog(WidgetInstance instance, PredictiveFormInstanceViewUI ui, WidgetDescriptor descriptor) {
    FlexLayout dialogLayout = new FlexLayout();
    dialogLayout.setClassName("survey-combo-dialog-layout");
    add(dialogLayout);
    
    Label titleLabel = new Label(descriptor.getLabel());
    
    Image image = new Image();
    
    ComboBox<Integer> cbScore = new ComboBox<>();
    cbScore.setItems(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5)));
    
    TextField tfComment = new TextField();
    
    // TODO binder
    Binder<WidgetInstance> binder = new Binder<>(WidgetInstance.class);
    binder.forField(cbScore).bind(w -> {
      return w.getIntValues().get(0);
    }, (w, v) -> {
      if (w.getIntValues() != null) {
        w.getIntValues().set(0, v);
      } else {
        w.addIntValuesItem(v);
      }
    });
    
    binder.forField(tfComment).bind(w -> {
      if (instance.getStringValues() != null && instance.getStringValues().size() > 0) {
        return instance.getStringValues().get(0);
      } else {
        return null;
      }
    }, (w, v) -> {
      if (w.getStringValues() != null) {
        w.getStringValues().set(0, v);
      } else {
        w.addStringValuesItem(v);
      }
    });
    
//    List<BinaryData> binaryDataValues = instance.getBinaryDataValues();
//    if (binaryDataValues != null && binaryDataValues.get(0) != null) {
////      image.setSrc(null);
//    }
//    
    
  }
}
