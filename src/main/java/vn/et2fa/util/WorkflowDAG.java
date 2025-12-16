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
     * OPTIMIZED: Uses BFS-based approach for better performance O(n+m) instead of O(n²)
     * More efficient than iterative approach for large DAGs
     */
    public void calculateTopologicalLevels() {
        calculateTopologicalLevelsBFS();
    }
    
    /**
     * Original O(n²) iterative approach for topological levels
     */
    public void calculateTopologicalLevelsOriginal() {
        Map<Et2faTask, Integer> levels = new HashMap<>();
        boolean changed = true;
        int maxIterations = tasks.size() * 2; // Prevent infinite loops
        int iteration = 0;
        
        // Initialize all tasks to level -1
        for (Et2faTask task : tasks) {
            levels.put(task, -1);
            task.setTopologicalLevel(-1);
        }
        
        // Iterative approach: assign level 0 to entry tasks, then propagate
        while (changed && iteration < maxIterations) {
            changed = false;
            iteration++;
            
            for (Et2faTask task : tasks) {
                if (levels.get(task) >= 0) continue; // Already assigned
                
                List<Et2faTask> predecessors = task.getPredecessors();
                if (predecessors == null || predecessors.isEmpty()) {
                    // Entry task
                    levels.put(task, 0);
                    task.setTopologicalLevel(0);
                    changed = true;
                } else {
                    // Check if all predecessors have levels assigned
                    boolean allPredecessorsAssigned = true;
                    int maxPredLevel = -1;
                    for (Et2faTask pred : predecessors) {
                        int predLevel = levels.getOrDefault(pred, -1);
                        if (predLevel < 0) {
                            allPredecessorsAssigned = false;
                            break;
                        }
                        maxPredLevel = Math.max(maxPredLevel, predLevel);
                    }
                    
                    if (allPredecessorsAssigned) {
                        levels.put(task, maxPredLevel + 1);
                        task.setTopologicalLevel(maxPredLevel + 1);
                        changed = true;
                    }
                }
            }
        }
        
        // Assign level 0 to any remaining unassigned tasks
        for (Et2faTask task : tasks) {
            if (levels.get(task) == null || levels.get(task) < 0) {
                levels.put(task, 0);
                task.setTopologicalLevel(0);
            }
        }
        
        int maxLevel = levels.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        System.out.println("Topological Levels (Original O(n²)): max level=" + maxLevel);
    }
    
    /**
     * BFS-optimized approach O(n+m)
     */
    private void calculateTopologicalLevelsBFS() {
        Map<Et2faTask, Integer> levels = new HashMap<>();
        Map<Et2faTask, Integer> inDegree = new HashMap<>();
        java.util.Queue<Et2faTask> queue = new java.util.LinkedList<>();
        
        // Initialize all tasks
        for (Et2faTask task : tasks) {
            levels.put(task, -1);
            task.setTopologicalLevel(-1);
            inDegree.put(task, task.getPredecessors() != null ? task.getPredecessors().size() : 0);
        }
        
        // Find all entry tasks (in-degree = 0) and add to queue with level 0
        int level0Count = 0;
        for (Et2faTask task : tasks) {
            if (inDegree.get(task) == 0) {
                levels.put(task, 0);
                task.setTopologicalLevel(0);
                queue.offer(task);
                level0Count++;
            }
        }
        
        // BFS: Process tasks level by level
        while (!queue.isEmpty()) {
            Et2faTask current = queue.poll();
            int currentLevel = levels.get(current);
            
            // Process all successors
            List<Et2faTask> successors = getSuccessors(current);
            for (Et2faTask succ : successors) {
                // Decrease in-degree
                int newInDegree = inDegree.get(succ) - 1;
                inDegree.put(succ, newInDegree);
                
                // If all predecessors processed, assign level and add to queue
                if (newInDegree == 0 && levels.get(succ) < 0) {
                    int newLevel = currentLevel + 1;
                    levels.put(succ, newLevel);
                    succ.setTopologicalLevel(newLevel);
                    queue.offer(succ);
                } else if (levels.get(succ) < 0) {
                    // Update level if not set yet (for parallel paths)
                    int newLevel = Math.max(levels.getOrDefault(succ, -1), currentLevel + 1);
                    if (newLevel > levels.getOrDefault(succ, -1)) {
                        levels.put(succ, newLevel);
                        succ.setTopologicalLevel(newLevel);
                    }
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
        System.out.println("Topological Levels (BFS-optimized): " + level0Count + " entry tasks, max level=" + maxLevel + 
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
        
        // For large workflows (1000+ tasks), skip simplifyDAG to prevent hanging
        if (initialSize >= 500) {
            System.out.println("DAG: Skipping simplifyDAG for large workflow (" + initialSize + " tasks)");
            return;
        }
        
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





