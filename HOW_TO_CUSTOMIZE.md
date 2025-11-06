# Hướng dẫn thay đổi thông số test

## 📍 File chính: `App.java`

Tất cả thông số test được định nghĩa trong file `src/main/java/vn/et2fa/App.java`

---

## 🔧 Các thông số có thể thay đổi

### 1. **Số lượng và cấu hình VMs**

**Vị trí**: Dòng 53-66 trong `App.java`

```java
// Thay đổi số lượng VMs
for (int i = 0; i < 5; i++) {  // ← Thay 5 thành số VMs bạn muốn
    // ...
}

// Thay đổi MIPS của VMs
double[] mips = {500, 1000, 1500, 2000, 2500}; // ← Thay đổi giá trị

// Ví dụ: Tạo 3 VMs với MIPS cao hơn
double[] mips = {2000, 4000, 8000};
for (int i = 0; i < 3; i++) {
    Vm vm = new VmSimple(mips[i], 1)
            .setRam(2048 * (i + 1))  // ← Thay đổi RAM
            .setBw(1000 * (i + 1))   // ← Thay đổi Bandwidth
            .setSize(10000)          // ← Thay đổi Storage
            .setCloudletScheduler(new CloudletSchedulerTimeShared());
    vmList.add(vm);
}
```

**Thông số VM**:
- `mips[i]`: Processing capacity (MIPS)
- `setRam()`: RAM (MB)
- `setBw()`: Bandwidth (Mbps)
- `setSize()`: Storage size (MB)

---

### 2. **Số lượng và computation của Tasks**

**Vị trí**: Hàm `createSampleWorkflow()` (dòng 110-128)

```java
private static List<Et2faTask> createSampleWorkflow() {
    List<Et2faTask> tasks = new ArrayList<>();
    
    // Thay đổi số lượng tasks
    tasks.add(new Et2faTask(10000, 1, TaskType.GENERAL)); // Task 0
    tasks.add(new Et2faTask(8000, 1, TaskType.GENERAL));   // Task 1
    tasks.add(new Et2faTask(12000, 1, TaskType.GENERAL)); // Task 2
    tasks.add(new Et2faTask(15000, 1, TaskType.GENERAL)); // Task 3
    
    return tasks;
}
```

**Thông số Task**:
- `10000`: Computation (length) - khối lượng tính toán
- `1`: Number of PEs (CPU cores cần thiết)
- `TaskType.GENERAL`: Loại task

**Ví dụ tạo workflow lớn hơn**:
```java
private static List<Et2faTask> createSampleWorkflow() {
    List<Et2faTask> tasks = new ArrayList<>();
    
    // Tạo 10 tasks với computation khác nhau
    for (int i = 0; i < 10; i++) {
        long computation = 5000 + i * 2000; // 5000, 7000, 9000, ...
        tasks.add(new Et2faTask(computation, 1, TaskType.GENERAL));
    }
    
    return tasks;
}
```

---

### 3. **Dependencies giữa Tasks**

**Vị trí**: Dòng 72-76 trong `App.java`

```java
Map<String, List<String>> dependencies = new HashMap<>();
dependencies.put("0", Arrays.asList("1", "2")); // Task 0 → Task 1, Task 2
dependencies.put("1", Arrays.asList("3"));
dependencies.put("2", Arrays.asList("3"));
```

**Cú pháp**:
- `"0"`: Task ID (index trong list)
- `Arrays.asList("1", "2")`: Danh sách tasks phụ thuộc

**Ví dụ workflow phức tạp hơn**:
```java
// Workflow với 10 tasks
// Task 0 → Task 1, 2, 3
// Task 1, 2 → Task 4
// Task 3 → Task 5
// Task 4, 5 → Task 6
// Task 6 → Task 7, 8
// Task 7, 8 → Task 9

dependencies.put("0", Arrays.asList("1", "2", "3"));
dependencies.put("1", Arrays.asList("4"));
dependencies.put("2", Arrays.asList("4"));
dependencies.put("3", Arrays.asList("5"));
dependencies.put("4", Arrays.asList("6"));
dependencies.put("5", Arrays.asList("6"));
dependencies.put("6", Arrays.asList("7", "8"));
dependencies.put("7", Arrays.asList("9"));
dependencies.put("8", Arrays.asList("9"));
```

