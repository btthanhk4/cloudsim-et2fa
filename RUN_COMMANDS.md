# Các câu lệnh chạy Workflows DAX

## 🚀 Chạy nhanh (Workflow mẫu)

```cmd
run.bat
```

Chạy workflow mẫu với 4 tasks (built-in).

## 📊 Chạy các Workflows Benchmark

### 1. CyberShake Workflows

```cmd
REM Small (50 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_100.dax --deadline=5000"

REM Large (1000 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_1000.dax --deadline=20000"
```

### 2. Epigenomics Workflows

**Lưu ý**: Epigenomics workflows có thể cần deadline cao hơn so với các workflows khác.

```cmd
REM Small (47 tasks) - Cần deadline cao hơn
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_50.dax --deadline=5000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_100.dax --deadline=6000"

REM Large (997 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_1000.dax --deadline=25000"
```

### 3. Inspiral Workflows

```cmd
REM Small (50 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_50.dax --deadline=3000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_100.dax --deadline=5000"

REM Large (1000 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_1000.dax --deadline=20000"
```

### 4. Montage Workflows

```cmd
REM Small (50 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_100.dax --deadline=5000"

REM Large (1000 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_1000.dax --deadline=20000"
```

### 5. Sipht Workflows

```cmd
REM Small (48 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_50.dax --deadline=3000"

REM Medium (97 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_100.dax --deadline=5000"

REM Large (968 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_1000.dax --deadline=20000"
```

## 🎯 Chạy Batch Test (Tất cả workflows)

```powershell
.\run-batch-tests.ps1
```

Script này sẽ:
- Chạy ET2FA trên tất cả 15 workflows
- Thu thập kết quả (Cost, Idle Rate, Meets Deadline, Running Time)
- Export kết quả ra file CSV: `results/batch-test-results.csv`

## 📋 Deadline khuyến nghị (Đã test)

| Workflow Size | Deadline | Workflows | Notes |
|---------------|----------|-----------|-------|
| Small (~50 tasks) | 3000s | CyberShake_50, Inspiral_50, Montage_50, Sipht_50 | Tăng từ 2000s |
| Small (~50 tasks) | 5000s | Epigenomics_50 | Cần deadline cao hơn |
| Medium (~100 tasks) | 5000s | CyberShake_100, Inspiral_100, Montage_100, Sipht_100 | Tăng từ 4000s |
| Medium (~100 tasks) | 6000s | Epigenomics_100 | Cần deadline cao hơn |
| Large (~1000 tasks) | 20000s | CyberShake_1000, Inspiral_1000, Montage_1000, Sipht_1000 | Tăng từ 15000s |
| Large (~1000 tasks) | 25000s | Epigenomics_1000 | Cần deadline cao hơn |

## 💡 Tips

1. **Bắt đầu nhỏ**: Test với workflows nhỏ (~50 tasks) trước
2. **Tăng dần**: Sau đó test với workflows lớn hơn
3. **Deadline hợp lý**: Tăng deadline nếu workflow lớn
4. **Batch test**: Dùng `run-batch-tests.ps1` để test tất cả workflows cùng lúc

## 📊 Kết quả

Khi chạy thành công, bạn sẽ thấy:

```
=== Scheduling Results ===
Task X: VM Y, Start: Zs, Finish: Ws, Level: L, Type: TYPE

=== Performance Metrics ===
Total Cost: $X.XXXXXX
Total Idle Rate: X.XXXX
Meets Deadline: Yes/No
```

## 🔧 Troubleshooting

### Lỗi: "DAX file not found"
**Giải pháp**: Kiểm tra đường dẫn file DAX. Dùng đường dẫn tương đối từ thư mục project.

### Lỗi: "Deadline too short"
**Giải pháp**: Tăng deadline. Với workflows lớn (1000 tasks), dùng `--deadline=15000` hoặc cao hơn.

### Lỗi: "Out of memory"
**Giải pháp**: Workflow quá lớn. Tăng heap size: `-Xmx2g` trong Maven.

