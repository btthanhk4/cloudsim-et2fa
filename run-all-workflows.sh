#!/bin/bash

# Script to run all 28 workflows in both original and optimized modes (56 commands total)
# Displays Performance Metrics for each run

echo "=================================================================================="
echo "=== RUNNING ALL WORKFLOWS (28 workflows × 2 modes = 56 commands) ==="
echo "=================================================================================="
echo ""

# Array of workflows with their deadlines
declare -a workflows=(
    "workflows/benchmark/CYBERSHAKE/Cyber_30.dax:3000"
    "workflows/benchmark/CYBERSHAKE/Cyber_50.dax:5000"
    "workflows/benchmark/CYBERSHAKE/Cyber_100.dax:10000"
    "workflows/benchmark/CYBERSHAKE/Cyber_1000.dax:50000"
    "workflows/benchmark/INSPIRAL/Inspi_30.dax:3000"
    "workflows/benchmark/INSPIRAL/Inspi_50.dax:5000"
    "workflows/benchmark/INSPIRAL/Inspi_100.dax:15000"
    "workflows/benchmark/INSPIRAL/Inspi_1000.dax:50000"
    "workflows/benchmark/SIPHT/Sipht_30.dax:3000"
    "workflows/benchmark/SIPHT/Sipht_60.dax:6000"
    "workflows/benchmark/SIPHT/Sipht_100.dax:10000"
    "workflows/benchmark/SIPHT/Sipht_1000.dax:50000"
    "workflows/benchmark/EPIGE/Epige_24.dax:3000"
    "workflows/benchmark/EPIGE/Epige_46.dax:5000"
    "workflows/benchmark/EPIGE/Epige_100.dax:15000"
    "workflows/benchmark/EPIGE/Epige_997.dax:50000"
    "workflows/benchmark/MONTAGE/Monta_25.dax:3000"
    "workflows/benchmark/MONTAGE/Monta_50.dax:5000"
    "workflows/benchmark/MONTAGE/Monta_100.dax:10000"
    "workflows/benchmark/MONTAGE/Monta_1000.dax:50000"
    "workflows/benchmark/GAUSSIAN/Gauss_54.dax:5000"
    "workflows/benchmark/GAUSSIAN/Gauss_209.dax:10000"
    "workflows/benchmark/GAUSSIAN/Gauss_629.dax:20000"
    "workflows/benchmark/GAUSSIAN/Gauss_1034.dax:30000"
    "workflows/benchmark/MOLECULAR/Molec_0.dax:3000"
    "workflows/benchmark/MOLECULAR/Molec_1.dax:3000"
    "workflows/benchmark/MOLECULAR/Molec_2.dax:3000"
    "workflows/benchmark/MOLECULAR/Molec_3.dax:3000"
)

total=0

for workflow_info in "${workflows[@]}"; do
    IFS=':' read -r workflow deadline <<< "$workflow_info"
    workflow_name=$(basename "$workflow" .dax)
    
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "Workflow: $workflow_name (Deadline: $deadline)"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    
    # Run Original mode
    echo "[$((++total))] ORIGINAL MODE"
    echo "────────────────────────────────────────────────────────────────────────────────"
    echo "Running... (this may take a while for large workflows)"
    output=$(mvn -q exec:java -Dexec.mainClass="vn.et2fa.App" \
        -Dexec.args="--dax=$workflow --deadline=$deadline --mode=original" 2>&1)
    
    echo "=== Performance Metrics ==="
    echo "$output" | grep -E "(Total Cost|Total Idle Rate|Meets Deadline|Max Finish Time|Deadline|SCHEDULING_TIME)"
    echo ""
    
    # Run Optimized mode
    echo "[$((++total))] OPTIMIZED MODE"
    echo "────────────────────────────────────────────────────────────────────────────────"
    echo "Running... (this may take a while for large workflows)"
    output=$(mvn -q exec:java -Dexec.mainClass="vn.et2fa.App" \
        -Dexec.args="--dax=$workflow --deadline=$deadline --mode=optimized" 2>&1)
    
    echo "=== Performance Metrics ==="
    echo "$output" | grep -E "(Total Cost|Total Idle Rate|Meets Deadline|Max Finish Time|Deadline|SCHEDULING_TIME)"
    echo ""
    echo ""
done

echo "=================================================================================="
echo "=== COMPLETED: All 56 commands executed ==="
echo "=================================================================================="
