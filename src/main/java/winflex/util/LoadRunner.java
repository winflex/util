package winflex.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

public class LoadRunner {
    
    public static LoadRunnberBuilder builder() {
        return new LoadRunnberBuilder();
    }
    
    private final Config config;
    private final Worker[] workers;
    private final Reporter reporter;
    
    private final CountDownLatch startLatch;
    private final CountDownLatch endLatch;
    
    private volatile boolean stopped;
    
    public LoadRunner(Config config) {
        this.config = config;
        this.reporter = new Reporter();
        this.workers = new Worker[config.threads];

        this.startLatch = new CountDownLatch(1);
        this.endLatch = new CountDownLatch(workers.length);
        
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker();
        }
    }

    public void run() {
        for (Worker w : workers) {
            w.start();
        }
        startLatch.countDown(); // start together
        
        reporter.start();
        
        sleepQuietly(config.millis);
        stopped = true;
        try {
            endLatch.await();
        } catch (InterruptedException e) {
        }
    }
    
    static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }
    
    final class Reporter extends Thread {
        final LongAdder successCounter = new LongAdder();
        final LongAdder failureCounter = new LongAdder();
        long lastCount; 
        
        final PrintStream out;
        
        Reporter() {
            this.out = new PrintStream(config.reportOutputStream);
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

        @Override
        public void run() {
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
    }

    final class Worker extends Thread {

        @Override
        public void run() {
            try {
                startLatch.await();
            } catch (InterruptedException e1) {
            }
            
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
            endLatch.countDown();
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
    }

    static final class Config {
        int threads;
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
