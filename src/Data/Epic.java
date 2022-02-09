package Data;

import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subtaskIDList = new ArrayList<>();

    public Epic(String taskName, String taskDetails) {
        super(taskName, taskDetails);
    }

    public ArrayList<Integer> getSubtaskIDList() {
        return subtaskIDList;
    }

    public void setSubtaskIDList(int subtaskID) {
        subtaskIDList.add(subtaskID);
    }

    public void setSubtaskIDList(ArrayList<Integer> subtaskIDList) {
        this.subtaskIDList = subtaskIDList;
    }

    public void setEpicStatus(String epicStatus) {
        super.taskStatus = epicStatus;
    }



    public String toString() {
        return "Наименование: " + taskName +
                ", Описание: " + taskDetails +
                ", Статус: " + taskStatus +
                ", Кол-во подзадач:" + getSubtaskIDList().size();
    }
}
