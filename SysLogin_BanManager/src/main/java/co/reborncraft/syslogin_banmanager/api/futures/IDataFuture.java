package co.reborncraft.syslogin_banmanager.api.futures;

import java.util.function.Consumer;

public interface IDataFuture extends IFuture {
	default <T> void complete(Consumer<T> runOnComplete, T state) {
		if (runOnComplete != null) {
			runOnComplete.accept(state);
		}
	}
}
