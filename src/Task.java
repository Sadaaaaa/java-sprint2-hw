
public class Task {
    String taskName;
    String taskDetails;
    String taskStatus = "NEW";

    public Task(String taskName, String taskDetails) {
        this.taskName = taskName;
        this.taskDetails = taskDetails;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String toString() {
        return "Наименование: " + taskName +
                ", Описание: " + taskDetails +
                ", Статус: " + taskStatus;
    }
}
