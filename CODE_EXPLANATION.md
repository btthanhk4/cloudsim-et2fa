# Giải thích chi tiết từng file code trong ET2FA

## 📁 Cấu trúc thư mục

```
cloudsim-et2fa/
├── model/              # Data models
├── algorithm/          # 3 phases của ET2FA
├── broker/             # Main scheduler
├── util/               # Utilities
└── App.java            # Demo application
```

---

## 📋 MODEL - Các lớp dữ liệu

### 1. `TaskType.java` - Định nghĩa loại task

**Vai trò**: Enum định nghĩa các loại task trong ET2FA algorithm

**Các loại**:
- **TYPE0**: Tasks đơn độc trong topological level (không có task nào khác cùng level)
- **TYPE1**: Parent nodes trong MOSI (Multiple Output Single Input) - 1 task có nhiều successors
- **TYPE2**: Child nodes trong MOSI - nhiều tasks có chung 1 predecessor
- **TYPE3**: Parent nodes trong SOMI (Single Output Multiple Input) - nhiều tasks có chung 1 successor
- **TYPE4**: Child nodes trong SOMI - 1 task có nhiều predecessors
- **GENERAL**: Tasks không thuộc các loại trên

**Ví dụ**:
```
TYPE1 → TYPE2, TYPE2  (MOSI: 1 parent, nhiều children)
TYPE3, TYPE3 → TYPE4  (SOMI: nhiều parents, 1 child)
```

**Tại sao cần**: Giúp T2FA algorithm biết cách schedule tasks theo đặc điểm của chúng.

---

### 2. `Et2faTask.java` - Task mở rộng

**Vai trò**: Mở rộng CloudletSimple để thêm thông tin cần cho ET2FA

**Thông tin thêm**:
- `taskType`: Loại task (TYPE0-TYPE4)
- `topologicalLevel`: Level trong DAG (0 = entry, cao hơn = phụ thuộc nhiều hơn)
- `predecessors`: Danh sách tasks phải chạy trước
- `successors`: Danh sách tasks chạy sau
- `computation`: Khối lượng tính toán (GFLOP)
- `actualStartTime`, `actualFinishTime`: Thời gian thực tế chạy

**Ví dụ**:
```java
Et2faTask task = new Et2faTask(10000, 1, TaskType.TYPE0);
task.setTopologicalLevel(0);
task.addSuccessor(otherTask);
```

**Tại sao cần**: CloudSim Plus mặc định không có thông tin về dependencies và levels, nên cần extend.

---

## 🔧 UTIL - Công cụ hỗ trợ

### 3. `WorkflowDAG.java` - Quản lý DAG

**Vai trò**: Quản lý Directed Acyclic Graph (DAG) của workflow

**Chức năng chính**:

1. **addTask()**: Thêm task vào DAG
2. **addDependency()**: Tạo dependency giữa 2 tasks
   ```java
   dag.addDependency(task0, task1, 100.0); // task0 → task1, data=100
   ```

3. **calculateTopologicalLevels()**: Tính level cho mỗi task
   - Level 0: Tasks không có predecessors
   - Level i: Tasks phụ thuộc vào tasks level i-1
   - Dùng BFS để tính

4. **getTasksByLevel()**: Nhóm tasks theo level
   - Trả về Map<Level, List<Tasks>>

5. **simplifyDAG()**: Gộp SOSI structures
   - SOSI = Single Output Single Input (1 → 1)
   - Gộp để giảm số tasks, giảm data transfer

**Ví dụ**:
```java
WorkflowDAG dag = new WorkflowDAG();
dag.addTask(task0);
dag.addDependency(task0, task1, 100.0);
dag.calculateTopologicalLevels(); // Tính levels
```

**Tại sao cần**: Cần quản lý dependencies và tính levels để schedule đúng.

---

## 🎯 ALGORITHM - 3 Phases của ET2FA

### 4. `T2FAAlgorithm.java` - Phase 1: Task Type First Algorithm

**Vai trò**: Schedule tasks dựa trên topological level và task types

**Các bước chính**:

1. **Pre-processing**:
   - Simplify DAG (gộp SOSI)
   - Tính topological levels
   - Phân loại task types

2. **Classify Task Types**:
   - Xem xét số predecessors/successors
   - Phân loại TYPE0-TYPE4

