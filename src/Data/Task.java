package Data;

public class Task {
    private String taskName;
    private String taskDetails;
    private StatusList taskStatus = StatusList.NEW;
    private int itemID;


    public String getTaskName() {
        return taskName;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public Task(String taskName, String taskDetails) {
        this.taskName = taskName;
        this.taskDetails = taskDetails;
    }

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

    public String toString() {
        return "Наименование: " + taskName +
                ", Описание: " + taskDetails +
                ", Статус: " + taskStatus;
    }
}
