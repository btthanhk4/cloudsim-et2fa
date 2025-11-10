# ET2FA: Enhanced Task Type First Algorithm

Implementation of the ET2FA algorithm for deadline-constrained workflow scheduling in cloud computing, based on the paper:

**"ET2FA: A Hybrid Heuristic Algorithm for Deadline-constrained Workflow Scheduling in Cloud"**

Zaixing Sun, Boyu Zhang, Chonglin Gu, Ruitao Xie, Bin Qian, and Hejiao Huang  
*IEEE Transactions on Services Computing, 2022*

## Overview

ET2FA is a hybrid heuristic algorithm that solves deadline-constrained workflow scheduling problems in cloud environments with the following features:
- Per-second billing with minimum 60 seconds
- Instance hibernation capability
- Heterogeneous VM resources
- Unlimited VM instances

## Algorithm Components

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
├── src/main/java/vn/et2fa/
│   ├── model/
│   │   ├── Et2faTask.java          # Extended Cloudlet with ET2FA properties
│   │   └── TaskType.java           # Task type enumeration
│   ├── algorithm/
│   │   ├── T2FAAlgorithm.java       # Phase 1: Task Type First Algorithm
│   │   ├── DOBSAlgorithm.java      # Phase 2: Delay Operation Based on Block Structure
│   │   └── IHSHAlgorithm.java      # Phase 3: Instance Hibernate Scheduling Heuristic
│   ├── broker/
│   │   └── Et2faBroker.java        # Main broker implementing ET2FA
│   ├── util/
│   │   ├── WorkflowDAG.java        # DAG representation and utilities
│   │   └── DaxLoader.java          # PEGASUS DAX XML loader
│   └── App.java                     # Example application
└── pom.xml                          # Maven configuration
```

## Usage Example

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

## Running with DAX (Pegasus Workflows)

App supports loading Pegasus DAX XML directly:

```bash
# Example
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=/path/to/workflow.dax --deadline=1500"
```

- `--dax`: path to a Pegasus DAX file (e.g., CyberShake, Epigenomics, Inspiral, Montage, Sipht)
- `--deadline`: deadline in seconds (optional, default 1000)

Notes:
- The loader reads `<job>` elements (uses `runtime` as computation if available)
- Dependencies are read from `<child><parent/></child>` elements
- Data transfers are approximated from output file sizes

## Generating Montage DAX Files (Windows)

### Quick Start

1. **Generate DAX file** (only needs Python 3, no Pegasus WMS required):
   ```cmd
   python generate-montage-dax-simple.py --center "56.7 24.0" --degrees 1.0 --bands 1 --output workflows/montage-test.dax
   ```

2. **Run with ET2FA**:
   ```cmd
   mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-test.dax --deadline=3000"
   ```

### Windows Setup

**Requirements**: 
- Python 3.6+ (for generating DAX files, no Pegasus WMS needed!)
- Java 17+
- Maven 3.6+

See `HOW_TO_RUN.md` for detailed running instructions.

## Key Features

- **Workflow DAG Support**: Handles complex workflow structures with dependencies
- **Topological Level Calculation**: Automatically calculates task levels
- **Task Type Classification**: Identifies special DAG structures (SOSI, MOSI, SOMI, MOMI)
- **Block Structure Optimization**: Delays tasks to reduce idle time
- **Hibernation Scheduling**: Automatically schedules VM hibernation
- **Cost Calculation**: Computes total cost including running and hibernation costs
- **Idle Rate Calculation**: Measures resource utilization

## Dependencies

- CloudSim Plus 7.3.0
- Java 17+
- Maven 3.6+

## Building

```bash
mvn clean compile
mvn package
```

## Running

### Quick Start (Windows)

```cmd
run.bat
```

### Run with Maven

```bash
# Sample workflow (4 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App"

# With DAX file
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-test.dax --deadline=3000"

# With large workflow
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-2deg-3bands.dax --deadline=5000"
```

### Available Workflows

Xem `RUN_COMMANDS.md` để biết tất cả các câu lệnh chạy workflows.

## Benchmarking với nhiều Workflows

Project đã có sẵn **15 workflows** được tổ chức trong `workflows/benchmark/`:

- **CyberShake**: 50, 100, 1000 tasks
- **Epigenomics**: 50, 100, 1000 tasks
- **Inspiral**: 50, 100, 1000 tasks
- **Montage**: 50, 100, 1000 tasks
- **Sipht**: 50, 100, 1000 tasks

### Chạy batch test (Tất cả workflows)

```powershell
.\run-batch-tests.ps1
```

Hoặc chạy từng workflow:

```cmd
REM Small workflow (50 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=2000"

REM Medium workflow (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_100.dax --deadline=4000"

REM Large workflow (1000 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_1000.dax --deadline=15000"
```

Xem `RUN_COMMANDS.md` để biết tất cả các câu lệnh chạy workflows.

## Paper References

- **Original Paper**: IEEE Transactions on Services Computing, 2022
- **DOI**: 10.1109/TSC.2022.3196620
- **Conference Version**: IEEE CLOUD 2021

## Notes

- The implementation follows the algorithms described in the paper
- Some simplifications were made for CloudSim Plus compatibility
- VM pricing models need to be configured based on actual cloud provider pricing
- Data transfer times are calculated based on VM bandwidth

## License

This implementation is for educational and research purposes.

