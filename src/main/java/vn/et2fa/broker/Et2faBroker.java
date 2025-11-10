package vn.et2fa.broker;

import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.vms.Vm;
import vn.et2fa.algorithm.DOBSAlgorithm;
import vn.et2fa.algorithm.IHSHAlgorithm;
import vn.et2fa.algorithm.T2FAAlgorithm;
import vn.et2fa.model.Et2faTask;
import vn.et2fa.util.WorkflowDAG;
import vn.et2fa.util.DaxLoader;

import java.util.*;

/**
 * Custom Broker implementing ET2FA algorithm.
 * ET2FA consists of three phases:
 * 1. T2FA (Task Type First Algorithm) - Initial scheduling
 * 2. DOBS (Delay Operation Based on Block Structure) - Optimization
 * 3. IHSH (Instance Hibernate Scheduling Heuristic) - Cost optimization
 * 
 * Compatible with CloudSim Plus 7.3.0
 */
public class Et2faBroker extends DatacenterBrokerSimple {
	private WorkflowDAG workflowDAG;
	private double deadline;
	private Map<Et2faTask, Vm> schedule;
	private IHSHAlgorithm ihshAlgorithm;
	
	public Et2faBroker(final CloudSim simulation) {
		super(simulation);
		this.workflowDAG = new WorkflowDAG();
		this.schedule = new HashMap<>();
	}

	/**
	 * Set deadline for workflow execution
	 */
	public void setDeadline(double deadline) {
		this.deadline = deadline;
	}

	/**
	 * Build workflow DAG directly from a DAX workflow (ids are strings)
	 */
	public void buildWorkflowFromDax(List<? extends Cloudlet> cloudlets, DaxLoader.DaxWorkflow dax) {
		Map<String, Et2faTask> taskMapById = new HashMap<>();
		int idx = 0;
		for (Cloudlet cl : cloudlets) {
			if (cl instanceof Et2faTask t) {
				// Map by DAX job id order if possible; else by incremental index as string
				String key = idx < dax.tasks.size() ? dax.tasks.get(idx).id : String.valueOf(t.getId());
				taskMapById.put(key, t);
				workflowDAG.addTask(t);
				idx++;
			}
		}
		// Dependencies
		for (Map.Entry<String, List<String>> e : dax.dependencies.entrySet()) {
			Et2faTask parent = taskMapById.get(e.getKey());
			if (parent == null) continue;
			for (String childId : e.getValue()) {
				Et2faTask child = taskMapById.get(childId);
				if (child == null) continue;
				double data = dax.dataTransfers.getOrDefault(e.getKey() + "_" + childId, 0.0);
				workflowDAG.addDependency(parent, child, data);
			}
		}
	}

	/**
	 * Build workflow DAG from cloudlet list
	 * @param cloudlets List of cloudlets (Et2faTask instances)
	 * @param dependencies Map from task index (as String) to list of dependent task indices
	 * @param dataTransfers Map from "fromId_toId" to data transfer size
	 */
	public void buildWorkflowDAG(List<? extends Cloudlet> cloudlets, Map<String, List<String>> dependencies, 
								  Map<String, Double> dataTransfers) {
		// Create mapping from string ID to task
		Map<String, Et2faTask> taskMapById = new HashMap<>();
		Map<Integer, Et2faTask> taskMapByIndex = new HashMap<>();
		
		// Create tasks and map by both ID and index
		int index = 0;
		for (Cloudlet cloudlet : cloudlets) {
			if (cloudlet instanceof Et2faTask task) {
				String idStr = String.valueOf(task.getId());
				taskMapById.put(idStr, task);
				taskMapByIndex.put(index, task);
				workflowDAG.addTask(task);
				index++;
			}
		}
		
		// Add dependencies - support both string IDs and indices
		for (Map.Entry<String, List<String>> entry : dependencies.entrySet()) {
			String fromKey = entry.getKey();
			Et2faTask fromTask = taskMapById.get(fromKey);
			if (fromTask == null) {
				// Try as index
				try {
					int fromIndex = Integer.parseInt(fromKey);
					fromTask = taskMapByIndex.get(fromIndex);
				} catch (NumberFormatException e) {
					// Skip if not found
				}
			}
			if (fromTask == null) continue;
			
			for (String toKey : entry.getValue()) {
				Et2faTask toTask = taskMapById.get(toKey);
				if (toTask == null) {
					// Try as index
					try {
						int toIndex = Integer.parseInt(toKey);
						toTask = taskMapByIndex.get(toIndex);
					} catch (NumberFormatException e) {
						// Skip if not found
					}
				}
				if (toTask == null) continue;
				
				// Use fromKey_toKey for data transfer lookup
				String dataKey = fromKey + "_" + toKey;
				double dataSize = dataTransfers.getOrDefault(dataKey, 0.0);
				workflowDAG.addDependency(fromTask, toTask, dataSize);
			}
		}
	}