---

### 4. **Data Transfer giữa Tasks**

**Vị trí**: Dòng 78-82 trong `App.java`

```java
Map<String, Double> dataTransfers = new HashMap<>();
dataTransfers.put("0_1", 100.0); // Task 0 → Task 1: 100 GFLOP
dataTransfers.put("0_2", 200.0);
dataTransfers.put("1_3", 150.0);
dataTransfers.put("2_3", 150.0);
```

**Cú pháp**:
- `"0_1"`: Key = "fromTask_toTask"
- `100.0`: Data size (GFLOP)

**Ví dụ với workflow lớn**:
```java
// Data transfer tương ứng với dependencies
dataTransfers.put("0_1", 100.0);
dataTransfers.put("0_2", 150.0);
dataTransfers.put("0_3", 200.0);
dataTransfers.put("1_4", 120.0);
dataTransfers.put("2_4", 130.0);
dataTransfers.put("3_5", 140.0);
// ... tiếp tục
```

---

### 5. **Deadline**

**Vị trí**: Dòng 86 trong `App.java`

```java
broker.setDeadline(1000.0); // 1000 giây
```

**Thay đổi deadline**:
```java
broker.setDeadline(500.0);  // Deadline ngắn hơn (khó hơn)
broker.setDeadline(2000.0); // Deadline dài hơn (dễ hơn)
```

**Công thức tính deadline** (theo paper):
```java
// Deadline = deadlineFactor × maxFinishTime
// deadlineFactor thường là: 0.8, 1.1, 1.5, 1.8
double deadlineFactor = 1.5;
double estimatedMaxFinishTime = ...; // Tính từ tasks
double deadline = deadlineFactor * estimatedMaxFinishTime;
```

---

### 6. **Cấu hình Hosts**

**Vị trí**: Dòng 35-48 trong `App.java`

```java
// Số lượng hosts
for (int i = 0; i < 5; i++) {  // ← Thay đổi số hosts
    
    // Số PEs (CPU cores) mỗi host
    peList.add(new PeSimple(2000, new PeProvisionerSimple())); // PE 1: 2000 MIPS
    peList.add(new PeSimple(2000, new PeProvisionerSimple())); // PE 2: 2000 MIPS
    
    // Cấu hình host
    Host host = new HostSimple(
        8192,      // RAM (MB)
        50000,     // Bandwidth (Mbps)
        10000000,  // Storage (MB)
        peList
    );
}
```

**Ví dụ tạo hosts mạnh hơn**:
```java
for (int i = 0; i < 3; i++) {
    List<Pe> peList = new ArrayList<>();
    // 4 cores, mỗi core 3000 MIPS
    for (int j = 0; j < 4; j++) {
        peList.add(new PeSimple(3000, new PeProvisionerSimple()));
    }
    
    Host host = new HostSimple(
        16384,     // 16GB RAM
        100000,    // 100Gbps
        20000000,  // 20GB Storage
        peList
    );
    hostList.add(host);
}
```

---

## 📝 Ví dụ: Tạo workflow test lớn

```java
// 1. Tạo 20 tasks
private static List<Et2faTask> createLargeWorkflow() {
    List<Et2faTask> tasks = new ArrayList<>();
    Random rand = new Random();
    
    for (int i = 0; i < 20; i++) {
        // Computation ngẫu nhiên từ 5000-25000
        long computation = 5000 + rand.nextInt(20000);
        tasks.add(new Et2faTask(computation, 1, TaskType.GENERAL));
    }
    
    return tasks;
}

// 2. Tạo dependencies phức tạp
Map<String, List<String>> dependencies = new HashMap<>();
// Level 0
dependencies.put("0", Arrays.asList("1", "2", "3"));
// Level 1
dependencies.put("1", Arrays.asList("4", "5"));
dependencies.put("2", Arrays.asList("5", "6"));
dependencies.put("3", Arrays.asList("6", "7"));
// Level 2
dependencies.put("4", Arrays.asList("8"));
dependencies.put("5", Arrays.asList("8", "9"));
dependencies.put("6", Arrays.asList("9", "10"));
dependencies.put("7", Arrays.asList("10", "11"));
// ... tiếp tục

// 3. Data transfers tương ứng
Map<String, Double> dataTransfers = new HashMap<>();
for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
    String from = entry.getKey();
    for (String to : entry.getValue()) {
        // Data transfer ngẫu nhiên 50-500 GFLOP
        double dataSize = 50 + rand.nextDouble() * 450;
        dataTransfers.put(from + "_" + to, dataSize);
    }
}

// 4. Deadline chặt hơn
broker.setDeadline(500.0); // Deadline ngắn hơn
```

