#!/bin/bash
# Script to test DAX workflows with ET2FA algorithm

echo "========================================"
echo "ET2FA Workflow Testing Script"
echo "========================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to test a workflow
test_workflow() {
    local workflow_path=$1
    local deadline=$2
    local workflow_name=$(basename "$workflow_path" .dax)
    
    echo -e "${BLUE}Testing: $workflow_name${NC}"
    echo "Deadline: ${deadline}s"
    echo "---"
    
    mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
        -Dexec.args="--dax=$workflow_path --deadline=$deadline" -q
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ $workflow_name completed successfully${NC}"
    else
        echo -e "${RED}✗ $workflow_name failed${NC}"
    fi
    echo ""
}

# Compile first
echo "Compiling..."
mvn clean compile -q
if [ $? -ne 0 ]; then
    echo -e "${RED}Compilation failed!${NC}"
    exit 1
fi
echo ""

# Test workflows
echo "========================================"
echo "Testing Workflows"
echo "========================================"
echo ""

# Small workflows (50 tasks)
echo "=== Small Workflows (50 tasks) ==="
test_workflow "workflows/benchmark/CYBERSHAKE/CyberShake_50.dax" 3000
test_workflow "workflows/benchmark/GENOME/Epigenomics_50.dax" 5000
test_workflow "workflows/benchmark/LIGO/Inspiral_50.dax" 3000
test_workflow "workflows/benchmark/MONTAGE/Montage_50.dax" 3000
test_workflow "workflows/benchmark/SIPHT/Sipht_50.dax" 3000

echo ""
echo "========================================"
echo "Testing Complete!"
echo "========================================"


