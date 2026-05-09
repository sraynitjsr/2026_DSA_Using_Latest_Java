import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Odd-Even Number Printing with Two Threads
 * 
 * Problem: Two threads print numbers alternately - one prints odd numbers,
 * the other prints even numbers, maintaining the sequence 1,2,3,4,5,6...
 * 
 * This solution demonstrates modern Java concurrency features:
 * - Virtual Threads (JDK 21+)
 * - Records (JDK 16+)
 * - Sealed interfaces (JDK 17+)
 * - Pattern matching (JDK 21+)
 * - ReentrantLock with Conditions
 * - StructuredTaskScope (JDK 21+)
 */
public final class MyOddEvenTwoThreads {

    /**
     * Configuration record for the printer.
     */
    public record PrinterConfig(
        int maxNumber,
        long delayMillis,
        boolean showThreadInfo
    ) {
        public PrinterConfig {
            if (maxNumber <= 0) {
                throw new IllegalArgumentException("maxNumber must be positive");
            }
            if (delayMillis < 0) {
                throw new IllegalArgumentException("delayMillis cannot be negative");
            }
        }

        // Convenience constructor
        public PrinterConfig(int maxNumber) {
            this(maxNumber, 0, false);
        }
    }

    /**
     * Sealed interface for different synchronization strategies.
     */
    public sealed interface SyncStrategy 
        permits WaitNotifyStrategy, ReentrantLockStrategy, SemaphoreStrategy, VirtualThreadStrategy {
        void printOddEven(PrinterConfig config) throws InterruptedException;
        String getName();
        String getDescription();
    }

    /**
     * Strategy 1: Classic wait-notify approach with synchronized blocks.
     * Time: O(n), Space: O(1)
     */
    public static final class WaitNotifyStrategy implements SyncStrategy {
        private final Object lock = new Object();
        private volatile boolean isOddTurn = true;

