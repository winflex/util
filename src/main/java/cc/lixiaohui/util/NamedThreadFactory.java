package cc.lixiaohui.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可命名的线程工厂
 * 
 * @author lixiaohui
 * @date 2016年11月8日 下午9:22:14
 */
public class NamedThreadFactory implements ThreadFactory {

	private String namePrefix;

	private final ThreadGroup GROUP;

	private final boolean DAEMON;

	private final AtomicInteger THREAD_NUMBER = new AtomicInteger(0);

	public NamedThreadFactory() {
		this("Default-Thread", false);
	}

	/**
	 * @param namePrefix 线程名前缀
	 */
	public NamedThreadFactory(String namePrefix) {
		this(namePrefix, false);
	}

	/**
	 * 
	 * @param namePrefix 线程名前缀
	 * @param daemon 是否daemon线程
	 */
	public NamedThreadFactory(String namePrefix, boolean daemon) {
		if (namePrefix != null) {
			this.namePrefix = namePrefix;
		}
		this.DAEMON = daemon;
		SecurityManager s = System.getSecurityManager();
		GROUP = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		String name = namePrefix + "-" + THREAD_NUMBER.getAndIncrement();
		Thread thread = new Thread(GROUP, r, name, 0);
		if (thread.isDaemon() != DAEMON) {
			thread.setDaemon(DAEMON);
		}
		return thread;
	}

}
