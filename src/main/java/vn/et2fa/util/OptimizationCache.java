package vn.et2fa.util;

import org.cloudbus.cloudsim.vms.Vm;
import vn.et2fa.model.Et2faTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for optimization to avoid redundant calculations.
 * This class provides caching mechanisms to improve ET2FA performance.
 */
public class OptimizationCache {
    // Cache for communication time: (task1_id, task2_id, vm1_id, vm2_id) -> communication time
    private final Map<String, Double> communicationTimeCache;
    
    // Cache for VM processing capacity: vm_id -> processing capacity
    private final Map<Long, Double> vmCapacityCache;
    
    // Cache for execution time: (task_id, vm_id) -> execution time
    private final Map<String, Double> executionTimeCache;
    
    // Cache for fastest VM (lazy initialization)
    private Vm fastestVm = null;
    private Double fastestVmCapacity = null;
    
    public OptimizationCache() {
        this.communicationTimeCache = new HashMap<>();
        this.vmCapacityCache = new HashMap<>();
        this.executionTimeCache = new HashMap<>();
    }
    
    /**
     * Generate cache key for communication time
     */
    private String getCommTimeKey(Et2faTask task1, Et2faTask task2, Vm vm1, Vm vm2) {
        // Use task IDs and VM IDs to create unique key
        // Order doesn't matter for communication, so normalize order
        long taskId1 = task1.getId();
        long taskId2 = task2.getId();
        long vmId1 = vm1.getId();
        long vmId2 = vm2.getId();
        
        // Normalize: smaller ID first
        if (taskId1 > taskId2) {
            long temp = taskId1;
            taskId1 = taskId2;
            taskId2 = temp;
        }
        if (vmId1 > vmId2) {
            long temp = vmId1;
            vmId1 = vmId2;
            vmId2 = temp;
        }
        
        return String.format("comm_%d_%d_%d_%d", taskId1, taskId2, vmId1, vmId2);
    }
    
    /**
     * Get cached communication time or calculate and cache it
     */
    public double getCommunicationTime(Et2faTask task1, Et2faTask task2, Vm vm1, Vm vm2, 
                                       WorkflowDAG dag) {
        if (vm1 == vm2) {
            return 0.0; // Same VM, no communication
        }
        
        String key = getCommTimeKey(task1, task2, vm1, vm2);
        Double cached = communicationTimeCache.get(key);
        
        if (cached != null) {
            return cached;
        }
        
        // Calculate and cache
        double dataSize = dag.getDataTransfer(task1, task2);
        double commTime = VmConfig.calculateCommunicationTime(dataSize, vm1, vm2);
        communicationTimeCache.put(key, commTime);
        
        return commTime;
    }
    
    /**
     * Get cached VM processing capacity or calculate and cache it
     */
    public double getVmCapacity(Vm vm) {
        Long vmId = vm.getId();
        Double cached = vmCapacityCache.get(vmId);
        
        if (cached != null) {
            return cached;
        }
        
        // Calculate and cache
        VmConfig.VmType vmType = VmConfig.getVmType(vm);
        double capacity = vmType != null ? vmType.processingCapacity : vm.getMips();
        vmCapacityCache.put(vmId, capacity);
        
        return capacity;
    }
    
    /**
     * Get cached execution time or calculate and cache it
     */
    public double getExecutionTime(Et2faTask task, Vm vm) {
        String key = String.format("exec_%d_%d", task.getId(), vm.getId());
        Double cached = executionTimeCache.get(key);
        
        if (cached != null) {
            return cached;
        }
        
        // Calculate and cache
        double capacity = getVmCapacity(vm);
        double execTime = task.getComputation() / capacity;
        executionTimeCache.put(key, execTime);
        
        return execTime;
    }
    
    /**
     * Get fastest VM (cached)
     */
    public Vm getFastestVm(java.util.List<Vm> vms) {
        if (fastestVm != null) {
            return fastestVm;
        }
        
        // Find fastest VM
        fastestVm = vms.stream()
            .max(java.util.Comparator.comparingDouble(this::getVmCapacity))
            .orElse(vms.get(0));
        
        fastestVmCapacity = getVmCapacity(fastestVm);
        
        return fastestVm;
    }
    
    /**
     * Get fastest VM capacity (cached)
     */
    public double getFastestVmCapacity(java.util.List<Vm> vms) {
        if (fastestVmCapacity != null) {
            return fastestVmCapacity;
        }
        
        getFastestVm(vms);
        return fastestVmCapacity;
    }
    
    /**
     * Clear all caches (useful for testing or reset)
     */
    public void clear() {
        communicationTimeCache.clear();
        vmCapacityCache.clear();
        executionTimeCache.clear();
        fastestVm = null;
        fastestVmCapacity = null;
    }
    
    /**
     * Get cache statistics (for debugging/monitoring)
     */
    public CacheStats getStats() {
        return new CacheStats(
            communicationTimeCache.size(),
            vmCapacityCache.size(),
            executionTimeCache.size()
        );
    }
    
    public static class CacheStats {
        public final int commTimeCacheSize;
        public final int vmCapacityCacheSize;
        public final int executionTimeCacheSize;
        
        public CacheStats(int commTimeCacheSize, int vmCapacityCacheSize, int executionTimeCacheSize) {
            this.commTimeCacheSize = commTimeCacheSize;
            this.vmCapacityCacheSize = vmCapacityCacheSize;
            this.executionTimeCacheSize = executionTimeCacheSize;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{commTime=%d, vmCapacity=%d, execTime=%d}",
                commTimeCacheSize, vmCapacityCacheSize, executionTimeCacheSize);
        }
    }
}

