# QWEN Context File for CloudSim ET2FA Project

## Project Overview

This is a Java-based workflow scheduling project implementing the **ET2FA (Enhanced Task Type First Algorithm)** for deadline-constrained workflow scheduling in cloud computing environments. The implementation is based on the research paper "ET2FA: A Hybrid Heuristic Algorithm for Deadline-constrained Workflow Scheduling in Cloud" published in IEEE Transactions on Services Computing, 2022.

### Key Features
- **Workflow DAG Support**: Handles complex workflow structures with dependencies
- **Per-second billing with minimum 60 seconds**: Implements realistic cloud billing models
- **Instance hibernation capability**: Optimizes costs by scheduling VM hibernation
- **Heterogeneous VM resources**: Supports different VM configurations with varying capabilities
- **Unlimited VM instances**: Can handle large-scale workflow execution

## Architecture

The ET2FA algorithm consists of three main phases:

### 1. T2FA (Task Type First Algorithm)
- Schedules tasks based on topological level and task types
- Classifies tasks into Type0-Type4 based on DAG structure:
  - **Type0**: Tasks alone in their topological level
  - **Type1**: Parent nodes in MOSI (Multiple Output Single Input) structure
  - **Type2**: Child nodes in MOSI structure
  - **Type3**: Parent nodes in SOMI (Single Output Multiple Input) structure
  - **Type4**: Child nodes in SOMI structure
- Uses compact-scheduling-condition based VM selection

### 2. DOBS (Delay Operation Based on Block Structure)
- Optimizes scheduling by delaying block structures
- Implements Theorem 1 from the paper
- Reduces idle time and cost

### 3. IHSH (Instance Hibernate Scheduling Heuristic)
- Schedules instance hibernation during idle periods
- Minimizes cost by utilizing hibernation mode

## Project Structure

```
cloudsim-et2fa/
├── pom.xml                      # Maven configuration
├── README.md                    # Main project documentation
├── QUICK_START.md               # Quick start guide
├── RUN.md                       # Detailed run instructions
├── run.bat                      # Windows execution script
├── run.sh                       # Linux/Mac execution script
├── src/
│   └── main/
│       └── java/
│           └── vn/
│               └── et2fa/
│                   ├── App.java                # Example application
│                   ├── broker/
│                   │   └── Et2faBroker.java    # Main broker implementing ET2FA
│                   ├── algorithm/
│                   │   ├── T2FAAlgorithm.java  # Phase 1: Task Type First Algorithm
│                   │   ├── DOBSAlgorithm.java  # Phase 2: Delay Operation Based on Block Structure
│                   │   └── IHSHAlgorithm.java  # Phase 3: Instance Hibernate Scheduling Heuristic
│                   ├── model/
│                   │   ├── Et2faTask.java      # Extended Cloudlet with ET2FA properties
│                   │   └── TaskType.java       # Task type enumeration
│                   └── util/
│                       └── WorkflowDAG.java    # DAG representation and utilities
└── target/                      # Maven build directory
```

## Building and Running

### Prerequisites
- Java 17+
- Maven 3.6+
- CloudSim Plus 7.3.0 (transitive dependency)

### Build Commands
```bash
mvn clean compile              # Compile the project
mvn package                    # Package into JAR
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App"  # Compile and run example
```

### Running the Example
```bash
# Using Maven
mvn exec:java -Dexec.mainClass="vn.et2fa.App"

# Using Windows script
run.bat

# Using Linux/Mac script
./run.sh
```

## Key Classes and Usage

### Et2faBroker
The main broker class implementing the ET2FA algorithm with methods for:
- `buildWorkflowDAG()` - Building workflow dependencies
- `executeET2FA()` - Running the three-phase algorithm
- `calculateTotalCost()` - Computing total execution cost
- `calculateTotalIdleRate()` - Calculating resource utilization
- `meetsDeadline()` - Checking deadline compliance

### Algorithm Classes
- `T2FAAlgorithm` - Implements the initial scheduling phase
- `DOBSAlgorithm` - Optimizes scheduling by delaying block structures
- `IHSHAlgorithm` - Schedules VM hibernation for cost optimization

### Example Usage Pattern
```java
// 1. Create simulation and broker
CloudSim simulation = new CloudSim();
Et2faBroker broker = new Et2faBroker(simulation);

// 2. Create VMs
List<Vm> vmList = ...;
broker.submitVmList(vmList);

// 3. Create workflow tasks
List<Et2faTask> tasks = ...;
broker.submitCloudletList(tasks);

// 4. Build workflow DAG with dependencies
Map<String, List<String>> dependencies = new HashMap<>();
dependencies.put("0", Arrays.asList("1", "2"));
// ... more dependencies

Map<String, Double> dataTransfers = new HashMap<>();
dataTransfers.put("0_1", 100.0);
// ... more data transfers

broker.buildWorkflowDAG(tasks, dependencies, dataTransfers);

// 5. Set deadline
broker.setDeadline(1000.0);

// 6. Execute ET2FA algorithm
broker.executeET2FA();

// 7. Run simulation
simulation.start();

// 8. Get results
double totalCost = broker.calculateTotalCost();
double idleRate = broker.calculateTotalIdleRate();
boolean meetsDeadline = broker.meetsDeadline();
```

## Development Conventions

- Java 17+ source compatibility
- UTF-8 encoding
- Maven-based dependency management
- CloudSim Plus 7.3.0 integration
- Workflow DAG structure for task dependencies
- Per-second billing model with minimum 60 seconds
- VM hibernation optimization with defined parameters:
  - DUR_H = 60.0s (shortest hibernation duration)
  - GAP_H = 120.0s (minimum gap between hibernations)
  - DUR_W = 34.0s (warm startup time)
  - DUR_P = 5.6s (stopping time)

## Dependencies

- CloudSim Plus 7.3.0 (core simulation framework)
- SLF4J 2.0.13 (logging)
- Java 17+ runtime

## Key Files for Reference

- **App.java**: Example implementation showing how to use the ET2FA components
- **Et2faBroker.java**: Main broker that orchestrates the three-phase algorithm
- **T2FAAlgorithm.java**: Core scheduling algorithm implementation
- **DOBSAlgorithm.java**: Optimization algorithm for block structures
- **IHSHAlgorithm.java**: Cost optimization through hibernation scheduling
- **Et2faTask.java**: Extended Cloudlet with ET2FA-specific properties
- **WorkflowDAG.java**: Data structure for managing workflow dependencies