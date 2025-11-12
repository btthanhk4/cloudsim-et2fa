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
            double tempT = 0; // Last hibernation end time (tempT in Algorithm 4)
            int j = 1;
            
            // Check gaps between consecutive tasks (Algorithm 4 lines 3-11)
            for (int k = 0; k < tasks.size() - 1; k++) {
                Et2faTask p = tasks.get(k); // Current task (p in Algorithm 4)
                Et2faTask s = tasks.get(k + 1); // Next task (s in Algorithm 4)
                
                double gap = s.getActualStartTime() - p.getActualFinishTime();
                double timeSinceLastHibernation = p.getActualFinishTime() - tempT;
                
                // Check hibernation requirements (Algorithm 4 line 5):
                // gap > Dur^H AND timeSinceLastHibernation > Gap^H
                if (gap > DUR_H && timeSinceLastHibernation > GAP_H) {
                    // Set hibernation start and end times
                    double hibernateStart = p.getActualFinishTime(); // t_{h,j}^S
                    double hibernateEnd = s.getActualStartTime(); // t_{h,j}^E
                    
                    // Hibernate start event: t_{h,j}^{HS} = t_p^F - Dur^W
                    // Hibernate end event: t_{h,j}^{HE} = t_s^S
                    // Note: The hibernation period itself is from start to end
                    hibernations.add(new HibernationPeriod(hibernateStart, hibernateEnd));
                    tempT = s.getActualStartTime(); // Update tempT
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
     * RC_h = v_h^M * (g(T_h^S, T_h^{HS_1}-1) + g(T_h^{HE_{M^H}}, T_h^E) + 
     *        sum_{k=1}^{M^H} g(T_h^{HS_k}, T_h^{HE_k}))
     */
    private double calculateRunningCost(Vm vm, VmCostInfo costInfo) {
        List<HibernationPeriod> hibernations = hibernationSchedule.getOrDefault(vm, new ArrayList<>());
        List<Et2faTask> tasks = tasksByVm.get(vm);
        
        if (tasks.isEmpty()) return 0;
        
        Et2faTask firstTask = tasks.get(0);
        Et2faTask lastTask = tasks.get(tasks.size() - 1);
        
        // Lease start time: T_h^S (including cold startup)
        double leaseStart = Math.max(0, firstTask.getActualStartTime() - 55.9); // Dur^C = 55.9s
        // Lease end time: T_h^E
        double leaseEnd = lastTask.getActualFinishTime();
        
        if (hibernations.isEmpty()) {
            // No hibernations: RC_h = v_h^M * g(T_h^S, T_h^E)
            return costInfo.pricePerSecond * billingTime(leaseStart, leaseEnd);
        }
        
        // Calculate running periods around hibernations (Equation 6)
        double runningCost = 0;
        double currentStart = leaseStart;
        
        for (HibernationPeriod hibernation : hibernations) {
            // Running period before hibernation: g(T_h^S, T_h^{HS_k}-1)
            // Hibernation start event: T_h^{HS_k} = T_h^S - Dur^W (but we use actual start)
            double runningEnd = hibernation.startTime;
            if (runningEnd > currentStart) {
                runningCost += costInfo.pricePerSecond * billingTime(currentStart, runningEnd);
            }
            
            // Next running period starts after hibernation ends (with warm startup)
            // T_h^{HE_k} + Dur^W
            currentStart = hibernation.endTime + DUR_W;
        }
        
        // Running period after last hibernation: g(T_h^{HE_{M^H}}, T_h^E)
        if (leaseEnd > currentStart) {
            runningCost += costInfo.pricePerSecond * billingTime(currentStart, leaseEnd);
        }
        
        return runningCost;
    }

    /**
     * Calculate hibernation cost for a VM (Equation 7)
     * HC_h = v_h^M * sum_{k=1}^{M^H} g(T_h^{HS_k}, T_h^{HE_k})
     * where v_h^M = M^H (ElasticIP cost per hour) = 0.005 $/h
     */
    private double calculateHibernationCost(Vm vm) {
        List<HibernationPeriod> hibernations = hibernationSchedule.getOrDefault(vm, new ArrayList<>());
        
        if (hibernations.isEmpty()) {
            return 0;
        }
        
        double totalCost = 0;
        for (HibernationPeriod hibernation : hibernations) {
            // Calculate billed time for hibernation period
            double duration = billingTime(hibernation.startTime, hibernation.endTime);
            totalCost += M_H * duration;
        }
        
        return totalCost;
    }

    /**
     * Calculate billing time with minimum 60 seconds (Equation 1)
     * g(t1, t2) = ceil(max{(t2 - t1), 60})
     * Per-second billing with minimum 60 seconds
     */
    private double billingTime(double start, double end) {
        double duration = end - start;
        // Minimum billing period is 60 seconds
        double billedDuration = Math.max(duration, 60.0);
        // Ceiling to nearest second (per-second billing)
        return Math.ceil(billedDuration);
    }

    /**
     * Calculate total idle rate (Equation 9)
     * total idle rate = sum_{h=1}^{|V|} (1 - (sum_{i=1}^{N_h} t_i^E / (T_h^E - T_h^S)))
     */
    public double calculateTotalIdleRate(Map<Vm, VmCostInfo> vmCosts) {
        double totalIdleRate = 0;
        
        for (Vm vm : tasksByVm.keySet()) {
            List<Et2faTask> tasks = tasksByVm.get(vm);
            if (tasks.isEmpty()) continue;
            
            Et2faTask firstTask = tasks.get(0);
            Et2faTask lastTask = tasks.get(tasks.size() - 1);
            
            // Lease start time: T_h^S (including cold startup)
            double leaseStart = Math.max(0, firstTask.getActualStartTime() - 55.9);
            // Lease end time: T_h^E
            double leaseEnd = lastTask.getActualFinishTime();
            double leaseDuration = leaseEnd - leaseStart;
            
            if (leaseDuration <= 0) continue;
            
            // Calculate total execution time: sum_{i=1}^{N_h} t_i^E
            // where t_i^E is execution time of task i
            double totalExecutionTime = tasks.stream()
                .mapToDouble(t -> t.getActualFinishTime() - t.getActualStartTime())
                .sum();
            
            // Idle rate for this VM: 1 - (totalExecutionTime / leaseDuration)
            // Ensure idle rate is between 0 and 1
            double utilization = totalExecutionTime / leaseDuration;
            double idleRate = Math.max(0, Math.min(1, 1 - utilization));
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

