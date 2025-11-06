@echo off
echo ========================================
echo Running ET2FA Workflow Scheduling
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

echo [2/2] Running simulation...
echo.
call mvn exec:java -Dexec.mainClass="vn.et2fa.App" -q

echo.
echo ========================================
echo Simulation Complete!
echo ========================================
pause

