package vn.et2fa.algorithm;

import org.cloudbus.cloudsim.vms.Vm;
import vn.et2fa.model.Et2faTask;
import vn.et2fa.model.TaskType;
import vn.et2fa.util.WorkflowDAG;

import java.util.*;

/**
 * Task Type First Algorithm (T2FA) - Phase 1 of ET2FA
 * Algorithm 1 and 2 from the paper
 */
public class T2FAAlgorithm {
    private WorkflowDAG dag;
    private List<Vm> availableVms;
    private Map<Vm, Double> vmCompletionTimes;
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
        this.type0Tasks = new HashSet<>();
        this.type1Tasks = new HashSet<>();
        this.type2Tasks = new HashSet<>();
        this.type3Tasks = new HashSet<>();
        this.type4Tasks = new HashSet<>();
        this.vC = new HashSet<>();
        this.vP = new HashSet<>();
        
        // Initialize VM completion times
        for (Vm vm : availableVms) {
            vmCompletionTimes.put(vm, 55.9); // Cold startup time (DurC)
        }
    }

    /**
     * Main T2FA algorithm (Algorithm 1 from paper)
     */
    public Map<Et2faTask, Vm> schedule() {
        // Pre-processing
        dag.simplifyDAG();
        dag.calculateTopologicalLevels();
        tasksByLevel = dag.getTasksByLevel();
        
        classifyTaskTypes();
        
        // Find VM with highest processing capacity
        Vm maxVm = availableVms.stream()
            .max(Comparator.comparingDouble(Vm::getMips))
            .orElse(availableVms.get(0));
        
        // Initialize tStar
        List<Et2faTask> level0Tasks = tasksByLevel.getOrDefault(0, new ArrayList<>());
        if (!level0Tasks.isEmpty()) {
            Et2faTask firstTask = level0Tasks.get(0);
            double maxComputation = level0Tasks.stream()
                .mapToDouble(Et2faTask::getComputation)
                .max()
                .orElse(0);
            tStar = maxComputation / maxVm.getMips();
            vC.add(maxVm);
        } else {
            tStar = 0;
        }
        
        Map<Et2faTask, Vm> schedule = new HashMap<>();
        int maxLevel = tasksByLevel.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        
        // Schedule by topological level
        for (int level = 0; level <= maxLevel; level++) {
            List<Et2faTask> levelTasks = tasksByLevel.getOrDefault(level, new ArrayList<>());
            
            // Handle TYPE0 tasks (single task in level)
            if (levelTasks.size() == 1) {
                Et2faTask task = levelTasks.get(0);
                double maxFinishTime = dag.getTasks().stream()
                    .mapToDouble(Et2faTask::getEstimatedFinishTime)
                    .max()
                    .orElse(0);
                
                if (task.getComputation() > 0.1 * maxFinishTime) {
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
     */
    private void classifyTaskTypes() {
        for (Et2faTask task : dag.getTasks()) {
            List<Et2faTask> successors = task.getSuccessors();
            List<Et2faTask> predecessors = task.getPredecessors();
            
            // Check for TYPE0 (single task in level)
            List<Et2faTask> levelTasks = tasksByLevel.getOrDefault(task.getTopologicalLevel(), new ArrayList<>());
            if (levelTasks.size() == 1) {
                type0Tasks.add(task.getType());
                task.setTaskType(TaskType.TYPE0);
                continue;
            }
            
            // Check for TYPE1 (MOSI parent)
            if (successors.size() > 1) {
                boolean allSingleParent = true;
                for (Et2faTask succ : successors) {
                    if (succ.getPredecessors().size() != 1) {
                        allSingleParent = false;
                        break;
                    }
                }
                if (allSingleParent) {
                    type1Tasks.add(task.getType());
                    task.setTaskType(TaskType.TYPE1);
                    continue;
                }
            }
            
            // Check for TYPE2 (MOSI child)
            if (predecessors.size() == 1 && predecessors.get(0).getSuccessors().size() > 1) {
                type2Tasks.add(task.getType());
                task.setTaskType(TaskType.TYPE2);
                continue;
            }
            
            // Check for TYPE3 (SOMI parent)
            if (predecessors.size() > 1) {
                boolean allSingleChild = true;
                for (Et2faTask pred : predecessors) {
                    if (pred.getSuccessors().size() != 1) {
                        allSingleChild = false;
                        break;
                    }
                }
                if (allSingleChild) {
                    type3Tasks.add(task.getType());
                    task.setTaskType(TaskType.TYPE3);
                    continue;
                }
            }
            
            // Check for TYPE4 (SOMI child)
            if (successors.size() == 1 && successors.get(0).getPredecessors().size() > 1) {
                type4Tasks.add(task.getType());
                task.setTaskType(TaskType.TYPE4);
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
     */
    private double calculateAvailableStartTime(Et2faTask task, Vm vm) {
        double vmReadyTime = vmCompletionTimes.getOrDefault(vm, 55.9);
        
        if (task.getPredecessors().isEmpty()) {
            return vmReadyTime;
        }
        
        double maxPredFinishTime = 0;
        for (Et2faTask pred : task.getPredecessors()) {
            double predFinishTime = pred.getActualFinishTime();
            // Add communication time if on different VM
            double commTime = 0; // Simplified - would need actual VM assignments
            maxPredFinishTime = Math.max(maxPredFinishTime, predFinishTime + commTime);
        }
        
        return Math.max(maxPredFinishTime, vmReadyTime);
    }

    private double calculateExecutionTime(Et2faTask task, Vm vm) {
        return task.getComputation() / vm.getMips();
    }

    private void scheduleTask(Et2faTask task, Vm vm, Map<Et2faTask, Vm> schedule) {
        schedule.put(task, vm);
        
        double startTime = calculateAvailableStartTime(task, vm);
        double executionTime = calculateExecutionTime(task, vm);
        double finishTime = startTime + executionTime;
        
        task.setActualStartTime(startTime);
        task.setActualFinishTime(finishTime);
        
        vmCompletionTimes.put(vm, finishTime);
        
        if (finishTime > tStar) {
            tStar = finishTime;
        }
        
        if (!vC.contains(vm)) {
            vC.add(vm);
        }
    }
}




