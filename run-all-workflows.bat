@echo off
REM Batch script to run all workflows for benchmarking

echo ========================================
echo ET2FA Workflow Benchmarking
echo ========================================
echo.

cd /d %~dp0

echo [1/2] Compiling...
call mvn clean compile -q
if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo [2/2] Running workflows...
echo.

REM Small workflows (50 tasks)
echo === Small Workflows (50 tasks) ===
echo.

echo CyberShake_50...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_50.dax --deadline=3000" -q
echo.

echo Epigenomics_50...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_50.dax --deadline=3000" -q
echo.

echo Inspiral_50...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_50.dax --deadline=3000" -q
echo.

echo Montage_50...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_50.dax --deadline=3000" -q
echo.

echo Sipht_50...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_50.dax --deadline=3000" -q
echo.

REM Medium workflows (100 tasks)
echo === Medium Workflows (100 tasks) ===
echo.

echo CyberShake_100...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_100.dax --deadline=5000" -q
echo.

echo Epigenomics_100...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_100.dax --deadline=5000" -q
echo.

echo Inspiral_100...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_100.dax --deadline=5000" -q
echo.

echo Montage_100...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_100.dax --deadline=5000" -q
echo.

echo Sipht_100...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_100.dax --deadline=5000" -q
echo.

REM Large workflows (500 tasks) - Replaced 1000 tasks for faster execution
echo === Large Workflows (500 tasks) ===
echo.

echo CyberShake_500...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/CYBERSHAKE/CyberShake_500.dax --deadline=10000" -q
echo.

echo Epigenomics_500...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/GENOME/Epigenomics_500.dax --deadline=12000" -q
echo.

echo Inspiral_500...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/LIGO/Inspiral_500.dax --deadline=10000" -q
echo.

echo Montage_500...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/MONTAGE/Montage_500.dax --deadline=10000" -q
echo.

echo Sipht_500...
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -Dexec.args="--dax=workflows/benchmark/SIPHT/Sipht_500.dax --deadline=10000" -q
echo.

echo ========================================
echo Benchmark Complete!
echo ========================================
echo.
echo Note: For detailed results and CSV export, use run-batch-tests.ps1
echo.
pause

