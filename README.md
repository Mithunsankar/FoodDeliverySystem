# Online Food Delivery System — Multithreading Demo

A Java console application that simulates a food delivery platform to demonstrate **all major multithreading concepts** in a single, cohesive project.

---

## Project Structure

```
FoodDeliverySystem/
├── src/
│   ├── Main.java               # Orchestration + concept map
│   ├── Order.java              # Data model (volatile status field)
│   ├── Restaurant.java         # Shared resource — synchronized methods
│   ├── OrderQueue.java         # Bounded queue — wait() / notify()
│   ├── OrderProducer.java      # Producer — Runnable, MAX_PRIORITY
│   ├── OrderConsumer.java      # Consumer — Runnable, NORM_PRIORITY
│   ├── DeliveryTask.java       # Thread pool task — Runnable + sleep()
│   ├── DeliveryManager.java    # ExecutorService thread pool
│   └── OrderMonitorThread.java # Daemon monitor — extends Thread
└── bin/                        # Compiled .class files
```

---

## Multithreading Concepts Covered

| # | Concept | Where Applied |
|---|---------|---------------|
| 1 | **Thread Extension** | `OrderMonitorThread extends Thread` |
| 2 | **Runnable Interface** | `DeliveryTask`, `OrderProducer`, `OrderConsumer` |
| 3 | **Thread Lifecycle** | NEW → RUNNABLE → TIMED_WAITING → TERMINATED visible in logs |
| 4 | **Synchronization** | `Restaurant.acceptOrder()` / `cancelOrder()` — `synchronized` methods |
| 5 | **wait() / notify()** | `OrderQueue.enqueue()` / `dequeue()` — producer-consumer coordination |
| 6 | **Thread Pool** | `DeliveryManager` — `Executors.newFixedThreadPool(3)` |
| 7 | **Daemon Thread** | `OrderMonitorThread.setDaemon(true)` — background revenue monitor |
| 8 | **Thread Priority** | Producer = `MAX_PRIORITY (10)`, Consumers = `NORM_PRIORITY (5)` |
| 9 | **join()** | `producerThread.join()` + `consumerThread.join()` in `Main` |
| 10| **Thread.sleep()** | Kitchen prep, delivery travel, and order pacing delays |

---

## How Each Class Maps to a Real-World Role

| Class | Real-World Analogy |
|-------|--------------------|
| `Restaurant` | The kitchen — only one chef (thread) can update the order book at a time |
| `OrderQueue` | The ticket rail between the front desk and the kitchen |
| `OrderProducer` | Customer app placing orders |
| `OrderConsumer` | Front desk staff picking up and routing orders |
| `DeliveryTask` | A delivery rider picking up and delivering one order |
| `DeliveryManager` | Dispatch centre with a fixed fleet of riders (thread pool) |
| `OrderMonitorThread` | Live dashboard showing revenue — doesn't block shutdown |

---

## Compilation

### Windows (Command Prompt)
```bat
cd FoodDeliverySystem
javac -d bin src\*.java
java -cp bin Main
```

### Linux / macOS
```bash
cd FoodDeliverySystem
javac -d bin src/*.java
java -cp bin Main
```

### One-liner run script (Windows)
```bat
@echo off
cd /d "%~dp0"
javac -d bin src\*.java
java -cp bin Main
pause
```

### IDE (IntelliJ / Eclipse)
1. Create a new Java project.
2. Set `src/` as source root, `bin/` as output.
3. Copy all `.java` files into `src/`.
4. Run `Main.java`.

---

## Configuration (edit constants in Main.java)

```java
private static final int    TOTAL_ORDERS       = 10;   // orders the producer places
private static final int    QUEUE_CAPACITY      = 5;    // max bounded queue size
private static final int    NUM_CONSUMERS       = 2;    // consumer threads
private static final int    DELIVERY_POOL_SIZE  = 3;    // thread pool riders
```

---

## Key Design Decisions

### Why `volatile` on `Order.status`?
Multiple threads read and write `status`. Without `volatile`, a thread's CPU cache might return a stale value. `volatile` guarantees the write is immediately visible to all threads.

### Why `synchronized` on `Restaurant` methods?
`totalRevenue` and `totalOrdersAccepted` are shared mutable state. Without synchronization, two threads depositing simultaneously could cause a **lost update** — the same problem as a bank race condition.

### Why `notifyAll()` instead of `notify()` in `OrderQueue`?
With multiple consumer threads waiting, `notify()` wakes exactly one thread — potentially another producer. `notifyAll()` is safer when there are multiple waiters of different types.

### Daemon vs Regular Thread
```
Regular thread → JVM waits for it before exiting
Daemon thread  → JVM exits regardless; monitor dies automatically
```
`OrderMonitorThread` is a daemon so it never prevents the program from finishing.

---

## Expected Output (excerpt)

```
======================================================================
    ONLINE FOOD DELIVERY SYSTEM — MULTITHREADING DEMO
======================================================================
[MAIN] Running on thread: main          Priority: 5  State: RUNNABLE

--- Starting Daemon Monitor Thread ---
[MONITOR] Daemon monitor started — tracking SpiceRoute
[MAIN] Monitor is daemon: true

--- Starting Producer Thread (MAX_PRIORITY) ---
[MAIN] Producer priority: 10

--- Starting Consumer Threads (NORM_PRIORITY) ---
[MAIN] Consumer-1 priority: 5
[MAIN] Consumer-2 priority: 5

--- Initializing Delivery Thread Pool ---
[MANAGER] DeliveryManager initialized — pool size: 3
[MANAGER] Dispatched delivery task for Order#02
...
[QUEUE]  +enqueued  Order#01 [Alice] Margherita Pizza    $12.99  <PLACED>
[QUEUE]  -dequeued  Order#01 [Alice] Margherita Pizza    $12.99  <PLACED>
[RESTAURANT] SpiceRoute   accepted  Order#01 ...  <PREPARING>
[DELIVERY]   Garlic Bread         is OUT_FOR_DELIVERY  (thread: pool-1-thread-1)
[DELIVERED]  Garlic Bread         delivered to ZomattoPlatform

[MONITOR] === SpiceRoute | Orders: 13 | Revenue: $130.46 ===

[MAIN] Waiting for producer to finish placing all orders (join)...
[MAIN] Producer finished.
[MAIN] All consumers finished.
[MANAGER] All delivery threads finished.

======================================================================
                        FINAL REPORT
======================================================================
  Restaurant       : SpiceRoute
  Orders Accepted  : 15
  Total Revenue    : $149.96
======================================================================
```

---

## Requirements
- JDK 8 or higher
- No external libraries

---

## Author
Submitted for Java Multithreading Assignment — April 2026
