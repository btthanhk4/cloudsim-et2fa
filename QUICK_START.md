# Quick Start Guide - ET2FA

## Chạy nhanh trong Git Bash (Windows)

```bash
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App"
```

## Chạy trong Windows CMD/PowerShell

```bash
run.bat
```

## Chạy trong Linux/Mac

```bash
./run.sh
```

## Kết quả mong đợi

Khi chạy thành công, bạn sẽ thấy:

```
=== ET2FA Workflow Scheduling Simulation ===
Created 4 tasks
...

=== Scheduling Results ===
Task 0: VM 4, Start: 55.90s, Finish: 59.90s, Level: 0, Type: TYPE0
Task 1: VM 3, Start: 60.70s, Finish: 64.70s, Level: 1, Type: TYPE2
Task 2: VM 4, Start: 59.90s, Finish: 64.70s, Level: 1, Type: TYPE2
Task 3: VM 4, Start: 64.70s, Finish: 70.70s, Level: 2, Type: TYPE0

=== Performance Metrics ===
Total Cost: $0.008264
Total Idle Rate: 1.7239
Meets Deadline: Yes
```

## Giải thích kết quả

- **Task 0-3**: Các tasks trong workflow
- **VM**: Virtual Machine được assign cho task
- **Start/Finish**: Thời gian bắt đầu và kết thúc (giây)
- **Level**: Topological level (0 = entry, cao hơn = phụ thuộc nhiều hơn)
- **Type**: Task type (TYPE0, TYPE2, etc.)
- **Total Cost**: Chi phí tổng để chạy workflow
- **Total Idle Rate**: Tỷ lệ idle của VMs
- **Meets Deadline**: Có đáp ứng deadline không

## Troubleshooting

### Lỗi: "command not found"
- Đảm bảo đã cài Maven: `mvn --version`
- Đảm bảo đã cài Java 17+: `java -version`

### Lỗi: "No VMs available"
- Code sẽ tự tạo VMs khi chạy simulation
- Nếu vẫn lỗi, kiểm tra file `App.java` phần tạo VMs

### Muốn tùy chỉnh workflow?
- Sửa file `src/main/java/vn/et2fa/App.java`
- Thay đổi số tasks, dependencies, VM configurations

