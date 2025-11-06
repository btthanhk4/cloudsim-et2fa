package vn.et2fa.algorithm;

import org.cloudbus.cloudsim.vms.Vm;
import vn.et2fa.model.Et2faTask;

import java.util.*;

/**
 * Instance Hibernate Scheduling Heuristic (IHSH) - Phase 3 of ET2FA
 * Algorithm 4 from the paper
 * 
 * Determines when to hibernate idle instances to save cost.
 */
public class IHSHAlgorithm {
    private Map<Et2faTask, Vm> schedule;
    private Map<Vm, List<Et2faTask>> tasksByVm;
    
    // Hibernation parameters
    private static final double DUR_H = 60.0; // Shortest duration of hibernation (seconds)
    private static final double GAP_H = 120.0; // Minimum gap between two adjacent hibernations (seconds)
    private static final double DUR_W = 34.0; // Warm startup time (seconds)
    private static final double DUR_P = 5.6; // Stopping time (seconds)
    private static final double M_H = 0.005 / 3600.0; // ElasticIP cost per second ($/h -> $/s)
    
    // Hibernation schedule: VM -> List of (start time, end time) pairs
    private Map<Vm, List<HibernationPeriod>> hibernationSchedule;
    
    public static class HibernationPeriod {
        public double startTime;
        public double endTime;
        
        public HibernationPeriod(double startTime, double endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
    
    public IHSHAlgorithm(Map<Et2faTask, Vm> schedule) {
        this.schedule = schedule;
        this.tasksByVm = new HashMap<>();
        this.hibernationSchedule = new HashMap<>();
        
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
     * Main IHSH algorithm (Algorithm 4 from paper)
     */
    public Map<Vm, List<HibernationPeriod>> scheduleHibernations() {
        for (Vm vm : tasksByVm.keySet()) {
            List<Et2faTask> tasks = tasksByVm.get(vm);
            if (tasks.isEmpty()) continue;
            
            List<HibernationPeriod> hibernations = new ArrayList<>();
            double tempT = 0; // Last hibernation end time
            int j = 1;
            
            // Check gaps between consecutive tasks
            for (int k = 0; k < tasks.size() - 1; k++) {
                Et2faTask currentTask = tasks.get(k);
                Et2faTask nextTask = tasks.get(k + 1);
                
                double gap = nextTask.getActualStartTime() - currentTask.getActualFinishTime();
                double timeSinceLastHibernation = currentTask.getActualFinishTime() - tempT;
                
                // Check hibernation requirements
                if (gap > DUR_H && timeSinceLastHibernation > GAP_H) {
                    double hibernateStart = currentTask.getActualFinishTime();
                    double hibernateEnd = nextTask.getActualStartTime() - DUR_W;
                    
                    hibernations.add(new HibernationPeriod(hibernateStart, hibernateEnd));
                    tempT = nextTask.getActualStartTime();
                    j++;
                }
            }
            
            hibernationSchedule.put(vm, hibernations);
        }
        
        return hibernationSchedule;
    }

    /**
     * Calculate total cost including hibernation costs
     */
    public double calculateTotalCost(Map<Vm, VmCostInfo> vmCosts) {
        double totalCost = 0;
        
        for (Vm vm : tasksByVm.keySet()) {
            VmCostInfo costInfo = vmCosts.get(vm);
            if (costInfo == null) continue;
            
            // Running cost
            double runningCost = calculateRunningCost(vm, costInfo);
            
            // Hibernation cost
            double hibernationCost = calculateHibernationCost(vm);
            
            totalCost += runningCost + hibernationCost;
        }
        
        return totalCost;
    }

    /**
     * Calculate running cost for a VM (Equation 6)
     */
    private double calculateRunningCost(Vm vm, VmCostInfo costInfo) {
        List<HibernationPeriod> hibernations = hibernationSchedule.getOrDefault(vm, new ArrayList<>());
        List<Et2faTask> tasks = tasksByVm.get(vm);
        
        if (tasks.isEmpty()) return 0;
        
        Et2faTask firstTask = tasks.get(0);
        Et2faTask lastTask = tasks.get(tasks.size() - 1);
        
        double leaseStart = firstTask.getActualStartTime() - 55.9; // Subtract cold startup
        double leaseEnd = lastTask.getActualFinishTime();
        
        if (hibernations.isEmpty()) {
            return costInfo.pricePerSecond * billingTime(leaseStart, leaseEnd);
        }
        
        // Calculate running periods around hibernations
        double runningCost = 0;
        double currentStart = leaseStart;
        
        for (HibernationPeriod hibernation : hibernations) {
            // Running period before hibernation
            runningCost += costInfo.pricePerSecond * 
                billingTime(currentStart, hibernation.startTime);
            
            // Update for next period
            currentStart = hibernation.endTime + DUR_W;
        }
        
        // Running period after last hibernation
        runningCost += costInfo.pricePerSecond * billingTime(currentStart, leaseEnd);
        
        return runningCost;
    }

    /**
     * Calculate hibernation cost for a VM (Equation 7)
     */
    private double calculateHibernationCost(Vm vm) {
        List<HibernationPeriod> hibernations = hibernationSchedule.getOrDefault(vm, new ArrayList<>());
        double totalCost = 0;
        
        for (HibernationPeriod hibernation : hibernations) {
            double duration = billingTime(hibernation.startTime, hibernation.endTime);
            totalCost += M_H * duration;
        }
        
        return totalCost;
    }

    /**
     * Calculate billing time with minimum 60 seconds (Equation 1)
     */
    private double billingTime(double start, double end) {
        double duration = Math.max(end - start, 60.0);
        return Math.ceil(duration);
    }

    /**
     * Calculate total idle rate (Equation 9)
     */
    public double calculateTotalIdleRate(Map<Vm, VmCostInfo> vmCosts) {
        double totalIdleRate = 0;
        
        for (Vm vm : tasksByVm.keySet()) {
            VmCostInfo costInfo = vmCosts.get(vm);
            if (costInfo == null) continue;
            
            List<Et2faTask> tasks = tasksByVm.get(vm);
            if (tasks.isEmpty()) continue;
            
            Et2faTask firstTask = tasks.get(0);
            Et2faTask lastTask = tasks.get(tasks.size() - 1);
            
            double leaseStart = firstTask.getActualStartTime() - 55.9;
            double leaseEnd = lastTask.getActualFinishTime();
            double leaseDuration = leaseEnd - leaseStart;
            
            if (leaseDuration <= 0) continue;
            
            // Calculate total execution time
            double totalExecutionTime = tasks.stream()
                .mapToDouble(t -> t.getActualFinishTime() - t.getActualStartTime())
                .sum();
            
            double idleRate = 1 - (totalExecutionTime / leaseDuration);
            totalIdleRate += idleRate;
        }
        
        return totalIdleRate;
    }

    /**
     * VM cost information structure
     */
    public static class VmCostInfo {
        public double pricePerSecond; // Price per second in $
        public double processingCapacity; // In GFLOPS
        
        public VmCostInfo(double pricePerSecond, double processingCapacity) {
            this.pricePerSecond = pricePerSecond;
            this.processingCapacity = processingCapacity;
        }
    }
}

