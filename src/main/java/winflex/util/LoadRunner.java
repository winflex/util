package winflex.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.LongAdder;

/**
 * 压测小工具
 * 
 * <pre>
 * LoadRunner lr = LoadRunner.builder().millis(60000).threads(1).transaction(() -> transaction()).build();
 * lr.start();
 * ...
 * lr.incrementThreads(1);
 * ...
 * lr.decrementThreads(1);
 * ...
 * lr.stop();
 * </pre>
 * 
 * @author winflex
 */
public class LoadRunner {

	public static LoadRunnberBuilder builder() {
		return new LoadRunnberBuilder();
	}

	private final Config config;
	private final LinkedList<Worker> workers = new LinkedList<>();
	private final Reporter reporter = new Reporter();

	private final CountDownLatch startLatch = new CountDownLatch(1);

	private volatile boolean running = false;

	public LoadRunner(Config config) {
		this.config = config;

		for (int i = 0; i < config.threads; i++) {
			workers.add(new Worker(config.threadFactory));
		}
	}

	/**
	 * 开始
	 */
	public synchronized void start() {
		if (running) {
			return;
		}

		for (Worker w : workers) {
			w.start();
		}
		startLatch.countDown(); // start together

		reporter.start();

		// schedule stop
		new Thread(() -> {
			sleepQuietly(config.millis);
			LoadRunner.this.stop();
		}).start();
		running = true;
	}

	/**
	 * 停止
	 */
	public void stop() {
		if (running) {
			reporter.stop();
			workers.forEach(w -> w.stop());
		}
	}

	/**
	 * 增加线程
	 */
	public void incrementThreads(int threads) {
		if (!running) {
			throw new IllegalStateException("not started yet!");
		}
		for (int i = 0; i < threads; i++) {
			Worker worker = new Worker(config.threadFactory);
			workers.add(worker);
			worker.start();
		}
	}

	/**
	 * 减少线程
	 */
	public void decrementThreads(int threads) {
		if (!running) {
			throw new IllegalStateException("not started yet!");
		}
		if (threads > workers.size()) {
			throw new IllegalArgumentException(
					"threads [" + threads + "] is larger than total workers[" + workers.size() + "]");
		}

		for (int i = 0; i < threads; i++) {
			workers.removeFirst().stop();
		}
	}

	static void sleepQuietly(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

	final class Reporter {
		final Thread thread;
		final LongAdder successCounter = new LongAdder();
		final LongAdder failureCounter = new LongAdder();
		long lastCount;
		volatile boolean stopped;

		Reporter() {
			this.thread = new Thread(() -> run(), "LoadRunner-Reporter");
		}

		void transactionDone(boolean success) {
			if (success) {
				successCounter.increment();
			} else {
				failureCounter.increment();
			}
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
			while (!stopped) {
				sleepQuietly(interval);
				long successCount = successCounter.sum();
				long failureCount = failureCounter.sum();
				long tps = successCount - lastCount;

				out.printf("tps = %10d, success = %10d, failure = %10d\n", tps, successCount, failureCount);
				lastCount = successCount;
			}
		}

		void start() {
			thread.start();
		}

		void stop() {
			stopped = true;
			thread.interrupt();
		}
	}

	final class Worker {

		final Thread thread;
		volatile boolean stopped;

		Worker(ThreadFactory tf) {
			this.thread = tf.newThread(() -> run());
		}

		private void run() {
			try {
				startLatch.await();
			} catch (InterruptedException e1) {
			}
			System.out.println("Worker " + thread.getName() + " started");
			final Runnable transaction = config.transaction;
			final boolean stopWhenError = config.stopWhenError;
			final Reporter stater = LoadRunner.this.reporter;
			while (!stopped) {
				try {
					transaction.run();
					stater.transactionDone(true);
				} catch (Throwable e) {
					stater.transactionDone(false);
					if (stopWhenError) {
						stopped = true;
					}
				}
			}
			System.out.println("Worker " + thread.getName() + " stopped");
		}

		final void start() {
			thread.start();
		}

		final void stop() {
			this.stopped = true;
		}
	}

	public static final class LoadRunnberBuilder {
		private final Config config = new Config();

		public LoadRunner build() {
			return new LoadRunner(config.validate());
		}

		public LoadRunnberBuilder threads(int threads) {
			config.threads = threads;
			return this;
		}

		public LoadRunnberBuilder transaction(Runnable transaction) {
			config.transaction = transaction;
			return this;
		}

		public LoadRunnberBuilder millis(long millis) {
			config.millis = millis;
			return this;
		}

		public LoadRunnberBuilder stopWhenError(boolean stopWhenError) {
			config.stopWhenError = stopWhenError;
			return this;
		}

		public LoadRunnberBuilder reportInterval(long reportInterval) {
			config.reportInterval = reportInterval;
			return this;
		}

		public LoadRunnberBuilder threadFactory(ThreadFactory tf) {
			config.threadFactory = tf;
			return this;
		}
	}

	static final class Config {
		int threads;
		ThreadFactory threadFactory;
		Runnable transaction;
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

			if (transaction == null) {
				throw new IllegalArgumentException("transsaction is required");
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
}
