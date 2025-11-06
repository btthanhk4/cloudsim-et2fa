package vn.et2fa.algorithm;

import org.cloudbus.cloudsim.vms.Vm;
import vn.et2fa.model.Et2faTask;

import java.util.*;

/**
 * Delay Operation Based on Block Structure (DOBS) - Phase 2 of ET2FA
 * Algorithm 3 from the paper
 * 
 * Implements Theorem 1: Delaying the start time of block structures
 * can reduce idle time and cost when conditions are met.
 */
public class DOBSAlgorithm {
    private Map<Et2faTask, Vm> schedule;
    private Map<Vm, List<Et2faTask>> tasksByVm;
    
    public DOBSAlgorithm(Map<Et2faTask, Vm> schedule) {
        this.schedule = schedule;
        this.tasksByVm = new HashMap<>();
        
        // Group tasks by VM
        for (Map.Entry<Et2faTask, Vm> entry : schedule.entrySet()) {
            tasksByVm.computeIfAbsent(entry.getValue(), k -> new ArrayList<>())
                .add(entry.getKey());
        }
        
        // Sort tasks by start time on each VM
        for (List<Et2faTask> tasks : tasksByVm.values()) {
            tasks.sort(Comparator.comparingDouble(Et2faTask::getActualStartTime));
        }
    }

    /**
     * Main DOBS algorithm (Algorithm 3 from paper)
     */
    public void optimize() {
        boolean changed = true;
        while (changed) {
            changed = false;
            
            for (Vm vm : tasksByVm.keySet()) {
                List<Et2faTask> tasks = tasksByVm.get(vm);
                if (tasks.isEmpty()) continue;
                
                // Find first block structure
                List<Et2faTask> block = findFirstBlockStructure(vm);
                if (block == null || block.isEmpty()) continue;
                
                // Calculate estimated latest finish times
                Map<Et2faTask, Double> estimatedLatestFinishTimes = 
                    calculateEstimatedLatestFinishTimes(block);
                
                // Check if block can be delayed
                boolean canDelay = true;
                for (Et2faTask task : block) {
                    double estimatedLatest = estimatedLatestFinishTimes.get(task);
                    if (estimatedLatest <= task.getActualFinishTime()) {
                        canDelay = false;
                        break;
                    }
                }
                
                if (canDelay) {
                    // Calculate delay time
                    double delayTime = calculateDelayTime(block, estimatedLatestFinishTimes);
                    
                    if (delayTime > 0) {
                        // Apply delay
                        for (Et2faTask task : block) {
                            task.setActualStartTime(task.getActualStartTime() + delayTime);
                            task.setActualFinishTime(task.getActualFinishTime() + delayTime);
                        }
                        
                        // Update VM completion times
                        updateVmCompletionTimes(vm);
                        changed = true;
                    }
                }
            }
        }
    }

    /**
     * Find first block structure on a VM
     * Block structure: tasks continuously executed without idle intervals
     */
    private List<Et2faTask> findFirstBlockStructure(Vm vm) {
        List<Et2faTask> tasks = tasksByVm.get(vm);
        if (tasks.isEmpty()) return null;
        
        List<Et2faTask> block = new ArrayList<>();
        block.add(tasks.get(0));
        
        for (int i = 1; i < tasks.size(); i++) {
            Et2faTask prev = tasks.get(i - 1);
            Et2faTask curr = tasks.get(i);
            
            // Check if there's an idle gap
            double gap = curr.getActualStartTime() - prev.getActualFinishTime();
            if (gap > 0.001) { // Small tolerance for floating point
                break;
            }
            
            block.add(curr);
        }
        
        return block;
    }

    /**
     * Calculate estimated latest finish time for each task in block (Equation 24)
     */
    private Map<Et2faTask, Double> calculateEstimatedLatestFinishTimes(List<Et2faTask> block) {
        Map<Et2faTask, Double> estimatedTimes = new HashMap<>();
        Set<Et2faTask> blockSet = new HashSet<>(block);
        
        for (Et2faTask task : block) {
            List<Et2faTask> successors = task.getSuccessors();
            
            if (successors.isEmpty()) {
                estimatedTimes.put(task, task.getActualFinishTime());
                continue;
            }
            
            double minStartTime = Double.MAX_VALUE;
            for (Et2faTask succ : successors) {
                // Skip successors in the same block
                if (blockSet.contains(succ)) {
                    continue;
                }
                
                // Calculate communication time (simplified)
                double commTime = 0; // Would need actual VM info
                double succStartTime = succ.getActualStartTime();
                minStartTime = Math.min(minStartTime, succStartTime - commTime);
            }
            
            if (minStartTime < Double.MAX_VALUE) {
                estimatedTimes.put(task, minStartTime);
            } else {
                estimatedTimes.put(task, task.getActualFinishTime());
            }
        }
        
        return estimatedTimes;
    }

    /**
     * Calculate delay time (Equation 25)
     */
    private double calculateDelayTime(List<Et2faTask> block, 
                                       Map<Et2faTask, Double> estimatedLatestFinishTimes) {
        // Find idle time after block
        double idleTime = Double.MAX_VALUE;
        if (block.size() < tasksByVm.get(schedule.get(block.get(0))).size()) {
            Et2faTask lastInBlock = block.get(block.size() - 1);
            List<Et2faTask> allTasks = tasksByVm.get(schedule.get(block.get(0)));
            int nextIndex = allTasks.indexOf(lastInBlock) + 1;
            if (nextIndex < allTasks.size()) {
                Et2faTask nextTask = allTasks.get(nextIndex);
                idleTime = nextTask.getActualStartTime() - lastInBlock.getActualFinishTime();
            }
        }
        
        // Find minimum slack time
        double minSlack = Double.MAX_VALUE;
        for (Et2faTask task : block) {
            double estimatedLatest = estimatedLatestFinishTimes.get(task);
            double slack = estimatedLatest - task.getActualFinishTime();
            minSlack = Math.min(minSlack, slack);
        }
        
        return Math.min(idleTime, minSlack);
    }

    private void updateVmCompletionTimes(Vm vm) {
        List<Et2faTask> tasks = tasksByVm.get(vm);
        if (tasks.isEmpty()) return;
        
        // Tasks are already sorted by start time
        // Completion time is the finish time of the last task
        Et2faTask lastTask = tasks.get(tasks.size() - 1);
        // VM completion time is updated implicitly through task finish times
    }
}

