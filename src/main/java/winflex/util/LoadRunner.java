package winflex.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * 压测小工具
 * 
 * <pre>
 * LoadRunner lr = LoadRunner.builder()
 * .millis(60000) // 运行时长
 * .threads(30, 3, 5) // 30个线程，每3秒启动5个线程
 * .action(() -> return action()) // 事务
 * .build();
 * lr.start();
 * ...
 * lr.incrementThreads(1);
 * ...
 * lr.decrementThreads(1);
 * ...
 * lr.join();
 * </pre>
 * 
 * @author winflex
 */
public class LoadRunner {

	static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static LoadRunnberBuilder builder() {
		return new LoadRunnberBuilder();
	}

	private final Config config;
	private final LinkedList<Worker> workers = new LinkedList<>();
	private final Reporter reporter;

	private final StartSemaphore startSemaphore;

	private volatile Status status = Status.NOT_STARTED;

	public LoadRunner(Config config) {
		this.config = config;
		this.reporter = new Reporter(config);
		if (config.duration > 0 && config.threadsEachDuration > 0) {
			startSemaphore = new GraduallyStartSemaphore(config.duration, config.threadsEachDuration);
		} else {
			startSemaphore = new TogetherStartSemaphore();
		}

		for (int i = 0; i < config.threads; i++) {
			workers.add(new Worker(config, startSemaphore, reporter));
		}
	}

	public synchronized LoadRunner start() {
		if (status == Status.STARTED) {
			return this;
		}
		if (status == Status.STOPPED) {
			throw new IllegalStateException("status: " + status);
		}

		workers.forEach(w -> w.start());
		startSemaphore.start();
		reporter.start();

		// schedule stop task
		new Thread(() -> {
			sleepQuietly(config.millis);
			LoadRunner.this.stop();
		}).start();

		status = Status.STARTED;

		return this;
	}

	public synchronized LoadRunner stop() {
		if (status == Status.STOPPED) {
			return this;
		}
		if (status == Status.NOT_STARTED) {
			throw new IllegalStateException("status: " + status);
		}

		reporter.stop();
		workers.forEach(w -> w.stop());
		return this;
	}

	public LoadRunner incrementThreads(int threads) {
		if (status != Status.STARTED) {
			throw new IllegalStateException("status: " + status);
		}
		for (int i = 0; i < threads; i++) {
			Worker worker = new Worker(config, startSemaphore, reporter);
			workers.add(worker);
			worker.start();
		}
		return this;
	}

	public LoadRunner decrementThreads(int threads) {
		if (status != Status.STARTED) {
			throw new IllegalStateException("status: " + status);
		}
		if (threads > workers.size()) {
			throw new IllegalArgumentException(
					"threads [" + threads + "] is larger than total workers[" + workers.size() + "]");
		}

		for (int i = 0; i < threads; i++) {
			workers.removeFirst().stop();
		}
		return this;
	}

	public LoadRunner join() throws InterruptedException {
		for (Worker w : workers) {
			w.join();
		}
		reporter.join();
		return this;
	}

	private static boolean sleepQuietly(long millis) {
		try {
			Thread.sleep(millis);
			return false;
		} catch (InterruptedException e) {
			Thread.interrupted();
			return true;
		}
	}

	static final class Reporter {
		static final NamedThreadFactory tf = new NamedThreadFactory("LoadRunner-Reporter", true);
		
		final Config config;
		final Thread thread;
		final LongAdder successCounter = new LongAdder();
		final LongAdder failureCounter = new LongAdder();
		final LongAdder errorCounter = new LongAdder();
		long lastCount;
		volatile boolean stopped;

		Reporter(Config config) {
			this.config = config;
			this.thread = tf.newThread(() -> run());
		}

		void actionDone(boolean success) {
			if (success) {
				successCounter.increment();
			} else {
				failureCounter.increment();
			}
		}

		void actionError(Throwable e) {
			errorCounter.increment();
		}

		long succeedTransactions() {
			return successCounter.longValue();
		}

		long failureTransactions() {
			return failureCounter.longValue();
		}

		private void run() {
			final PrintStream out = new PrintStream(config.reportOutputStream);
			final long interval = config.reportInterval;
			final long intervalSecs = interval / 1000;
			while (!stopped) {
				if (sleepQuietly(interval)) {
					break;
				}

				long successCount = successCounter.sum();
				long failureCount = failureCounter.sum();
				long tps = (successCount - lastCount) / intervalSecs;

				out.printf("[%s] tps = %10d, success = %10d, failure = %10d, error = %10d\n", format.format(new Date()),
						tps, successCount, failureCount, errorCounter.sum());
				lastCount = successCount;
			}
		}

		final void start() {
			thread.start();
		}

		final void stop() {
			stopped = true;
			thread.interrupt();
		}

		final void join() throws InterruptedException {
			thread.join();
		}
	}

	static final class Worker {

		final Thread thread;
		final Config config;
		final Reporter reporter;

		StartSemaphore startSemaphore;

		volatile boolean stopped;

		Worker(Config config, StartSemaphore startSemaphore, Reporter reporter) {
			this.config = config;
			this.startSemaphore = startSemaphore;
			this.reporter = reporter;
			this.thread = config.threadFactory.newThread(() -> run());
		}

