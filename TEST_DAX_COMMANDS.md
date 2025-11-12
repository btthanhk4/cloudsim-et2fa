# Các lệnh Terminal để Test File DAX

## Cấu trúc lệnh cơ bản

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=<đường_dẫn_file> --deadline=<deadline>"
```

## 1. Test Workflow mẫu (4 tasks)

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App"
```

## 2. Test từng Workflow

### CyberShake

```bash
# CyberShake 50 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"

# CyberShake 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_100.dax --deadline=5000"

# CyberShake 500 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_500.dax --deadline=10000"
```

### Epigenomics

```bash
# Epigenomics 50 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_50.dax --deadline=5000"

# Epigenomics 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_100.dax --deadline=6000"

# Epigenomics 500 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_500.dax --deadline=12000"
```

### Inspiral (LIGO)

```bash
# Inspiral 50 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_50.dax --deadline=3000"

# Inspiral 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_100.dax --deadline=5000"

# Inspiral 500 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_500.dax --deadline=10000"
```

### Montage

```bash
# Montage 50 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000"

# Montage 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_100.dax --deadline=5000"

# Montage 500 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_500.dax --deadline=10000"
```

### Sipht

```bash
# Sipht 50 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_50.dax --deadline=3000"

# Sipht 100 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_100.dax --deadline=5000"

# Sipht 500 tasks
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_500.dax --deadline=10000"
```

## 3. Test nhanh một workflow cụ thể

### Ví dụ: Test CyberShake_50 với deadline 3000s

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"
```

### Ví dụ: Test Montage_100 với deadline 5000s

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_100.dax --deadline=5000"
```

## 4. Compile trước khi chạy (nếu có thay đổi code)

```bash
mvn clean compile
```

## 5. Chạy với output đầy đủ (không -q)

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"
```

## 6. Chạy với output tối giản (có -q)

```bash
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000" -q
```

## 7. Test tất cả workflows nhỏ (50 tasks)

```bash
# CyberShake_50
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000" -q

# Epigenomics_50
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_50.dax --deadline=5000" -q

# Inspiral_50
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_50.dax --deadline=3000" -q

# Montage_50
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000" -q

# Sipht_50
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_50.dax --deadline=3000" -q
```

## 8. Test với đường dẫn tuyệt đối (nếu cần)

```bash
# Windows path
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=C:/Users/Admin/cloudsim/cloudsim-et2fa/workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"

# Linux/Mac path
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=/path/to/workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000"
```

## Deadline khuyến nghị

| Workflow | 50 tasks | 100 tasks | 500 tasks |
|----------|----------|-----------|-----------|
| CyberShake | 3000s | 5000s | 10000s |
| Epigenomics | 5000s | 6000s | 12000s |
| Inspiral | 3000s | 5000s | 10000s |
| Montage | 3000s | 5000s | 10000s |
| Sipht | 3000s | 5000s | 10000s |

## Lưu ý

1. **Epigenomics** cần deadline cao hơn các workflow khác
2. **Workflows lớn (500 tasks)** có thể mất vài phút để chạy
3. Đảm bảo đã compile code trước khi chạy: `mvn clean compile`
4. Sử dụng `-q` để giảm output log của Maven
5. Để xem kết quả chi tiết, bỏ flag `-q`

## Ví dụ test nhanh

```bash
# Compile
mvn clean compile

# Test một workflow nhỏ
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000"
```

## Troubleshooting

### Lỗi: File not found
- Kiểm tra đường dẫn file DAX có đúng không
- Sử dụng đường dẫn tương đối từ thư mục project root
- Hoặc sử dụng đường dẫn tuyệt đối

### Lỗi: Compilation error
- Chạy `mvn clean compile` để compile lại
- Kiểm tra Java version (cần Java 17+)

### Lỗi: Deadline not met
- Tăng deadline value
- Kiểm tra workflow có quá phức tạp không


