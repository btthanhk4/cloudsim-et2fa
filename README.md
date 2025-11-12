# ET2FA: Thuật toán Task Type First nâng cao

Triển khai thuật toán ET2FA cho bài toán lập lịch workflow có ràng buộc deadline trong điện toán đám mây, dựa trên bài báo:

**"ET2FA: A Hybrid Heuristic Algorithm for Deadline-constrained Workflow Scheduling in Cloud"**

Zaixing Sun, Boyu Zhang, Chonglin Gu, Ruitao Xie, Bin Qian, và Hejiao Huang  
*IEEE Transactions on Services Computing, 2022*

## Tổng quan

ET2FA là một thuật toán heuristic lai giải quyết bài toán lập lịch workflow có ràng buộc deadline trong môi trường điện toán đám mây với các tính năng sau:

- Thanh toán theo giây với tối thiểu 60 giây
- Khả năng ngủ đông (hibernation) instance
- Tài nguyên VM không đồng nhất (heterogeneous)
- Số lượng VM không giới hạn

## Các thành phần của thuật toán

Thuật toán ET2FA bao gồm ba giai đoạn chính:

### 1. T2FA (Task Type First Algorithm - Thuật toán ưu tiên loại task)

- Lập lịch các task dựa trên mức độ topo và loại task
- Phân loại task thành Type0-Type4 dựa trên cấu trúc DAG:
  - **Type0**: Task đơn lẻ trong mức độ topo của nó
  - **Type1**: Nút cha trong cấu trúc MOSI (Multiple Output Single Input - Nhiều đầu ra, một đầu vào)
  - **Type2**: Nút con trong cấu trúc MOSI
  - **Type3**: Nút cha trong cấu trúc SOMI (Single Output Multiple Input - Một đầu ra, nhiều đầu vào)
  - **Type4**: Nút con trong cấu trúc SOMI
- Sử dụng điều kiện lập lịch compact để chọn VM

### 2. DOBS (Delay Operation Based on Block Structure - Thao tác trì hoãn dựa trên cấu trúc khối)

- Tối ưu hóa lập lịch bằng cách trì hoãn các cấu trúc khối
- Triển khai Định lý 1 từ bài báo
- Giảm thời gian nhàn rỗi và chi phí

### 3. IHSH (Instance Hibernate Scheduling Heuristic - Heuristic lập lịch ngủ đông instance)

- Lập lịch ngủ đông instance trong các khoảng thời gian nhàn rỗi
- Tối thiểu hóa chi phí bằng cách sử dụng chế độ ngủ đông

## Cấu trúc dự án

```
cloudsim-et2fa/
├── src/main/java/vn/et2fa/
│   ├── model/
│   │   ├── Et2faTask.java          # Cloudlet mở rộng với các thuộc tính ET2FA
│   │   └── TaskType.java           # Enum các loại task
│   ├── algorithm/
│   │   ├── T2FAAlgorithm.java       # Giai đoạn 1: Task Type First Algorithm
│   │   ├── DOBSAlgorithm.java      # Giai đoạn 2: Delay Operation Based on Block Structure
│   │   └── IHSHAlgorithm.java      # Giai đoạn 3: Instance Hibernate Scheduling Heuristic
│   ├── broker/
│   │   └── Et2faBroker.java        # Broker chính triển khai ET2FA
│   ├── util/
│   │   ├── WorkflowDAG.java        # Biểu diễn DAG và các tiện ích
│   │   ├── DaxLoader.java          # Bộ tải PEGASUS DAX XML
│   │   └── VmConfig.java           # Cấu hình VM
│   └── App.java                     # Ứng dụng mẫu
└── pom.xml                          # Cấu hình Maven
```

## Ví dụ sử dụng

```java
// 1. Tạo môi trường mô phỏng và broker
CloudSim simulation = new CloudSim();
Et2faBroker broker = new Et2faBroker(simulation);

// 2. Tạo các VM
List<Vm> vmList = ...;
broker.submitVmList(vmList);

// 3. Tạo các task workflow
List<Et2faTask> tasks = ...;
broker.submitCloudletList(tasks);

// 4. Xây dựng DAG workflow với các phụ thuộc
Map<String, List<String>> dependencies = new HashMap<>();
dependencies.put("0", Arrays.asList("1", "2"));
// ... thêm các phụ thuộc khác

Map<String, Double> dataTransfers = new HashMap<>();
dataTransfers.put("0_1", 100.0);
// ... thêm các chuyển dữ liệu khác

broker.buildWorkflowDAG(tasks, dependencies, dataTransfers);

// 5. Thiết lập deadline
broker.setDeadline(1000.0);

// 6. Thực thi thuật toán ET2FA
broker.executeET2FA();

// 7. Chạy mô phỏng
simulation.start();

// 8. Lấy kết quả
double totalCost = broker.calculateTotalCost();
double idleRate = broker.calculateTotalIdleRate();
boolean meetsDeadline = broker.meetsDeadline();
```

