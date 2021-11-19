package org.smartbit4all.core.object;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * This interface is used when an ObservableObject publishes its changes. It is used when publishing
 * to a UI, and for example publish should happen on the UI thread. The implemented consumer can
 * make sure it happens according to the current context.
 * 
 * @author Attila Mate
 *
 */
public interface ObservablePublisherWrapper extends Consumer<Runnable> {

}
