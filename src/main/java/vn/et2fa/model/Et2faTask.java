package vn.et2fa.model;

import org.cloudbus.cloudsim.cloudlets.CloudletSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp mở rộng CloudletSimple để thêm thuộc tính cho ET2FA algorithm.
 * Based on the ET2FA paper for deadline-constrained workflow scheduling.
 */
public class Et2faTask extends CloudletSimple {
    private TaskType taskType;
    private int topologicalLevel;
    private List<Et2faTask> predecessors;
    private List<Et2faTask> successors;
    private double computation; // in GFLOP
    private double estimatedStartTime;
    private double estimatedFinishTime;
    private double actualStartTime;
    private double actualFinishTime;

    public Et2faTask(long length, int pesNumber, TaskType taskType) {
        super(length, pesNumber);
        this.taskType = taskType;
        this.predecessors = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.computation = length; // Assuming length represents computation
        this.topologicalLevel = -1;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    /** Alias để Broker gọi ngắn gọn hơn **/
    public TaskType getType() {
        return taskType;
    }

    public int getTopologicalLevel() {
        return topologicalLevel;
    }

    public void setTopologicalLevel(int topologicalLevel) {
        this.topologicalLevel = topologicalLevel;
    }

    public List<Et2faTask> getPredecessors() {
        return predecessors;
    }

    public void addPredecessor(Et2faTask task) {
        if (!predecessors.contains(task)) {
            predecessors.add(task);
        }
    }

    public List<Et2faTask> getSuccessors() {
        return successors;
    }

    public void addSuccessor(Et2faTask task) {
        if (!successors.contains(task)) {
            successors.add(task);
        }
    }

    public double getComputation() {
        return computation;
    }

    public void setComputation(double computation) {
        this.computation = computation;
    }

    public double getEstimatedStartTime() {
        return estimatedStartTime;
    }

    public void setEstimatedStartTime(double estimatedStartTime) {
        this.estimatedStartTime = estimatedStartTime;
    }

    public double getEstimatedFinishTime() {
        return estimatedFinishTime;
    }

    public void setEstimatedFinishTime(double estimatedFinishTime) {
        this.estimatedFinishTime = estimatedFinishTime;
    }

    public double getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(double actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public double getActualFinishTime() {
        return actualFinishTime;
    }

    public void setActualFinishTime(double actualFinishTime) {
        this.actualFinishTime = actualFinishTime;
    }

    @Override
    public String toString() {
        return String.format("Et2faTask{id=%d, type=%s, level=%d, length=%d}", 
                getId(), taskType, topologicalLevel, getLength());
    }
}
