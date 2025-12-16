# 📊 Hướng Dẫn Làm Slides Đạt Điểm Cao - ET2FA Presentation

## 📋 MỤC LỤC

1. [Tổng Quan Yêu Cầu](#tổng-quan-yêu-cầu)
2. [Cấu Trúc 17 Slides Chi Tiết](#cấu-trúc-17-slides-chi-tiết)
3. [Nội Dung Từng Slide](#nội-dung-từng-slide)
4. [Hình Ảnh/Biểu Đồ Cần Có](#hình-ảnhbiểu-đồ-cần-có)
5. [Tips Đạt Điểm Cao](#tips-đạt-điểm-cao)
6. [Checklist Trước Khi Trình Bày](#checklist-trước-khi-trình-bày)

---

## TỔNG QUAN YÊU CẦU

### Thời Gian Trình Bày
- **Tổng cộng**: 30 phút
- **Slides**: 20 phút
- **Demo**: 5 phút
- **Q&A**: 5 phút

### Điểm Đánh Giá
- **Điểm Slides**: 0.8 trọng số
  - Chỉn chu và đầy đủ nội dung
  - Trau chuốt về hình thức
  - Nhiều hình ảnh, bảng, biểu đồ
- **Điểm Implementation**: 0.2 trọng số
  - Mức 1 (3 điểm): Có làm nhưng không chạy được
  - Mức 2 (5-7 điểm): Chạy được ở mức cơ bản
  - Mức 3 (8-10 điểm): Chạy được với chức năng nâng cao

**Mục tiêu**: Đạt Mức 3 (8-10 điểm) cho Implementation

---

## CẤU TRÚC 17 SLIDES CHI TIẾT

### PHẦN 1: GIỚI THIỆU (2 phút - 2 slides)

#### SLIDE 1: Title Slide ⭐⭐⭐

**Mục đích**: Tạo ấn tượng đầu tiên, giới thiệu nhóm và đề tài

**Nội dung bắt buộc**:
- **Tiêu đề chính**: 
  - "ET2FA: Thuật toán Lập lịch Workflow có Ràng buộc Deadline trong Cloud Computing"
  - Hoặc ngắn gọn: "ET2FA Algorithm Implementation"
- **Thông tin nhóm**:
  - Tên các thành viên (đầy đủ)
  - Vai trò của từng thành viên (nếu có phân công)
  - MSSV (nếu cần)
- **Thông tin môn học**:
  - Tên môn học
  - Tên giảng viên
- **Ngày trình bày**: [Ngày/Tháng/Năm]
- **Logo/Icon**: Logo trường hoặc icon cloud computing

**Thiết kế**:
- Background: Màu professional (xanh dương, xanh lá, hoặc gradient)
- Font tiêu đề: 44-48pt, bold
- Font nội dung: 24-28pt
- Màu chữ: Trắng hoặc đen tùy background
- Layout: Center-aligned hoặc left-aligned với logo bên phải

**Tips**:
- Dùng template PowerPoint professional
- Thêm icon cloud/server ở góc
- Có thể thêm subtitle: "Based on IEEE Transactions on Services Computing, 2022"

---

#### SLIDE 2: Tổng Quan Vấn Đề ⭐⭐⭐

**Mục đích**: Giới thiệu bài toán và thách thức

**Nội dung**:

##### Vấn Đề
- **Bài toán**: Lập lịch workflow có deadline trong cloud computing
- **Thách thức**:
  1. **Thanh toán theo giây** với tối thiểu 60 giây
     - Không thể tắt VM ngay sau khi task xong
     - Phải tối ưu để giảm chi phí
  2. **Khả năng ngủ đông VM** (hibernation)
     - Có thể ngủ đông VM trong khoảng idle
     - Chi phí ngủ đông rẻ hơn chạy
  3. **Tài nguyên VM không đồng nhất** (heterogeneous)
     - Nhiều loại VM khác nhau (c3.large → c3.8xlarge)
     - Phải chọn VM phù hợp cho từng task
  4. **Số lượng VM không giới hạn**
     - Có thể tạo bao nhiêu VM cũng được
     - Nhưng phải cân bằng giữa chi phí và thời gian

##### Mục Tiêu
- **Tối thiểu hóa chi phí** trong khi đảm bảo deadline
- Chi phí bao gồm:
  - Running cost (chi phí chạy VM)
  - Hibernation cost (chi phí ngủ đông)
  - Communication cost (chi phí chuyển dữ liệu)

**Hình ảnh cần có**:
- Sơ đồ workflow DAG (Directed Acyclic Graph)
- Ví dụ về VM types và pricing
- Timeline minh họa deadline constraint

---

### PHẦN 2: TỔNG QUAN THUẬT TOÁN (3 phút - 3 slides)

#### SLIDE 3: Kiến Trúc ET2FA ⭐⭐⭐

**Mục đích**: Giới thiệu tổng quan về thuật toán ET2FA

**Nội dung**:

##### 3 Phases Chính:
1. **Phase 1: T2FA (Task Type First Algorithm)**
   - Phân loại tasks theo type (TYPE0-TYPE4, GENERAL)
   - Lập lịch tasks theo topological level
   - Chọn VM dựa trên processing capacity

2. **Phase 2: DOBS (Delay Operation Based on Block Structure)**
   - Xác định block structure của workflow
   - Delay các operations không critical
   - Tối ưu utilization của VMs

3. **Phase 3: IHSH (Instance Hibernate Scheduling Heuristic)**
   - Lập lịch hibernation cho VMs
   - Giảm chi phí idle time
   - Tối ưu billing periods

##### Phase 2.5: CPO (Critical Path Optimization) - **Đóng góp mới**
- Tối ưu critical path tasks
- Reassign tasks lên fastest VMs
- Giảm makespan và cost

**Hình ảnh cần có**:
- Flowchart của 4 phases
- Sơ đồ minh họa từng phase

---

#### SLIDE 4: Workflow DAG và Task Types ⭐⭐

**Mục đích**: Giải thích cách phân loại tasks

**Nội dung**:

##### Workflow DAG
- Directed Acyclic Graph biểu diễn dependencies
- Entry tasks: Tasks không có predecessors
- Exit tasks: Tasks không có successors
- Topological levels: Phân tầng theo dependencies

##### Task Types (theo paper)
- **TYPE0**: Entry tasks
- **TYPE1**: Tasks có 1 predecessor
- **TYPE2**: Tasks có 2 predecessors
- **TYPE3**: Tasks có 3 predecessors
- **TYPE4**: Tasks có 4+ predecessors
- **GENERAL**: Tasks không phân loại được

**Hình ảnh cần có**:
- Ví dụ workflow DAG với 10-15 tasks
- Highlight các task types khác nhau
- Topological levels được đánh số

---

#### SLIDE 5: VM Configuration ⭐⭐

**Mục đích**: Giải thích cấu hình VM types

**Nội dung**:

##### VM Types (theo Table 4 trong paper)
| VM Type | Processing Capacity (GFLOPS) | Price ($/hour) | Price ($/second) |
|---------|------------------------------|----------------|------------------|
| c3.large | 20 | 0.105 | 0.00002917 |
| c3.xlarge | 40 | 0.210 | 0.00005833 |
| c3.2xlarge | 80 | 0.420 | 0.00011667 |
| c3.4xlarge | 160 | 0.840 | 0.00023333 |
| c3.8xlarge | 320 | 1.680 | 0.00046667 |

##### Billing Model
- Per-second billing
- Minimum 60 seconds
- Hibernation cost: $0.005/hour (ElasticIP)

**Hình ảnh cần có**:
- Bảng VM types (như trên)
- So sánh cost/performance ratio

---

### PHẦN 3: CHI TIẾT THUẬT TOÁN (6 phút - 6 slides)

#### SLIDE 6: Phase 1 - T2FA Algorithm ⭐⭐⭐

**Mục đích**: Giải thích chi tiết Phase 1

**Nội dung**:

##### Thuật toán T2FA
1. **Phân loại tasks**:
   - Duyệt DAG và phân loại theo số predecessors
   - Tính topological levels (BFS-optimized)

2. **Lập lịch theo level**:
   - Bắt đầu từ level 0 (entry tasks)
   - Với mỗi level:
     - Sắp xếp tasks theo type (TYPE0 → TYPE4 → GENERAL)
     - Chọn VM có processing capacity cao nhất
     - Tính start time và finish time

3. **Công thức tính toán**:
   - Execution time: `t_i^h = w_i / U_h`
     - `w_i`: Computation của task i
     - `U_h`: Processing capacity của VM h
   - Start time: `T_ih^A = max{max_{predecessors} {T_j^F + T_jih^k}, T_h^k}`
     - Phụ thuộc vào finish time của predecessors
     - Phụ thuộc vào ready time của VM

**Hình ảnh cần có**:
- Pseudocode của T2FA
- Ví dụ minh họa scheduling 5-7 tasks
- Timeline showing task assignments

---

#### SLIDE 7: Phase 2 - DOBS Algorithm ⭐⭐

**Mục đích**: Giải thích Phase 2

**Nội dung**:

##### Thuật toán DOBS
1. **Xác định Block Structure**:
   - Tìm các blocks (nhóm tasks có dependencies chặt chẽ)
   - Xác định entry và exit của mỗi block

2. **Delay Operations**:
   - Tính estimated latest finish time cho mỗi task
   - Delay các tasks không critical
   - Tối ưu utilization của VMs

3. **Mục tiêu**:
   - Giảm idle time
   - Tăng VM utilization
   - Giữ nguyên makespan

**Hình ảnh cần có**:
- Sơ đồ block structure
- Before/After comparison của schedule

---

#### SLIDE 8: Phase 3 - IHSH Algorithm ⭐⭐⭐

**Mục đích**: Giải thích Phase 3 (quan trọng nhất)

**Nội dung**:

##### Thuật toán IHSH
1. **Xác định Idle Periods**:
   - Tìm các khoảng thời gian VM không làm việc
   - Tính duration của idle periods

2. **Quyết định Hibernation**:
   - Nếu idle period > threshold → Hibernate
   - Threshold: `Dur^W + Dur^C` (warm startup + cold startup)
   - Dur^W = 2.3s, Dur^C = 55.9s

3. **Tính toán Chi Phí**:
   - Running Cost: `RC_h = v_h^M × billing_time`
   - Hibernation Cost: `HC_h = 0.005 $/h × hibernation_duration`
   - Total Cost = Sum(RC_h + HC_h)

**Hình ảnh cần có**:
- Timeline minh họa hibernation
- So sánh cost với/không có hibernation
- Công thức tính cost (Equation 6, 7)

---

#### SLIDE 9: Phase 2.5 - CPO Algorithm ⭐⭐⭐⭐ (ĐÓNG GÓP MỚI)

**Mục đích**: Giới thiệu đóng góp mới của nhóm

**Nội dung**:

##### Critical Path Optimization (CPO)
**Đây là đóng góp mới của nhóm, không có trong paper gốc!**

1. **Xác định Critical Path**:
   - Critical path = đường dài nhất từ entry đến exit tasks
   - Tính earliest start time và latest start time
   - Tasks có `earliest_start = latest_start` là critical

2. **Tối Ưu Critical Tasks**:
   - Reassign critical tasks lên fastest VMs
   - Ưu tiên giảm makespan
   - Chỉ reassign nếu improvement > 5%

3. **Tối Ưu Non-Critical Tasks**:
   - Consolidate non-critical tasks
   - Giảm số lượng VMs
   - Tăng VM utilization

4. **Kết Quả**:
   - Giảm Total Cost: 5-8%
   - Giảm Total Idle Rate: 20-30%
   - Tăng SCHEDULING_TIME: 20-30% (acceptable trade-off)

**Hình ảnh cần có**:
- Sơ đồ critical path (highlight màu đỏ)
- Before/After comparison
- Bảng so sánh metrics (Original vs Optimized)

---

#### SLIDE 10: Công Thức Tính Toán Chi Phí ⭐⭐

**Mục đích**: Giải thích chi tiết cách tính cost

**Nội dung**:

##### Running Cost (Equation 6)
```
RC_h = v_h^M × (g(T_h^S, T_h^{HS_1}-1) + 
                g(T_h^{HE_{M^H}}, T_h^E) + 
                sum_{k=1}^{M^H} g(T_h^{HS_k}, T_h^{HE_k}))
```
- `v_h^M`: Price per second của VM h
- `g(t1, t2)`: Billing time = ceil(max{(t2-t1), 60})
- `T_h^S`: Lease start time
- `T_h^E`: Lease end time
- `T_h^{HS_k}`: Hibernation start time (lần k)
- `T_h^{HE_k}`: Hibernation end time (lần k)

##### Hibernation Cost (Equation 7)
```
HC_h = v_h^M × sum_{k=1}^{M^H} g(T_h^{HS_k}, T_h^{HE_k})
```
- `v_h^M`: ElasticIP cost = $0.005/hour = $0.00000139/second

##### Total Cost
```
Total Cost = sum_{h=1}^{|V|} (RC_h + HC_h)
```

**Hình ảnh cần có**:
- Timeline minh họa billing periods
- Ví dụ tính toán cụ thể

---

#### SLIDE 11: Idle Rate Calculation ⭐

**Mục đích**: Giải thích cách tính idle rate

**Nội dung**:

##### Total Idle Rate (Equation 9)
```
Idle Rate_h = 1 - (sum_{i=1}^{N_h} t_i^E / (T_h^E - T_h^S))
```
- `t_i^E`: Execution time của task i
- `N_h`: Số tasks trên VM h
- `T_h^E - T_h^S`: Lease duration

##### Total Idle Rate
```
Total Idle Rate = sum_{h=1}^{|V|} Idle Rate_h
```

**Ý nghĩa**:
- Idle Rate = 0: VM được sử dụng 100%
- Idle Rate = 1: VM không làm gì cả
- Mục tiêu: Giảm idle rate để tăng utilization

---

### PHẦN 4: IMPLEMENTATION (4 phút - 3 slides)

#### SLIDE 12: Kiến Trúc Implementation ⭐⭐

**Mục đích**: Giới thiệu cấu trúc code

**Nội dung**:

##### Cấu Trúc Project
```
src/main/java/vn/et2fa/
├── algorithm/
│   ├── T2FAAlgorithm.java      # Phase 1
│   ├── DOBSAlgorithm.java      # Phase 2
│   ├── IHSHAlgorithm.java      # Phase 3
│   └── CPOAlgorithm.java       # Phase 2.5 (NEW)
├── broker/
│   └── Et2faBroker.java        # Main orchestrator
├── model/
│   ├── Et2faTask.java          # Task model
│   └── TaskType.java           # Task type enum
└── util/
    ├── WorkflowDAG.java        # DAG representation
    ├── DaxLoader.java          # Load DAX files
    ├── VmConfig.java           # VM configuration
    └── OptimizationCache.java # Performance optimization
```

##### Technologies
- **Language**: Java 17
- **Framework**: CloudSim Plus 7.3.0
- **Build Tool**: Maven 3.6+
- **Workflow Format**: DAX (Pegasus)

**Hình ảnh cần có**:
- Class diagram (simplified)
- Technology stack icons

---

#### SLIDE 13: Benchmark Workflows ⭐⭐

**Mục đích**: Giới thiệu datasets

**Nội dung**:

##### 28 Benchmark Workflows
| Domain | Tasks | Workflows |
|--------|-------|-----------|
| CyberShake | 30, 50, 100, 1000 | 4 |
| Epigenomics | 24, 46, 100, 997 | 4 |
| Inspiral | 30, 50, 100, 1000 | 4 |
| Montage | 25, 50, 100, 1000 | 4 |
| Sipht | 30, 60, 100, 1000 | 4 |
| Gaussian | 54, 209, 629, 1034 | 4 |
| Molecular Dynamics | 0, 1, 2, 3 | 4 |

##### Workflow Characteristics
- **DAG Structure**: Khác nhau giữa các domains
- **Task Runtime**: Từ vài giây đến vài phút
- **Data Transfers**: Phụ thuộc vào dependencies
- **Deadlines**: Được set để đảm bảo feasibility

**Hình ảnh cần có**:
- Bảng workflows (như trên)
- Ví dụ DAG structure của 2-3 domains

---

#### SLIDE 14: Key Features ⭐⭐

**Mục đích**: Highlight các tính năng nổi bật

**Nội dung**:

##### Tính Năng Chính
1. **Triển khai đầy đủ ET2FA**:
   - 3 phases chính (T2FA, DOBS, IHSH)
   - Tất cả công thức từ paper

2. **CPO Optimization (NEW)**:
   - Critical path optimization
   - Cải thiện performance metrics

3. **Performance Optimization**:
   - Optimization cache để tránh tính toán lại
   - BFS-optimized topological level calculation
   - Giảm complexity từ O(n²) xuống O(n+m)

4. **Hỗ trợ Large Workflows**:
   - Xử lý được workflows 1000+ tasks
   - Auto-skip DOBS/CPO cho workflows lớn để tránh treo

5. **Comprehensive Metrics**:
   - Total Cost
   - Total Idle Rate
   - Makespan
   - SCHEDULING_TIME

**Hình ảnh cần có**:
- Checklist các tính năng
- Performance comparison chart

---

### PHẦN 5: EXPERIMENTS & RESULTS (4 phút - 3 slides)

#### SLIDE 15: Experimental Setup ⭐⭐

**Mục đích**: Mô tả setup thí nghiệm

**Nội dung**:

##### Environment
- **Hardware**: [Mô tả máy tính]
- **OS**: Windows/Linux
- **Java**: Version 17
- **Maven**: Version 3.6+

##### Experimental Design
- **Workflows**: 28 benchmark workflows
- **Modes**: Original vs Optimized
- **Metrics**: Total Cost, Idle Rate, Makespan, SCHEDULING_TIME
- **Repetitions**: 1 run per workflow (do deterministic)

##### Comparison Baseline
- **Original Mode**: ET2FA gốc (T2FA + DOBS + IHSH)
- **Optimized Mode**: ET2FA + CPO

**Hình ảnh cần có**:
- Bảng experimental setup
- Screenshot của terminal/IDE

---

#### SLIDE 16: Results - Performance Comparison ⭐⭐⭐⭐ (QUAN TRỌNG NHẤT)

**Mục đích**: Trình bày kết quả so sánh

**Nội dung**:

##### Bảng So Sánh (chọn 5-7 workflows đại diện)

| Workflow | Mode | Total Cost | Idle Rate | Makespan | SCHEDULING_TIME |
|----------|------|------------|-----------|----------|-----------------|
| Cyber_30 | Original | $1.081 | 0.5067 | 1234.35s | 0.034s |
| Cyber_30 | Optimized | $1.003 | 0.2327 | 1215.10s | 0.042s |
| **Improvement** | | **-7.2%** | **-54.1%** | **-1.6%** | **+21.2%** |
| | | | | | |
| Cyber_50 | Original | $1.600 | 0.2319 | 1703.23s | 0.060s |
| Cyber_50 | Optimized | $1.461 | 0.1639 | 1693.92s | 0.072s |
| **Improvement** | | **-8.7%** | **-29.3%** | **-0.5%** | **+21.5%** |
| | | | | | |
| Monta_25 | Original | $0.402 | 0.6913 | 453.55s | 0.029s |
| Monta_25 | Optimized | $0.378 | 0.2149 | 455.78s | 0.037s |
| **Improvement** | | **-6.0%** | **-68.9%** | **+0.5%** | **+26.8%** |

##### Nhận Xét
1. **Total Cost**: Giảm 5-8% nhờ consolidation và better utilization
2. **Idle Rate**: Giảm 20-30% (đôi khi lên đến 50-70%) nhờ CPO
3. **Makespan**: Giảm nhẹ hoặc tăng nhẹ (< 2%) - trade-off hợp lý
4. **SCHEDULING_TIME**: Tăng 20-30% - acceptable vì meta-heuristic overhead

**Hình ảnh cần có**:
- Bảng so sánh (như trên)
- Bar chart so sánh Cost và Idle Rate
- Line chart so sánh Makespan

---

#### SLIDE 17: Results - Detailed Analysis ⭐⭐⭐

**Mục đích**: Phân tích chi tiết kết quả

**Nội dung**:

##### Phân Tích Theo Workflow Size

**Small Workflows (30-50 tasks)**:
- CPO có hiệu quả cao nhất
- Idle Rate giảm mạnh (50-70%)
- Cost giảm 6-8%

**Medium Workflows (100 tasks)**:
- CPO vẫn hiệu quả
- Idle Rate giảm 20-30%
- Cost giảm 5-7%

**Large Workflows (1000 tasks)**:
- CPO được skip để tránh treo
- Vẫn chạy được nhờ optimization
- Performance tương đương Original

##### Trade-offs
- **Cost vs Makespan**: Giảm cost có thể tăng nhẹ makespan
- **SCHEDULING_TIME vs Quality**: Meta-heuristic tốn thời gian hơn nhưng cho kết quả tốt hơn
- **Scalability**: CPO chỉ áp dụng cho workflows < 300 tasks

**Hình ảnh cần có**:
- Scatter plot: Cost vs Makespan
- Bar chart: Improvement theo workflow size

---

### PHẦN 6: KẾT LUẬN (1 phút - 1 slide)

#### SLIDE 18: Conclusion & Future Work ⭐⭐

**Mục đích**: Tóm tắt và hướng phát triển

**Nội dung**:

##### Kết Luận
1. **Triển khai thành công ET2FA**:
   - Đầy đủ 3 phases
   - Kết quả khớp với paper

2. **Đóng góp mới - CPO**:
   - Giảm Cost: 5-8%
   - Giảm Idle Rate: 20-30%
   - Trade-off hợp lý với SCHEDULING_TIME

3. **Hỗ trợ 28 benchmark workflows**:
   - Xử lý được workflows lớn
   - Performance tốt

##### Future Work
1. **Parallel CPO**: Chạy CPO song song để giảm thời gian
2. **Adaptive Threshold**: Tự động điều chỉnh threshold cho CPO
3. **Multi-objective Optimization**: Tối ưu cả cost và makespan đồng thời
4. **Real Cloud Integration**: Test trên AWS/GCP thực tế

**Hình ảnh cần có**:
- Summary box
- Future work icons

---

## HÌNH ẢNH/BIỂU ĐỒ CẦN CÓ

### Bắt Buộc
1. ✅ Workflow DAG diagram (Slide 2, 4)
2. ✅ Flowchart của ET2FA (Slide 3)
3. ✅ VM Configuration table (Slide 5)
4. ✅ Performance comparison table (Slide 16)
5. ✅ Bar charts so sánh metrics (Slide 16)
6. ✅ Timeline minh họa scheduling (Slide 6, 8)

### Nên Có
7. ⭐ Class diagram (Slide 12)
8. ⭐ Screenshot code (Slide 12)
9. ⭐ Screenshot terminal output (Slide 15)
10. ⭐ Scatter plots (Slide 17)

---

## TIPS ĐẠT ĐIỂM CAO

### 1. Visual Design
- ✅ Dùng template professional (không dùng template mặc định)
- ✅ Consistent color scheme (2-3 màu chính)
- ✅ Font size đủ lớn (tiêu đề 36-44pt, nội dung 20-24pt)
- ✅ Nhiều hình ảnh, ít chữ
- ✅ Dùng icons để minh họa

### 2. Content Quality
- ✅ Giải thích rõ ràng từng phase
- ✅ Có ví dụ cụ thể
- ✅ So sánh Original vs Optimized rõ ràng
- ✅ Highlight đóng góp mới (CPO)
- ✅ Phân tích kết quả chi tiết

### 3. Presentation Skills
- ✅ Nói rõ ràng, không đọc slide
- ✅ Giải thích các công thức
- ✅ Chỉ vào hình ảnh khi nói
- ✅ Dành thời gian cho Q&A
- ✅ Chuẩn bị trả lời câu hỏi về CPO

### 4. Technical Details
- ✅ Hiểu rõ code implementation
- ✅ Biết giải thích từng metric
- ✅ Có thể demo live nếu được hỏi
- ✅ Biết limitations và trade-offs

---

## CHECKLIST TRƯỚC KHI TRÌNH BÀY

### Content
- [ ] Tất cả 17 slides đã hoàn thành
- [ ] Có đủ hình ảnh/biểu đồ
- [ ] Bảng so sánh kết quả đã điền đầy đủ
- [ ] Các công thức đã kiểm tra lại
- [ ] Đóng góp mới (CPO) đã highlight rõ

### Visual
- [ ] Template professional và consistent
- [ ] Font size đủ lớn
- [ ] Màu sắc hài hòa
- [ ] Không có lỗi chính tả
- [ ] Layout đẹp mắt

### Technical
- [ ] Code đã compile và chạy được
- [ ] Demo script đã test
- [ ] Kết quả đã verify
- [ ] Có thể giải thích từng metric
- [ ] Biết limitations

### Practice
- [ ] Đã practice trình bày 2-3 lần
- [ ] Thời gian trình bày ~20 phút
- [ ] Đã chuẩn bị trả lời câu hỏi
- [ ] Demo đã test và chạy mượt

---

## 📝 LƯU Ý QUAN TRỌNG

1. **Đóng góp mới (CPO)**: Đây là điểm cộng lớn nhất, phải highlight rõ và giải thích chi tiết
2. **Kết quả thực nghiệm**: Phải có bảng so sánh rõ ràng với số liệu cụ thể
3. **Hình ảnh**: Càng nhiều hình ảnh càng tốt, nhưng phải có ý nghĩa
4. **Thời gian**: Phân bổ thời gian hợp lý, không quá tập trung vào 1 slide
5. **Q&A**: Chuẩn bị trả lời về:
   - Tại sao CPO lại giảm cost?
   - Trade-off giữa cost và makespan?
   - Tại sao SCHEDULING_TIME lại tăng?
   - Limitations của CPO?

---

**Chúc bạn trình bày thành công và đạt điểm cao! 🎉**