		private void run() {
			try {
				startSemaphore.acquire();
			} catch (InterruptedException e) {
			}
			System.out.printf("[%s] Worker %s started\n", format.format(new Date()), thread.getName());
			final Callable<Boolean> transaction = config.action;
			final boolean stopWhenError = config.stopWhenError;
			final Reporter stater = reporter;
			while (!stopped) {
				try {
					boolean success = transaction.call();
					stater.actionDone(success);
				} catch (Throwable e) {
					stater.actionError(e);
					if (stopWhenError) {
						stopped = true;
					}
				}
			}
			System.out.printf("[%s] Worker %s stopped\n", format.format(new Date()), thread.getName());
		}

		final void start() {
			thread.start();
		}

		final void stop() {
			this.stopped = true;
		}

		final void join() throws InterruptedException {
			thread.join();
		}
	}

	public static final class LoadRunnberBuilder {
		private final Config config = new Config();

		public LoadRunner build() {
			return new LoadRunner(config.validate());
		}

		/**
		 * 指定使用几个线程
		 */
		public LoadRunnberBuilder threads(int threads) {
			config.threads = threads;
			return this;
		}

		/**
		 * 指定使用几个线程，同时指定每durationSecs秒启动threadsEachDuration线程
		 */
		public LoadRunnberBuilder threads(int threads, int durationSecs, int threadsEachDuration) {
			config.threads = threads;
			config.duration = durationSecs;
			config.threadsEachDuration = threadsEachDuration;
			return this;
		}

		/**
		 * 指定事务，若action.call() == true，则事务成功，否则事务失败
		 */
		public LoadRunnberBuilder action(Callable<Boolean> action) {
			config.action = action;
			return this;
		}

		/**
		 * 指定运行时间，单位毫秒
		 */
		public LoadRunnberBuilder millis(long millis) {
			config.millis = millis;
			return this;
		}

		public LoadRunnberBuilder stopWhenError() {
			config.stopWhenError = true;
			return this;
		}

		/**
		 * 指定汇报间隔，单位毫秒，默认1000
		 */
		public LoadRunnberBuilder reportInterval(long reportInterval) {
			config.reportInterval = reportInterval;
			return this;
		}

		/**
		 * 指定汇报目标输出流，默认System.out
		 */
		public LoadRunnberBuilder reportTo(OutputStream out) {
			config.reportOutputStream = out;
			return this;
		}

		/**
		 * 指定线程工厂
		 */
		public LoadRunnberBuilder threadFactory(ThreadFactory tf) {
			config.threadFactory = tf;
			return this;
		}
	}

	static final class Config {
		int threads;
		int duration;
		int threadsEachDuration;
		ThreadFactory threadFactory;
		Callable<Boolean> action;
		long millis;
		boolean stopWhenError;
		long reportInterval;
		OutputStream reportOutputStream;

		Config validate() {
			if (millis <= 0) {
				throw new IllegalArgumentException("millis is required");
			}

			if (threads <= 0) {
				throw new IllegalArgumentException("threads is required");
			}

			if (action == null) {
				throw new IllegalArgumentException("transaction is required");
			}

			if (threadFactory == null) {
				threadFactory = new NamedThreadFactory("LoadRunner-Worker");
			}

			if (reportInterval <= 1000) {
				reportInterval = 1000;
			}

			if (reportOutputStream == null) {
				reportOutputStream = System.out;
			}
			return this;
		}
	}

	static abstract class StartSemaphore {

		protected final AtomicInteger totalThreads = new AtomicInteger(); // 总线程数

		void acquire() throws InterruptedException {
			totalThreads.incrementAndGet();
			acquire0();
		}

		protected abstract void acquire0() throws InterruptedException;

		void start() {
		}

		void stop() {
		}
	}

	// 一起启动所有线程
	static final class TogetherStartSemaphore extends StartSemaphore {

		static final NamedThreadFactory tf = new NamedThreadFactory("TogetherStartSemaphore-Controller", true);

		final Semaphore semaphore;
		final Thread thread;
		int releasedCount;

		TogetherStartSemaphore() {
			this.semaphore = new Semaphore(0);
			this.thread = tf.newThread(() -> {
				final long millis = 1000;
				while (true) {
					if (sleepQuietly(millis)) {
						break;
					}
					int releaseCount = totalThreads.get() - releasedCount;
					if (releaseCount > 0) {
						semaphore.release(releaseCount);
						releasedCount += releaseCount;
					}
				}
			});
		}

		@Override
		protected void acquire0() throws InterruptedException {
			semaphore.acquire();
		}
		
		@Override
		void start() {
			thread.start();
		}
	}

	// 逐渐启动线程
	static final class GraduallyStartSemaphore extends StartSemaphore {

		static final NamedThreadFactory tf = new NamedThreadFactory("GraduallyStartSemaphore-Controller", true);

		final Semaphore semaphore;
		final Thread thread;
		int releasedCount;

		// 每seconds秒增加count个线程
		GraduallyStartSemaphore(final int seconds, final int count) {
			this.semaphore = new Semaphore(0);
			this.thread = tf.newThread(() -> {
				final long millis = seconds * 1000;
				while (true) {
					if (sleepQuietly(millis)) {
						break;
					}
					int left = totalThreads.get() - releasedCount;
					if (left > 0) {
						int releaseCount = left >= count ? count : left;
						semaphore.release(releaseCount);
						releasedCount += releaseCount;
					}
				}
			});
		}

		@Override
		protected void acquire0() throws InterruptedException {
			semaphore.acquire();
		}

		@Override
		void start() {
			thread.start();
		}
	}

	static enum Status {
		NOT_STARTED, STARTED, STOPPED;
	}
}
