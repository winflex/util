package winflex.util;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	private final String NAME_PREFIX;
	private final ThreadGroup GROUP;
	private final boolean DAEMON;
	private final AtomicInteger threadIdGenerator = new AtomicInteger(0);

	public NamedThreadFactory() {
		this("Default-Thread", false);
	}

	public NamedThreadFactory(String namePrefix) {
		this(namePrefix, false);
	}

	public NamedThreadFactory(String namePrefix, boolean daemon) {
		this.NAME_PREFIX = Objects.requireNonNull(namePrefix, "namePrefix is required");
		this.DAEMON = daemon;
		SecurityManager s = System.getSecurityManager();
		GROUP = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	@Override
	public Thread newThread(Runnable r) {
		String name = NAME_PREFIX + "-" + threadIdGenerator.getAndIncrement();
		Thread thread = new Thread(GROUP, r, name, 0);
		if (thread.isDaemon() != DAEMON) {
			thread.setDaemon(DAEMON);
		}
		return thread;
	}
}

