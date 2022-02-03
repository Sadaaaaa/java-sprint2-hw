package Data;

public class Task {
    String taskName;
    String taskDetails;
    String taskStatus = "NEW";
    int itemID;

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
