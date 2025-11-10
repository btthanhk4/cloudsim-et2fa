package vn.et2fa;

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.schedulers.vm.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;

import vn.et2fa.broker.Et2faBroker;
import vn.et2fa.model.Et2faTask;
import vn.et2fa.model.TaskType;
import vn.et2fa.util.DaxLoader;

import java.util.*;

/**
 * Example application demonstrating ET2FA algorithm usage.
 * Based on the paper: "ET2FA: A Hybrid Heuristic Algorithm for 
 * Deadline-constrained Workflow Scheduling in Cloud"
 */
public class App {
	public static void main(String[] args) {
		System.out.println("=== ET2FA Workflow Scheduling Simulation ===");

		// Optional: --dax=/path/to/workflow.dax  --deadline=1000
		String daxPath = null;
		double deadlineOpt = 1000.0;
		for (String arg : args) {
			if (arg.startsWith("--dax=")) daxPath = arg.substring("--dax=".length());
			if (arg.startsWith("--deadline=")) {
				try { deadlineOpt = Double.parseDouble(arg.substring("--deadline=".length())); } catch (Exception ignored) {}
			}
		}

		// Step 1: Initialize simulation environment
		CloudSim simulation = new CloudSim();

		// Step 2: Create Datacenter with sufficient resources
		// Create multiple hosts to support multiple VMs
		List<Host> hostList = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			List<Pe> peList = new ArrayList<>();
			// Each host has 2 PEs (CPU cores) with 2000 MIPS each
			peList.add(new PeSimple(2000, new PeProvisionerSimple()));
			peList.add(new PeSimple(2000, new PeProvisionerSimple()));
			
			Host host = new HostSimple(8192, 50000, 10000000, peList)
					.setVmScheduler(new VmSchedulerTimeShared());
			hostList.add(host);
		}
		new DatacenterSimple(simulation, hostList, new VmAllocationPolicySimple());

		// Step 3: Create ET2FA Broker
		Et2faBroker broker = new Et2faBroker(simulation);

		// Step 4: Create VMs with different configurations (simulating EC2 instance types)
		List<Vm> vmList = new ArrayList<>();
		// VM types: c3.large, c3.xlarge, c3.2xlarge, c3.4xlarge, c3.8xlarge
		// Reduced MIPS to fit within host capacity
		double[] mips = {500, 1000, 1500, 2000, 2500}; // Simplified MIPS
		for (int i = 0; i < 5; i++) {
			Vm vm = new VmSimple(mips[i], 1)
					.setRam(1024 * (i + 1))
					.setBw(1000 * (i + 1))
					.setSize(10000)
					.setCloudletScheduler(new CloudletSchedulerTimeShared());
			vmList.add(vm);
		}
		broker.submitVmList(vmList);

		// Step 5: Load workflow (DAX or sample)
		List<Et2faTask> cloudletList;
		if (daxPath != null && !daxPath.isEmpty()) {
			cloudletList = createTasksForDax(daxPath);
			broker.submitCloudletList(cloudletList);
			try {
				DaxLoader.DaxWorkflow dax = DaxLoader.load(daxPath);
				broker.buildWorkflowFromDax(cloudletList, dax);
				System.out.println("Loaded DAX: jobs=" + dax.tasks.size());
			} catch (Exception e) {
				throw new RuntimeException("Failed to load DAX: " + e.getMessage(), e);
			}
		} else {
			cloudletList = createSampleWorkflow();
			broker.submitCloudletList(cloudletList);
			// Build simple dependencies
			Map<String, List<String>> dependencies = new HashMap<>();
			dependencies.put("0", Arrays.asList("1", "2")); // Task 0 -> Task 1, Task 2
			dependencies.put("1", Arrays.asList("3"));
			dependencies.put("2", Arrays.asList("3"));
			Map<String, Double> dataTransfers = new HashMap<>();
			dataTransfers.put("0_1", 100.0);
			dataTransfers.put("0_2", 200.0);
			dataTransfers.put("1_3", 150.0);
			dataTransfers.put("2_3", 150.0);
			broker.buildWorkflowDAG(cloudletList, dependencies, dataTransfers);
		}

		// Step 6: Set deadline (default or from args)
		broker.setDeadline(deadlineOpt);

		// Step 7: Run simulation (let VMs be created first)
		simulation.start();
		
		// Step 8: Execute ET2FA algorithm after VMs are created
		if (broker.getVmCreatedList().size() > 0) {
			broker.executeET2FA();
		}

		// Step 9: Print results
		System.out.println("\n=== Scheduling Results ===");
		List<Cloudlet> finished = broker.getCloudletFinishedList();
		for (Cloudlet cl : finished) {
			if (cl instanceof Et2faTask task) {
				System.out.printf("Task %d: VM %d, Start: %.2fs, Finish: %.2fs, Level: %d, Type: %s%n",
						cl.getId(), cl.getVm().getId(), 
						task.getActualStartTime(), task.getActualFinishTime(),
						task.getTopologicalLevel(), task.getType());
			}
		}
		
		System.out.println("\n=== Performance Metrics ===");
		System.out.printf("Total Cost: $%.6f%n", broker.calculateTotalCost());
		System.out.printf("Total Idle Rate: %.4f%n", broker.calculateTotalIdleRate());
		System.out.printf("Meets Deadline: %s%n", broker.meetsDeadline() ? "Yes" : "No");
		
		System.out.println("\n=== Simulation Complete ===");
	}

	/**
	 * Create tasks based on DAX jobs count; lengths are adjusted when running schedule.
	 */
	private static List<Et2faTask> createTasksForDax(String daxPath) {
		try {
			DaxLoader.DaxWorkflow dax = DaxLoader.load(daxPath);
			List<Et2faTask> tasks = new ArrayList<>();
			for (DaxLoader.TaskSpec spec : dax.tasks) {
				// Create a Cloudlet for each DAX job; computation from DAX runtime
				Et2faTask t = new Et2faTask(spec.computation, 1, TaskType.GENERAL);
				tasks.add(t);
			}
			return tasks;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a sample workflow (simplified DAG structure)
	 */
	private static List<Et2faTask> createSampleWorkflow() {
		List<Et2faTask> tasks = new ArrayList<>();
		
		// Create tasks with different computation requirements
		tasks.add(new Et2faTask(10000, 1, TaskType.GENERAL)); // Task 0 (entry)
		tasks.add(new Et2faTask(8000, 1, TaskType.GENERAL));  // Task 1
		tasks.add(new Et2faTask(12000, 1, TaskType.GENERAL)); // Task 2
		tasks.add(new Et2faTask(15000, 1, TaskType.GENERAL)); // Task 3 (exit)
		
		System.out.println("Created " + tasks.size() + " tasks");
		for (int i = 0; i < tasks.size(); i++) {
			System.out.println("  Task " + i + " -> ID: " + tasks.get(i).getId());
		}
		
		return tasks;
	}
}
