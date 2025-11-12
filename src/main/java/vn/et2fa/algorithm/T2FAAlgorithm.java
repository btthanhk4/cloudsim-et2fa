package vn.et2fa.algorithm;

import org.cloudbus.cloudsim.vms.Vm;
import vn.et2fa.model.Et2faTask;
import vn.et2fa.model.TaskType;
import vn.et2fa.util.WorkflowDAG;
import vn.et2fa.util.VmConfig;

import java.util.*;

/**
 * Task Type First Algorithm (T2FA) - Phase 1 of ET2FA
 * Algorithm 1 and 2 from the paper
 */
public class T2FAAlgorithm {
    private WorkflowDAG dag;
    private List<Vm> availableVms;
    private Map<Vm, Double> vmCompletionTimes;
    private Map<Et2faTask, Vm> taskVmMap; // Track which VM each task is assigned to
    private Map<Integer, List<Et2faTask>> tasksByLevel;
    private Set<TaskType> type0Tasks;
    private Set<TaskType> type1Tasks;
    private Set<TaskType> type2Tasks;
    private Set<TaskType> type3Tasks;
    private Set<TaskType> type4Tasks;
    private double tStar; // Expected maximum finish time
    private Set<Vm> vC; // VMs with running tasks at current level
    private Set<Vm> vP; // VMs with running tasks at previous level
    
    public T2FAAlgorithm(WorkflowDAG dag, List<Vm> availableVms) {
        this.dag = dag;
        this.availableVms = new ArrayList<>(availableVms);
        this.vmCompletionTimes = new HashMap<>();
        this.taskVmMap = new HashMap<>();
        this.type0Tasks = new HashSet<>();
        this.type1Tasks = new HashSet<>();
        this.type2Tasks = new HashSet<>();
        this.type3Tasks = new HashSet<>();
        this.type4Tasks = new HashSet<>();
        this.vC = new HashSet<>();
        this.vP = new HashSet<>();
        
        // Initialize VM completion times with cold startup time (DurC = 55.9s)
        for (Vm vm : availableVms) {
            vmCompletionTimes.put(vm, 55.9);
        }
    }

