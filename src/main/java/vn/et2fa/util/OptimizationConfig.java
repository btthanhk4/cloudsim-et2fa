package vn.et2fa.util;

/**
 * Configuration for optimization features.
 * Controls which optimizations are enabled/disabled.
 */
public class OptimizationConfig {
    private boolean useCache = false;
    private boolean useBFSTopological = false;
    private boolean useEarlyTermination = false;
    private boolean useCPO = false;
    private boolean useOptimizedDOBS = false;
    
    // Mode: "original" or "optimized"
    private String mode = "optimized";
    
    public OptimizationConfig() {
        // Default: optimized mode
        this.mode = "optimized";
        enableAllOptimizations();
    }
    
    public OptimizationConfig(String mode) {
        this.mode = mode;
        if ("original".equalsIgnoreCase(mode)) {
            disableAllOptimizations();
        } else {
            enableAllOptimizations();
        }
    }
    
    private void enableAllOptimizations() {
        this.useCache = true;
        this.useBFSTopological = true;
        this.useEarlyTermination = true;
        this.useCPO = true;
        this.useOptimizedDOBS = true;
    }
    
    private void disableAllOptimizations() {
        this.useCache = false;
        this.useBFSTopological = false;
        this.useEarlyTermination = false;
        this.useCPO = false;
        this.useOptimizedDOBS = false;
    }
    
    public boolean isUseCache() {
        return useCache;
    }
    
    public boolean isUseBFSTopological() {
        return useBFSTopological;
    }
    
    public boolean isUseEarlyTermination() {
        return useEarlyTermination;
    }
    
    public boolean isUseCPO() {
        return useCPO;
    }
    
    public boolean isUseOptimizedDOBS() {
        return useOptimizedDOBS;
    }
    
    public String getMode() {
        return mode;
    }
    
    public boolean isOptimized() {
        return "optimized".equalsIgnoreCase(mode);
    }
}

