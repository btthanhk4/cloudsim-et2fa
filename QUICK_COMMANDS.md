# Các câu lệnh chạy Workflows - Tóm tắt nhanh

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

### CyberShake
```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_1000.dax --deadline=20000"
```

### Epigenomics (Cần deadline cao hơn)
```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_50.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_100.dax --deadline=6000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_1000.dax --deadline=25000"
```

### Inspiral
```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_1000.dax --deadline=20000"
```

### Montage
```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_1000.dax --deadline=20000"
```

### Sipht
```cmd
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_50.dax --deadline=3000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_100.dax --deadline=5000"
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_1000.dax --deadline=20000"
```

## ✅ Đã test thành công

- ✅ CyberShake_50, CyberShake_100
- ✅ Montage_50, Montage_100
- ✅ Inspiral_50
- ✅ Sipht_50 (với deadline 3000s)
- ⚠️ Epigenomics_50 (cần deadline 5000s+)

## 📋 Deadline khuyến nghị

| Workflow | Size | Deadline |
|----------|------|----------|
| CyberShake | 50 | 3000s |
| CyberShake | 100 | 5000s |
| CyberShake | 1000 | 20000s |
| Epigenomics | 50 | 5000s |
| Epigenomics | 100 | 6000s |
| Epigenomics | 1000 | 25000s |
| Inspiral | 50 | 3000s |
| Inspiral | 100 | 5000s |
| Inspiral | 1000 | 20000s |
| Montage | 50 | 3000s |
| Montage | 100 | 5000s |
| Montage | 1000 | 20000s |
| Sipht | 50 | 3000s |
| Sipht | 100 | 5000s |
| Sipht | 1000 | 20000s |

## 💡 Lưu ý

- **Epigenomics workflows** cần deadline cao hơn so với các workflows khác
- **Workflows lớn (1000 tasks)** có thể mất vài phút để chạy
- **Batch test** nên chạy vào thời gian rảnh

Xem `RUN_COMMANDS.md` để biết chi tiết đầy đủ.

