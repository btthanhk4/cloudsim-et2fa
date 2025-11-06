# Hướng dẫn chạy ET2FA

## Cách 1: Chạy bằng Script (Dễ nhất) ⭐

### Windows (CMD/PowerShell):
```bash
run.bat
```
Hoặc double-click file `run.bat` trong Windows Explorer.

### Windows (Git Bash):
```bash
cmd //c run.bat
```

### Linux/Mac:
```bash
chmod +x run.sh
./run.sh
```

## Cách 2: Chạy bằng Maven (Thủ công)

### Bước 1: Mở terminal/command prompt
```bash
cd cloudsim-et2fa
```

### Bước 2: Compile code
```bash
mvn clean compile
```

### Bước 3: Chạy chương trình
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App"
```

### Hoặc chạy một lệnh:
```bash
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App"
```

## Cách 2: Chạy bằng IDE (IntelliJ IDEA / Eclipse)

### IntelliJ IDEA:
1. Mở project: File → Open → chọn thư mục `cloudsim-et2fa`
2. Maven sẽ tự động download dependencies
3. Click chuột phải vào file `App.java`
4. Chọn "Run 'App.main()'"

### Eclipse:
1. File → Import → Maven → Existing Maven Projects
2. Chọn thư mục `cloudsim-et2fa`
3. Mở file `App.java`
4. Click chuột phải → Run As → Java Application

## Cách 3: Chạy JAR file (sau khi build)

### Build JAR:
```bash
mvn clean package
```

### Chạy JAR:
```bash
java -cp target/cloudsim-et2fa-1.0-SNAPSHOT.jar:target/lib/* vn.et2fa.App
```

## Kết quả mong đợi

Khi chạy thành công, bạn sẽ thấy output như:

```
=== ET2FA Workflow Scheduling Simulation ===
Created 4 tasks
  Task 0 -> ID: 0
  Task 1 -> ID: 1
  Task 2 -> ID: 2
  Task 3 -> ID: 3

=== Scheduling Results ===
Task 0: VM 0, Start: 55.90s, Finish: 65.90s, Level: 0, Type: GENERAL
Task 1: VM 1, Start: 65.90s, Finish: 69.90s, Level: 1, Type: GENERAL
...

=== Performance Metrics ===
Total Cost: $0.000123
Total Idle Rate: 0.1234
Meets Deadline: Yes

=== Simulation Complete ===
```

## Troubleshooting

### Lỗi: "No VMs available"
- Đảm bảo bạn đã submit VM list trước khi chạy ET2FA
- Kiểm tra: `broker.submitVmList(vmList);`

### Lỗi: Dependencies không tìm thấy
- Chạy `mvn clean install` để download dependencies
- Kiểm tra internet connection

### Lỗi: Compile error
- Kiểm tra Java version: cần Java 17+
- Xóa thư mục `target` và compile lại: `mvn clean compile`

## Tùy chỉnh workflow

Bạn có thể sửa file `App.java` để:
- Thay đổi số lượng tasks
- Thay đổi dependencies giữa tasks
- Thay đổi VM configurations
- Thay đổi deadline

Ví dụ tạo workflow phức tạp hơn:
```java
// Tạo 10 tasks
List<Et2faTask> tasks = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    tasks.add(new Et2faTask(10000 + i * 1000, 1, TaskType.GENERAL));
}

// Tạo dependencies phức tạp
Map<String, List<String>> deps = new HashMap<>();
deps.put("0", Arrays.asList("1", "2", "3")); // Task 0 -> 1,2,3
deps.put("1", Arrays.asList("4", "5"));
deps.put("2", Arrays.asList("4"));
// ... more dependencies
```