3. **Schedule Tasks**:
   - Schedule theo level (từ 0 → max)
   - Trong mỗi level, schedule theo type (TYPE0 → TYPE4)
   - VM selection với 3 layers:
     - Layer 1: VMs đang chạy tasks cùng level
     - Layer 2: VMs đang chạy tasks level trước
     - Layer 3: Tất cả VMs available

4. **Compact Scheduling**:
   - Ưu tiên VMs đang busy để giảm idle time
   - Ưu tiên VMs có predecessors để giảm data transfer

**Input**: WorkflowDAG, List<VMs>
**Output**: Map<Et2faTask, Vm> - Schedule ban đầu

**Ví dụ**:
```java
T2FAAlgorithm t2fa = new T2FAAlgorithm(dag, vms);
Map<Et2faTask, Vm> schedule = t2fa.schedule();
```

**Tại sao cần**: Phase 1 tạo schedule ban đầu, các phase sau sẽ optimize.

---

### 5. `DOBSAlgorithm.java` - Phase 2: Delay Operation Based on Block Structure

**Vai trò**: Tối ưu schedule bằng cách delay block structures

**Block Structure**: Chuỗi tasks chạy liên tục không có idle time trên cùng VM

**Các bước**:

1. **Tìm Block Structures**:
   - Tìm các tasks chạy liên tục trên cùng VM
   - Không có gap giữa chúng

2. **Kiểm tra có thể delay**:
   - Tính estimated latest finish time
   - Nếu có thể delay mà không ảnh hưởng tasks khác → delay

3. **Apply Delay**:
   - Tính delay time (min của idle time và slack time)
   - Delay start time của block
   - Update finish time

**Ví dụ**:
```
Trước: [Task1: 0-10s] [idle 20s] [Task2: 30-40s]
Sau:   [Task1: 20-30s] [Task2: 30-40s]  ← Giảm idle time 20s
```

**Input**: Schedule từ T2FA
**Output**: Schedule đã được optimize

**Tại sao cần**: Giảm idle time → Giảm cost → Tăng resource utilization.

---

### 6. `IHSHAlgorithm.java` - Phase 3: Instance Hibernate Scheduling Heuristic

**Vai trò**: Schedule hibernation cho VMs khi idle để tiết kiệm cost

**Các bước**:

1. **Tìm Idle Periods**:
   - Tìm các khoảng thời gian VM không chạy task
   - Giữa các tasks trên cùng VM

2. **Kiểm tra điều kiện hibernate**:
   - Idle time > 60s (DurH)
   - Gap giữa 2 lần hibernate > 120s (GapH)

3. **Schedule Hibernation**:
   - Tạo HibernationPeriod (start, end)
   - Tính cost: chỉ tính ElasticIP (~$0.005/h)
   - Running cost: per-second billing

4. **Tính Total Cost và Idle Rate**:
   - Total Cost = Running Cost + Hibernation Cost
   - Idle Rate = 1 - (Execution Time / Lease Time)

**Ví dụ**:
```
VM chạy Task1 (0-10s), idle (10-80s), Task2 (80-90s)
→ Hibernate từ 10s đến 80s (70s > 60s)
→ Cost: Running (0-10s, 80-90s) + Hibernation (10-80s)
```

**Input**: Schedule từ DOBS
**Output**: Hibernation schedule, Total Cost, Idle Rate

**Tại sao cần**: Hibernation giúp tiết kiệm cost đáng kể khi VMs idle.

---

## 🎮 BROKER - Main Scheduler

### 7. `Et2faBroker.java` - Broker chính

**Vai trò**: Quản lý toàn bộ quá trình scheduling, tích hợp 3 phases

**Chức năng chính**:

1. **buildWorkflowDAG()**:
   - Nhận tasks, dependencies, data transfers
   - Xây dựng WorkflowDAG

2. **executeET2FA()**:
   - Chạy 3 phases tuần tự:
     ```java
     // Phase 1: T2FA
     T2FAAlgorithm t2fa = new T2FAAlgorithm(workflowDAG, vms);
     schedule = t2fa.schedule();
     
     // Phase 2: DOBS
     DOBSAlgorithm dobs = new DOBSAlgorithm(schedule);
     dobs.optimize();
     
     // Phase 3: IHSH
     IHSHAlgorithm ihsh = new IHSHAlgorithm(schedule);
     ihsh.scheduleHibernations();
     ```

3. **applyScheduleToCloudlets()**:
   - Gán VMs cho tasks theo schedule

4. **calculateTotalCost()**: Tính total cost
5. **calculateTotalIdleRate()**: Tính idle rate
6. **meetsDeadline()**: Kiểm tra deadline