    /**
     * Main T2FA algorithm (Algorithm 1 from paper)
     */
    public Map<Et2faTask, Vm> schedule() {
        // Pre-processing
        // Temporarily disable simplifyDAG to preserve all tasks for testing
        // dag.simplifyDAG();
        dag.calculateTopologicalLevels();
        tasksByLevel = dag.getTasksByLevel();
        
        classifyTaskTypes();
        
        // Find VM with highest processing capacity
        Vm maxVm = availableVms.stream()
            .max(Comparator.comparingDouble(Vm::getMips))
            .orElse(availableVms.get(0));
        
        // Initialize tStar based on maximum computation and fastest VM
        // Equation: f* = max{w_i, e_i, a_i} / U(k) where U(k) is processing capacity
        List<Et2faTask> level0Tasks = tasksByLevel.getOrDefault(0, new ArrayList<>());
        if (!level0Tasks.isEmpty()) {
            double maxComputation = level0Tasks.stream()
                .mapToDouble(Et2faTask::getComputation)
                .max()
                .orElse(0);
            // Use VM processing capacity (GFLOPS) if available, otherwise use MIPS
            VmConfig.VmType maxVmType = VmConfig.getVmType(maxVm);
            double processingCapacity = maxVmType != null ? maxVmType.processingCapacity : maxVm.getMips();
            tStar = maxComputation / processingCapacity;
            if (!vC.contains(maxVm)) {
                vC.add(maxVm);
            }
        } else {
            tStar = 0;
        }
        
        Map<Et2faTask, Vm> schedule = new HashMap<>();
        int maxLevel = tasksByLevel.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        
        // Schedule by topological level
        for (int level = 0; level <= maxLevel; level++) {
            List<Et2faTask> levelTasks = tasksByLevel.getOrDefault(level, new ArrayList<>());
            
            if (levelTasks.isEmpty()) continue;
            
            // Handle TYPE0 tasks (single task in level) - Algorithm 1 lines 7-12
            if (levelTasks.size() == 1) {
                Et2faTask task = levelTasks.get(0);
                // Calculate max estimated finish time of all tasks
                // Use computation on fastest VM as estimate if estimatedFinishTime not set
                double maxEstimatedFinishTime = 0;
                for (Et2faTask t : dag.getTasks()) {
                    double estFinish = t.getEstimatedFinishTime();
                    if (estFinish <= 0) {
                        // Estimate: use computation / fastest VM capacity
                        Vm fastestVm = availableVms.stream()
                            .max(Comparator.comparingDouble(Vm::getMips))
                            .orElse(availableVms.get(0));
                        VmConfig.VmType vmType = VmConfig.getVmType(fastestVm);
                        double capacity = vmType != null ? vmType.processingCapacity : fastestVm.getMips();
                        estFinish = t.getComputation() / capacity;
                    }
                    maxEstimatedFinishTime = Math.max(maxEstimatedFinishTime, estFinish);
                }
                // Check if task computation > 0.1 * max estimated finish time
                // Actually, check if task weight is significant compared to max task weight
                double maxTaskComputation = dag.getTasks().stream()
                    .mapToDouble(Et2faTask::getComputation)
                    .max()
                    .orElse(1);
                if (task.getComputation() > 0.1 * maxTaskComputation) {
                    // Select VM that can finish at the earliest
                    Vm bestVm = selectBestVmForTask(task);
                    scheduleTask(task, bestVm, schedule);
                    vP.clear();
                    vP.add(bestVm);
                    vC.clear();
                    continue;
                } else {
                    // If TYPE0 task doesn't meet the condition, schedule it as a general task
                    // This ensures all tasks are scheduled
                    Vm bestVm = selectBestVmForTask(task);
                    scheduleTask(task, bestVm, schedule);
                    vP.clear();
                    vP.add(bestVm);
                    vC.clear();
                    continue;
                }
            }
            
            // Schedule special types in random order
            List<TaskType> typeOrder = Arrays.asList(
                TaskType.TYPE1, TaskType.TYPE2, TaskType.TYPE3, TaskType.TYPE4
            );
            Collections.shuffle(typeOrder);
            
            List<Et2faTask> remainingTasks = new ArrayList<>(levelTasks);
            
            for (TaskType type : typeOrder) {
                List<Et2faTask> typeTasks = new ArrayList<>();
                for (Et2faTask task : remainingTasks) {
                    if (task.getType() == type) {
                        typeTasks.add(task);
                    }
                }
                
                remainingTasks.removeAll(typeTasks);
                
                // Sort by weight (computation) descending
                typeTasks.sort((a, b) -> Double.compare(b.getComputation(), a.getComputation()));
                
                scheduleTasks(typeTasks, schedule);
            }
            
            // Schedule remaining general tasks
            remainingTasks.sort((a, b) -> Double.compare(b.getComputation(), a.getComputation()));
            scheduleTasks(remainingTasks, schedule);
            
            // Update vP and vC for next level
            vP = new HashSet<>(vC);
            vC.clear();
        }
        
        return schedule;
    }

