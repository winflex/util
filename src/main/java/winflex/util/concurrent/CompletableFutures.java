package winflex.util.concurrent;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import winflex.util.NamedThreadFactory;

/**
 * 提供一系列创建{@link CompletableFuture}的静态方法, 这些方法都带有timeout超时参数,
 * 通过这些静态方法创建的{@link CompletableFuture}将会在指定timeout时以{@link TimeoutException}异常结束
 * 
 * 
 * @author winflex
 */
public final class CompletableFutures {

	private static final ScheduledExecutorService scheduler = Executors
			.newSingleThreadScheduledExecutor(new NamedThreadFactory("CompletableFuture-Watchdog", true));

	public static <U> CompletableFuture<U> fresh(Duration duration) {
		return scheduleTimeout(new CompletableFuture<U>(), duration);
	}

	public static CompletableFuture<Void> runAsync(Runnable runnable, Duration duration) {
		return scheduleTimeout(CompletableFuture.runAsync(runnable), duration);
	}

	public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor, Duration duration) {
		return scheduleTimeout(CompletableFuture.runAsync(runnable, executor), duration);
	}

	public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Duration duration) {
		return scheduleTimeout(CompletableFuture.supplyAsync(supplier), duration);
	}

	public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor, Duration duration) {
		return scheduleTimeout(CompletableFuture.supplyAsync(supplier, executor), duration);
	}

	public static CompletableFuture<Void> allOf(Duration duration, CompletableFuture<?>... cfs) {
		return scheduleTimeout(CompletableFuture.allOf(cfs), duration);
	}

	public static CompletableFuture<Object> anyOf(Duration duration, CompletableFuture<?>... cfs) {
		return scheduleTimeout(CompletableFuture.anyOf(cfs), duration);
	}

	private static <U> CompletableFuture<U> scheduleTimeout(CompletableFuture<U> future, Duration duration) {
		final long millis = duration.toMillis();
		scheduler.schedule(() -> {
			if (!future.isDone()) {
				future.completeExceptionally(new TimeoutException("Future timed out after " + millis + " ms"));
			}
		}, millis, TimeUnit.MILLISECONDS);
		return future;
	}

	private CompletableFutures() {
	}
}
