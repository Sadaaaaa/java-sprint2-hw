package Data;

public class Task {
    String taskName;
    String taskDetails;
    StatusList taskStatus = StatusList.NEW;
    int itemID;

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
