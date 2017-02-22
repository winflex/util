package cc.lixiaohui.util.future;

public interface IFutureListener<V> {
	
	void operationCompleted(IFuture<V> future) throws Exception;
	
}
