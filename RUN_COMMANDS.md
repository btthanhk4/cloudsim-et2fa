# Các câu lệnh chạy Workflows DAX

## 🚀 Chạy nhanh

### 1. Workflow mẫu (4 tasks)
```cmd
run.bat
```

### 2. Tất cả workflows (15 workflows)
```cmd
run-all-workflows.bat
```

### 3. Batch test với CSV export
```powershell
.\run-batch-tests.ps1
```

## 📊 Chạy từng Workflow

### 1. CyberShake Workflows

```cmd
REM Small (50 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_100.dax --deadline=5000"

REM Large (500 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_500.dax --deadline=10000"
```

### 2. Epigenomics Workflows

**Lưu ý**: Epigenomics workflows cần deadline cao hơn.

```cmd
REM Small (47 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_50.dax --deadline=5000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_100.dax --deadline=6000"

REM Large (497 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_500.dax --deadline=12000"
```

### 3. Inspiral Workflows

```cmd
REM Small (50 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_50.dax --deadline=3000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_100.dax --deadline=5000"

REM Large (500 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_500.dax --deadline=10000"
```

### 4. Montage Workflows

```cmd
REM Small (50 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000"

REM Medium (100 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_100.dax --deadline=5000"

REM Large (500 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_500.dax --deadline=10000"
```

### 5. Sipht Workflows

```cmd
REM Small (48 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_50.dax --deadline=3000"

REM Medium (97 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_100.dax --deadline=5000"

REM Large (484 tasks)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_500.dax --deadline=10000"
```

## 📋 Deadline khuyến nghị

| Workflow | Size | Deadline | Notes |
|----------|------|----------|-------|
| CyberShake | 50 | 3000s | Small |
| CyberShake | 100 | 5000s | Medium |
| CyberShake | 500 | 10000s | Large |
| Epigenomics | 50 | 5000s | Small (cần deadline cao hơn) |
| Epigenomics | 100 | 6000s | Medium (cần deadline cao hơn) |
| Epigenomics | 500 | 12000s | Large (cần deadline cao hơn) |
| Inspiral | 50 | 3000s | Small |
| Inspiral | 100 | 5000s | Medium |
| Inspiral | 500 | 10000s | Large |
| Montage | 50 | 3000s | Small |
| Montage | 100 | 5000s | Medium |
| Montage | 500 | 10000s | Large |
| Sipht | 50 | 3000s | Small |
| Sipht | 100 | 5000s | Medium |
| Sipht | 500 | 10000s | Large |

## 💡 Tips

1. **Bắt đầu nhỏ**: Test với workflows nhỏ (~50 tasks) trước
2. **Tăng dần**: Sau đó test với workflows lớn hơn
3. **Deadline hợp lý**: Tăng deadline nếu workflow lớn
4. **Batch test**: Dùng `run-batch-tests.ps1` để test tất cả workflows cùng lúc

## 📊 Kết quả mong đợi

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
**Giải pháp**: Tăng deadline. Với workflows lớn (500 tasks), dùng `--deadline=10000` hoặc cao hơn.

### Lỗi: "Out of memory"
**Giải pháp**: Workflow quá lớn. Tăng heap size: `-Xmx2g` trong Maven.

### Workflow chạy quá lâu
**Giải pháp**: 
- Workflows lớn (500 tasks) có thể mất vài phút
- Đã thay thế workflows 1000 tasks bằng 500 tasks để chạy nhanh hơn
- Có thể test với workflows nhỏ hơn trước