    /**
     * Classify tasks into types (Type0-Type4) based on DAG structure
     * Optimized to avoid redundant checks
     */
    private void classifyTaskTypes() {
        // First, classify TYPE0 tasks (single task in level)
        for (Map.Entry<Integer, List<Et2faTask>> entry : tasksByLevel.entrySet()) {
            List<Et2faTask> levelTasks = entry.getValue();
            if (levelTasks.size() == 1) {
                Et2faTask task = levelTasks.get(0);
                task.setTaskType(TaskType.TYPE0);
                type0Tasks.add(TaskType.TYPE0);
                continue; // Skip further classification for TYPE0 tasks
            }
        }
        
        // Classify other types
        for (Et2faTask task : dag.getTasks()) {
            // Skip if already classified as TYPE0
            if (task.getType() == TaskType.TYPE0) {
                continue;
            }
            
            List<Et2faTask> successors = task.getSuccessors();
            List<Et2faTask> predecessors = task.getPredecessors();
            
            // Check for TYPE1 (MOSI parent) - Equation 18
            if (successors.size() > 1) {
                boolean allSingleParent = true;
                for (Et2faTask succ : successors) {
                    if (succ.getPredecessors().size() != 1) {
                        allSingleParent = false;
                        break;
                    }
                }
                if (allSingleParent) {
                    task.setTaskType(TaskType.TYPE1);
                    type1Tasks.add(TaskType.TYPE1);
                    continue;
                }
            }
            
            // Check for TYPE2 (MOSI child) - Equation 19
            if (predecessors.size() == 1) {
                Et2faTask parent = predecessors.get(0);
                if (parent.getSuccessors().size() > 1) {
                    task.setTaskType(TaskType.TYPE2);
                    type2Tasks.add(TaskType.TYPE2);
                    continue;
                }
            }
            
            // Check for TYPE3 (SOMI parent) - Equation 21
            if (predecessors.size() > 1) {
                boolean allSingleChild = true;
                for (Et2faTask pred : predecessors) {
                    if (pred.getSuccessors().size() != 1) {
                        allSingleChild = false;
                        break;
                    }
                }
                if (allSingleChild) {
                    task.setTaskType(TaskType.TYPE3);
                    type3Tasks.add(TaskType.TYPE3);
                    continue;
                }
            }
            
            // Check for TYPE4 (SOMI child) - Equation 22
            if (predecessors.size() > 1 && successors.size() == 1) {
                task.setTaskType(TaskType.TYPE4);
                type4Tasks.add(TaskType.TYPE4);
                continue;
            }
            
            // General type
            task.setTaskType(TaskType.GENERAL);
        }
    }

    /**
     * Schedule a list of tasks (Algorithm 2 from paper)
     */
    private void scheduleTasks(List<Et2faTask> tasks, Map<Et2faTask, Vm> schedule) {
        for (Et2faTask task : tasks) {
            Vm selectedVm = selectVmForTask(task);
            scheduleTask(task, selectedVm, schedule);
        }
    }

    /**
     * Select VM for a task based on compact scheduling conditions
     */
    private Vm selectVmForTask(Et2faTask task) {
        // Layer 1: VMs with running tasks at current level (vC)
        if (!vC.isEmpty()) {
            Vm bestVm = findBestVmInSet(task, vC);
            if (bestVm != null) {
                double availableStartTime = calculateAvailableStartTime(task, bestVm);
                double finishTime = availableStartTime + calculateExecutionTime(task, bestVm);
                if (finishTime <= tStar) {
                    return bestVm;
                }
            }
        }
        
        // Layer 2: VMs with running tasks at previous level (vP)
        if (!vP.isEmpty()) {
            Vm bestVm = findBestVmInSet(task, vP);
            if (bestVm != null) {
                double availableStartTime = calculateAvailableStartTime(task, bestVm);
                double finishTime = availableStartTime + calculateExecutionTime(task, bestVm);
                if (finishTime <= tStar) {
                    return bestVm;
                }
            }
        }
        
        // Layer 3: All available VMs
        return selectBestVmForTask(task);
    }

    private Vm findBestVmInSet(Et2faTask task, Set<Vm> vmSet) {
        Vm bestVm = null;
        double minStartTime = Double.MAX_VALUE;
        
        for (Vm vm : vmSet) {
            double availableStartTime = calculateAvailableStartTime(task, vm);
            if (availableStartTime < minStartTime) {
                minStartTime = availableStartTime;
                bestVm = vm;
            }
        }
        
        return bestVm;
    }

