# DSA Using Latest Java Features (Java 21+)

This repository demonstrates solving Data Structures and Algorithms (DSA) problems using the **latest Java features** available as of 2026, including Java 21+ syntax and APIs.

## 🚀 Modern Java Features Demonstrated

### Core Language Features

#### Java 21+ Features
- **Pattern Matching for Switch** - Enhanced switch expressions with type patterns
- **Record Patterns** - Destructuring records in pattern matching
- **Virtual Threads** - Lightweight threads for improved concurrency
- **Sequenced Collections** - New collection interfaces with defined encounter order
- **String Templates** (Preview) - Simplified string interpolation
- **Unnamed Patterns (`_`)** - Cleaner code when pattern variables aren't needed

#### Java 17+ Features
- **Sealed Classes & Interfaces** - Restricted class hierarchies for better design
- **Enhanced Pattern Matching** - Type patterns in switch statements

#### Java 16+ Features
- **Records** - Immutable data carriers with concise syntax
- **Pattern Matching for instanceof** - Cleaner type checks

#### Java 15+ Features
- **Text Blocks (""")** - Multi-line string literals with proper formatting
- **Helpful NullPointerExceptions** - Better debugging information

#### Java 10+ Features
- **Local Variable Type Inference (`var`)** - Cleaner code with type inference
- **Stream API Enhancements** - `toList()`, `dropWhile()`, `takeWhile()`

### Stream API & Functional Programming
- Extensive use of `Collectors.groupingBy()`
- Method references and lambda expressions
- Parallel streams for performance
- `Optional` for null-safety
- Custom collectors and reduction operations

## 📂 Files Overview

### 1. **ContainsNearbyAlmostDuplicate.java**
**LeetCode #220: Contains Duplicate III**

**Modern Features:**
- Records for test cases
- Text blocks for formatted output
- `var` for local type inference
- Enhanced for-each loops
- Sequenced collections (HashMap operations)
- Pattern validation

**Algorithm:** Bucket sort with sliding window  
**Complexity:** Time O(n), Space O(min(n, indexDiff))

---

### 2. **GroupAnagramsDemo.java**
**LeetCode #49: Group Anagrams**

**Modern Features:**
- **Sealed interfaces** with three strategy implementations
- **Pattern matching for switch** with strategy analysis
- **Unnamed patterns (`_`)** for unused variables
- Records for data representation
- Stream API with collectors
- Parallel streams for performance
- Method references

**Strategies:**
1. Sorted Key Strategy - O(n * k log k)
2. Frequency Key Strategy - O(n * k)
3. Parallel Strategy - Concurrent processing

---

### 3. **TwoSumDemo.java**
**LeetCode #1: Two Sum**

**Modern Features:**
- **Sealed interface** for solution strategies
- **Pattern matching** with comprehensive type analysis
- **Records** for results and test cases
- **Text blocks** for documentation
- **Unnamed patterns (`_`)** in switch expressions
- Multiple solution approaches with timing

**Strategies:**
1. HashMap Strategy - O(n)
2. Brute Force Strategy - O(n²)
3. Two Pointer Strategy - O(n log n)

---

### 4. **MyRemoveKdigits.java**
**LeetCode #402: Remove K Digits**

**Modern Features:**
- **Pattern matching for switch** in validation
- **Guard clauses** (`when`) in switch
- Records for test cases
- `var` keyword throughout
- Functional approach with Stream API
- Text blocks for output formatting
- Enhanced ArrayDeque operations

**Algorithm:** Monotonic stack approach  
**Complexity:** Time O(n), Space O(n)

---

### 5. **MyGetCharsDemo.java**
**JDK 25 Feature Demonstration**

**Modern Features:**
- Demonstrates **JDK 25's new `getChars()` method** in CharSequence
- `var` keyword for cleaner code
- Text blocks for documentation
- `formatted()` method for string formatting
- Custom CharSequence implementation
- Performance comparisons

---

### 6. **MyMain.java**
**Application Entry Point**

**Modern Features:**
- **Virtual Threads** support (commented implementation)
- Records for task representation
- Text blocks with ASCII art
- Enhanced exception handling
- Functional task execution with method references
- Timing and performance metrics

## 🎯 Key Modern Java Patterns Used

### 1. Sealed Types for Restricted Hierarchies
```java
public sealed interface SolutionStrategy 
    permits BruteForceStrategy, HashMapStrategy, TwoPointerStrategy {
    // Interface methods
}
```

### 2. Pattern Matching with Unnamed Variables
```java
var result = switch (strategy) {
    case HashMapStrategy _ -> "HashMap implementation";
    case BruteForceStrategy _ -> "Brute force implementation";
    case TwoPointerStrategy _ -> "Two pointer implementation";
};
```

### 3. Records for Data Carriers
```java
public record TwoSumResult(
    int index1, int index2, 
    int value1, int value2, 
    int sum
) {
    // Compact constructor with validation
}
```

### 4. Text Blocks for Readability
```java
var message = """
    Test: %s
    Result: %s
    Status: %s
    """.formatted(name, result, status);
```

### 5. Stream API with Modern Collectors
```java
var groups = Arrays.stream(strs)
    .collect(Collectors.groupingBy(
        str -> str.chars().sorted().boxed().toList(),
        Collectors.toList()
    ));
```

## 🏃 Running the Code

### Prerequisites
- **Java 21 or higher** (preferably Java 21+ for full feature support)
- Modern IDE (IntelliJ IDEA, Eclipse, VS Code with Java extensions)

### Compile and Run
```bash
# Compile all files
javac *.java

# Run the main program
java MyMain

# Run individual demos
java -cp . MyGetCharsDemo
java -cp . MyRemoveKdigits
```

### Expected Output
The program runs all demonstrations sequentially, showing:
- Test case inputs and outputs
- Algorithm performance metrics
- Timing information
- Pattern matching demonstrations
- Stream API operations

## 📊 Performance Comparisons

Each solution includes:
- Multiple algorithmic approaches
- Time complexity analysis
- Space complexity analysis
- Actual runtime measurements
- Parallel vs sequential comparisons

## 🎓 Learning Objectives

This repository helps you:
1. ✅ Master latest Java syntax and features (Java 21+)
2. ✅ Understand sealed types and pattern matching
3. ✅ Apply Stream API effectively
4. ✅ Write clean, modern Java code
5. ✅ Compare algorithmic approaches systematically
6. ✅ Use records and text blocks appropriately
7. ✅ Leverage functional programming in Java
8. ✅ Understand virtual threads and concurrency improvements

## 🔧 Code Quality Features

- **Type Safety:** Records, sealed types, pattern matching
- **Null Safety:** Optional usage throughout
- **Immutability:** Records are immutable by default
- **Readability:** Text blocks, var keyword, formatted strings
- **Maintainability:** Sealed hierarchies, clear patterns
- **Performance:** Parallel streams, virtual threads support
- **Testability:** Multiple strategy implementations

## 📚 Further Reading

- [JEP 409: Sealed Classes](https://openjdk.org/jeps/409)
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441)
- [JEP 456: Unnamed Variables & Patterns](https://openjdk.org/jeps/456)
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 431: Sequenced Collections](https://openjdk.org/jeps/431)

---
