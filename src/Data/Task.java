package Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

public class Task {
    private String taskName;
    private String taskDetails;
    private StatusList taskStatus = StatusList.NEW;
    private int itemID;
    private String type = "TASK";
    private LocalDate startTime;
    private Duration duration;

    public Task(String taskName, String taskDetails) {
        this.taskName = taskName;
        this.taskDetails = taskDetails;
    }

    public String getType() {
        return type;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

//    public Task(String taskName, String taskDetails, LocalDate startTime, Duration duration) {
//        this.taskName = taskName;
//        this.taskDetails = taskDetails;
//        this.startTime = startTime;
//        this.duration = duration;
//    }

    public StatusList getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(StatusList status) {
        this.taskStatus = status;
    }

    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDate startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String toString() {
        return "Наименование: " + taskName +
                ", Описание: " + taskDetails +
                ", Статус: " + taskStatus;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
