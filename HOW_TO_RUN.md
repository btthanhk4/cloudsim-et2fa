# Hướng dẫn chạy Project ET2FA

## 🚀 Cách 1: Chạy nhanh (Windows)

### Dùng file batch (Dễ nhất)

```cmd
run.bat
```

Script này sẽ:
1. Compile project
2. Chạy simulation với workflow mẫu (4 tasks)

## 🚀 Cách 2: Chạy với Maven

### Chạy workflow mẫu (không có DAX file)

```cmd
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App"
```

### Chạy với DAX file

```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-test.dax --deadline=3000"
```

### Chạy với workflow lớn

```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-2deg-3bands.dax --deadline=5000"
```

## 📋 Các tham số

### --dax
Đường dẫn đến file DAX workflow

**Ví dụ:**
- `--dax=workflows/sample.dax` - Workflow mẫu đơn giản (3 tasks)
- `--dax=workflows/montage-test.dax` - Workflow test (27 tasks)
- `--dax=workflows/montage-2deg-3bands.dax` - Workflow thật (165 tasks)

### --deadline
Deadline cho workflow (giây). Mặc định: 1000 giây

**Ví dụ:**
- `--deadline=1000` - 1000 giây
- `--deadline=3000` - 3000 giây
- `--deadline=5000` - 5000 giây

**Lưu ý**: Workflow lớn cần deadline cao hơn

## 🎯 Ví dụ đầy đủ

### 1. Chạy workflow mẫu (4 tasks)

```cmd
run.bat
```

Hoặc:
```cmd
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App"
```

**Kết quả mong đợi:**
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

### 2. Chạy với workflow nhỏ (sample.dax - 3 tasks)

```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/sample.dax --deadline=1000"
```

### 3. Chạy với workflow test (montage-test.dax - 27 tasks)

```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-test.dax --deadline=3000"
```

### 4. Chạy với workflow thật (montage-2deg-3bands.dax - 165 tasks)

```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/montage-2deg-3bands.dax --deadline=5000"
```

**Lưu ý**: Workflow này lớn, có thể mất vài phút để chạy.

## 🔧 Yêu cầu hệ thống

### Bắt buộc
- **Java 17+** (đã cài: Java 17.0.15)
- **Maven 3.6+** (đã cài: Maven 3.9.9)

### Kiểm tra
```cmd
java -version
mvn --version
```

## ❓ Troubleshooting

### Lỗi: "mvn: command not found"
**Giải pháp**: Đảm bảo Maven đã được cài và có trong PATH

### Lỗi: "Java version error"
**Giải pháp**: Cần Java 17+. Kiểm tra: `java -version`

### Lỗi: "DAX file not found"
**Giải pháp**: Kiểm tra đường dẫn file DAX. Dùng đường dẫn tương đối từ thư mục project.

### Lỗi: "Deadline too short"
**Giải pháp**: Tăng deadline. Với workflow lớn (165 tasks), dùng `--deadline=5000` hoặc cao hơn.

### Lỗi: "Compilation failed"
**Giải pháp**: 
```cmd
mvn clean compile
```
Kiểm tra lỗi compile và sửa.

## 📊 So sánh các workflows

| Workflow | Số tasks | Deadline khuyến nghị | Thời gian chạy |
|----------|----------|---------------------|----------------|
| Sample (mẫu) | 4 | 1000s | ~1 giây |
| sample.dax | 3 | 1000s | ~1 giây |
| montage-test.dax | 27 | 3000s | ~5-10 giây |
| montage-2deg-3bands.dax | 165 | 5000s | ~1-2 phút |

## 💡 Tips

1. **Bắt đầu nhỏ**: Test với workflow mẫu (4 tasks) trước
2. **Tăng dần**: Sau đó test với workflows lớn hơn
3. **Deadline hợp lý**: Set deadline cao hơn với workflows lớn
4. **Kiểm tra kết quả**: Xem cost, idle rate, và deadline status

## 🎉 Hoàn thành!

Bạn đã sẵn sàng chạy ET2FA algorithm với các workflows khác nhau!

