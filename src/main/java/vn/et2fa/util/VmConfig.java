package vn.et2fa.util;

import org.cloudbus.cloudsim.vms.Vm;

import java.util.HashMap;
import java.util.Map;

/**
 * VM Configuration utility based on Table 4 from the ET2FA paper.
 * Stores VM type configurations including processing capacity, cost, and bandwidth.
 */
public class VmConfig {
    // VM configurations from Table 4 (EC2-like instances)
    public static class VmType {
        public final double processingCapacity; // in GFLOPS
        public final double costPerHour; // in $/h
        public final double bandwidth; // in Gbps
        
        public VmType(double processingCapacity, double costPerHour, double bandwidth) {
            this.processingCapacity = processingCapacity;
            this.costPerHour = costPerHour;
            this.bandwidth = bandwidth;
        }
        
        public double getCostPerSecond() {
            return costPerHour / 3600.0;
        }
    }
    
    // VM types from Table 4
    public static final VmType C3_LARGE = new VmType(30.8, 0.128, 1.0);
    public static final VmType C3_XLARGE = new VmType(61.6, 0.255, 1.5);
    public static final VmType C3_2XLARGE = new VmType(123.2, 0.511, 2.0);
    public static final VmType C3_4XLARGE = new VmType(242.0, 1.021, 3.0);
    public static final VmType C3_8XLARGE = new VmType(475.2, 2.043, 3.0);
    
    // Map VM instances to their configurations
    private static final Map<Vm, VmType> vmConfigs = new HashMap<>();
    
    /**
     * Register a VM with its type configuration
     */
    public static void registerVm(Vm vm, VmType type) {
        vmConfigs.put(vm, type);
    }
    
    /**
     * Get VM type configuration
     */
    public static VmType getVmType(Vm vm) {
        return vmConfigs.get(vm);
    }
    
    /**
     * Calculate communication time between two VMs (in seconds)
     * Communication time = dataSize / bandwidth
     * 
     * From paper: Communication time between tasks on different VMs depends on
     * data transfer size and bandwidth between VMs.
     * 
     * @param dataSize Data size in MB (megabytes)
     * @param vm1 First VM
     * @param vm2 Second VM
     * @return Communication time in seconds
     */
    public static double calculateCommunicationTime(double dataSize, Vm vm1, Vm vm2) {
        if (vm1 == vm2) {
            return 0.0; // Same VM, no communication time (data transfer is 0)
        }
        
        if (dataSize <= 0) {
            return 0.0; // No data to transfer
        }
        
        VmType type1 = getVmType(vm1);
        VmType type2 = getVmType(vm2);
        
        double minBandwidth; // in Gbps
        
        if (type1 != null && type2 != null) {
            // Use minimum bandwidth between the two VM types (from paper)
            minBandwidth = Math.min(type1.bandwidth, type2.bandwidth);
        } else {
            // Fallback: use VM bandwidth from CloudSim
            // CloudSim bandwidth is in Mbps (megabits per second)
            // Convert to Gbps: 1 Gbps = 1000 Mbps
            double bw1Mbps = vm1.getBw().getCapacity(); // Mbps (get capacity from Resource)
            double bw2Mbps = vm2.getBw().getCapacity(); // Mbps (get capacity from Resource)
            double minBwMbps = Math.min(bw1Mbps, bw2Mbps);
            minBandwidth = minBwMbps / 1000.0; // Convert to Gbps
            if (minBandwidth <= 0) {
                minBandwidth = 1.0; // Default 1 Gbps if not available
            }
        }
        
        // Calculate communication time
        // Bandwidth is in Gbps (gigabits per second)
        // 1 Gbps = 125 MB/s (megabytes per second) = 125 * 1024 KB/s
        // Time (seconds) = DataSize (MB) / (Bandwidth (Gbps) * 125 MB/s per Gbps)
        double bandwidthMBps = minBandwidth * 125.0; // Convert Gbps to MB/s
        return dataSize / bandwidthMBps;
    }
    
    /**
     * Get bandwidth between two VMs (in Gbps)
     */
    public static double getBandwidth(Vm vm1, Vm vm2) {
        if (vm1 == vm2) {
            return Double.MAX_VALUE; // Same VM, infinite bandwidth
        }
        
        VmType type1 = getVmType(vm1);
        VmType type2 = getVmType(vm2);
        
        if (type1 == null || type2 == null) {
            // Fallback: use VM bandwidth from CloudSim
            double bw1 = vm1.getBw().getCapacity() / 1000.0; // Convert Mbps to Gbps
            double bw2 = vm2.getBw().getCapacity() / 1000.0; // Convert Mbps to Gbps
            return Math.min(bw1, bw2);
        }
        
        return Math.min(type1.bandwidth, type2.bandwidth);
    }
    
    /**
     * Auto-detect VM type based on MIPS (processing capacity)
     * This is a heuristic since CloudSim uses MIPS instead of GFLOPS
     */
    public static VmType detectVmType(Vm vm) {
        double mips = vm.getMips();
        
        // Map MIPS to GFLOPS (rough approximation: 1 MIPS ≈ 1 GFLOPS for our purposes)
        // Try to match based on relative performance
        if (mips <= 600) return C3_LARGE;
        if (mips <= 1200) return C3_XLARGE;
        if (mips <= 2400) return C3_2XLARGE;
        if (mips <= 4800) return C3_4XLARGE;
        return C3_8XLARGE;
    }
    
    /**
     * Initialize VM configurations for a list of VMs
     */
    public static void initializeVmConfigs(java.util.List<Vm> vms) {
        for (int i = 0; i < vms.size(); i++) {
            Vm vm = vms.get(i);
            // Assign VM types in order: c3.large, c3.xlarge, c3.2xlarge, c3.4xlarge, c3.8xlarge
            VmType[] types = {C3_LARGE, C3_XLARGE, C3_2XLARGE, C3_4XLARGE, C3_8XLARGE};
            VmType type = types[i % types.length];
            registerVm(vm, type);
        }
    }
}

