import java.util.List;

/**
 * Main entry point for DSA demonstrations using Latest Java Features.
 * Demonstrates Java 21+ features including:
 * - Virtual threads (JDK 21)
 * - Pattern matching
 * - Text blocks
 * - Records
 * - Sealed types
 * - Enhanced switch expressions
 */
public final class MyMain {

    private static final String SEPARATOR = "=".repeat(60);

    public static void main(String[] args) {
        System.out.println("""
            ╔════════════════════════════════════════════════════════════╗
            ║     DSA Using Latest Java Features (Java 21+)              ║
            ║     Modern Java Syntax and Best Practices                  ║
            ╚════════════════════════════════════════════════════════════╝
            """);

        // Using try-with-resources pattern for better error handling
        try {
            runAllDemos();
        } catch (Exception e) {
            System.err.println("Error occurred during demo execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Executes all demonstration modules sequentially.
     * Uses enhanced switch expressions and pattern matching.
     */
    private static void runAllDemos() {
        var demos = List.of(
            new DemoTask("GetChars Demo (JDK 25)", MyGetCharsDemo::executeDemo),
            new DemoTask("Two Sum Demo", () -> {
                TwoSumDemo.executeDemo();
                TwoSumDemo.demonstrateFunctionalApproach();
            }),
            new DemoTask("Group Anagrams Demo", GroupAnagramsDemo::executeDemo),
            new DemoTask("Remove K Digits Demo", MyRemoveKdigits::executeDemo),
            new DemoTask("Contains Nearby Almost Duplicate", () -> {
                new ContainsNearbyAlmostDuplicate().start();
            })
        );

        // Execute each demo with timing information
        demos.forEach(MyMain::executeDemo);

        System.out.println("\n" + SEPARATOR);
        System.out.println("All demonstrations completed successfully!");
        System.out.println(SEPARATOR);
    }

    /**
     * Executes a single demo task with timing and error handling.
     */
    private static void executeDemo(DemoTask task) {
        System.out.println("\n" + SEPARATOR);
        System.out.println("Executing: " + task.name());
        System.out.println(SEPARATOR + "\n");

        var startTime = System.nanoTime();
        
        try {
            task.runner().run();
        } catch (Exception e) {
            System.err.println("Error in " + task.name() + ": " + e.getMessage());
        }
        
        var duration = System.nanoTime() - startTime;
        System.out.println("\n[Completed in %,.2f ms]".formatted(duration / 1_000_000.0));
    }

    /**
     * Alternative implementation using virtual threads (Java 21+) for parallel execution.
     * This can significantly speed up independent demo executions.
     */
    @SuppressWarnings("unused")
    private static void runAllDemosWithVirtualThreads() {
        System.out.println("Running demos with Virtual Threads...\n");

        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();

            // Submit each demo as a virtual thread task
            futures.add(executor.submit(MyGetCharsDemo::executeDemo));
            futures.add(executor.submit(() -> {
                TwoSumDemo.executeDemo();
                TwoSumDemo.demonstrateFunctionalApproach();
            }));
            futures.add(executor.submit(GroupAnagramsDemo::executeDemo));
            futures.add(executor.submit(MyRemoveKdigits::executeDemo));
            futures.add(executor.submit(() -> new ContainsNearbyAlmostDuplicate().start()));

            // Wait for all tasks to complete
            futures.forEach(future -> {
                try {
                    future.get();
                } catch (Exception e) {
                    System.err.println("Task execution failed: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Record representing a demo task to execute.
     * Uses modern Java records (JDK 16+) for immutable data carriers.
     */
    private record DemoTask(String name, Runnable runner) {
        DemoTask {
            java.util.Objects.requireNonNull(name, "Demo name cannot be null");
            java.util.Objects.requireNonNull(runner, "Demo runner cannot be null");
        }
    }
}
