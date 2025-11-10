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
        if (!tasks.contains(task)) {
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
     */
    public void calculateTopologicalLevels() {
        Map<Et2faTask, Integer> levels = new HashMap<>();
        
        // Initialize all tasks
        for (Et2faTask task : tasks) {
            levels.put(task, -1);
        }
        
        // Calculate levels using BFS
        Queue<Et2faTask> queue = new LinkedList<>();
        for (Et2faTask task : tasks) {
            if (task.getPredecessors().isEmpty()) {
                levels.put(task, 0);
                task.setTopologicalLevel(0);
                queue.offer(task);
            }
        }
        
        while (!queue.isEmpty()) {
            Et2faTask current = queue.poll();
            int currentLevel = levels.get(current);
            
            for (Et2faTask successor : getSuccessors(current)) {
                int maxPredLevel = successor.getPredecessors().stream()
                    .mapToInt(p -> levels.get(p))
                    .max()
                    .orElse(-1);
                
                int newLevel = maxPredLevel + 1;
                if (newLevel > levels.get(successor)) {
                    levels.put(successor, newLevel);
                    successor.setTopologicalLevel(newLevel);
                    queue.offer(successor);
                }
            }
        }
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
     * Equation 16 in paper
     */
    public void simplifyDAG() {
        boolean changed = true;
        while (changed) {
            changed = false;
            List<Et2faTask> toRemove = new ArrayList<>();
            
            for (Et2faTask task : tasks) {
                List<Et2faTask> successors = getSuccessors(task);
                if (successors.size() == 1) {
                    Et2faTask successor = successors.get(0);
                    if (successor.getPredecessors().size() == 1) {
                        // SOSI structure found - merge tasks
                        mergeTasks(task, successor);
                        toRemove.add(successor);
                        changed = true;
                        break; // Process one at a time
                    }
                }
            }
            
            tasks.removeAll(toRemove);
            for (Et2faTask task : toRemove) {
                edges.remove(task);
            }
        }
    }

    private void mergeTasks(Et2faTask from, Et2faTask to) {
        // Merge computation
        from.setComputation(from.getComputation() + to.getComputation());
        from.setLength(from.getLength() + to.getLength());
        
        // Update edges
        List<Et2faTask> toSuccessors = edges.get(to);
        edges.get(from).clear();
        edges.get(from).addAll(toSuccessors);
        
        // Update predecessor/successor relationships
        from.getSuccessors().clear();
        from.getSuccessors().addAll(to.getSuccessors());
        
        for (Et2faTask succ : to.getSuccessors()) {
            succ.getPredecessors().remove(to);
            if (!succ.getPredecessors().contains(from)) {
                succ.getPredecessors().add(from);
            }
        }
        
        // Remove data transfer between merged tasks
        String key = from.getId() + "_" + to.getId();
        dataTransfers.remove(key);
    }
}




