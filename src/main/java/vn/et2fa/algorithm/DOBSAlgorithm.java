package vn.et2fa.algorithm;

import org.cloudbus.cloudsim.vms.Vm;
import vn.et2fa.model.Et2faTask;
import vn.et2fa.util.WorkflowDAG;
import vn.et2fa.util.VmConfig;

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
    private WorkflowDAG dag;
    
    public DOBSAlgorithm(Map<Et2faTask, Vm> schedule) {
        this(schedule, null);
    }
    
    public DOBSAlgorithm(Map<Et2faTask, Vm> schedule, WorkflowDAG dag) {
        this.schedule = schedule;
        this.dag = dag;
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
     * Added iteration limit to prevent infinite loops
     */
    public void optimize() {
        boolean changed = true;
        // Reduced max iterations to prevent long execution times
        // For 50 tasks, 100 iterations should be sufficient
        int maxIterations = Math.min(100, tasksByVm.size() * 20);
        int iteration = 0;
        int totalDelays = 0;
        
        while (changed && iteration < maxIterations) {
            changed = false;
            iteration++;
            
            // Create a copy of VM set to avoid concurrent modification
            List<Vm> vmList = new ArrayList<>(tasksByVm.keySet());
            
            for (Vm vm : vmList) {
                List<Et2faTask> tasks = tasksByVm.get(vm);
                if (tasks.isEmpty()) continue;
                
                // Find first block structure
                List<Et2faTask> block = findFirstBlockStructure(vm);
                if (block == null || block.isEmpty()) continue;
                
                // Calculate estimated latest finish times
                Map<Et2faTask, Double> estimatedLatestFinishTimes = 
                    calculateEstimatedLatestFinishTimes(block);
                
                // Check if block can be delayed (Theorem 1 condition)
                // Condition: ∀x in X, t_x^F > t_x^S (estimated latest finish time > actual start time)
                boolean canDelay = true;
                for (Et2faTask task : block) {
                    double estimatedLatest = estimatedLatestFinishTimes.get(task);
                    // If estimated latest finish time is less than or equal to start time, cannot delay
                    // Also check if estimated latest is at least greater than current finish time
                    if (estimatedLatest != Double.MAX_VALUE) {
                        if (estimatedLatest <= task.getActualStartTime() || estimatedLatest <= task.getActualFinishTime()) {
                            canDelay = false;
                            break;
                        }
                    }
                    // If estimatedLatest is Double.MAX_VALUE, task can be delayed (no external constraints)
                }
                
                if (canDelay) {
                    // Calculate delay time Δt
                    double delayTime = calculateDelayTime(block, estimatedLatestFinishTimes);
                    
                    // Increased tolerance to avoid infinite small delays
                    // Also check if delayTime is reasonable (not too large)
                    if (delayTime > 0.1 && delayTime < 100000) { // Reasonable delay range
                        // Apply delay to all tasks in block
                        for (Et2faTask task : block) {
                            task.setActualStartTime(task.getActualStartTime() + delayTime);
                            task.setActualFinishTime(task.getActualFinishTime() + delayTime);
                        }
                        
                        // Update subsequent tasks on the same VM if needed
                        updateSubsequentTasks(vm, block);
                        changed = true;
                        totalDelays++;
                        break; // Only process one block per iteration to avoid complexity
                    }
                }
            }
            
            // Progress indicator for large workflows
            if (iteration % 10 == 0 && iteration < maxIterations) {
                System.out.println("DOBS: Iteration " + iteration + ", delays applied: " + totalDelays);
            }
        }
        
        if (iteration >= maxIterations) {
            System.out.println("DOBS: Reached maximum iterations (" + maxIterations + "), total delays: " + totalDelays);
        } else {
            System.out.println("DOBS: Completed in " + iteration + " iterations, total delays: " + totalDelays);
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
     * t_x^F = min{t_y^S - C_{x,y}^T | y in (Suc(x) - Suc(x) ∩ X)}
     * where C_{x,y}^T is communication time from x to y
     */
    private Map<Et2faTask, Double> calculateEstimatedLatestFinishTimes(List<Et2faTask> block) {
        Map<Et2faTask, Double> estimatedTimes = new HashMap<>();
        Set<Et2faTask> blockSet = new HashSet<>(block);
        
        for (Et2faTask task : block) {
            List<Et2faTask> successors = task.getSuccessors();
            
            // If no successors, estimated latest finish time is current finish time
            if (successors.isEmpty()) {
                estimatedTimes.put(task, Double.MAX_VALUE); // Can be delayed indefinitely
                continue;
            }
            
            double minStartTime = Double.MAX_VALUE;
            for (Et2faTask succ : successors) {
                // Skip successors in the same block (Suc(x) ∩ X)
                if (blockSet.contains(succ)) {
                    continue;
                }
                
                // Calculate communication time C_{x,y}^T
                Vm taskVm = schedule.get(task);
                Vm succVm = schedule.get(succ);
                double commTime = 0;
                if (taskVm != null && succVm != null && taskVm != succVm && dag != null) {
                    double dataSize = dag.getDataTransfer(task, succ); // Data size in MB
                    commTime = VmConfig.calculateCommunicationTime(dataSize, taskVm, succVm);
                }
                
                // t_y^S - C_{x,y}^T
                double succStartTime = succ.getActualStartTime();
                double estimatedFinish = succStartTime - commTime;
                minStartTime = Math.min(minStartTime, estimatedFinish);
            }
            
            if (minStartTime < Double.MAX_VALUE) {
                estimatedTimes.put(task, minStartTime);
            } else {
                // No external successors, can be delayed indefinitely
                estimatedTimes.put(task, Double.MAX_VALUE);
            }
        }
        
        return estimatedTimes;
    }

    /**
     * Calculate delay time (Equation 25)
     * Δt = min{t_{|X|+1}^S - t_{|X|}^F, min{t_x^F - t_x^S | x in X}}
     */
    private double calculateDelayTime(List<Et2faTask> block, 
                                       Map<Et2faTask, Double> estimatedLatestFinishTimes) {
        Vm vm = schedule.get(block.get(0));
        List<Et2faTask> allTasks = tasksByVm.get(vm);
        
        // Find idle time after block: t_{|X|+1}^S - t_{|X|}^F
        double idleTime = Double.MAX_VALUE;
        Et2faTask lastInBlock = block.get(block.size() - 1);
        int lastIndex = allTasks.indexOf(lastInBlock);
        
        if (lastIndex >= 0 && lastIndex < allTasks.size() - 1) {
            Et2faTask nextTask = allTasks.get(lastIndex + 1);
            idleTime = nextTask.getActualStartTime() - lastInBlock.getActualFinishTime();
        }
        
        // Find minimum slack time: min{t_x^F - t_x^S | x in X}
        // where t_x^F is estimated latest finish time
        double minSlack = Double.MAX_VALUE;
        for (Et2faTask task : block) {
            double estimatedLatest = estimatedLatestFinishTimes.get(task);
            if (estimatedLatest < Double.MAX_VALUE) {
                double slack = estimatedLatest - task.getActualStartTime();
                if (slack > 0) {
                    minSlack = Math.min(minSlack, slack);
                }
            }
        }
        
        // Return minimum of idle time and minimum slack
        double delayTime = Math.min(idleTime, minSlack);
        
        // Only return positive delay time
        return delayTime > 0 ? delayTime : 0;
    }

    /**
     * Update subsequent tasks on the same VM after delaying a block
     * This ensures that tasks after the block are properly shifted if needed
     */
    private void updateSubsequentTasks(Vm vm, List<Et2faTask> delayedBlock) {
        List<Et2faTask> tasks = tasksByVm.get(vm);
        if (tasks.isEmpty()) return;
        
        Et2faTask lastInBlock = delayedBlock.get(delayedBlock.size() - 1);
        int lastIndex = tasks.indexOf(lastInBlock);
        
        if (lastIndex < 0 || lastIndex >= tasks.size() - 1) {
            return; // No subsequent tasks
        }
        
        // Check if we need to shift subsequent tasks
        // Only shift if there's an overlap
        for (int i = lastIndex + 1; i < tasks.size(); i++) {
            Et2faTask currentTask = tasks.get(i);
            Et2faTask prevTask = tasks.get(i - 1);
            
            // If current task starts before previous task finishes, adjust it
            if (currentTask.getActualStartTime() < prevTask.getActualFinishTime()) {
                // Shift task to start after previous task finishes
                double newStartTime = prevTask.getActualFinishTime();
                double executionTime = currentTask.getActualFinishTime() - currentTask.getActualStartTime();
                currentTask.setActualStartTime(newStartTime);
                currentTask.setActualFinishTime(newStartTime + executionTime);
            } else {
                // No overlap, no need to shift further tasks
                break;
            }
        }
    }
}