---

## 🎯 Các test cases phổ biến

### Test Case 1: Workflow nhỏ (4 tasks)
```java
// Đã có sẵn trong App.java
// 4 tasks, 5 VMs, deadline 1000s
```

### Test Case 2: Workflow trung bình (10 tasks)
```java
// 10 tasks
// 5 VMs
// Deadline 2000s
```

### Test Case 3: Workflow lớn (50+ tasks)
```java
// 50 tasks
// 10 VMs
// Deadline 5000s
```

### Test Case 4: Deadline chặt
```java
// Deadline ngắn để test constraint
broker.setDeadline(100.0);
```

### Test Case 5: Nhiều VMs
```java
// Tạo 10-20 VMs để test resource allocation
for (int i = 0; i < 20; i++) {
    // ...
}
```

---

## 🔍 Cách test các thông số

### 1. Test với deadline khác nhau
```java
double[] deadlines = {500.0, 1000.0, 1500.0, 2000.0};
for (double deadline : deadlines) {
    broker.setDeadline(deadline);
    broker.executeET2FA();
    System.out.println("Deadline: " + deadline + 
                      ", Cost: " + broker.calculateTotalCost() +
                      ", Meets: " + broker.meetsDeadline());
}
```

### 2. Test với số VMs khác nhau
```java
int[] vmCounts = {3, 5, 10, 20};
for (int count : vmCounts) {
    // Tạo VMs
    List<Vm> vms = createVMs(count);
    // Test và so sánh
}
```

### 3. Test với workflow sizes khác nhau
```java
int[] taskCounts = {10, 20, 50, 100};
for (int count : taskCounts) {
    List<Et2faTask> tasks = createWorkflow(count);
    // Test và đo performance
}
```

---

## ⚠️ Lưu ý khi thay đổi

1. **Dependencies phải hợp lệ**:
   - Không có cycle (vòng lặp)
   - Task IDs phải tồn tại trong list tasks

2. **Data transfers phải match dependencies**:
   - Mỗi dependency nên có data transfer tương ứng

3. **VMs phải đủ cho tasks**:
   - Nếu không đủ, sẽ có warning "No suitable host"

4. **Deadline phải hợp lý**:
   - Quá ngắn → có thể không meet deadline
   - Quá dài → không có ý nghĩa test

5. **Computation phải phù hợp**:
   - Quá lớn → chạy lâu
   - Quá nhỏ → không có ý nghĩa

---

## 🚀 Quick Start - Thay đổi nhanh

### Thay đổi số tasks:
```java
// Dòng 114-117: Thêm/bớt tasks
tasks.add(new Et2faTask(10000, 1, TaskType.GENERAL));
```

### Thay đổi dependencies:
```java
// Dòng 73-76: Sửa dependencies
dependencies.put("0", Arrays.asList("1", "2")); // ← Sửa đây
```

### Thay đổi deadline:
```java
// Dòng 86: Thay đổi deadline
broker.setDeadline(1500.0); // ← Sửa đây
```

### Thay đổi số VMs:
```java
// Dòng 58: Thay đổi số VMs
for (int i = 0; i < 10; i++) { // ← Sửa đây
```

---

## 📊 Sau khi thay đổi

1. **Compile lại**:
```bash
mvn clean compile
```

2. **Chạy lại**:
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App"
```

3. **Xem kết quả**:
- Total Cost
- Total Idle Rate
- Meets Deadline
- Scheduling details

---

## 💡 Tips

1. **Bắt đầu với thay đổi nhỏ**: Test với 1-2 thông số trước
2. **Ghi lại kết quả**: So sánh khi thay đổi thông số
3. **Test từng phần**: Test VMs riêng, tasks riêng, dependencies riêng
4. **Dùng các giá trị thực tế**: Dựa trên paper hoặc real-world workflows

