package org.smartbit4all.ui.vaadin.components.sb4starter;

import java.util.function.Supplier;

import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.ui.api.sb4starter.SB4StarterWordViewModel;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.StreamResource;

public class SB4StarterWordButton extends Anchor {
	
	private Button btnInner;
	
	private String sb4StarterFileNameWithoutExt = "editing.sb4starter";
	
	private BinaryData sb4starter;

	public String getSb4StarterFileNameWithoutExt() {
		return sb4StarterFileNameWithoutExt;
	}

	public void setSb4StarterFileNameWithoutExt(String sb4StarterFileNameWithoutExt) {
		this.sb4StarterFileNameWithoutExt = sb4StarterFileNameWithoutExt;
	}

	public SB4StarterWordButton(Supplier<SB4StarterWordViewModel> sb4StarterVMFactoryMethod, String btnText) {
		getElement().setAttribute("download", true);
		setHref(new StreamResource(sb4StarterFileNameWithoutExt, 
				() -> sb4starter.inputStream()));
		btnInner = new Button(btnText);
    btnInner.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    btnInner.setWidthFull();
		btnInner.addClickListener(e -> btnInnerClick(sb4StarterVMFactoryMethod));
		add(btnInner);
	}
	
	private void btnInnerClick(Supplier<SB4StarterWordViewModel> sb4StarterFactoryMethod) {
		SB4StarterWordViewModel viewModel = sb4StarterFactoryMethod.get();
		try {
			sb4starter = viewModel.createSB4Starter();

			new SB4StarterWordUI(viewModel).open();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
