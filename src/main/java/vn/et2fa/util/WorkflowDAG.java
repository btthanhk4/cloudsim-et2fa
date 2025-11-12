package vn.et2fa.util;

import vn.et2fa.model.Et2faTask;

import java.util.*;

/**
 * Represents a Directed Acyclic Graph (DAG) for workflow scheduling.
 * Based on the ET2FA paper model.
 */
public class WorkflowDAG {
    private List<Et2faTask> tasks;
    private Map<Et2faTask, List<Et2faTask>> edges; // Dependencies: task -> successors
    private Map<String, Double> dataTransfers; // Data transfer sizes between tasks (in GFLOP)
    
    public WorkflowDAG() {
        this.tasks = new ArrayList<>();
        this.edges = new HashMap<>();
        this.dataTransfers = new HashMap<>();
    }

    public void addTask(Et2faTask task) {
        // Check by ID instead of object reference to avoid duplicates
        boolean exists = tasks.stream().anyMatch(t -> t.getId() == task.getId());
        if (!exists) {
            tasks.add(task);
            edges.put(task, new ArrayList<>());
        }
    }

    public void addDependency(Et2faTask from, Et2faTask to, double dataSize) {
        addTask(from);
        addTask(to);
        
        if (!edges.get(from).contains(to)) {
            edges.get(from).add(to);
            from.addSuccessor(to);
            to.addPredecessor(from);
        }
        
        String key = from.getId() + "_" + to.getId();
        dataTransfers.put(key, dataSize);
    }

    public List<Et2faTask> getTasks() {
        return tasks;
    }

    public List<Et2faTask> getSuccessors(Et2faTask task) {
        return edges.getOrDefault(task, new ArrayList<>());
    }

    public double getDataTransfer(Et2faTask from, Et2faTask to) {
        String key = from.getId() + "_" + to.getId();
        return dataTransfers.getOrDefault(key, 0.0);
    }

    /**
     * Calculate topological levels for all tasks (Equation 13 in paper)
     * Uses iterative approach: assign levels based on predecessor levels
     * More efficient and safer than BFS for large DAGs
     */
    public void calculateTopologicalLevels() {
        Map<Et2faTask, Integer> levels = new HashMap<>();
        
        // Initialize all tasks to level -1
        for (Et2faTask task : tasks) {
            levels.put(task, -1);
            task.setTopologicalLevel(-1);
        }
        
        // First pass: assign level 0 to tasks with no predecessors
        int level0Count = 0;
        for (Et2faTask task : tasks) {
            List<Et2faTask> preds = task.getPredecessors();
            if (preds == null || preds.isEmpty()) {
                levels.put(task, 0);
                task.setTopologicalLevel(0);
                level0Count++;
            }
        }
        
        // Iterative passes: assign levels based on predecessor levels
        // Maximum number of iterations = maximum depth of DAG (safety limit)
        int maxIterations = tasks.size();
        boolean changed = true;
        int iteration = 0;
        
        while (changed && iteration < maxIterations) {
            changed = false;
            iteration++;
            
            for (Et2faTask task : tasks) {
                // Skip if already assigned
                if (levels.get(task) >= 0) {
                    continue;
                }
                
                List<Et2faTask> preds = task.getPredecessors();
                if (preds == null || preds.isEmpty()) {
                    // No predecessors but not yet assigned - assign level 0
                    levels.put(task, 0);
                    task.setTopologicalLevel(0);
                    changed = true;
                    level0Count++;
                    continue;
                }
                
                // Check if all predecessors have been assigned levels
                boolean allPredsAssigned = true;
                int maxPredLevel = -1;
                for (Et2faTask pred : preds) {
                    Integer predLevel = levels.get(pred);
                    if (predLevel == null || predLevel < 0) {
                        allPredsAssigned = false;
                        break;
                    }
                    maxPredLevel = Math.max(maxPredLevel, predLevel);
                }
                
                // If all predecessors are assigned, assign level = max(pred levels) + 1
                if (allPredsAssigned && maxPredLevel >= 0) {
                    int newLevel = maxPredLevel + 1;
                    levels.put(task, newLevel);
                    task.setTopologicalLevel(newLevel);
                    changed = true;
                }
            }
        }
        
        // Final pass: assign level 0 to any remaining unassigned tasks (orphaned or cyclic)
        int unassignedCount = 0;
        for (Et2faTask task : tasks) {
            if (levels.get(task) == null || levels.get(task) < 0) {
                levels.put(task, 0);
                task.setTopologicalLevel(0);
                unassignedCount++;
            }
        }
        
        // Calculate max level
        int maxLevel = levels.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        System.out.println("Topological Levels: " + level0Count + " entry tasks, max level=" + maxLevel + 
                          (unassignedCount > 0 ? ", " + unassignedCount + " unassigned tasks set to level 0" : ""));
    }

