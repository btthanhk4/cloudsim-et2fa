#!/bin/bash

# Script to test ET2FA with different workflow sizes (50, 100, 500 tasks)
# Usage: ./test-all-workflows.sh

echo "=========================================="
echo "ET2FA Workflow Testing Script"
echo "=========================================="
echo ""

# Compile first
echo "Compiling project..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo "ERROR: Compilation failed!"
    exit 1
fi
echo "Compilation successful!"
echo ""

# Test function
test_workflow() {
    local dax_file=$1
    local deadline=$2
    local name=$3
    
    echo "----------------------------------------"
    echo "Testing: $name"
    echo "DAX File: $dax_file"
    echo "Deadline: $deadline seconds"
    echo "----------------------------------------"
    
    mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
        -Dexec.args="--dax=$dax_file --deadline=$deadline" \
        -q 2>&1 | grep -E "(ET2FA: Scheduled|Total Cost:|Total Idle Rate:|Meets Deadline:|Max Finish Time:|ERROR|WARNING)" | head -10
    
    echo ""
    echo "----------------------------------------"
    echo ""
}

# Test workflows
echo "Starting workflow tests..."
echo ""

# CyberShake 50 tasks
test_workflow "workflows/benchmark/CYBERSHAKE/CyberShake_50.dax" 3000 "CyberShake 50 tasks"

# CyberShake 100 tasks
test_workflow "workflows/benchmark/CYBERSHAKE/CyberShake_100.dax" 5000 "CyberShake 100 tasks"

# CyberShake 500 tasks
test_workflow "workflows/benchmark/CYBERSHAKE/CyberShake_500.dax" 15000 "CyberShake 500 tasks"

echo "=========================================="
echo "All tests completed!"
echo "=========================================="

