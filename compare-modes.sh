#!/bin/bash

# Script để so sánh Original vs Optimized mode

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BOLD='\033[1m'
NC='\033[0m'

DAX_PATH=$1
DEADLINE=${2:-3000}

if [ -z "$DAX_PATH" ]; then
    echo "Usage: ./compare-modes.sh <dax_path> [deadline]"
    echo "Example: ./compare-modes.sh workflows/benchmark/CYBERSHAKE/Cyber_30.dax 3000"
    exit 1
fi

WORKFLOW_NAME=$(basename "$DAX_PATH" .dax)

echo -e "${BOLD}${BLUE}========================================${NC}"
echo -e "${BOLD}${BLUE}  So Sánh Original vs Optimized${NC}"
echo -e "${BOLD}${BLUE}  Workflow: $WORKFLOW_NAME${NC}"
echo -e "${BOLD}${BLUE}========================================${NC}"
echo ""

# Run Original
echo -e "${YELLOW}1. Running ORIGINAL mode (no optimizations)...${NC}"
ORIGINAL_OUTPUT=$(mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
    -Dexec.args="--dax=$DAX_PATH --deadline=$DEADLINE --mode=original" 2>&1)

ORIGINAL_TIME=$(echo "$ORIGINAL_OUTPUT" | grep "SCHEDULING_TIME" | awk '{print $2}')
ORIGINAL_PHASE1=$(echo "$ORIGINAL_OUTPUT" | grep "Phase 1 completed" | grep -oE '[0-9]+ms' | head -1)
ORIGINAL_PHASE2=$(echo "$ORIGINAL_OUTPUT" | grep "Phase 2 completed" | grep -oE '[0-9]+ms' | head -1)
ORIGINAL_CPO=$(echo "$ORIGINAL_OUTPUT" | grep "Phase 2.5" | wc -l)

echo -e "${GREEN}✓ Original Results:${NC}"
echo -e "  SCHEDULING_TIME: ${BOLD}$ORIGINAL_TIME${NC} seconds"
echo -e "  Phase 1: $ORIGINAL_PHASE1"
echo -e "  Phase 2: $ORIGINAL_PHASE2"
if [ "$ORIGINAL_CPO" -eq 0 ]; then
    echo -e "  Phase 2.5 CPO: ${RED}❌ Không có${NC}"
else
    echo -e "  Phase 2.5 CPO: ${GREEN}✅ Có${NC}"
fi
echo ""

# Run Optimized
echo -e "${YELLOW}2. Running OPTIMIZED mode (all optimizations)...${NC}"
OPTIMIZED_OUTPUT=$(mvn exec:java -Dexec.mainClass="vn.et2fa.App" \
    -Dexec.args="--dax=$DAX_PATH --deadline=$DEADLINE --mode=optimized" 2>&1)

OPTIMIZED_TIME=$(echo "$OPTIMIZED_OUTPUT" | grep "SCHEDULING_TIME" | awk '{print $2}')
OPTIMIZED_PHASE1=$(echo "$OPTIMIZED_OUTPUT" | grep "Phase 1 completed" | grep -oE '[0-9]+ms' | head -1)
OPTIMIZED_PHASE2=$(echo "$OPTIMIZED_OUTPUT" | grep "Phase 2 completed" | grep -oE '[0-9]+ms' | head -1)
OPTIMIZED_CPO=$(echo "$OPTIMIZED_OUTPUT" | grep "Phase 2.5" | wc -l)
OPTIMIZED_CPO_TIME=$(echo "$OPTIMIZED_OUTPUT" | grep "Phase 2.5 completed" | grep -oE '[0-9]+ms' | head -1)

echo -e "${GREEN}✓ Optimized Results:${NC}"
echo -e "  SCHEDULING_TIME: ${BOLD}$OPTIMIZED_TIME${NC} seconds"
echo -e "  Phase 1: $OPTIMIZED_PHASE1"
echo -e "  Phase 2: $OPTIMIZED_PHASE2"
if [ "$OPTIMIZED_CPO" -gt 0 ]; then
    echo -e "  Phase 2.5 CPO: ${GREEN}✅ Có${NC} ($OPTIMIZED_CPO_TIME)"
else
    echo -e "  Phase 2.5 CPO: ${RED}❌ Không có${NC}"
fi
echo ""

# Calculate improvement
if [ ! -z "$ORIGINAL_TIME" ] && [ ! -z "$OPTIMIZED_TIME" ]; then
    # Use awk for floating point calculation
    IMPROVEMENT=$(echo "$ORIGINAL_TIME $OPTIMIZED_TIME" | awk '{printf "%.1f", ($1 - $2) / $1 * 100}')
    TIME_SAVED=$(echo "$ORIGINAL_TIME $OPTIMIZED_TIME" | awk '{printf "%.6f", $1 - $2}')
    
    echo -e "${BOLD}${BLUE}=== So Sánh ===${NC}"
    echo -e "Original:   ${BOLD}$ORIGINAL_TIME${NC} s"
    echo -e "Optimized:  ${BOLD}$OPTIMIZED_TIME${NC} s"
    echo -e "Time Saved: ${GREEN}${BOLD}$TIME_SAVED${NC} s${NC}"
    echo -e "Improvement: ${GREEN}${BOLD}${IMPROVEMENT}% ⬇️${NC}"
    echo ""
    
    # Check if improvement meets target (10-15%)
    IMPROVEMENT_NUM=$(echo "$IMPROVEMENT" | awk '{print int($1)}')
    if [ "$IMPROVEMENT_NUM" -ge 10 ] && [ "$IMPROVEMENT_NUM" -le 15 ]; then
        echo -e "${GREEN}✅ Đạt mục tiêu (10-15% improvement)!${NC}"
    elif [ "$IMPROVEMENT_NUM" -gt 15 ]; then
        echo -e "${GREEN}✅ Vượt mục tiêu (>15% improvement)!${NC}"
    else
        echo -e "${YELLOW}⚠️  Chưa đạt mục tiêu (<10% improvement)${NC}"
    fi
fi