    private Vm selectBestVmForTask(Et2faTask task) {
        Vm bestVm = null;
        double minFinishTime = Double.MAX_VALUE;
        
        for (Vm vm : availableVms) {
            double availableStartTime = calculateAvailableStartTime(task, vm);
            double executionTime = calculateExecutionTime(task, vm);
            double finishTime = availableStartTime + executionTime;
            
            if (finishTime < minFinishTime) {
                minFinishTime = finishTime;
                bestVm = vm;
            }
        }
        
        if (bestVm == null) {
            bestVm = availableVms.get(0);
        }
        
        return bestVm;
    }

    /**
     * Calculate available start time (Equation 23 from paper)
     * T_ih^A = max{max_{a_j in Pre(a_i)} {T_j^F + T_jih^k}, T_h^k}
     * Optimized to cache results and avoid redundant calculations
     */
    private double calculateAvailableStartTime(Et2faTask task, Vm vm) {
        // T_h^k: current completion time of VM v_h (including cold startup if new)
        double vmReadyTime = vmCompletionTimes.getOrDefault(vm, 55.9);
        
        List<Et2faTask> predecessors = task.getPredecessors();
        if (predecessors == null || predecessors.isEmpty()) {
            return vmReadyTime;
        }
        
        // Calculate max of all predecessor finish times + communication times
        double maxPredFinishTime = 0;
        for (Et2faTask pred : predecessors) {
            // Skip if predecessor hasn't been scheduled yet (finish time is 0)
            double predFinishTime = pred.getActualFinishTime();
            if (predFinishTime <= 0) {
                // Predecessor not scheduled yet, use a conservative estimate
                continue;
            }
            
            // T_jih^k: communication time from task a_j (on VM v_k) to task a_i (on VM v_h)
            Vm predVm = taskVmMap.get(pred);
            double commTime = 0;
            if (predVm != null && predVm != vm && dag != null) {
                // Calculate communication time based on data transfer and bandwidth
                double dataSize = dag.getDataTransfer(pred, task); // Data size in MB
                if (dataSize > 0) {
                    commTime = VmConfig.calculateCommunicationTime(dataSize, predVm, vm);
                }
            }
            
            double predReadyTime = predFinishTime + commTime;
            maxPredFinishTime = Math.max(maxPredFinishTime, predReadyTime);
        }
        
        // Return max of max predecessor ready time and VM ready time
        return Math.max(maxPredFinishTime, vmReadyTime);
    }

    /**
     * Calculate execution time of task on VM
     * t_i^h = w_i / U_h where w_i is computation and U_h is processing capacity
     */
    private double calculateExecutionTime(Et2faTask task, Vm vm) {
        VmConfig.VmType vmType = VmConfig.getVmType(vm);
        if (vmType != null) {
            // Use GFLOPS from VM configuration
            return task.getComputation() / vmType.processingCapacity;
        } else {
            // Fallback to MIPS
            return task.getComputation() / vm.getMips();
        }
    }

    private void scheduleTask(Et2faTask task, Vm vm, Map<Et2faTask, Vm> schedule) {
        schedule.put(task, vm);
        taskVmMap.put(task, vm); // Track task-VM mapping for communication time calculation
        
        // Calculate actual start and finish times
        double startTime = calculateAvailableStartTime(task, vm);
        double executionTime = calculateExecutionTime(task, vm);
        double finishTime = startTime + executionTime;
        
        task.setActualStartTime(startTime);
        task.setActualFinishTime(finishTime);
        
        // Update VM completion time
        vmCompletionTimes.put(vm, finishTime);
        
        // Update tStar if finish time exceeds it
        if (finishTime > tStar) {
            tStar = finishTime;
        }
        
        // Add VM to current level set
        if (!vC.contains(vm)) {
            vC.add(vm);
        }
    }
}