## Chạy với DAX (Pegasus Workflows)

Ứng dụng hỗ trợ tải trực tiếp file Pegasus DAX XML:

```bash
# Ví dụ
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=/đường/dẫn/đến/workflow.dax --deadline=1500"
```

- `--dax`: đường dẫn đến file Pegasus DAX (ví dụ: CyberShake, Epigenomics, Inspiral, Montage, Sipht)
- `--deadline`: deadline tính bằng giây (tùy chọn, mặc định 1000)

**Lưu ý:**
- Bộ tải đọc các phần tử `<job>` (sử dụng `runtime` làm computation nếu có)
- Các phụ thuộc được đọc từ các phần tử `<child><parent/></child>`
- Chuyển dữ liệu được ước tính từ kích thước file đầu ra

## Tạo file DAX Montage (Windows)

### Bắt đầu nhanh

1. **Tạo file DAX** (chỉ cần Python 3, không cần Pegasus WMS):
   ```cmd
   python generate-montage-dax-simple.py --center "56.7 24.0" --degrees 1.0 --bands 1 --output workflows/montage-test.dax
   ```

2. **Chạy với ET2FA**:
   ```cmd
   mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-test.dax --deadline=3000"
   ```

### Cài đặt Windows

**Yêu cầu:**
- Python 3.6+ (để tạo file DAX, không cần Pegasus WMS!)
- Java 17+
- Maven 3.6+

## Tính năng chính

- **Hỗ trợ Workflow DAG**: Xử lý các cấu trúc workflow phức tạp với các phụ thuộc
- **Tính toán mức độ Topo**: Tự động tính toán mức độ của các task
- **Phân loại loại Task**: Nhận diện các cấu trúc DAG đặc biệt (SOSI, MOSI, SOMI, MOMI)
- **Tối ưu hóa cấu trúc khối**: Trì hoãn các task để giảm thời gian nhàn rỗi
- **Lập lịch ngủ đông**: Tự động lập lịch ngủ đông VM
- **Tính toán chi phí**: Tính tổng chi phí bao gồm chi phí chạy và chi phí ngủ đông
- **Tính toán tỷ lệ nhàn rỗi**: Đo lường mức độ sử dụng tài nguyên

## Phụ thuộc

- CloudSim Plus 7.3.0
- Java 17+
- Maven 3.6+

## Biên dịch

```bash
mvn clean compile
mvn package
```

## Chạy chương trình

### Bắt đầu nhanh (Windows)

```cmd
run.bat
```

### Chạy với Maven

```bash
# Workflow mẫu (4 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App"

# Với file DAX
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-test.dax --deadline=3000"

# Với workflow lớn (500 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_500.dax --deadline=10000"
```

## Các Workflow có sẵn

Dự án đã có sẵn **15 workflows** được tổ chức trong `workflows/benchmark/`:

- **CyberShake**: 50, 100, 500 tasks
- **Epigenomics**: 50, 100, 500 tasks
- **Inspiral**: 50, 100, 500 tasks
- **Montage**: 50, 100, 500 tasks
- **Sipht**: 50, 100, 500 tasks

### Chạy batch test (Tất cả workflows)

**Windows:**
```powershell
.\run-batch-tests.ps1
```

**Linux/Mac:**
```bash
./test-all-workflows.sh
```

Hoặc chạy tất cả workflows bằng script Windows:
```cmd
run-all-workflows.bat
```

### Chạy từng workflow

**CyberShake:**
```bash
# 50 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"

# 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_100.dax --deadline=5000"

# 500 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_500.dax --deadline=15000"
```

**Epigenomics:**
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_500.dax --deadline=15000"
```

**Inspiral:**
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_500.dax --deadline=15000"
```

**Montage:**
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_500.dax --deadline=15000"
```

**Sipht:**
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_500.dax --deadline=15000"
```

## Tham khảo bài báo

- **Bài báo gốc**: IEEE Transactions on Services Computing, 2022
- **DOI**: 10.1109/TSC.2022.3196620
- **Phiên bản hội nghị**: IEEE CLOUD 2021

## Ghi chú

- Triển khai tuân theo các thuật toán được mô tả trong bài báo
- Một số đơn giản hóa đã được thực hiện để tương thích với CloudSim Plus
- Mô hình định giá VM cần được cấu hình dựa trên giá của nhà cung cấp đám mây thực tế
- Thời gian chuyển dữ liệu được tính toán dựa trên băng thông VM

## Giấy phép

Triển khai này dành cho mục đích giáo dục và nghiên cứu.
