# Cấu trúc Project ET2FA

## 📁 Cấu trúc thư mục

```
cloudsim-et2fa/
├── README.md                    # Documentation chính
├── RUN_COMMANDS.md              # Các câu lệnh chạy workflows
├── pom.xml                      # Maven configuration
├── run.bat                      # Script chạy workflow mẫu
├── run-all-workflows.bat        # Script chạy tất cả workflows
├── run-batch-tests.ps1          # Script batch test với CSV export
├── src/
│   ├── main/java/vn/et2fa/
│   │   ├── algorithm/           # 3 phases của ET2FA
│   │   │   ├── T2FAAlgorithm.java
│   │   │   ├── DOBSAlgorithm.java
│   │   │   └── IHSHAlgorithm.java
│   │   ├── broker/
│   │   │   └── Et2faBroker.java
│   │   ├── model/
│   │   │   ├── Et2faTask.java
│   │   │   └── TaskType.java
│   │   ├── util/
│   │   │   ├── DaxLoader.java
│   │   │   └── WorkflowDAG.java
│   │   └── App.java
│   └── test/java/
│       └── vn/et2fa/
│           └── AppTest.java
└── workflows/
    └── benchmark/
        ├── CYBERSHAKE/          # 3 workflows (50, 100, 1000 tasks)
        ├── GENOME/              # 3 workflows (50, 100, 1000 tasks)
        ├── LIGO/                # 3 workflows (50, 100, 1000 tasks)
        ├── MONTAGE/             # 3 workflows (50, 100, 1000 tasks)
        └── SIPHT/               # 3 workflows (50, 100, 1000 tasks)
```

## 📋 Files quan trọng

### Source Code
- `src/main/java/vn/et2fa/algorithm/` - 3 phases của ET2FA
- `src/main/java/vn/et2fa/broker/Et2faBroker.java` - Main scheduler
- `src/main/java/vn/et2fa/util/DaxLoader.java` - Load DAX files
- `src/main/java/vn/et2fa/App.java` - Main application

### Scripts
- `run.bat` - Chạy workflow mẫu (4 tasks)
- `run-all-workflows.bat` - Chạy tất cả workflows (15 workflows)
- `run-batch-tests.ps1` - Batch test với CSV export

### Documentation
- `README.md` - Documentation chính
- `RUN_COMMANDS.md` - Các câu lệnh chạy workflows
- `PROJECT_STRUCTURE.md` - File này

### Workflows
- `workflows/benchmark/` - 15 workflows được tổ chức theo 5 loại

## 🚀 Cách sử dụng

### Chạy workflow mẫu
```cmd
run.bat
```

### Chạy từng workflow
Xem `RUN_COMMANDS.md` để biết tất cả các câu lệnh.

### Chạy batch test
```powershell
.\run-batch-tests.ps1
```

## ✅ Tổng kết

- **Source Code**: Đầy đủ các file Java
- **Scripts**: 3 scripts để chạy workflows
- **Documentation**: 3 files documentation
- **Workflows**: 15 workflows được tổ chức rõ ràng
- **Gọn gàng**: Đã xóa các file không cần thiết