        @Override
        public void printOddEven(PrinterConfig config) throws InterruptedException {
            var oddThread = Thread.ofPlatform().name("Odd-Thread").start(() -> {
                try {
                    printOdd(config);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            var evenThread = Thread.ofPlatform().name("Even-Thread").start(() -> {
                try {
                    printEven(config);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            oddThread.join();
            evenThread.join();
        }

        private void printOdd(PrinterConfig config) throws InterruptedException {
            for (var i = 1; i <= config.maxNumber(); i += 2) {
                synchronized (lock) {
                    while (!isOddTurn) {
                        lock.wait();
                    }
                    printNumber(i, config);
                    isOddTurn = false;
                    lock.notifyAll();
                }
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        private void printEven(PrinterConfig config) throws InterruptedException {
            for (var i = 2; i <= config.maxNumber(); i += 2) {
                synchronized (lock) {
                    while (isOddTurn) {
                        lock.wait();
                    }
                    printNumber(i, config);
                    isOddTurn = true;
                    lock.notifyAll();
                }
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        @Override
        public String getName() {
            return "Wait-Notify Strategy";
        }

        @Override
        public String getDescription() {
            return "Classic synchronized with wait/notify";
        }
    }

    /**
     * Strategy 2: ReentrantLock with Condition variables.
     * More flexible than synchronized, explicit lock control.
     * Time: O(n), Space: O(1)
     */
    public static final class ReentrantLockStrategy implements SyncStrategy {
        private final Lock lock = new ReentrantLock();
        private final Condition oddCondition = lock.newCondition();
        private final Condition evenCondition = lock.newCondition();
        private volatile boolean isOddTurn = true;

        @Override
        public void printOddEven(PrinterConfig config) throws InterruptedException {
            var oddThread = Thread.ofPlatform().name("Odd-Thread").start(() -> {
                try {
                    printOdd(config);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            var evenThread = Thread.ofPlatform().name("Even-Thread").start(() -> {
                try {
                    printEven(config);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            oddThread.join();
            evenThread.join();
        }

        private void printOdd(PrinterConfig config) throws InterruptedException {
            for (var i = 1; i <= config.maxNumber(); i += 2) {
                lock.lock();
                try {
                    while (!isOddTurn) {
                        oddCondition.await();
                    }
                    printNumber(i, config);
                    isOddTurn = false;
                    evenCondition.signal();
                } finally {
                    lock.unlock();
                }
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        private void printEven(PrinterConfig config) throws InterruptedException {
            for (var i = 2; i <= config.maxNumber(); i += 2) {
                lock.lock();
                try {
                    while (isOddTurn) {
                        evenCondition.await();
                    }
                    printNumber(i, config);
                    isOddTurn = true;
                    oddCondition.signal();
                } finally {
                    lock.unlock();
                }
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        @Override
        public String getName() {
            return "ReentrantLock Strategy";
        }

        @Override
        public String getDescription() {
            return "ReentrantLock with Condition variables";
        }
    }

    /**
     * Strategy 3: Semaphore-based synchronization.
     * Uses binary semaphores to control thread execution order.
     * Time: O(n), Space: O(1)
     */
    public static final class SemaphoreStrategy implements SyncStrategy {
        private final Semaphore oddSemaphore = new Semaphore(1);
        private final Semaphore evenSemaphore = new Semaphore(0);

        @Override
        public void printOddEven(PrinterConfig config) throws InterruptedException {
            var oddThread = Thread.ofPlatform().name("Odd-Thread").start(() -> {
                try {
                    printOdd(config);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            var evenThread = Thread.ofPlatform().name("Even-Thread").start(() -> {
                try {
                    printEven(config);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            oddThread.join();
            evenThread.join();
        }

        private void printOdd(PrinterConfig config) throws InterruptedException {
            for (var i = 1; i <= config.maxNumber(); i += 2) {
                oddSemaphore.acquire();
                printNumber(i, config);
                evenSemaphore.release();
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        private void printEven(PrinterConfig config) throws InterruptedException {
            for (var i = 2; i <= config.maxNumber(); i += 2) {
                evenSemaphore.acquire();
                printNumber(i, config);
                oddSemaphore.release();
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        @Override
        public String getName() {
            return "Semaphore Strategy";
        }

        @Override
        public String getDescription() {
            return "Binary semaphores for thread coordination";
        }
    }

    /**
     * Strategy 4: Virtual Threads (Java 21+) with ExecutorService.
     * Lightweight threads with structured concurrency.
     * Time: O(n), Space: O(1)
     */
    public static final class VirtualThreadStrategy implements SyncStrategy {
        private final Object lock = new Object();
        private volatile boolean isOddTurn = true;

        @Override
        public void printOddEven(PrinterConfig config) throws InterruptedException {
            // Using virtual threads for lightweight concurrency
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                var oddFuture = executor.submit(() -> {
                    try {
                        printOdd(config);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                });

                var evenFuture = executor.submit(() -> {
                    try {
                        printEven(config);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                });

                // Wait for both tasks to complete
                try {
                    oddFuture.get();
                    evenFuture.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Task execution failed", e);
                }
            }
        }

        private void printOdd(PrinterConfig config) throws InterruptedException {
            for (var i = 1; i <= config.maxNumber(); i += 2) {
                synchronized (lock) {
                    while (!isOddTurn) {
                        lock.wait();
                    }
                    printNumber(i, config);
                    isOddTurn = false;
                    lock.notifyAll();
                }
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        private void printEven(PrinterConfig config) throws InterruptedException {
            for (var i = 2; i <= config.maxNumber(); i += 2) {
                synchronized (lock) {
                    while (isOddTurn) {
                        lock.wait();
                    }
                    printNumber(i, config);
                    isOddTurn = true;
                    lock.notifyAll();
                }
                if (config.delayMillis() > 0) {
                    Thread.sleep(config.delayMillis());
                }
            }
        }

        @Override
        public String getName() {
            return "Virtual Threads Strategy";
        }

        @Override
        public String getDescription() {
            return "Java 21+ Virtual Threads with ExecutorService";
        }
    }

    /**
     * Helper method to print a number with optional thread information.
     */
    private static void printNumber(int num, PrinterConfig config) {
        if (config.showThreadInfo()) {
            System.out.printf("[%s] %d%n", 
                Thread.currentThread().getName(), num);
        } else {
            System.out.print(num + " ");
        }
    }

    /**
     * Analyzes a strategy using pattern matching (Java 21+).
     */
    private static String analyzeStrategy(SyncStrategy strategy) {
        return switch (strategy) {
            case WaitNotifyStrategy _ -> """
                Wait-Notify Strategy:
                  Mechanism: synchronized + wait/notify
                  Pros: Simple, built-in language support
                  Cons: Less flexible, must hold monitor lock
                  Best for: Simple synchronization scenarios
                """;
            case ReentrantLockStrategy _ -> """
                ReentrantLock Strategy:
                  Mechanism: Explicit Lock + Condition variables
                  Pros: Timeout support, fairness policy, tryLock
                  Cons: More verbose, manual lock management
                  Best for: Complex synchronization, need for fairness
                """;
            case SemaphoreStrategy _ -> """
                Semaphore Strategy:
                  Mechanism: Binary semaphores (0 or 1 permits)
                  Pros: Clear signaling semantics, simple logic
                  Cons: Limited to permit-based coordination
                  Best for: Producer-consumer, resource pools
                """;
            case VirtualThreadStrategy _ -> """
                Virtual Threads Strategy:
                  Mechanism: Lightweight threads (JDK 21+)
                  Pros: Scalable, millions of threads possible, low overhead
                  Cons: Requires Java 21+
                  Best for: High-concurrency applications, I/O-bound tasks
                """;
        };
    }

    /**
     * Main demonstration method.
     */
    public static void executeDemo() {
        System.out.println("=== Odd-Even Two Threads Demo ===\n");

        // Test configurations
        var configs = List.of(
            new PrinterConfig(20, 0, false),
            new PrinterConfig(10, 50, true)
        );

        // All strategies
        var strategies = List.<SyncStrategy>of(
            new WaitNotifyStrategy(),
            new ReentrantLockStrategy(),
            new SemaphoreStrategy(),
            new VirtualThreadStrategy()
        );

        // Strategy analysis
        System.out.println("Strategy Comparison:\n");
        strategies.forEach(strategy -> {
            System.out.println(strategy.getName() + ":");
            System.out.println(analyzeStrategy(strategy));
        });

        // Run demonstrations
        for (var i = 0; i < configs.size(); i++) {
            var config = configs.get(i);
            System.out.println("=".repeat(60));
            System.out.println("Configuration #%d: max=%d, delay=%dms, showThread=%b"
                .formatted(i + 1, config.maxNumber(), 
                    config.delayMillis(), config.showThreadInfo()));
            System.out.println("=".repeat(60));

            for (var strategy : strategies) {
                System.out.println("\n" + strategy.getName() + ":");
                
                var startTime = System.nanoTime();
                try {
                    strategy.printOddEven(config);
                } catch (InterruptedException e) {
                    System.err.println("Interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
                var duration = (System.nanoTime() - startTime) / 1_000_000.0;
                
                if (!config.showThreadInfo()) {
                    System.out.println(); // New line after numbers
                }
                System.out.println("[Completed in %.2f ms]\n".formatted(duration));
            }
        }

        // Additional demonstrations
        demonstrateThreadStates();
        demonstratePerformanceComparison();
    }

    /**
     * Demonstrates thread state transitions during execution.
     */
    private static void demonstrateThreadStates() {
        System.out.println("\n=== Thread State Demonstration ===\n");

        var config = new PrinterConfig(6, 100, true);
        var strategy = new WaitNotifyStrategy();

        System.out.println("Watching thread states with 100ms delays...\n");
        
        try {
            strategy.printOddEven(config);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Performance comparison across strategies.
     */
    private static void demonstratePerformanceComparison() {
        System.out.println("\n=== Performance Comparison ===\n");

        var config = new PrinterConfig(1000, 0, false);
        var strategies = List.<SyncStrategy>of(
            new WaitNotifyStrategy(),
            new ReentrantLockStrategy(),
            new SemaphoreStrategy(),
            new VirtualThreadStrategy()
        );

        System.out.println("Running each strategy with 1000 numbers...\n");

        var results = new ArrayList<PerformanceResult>();

        for (var strategy : strategies) {
            var startTime = System.nanoTime();
            try {
                strategy.printOddEven(config);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            var duration = (System.nanoTime() - startTime) / 1_000_000.0;
            
            results.add(new PerformanceResult(strategy.getName(), duration));
            System.out.println(); // New line after output
        }

        // Display results
        System.out.println("\nPerformance Results:");
        results.stream()
            .sorted(Comparator.comparing(PerformanceResult::durationMs))
            .forEach(result -> 
                System.out.println("  %-30s : %8.2f ms"
                    .formatted(result.strategyName(), result.durationMs()))
            );

        // Find fastest
        var fastest = results.stream()
            .min(Comparator.comparing(PerformanceResult::durationMs))
            .orElseThrow();

        System.out.println("\nFastest: %s (%.2f ms)"
            .formatted(fastest.strategyName(), fastest.durationMs()));
    }

    /**
     * Record for performance measurement results.
     */
    private record PerformanceResult(String strategyName, double durationMs) {
        PerformanceResult {
            Objects.requireNonNull(strategyName, "Strategy name cannot be null");
            if (durationMs < 0) {
                throw new IllegalArgumentException("Duration must be non-negative");
            }
        }
    }
}
