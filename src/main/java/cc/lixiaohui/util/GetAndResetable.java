package cc.lixiaohui.util;

/**
 * @author lixiaohui
 * @date 2016年10月27日 下午1:56:35
 * 
 */
public interface GetAndResetable<T> extends Gettable<T>, Resetable {
	T getAndReset();
}
