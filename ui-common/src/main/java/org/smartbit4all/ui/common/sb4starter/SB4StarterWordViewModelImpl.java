package org.smartbit4all.ui.common.sb4starter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import org.smartbit4all.api.binarydata.BinaryContent;
import org.smartbit4all.api.binarydata.BinaryData;
import org.smartbit4all.api.contentaccess.ContentAccessApi;
import org.smartbit4all.api.sb4starter.bean.CommandKind;
import org.smartbit4all.api.sb4starter.bean.SB4Command;
import org.smartbit4all.api.sb4starter.bean.SB4File;
import org.smartbit4all.api.sb4starter.bean.SB4Starter;
import org.smartbit4all.core.object.ObjectEditingImpl;
import org.smartbit4all.core.object.ObservableObject;
import org.smartbit4all.core.object.ObservableObjectImpl;
import org.smartbit4all.gson.GsonBinaryData;
import org.smartbit4all.ui.api.sb4starter.SB4StarterWordViewModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordFormModel;
import org.smartbit4all.ui.api.sb4starterui.model.SB4StarterWordState;
import org.springframework.beans.factory.annotation.Autowired;
import io.reactivex.rxjava3.disposables.Disposable;

public class SB4StarterWordViewModelImpl extends ObjectEditingImpl implements SB4StarterWordViewModel {

	protected ObservableObject sb4Starter = new ObservableObjectImpl();
	
	private String contentAccessApiBaseUrl;
	
	private BiConsumer<BinaryContent, BinaryContent> acceptHandler;
	
	private SB4StarterWordFormModel sb4StarterWordFormModel;
	
	private List<Disposable> eventListeners = new ArrayList<>();
	
	@Autowired
	ContentAccessApi contentAccessApi;

	public SB4StarterWordViewModelImpl(String contentAccessApiBaseUrl) {
		this.contentAccessApiBaseUrl = contentAccessApiBaseUrl;
	}
	
	@Override
	public ObservableObject sb4Starter() {
		return sb4Starter;
	}

	@Override
	public BinaryData createSB4Starter() throws Exception {
		UUID wordToEditId = contentAccessApi.share(sb4StarterWordFormModel.getStartContent());
		Disposable downloadListener = contentAccessApi.subscribeToContentAccessEvent(wordToEditId, contentAccess  -> sb4StarterWordFormModel.setState(SB4StarterWordState.EDIT));
		eventListeners.add(downloadListener);
		
		UUID editedWordId = contentAccessApi.share(sb4StarterWordFormModel.getResultContent());
		Disposable uploadListener =contentAccessApi.subscribeToContentAccessEvent(editedWordId, contentAccess  -> sb4StarterWordFormModel.setState(SB4StarterWordState.UPLOADED));
		eventListeners.add(uploadListener);
		
		String wordFileName = sb4StarterWordFormModel.getStartContent().getFileName();
		BinaryData sb4Starter = createSB4Starter(wordToEditId, wordFileName, editedWordId);
		
		return sb4Starter;
	}
	
	private BinaryData createSB4Starter(UUID wordToEditId, String wordFileName, UUID editedWordId) throws IOException, Exception {
		SB4File wordFileToEdit = new SB4File().filename(wordFileName).id(wordToEditId);
		SB4File editedWordFile = new SB4File().filename(wordFileName).id(editedWordId);
		
		URI uri = URI.create(contentAccessApiBaseUrl);
		
		SB4Command downloadCommand = new SB4Command().commandKind(CommandKind.CONTENTACCESSDOWNLOAD).id(UUID.randomUUID())
				.restUrl(uri).command("").sb4Files(Arrays.asList(wordFileToEdit));

		SB4Command wordEditCommand = new SB4Command().commandKind(CommandKind.WORDEDIT).id(UUID.randomUUID())
				.restUrl(uri).command("").sb4Files(Arrays.asList(wordFileToEdit));

		SB4Command uploadCommand = new SB4Command().commandKind(CommandKind.CONTENTACCESSUPLOAD).id(UUID.randomUUID())
				.restUrl(uri).command("").sb4Files(Arrays.asList(editedWordFile));

		SB4Starter starter =  new SB4Starter().id(UUID.randomUUID())
				.commands(Arrays.asList(downloadCommand, wordEditCommand, uploadCommand));
		
		return GsonBinaryData.toJsonBinaryData(starter, SB4Starter.class);
	}

	@Override
	public void setSb4StarterFormModel(SB4StarterWordFormModel sb4StarterWordFormModel,
			BiConsumer<BinaryContent, BinaryContent> acceptHandler) {
		this.sb4StarterWordFormModel = sb4StarterWordFormModel;
		this.acceptHandler = acceptHandler;
	}

	@Override
	public void accept() {
		acceptHandler.accept(sb4StarterWordFormModel.getStartContent(), sb4StarterWordFormModel.getResultContent());
		close();
	}

	@Override
	public void close() {
		eventListeners.forEach(listener -> listener.dispose());
		
	}

}
