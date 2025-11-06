#!/bin/bash

echo "========================================"
echo "Running ET2FA Workflow Scheduling"
echo "========================================"
echo ""

# Get the directory where the script is located
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$DIR"

echo "[1/2] Compiling..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed!"
    exit 1
fi

echo "[2/2] Running simulation..."
echo ""
mvn exec:java -Dexec.mainClass="vn.et2fa.App" -q

echo ""
echo "========================================"
echo "Simulation Complete!"
echo "========================================"