**Input**: Tasks, VMs, Dependencies, Deadline
**Output**: Schedule, Cost, Idle Rate, Deadline status

**Tại sao cần**: Broker là điểm vào duy nhất, quản lý toàn bộ workflow.

---

## 🚀 APP - Demo Application

### 8. `App.java` - Ứng dụng demo

**Vai trò**: Demo cách sử dụng ET2FA algorithm

**Các bước**:

1. **Khởi tạo CloudSim**:
   ```java
   CloudSim simulation = new CloudSim();
   ```

2. **Tạo Datacenter và Hosts**:
   - Tạo 5 hosts với đủ tài nguyên

3. **Tạo VMs**:
   - 5 VMs với cấu hình khác nhau (500-2500 MIPS)

4. **Tạo Tasks**:
   - 4 tasks với dependencies
   - Task 0 → Task 1, Task 2
   - Task 1, Task 2 → Task 3

5. **Build DAG và Execute ET2FA**:
   ```java
   broker.buildWorkflowDAG(tasks, dependencies, dataTransfers);
   broker.setDeadline(1000.0);
   broker.executeET2FA();
   ```

6. **Run Simulation và Show Results**:
   - Chạy simulation
   - In kết quả: schedule, cost, idle rate

**Tại sao cần**: Để demo và test algorithm.

---

## 🔄 Luồng hoạt động tổng thể

```
1. App.java tạo tasks, VMs
   ↓
2. Et2faBroker.buildWorkflowDAG() → WorkflowDAG
   ↓
3. Et2faBroker.executeET2FA():
   ├─ Phase 1: T2FAAlgorithm.schedule()
   │  ├─ WorkflowDAG.calculateTopologicalLevels()
   │  ├─ Classify task types
   │  └─ Schedule tasks → Map<Et2faTask, Vm>
   │
   ├─ Phase 2: DOBSAlgorithm.optimize()
   │  ├─ Tìm block structures
   │  └─ Delay blocks → Update schedule
   │
   └─ Phase 3: IHSHAlgorithm.scheduleHibernations()
      ├─ Tìm idle periods
      └─ Schedule hibernations → Cost, Idle Rate
   ↓
4. Simulation chạy tasks theo schedule
   ↓
5. Show results: Cost, Idle Rate, Deadline status
```

---

## 📊 Tóm tắt vai trò từng file

| File | Vai trò | Quan trọng |
|------|---------|------------|
| `TaskType.java` | Định nghĩa loại task | ⭐⭐⭐ |
| `Et2faTask.java` | Task với dependencies | ⭐⭐⭐⭐⭐ |
| `WorkflowDAG.java` | Quản lý DAG | ⭐⭐⭐⭐⭐ |
| `T2FAAlgorithm.java` | Phase 1: Schedule | ⭐⭐⭐⭐⭐ |
| `DOBSAlgorithm.java` | Phase 2: Optimize | ⭐⭐⭐⭐ |
| `IHSHAlgorithm.java` | Phase 3: Cost optimization | ⭐⭐⭐⭐ |
| `Et2faBroker.java` | Main scheduler | ⭐⭐⭐⭐⭐ |
| `App.java` | Demo application | ⭐⭐ |

---

## 💡 Tips khi đọc code

1. **Bắt đầu từ `App.java`**: Hiểu flow tổng thể
2. **Đọc `Et2faBroker.java`**: Hiểu cách tích hợp 3 phases
3. **Đọc từng algorithm**: Hiểu logic từng phase
4. **Xem `WorkflowDAG.java`**: Hiểu cách quản lý DAG
5. **Xem `Et2faTask.java`**: Hiểu cấu trúc dữ liệu

---

## ❓ Câu hỏi thường gặp

### Q: File nào quan trọng nhất?
**A**: `Et2faBroker.java` - quản lý toàn bộ, và `T2FAAlgorithm.java` - phase chính.

### Q: Có thể bỏ phase nào không?
**A**: Không. Mỗi phase có vai trò riêng:
- T2FA: Tạo schedule ban đầu (BẮT BUỘC)
- DOBS: Optimize (quan trọng)
- IHSH: Tiết kiệm cost (quan trọng)

### Q: Làm sao thêm workflow mới?
**A**: Sửa `App.java`:
- Tạo tasks mới
- Thêm dependencies
- Update data transfers

### Q: Code có thể scale không?
**A**: Có. Time complexity O(n²), đã test với 1000+ tasks.