    /**
     * Get tasks grouped by topological level
     */
    public Map<Integer, List<Et2faTask>> getTasksByLevel() {
        Map<Integer, List<Et2faTask>> tasksByLevel = new HashMap<>();
        for (Et2faTask task : tasks) {
            int level = task.getTopologicalLevel();
            tasksByLevel.computeIfAbsent(level, k -> new ArrayList<>()).add(task);
        }
        return tasksByLevel;
    }

    /**
     * Simplify DAG by merging SOSI (Single Output Single Input) structures
     * Equation 16 in paper: |Suc(a_i)| = 1 AND |Pre(a_j)| = 1
     * Tasks in SOSI structure can be merged into a task block
     * Optimized with better iteration limits and merge limits
     */
    public void simplifyDAG() {
        int initialSize = tasks.size();
        
        // Limit merging: don't merge more than 30% of tasks to preserve workflow structure
        int maxMerges = Math.max(1, (int)(initialSize * 0.3));
        int totalMerged = 0;
        boolean changed = true;
        int maxIterations = Math.min(50, tasks.size()); // Limit iterations based on task count
        int iterations = 0;
        
        while (changed && iterations < maxIterations && totalMerged < maxMerges) {
            changed = false;
            iterations++;
            List<Et2faTask> toRemove = new ArrayList<>();
            Set<Et2faTask> processed = new HashSet<>();
            
            // Create a copy of tasks to iterate over (to avoid concurrent modification)
            List<Et2faTask> tasksToProcess = new ArrayList<>(tasks);
            
            for (Et2faTask task : tasksToProcess) {
                if (processed.contains(task) || toRemove.contains(task)) {
                    continue;
                }
                
                // Check if we've reached merge limit
                if (totalMerged >= maxMerges) {
                    break;
                }
                
                List<Et2faTask> successors = getSuccessors(task);
                // Check SOSI condition: task has exactly one successor
                if (successors.size() == 1) {
                    Et2faTask successor = successors.get(0);
                    List<Et2faTask> preds = successor.getPredecessors();
                    // Check if successor has exactly one predecessor (which should be this task)
                    if (preds != null && preds.size() == 1 && 
                        preds.contains(task) &&
                        !processed.contains(successor) &&
                        !toRemove.contains(successor)) {
                        // SOSI structure found - merge tasks
                        mergeTasks(task, successor);
                        toRemove.add(successor);
                        processed.add(task);
                        processed.add(successor);
                        changed = true;
                        totalMerged++;
                        // Only merge one pair per iteration to avoid complexity
                        break;
                    }
                }
            }
            
            // Remove merged tasks
            if (!toRemove.isEmpty()) {
                tasks.removeAll(toRemove);
                for (Et2faTask task : toRemove) {
                    edges.remove(task);
                    // Remove data transfers involving this task
                    dataTransfers.entrySet().removeIf(entry -> {
                        String key = entry.getKey();
                        String taskIdStr = String.valueOf(task.getId());
                        return key.contains("_" + taskIdStr) || key.contains(taskIdStr + "_");
                    });
                }
            }
        }
        
        int finalSize = tasks.size();
        if (initialSize != finalSize) {
            System.out.println("DAG Simplify: Merged " + totalMerged + " tasks (limit: " + maxMerges + "). " + initialSize + " -> " + finalSize + " tasks");
        }
    }

    private void mergeTasks(Et2faTask from, Et2faTask to) {
        // Merge computation (sum of computations)
        from.setComputation(from.getComputation() + to.getComputation());
        from.setLength(from.getLength() + to.getLength());
        
        // Get successors of 'to' task
        List<Et2faTask> toSuccessors = new ArrayList<>(edges.getOrDefault(to, new ArrayList<>()));
        
        // Update edges: from now points to to's successors
        edges.get(from).clear();
        edges.get(from).addAll(toSuccessors);
        
        // Update predecessor/successor relationships
        from.getSuccessors().clear();
        from.getSuccessors().addAll(to.getSuccessors());
        
        // Update successors to point to 'from' instead of 'to'
        for (Et2faTask succ : to.getSuccessors()) {
            if (succ != null) {
                succ.getPredecessors().remove(to);
                if (!succ.getPredecessors().contains(from)) {
                    succ.getPredecessors().add(from);
                }
            }
        }
        
        // Merge data transfers: add data transfers from 'to' to its successors
        // Data transfer between from and to is removed (internal to merged task)
        String mergeKey = from.getId() + "_" + to.getId();
        dataTransfers.remove(mergeKey);
        
        // Update data transfer keys for to's successors
        for (Et2faTask succ : toSuccessors) {
            String oldKey = to.getId() + "_" + succ.getId();
            String newKey = from.getId() + "_" + succ.getId();
            Double dataSize = dataTransfers.get(oldKey);
            if (dataSize != null) {
                dataTransfers.put(newKey, dataSize);
                dataTransfers.remove(oldKey);
            }
        }
    }
}





