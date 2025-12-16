# 📋 Tất Cả Lệnh Chạy Dự Án ET2FA

## 🔨 1. COMPILE PROJECT

```bash
mvn clean compile
```

---

## 🚀 2. CHẠY WORKFLOW ĐƠN LẺ

### Cyber_30 (Demo - Recommended)
```bash
# Optimized mode (có tối ưu)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized"

# Original mode (không tối ưu)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=original"
```

### Cyber_50
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_50.dax --deadline=5000 --mode=optimized"
```

### Cyber_100
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_100.dax --deadline=10000 --mode=optimized"
```

### Inspiral_30
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/INSPIRAL/Inspi_30.dax --deadline=3000 --mode=optimized"
```

### Inspiral_50
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/INSPIRAL/Inspi_50.dax --deadline=5000 --mode=optimized"
```

### Sipht_30
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_30.dax --deadline=3000 --mode=optimized"
```

### Sipht_60
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_60.dax --deadline=6000 --mode=optimized"
```

### Epigenomics_24
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/EPIGENOMICS/Epige_24.dax --deadline=3000 --mode=optimized"
```

### Epigenomics_46
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/EPIGENOMICS/Epige_46.dax --deadline=5000 --mode=optimized"
```

### Montage_25
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/MONTAGE/Monta_25.dax --deadline=3000 --mode=optimized"
```

### Montage_50
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/MONTAGE/Monta_50.dax --deadline=5000 --mode=optimized"
```

### Gaussian_54
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/GAUSSIAN/Gauss_54.dax --deadline=5000 --mode=optimized"
```

### Gaussian_209
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/GAUSSIAN/Gauss_209.dax --deadline=10000 --mode=optimized"
```

### Gaussian_629
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/GAUSSIAN/Gauss_629.dax --deadline=20000 --mode=optimized"
```

### Gaussian_1034
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/GAUSSIAN/Gauss_1034.dax --deadline=30000 --mode=optimized"
```

### Cyber_1000
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_1000.dax --deadline=50000 --mode=optimized"
```

### Inspiral_1000
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/INSPIRAL/Inspi_1000.dax --deadline=50000 --mode=optimized"
```

### Epigenomics_997
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/EPIGENOMICS/Epige_997.dax --deadline=50000 --mode=optimized"
```

---

## 🔄 3. SO SÁNH ORIGINAL VS OPTIMIZED

### Sử dụng script compare-modes.sh
```bash
# So sánh Cyber_30
bash compare-modes.sh workflows/benchmark/CYBERSHAKE/Cyber_30.dax 3000

# So sánh Cyber_50
bash compare-modes.sh workflows/benchmark/CYBERSHAKE/Cyber_50.dax 5000

# So sánh Inspiral_30
bash compare-modes.sh workflows/benchmark/INSPIRAL/Inspi_30.dax 3000

# So sánh Montage_25
bash compare-modes.sh workflows/benchmark/MONTAGE/Monta_25.dax 3000
```

### Hoặc chạy thủ công
```bash
# Chạy Original mode
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=original" \
  2>&1 | grep "SCHEDULING_TIME"

# Chạy Optimized mode
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "SCHEDULING_TIME"
```

---

## 📊 4. XEM LOG CHI TIẾT

### Xem toàn bộ log CPO
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "CPO:"
```

### Xem log CPO với context (50 dòng đầu)
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep -A 50 "CPO:" | head -100
```

### Xem log ET2FA (tất cả phases)
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "ET2FA:"
```

### Xem log T2FA
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "T2FA:"
```

### Xem log DOBS
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "DOBS:"
```

---

## 🎯 5. LỆNH DEMO NHANH (Recommended)

### Demo Cyber_30 với log đầy đủ
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized"
```

### Demo chỉ xem kết quả SCHEDULING_TIME và metrics
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep -E "SCHEDULING_TIME|Total Cost|Total Idle Rate|Meets Deadline|Max Finish Time"
```

