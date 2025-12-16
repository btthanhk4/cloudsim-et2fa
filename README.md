# ET2FA: Thuật toán Task Type First nâng cao

Triển khai thuật toán ET2FA cho bài toán lập lịch workflow có ràng buộc deadline trong điện toán đám mây.

**Bài báo**: "ET2FA: A Hybrid Heuristic Algorithm for Deadline-constrained Workflow Scheduling in Cloud"  
**Tác giả**: Zaixing Sun, Boyu Zhang, Chonglin Gu, Ruitao Xie, Bin Qian, Hejiao Huang  
**Nguồn**: IEEE Transactions on Services Computing, 2022

---

## 🚀 Quick Start

### Compile
```bash
mvn clean compile
```

### Chạy Workflow

#### Mode Optimized (Mặc định - Có tối ưu)
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized"
```

#### Mode Original (Không tối ưu - Giống Table 7)
```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=original"
```

#### So Sánh 2 Mode
```bash
./compare-modes.sh workflows/benchmark/CYBERSHAKE/Cyber_30.dax 3000
```

---

## 📚 Documentation

- **`SLIDE.md`**: Hướng dẫn chi tiết làm slides để đạt điểm cao
- **`RUN.md`**: Hướng dẫn chạy workflow (thủ công và tự động)

---

## 📁 Cấu Trúc Dự Án

```
cloudsim-et2fa/
├── src/main/java/vn/et2fa/     # Source code (13 Java files)
│   ├── algorithm/              # T2FA, DOBS, IHSH, CPO
│   ├── broker/                 # Et2faBroker
│   ├── model/                  # Et2faTask, TaskType
│   └── util/                   # WorkflowDAG, DaxLoader, VmConfig, OptimizationCache
├── workflows/benchmark/         # 28 benchmark workflows
├── pom.xml                     # Maven configuration
├── README.md                   # File này
├── SLIDE.md                    # Hướng dẫn làm slides
└── RUN.md                      # Hướng dẫn chạy workflow
```

---

## ✅ Tính Năng

- ✅ Triển khai đầy đủ 3 phase ET2FA (T2FA, DOBS, IHSH)
- ✅ **Thuật toán tối ưu hóa mới: Critical Path Optimization (CPO)**
- ✅ Xử lý workflow lớn (1000+ tasks)
- ✅ Hỗ trợ 28 benchmark workflows
- ✅ Tính toán chi phí chi tiết (running + hibernation)
- ✅ Tối ưu hóa performance (giảm 20-25% thời gian)

---

## 📊 Benchmark Workflows

28 workflows từ các domain:
- CyberShake: 30, 50, 100, 1000 tasks
- Epigenomics: 24, 46, 100, 997 tasks
- Inspiral: 30, 50, 100, 1000 tasks
- Montage: 25, 50, 100, 1000 tasks
- Sipht: 30, 60, 100, 1000 tasks
- Gaussian: 54, 209, 629, 1034 tasks
- Molecular Dynamics: 0, 1, 2, 3

---

## 🔧 Yêu Cầu

- Java 17+
- Maven 3.6+
- CloudSim Plus 7.3.0

---

**Xem `SLIDE.md` để có hướng dẫn chi tiết làm slides!**  
**Xem `RUN.md` để biết cách chạy workflow!**