	/**
	 * Execute ET2FA algorithm
	 */
	public void executeET2FA() {
		List<Vm> vms = getVmCreatedList();
		if (vms.isEmpty()) {
			System.err.println("No VMs available for scheduling");
			return;
		}

		// Phase 1: T2FA - Task Type First Algorithm
		T2FAAlgorithm t2fa = new T2FAAlgorithm(workflowDAG, vms);
		schedule = t2fa.schedule();
		
		// Phase 2: DOBS - Delay Operation Based on Block Structure
		DOBSAlgorithm dobs = new DOBSAlgorithm(schedule);
		dobs.optimize();
		
		// Phase 3: IHSH - Instance Hibernate Scheduling Heuristic
		ihshAlgorithm = new IHSHAlgorithm(schedule);
		ihshAlgorithm.scheduleHibernations();
		
		// Apply schedule to cloudlets
		applyScheduleToCloudlets();
	}

	/**
	 * Apply the computed schedule to cloudlets
	 */
	private void applyScheduleToCloudlets() {
		for (Map.Entry<Et2faTask, Vm> entry : schedule.entrySet()) {
			Et2faTask task = entry.getKey();
			Vm vm = entry.getValue();
			
			// Set VM assignment
			task.setVm(vm);
		}
	}

	@Override
	protected Vm defaultVmMapper(Cloudlet cloudlet) {
		// If schedule already exists, use it
		if (cloudlet instanceof Et2faTask task && schedule.containsKey(task)) {
			return schedule.get(task);
		}
		
		// Otherwise, use default mapping
		return super.defaultVmMapper(cloudlet);
	}

	/**
	 * Get schedule results
	 */
	public Map<Et2faTask, Vm> getSchedule() {
		return schedule;
	}

	/**
	 * Calculate and return total cost
	 */
	public double calculateTotalCost() {
		if (ihshAlgorithm == null) return 0;
		
		Map<Vm, IHSHAlgorithm.VmCostInfo> vmCosts = new HashMap<>();
		for (Vm vm : getVmCreatedList()) {
			// Convert VM MIPS to GFLOPS (assuming 1 MIPS ≈ 1 GFLOPS approximation)
			double processingCapacity = vm.getMips();
			// Convert hourly price to per-second (assuming a simple pricing model)
			// In real implementation, this would come from VM configuration
			double pricePerSecond = processingCapacity * 0.0001 / 3600.0; // Simplified pricing
			vmCosts.put(vm, new IHSHAlgorithm.VmCostInfo(pricePerSecond, processingCapacity));
		}
		
		return ihshAlgorithm.calculateTotalCost(vmCosts);
	}

	/**
	 * Calculate and return total idle rate
	 */
	public double calculateTotalIdleRate() {
		if (ihshAlgorithm == null) return 0;
		
		Map<Vm, IHSHAlgorithm.VmCostInfo> vmCosts = new HashMap<>();
		for (Vm vm : getVmCreatedList()) {
			double processingCapacity = vm.getMips();
			double pricePerSecond = processingCapacity * 0.0001 / 3600.0;
			vmCosts.put(vm, new IHSHAlgorithm.VmCostInfo(pricePerSecond, processingCapacity));
		}
		
		return ihshAlgorithm.calculateTotalIdleRate(vmCosts);
	}

	/**
	 * Check if schedule meets deadline constraint
	 */
	public boolean meetsDeadline() {
		if (deadline <= 0) return true;
		
		double maxFinishTime = schedule.keySet().stream()
			.mapToDouble(Et2faTask::getActualFinishTime)
			.max()
			.orElse(0);
		
		return maxFinishTime <= deadline;
	}
}
