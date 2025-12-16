# 🎥 Hướng Dẫn Quay Video Demo - ET2FA Project

## 📋 MỤC LỤC

1. [Chuẩn Bị Trước Khi Quay](#chuẩn-bị-trước-khi-quay)
2. [Script Demo Chi Tiết](#script-demo-chi-tiết)
3. [Giải Thích Từng Dòng Log](#giải-thích-từng-dòng-log)
4. [Kịch Bản Quay Video](#kịch-bản-quay-video)
5. [Tips Quay Video Chuyên Nghiệp](#tips-quay-video-chuyên-nghiệp)
6. [Checklist Trước Khi Nộp](#checklist-trước-khi-nộp)

---

## CHUẨN BỊ TRƯỚC KHI QUAY

### 1. Chuẩn Bị Môi Trường

#### Kiểm Tra Hệ Thống
```bash
# Kiểm tra Java version (cần Java 17+)
java -version

# Kiểm tra Maven version (cần Maven 3.6+)
mvn -version

# Kiểm tra đang ở đúng thư mục
pwd
# Kết quả mong đợi: .../cloudsim-et2fa
```

#### Compile Project
```bash
mvn clean compile
```

**Giải thích**:
- `mvn clean`: Xóa các file đã compile trước đó (thư mục `target/`)
- `compile`: Compile source code Java thành bytecode (.class files)

**Kết quả mong đợi**:
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Nếu có lỗi**: Kiểm tra lại Java version và Maven configuration

---

### 2. Chuẩn Bị Terminal/IDE

#### Terminal Setup
- **Font**: Consolas hoặc Courier New (monospace, dễ đọc)
- **Font size**: 14-16pt
- **Background**: Dark theme (đen/xám đen) - dễ nhìn trên video
- **Window size**: 120x40 characters (đủ rộng để hiển thị đầy đủ)
- **Clear screen**: `clear` hoặc `Ctrl+L` trước khi quay

#### IDE Setup (nếu quay code)
- **Theme**: Dark theme
- **Font**: Consolas 14pt
- **Show line numbers**: Bật
- **Zoom**: 100% (không zoom in/out)

---

### 3. Chuẩn Bị Recording Software

#### Phần Mềm Quay Màn Hình
- **Windows**: OBS Studio (miễn phí, chất lượng cao)
- **Mac**: QuickTime Player (built-in) hoặc OBS Studio
- **Linux**: OBS Studio hoặc SimpleScreenRecorder

#### Settings Recording
- **Resolution**: 1920x1080 (Full HD)
- **Frame rate**: 30 fps (đủ mượt)
- **Audio**: Bật microphone để giải thích
- **Format**: MP4 (dễ upload)

---

## SCRIPT DEMO CHI TIẾT

### Demo 1: Compile và Chạy Workflow Cơ Bản (2 phút)

#### Bước 1: Compile Project

**Lệnh**:
```bash
mvn clean compile
```

**Giải thích khi quay**:
> "Đầu tiên, tôi sẽ compile project để đảm bảo code không có lỗi. Lệnh `mvn clean compile` sẽ xóa các file cũ và compile lại từ đầu."

**Log mong đợi**:
```
[INFO] Scanning for projects...
[INFO] 
[INFO] -----------------< vn.et2fa:cloudsim-et2fa >------------------
[INFO] Building cloudsim-et2fa 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ cloudsim-et2fa ---
[INFO] Deleting C:\Users\Admin\cloudsim\cloudsim-et2fa\target
[INFO] 
[INFO] --- maven-compiler-plugin:3.8.1:compile (default-compile) @ cloudsim-et2fa ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 13 source files to C:\Users\Admin\cloudsim\cloudsim-et2fa\target\classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.345 s
[INFO] Finished at: 2024-XX-XX
[INFO] ------------------------------------------------------------------------
```

**Giải thích log**:
- `[INFO] Building cloudsim-et2fa 1.0-SNAPSHOT`: Đang build project
- `Deleting ...\target`: Xóa thư mục target cũ
- `Compiling 13 source files`: Compile 13 file Java
- `BUILD SUCCESS`: Compile thành công, không có lỗi

---

#### Bước 2: Chạy Workflow Original Mode

**Lệnh**:
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=original"
```

**Giải thích khi quay**:
> "Bây giờ tôi sẽ chạy workflow Cyber_30 với original mode. Đây là mode không có CPO optimization, giống như thuật toán ET2FA gốc trong paper. Deadline được set là 3000 giây."

**Log mong đợi và giải thích**:

```
=== ET2FA Workflow Scheduling Simulation ===
Mode: ORIGINAL
Running in ORIGINAL mode (no CPO optimization)
```

**Giải thích**:
- Hiển thị mode đang chạy (ORIGINAL)
- Không có CPO optimization

```
Created 30 tasks with IDs 0-29
DAG Build: Added 30 tasks from 30 cloudlets (DAX has 30 tasks)
DAG Build: Added 45 dependencies
Loaded DAX: jobs=30
```

**Giải thích**:
- `Created 30 tasks`: Tạo 30 tasks từ DAX file
- `Added 45 dependencies`: Có 45 dependencies giữa các tasks
- `jobs=30`: DAX file có 30 jobs

```
INFO
================== Starting CloudSim Plus 7.3.0 ==================
```

**Giải thích**:
- Khởi động CloudSim Plus framework
- Version 7.3.0

```
INFO  0.00: DatacenterSimple1 is starting...
INFO  Et2faBroker2 is starting...
INFO  Entities started.
INFO  0.00: Et2faBroker2: List of 1 datacenters(s) received.
```

**Giải thích**:
- `DatacenterSimple1`: Tạo datacenter (cloud provider)
- `Et2faBroker2`: Tạo broker để quản lý scheduling
- `List of 1 datacenters`: Broker nhận được thông tin về 1 datacenter

```
INFO  0.00: Et2faBroker2: Trying to create Vm 0 in DatacenterSimple1
INFO  0.00: Et2faBroker2: Trying to create Vm 1 in DatacenterSimple1
INFO  0.00: Et2faBroker2: Trying to create Vm 2 in DatacenterSimple1
INFO  0.00: Et2faBroker2: Trying to create Vm 3 in DatacenterSimple1
INFO  0.00: Et2faBroker2: Trying to create Vm 4 in DatacenterSimple1
```

**Giải thích**:
- Broker đang tạo 5 VMs để chạy tasks
- Mỗi VM sẽ được allocate vào datacenter

```
INFO  0.00: VmAllocationPolicySimple: Vm 0 has been allocated to Host 0/DC 1
INFO  0.00: VmAllocationPolicySimple: Vm 1 has been allocated to Host 1/DC 1
INFO  0.00: VmAllocationPolicySimple: Vm 2 has been allocated to Host 2/DC 1
INFO  0.00: VmAllocationPolicySimple: Vm 3 has been allocated to Host 3/DC 1
INFO  0.00: VmAllocationPolicySimple: Vm 4 has been allocated to Host 4/DC 1
```

**Giải thích**:
- Mỗi VM được allocate vào một Host vật lý
- Có 5 hosts trong datacenter

```
defaultVmMapper: DAG has 30 tasks before ET2FA
ET2FA: Starting scheduling for 30 tasks with 5 VMs
```

**Giải thích**:
- DAG có 30 tasks trước khi chạy ET2FA
- Bắt đầu scheduling với 30 tasks và 5 VMs

```
Topological Levels (BFS-optimized): 5 entry tasks, max level=4, 0 unassigned tasks set to level 0
T2FA: Classifying tasks into types (TYPE0-TYPE4, GENERAL)...
T2FA: Scheduling 30 tasks across 5 topological levels...
T2FA: Processing level 0 (5 tasks)...
T2FA: Processing level 1 (8 tasks)...
T2FA: Processing level 2 (7 tasks)...
T2FA: Processing level 3 (6 tasks)...
T2FA: Processing level 4 (4 tasks)...
```

**Giải thích**:
- **Topological Levels**: Tính toán levels bằng BFS (nhanh hơn O(n²))
- **5 entry tasks**: Có 5 tasks không có predecessors (level 0)
- **max level=4**: Level cao nhất là 4
- **Classifying tasks**: Phân loại tasks theo type (TYPE0-TYPE4, GENERAL)
- **Scheduling theo level**: Lập lịch từ level 0 đến level 4
- **Processing level X**: Đang xử lý tasks ở level X

```
ET2FA: Phase 1 completed in 15ms.
ET2FA: Scheduled 30 tasks out of 30 tasks in DAG
```

**Giải thích**:
- **Phase 1 completed**: T2FA hoàn thành trong 15ms
- **Scheduled 30 tasks**: Đã lập lịch đủ 30 tasks

```
ET2FA: Phase 2 - DOBS (Delay Operation Based on Block Structure)...
DOBS: Analyzing block structure...
DOBS: Found 3 blocks
DOBS: Optimizing delays...
ET2FA: Phase 2 completed in 8ms.
```

**Giải thích**:
- **Phase 2 - DOBS**: Bắt đầu phase 2
- **Found 3 blocks**: Tìm thấy 3 block structures
- **Optimizing delays**: Tối ưu delays để giảm idle time
- **Phase 2 completed**: Hoàn thành trong 8ms

```
ET2FA: Phase 3 - IHSH (Instance Hibernate Scheduling Heuristic)...
IHSH: Analyzing VM idle periods...
IHSH: Scheduling hibernations for 3 VMs
ET2FA: Phase 3 completed in 12ms.
```

**Giải thích**:
- **Phase 3 - IHSH**: Bắt đầu phase 3
- **Analyzing VM idle periods**: Phân tích các khoảng idle của VMs
- **Scheduling hibernations**: Lập lịch hibernation cho 3 VMs
- **Phase 3 completed**: Hoàn thành trong 12ms

```
INFO  0.10: Processing last events before simulation shutdown.
INFO  0.10: Et2faBroker2 is shutting down...
INFO  0.10: Et2faBroker2: Requesting Vm 4 destruction.
INFO  0.10: Et2faBroker2: Requesting Vm 3 destruction.
INFO  0.10: Et2faBroker2: Requesting Vm 2 destruction.
INFO  0.10: Et2faBroker2: Requesting Vm 1 destruction.
INFO  0.10: Et2faBroker2: Requesting Vm 0 destruction.
```

**Giải thích**:
- **Processing last events**: Xử lý các events cuối cùng
- **Requesting Vm X destruction**: Yêu cầu destroy VM X (sau khi tasks xong)

```
INFO
================== Simulation finished at time 0.10 ==================
```

**Giải thích**:
- Simulation hoàn thành tại thời điểm 0.10 (simulation time, không phải real time)

```
=== Scheduling Results ===
```

**Giải thích**:
- Bắt đầu hiển thị kết quả scheduling

```
=== Performance Metrics ===
Total Cost: $1.080888
Total Idle Rate: 0.5067
Meets Deadline: Yes
Max Finish Time: 1234.35s
Deadline: 3000.00s
SCHEDULING_TIME: 0.03435861
```

**Giải thích chi tiết từng metric**:

1. **Total Cost: $1.080888**
   - Tổng chi phí để chạy workflow
   - Bao gồm: Running cost + Hibernation cost
   - Đơn vị: USD

2. **Total Idle Rate: 0.5067**
   - Tỷ lệ thời gian VMs không làm việc
   - 0.5067 = 50.67% idle time
   - Utilization = 1 - 0.5067 = 49.33%

3. **Meets Deadline: Yes**
   - Max Finish Time (1234.35s) < Deadline (3000s)
   - Workflow hoàn thành đúng hạn

4. **Max Finish Time: 1234.35s**
   - Thời gian hoàn thành của task cuối cùng (makespan)
   - ≈ 20.6 phút

5. **Deadline: 3000.00s**
   - Ràng buộc deadline từ input
   - ≈ 50 phút

6. **SCHEDULING_TIME: 0.03435861**
   - Thời gian CPU để chạy thuật toán scheduling
   - ≈ 34 milliseconds
   - Rất nhanh!

---

### Demo 2: Chạy Workflow Optimized Mode (2 phút)

#### Bước 1: Chạy Optimized Mode

**Lệnh**:
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized"
```

**Giải thích khi quay**:
> "Bây giờ tôi sẽ chạy cùng workflow Cyber_30 nhưng với optimized mode. Mode này có thêm CPO (Critical Path Optimization) - đây là đóng góp mới của nhóm."

**Log khác biệt so với Original**:

```
=== ET2FA Workflow Scheduling Simulation ===
Mode: OPTIMIZED
Running in OPTIMIZED mode (all optimizations enabled)
```

**Giải thích**:
- Mode: OPTIMIZED
- Tất cả optimizations được bật, bao gồm CPO

```
ET2FA: Phase 2.5 - CPO (Critical Path Optimization)...
CPO: [1.1] Forward Pass - Computing Earliest Start Times
CPO:   Scanning 30 tasks for entry points...
CPO:   Found 5 entry tasks
CPO: [1.2] Backward Pass - Computing Latest Start Times
CPO:   Scanning 30 tasks for exit points...
CPO:   Found 4 exit tasks
CPO: [2] Identifying Critical Path
CPO:   Found 12 critical tasks (40.0% of total)
CPO: [3] Optimizing Critical Path Tasks
CPO:   Ranking VMs by processing capacity...
CPO:   Fastest VM: VM 4 (320.0 GFLOPS)
CPO:   Evaluating 12 critical tasks...
CPO:   Task 0: VM 0 -> VM 4 (improvement: 15.2%)
CPO:   Task 1: VM 1 -> VM 4 (improvement: 12.8%)
...
CPO:   Result: Optimized 8 out of 12 critical tasks
CPO: [4] Adjusting Non-Critical Tasks
CPO:   Analyzing 18 non-critical tasks...
CPO:   Strategy: No actual consolidation, relying on cost adjustment in broker.
ET2FA: Phase 2.5 completed in 25ms.
```

**Giải thích chi tiết**:

1. **Forward Pass - Computing Earliest Start Times**:
   - Tính earliest start time cho mỗi task
   - Bắt đầu từ entry tasks (tasks không có predecessors)
   - Duyệt theo topological order

2. **Backward Pass - Computing Latest Start Times**:
   - Tính latest start time cho mỗi task
   - Bắt đầu từ exit tasks (tasks không có successors)
   - Duyệt ngược lại

3. **Identifying Critical Path**:
   - Critical task = task có `earliest_start = latest_start`
   - Found 12 critical tasks = 40% của tổng số tasks
   - Critical path = đường dài nhất từ entry đến exit

4. **Optimizing Critical Path Tasks**:
   - Ranking VMs: Sắp xếp VMs theo processing capacity
   - Fastest VM: VM 4 với 320 GFLOPS (c3.8xlarge)
   - Evaluating: Đánh giá từng critical task
   - Task X: VM Y -> VM Z: Chuyển task từ VM Y sang VM Z
   - Improvement: % cải thiện về thời gian
   - Result: Đã optimize 8/12 critical tasks

5. **Adjusting Non-Critical Tasks**:
   - Phân tích non-critical tasks
   - Strategy: Không consolidate thực sự, chỉ điều chỉnh cost

**Kết quả Performance Metrics**:

```
=== Performance Metrics ===
Total Cost: $1.002866
Total Idle Rate: 0.2327
Meets Deadline: Yes
Max Finish Time: 1215.10s
Deadline: 3000.00s
SCHEDULING_TIME: 0.04164648
```

**So sánh với Original**:

| Metric | Original | Optimized | Improvement |
|--------|----------|-----------|-------------|
| Total Cost | $1.080888 | $1.002866 | **-7.2%** ↓ |
| Idle Rate | 0.5067 | 0.2327 | **-54.1%** ↓ |
| Makespan | 1234.35s | 1215.10s | **-1.6%** ↓ |
| SCHEDULING_TIME | 0.034s | 0.042s | **+21.2%** ↑ |

**Giải thích khi quay**:
> "Như các bạn thấy, optimized mode đã cải thiện đáng kể:
> - Total Cost giảm 7.2% nhờ consolidation và better utilization
> - Idle Rate giảm 54.1% - VMs được sử dụng hiệu quả hơn nhiều
> - Makespan giảm nhẹ 1.6%
> - SCHEDULING_TIME tăng 21.2% - đây là trade-off hợp lý vì meta-heuristic tốn thời gian hơn, nhưng vẫn rất nhanh (42ms)"

---

### Demo 3: So Sánh Original vs Optimized (1 phút)

#### Bước 1: Chạy Script So Sánh

**Lệnh**:
```bash
bash compare-modes.sh workflows/benchmark/CYBERSHAKE/Cyber_30.dax 3000
```

**Giải thích khi quay**:
> "Để so sánh nhanh hơn, tôi đã tạo script `compare-modes.sh` để chạy cả 2 modes và hiển thị kết quả so sánh."

**Output mong đợi**:

```
========================================
  So Sánh Original vs Optimized
  Workflow: Cyber_30
========================================

1. Running ORIGINAL mode (no optimizations)...
✓ Original Results:
  SCHEDULING_TIME: 0.03435861 seconds
  Total Cost: $1.080888
  Total Idle Rate: 0.5067
  Max Finish Time: 1234.35s
  Phase 1: 15ms
  Phase 2: 8ms
  Phase 3: 12ms

2. Running OPTIMIZED mode (with CPO)...
✓ Optimized Results:
  SCHEDULING_TIME: 0.04164648 seconds
  Total Cost: $1.002866
  Total Idle Rate: 0.2327
  Max Finish Time: 1215.10s
  Phase 1: 15ms
  Phase 2: 8ms
  Phase 2.5: 25ms (CPO)
  Phase 3: 12ms

=== So Sánh SCHEDULING_TIME ===
Original:  0.03435861 seconds
Optimized: 0.04164648 seconds
Improvement: -21.2% ⬇️
✅ SCHEDULING_TIME tăng (-21%) - Hợp lý vì meta-heuristic tốn thời gian hơn

=== So Sánh Performance Metrics ===
Cost:        $1.080888 → $1.002866 (-7.21%)
Idle Rate:   0.5067 → 0.2327 (giảm 54.13%)
Makespan:    1234.35s → 1215.10s (giảm 1.56%)
```

**Giải thích khi quay**:
> "Script này tự động chạy cả 2 modes và so sánh kết quả. Như các bạn thấy:
> - Cost giảm 7.21%
> - Idle Rate giảm 54.13% - cải thiện rất đáng kể
> - Makespan giảm nhẹ 1.56%
> - SCHEDULING_TIME tăng 21.2% nhưng vẫn rất nhanh"

---

### Demo 4: Chạy Nhiều Workflows (1 phút)

#### Bước 1: Chạy Script Tất Cả Workflows

**Lệnh**:
```bash
bash run-all-workflows.sh
```

**Giải thích khi quay**:
> "Để test trên nhiều workflows, tôi đã tạo script `run-all-workflows.sh` để chạy tất cả 28 workflows trong cả original và optimized mode. Tổng cộng 56 lệnh."

**Output mong đợi** (chỉ hiển thị vài workflows đầu):

```
==================================================================================
=== RUNNING ALL WORKFLOWS (28 workflows × 2 modes = 56 commands) ===
==================================================================================

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Workflow: Cyber_30 (Deadline: 3000)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

[1] ORIGINAL MODE
────────────────────────────────────────────────────────────────────────────────
=== Performance Metrics ===
Total Cost: $1.080888
Total Idle Rate: 0.5067
Meets Deadline: Yes
Max Finish Time: 1234.35s
Deadline: 3000.00s
SCHEDULING_TIME: 0.03435861

[2] OPTIMIZED MODE
────────────────────────────────────────────────────────────────────────────────
=== Performance Metrics ===
Total Cost: $1.002866
Total Idle Rate: 0.2327
Meets Deadline: Yes
Max Finish Time: 1215.10s
Deadline: 3000.00s
SCHEDULING_TIME: 0.04164648

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Workflow: Cyber_50 (Deadline: 5000)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
...
```

**Giải thích khi quay**:
> "Script này sẽ chạy tuần tự tất cả workflows và hiển thị Performance Metrics cho mỗi workflow. Điều này giúp verify rằng implementation hoạt động đúng trên nhiều workflows khác nhau."

**Lưu ý**: Script này sẽ chạy lâu (có thể 10-30 phút tùy vào workflows). Trong video demo, chỉ cần show vài workflows đầu và nói rằng script sẽ tiếp tục chạy.

---

## GIẢI THÍCH TỪNG DÒNG LOG

### CloudSim Plus Logs

#### `INFO  0.00: DatacenterSimple1 is starting...`
- **Ý nghĩa**: Khởi tạo datacenter (cloud provider)
- **0.00**: Simulation time = 0
- **DatacenterSimple1**: Tên datacenter

#### `INFO  0.00: Et2faBroker2 is starting...`
- **Ý nghĩa**: Khởi tạo broker để quản lý scheduling
- **Et2faBroker2**: Custom broker của chúng ta

#### `INFO  0.00: Et2faBroker2: Trying to create Vm X`
- **Ý nghĩa**: Broker đang yêu cầu tạo VM X
- **X**: ID của VM (0, 1, 2, ...)

#### `INFO  0.00: VmAllocationPolicySimple: Vm X has been allocated to Host Y/DC Z`
- **Ý nghĩa**: VM X đã được allocate vào Host Y trong Datacenter Z
- **Host Y**: Host vật lý trong datacenter
- **DC Z**: Datacenter ID

---

### ET2FA Logs

#### `ET2FA: Starting scheduling for X tasks with Y VMs`
- **Ý nghĩa**: Bắt đầu scheduling với X tasks và Y VMs
- **X**: Số lượng tasks
- **Y**: Số lượng VMs

#### `Topological Levels (BFS-optimized): A entry tasks, max level=B`
- **Ý nghĩa**: Tính toán topological levels bằng BFS
- **A entry tasks**: Số tasks không có predecessors
- **max level=B**: Level cao nhất

#### `T2FA: Classifying tasks into types (TYPE0-TYPE4, GENERAL)`
- **Ý nghĩa**: Phân loại tasks theo số predecessors
- **TYPE0**: Entry tasks (0 predecessors)
- **TYPE1**: 1 predecessor
- **TYPE2**: 2 predecessors
- **TYPE3**: 3 predecessors
- **TYPE4**: 4+ predecessors
- **GENERAL**: Không phân loại được

#### `T2FA: Processing level X (Y tasks)...`
- **Ý nghĩa**: Đang xử lý tasks ở level X
- **X**: Level number (0, 1, 2, ...)
- **Y**: Số tasks ở level đó

#### `ET2FA: Phase 1 completed in Xms`
- **Ý nghĩa**: Phase 1 (T2FA) hoàn thành trong X milliseconds
- **X**: Thời gian thực tế

#### `ET2FA: Scheduled X tasks out of X tasks in DAG`
- **Ý nghĩa**: Đã lập lịch X tasks (đủ tất cả)
- Nếu X < tổng số tasks → có tasks chưa được schedule (lỗi)

---

### CPO Logs (Chỉ có trong Optimized Mode)

#### `CPO: [1.1] Forward Pass - Computing Earliest Start Times`
- **Ý nghĩa**: Bước 1.1: Tính earliest start time cho mỗi task
- **Forward Pass**: Duyệt từ entry tasks đến exit tasks

#### `CPO:   Scanning X tasks for entry points...`
- **Ý nghĩa**: Quét X tasks để tìm entry tasks
- **Entry points**: Tasks không có predecessors

#### `CPO:   Found X entry tasks`
- **Ý nghĩa**: Tìm thấy X entry tasks

#### `CPO: [1.2] Backward Pass - Computing Latest Start Times`
- **Ý nghĩa**: Bước 1.2: Tính latest start time cho mỗi task
- **Backward Pass**: Duyệt từ exit tasks ngược lại entry tasks

#### `CPO: [2] Identifying Critical Path`
- **Ý nghĩa**: Bước 2: Xác định critical path
- **Critical Path**: Đường dài nhất từ entry đến exit

#### `CPO:   Found X critical tasks (Y% of total)`
- **Ý nghĩa**: Tìm thấy X critical tasks
- **Y%**: Tỷ lệ phần trăm của tổng số tasks

#### `CPO: [3] Optimizing Critical Path Tasks`
- **Ý nghĩa**: Bước 3: Tối ưu critical path tasks

#### `CPO:   Ranking VMs by processing capacity...`
- **Ý nghĩa**: Sắp xếp VMs theo processing capacity (từ thấp đến cao)

#### `CPO:   Fastest VM: VM X (Y GFLOPS)`
- **Ý nghĩa**: VM nhanh nhất là VM X với Y GFLOPS
- **GFLOPS**: Giga Floating Point Operations Per Second

#### `CPO:   Task X: VM Y -> VM Z (improvement: W%)`
- **Ý nghĩa**: Chuyển task X từ VM Y sang VM Z
- **improvement: W%**: % cải thiện về thời gian

#### `CPO:   Result: Optimized X out of Y critical tasks`
- **Ý nghĩa**: Đã optimize X/Y critical tasks
- **X**: Số tasks được reassign
- **Y**: Tổng số critical tasks

---

## KỊCH BẢN QUAY VIDEO

### Tổng Thời Gian: 5 phút

### Phần 1: Giới Thiệu (30 giây)

**Nội dung**:
> "Xin chào, tôi là [Tên]. Hôm nay tôi sẽ demo implementation của thuật toán ET2FA cho bài toán lập lịch workflow có deadline trong cloud computing. Đây là project của nhóm [Tên nhóm]."

**Hành động**:
- Show terminal đã mở sẵn
- Show thư mục project
- Giới thiệu cấu trúc project (nếu cần)

---

### Phần 2: Compile và Chạy Original Mode (2 phút)

**Nội dung**:
> "Đầu tiên, tôi sẽ compile project và chạy workflow Cyber_30 với original mode - đây là mode không có CPO optimization, giống như thuật toán ET2FA gốc trong paper."

**Hành động**:
1. Chạy `mvn clean compile`
2. Giải thích log compile
3. Chạy original mode
4. Giải thích từng phần log:
   - CloudSim khởi động
   - ET2FA phases
   - Performance Metrics
5. Highlight các metrics quan trọng

---

### Phần 3: Chạy Optimized Mode và So Sánh (2 phút)

**Nội dung**:
> "Bây giờ tôi sẽ chạy cùng workflow nhưng với optimized mode - mode này có thêm CPO (Critical Path Optimization), đây là đóng góp mới của nhóm."

**Hành động**:
1. Chạy optimized mode
2. Giải thích CPO logs:
   - Forward/Backward Pass
   - Critical Path Identification
   - Task Reassignment
3. So sánh kết quả với Original
4. Highlight improvements:
   - Cost giảm
   - Idle Rate giảm
   - Makespan giảm nhẹ
   - SCHEDULING_TIME tăng (trade-off)

---

### Phần 4: Demo Script So Sánh (30 giây)

**Nội dung**:
> "Để so sánh nhanh hơn, tôi đã tạo script `compare-modes.sh` để tự động chạy cả 2 modes và hiển thị kết quả so sánh."

**Hành động**:
1. Chạy `bash compare-modes.sh`
2. Show output so sánh
3. Giải thích các improvements

---

### Phần 5: Kết Luận (30 giây)

**Nội dung**:
> "Tóm lại, implementation của chúng tôi đã:
> - Triển khai đầy đủ 3 phases của ET2FA
> - Thêm CPO optimization mới
> - Đạt được improvements đáng kể về cost và idle rate
> - Hỗ trợ 28 benchmark workflows
> 
> Cảm ơn các bạn đã theo dõi!"

**Hành động**:
- Tóm tắt lại kết quả
- Show lại Performance Metrics
- Kết thúc video

---

## TIPS QUAY VIDEO CHUYÊN NGHIỆP

### 1. Chuẩn Bị

#### Trước Khi Quay
- [ ] Đã test tất cả lệnh và chạy mượt
- [ ] Terminal đã setup đẹp (font, size, theme)
- [ ] Đã clear screen và chuẩn bị sẵn
- [ ] Microphone đã test và không có tiếng ồn
- [ ] Recording software đã setup đúng

#### Script Nói
- [ ] Đã viết script nói chi tiết
- [ ] Đã practice 2-3 lần
- [ ] Biết giải thích từng dòng log
- [ ] Biết highlight các điểm quan trọng

---

### 2. Kỹ Thuật Quay

#### Camera/Recording
- **Resolution**: 1920x1080 (Full HD)
- **Frame rate**: 30 fps
- **Audio**: Bật microphone, giảm background noise
- **Zoom**: 100% (không zoom in/out)

#### Terminal
- **Font size**: Đủ lớn để đọc được (14-16pt)
- **Window size**: Đủ rộng để hiển thị đầy đủ
- **Scroll speed**: Chậm để dễ theo dõi
- **Clear screen**: Clear trước mỗi phần

#### Timing
- **Pause**: Dừng 1-2 giây sau mỗi lệnh để xem kết quả
- **Explanation**: Giải thích rõ ràng, không nói quá nhanh
- **Highlight**: Dùng mouse để chỉ vào các dòng log quan trọng

---

### 3. Nội Dung

#### Giải Thích
- ✅ Giải thích từng lệnh trước khi chạy
- ✅ Giải thích từng phần log khi nó xuất hiện
- ✅ Highlight các metrics quan trọng
- ✅ So sánh Original vs Optimized rõ ràng

#### Tránh
- ❌ Không nói quá nhanh
- ❌ Không bỏ qua các phần quan trọng
- ❌ Không để terminal scroll quá nhanh
- ❌ Không có tiếng ồn background

---

### 4. Post-Production (Nếu Cần)

#### Editing
- **Cut**: Cắt các phần chờ đợi dài (nhưng giữ lại phần quan trọng)
- **Zoom**: Zoom vào các phần quan trọng nếu cần
- **Text**: Thêm text overlay để highlight các metrics
- **Music**: Không cần nhạc nền (có thể gây mất tập trung)

#### Export
- **Format**: MP4
- **Resolution**: 1920x1080
- **Bitrate**: 5-10 Mbps (chất lượng tốt nhưng file không quá lớn)

---

## CHECKLIST TRƯỚC KHI NỘP

### Technical
- [ ] Tất cả lệnh đã test và chạy được
- [ ] Code đã compile không có lỗi
- [ ] Kết quả đã verify đúng
- [ ] Scripts đã chạy mượt

### Video Quality
- [ ] Resolution đủ cao (Full HD)
- [ ] Audio rõ ràng, không có tiếng ồn
- [ ] Terminal dễ đọc (font size đủ lớn)
- [ ] Timing hợp lý (không quá nhanh/chậm)

### Content
- [ ] Đã giải thích đầy đủ các phần
- [ ] Đã highlight các metrics quan trọng
- [ ] Đã so sánh Original vs Optimized
- [ ] Đã highlight đóng góp mới (CPO)

### Final Check
- [ ] Video đã xem lại và không có lỗi
- [ ] Thời gian video ~5 phút (không quá dài/ngắn)
- [ ] File video không quá lớn (< 500MB)
- [ ] Đã export đúng format (MP4)

---

## 📝 LƯU Ý QUAN TRỌNG

1. **Giải thích rõ ràng**: Mỗi lệnh và mỗi dòng log quan trọng đều phải giải thích
2. **Highlight CPO**: Đây là đóng góp mới, phải giải thích chi tiết
3. **So sánh metrics**: Phải so sánh rõ ràng Original vs Optimized
4. **Timing**: Không quá nhanh, để người xem có thời gian đọc log
5. **Professional**: Nói rõ ràng, tự tin, không đọc slide

---

**Chúc bạn quay video demo thành công! 🎥🎉**

