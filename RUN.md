# Hướng Dẫn Chạy Workflow - ET2FA

## 📋 MỤC LỤC

1. [Cách 1: Chạy Thủ Công Từng Lệnh](#cách-1-chạy-thủ-công-từng-lệnh)
2. [Cách 2: Chạy Tự Động Tất Cả Workflows](#cách-2-chạy-tự-động-tất-cả-workflows)
3. [Giải Thích Output](#giải-thích-output)
4. [Troubleshooting](#troubleshooting)

---

# CÁCH 1: CHẠY THỦ CÔNG TỪNG LỆNH

## Bước 1: Compile Project

```bash
mvn clean compile
```

**Giải thích**: 
- `mvn clean`: Xóa các file đã compile trước đó
- `compile`: Compile source code thành bytecode

**Kết quả mong đợi**: 
```
[INFO] BUILD SUCCESS
```

---

## Bước 2: Chạy Workflow Cụ Thể

### Workflow Nhỏ (30 tasks) - Cho Demo

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --use-expected"
```

**Giải thích**:
- `mvn exec:java`: Chạy Java class qua Maven
- `-Dexec.mainClass="vn.et2fa.App"`: Class chính
- `-Dexec.args="..."`: Tham số command-line
  - `--dax=...`: Đường dẫn đến file DAX workflow
  - `--deadline=3000`: Deadline = 3000 giây (50 phút)
  - `--use-expected`: Sử dụng expected time từ Table 7

**Kết quả mong đợi**:
```
ET2FA: Phase 1 - T2FA...
ET2FA: Phase 2 - DOBS...
ET2FA: Phase 2.5 - CPO...
ET2FA: Phase 3 - IHSH...
SCHEDULING_TIME: 0.034567 seconds
Performance Metrics:
  Total Cost: $0.000123
  Total Idle Rate: 0.1234
  Meets Deadline: Yes
```

---

### Workflow Trung Bình (100 tasks)

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_100.dax --deadline=5000 --use-expected"
```

**Deadline**: 5000 giây (83 phút)

---

### Workflow Lớn (1000 tasks) - Cho Demo

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_1000.dax --deadline=15000 --use-expected"
```

**Deadline**: 15000 giây (250 phút)

**Lưu ý**: 
- DOBS và CPO sẽ tự động skip để tránh treo
- Vẫn chạy được trong 2-5 giây

---

## Các Workflows Khác

### Epigenomics

```bash
# 24 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/EPIGE/Epige_24.dax --deadline=2000 --use-expected"

# 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/EPIGE/Epige_100.dax --deadline=5000 --use-expected"
```

### Montage

```bash
# 25 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/MONTAGE/Monta_25.dax --deadline=2000 --use-expected"

# 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/MONTAGE/Monta_100.dax --deadline=5000 --use-expected"
```

### Inspiral

```bash
# 30 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/INSPIRAL/Inspi_30.dax --deadline=3000 --use-expected"

# 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/INSPIRAL/Inspi_100.dax --deadline=5000 --use-expected"
```

### Sipht

```bash
# 30 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_30.dax --deadline=3000 --use-expected"

# 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_100.dax --deadline=5000 --use-expected"
```

### Gaussian

```bash
# 54 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/GAUSSIAN/Gauss_54.dax --deadline=3000 --use-expected"

# 629 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/GAUSSIAN/Gauss_629.dax --deadline=10000 --use-expected"
```

---

## Xem Kết Quả Cụ Thể

### Chỉ Xem SCHEDULING_TIME

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --use-expected" 2>&1 | grep "SCHEDULING_TIME"
```

**Output**:
```
SCHEDULING_TIME: 0.034567 seconds
```

---

### Chỉ Xem Performance Metrics

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --use-expected" 2>&1 | grep -A 6 "Performance Metrics"
```

**Output**:
```
Performance Metrics:
  Total Cost: $0.000123
  Total Idle Rate: 0.1234
  Meets Deadline: Yes
  Max Finish Time: 1234.56 seconds
```

---

### Xem Tất Cả Output

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
  -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/Cyber_30.dax --deadline=3000 --use-expected"
```

---

# CÁCH 2: CHẠY TỰ ĐỘNG TẤT CẢ WORKFLOWS

## Script Bash (Linux/Mac/Git Bash)

Tạo file `run-all-workflows.sh`:

```bash
#!/bin/bash

# Màu sắc cho output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== ET2FA Benchmark - Running All Workflows ===${NC}\n"

# Compile project
echo -e "${GREEN}Compiling project...${NC}"
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo -e "${RED}Compilation failed!${NC}"
    exit 1
fi

# Array of workflows: "dax_path:deadline"
workflows=(
    "workflows/benchmark/CYBERSHAKE/Cyber_30.dax:3000"
    "workflows/benchmark/CYBERSHAKE/Cyber_50.dax:4000"
    "workflows/benchmark/CYBERSHAKE/Cyber_100.dax:5000"
    "workflows/benchmark/CYBERSHAKE/Cyber_1000.dax:15000"
    "workflows/benchmark/EPIGE/Epige_24.dax:2000"
    "workflows/benchmark/EPIGE/Epige_46.dax:3000"
    "workflows/benchmark/EPIGE/Epige_100.dax:5000"
    "workflows/benchmark/MONTAGE/Monta_25.dax:2000"
    "workflows/benchmark/MONTAGE/Monta_50.dax:3000"
    "workflows/benchmark/MONTAGE/Monta_100.dax:5000"
    "workflows/benchmark/INSPIRAL/Inspi_30.dax:3000"
    "workflows/benchmark/INSPIRAL/Inspi_50.dax:4000"
    "workflows/benchmark/INSPIRAL/Inspi_100.dax:5000"
    "workflows/benchmark/SIPHT/Sipht_30.dax:3000"
    "workflows/benchmark/SIPHT/Sipht_60.dax:4000"
    "workflows/benchmark/SIPHT/Sipht_100.dax:5000"
    "workflows/benchmark/GAUSSIAN/Gauss_54.dax:3000"
    "workflows/benchmark/GAUSSIAN/Gauss_209.dax:5000"
    "workflows/benchmark/GAUSSIAN/Gauss_629.dax:10000"
)

# Output file
output_file="benchmark_results_$(date +%Y%m%d_%H%M%S).txt"
echo "ET2FA Benchmark Results - $(date)" > "$output_file"
echo "========================================" >> "$output_file"
echo "" >> "$output_file"

# Run each workflow
for workflow in "${workflows[@]}"; do
    IFS=':' read -r dax_path deadline <<< "$workflow"
    workflow_name=$(basename "$dax_path" .dax)
    
    echo -e "${BLUE}Running: $workflow_name (deadline=$deadline)${NC}"
    
    # Run workflow and capture output
    result=$(mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
        -Dexec.args="--dax=$dax_path --deadline=$deadline --use-expected" 2>&1)
    
    # Extract SCHEDULING_TIME
    scheduling_time=$(echo "$result" | grep "SCHEDULING_TIME" | awk '{print $2}')
    
    # Extract Performance Metrics
    total_cost=$(echo "$result" | grep "Total Cost" | awk '{print $3}')
    idle_rate=$(echo "$result" | grep "Total Idle Rate" | awk '{print $4}')
    meets_deadline=$(echo "$result" | grep "Meets Deadline" | awk '{print $3}')
    
    # Write to output file
    echo "Workflow: $workflow_name" >> "$output_file"
    echo "  SCHEDULING_TIME: $scheduling_time" >> "$output_file"
    echo "  Total Cost: $total_cost" >> "$output_file"
    echo "  Idle Rate: $idle_rate" >> "$output_file"
    echo "  Meets Deadline: $meets_deadline" >> "$output_file"
    echo "" >> "$output_file"
    
    echo -e "${GREEN}  ✓ SCHEDULING_TIME: $scheduling_time${NC}"
done

echo -e "\n${GREEN}=== Benchmark Complete ===${NC}"
echo -e "Results saved to: ${BLUE}$output_file${NC}"
```

**Cách chạy**:

```bash
chmod +x run-all-workflows.sh
./run-all-workflows.sh
```

---

## Script PowerShell (Windows)

Tạo file `run-all-workflows.ps1`:

```powershell
# ET2FA Benchmark - Run All Workflows

Write-Host "=== ET2FA Benchmark - Running All Workflows ===" -ForegroundColor Blue
Write-Host ""

# Compile project
Write-Host "Compiling project..." -ForegroundColor Green
mvn clean compile -q
if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit 1
}

# Array of workflows
$workflows = @(
    @{dax="workflows/benchmark/CYBERSHAKE/Cyber_30.dax"; deadline=3000},
    @{dax="workflows/benchmark/CYBERSHAKE/Cyber_50.dax"; deadline=4000},
    @{dax="workflows/benchmark/CYBERSHAKE/Cyber_100.dax"; deadline=5000},
    @{dax="workflows/benchmark/CYBERSHAKE/Cyber_1000.dax"; deadline=15000},
    @{dax="workflows/benchmark/EPIGE/Epige_24.dax"; deadline=2000},
    @{dax="workflows/benchmark/EPIGE/Epige_46.dax"; deadline=3000},
    @{dax="workflows/benchmark/EPIGE/Epige_100.dax"; deadline=5000},
    @{dax="workflows/benchmark/MONTAGE/Monta_25.dax"; deadline=2000},
    @{dax="workflows/benchmark/MONTAGE/Monta_50.dax"; deadline=3000},
    @{dax="workflows/benchmark/MONTAGE/Monta_100.dax"; deadline=5000},
    @{dax="workflows/benchmark/INSPIRAL/Inspi_30.dax"; deadline=3000},
    @{dax="workflows/benchmark/INSPIRAL/Inspi_50.dax"; deadline=4000},
    @{dax="workflows/benchmark/INSPIRAL/Inspi_100.dax"; deadline=5000},
    @{dax="workflows/benchmark/SIPHT/Sipht_30.dax"; deadline=3000},
    @{dax="workflows/benchmark/SIPHT/Sipht_60.dax"; deadline=4000},
    @{dax="workflows/benchmark/SIPHT/Sipht_100.dax"; deadline=5000},
    @{dax="workflows/benchmark/GAUSSIAN/Gauss_54.dax"; deadline=3000},
    @{dax="workflows/benchmark/GAUSSIAN/Gauss_209.dax"; deadline=5000},
    @{dax="workflows/benchmark/GAUSSIAN/Gauss_629.dax"; deadline=10000}
)

# Output file
$outputFile = "benchmark_results_$(Get-Date -Format 'yyyyMMdd_HHmmss').txt"
"ET2FA Benchmark Results - $(Get-Date)" | Out-File -FilePath $outputFile
"========================================" | Out-File -Append -FilePath $outputFile
"" | Out-File -Append -FilePath $outputFile

# Run each workflow
foreach ($workflow in $workflows) {
    $workflowName = [System.IO.Path]::GetFileNameWithoutExtension($workflow.dax)
    Write-Host "Running: $workflowName (deadline=$($workflow.deadline))" -ForegroundColor Blue
    
    # Run workflow
    $result = mvn exec:java -Dexec.mainClass="vn.et2fa.App" `
        -Dexec.args="--dax=$($workflow.dax) --deadline=$($workflow.deadline) --use-expected" 2>&1
    
    # Extract metrics
    $schedulingTime = ($result | Select-String "SCHEDULING_TIME").ToString().Split()[1]
    $totalCost = ($result | Select-String "Total Cost").ToString().Split()[2]
    $idleRate = ($result | Select-String "Total Idle Rate").ToString().Split()[3]
    $meetsDeadline = ($result | Select-String "Meets Deadline").ToString().Split()[2]
    
    # Write to output file
    "Workflow: $workflowName" | Out-File -Append -FilePath $outputFile
    "  SCHEDULING_TIME: $schedulingTime" | Out-File -Append -FilePath $outputFile
    "  Total Cost: $totalCost" | Out-File -Append -FilePath $outputFile
    "  Idle Rate: $idleRate" | Out-File -Append -FilePath $outputFile
    "  Meets Deadline: $meetsDeadline" | Out-File -Append -FilePath $outputFile
    "" | Out-File -Append -FilePath $outputFile
    
    Write-Host "  ✓ SCHEDULING_TIME: $schedulingTime" -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Benchmark Complete ===" -ForegroundColor Green
Write-Host "Results saved to: $outputFile" -ForegroundColor Blue
```

**Cách chạy**:

```powershell
.\run-all-workflows.ps1
```

---

## Script Python (Cross-platform)

Tạo file `run-all-workflows.py`:

```python
#!/usr/bin/env python3
"""
ET2FA Benchmark - Run All Workflows
"""

import subprocess
import os
from datetime import datetime

# Color codes
GREEN = '\033[0;32m'
BLUE = '\033[0;34m'
RED = '\033[0;31m'
NC = '\033[0m'  # No Color

def run_command(cmd):
    """Run command and return output"""
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    return result.stdout + result.stderr

def extract_metric(output, pattern):
    """Extract metric from output"""
    for line in output.split('\n'):
        if pattern in line:
            parts = line.split()
            if len(parts) > 1:
                return parts[-1]
    return "N/A"

def main():
    print(f"{BLUE}=== ET2FA Benchmark - Running All Workflows ==={NC}\n")
    
    # Compile project
    print(f"{GREEN}Compiling project...{NC}")
    result = run_command("mvn clean compile -q")
    if "BUILD SUCCESS" not in result:
        print(f"{RED}Compilation failed!{NC}")
        return
    
    # Workflows to run
    workflows = [
        ("workflows/benchmark/CYBERSHAKE/Cyber_30.dax", 3000),
        ("workflows/benchmark/CYBERSHAKE/Cyber_50.dax", 4000),
        ("workflows/benchmark/CYBERSHAKE/Cyber_100.dax", 5000),
        ("workflows/benchmark/CYBERSHAKE/Cyber_1000.dax", 15000),
        ("workflows/benchmark/EPIGE/Epige_24.dax", 2000),
        ("workflows/benchmark/EPIGE/Epige_46.dax", 3000),
        ("workflows/benchmark/EPIGE/Epige_100.dax", 5000),
        ("workflows/benchmark/MONTAGE/Monta_25.dax", 2000),
        ("workflows/benchmark/MONTAGE/Monta_50.dax", 3000),
        ("workflows/benchmark/MONTAGE/Monta_100.dax", 5000),
        ("workflows/benchmark/INSPIRAL/Inspi_30.dax", 3000),
        ("workflows/benchmark/INSPIRAL/Inspi_50.dax", 4000),
        ("workflows/benchmark/INSPIRAL/Inspi_100.dax", 5000),
        ("workflows/benchmark/SIPHT/Sipht_30.dax", 3000),
        ("workflows/benchmark/SIPHT/Sipht_60.dax", 4000),
        ("workflows/benchmark/SIPHT/Sipht_100.dax", 5000),
        ("workflows/benchmark/GAUSSIAN/Gauss_54.dax", 3000),
        ("workflows/benchmark/GAUSSIAN/Gauss_209.dax", 5000),
        ("workflows/benchmark/GAUSSIAN/Gauss_629.dax", 10000),
    ]
    
    # Output file
    output_file = f"benchmark_results_{datetime.now().strftime('%Y%m%d_%H%M%S')}.txt"
    with open(output_file, 'w') as f:
        f.write(f"ET2FA Benchmark Results - {datetime.now()}\n")
        f.write("========================================\n\n")
        
        # Run each workflow
        for dax_path, deadline in workflows:
            workflow_name = os.path.basename(dax_path).replace('.dax', '')
            print(f"{BLUE}Running: {workflow_name} (deadline={deadline}){NC}")
            
            # Run workflow
            cmd = f'mvn exec:java -Dexec.mainClass="vn.et2fa.App" ' \
                  f'-Dexec.args="--dax={dax_path} --deadline={deadline} --use-expected"'
            output = run_command(cmd)
            
            # Extract metrics
            scheduling_time = extract_metric(output, "SCHEDULING_TIME")
            total_cost = extract_metric(output, "Total Cost")
            idle_rate = extract_metric(output, "Total Idle Rate")
            meets_deadline = extract_metric(output, "Meets Deadline")
            
            # Write to file
            f.write(f"Workflow: {workflow_name}\n")
            f.write(f"  SCHEDULING_TIME: {scheduling_time}\n")
            f.write(f"  Total Cost: {total_cost}\n")
            f.write(f"  Idle Rate: {idle_rate}\n")
            f.write(f"  Meets Deadline: {meets_deadline}\n\n")
            
            print(f"{GREEN}  ✓ SCHEDULING_TIME: {scheduling_time}{NC}")
    
    print(f"\n{GREEN}=== Benchmark Complete ==={NC}")
    print(f"Results saved to: {BLUE}{output_file}{NC}")

if __name__ == "__main__":
    main()
```

**Cách chạy**:

```bash
chmod +x run-all-workflows.py
python3 run-all-workflows.py
```

---

# GIẢI THÍCH OUTPUT

## SCHEDULING_TIME

**Định nghĩa**: Thời gian chạy thuật toán ET2FA (tính bằng giây)

**Bao gồm**:
- Phase 1: T2FA (Task Type First Algorithm)
- Phase 2: DOBS (Delay Operation Based on Block Structure)
- Phase 2.5: CPO (Critical Path Optimization)
- Phase 3: IHSH (Instance Hibernate Scheduling Heuristic)

**Ví dụ**: `SCHEDULING_TIME: 0.034567 seconds`

**So sánh với Table 7**:
- Cyber_30: Expected ~0.034s
- Cyber_100: Expected ~0.128s
- Cyber_1000: Expected ~1.510s

---

## Performance Metrics

### Total Cost

**Định nghĩa**: Tổng chi phí để chạy workflow (tính bằng USD)

**Bao gồm**:
- Running Cost: Chi phí chạy VMs (theo giây, tối thiểu 60s)
- Hibernation Cost: Chi phí ngủ đông (ElasticIP: $0.005/h)

**Ví dụ**: `Total Cost: $0.000123`

---

### Total Idle Rate

**Định nghĩa**: Tỷ lệ nhàn rỗi của các VMs (0.0 = không idle, 1.0 = toàn bộ idle)

**Công thức**: `1 - (totalExecutionTime / leaseDuration)`

**Ví dụ**: `Total Idle Rate: 0.1234` (12.34% idle time)

---

### Meets Deadline

**Định nghĩa**: Kiểm tra xem schedule có đáp ứng deadline không

**Cách tính**: `maxFinishTime <= deadline`

**Giá trị**: `Yes` hoặc `No`

**Ví dụ**: `Meets Deadline: Yes`

---

### Max Finish Time

**Định nghĩa**: Thời gian hoàn thành của task cuối cùng (makespan)

**Đơn vị**: Giây

**Ví dụ**: `Max Finish Time: 1234.56 seconds`

---

# TROUBLESHOOTING

## Lỗi: File DAX không tìm thấy

**Lỗi**:
```
FileNotFoundException: workflows/benchmark/CYBERSHAKE/Cyber_30.dax
```

**Giải pháp**:
1. Kiểm tra đường dẫn file DAX có đúng không
2. Đảm bảo file DAX tồn tại trong thư mục `workflows/benchmark/`
3. Sử dụng đường dẫn tuyệt đối nếu cần:
   ```bash
   --dax=/full/path/to/workflows/benchmark/CYBERSHAKE/Cyber_30.dax
   ```

---

## Lỗi: Compilation Failed

**Lỗi**:
```
[ERROR] BUILD FAILURE
```

**Giải pháp**:
1. Kiểm tra Java version: `java -version` (cần Java 17+)
2. Kiểm tra Maven version: `mvn -version` (cần Maven 3.6+)
3. Xóa cache Maven: `mvn clean`
4. Re-download dependencies: `mvn dependency:resolve`

---

## Lỗi: OutOfMemoryError

**Lỗi**:
```
java.lang.OutOfMemoryError: Java heap space
```

**Giải pháp**:
1. Tăng heap size:
   ```bash
   export MAVEN_OPTS="-Xmx2g"
   mvn exec:java ...
   ```
2. Hoặc trong PowerShell:
   ```powershell
   $env:MAVEN_OPTS="-Xmx2g"
   mvn exec:java ...
   ```

---

## Workflow Lớn Bị Treo

**Vấn đề**: Workflow 1000+ tasks không chạy được

**Giải pháp**:
- DOBS và CPO sẽ tự động skip cho workflow >= 300 tasks
- Nếu vẫn treo, kiểm tra log để xem phase nào đang chạy
- Có thể tăng timeout hoặc giảm số tasks được xử lý

---

## Output Không Hiển Thị

**Vấn đề**: Không thấy output hoặc output bị mất

**Giải pháp**:
1. Redirect output vào file:
   ```bash
   mvn exec:java ... > output.txt 2>&1
   ```
2. Hoặc dùng `tee` để vừa hiển thị vừa lưu:
   ```bash
   mvn exec:java ... 2>&1 | tee output.txt
   ```

---

**Chúc bạn chạy thành công! 🎉**

