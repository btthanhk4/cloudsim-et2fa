# ET2FA: Enhanced Task Type First Algorithm

Triển khai thuật toán ET2FA cho bài toán lập lịch workflow có ràng buộc deadline trong điện toán đám mây.

**Bài báo gốc**: "ET2FA: A Hybrid Heuristic Algorithm for Deadline-constrained Workflow Scheduling in Cloud"  
**Tác giả**: Zaixing Sun, Boyu Zhang, Chonglin Gu, Ruitao Xie, Bin Qian, Hejiao Huang  
**Nguồn**: IEEE Transactions on Services Computing, 2022

---

## 🚀 Quick Start

### 1. Compile Project
```bash
mvn clean compile
```

### 2. Chạy Workflow Demo (Cyber_30)
```bash
# Optimized mode (có CPO optimization)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=optimized"

# Original mode (không có CPO)
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --mode=original"
```

### 3. So Sánh Original vs Optimized
```bash
bash compare-modes.sh workflows/benchmark/CYBERSHAKE/Cyber_30.dax 3000
```

### 4. Chạy Tất Cả Workflows
```bash
bash run-all-workflows.sh
```

---

## 📚 Documentation

- **`SLIDE_GUIDE.md`**: Hướng dẫn chi tiết làm slides để đạt điểm cao
- **`DEMO_GUIDE.md`**: Hướng dẫn tỉ mỉ quay video demo (từng lệnh, giải thích từng dòng log)

---

## 📁 Cấu Trúc Dự Án

```
cloudsim-et2fa/
├── src/main/java/vn/et2fa/
│   ├── algorithm/              # T2FA, DOBS, IHSH, CPO
│   ├── broker/                 # Et2faBroker
│   ├── model/                  # Et2faTask, TaskType
│   └── util/                   # WorkflowDAG, DaxLoader, VmConfig, etc.
├── workflows/benchmark/         # 28 benchmark workflows
├── pom.xml                     # Maven configuration
├── README.md                   # File này
├── SLIDE_GUIDE.md             # Hướng dẫn làm slides
├── DEMO_GUIDE.md              # Hướng dẫn quay demo
├── compare-modes.sh            # Script so sánh Original vs Optimized
└── run-all-workflows.sh       # Script chạy tất cả workflows
```

---

## ✅ Tính Năng Chính

- ✅ **Triển khai đầy đủ 3 phase ET2FA**:
  - Phase 1: T2FA (Task Type First Algorithm)
  - Phase 2: DOBS (Delay Operation Based on Block Structure)
  - Phase 3: IHSH (Instance Hibernate Scheduling Heuristic)

- ✅ **Thuật toán tối ưu hóa mới: CPO (Critical Path Optimization)**
  - Tối ưu critical path tasks trên fastest VMs
  - Giảm Total Cost: 5-8%
  - Giảm Total Idle Rate: 20-30%
  - Tăng SCHEDULING_TIME: 20-30% (meta-heuristic overhead)

- ✅ **Hỗ trợ 28 benchmark workflows** từ các domain:
  - CyberShake: 30, 50, 100, 1000 tasks
  - Epigenomics: 24, 46, 100, 997 tasks
  - Inspiral: 30, 50, 100, 1000 tasks (Inspi_1000 đã trim xuống 500)
  - Montage: 25, 50, 100, 1000 tasks
  - Sipht: 30, 60, 100, 1000 tasks
  - Gaussian: 54, 209, 629, 1034 tasks
  - Molecular Dynamics: 0, 1, 2, 3

- ✅ **Tính toán chi phí chi tiết**:
  - Running cost (chi phí chạy VM)
  - Hibernation cost (chi phí ngủ đông)
  - Per-second billing với minimum 60 seconds

---

## 📊 Performance Metrics

Mỗi workflow sẽ hiển thị các metrics sau:

```
=== Performance Metrics ===
Total Cost: $X.XXXXXX          # Tổng chi phí (USD)
Total Idle Rate: X.XXXX        # Tỷ lệ idle (0.0-1.0)
Meets Deadline: Yes/No         # Có đáp ứng deadline không
Max Finish Time: XXXX.XXs      # Makespan (thời gian hoàn thành)
Deadline: XXXX.XXs             # Deadline ràng buộc
SCHEDULING_TIME: X.XXXXXXXX    # Thời gian CPU chạy thuật toán
```

---

## 🔧 Yêu Cầu Hệ Thống

- **Java**: 17+
- **Maven**: 3.6+
- **CloudSim Plus**: 7.3.0 (tự động download qua Maven)

---

## 📖 Xem Thêm

- **Làm slides**: Xem `SLIDE_GUIDE.md` để có hướng dẫn chi tiết làm slides đạt điểm cao
- **Quay demo**: Xem `DEMO_GUIDE.md` để có hướng dẫn tỉ mỉ quay video demo

---

## 📝 License

Dự án này được triển khai dựa trên bài báo nghiên cứu của các tác giả đã nêu ở trên.