### Demo xem số tasks
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep -E "Created|Loaded DAX|Starting scheduling|Tasks before|Scheduled|CPO.*Schedule size"
```

---

## 📝 6. LƯU KẾT QUẢ VÀO FILE

### Lưu toàn bộ output
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  > results_cyber_30_optimized.txt 2>&1
```

### Lưu chỉ SCHEDULING_TIME
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "SCHEDULING_TIME" > scheduling_time.txt
```

### Lưu log CPO
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "CPO:" > cpo_log.txt
```

---

## 🔍 7. XEM CÁC WORKFLOW CÓ SẴN

### List tất cả workflows
```bash
find workflows/benchmark -name "*.dax" | sort
```

### List CyberShake workflows
```bash
ls workflows/benchmark/CYBERSHAKE/*.dax
```

### List Inspiral workflows
```bash
ls workflows/benchmark/INSPIRAL/*.dax
```

### List Epigenomics workflows
```bash
ls workflows/benchmark/EPIGENOMICS/*.dax
```

### List Montage workflows
```bash
ls workflows/benchmark/MONTAGE/*.dax
```

### List Sipht workflows
```bash
ls workflows/benchmark/SIPHT/*.dax
```

### List Gaussian workflows
```bash
ls workflows/benchmark/GAUSSIAN/*.dax
```

---

## ⚙️ 8. CÁC THAM SỐ

- `--dax=<path>`: Đường dẫn đến file DAX workflow
- `--deadline=<number>`: Deadline (giây), ví dụ: 3000
- `--mode=<original|optimized>`: Chế độ chạy
  - `original`: Không có optimizations
  - `optimized`: Có đầy đủ optimizations (default)
- `--use-expected`: Sử dụng expected times từ Table 7 (nếu có)

---

## 🎬 9. LỆNH DEMO CHO PRESENTATION

### Demo so sánh Original vs Optimized
```bash
# 1. Original mode
echo "=== ORIGINAL MODE ==="
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=original" \
  2>&1 | grep -E "SCHEDULING_TIME|Total Cost|Total Idle Rate"

# 2. Optimized mode
echo "=== OPTIMIZED MODE ==="
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep -E "SCHEDULING_TIME|Total Cost|Total Idle Rate"

# 3. Xem log CPO chi tiết
echo "=== CPO LOG ==="
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "CPO:" | head -30
```

### Demo với nhiều workflows
```bash
# Test nhiều workflows cùng lúc
for workflow in Cyber_30 Inspi_30 Monta_25 Sipht_30; do
  echo "=== Testing $workflow ==="
  mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
    -Dexec.args="--dax=workflows/benchmark/*/$workflow.dax --deadline=3000 --mode=optimized" \
    2>&1 | grep "SCHEDULING_TIME"
done
```

---

## 💡 10. TIPS & TRICKS

### Compile và chạy trong một lệnh
```bash
mvn clean compile exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized"
```

### Xem output đẹp hơn với colors
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep --color=always -E "SCHEDULING_TIME|CPO:|ET2FA:"
```

### Chạy và lưu log vào file đồng thời xem trên terminal
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | tee output.log | grep "SCHEDULING_TIME"
```

---

## 📋 11. CHECKLIST TRƯỚC KHI DEMO

- [ ] Đã compile project: `mvn clean compile`
- [ ] Đã test Cyber_30: Chạy và kiểm tra output
- [ ] Đã test Original mode: So sánh với Optimized
- [ ] Đã xem log CPO: Đảm bảo log hợp lý và clean
- [ ] Đã kiểm tra số tasks: Đúng với tên file (30 tasks cho Cyber_30)
- [ ] Đã chuẩn bị script demo: `compare-modes.sh` hoặc lệnh thủ công

---

## 🎯 LỆNH QUAN TRỌNG NHẤT (Copy & Paste)

### Lệnh Demo Chính (Cyber_30)
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized"
```

### So Sánh Original vs Optimized
```bash
bash compare-modes.sh workflows/benchmark/CYBERSHAKE/Cyber_30.dax 3000
```

### Xem Log CPO
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized" \
  2>&1 | grep "CPO:"
```

---

**Chúc bạn demo thành công! 🎉**

