package Data;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> subtaskIDList = new ArrayList<>();

    public Epic(String taskName, String taskDetails) {
        super(taskName, taskDetails);
    }

    public List<Integer> getSubtaskIDList() {
        return subtaskIDList;
    }

    public void setSubtaskIDList(int subtaskID) {
        subtaskIDList.add(subtaskID);
    }

    public void setSubtaskIDList(List<Integer> subtaskIDList) {
        this.subtaskIDList = subtaskIDList;
    }

    public void setEpicStatus(StatusList status) {
        super.setTaskStatus(status);
    }



    public String toString() {
        return "Наименование: " + getTaskName() +
                ", Описание: " + getTaskDetails() +
                ", Статус: " + getTaskStatus() +
                ", Кол-во подзадач:" + getSubtaskIDList().size();
    }
}
