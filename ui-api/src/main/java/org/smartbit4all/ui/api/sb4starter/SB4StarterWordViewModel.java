package org.smartbit4all.ui.api.sb4starter;

import java.util.function.BiConsumer;

import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.core.object.ObjectEditing;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;

public interface SB4StarterWordViewModel extends ObjectEditing {

	ObservableObject sb4Starter();
	
	BinaryData createSB4Starter() throws Exception;
	
	void setSb4StarterFormModel(SB4StarterWordFormModel sb4StarterWordFormModel, BiConsumer<BinaryContent, BinaryContent> acceptHandler);
	
	void accept();
	
	void close();
}
